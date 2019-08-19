package MapReduce;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class ExplorerPartitioner extends Partitioner<Text, Text> {

	private Integer counter;
	@Override
	public int getPartition(Text key, Text val, int partitions) {
		return (counter == null ? counter = 0 : ++counter);
	}

}
