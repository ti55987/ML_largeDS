//package train;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

public class NBTrain {
	
	
	static int BUFFERSIZE = 100;
	public static void main(String[] args) {
		
		InputStreamReader is = new InputStreamReader(System.in, Charset.defaultCharset());
		BufferedReader reader = new BufferedReader(is); 
		Map<String, Integer> map = new HashMap<String, Integer>();
		int num_training = 0;// (Y = *)
		int numWords = 0;
		
        String line;
        try {
			while ((line = reader.readLine()) != null) 
			{
				StringTokenizer st = new StringTokenizer(line);
				String[] labels = st.nextToken().split(",");
				numWords = st.countTokens();
				while(st.hasMoreElements())
				{
					StringBuilder sb = new StringBuilder(st.nextToken());
					sb.append("|");
					for(String l : labels)
					{	//(Y=y,W=wi)
						//addMap(map, sb.toString()+ l, 1);
						System.out.println(sb.toString()+ l + "\t1");
					}
				}
				
				for(String l : labels)
				{
					//addMap(map,l+":", 1);
					//addMap(map,l+"::", numWords);
					System.out.println(l + ":\t1");//(Y=y)
		        	System.out.println(l + "::\t" + numWords);//(Y=y,W=*)
		
				}
				num_training += labels.length;
				
			 }
		} catch (IOException e) {
			e.printStackTrace();
		}

        System.out.println("Train_Instances\t" + num_training);//(Y=*)
	}
	
	
	public static void addMap(Map<String, Integer> map, String key, int val)
	{
		Integer count;
		if((count = map.get(key)) != null)
			count += val;
		else
			count = val;
		
		map.put(key, count);
		if(map.size() > BUFFERSIZE)
			print(map);
	}
	
	public static void print(Map<String, Integer> map)
	{
		for(Map.Entry<String, Integer> entry: map.entrySet())
		{
		       String key = entry.getKey();
		       int val = entry.getValue();
		       System.out.println(key + "\t" + val);
		}
		
		map.clear();
	}

}
