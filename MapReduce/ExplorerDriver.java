package MapReduce;


import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

/*This class is responsible for running map reduce job*/
public class ExplorerDriver extends Configured implements Tool {
	// private static final String MAX_SPLIT_SIZE = "8388608";
	// private static final String MIN_SPLIT_SIZE = "0";
	public int run(String[] args) throws Exception {

		if (args.length != 3) {
			System.err.println("Usage: ExplorerDriver <input path> <outputpath> <#_nodes_to_use>");
			System.exit(-1);
		}
		System.out.println("----------------------------------------\nWelcome to map-red driver!\n----------------------------------------------\n");
		
		
		
		Configuration conf = getConf();
		conf.setBoolean("mapred.task.profile", true);
		conf.set("mapred.task.profile.params",
				"-agentlib:hprof=cpu=samples," + "heap=sites,depth=6,force=n,thread=y,verbose=n,file=%s");
		conf.set("mapred.task.profile.maps", "0-2");
		conf.set("mapred.task.profile.reduces", ""); 
		
		//set number of reducers by casting 3rd agument
		int nodes=Integer.parseInt(args[2]);
		
		conf.setInt("nodes", nodes);
		//conf.set("mapred.split.size", Long.toString(mapNumber));
		System.out.println("----------------------------------------\nUsed reducers: "+nodes+"\n----------------------------------------------\n");

		
		JobConf jobConf = new JobConf(conf, ExplorerDriver.class);
		jobConf.setNumReduceTasks(nodes);
		// Setting a job
		Job job = new Job(jobConf);

		job.setJarByClass(ExplorerDriver.class);
		job.setJobName("Graph Exploration");
		job.getConfiguration();


		// Adding input path of the file
		FileInputFormat.addInputPath(job, new Path(args[0]));
		job.setInputFormatClass(GraphInputFormat.class);
		// Set output directory
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setMapperClass(ExplorerMapper.class);
		job.setReducerClass(ExplorerReducer.class);
		job.setPartitionerClass(ExplorerPartitioner.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		System.exit(job.waitForCompletion(true) ? 0 : 1);
		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		ExplorerDriver driver = new ExplorerDriver();
		int exitCode = ToolRunner.run(driver, args);
		System.exit(exitCode);
	}
}
