//package train;

import java.util.HashMap;
import java.util.Map;

public class Label
{

	Map<String, Double> map;
	double num_instances;//(Y=y)
	double num_tokens;//(Y=y,W=*);
	String name;
	
	Label()
	{
		map = new HashMap<String, Double>();
	}
	Label(Pair p)
	{
		map = new HashMap<String, Double>();
		updateLabel(p);
	}
	public void addWord(String w, double count)
	{
		map.put(w, count);
	}
	
	public double findWord(String w)
	{
		Double count = map.get(w);
		if(count == null)
			return 0;
		else 
			return count;
	}
	
	public void updateLabel(Pair pair)
	{
       	if(pair.key[1].equals("num_tokens"))
    		num_tokens = pair.val;
       	else if(pair.key[1].equals("num_instances"))
       		num_instances = pair.val;
       	else
       		addWord(pair.key[1], pair.val);
       	
	}

}