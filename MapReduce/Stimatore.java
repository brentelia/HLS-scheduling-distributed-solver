package MapReduce;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;
import java.util.TreeSet;


import structure.Chain;
import structure.Dag;
import structure.Node;
import util.Pair;

public final class Stimatore {
	private static final boolean DEBUG = false;

	public static Pair<Long, ArrayList<Chain>> stima(Dag graph) {
		long  perm=1;
		//Copia
		Dag g2=new Dag(graph);

		//Copia i nodi del DAG in un set
		TreeSet<Node> t=new TreeSet<>();
		for(Node i: g2.getNodes()) {
			if(!t.add(i))
				if (DEBUG) System.err.println(i+"\n GIa presente");
		}
		// Stack per l'ordine di analisi dei nodi
		Stack<Node> stack=new Stack<>();
		//Inizializza lo stack con i nodi terminali
		for(Node i:g2.getNodes()) {
			if(i.getOutEdges().isEmpty())
				stack.push(i);
		}
		
		ArrayList<Chain> chains = new ArrayList<>();
		Chain currentChain = new Chain();
			
		int numNode=0;
		//int domain=0;
		int mobility = -1;
		Node x;
		boolean reset=false;
		
		int k;
		long middle=1;

		//Finchè non ho esplorato tutti i nodi
		while(!stack.isEmpty()) {
			//if (DEBUG) System.err.println(stack);
			//Se l'ultimo nodo esplorato non aveva padri (reset=true) riparto da il più in fondo possibile
			//(per fare in modo da generare catene più lunghe)
			if(!reset)
				x=stack.pop();
			else
				x=stack.remove(0);
				if(t.contains(x)) {
	
				//Togliamo il nodo e relativi archi dal grafo per evitare di analizzare lo stesso nodo più volte
				for(Node i:g2.getOutEdges().get(x))
						g2.getInEdges().get(i).remove(x);
				g2.getOutEdges().get(x).clear();
				
				//Se devo iniziare una nuova catena imposto il dominio della catena corrente uguale al dominio del nodo
				if(mobility==-1) {
					mobility=x.getMaxMobility();
					currentChain.setVoid(mobility);
				}
				reset=true;
				//Aggiungo tutti i padri del nodo nella lista
				//Se ha almeno un padre metto reset=false
				Node emNode = null;
				for(Node i:g2.getInEdges().get(x)) {
					if(t.contains(i)) {
						//Se almeno un padre ha la mia stessa mobilità proseguo la catena scegliendo lui per primo
						//nella pila
						if (emNode == null && i.getMaxMobility() == mobility) {
							emNode = i;
						}else {
							stack.push(i);
							//Se non sto proseguendo la catena (e non sono sul cammino critico) ho un branch, quindi
							//aumento il livello di errori generabili dal nodo padre
							if (mobility > 0) {
								i.incrementDangerLevel();
							}
						}
						
						reset=false;
					}
				}
				if (emNode != null) {
					stack.push(emNode);
				}
			//Se non ho già analizzato il nodo
			
				//Allungo la catena di 1 (sto provando ad aggiungere il nodo alla catena in corso)
				numNode++;
				currentChain.addNode(x, true);
				
				/*
				 * Se il nodo ha una mobilità diversa dalla catena in corso interrompo la catena, ricalcolo le 
				 * permutazioni totali e inizio una nuova catena contenente solo il nodo attuale x
				 */
				if(x.getMaxMobility() != mobility) {
					
					perm *= util.Util.calculatePermutations(mobility, numNode);


					mobility=x.getMaxMobility();
					numNode=1;
					currentChain.removeLast();
					chains.add(currentChain);
					currentChain = new Chain();
					currentChain.addNode(x, true);
					currentChain.setVoid(mobility);
					//if (DEBUG) System.err.println("Fine Catena");
					//t.remove(x);

				}
				/*
				 * Se il nodo è senza padri interrompo la catena e NON ne inizio un'altra
				 * perchè devo iniziare la prossima catena da uno dei nodi terminali.
				 * Questo viene fatto grazie a reset e pescando dal fondo dello stack
				 */
				if(reset) {
					
					perm *= util.Util.calculatePermutations(mobility, numNode);
					//if (DEBUG) System.err.println(x+"\n"+perm+":"+middle+"-"+mobility+"-"+numNode);
					mobility = -1;
					numNode=0;
					chains.add(currentChain);
					currentChain = new Chain();
					//if (DEBUG) System.err.println("Reset");
				}
				//Se non ho raggiunto un nodo terminale E non ho interrotto la catena, vado avanti.
				/*if (DEBUG) if(!reset && x.getMaxMobility() == mobility)  {
					System.err.println(x+"\n"+perm+"-"+mobility+"-"+numNode);
				}*/
				t.remove(x);

			}
			else {
				mobility = -1;
				numNode=0;
				//if (DEBUG) System.err.println("Node discarded");
			}
			
		}
		if (currentChain.getSize() > 0) {
			chains.add(currentChain);
		}
		//Cancello le catene con mobilità 0 in quanto non ottimizzabili
		for (int i = 0; i<chains.size();i++) {
			/*
			if (chains.get(i).getVoid() == 0) {
				chains.remove(i);
				i--;
			}
			else {
			*/
			chains.get(i).calculateDangerLevel();
			System.out.println(chains.get(i).getMinimumPosition());
			//}
		}
		/*if (DEBUG) for (Chain c : chains) {
			System.err.println(c.getDebugString());
		}*/
		
		Collections.sort(chains);
		if (DEBUG) System.err.println("----");
		if (DEBUG) for (Chain c : chains) {
			//System.err.println(c.toString());
			System.err.println(c.getDebugString());
		}
		
		
		return new Pair<Long, ArrayList<Chain>>(perm, chains);
	}
}
