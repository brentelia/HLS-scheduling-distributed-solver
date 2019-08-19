package MapReduce;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class ExplorerReducer extends Reducer<Text, Text, Text, Text> {

//	private Map<String, Integer> bestCost;

	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
	
		System.out.println("#####START#####");
		context.write(new Text("Reducer"), new Text(CostCalculator.calculate(values.iterator().next().toString(), key.toString())));

	}
}