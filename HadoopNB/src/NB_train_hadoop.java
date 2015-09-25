import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.StringUtils;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.hadoop.io.*;

public class NB_train_hadoop 
{
	
	public static class Map extends Mapper<LongWritable, Text, Text, IntWritable>
	{
		private final static IntWritable one = new IntWritable(1);
		private Text out = new Text();
		
		public void map(LongWritable key, Text value, Context context) throws 
		IOException, InterruptedException
		{
			String line = value.toString();
			StringTokenizer st = new StringTokenizer(line);
			String ID = st.nextToken();
			String[] labels = st.nextToken().split(",");
			Vector<String> word = new Vector<String>();
			tokenizedoc(st,  word);
			
			for(String w : word)
			{				
				for(String l : labels)
				{	
					StringBuilder sb = new StringBuilder("Y=");
					sb.append(l);
					sb.append(",W=");
					sb.append(w);
					out.set(sb.toString());
					context.write(out, one);//(Y=y,W=wi)
				}
			}
						
			for(String l : labels)
			{
				StringBuilder sb = new StringBuilder("Y=");
				sb.append(l);
				out.set(sb.toString());
				context.write(out, one);//(Y=y)
				out.set(sb.toString() + ",W=*");
				context.write(out, new IntWritable(word.size()));//(Y=y,W=*)
			}
			
			out.set("Y=*");
			context.write(out, new IntWritable(labels.length));//(Y=*)
		}
	}
	
	public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable>
	{
		private IntWritable result = new IntWritable();
		
		public void reduce(Text key, Iterable<IntWritable> values, Context context)
		throws IOException, InterruptedException{

			int sum = 0;
			for(IntWritable val : values){
				sum += val.get();
			}
			result.set(sum);
			
			context.write(key, result);
		}
	}
	
	static void tokenizedoc(StringTokenizer st,  Vector<String> word)
	{
		while(st.hasMoreTokens())
		{				
			String w = st.nextToken();
			w = w.replaceAll("\\W", "");
			if(w.length() > 0)
			{
				word.add(w);
			}
		}

	}
}
