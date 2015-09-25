import java.io.IOException;
import java.util.*;

import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.StringUtils;

public class run 
{
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		Configuration conf  = new Configuration();
		
		Job job = new Job(conf,"NB_train_hadoop");
		job.setJarByClass(NB_train_hadoop.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		job.setMapperClass(NB_train_hadoop.Map.class);
		job.setReducerClass(NB_train_hadoop.Reduce.class);
		job.setNumReduceTasks(Integer.parseInt(args[2]));
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		
		job.waitForCompletion(true);

	}
}
