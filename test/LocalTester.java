package test;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.TreeSet;

import MapReduce.CostCalculator;
import MapReduce.Stimatore;
import structure.Chain;
import structure.Dag;
import structure.Node;
import util.Pair;

public class LocalTester {

	public static void main(String[] args) {
		Dag graph;
		BufferedReader in;
		String line;
		String graphStr; 
		String[] tests= {"CFG.txt","DAG.txt","EVIL.txt","FREE.txt","XTEA.txt","MULTI.txt"};

		try {
//			PrintStream fileOut = new PrintStream(new FileOutputStream("log.txt"));
//			System.setOut(fileOut);

			System.out.println("===BEGIN TEST====");
			for(int iterator=0; iterator<tests.length;iterator++) {
			graphStr	= "";
			graph= new Dag();
			in = new BufferedReader(new FileReader("./src/test/"+tests[iterator]));
			System.out.println("Testing file: "+tests[iterator]);
			/*
			 * long startDAG = System.nanoTime(); while ((line=in.readLine())!= null) {
			 * graph.addNode(new Node(line)); graphStr += line + "\n"; }
			 * 
			 * graph.fixEdges();
			 * 
			 * long stopDAG = System.nanoTime();
			 * 
			 * System.out.println("Slow: "+ (stopDAG-startDAG)); graph = new Dag();
			 */

			while ((line = in.readLine()) != null) {
				graphStr += line + "\n";
			}

			// startDAG = System.nanoTime();
			graph.parseFromString(graphStr);
			// stopDAG = System.nanoTime();

			// System.out.println("Fast: "+ (stopDAG-startDAG));

			graph.asapSchedule();

			//System.out.println("ASAP\n" + graph);

			graph.alapSchedule();

			ArrayList<Node> movNodes = new ArrayList<Node>();
			for (Node i : graph.getNodes()) {
				if (i.isMovable())
					movNodes.add(i);
			}
			Collections.sort(movNodes);
			// metodo grezzo
			double ext_cost = 1;
			for (Node i : movNodes) {
				ext_cost *= (i.getMaxMobility() + 1);
			}

			// formula C

			// calcolare Num combinazioni

			long startTime = System.nanoTime();
			Pair<Long, ArrayList<Chain>> tmpPair = Stimatore.stima(graph);
			long stopTime = System.nanoTime();

			long perm = tmpPair.getFirst();
			ArrayList<Chain> chains = tmpPair.getSecond();

			//System.out.println("Estimated number of expansions with Raw formula: " + ext_cost);
			System.out.println(
					"Estimated number of expansions with approximated formula: " + perm + ", Time (ns): " + (stopTime - startTime));

			long slowStartTime = System.nanoTime();
			boolean loop = true, firstStep = true;
			Node lastNode = movNodes.get(movNodes.size() - 1);
			int j = 0;
			while (loop) {
				if (firstStep) {
					firstStep = false;
					loop = true;
				} else {
					Node pop;
					int i = 0;
					boolean loop2 = true;
					while (loop2) { // provare a fare solo per mezza lista (ordine invertito) e ad inviare info su
									// altra metà a mapper
						pop = movNodes.get(i);
						if (pop.getMobility() < pop.getMaxMobility()) { // aggiorna la mobilità di ogni nodo
							pop.setMobility(pop.getMobility() + 1);
							graph.fixMobility(pop);
							// System.out.println("--->"+graph+"\n");
							loop2 = false;
						} else if (pop.equals(lastNode)) {
							// System.out.println("--->"+graph+"\n");
							loop = false;
							loop2 = false;
						} else {
							graph.resetMobility(pop);
							graph.fixMobility(pop);
							i++;
						}
					}

				}
				// System.out.println(j+"--->"+graph+"\n");
				j++;
			}
			long slowStopTime = System.nanoTime();

			System.out.println("Real number of expansions: " + (j - 1) + " , Time (ns): " + (slowStopTime - slowStartTime));
			System.out.println("Speedup: " + ((slowStopTime - slowStartTime) / (stopTime - startTime) + "x"));
			System.out.println("Node overestimation: "+((perm-(j-1))*100)/(j-1) +"%");
			System.out.println("-----------------");
//			System.out.println("====END TEST====");
			
			
			
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//			TEST CLUSTER
			
			
//			System.out.println("BEGINNING TEST");			
			
			String chainStr = "";
			for (Chain c : chains) {
			//	System.out.println(c.toString());
				chainStr += (chainStr== "" ? "" : "/") +c.toString();
			}
			//String baseStr = ""+(perm/10*3) + "," + (perm/10)+",";
			String baseStr = ""+(0) + "," + (perm)+",";
			String graph_str = graph.toString();
			String key = baseStr+chainStr;
			startTime = System.nanoTime();
			System.out.println(CostCalculator.calculate(graph_str, key));
			long endTime = System.nanoTime();
			System.out.println("Errors: "+ CostCalculator.errors);
			// graph.printToFile(out);
			in.close();
			System.out.println("Time (ns): "+(endTime-startTime));
			System.out.println("==============\n");
			}
			// out.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("crash");
		}
		System.out.println("====END TEST====");
	}
}
