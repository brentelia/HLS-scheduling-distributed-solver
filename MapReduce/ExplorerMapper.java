package MapReduce;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import structure.Chain;
import structure.Dag;
import structure.Node;
import util.Pair;

public class ExplorerMapper extends Mapper<Text, Text, Text, Text> {

	/**
	 * key= Starting point,config to calc,list of chains
	 * value= toString del grafo da ricreare
	 */
	@Override
	public void map(Text key, Text value, Context context) throws IOException, InterruptedException {

		Configuration conf = context.getConfiguration();
		Dag graph = new Dag();
		graph.parseFromString(value.toString());

		graph.asapSchedule();
		graph.alapSchedule();

		
		Pair<Long, ArrayList<Chain>> tmpPair = Stimatore.stima(graph);
		
		long estimatedNumber = tmpPair.getFirst();
		ArrayList<Chain> chains = tmpPair.getSecond();
		
		String chainStr = "";
		for (Chain c : chains) {
			chainStr += (chainStr== "" ? "" : "/") +c.toString();
		}
		System.out.println("  ");
		System.out.println("est num: "+estimatedNumber);
		int numClusters = conf.getInt("nodes", 0);
		float partial = 0f;
		for (int i = 0; i<numClusters;i++) {
			String baseStr = ""+((int)partial) + "," + ((int)(estimatedNumber/(float)numClusters)+1)+",";
			partial += (estimatedNumber/(float)numClusters);
			context.write(new Text(baseStr+chainStr), new Text(value));
		}
		

	}

}