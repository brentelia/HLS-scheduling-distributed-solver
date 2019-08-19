package MapReduce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.LineRecordReader.LineReader;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import structure.Chain;
import structure.Dag;
import structure.Node;
import util.Pair;

public class GraphInputReader extends RecordReader<Text, Text> {
	private LineReader in;
	private Text key;
	private Dag graph;
	private List<Node> movNodes;
	private Node lastNode;
	private boolean firstStep;
	private long estimatedNumber;
	private int numClusters;
	private ArrayList<Chain> chains;
	private boolean jobsSent;
	private String chainStr;
	private String graphStr;

	@Override
	public void close() throws IOException {
		if (in != null) {
			in.close();
		}
	}

	@Override
	public Text getCurrentKey() throws IOException, InterruptedException {
		return new Text("");
	}

	@Override
	public Text getCurrentValue() throws IOException, InterruptedException {
		jobsSent = true;
		return new Text(graphStr);
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		return 0.5f; // inutile
	}

	@Override
	public void initialize(InputSplit genericSplit, TaskAttemptContext context)
			throws IOException, InterruptedException {
		jobsSent = false;
		FileSplit split = (FileSplit) genericSplit;
		final Path file = split.getPath();
		Configuration conf = context.getConfiguration();
		FileSystem fs = file.getFileSystem(conf);
		FSDataInputStream filein = fs.open(split.getPath());
		in = new LineReader(filein, conf);
		graph = new Dag();
		chainStr = "";

		Text line = new Text();
		graphStr = "";

		while (in.readLine(line) != 0) {
			graphStr+=line.toString()+"\n";
		}
		
	}

	/** 
	 * Crea il grafo successivo da analizzare
	 */
	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		if (jobsSent) {
			return false;
		}else {
			return true;
		}
//		
	}
}