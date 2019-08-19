package structure;
import java.util.ArrayList;

public class Node implements Comparable<Node>{
	
	private String name;
	private String type;
	private int time;
	private int mobility;
	private int maxMobility;
	private ArrayList<String> inEdges;
	private ArrayList<String> outEdges;
	private int dangerLevel;
	private Chain parentChain;
	private int position;
	
	public Node(Node other) {
		this.parentChain = null;
		this.position = 0;
		this.name=other.name;
		this.type=other.type;
		this.time=other.time;
		this.mobility=other.mobility;
		this.maxMobility=other.maxMobility;
		this.inEdges=new ArrayList<String>();
		for(String i: other.inEdges) {
			this.inEdges.add(i);
		}
		this.outEdges=new ArrayList<String>();
		for(String i: other.outEdges) {
			this.outEdges.add(i);
		}
	}
	
	public Node(String parse) {
		this.parentChain = null;
		this.position = 0;
		inEdges=new ArrayList<String>();		
		outEdges=new ArrayList<String>();
		String[] values= parse.split("/");
//		System.out.println( values[0]);
//		System.out.println( values[1]);
//		System.out.println( values[2]);
		String [] data= values[0].split(":");
		this.name = data[0].replaceAll(" ", "_");
		this.type= data[1];
		this.time=0;
		if(data.length == 4) {
			this.time=Integer.parseInt(data[2]);
			this.maxMobility=Integer.parseInt(data[3]);
		}
		else this.maxMobility=-1;
		this.mobility=0;
		String[] in= values[1].split(",");
		
		String[] out= values[2].split(",");
		if(in.length != 0) {
			for(int i=0;i<in.length ;i++) {
				inEdges.add(in[i]);
			}
		}
		if(out.length != 0) {
			for(int i=0;i<out.length ;i++) {
				outEdges.add(out[i]);
			}
		}
	}
	
	public boolean isMovable() {
		return this.maxMobility>0;
	}

	public String toString() {
		String out=this.name+":"+type+":"+(time+mobility)+":"+maxMobility+"/";
		if(inEdges.isEmpty())
			out+=",/";
		else { 
			for (String i:inEdges)
				out+=i+",";
			out=out.substring(0,out.length()-1)+"/";
		}
		if(outEdges.isEmpty())
			out+=",";
		else {
			for (String i:outEdges)
				out+=i+",";
			out=out.substring(0,out.length()-1);
		}
		return out;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public Chain getParentChain() {
		return parentChain;
	}
	
	public void setParentChain(Chain c) {
		parentChain = c;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public ArrayList<String> getInEdges() {
		return inEdges;
	}

	public void setInEdges(ArrayList<String> inEdges) {
		this.inEdges = inEdges;
	}

	public ArrayList<String> getOutEdges() {
		return outEdges;
	}

	public void setOutEdges(ArrayList<String> outEdges) {
		this.outEdges = outEdges;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Node) {
			return this.name.equals(((Node)obj).name);
		}
		return false;
	}
	@Override
	public int hashCode() {
		return name.hashCode()^type.hashCode();
	}
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getMobility() {
		return mobility;
	}
	

	public int getMaxMobility() {
		return maxMobility;
	}

	public void setMobility(int mobility) {
		this.mobility = mobility;
	}

	@Override
	public int compareTo(Node o) {
		int val=o.time-this.time; //se il tempo è inferiore bisogna inserire il nodo dopo nella lista
		if(val ==0)
			return this.name.compareTo(o.name);
		return val;
	}

	public void setMaxMobility(int maxMobility) {
		this.maxMobility = maxMobility;
	}
	
	public void incrementDangerLevel() {
		dangerLevel++;
	}
	
	public int getDangerLevel() {
		return dangerLevel;
	}

	public String toStringReadable() {
		String out=this.name+":"+type+":"+(time+mobility)+"/";
		if(inEdges.isEmpty())
			out+=",/";
		else { 
			for (String i:inEdges)
				out+=i+",";
			out=out.substring(0,out.length()-1)+"/";
		}
		if(outEdges.isEmpty())
			out+=",";
		else {
			for (String i:outEdges)
				out+=i+",";
			out=out.substring(0,out.length()-1);
		}
		return out;
	}
	
}
