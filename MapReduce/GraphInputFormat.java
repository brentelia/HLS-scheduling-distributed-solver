package MapReduce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

public class GraphInputFormat extends FileInputFormat<Text, Text> {
	
	private long splitsize;
	
	@Override
	public RecordReader<Text, Text> createRecordReader(InputSplit inputsplit,
			TaskAttemptContext taskattemptcontext) throws IOException, InterruptedException {

		return new GraphInputReader();
	}

	@Override
	protected long computeSplitSize(long blockSize, long minSize, long maxSize) {
		
		splitsize=super.computeSplitSize(blockSize, minSize, maxSize);
		System.out.println("SPLIT: "+splitsize);
		return splitsize;
	}
	
	@Override
	protected boolean isSplitable(JobContext context, Path filename) {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}