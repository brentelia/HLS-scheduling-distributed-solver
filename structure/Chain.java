package structure;

import java.util.ArrayList;
import java.util.LinkedList;

import util.Pair;

public class Chain implements Comparable{

	

	//Lista di nodi (pieni/veri, non buchi/mobilità) appartenenti alla catena con relativa posizione nella catena
	//Es: (X = nodo, O = vuoto) se la catena è fatta O X X chain sarà lunga 2 e sarà fatta {<Node, 0>, <Node, 1>}
	//dove 0 è la posizione più a destra
	private LinkedList<Pair<Node, Integer>> chain;
	//Quantità di vuoti nella catena
	private Integer voidSize;
	//Se devo propagare il riporto in un'altra catena
	private boolean carry;
	//Lista dei cambiamenti nelle posizioni dei nodi per non dover ricreare il grafo ad ogni incremento
	private ArrayList<Pair<Node, Integer>> movement;
	private int dangerLevel;
	private int minimumPosition;

	public Chain() {
		minimumPosition = Integer.MAX_VALUE;
		dangerLevel = 0;
		voidSize = 0;
		chain = new LinkedList<>();
		movement = new ArrayList<>();
	}
	
	public Chain(int voidSize) {
		minimumPosition = Integer.MAX_VALUE;
		dangerLevel = 0;
		this.voidSize = voidSize;
		chain = new LinkedList<>();
		movement = new ArrayList<>();
	}

	public Pair<Boolean, ArrayList<Pair<Node, Integer>>> increment() {
		movement = new ArrayList<>();
		carry = false;
		recursiveIncrement(chain.size()-1);
		return new Pair<Boolean, ArrayList<Pair<Node,Integer>>>(carry, movement);
	}

	/*
	 * Caso terminale: se il nodo che provo ad incrementare non esiste (ho finito i nodi da incrementare) resetto la catena alla
	 * posizione iniziale e propago il riporto alla prossima catena
	 * Caso ricorsivo: provo a muovere il nodo in analisi di uno spazio a sx (Es: X X O -> X O X)
	 * Se vado fuori dagli spazi oppure mi sovrappongo ad un altro nodo incremento il nodo precedente (alla sua sx) e mi ci metto una posizione a dx
	 * Es: (X O O X ++ -> O X X O)
	 * Dopodichè aggiungo alla lista delle modifiche di quanto ho spostato quel nodo
	 */
	private int recursiveIncrement(int node) {
		if (node == -1) {
			carry = true;
			return -1;
		}
		Pair<Node, Integer> n = chain.get(node);
		int newPosition = n.getSecond() + 1;
		if (newPosition >= voidSize+chain.size() || (node < chain.size()-1 && newPosition == chain.get(node + 1).getSecond().intValue())) {
			newPosition = recursiveIncrement(node - 1) + 1;
		}
		movement.add(new Pair<Node, Integer>(n.getFirst(), newPosition - n.getSecond()));
		chain.get(node).setSecond(newPosition);

		return newPosition;
	}
	
	public void setInitialState(int value) {
		recurrentSetInitialState(chain.size(), voidSize, value);
	}
	
	private void recurrentSetInitialState(int numNodes, int voidSize, int value) {
		//System.out.println("numNodes: "+numNodes+", voidSize: "+voidSize+", value: "+value);
		if (numNodes == 1) {
			Pair<Node, Integer> n = chain.get(chain.size()-1);
			n.setSecond(chain.size()-1+value+(this.voidSize-voidSize));
			//System.out.println("Set node "+n.getFirst().getName()+" to position "+n.getSecond());
			return;
		}
		int partial = (int) util.Util.calculatePermutations(numNodes-1, voidSize);
		//System.out.println("Partial: "+partial);
		if(value < partial) {
			Pair<Node, Integer> n = chain.get(chain.size()-numNodes);
			n.setSecond(chain.size()-numNodes+(this.voidSize-voidSize));
			//System.out.println("Set node "+n.getFirst().getName()+" to position "+n.getSecond());
			recurrentSetInitialState(numNodes-1, voidSize, value);
			
		}else {
			recurrentSetInitialState(numNodes, voidSize-1, value-partial);
		}		
	}

	//Aggiungo un nodo alla lista
	public void addNode(Node n, boolean reverse) {
		if (reverse) chain.add(0, new Pair<Node, Integer>(n, -1));
		else chain.add(new Pair<Node, Integer>(n, -1));
		if (n.getTime() < minimumPosition) {
			minimumPosition = n.getTime();
		}
		n.setParentChain(this);
	}
	
	public void fixNodePosition() {
		int index = 0;
		for (Pair<Node, Integer> p : chain) {
			p.setSecond(index++);
		}
	}
	
	public LinkedList<Pair<Node, Integer>> getNodes(){
		return chain;
	}
	
	public int getMinimumPosition() {
		return minimumPosition;
	}
	
	public void setMinimumPosition(int position) {
		this.minimumPosition = position;
	}
	
	public void removeLast() {
		chain.remove(chain.size()-1);
	}
	
	public void setVoid(int voidSize) {
		this.voidSize = voidSize;
	}
	
	public int getVoid() {
		return voidSize;
	}
	
	public int getSize() {
		return chain.size();
	}
	
	public void calculateDangerLevel() {
		for (Pair<Node, Integer> n : chain) {
			dangerLevel += n.getFirst().getDangerLevel();
		}
	}
	
	public void setDangerLevel(int dangerLevel) {
		this.dangerLevel = dangerLevel;
	}

	@Override
	public String toString() {
		String s = "" + voidSize + ":"+dangerLevel + ":"+minimumPosition;

		for (Pair<Node, Integer> p : chain) {
			s += ":" + p.getFirst().getName();
		}
		return s;
	}
	
	public String getDebugString() {
		String str = "VoidSize: "+ voidSize+", NodeNumber: " + chain.size()+", DangerLevel: "+dangerLevel + " Nodes:";
		for (Pair<Node, Integer> p : chain) {
			str += " " + p.getFirst().getName();
		}
		return str;
	}
	
	public String drawChain() {
		String ret = "";
		int index = 0;
		for (Pair<Node, Integer> p : chain) {
			for (; index < p.getSecond(); index++) {
				ret+="# ";
			}
			ret += p.getFirst().getName()+" ";
			index++;
		}
		for (; index < chain.size()+voidSize; index++) {
			ret+="# ";
		}
		return ret;
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof Chain) {
			Chain c  = (Chain) o;
			return dangerLevel != c.dangerLevel ? c.dangerLevel - dangerLevel : c.chain.size() - chain.size() ;
		}
		return 0;
	}
	

}
