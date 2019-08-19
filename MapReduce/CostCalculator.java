package MapReduce;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import structure.Chain;
import structure.Dag;
import structure.Node;
import util.Pair;

public class CostCalculator {
	public static int errors = 0;
	public static String calculate(String graphStr, String key) {
		String chainStr = "";
		
		Dag graph = new Dag();
		graph.parseFromString(graphStr);
		
		String params[] = key.toString().split(",");
		int startingValue = Integer.parseInt(params[0]);
		int permToCalc = Integer.parseInt(params[1]);
		
		System.out.println("StartingValue: "+startingValue+", PermToCalc: "+permToCalc);
		ArrayList<Chain> chains = graph.parseChainsFromString(params[2]);
		
		
		/*
		for (Chain c : chains) {
			System.out.println(c.toString());
		}*/
		
		
		long remainder = startingValue;
		
		//Calcolo la base di ogni cifra (catena)
		long base[] = new long[chains.size()];
		base[0] = 1;
		for (int i = 1; i < chains.size(); i++) {
			base[i] = base[i - 1] * (long) util.Util.calculatePermutations(chains.get(i - 1).getSize(), chains.get(i - 1).getVoid());
		}

		for (int i = chains.size() - 1; i >= 0; i--) {
			int modulo = (int) (remainder / base[i]);
			remainder %= base[i];
			chains.get(i).setInitialState((int) modulo);
			//System.out.println("Remainder: " + remainder + ", Base: " + base[i] + ", Modulo: " + modulo + ", State: " + chains.get(i).drawChain());
		}
		
		
		//Calcolo il costo della configurazione iniziale e imposto la posizione iniziale dei nodi
		int bestCost = Integer.MAX_VALUE; // Costo: massimo numero di nodi paralleli
		ArrayList<Integer> costArray = new ArrayList<Integer>();
		boolean isCostValid = false;
		int bestId = startingValue;
		
		
		for (Chain c : chains) {
			int position = c.getMinimumPosition();
			for (Pair<Node, Integer> p : c.getNodes()) {
				//int nodePos = p.getSecond()+position;
				int nodePos = p.getSecond()+position;
				p.getFirst().setPosition(nodePos);
				while (nodePos >= costArray.size()) {
					costArray.add(0);
				}
				costArray.set(nodePos, costArray.get(nodePos)+1);
			}
			//System.out.println(costArray);
		}
		/*
		for (Node n : graph.getNodes()) {
			System.out.println(n.toString()+ " Position:"+n.getPosition());
		}
		*/
		System.out.println("Beginning for");
		HashMap <Node, Integer> totalMovements = new HashMap<>();
		for (int counter = 1; counter<=permToCalc; counter++){
			//System.out.println("Examining ID "+counter+", PermToCalc = "+permToCalc);
			if (counter % 100000 == 0)System.out.println("Progress: "+counter);
			//System.out.println("ID: "+(counter+startingValue));
			//Incremento la cifra meno significativa e propago il riporto
			boolean carry = true;
			Pair<Boolean, ArrayList<Pair<Node, Integer>>> ret;
			ArrayList<Pair<Node, Integer>> mov = new ArrayList<Pair<Node,Integer>>();
			for (int i = chains.size() - 1; i >= 0 && carry; i--) {
				ret = chains.get(i).increment();
				carry = ret.getFirst();
				mov.addAll(ret.getSecond());
			}
			
			/*
			for(Chain c : chains) {
				System.out.println(c.drawChain());
			}*/
			
			//Verifico la validit� della nuova configurazione
			/*
			 * Prendo solamente i movimenti, li sommo in una hashmap e controllo le posizioni.
			 * Se trovo errori incremento la catena pi� piccola (e incremento le permutazioni calcolate)
			 * La prima volta che non trovo un errore uso la hashmap per calcolare la variazione di posizioni dall'ultimo grafo corretto
			 */
			
			for (Pair<Node, Integer> p : mov) {
				//int move= totalMovements.containsKey(p.getFirst())? totalMovements.get(p.getFirst())+p.getSecond() : 0;
				Integer tmpMove = totalMovements.get(p.getFirst());
				///////////////
				int move = tmpMove != null ? tmpMove : 0;
				//////////////
				totalMovements.put(p.getFirst(), move+p.getSecond());
			}
			
			boolean error = false;
			Node gen1 = null, gen2 = null;
			for (Entry<Node,Integer> e : totalMovements.entrySet()) {
				for (Node parent : graph.getInEdges().get(e.getKey())) {
					Integer tmpMove = totalMovements.get(parent);
					/////////////////////////////////////////
					int move = tmpMove != null ? tmpMove : 0;
					////////////////////////////////////////
					if (e.getKey().getPosition()+e.getValue() <= parent.getPosition()+move) {
						error = true;
						gen1 = e.getKey();
						gen2 = parent;
						break;
					}
				}
				if (!error) for (Node child : graph.getOutEdges().get(e.getKey())) {
					Integer tmpMove = totalMovements.get(child);
					////////////////////////
					int move = tmpMove != null ? tmpMove : 0;
					////////////////////////
					if (e.getKey().getPosition()+e.getValue() >= child.getPosition()+move) {
						error = true;
						gen1 = e.getKey();
						gen2 = child;
						break;
					}
				}
				if (error) break;
			}
			//if(error) System.out.println("Generated error between node "+gen1.getName()+" and "+gen2.getName());

			
			//TODO:Devo incrementare la catena pi� piccola e resettare tutte le catene a dx 
			if (error) {
				//System.out.println("Wrong chain, id: "+(counter+startingValue)+", "+totalMovements.entrySet());
				errors++;
				
				continue;
			}
			//System.out.println("Correct chain, id:"+(counter+startingValue)+", "+totalMovements.entrySet());
			//Se non c'� stato errore ricalcolo il costo e segno il nuovo best cost come valido
			ArrayList<Integer> newCost = (ArrayList<Integer>) costArray.clone();
			for (Entry<Node, Integer> e : totalMovements.entrySet()) {
				//System.out.println(e);
				int oldPos = e.getKey().getPosition();
				//System.out.println(oldPos);
				int newPos = oldPos + e.getValue();
				newCost.set(oldPos, newCost.get(oldPos)-1);
				//System.out.println(newPos);
				/*while (newPos >= newCost.size()) {
					newCost.add(0);
				}*/
				newCost.set(newPos, newCost.get(newPos)+1);
			}
			/*
			System.out.println("Cost difference: ");
			System.out.println(costArray);
			System.out.println(newCost);
			System.out.println("----");*/
			int localMin = Collections.max(newCost) ;
			if (localMin < bestCost ) {
				bestCost = localMin;
				costArray = newCost;
				isCostValid = true;
				bestId = counter+startingValue;
				for (Entry<Node, Integer> e : totalMovements.entrySet()) {
					e.getKey().setPosition(e.getKey().getPosition()+e.getValue());
				}
				totalMovements = new HashMap<Node, Integer>();
			}
		}
		
		String output = "ID: "+bestId+", Graph cost: "+bestCost + ", Graph validity: "+isCostValid+"\n";
		for (Node n : graph.getNodes()) {
			output+=(n.toString()+ " Position:"+n.getPosition()) + "\n";
			//System.out.println(n.toString()+ " Position:"+n.getPosition());
		}
		//System.out.println(output);
		return output;
	}
}
