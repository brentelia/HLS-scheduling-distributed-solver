package structure;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Dag {

	private ArrayList<Node> nodes;
	private HashMap<Node, ArrayList<Node>> inEdges;
	private HashMap<Node, ArrayList<Node>> outEdges;
	private int totalTime;
	private int movableNodes;
	//Necessari per creare il grafo in O(n)
	private HashMap<String, Node> strToNode;
	
	public Dag() {
		this.nodes=new ArrayList<Node>();
		this.inEdges=new HashMap<Node,ArrayList<Node>>();
		this.outEdges=new HashMap<Node,ArrayList<Node>>();
		strToNode = new HashMap<String, Node>();
		this.totalTime=0;
		this.movableNodes=0;
	}
	
	public Dag(Dag other) {
		this.nodes=new ArrayList<Node>();
		this.inEdges=new HashMap<Node,ArrayList<Node>>();
		this.outEdges=new HashMap<Node,ArrayList<Node>>();
		strToNode = new HashMap<String, Node>();
		for(Node i:other.getNodes())
			this.addNode(new Node(i));
		
		this.totalTime=0;
		this.movableNodes=0;
		//sthis.fixEdges();
		System.out.println("New GRAPH:\n"+this.toString());
	}
	
	public void addNode(Node node) {
		strToNode.put(node.getName(), node);
		this.nodes.add(node);
		this.inEdges.put(node, new ArrayList<Node>());
		this.outEdges.put(node, new ArrayList<Node>());
		for (String k : node.getInEdges()) {
			Node n = strToNode.get(k);
			if (n != null) {
				inEdges.get(node).add(n);
				outEdges.get(n).add(node);
			}
		}
		for (String k : node.getOutEdges()) {
			Node n = strToNode.get(k);
			if (n != null) {
				inEdges.get(n).add(node);
				outEdges.get(node).add(n);
			}
		}
	}
	
	//TODO: sistemare questo da O(n^2) inserendolo in addNode e portandolo a O(nlogn) max
	public void fixEdges() {
		throw new UnsupportedOperationException();
		/*
		ArrayList<String> inedg;
		ArrayList<String> outedg;
		for(Node i: nodes) {
			inedg=i.getInEdges();
			outedg=i.getOutEdges();
			for(Node j:nodes) {
				for(String k:inedg) {
					if(j.getName().equals(k))
						inEdges.get(i).add(j);
				}
				for(String k:outedg) {
					if(j.getName().equals(k))
						outEdges.get(i).add(j);
				}
			}
		}*/
		
	}
	
	public void fixMobility(Node node) {
		if(node.getMobility()==0) {				//reset mobilità -> non ha senso  la mobilità se il nodo non si è mosso ==> devo resettare tutto
			for(Node i: outEdges.get(node)) {
				if (i.isMovable()) {
					i.setMobility(0);
					fixMobility(i);
				}
			}
		}
		else {
			for(Node i: outEdges.get(node)) {	//modifica mobilità dei nodi che si trovano sullo stesso livello. Non serve controllo di inamovibili perchè
												//nodo non può arrivare fino a loro
				if(i.getTime()+i.getMobility()==node.getMobility()+node.getTime()) {
					i.setMobility(i.getMobility()+1);
					fixMobility(i);
				}
			}
		}
	}
	
	//TODO: sistemare questi, passare da O(n^2) a O(nlogn) max
	public void asapSchedule() {
		List<Node> nodeToCheck=new ArrayList<>();
		for(Node i: nodes) {
			if(inEdges.get(i).isEmpty())
				i.setTime(0);
			else {
				i.setTime(-1);
				nodeToCheck.add(i);
			}
		}
		Node check;
		ArrayList<Node> checkIn;
		boolean flag;
		int maxTime;
		while(! nodeToCheck.isEmpty()) {
			flag=true;
			maxTime=0;
			check=nodeToCheck.get(0);
			nodeToCheck.remove(0);
			checkIn=inEdges.get(check);
			for(Node j: checkIn) {
				if(j.getTime()== -1)
					flag=false;
				else if(j.getTime()+1>maxTime)
					maxTime=j.getTime()+1;
			}
			if(flag) 
				check.setTime(maxTime);
			else nodeToCheck.add(check);
		}
		maxTime=0;
		for (Node i: nodes) {
			if(maxTime<i.getTime())
				maxTime=i.getTime();
		}
		this.totalTime=maxTime;
	}
	
	public void alapSchedule() {
		List<Node> nodeToCheck=new ArrayList<>();
		for(Node i: nodes) {
			if(i.getTime()==totalTime)
				i.setMaxMobility(0);
			else if(i.getOutEdges().isEmpty())
					i.setMaxMobility(totalTime-i.getTime());
			else {
				i.setMaxMobility(-1);
				nodeToCheck.add(i);
			}
		}
		Node check;
		ArrayList<Node> checkOut;
		boolean flag;
		int maxTime;
		while(! nodeToCheck.isEmpty()) {
			maxTime=totalTime;
			flag=true;
			check=nodeToCheck.get(0);
			nodeToCheck.remove(0);
			checkOut=outEdges.get(check);
			for(Node j: checkOut) {
				if(j.getMaxMobility()== -1)
					flag=false;
				else if(j.getTime()+j.getMaxMobility()<maxTime)
					maxTime=j.getTime()+j.getMaxMobility();
			}
			if(flag)
				check.setMaxMobility(maxTime-check.getTime()-1);
			else nodeToCheck.add(check);
		}
		for(Node i: nodes) {
			if(i.getMaxMobility()!=0)
				movableNodes++;
				
		}
		
	}
	
	public void parseFromString(String graph) {
		String[] nodes = graph.split("\n");
		for (String node : nodes) {
			addNode(new Node(node));
		}
	}
	
	public ArrayList<Chain> parseChainsFromString(String chainList) {
		ArrayList<Chain> chains = new ArrayList<Chain>();
		String[] singleChain = chainList.split("/");
		for (String cStr : singleChain) {
			Chain c = new Chain();
			String[] params = cStr.split(":");
			c.setVoid(Integer.parseInt(params[0]));
			c.setDangerLevel(Integer.parseInt(params[1]));
			for (int i = 3; i<params.length; i++){
				Node n = strToNode.get(params[i]);
				c.addNode(n, false);
			}
			c.setMinimumPosition(Integer.parseInt(params[2]));
			c.fixNodePosition();
			chains.add(c);
		}
		
		return chains;
	}
	
	public void printGraph() {
		for(Node i:nodes) {
			System.out.println(i.getName()+":"+i.getType()+"/"+inEdges.get(i)+"/"+outEdges.get(i));
		}
	}
	
	public String toString() {
		String ret="";
		for(Node i:nodes) {
			ret=ret+i.toString()+"\n";
		}
		return ret;
	}
	public String toStringReadable() {
		String ret="";
		for(Node i:nodes) {
			ret=ret+i.toStringReadable()+"\n";
		}
		return ret;
	}
	
	public void printToFile(PrintStream out) {
		out.println(totalTime+","+movableNodes);
		for (Node i: nodes) {
			out.println(i);
		}
	}

	public ArrayList<Node> getNodes() {
		return nodes;
	}

	public void setNodes(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}

	public HashMap<Node, ArrayList<Node>> getInEdges() {
		return inEdges;
	}

	public void setInEdges(HashMap<Node, ArrayList<Node>> inEdges) {
		this.inEdges = inEdges;
	}

	public HashMap<Node, ArrayList<Node>> getOutEdges() {
		return outEdges;
	}

	public void setOutEdges(HashMap<Node, ArrayList<Node>> outEdges) {
		this.outEdges = outEdges;
	}
	public int getTotalTime() {
		return this.totalTime;
	}

	public void resetMobility(Node node) {
		if(!inEdges.get(node).isEmpty()) {
			int maxdepth=0;
			for(Node i: inEdges.get(node)) {
				if(maxdepth<i.getMobility())
					maxdepth=i.getMobility();
			}
			node.setMobility(maxdepth);
		}
		else node.setMobility(0);
	}

}
