//package train;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


class Pair{
	String[] key;
	double val;
	Pair(String[] k, double v){key = k; val = v;}
}

public class NBTest {
	public static void main(String[] args) 
	{	
		
		InputStreamReader is = new InputStreamReader(System.in, Charset.defaultCharset());
		BufferedReader reader = new BufferedReader(is);
		Map<String,Label> map = new HashMap<String,Label>();
		double num_training = 0, V = 0;
        Pair pair = parseline(reader);
        String curr_l = pair.key[0];
        Label label = new Label(pair);
        map.put(curr_l, label);
        
        while((pair = parseline(reader)) != null){
        	
        	curr_l = pair.key[0];
        	if(curr_l.equals("Train_Instances"))
        		num_training = pair.val;
        	else if(curr_l.equals("Vocabulary_size"))
        		V = pair.val;
        	else if((label = map.get(curr_l)) != null)
               label.updateLabel(pair);
           	else
           	{        		
           		label = new Label(pair);
           		map.put(curr_l, label);
           	}
        }
        
        
		String testfile = null;
		String line;
		
		for(String s: args)
		{
			if(s == "-t")
				continue;
			testfile = s;
		}
		
		try {
			BufferedReader buff = new BufferedReader(
					new FileReader(testfile));
			double m = 1;//smoothing parameter
			V = m/V;
			while((line = buff.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(line);
				Document doc = new Document();
				//ignore labels
				String irrelevant = st.nextToken();
				while(st.hasMoreElements())
				{
				   String word = st.nextToken();
				   for(Map.Entry<String, Label> entry: map.entrySet())
				   {
				       String name = entry.getKey();
				       Label y = entry.getValue();
				       double count = y.findWord(word);
				       double prob = Math.log((count+V)/(y.num_tokens+m));
				       doc.updateLabelCount(name, prob);
				   }
				}
				doc.predict(map, num_training);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		
	}
	
	public static Pair parseline(BufferedReader buff)
	{
    	String line;
    	Pair p = null;
		try {
			if((line= buff.readLine()) == null)
				return null;

	    	StringTokenizer st = new StringTokenizer(line);
	    	String[] key = parsekey(st.nextToken());
	    	double val = Double.parseDouble(st.nextToken());
	    	p = new Pair(key, val);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return p;
	}
	
	public static String[] parsekey(String str)
	{
		StringTokenizer st = new StringTokenizer(str);
		String[] key = {null, null};
    	if(str.contains("|"))
    	{
    		key[1] = st.nextToken("|");//word
    		key[0] = st.nextToken("|");//label
    	}
    	else if(str.contains("::"))
    	{
    		key[0] = st.nextToken("::");
    		key[1] = "num_tokens";
    	}
    	else if(str.contains(":"))
    	{
    		key[0] = st.nextToken(":");
    		key[1] = "num_instances";
    	}
    	else
    		key[0] = str;
    	
    	return key;
	}

}
