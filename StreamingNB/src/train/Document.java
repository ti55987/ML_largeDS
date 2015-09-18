//package train;

import java.util.HashMap;
import java.util.Map;

public class Document
{
	Map<String, Double> map;
	
	Document()
	{
		map = new HashMap<String, Double>();
	}
	
	public void updateLabelCount(String l, double new_count)
	{
		Double count;
		if((count = map.get(l)) != null)
			count += new_count;
		else
			count = new_count;
		
		map.put(l, count);
	}
	
	public void predict(Map<String, Label> map_l, double num_training)
	{
		double max = -Double.MAX_VALUE;
		double dom_y = map_l.size();
		double m = 1;
		dom_y = m/dom_y;
		String best_l = null;
		for(Map.Entry<String, Double> entry: map.entrySet())
		{
			String key = entry.getKey();
		    double val = entry.getValue();
		    double cy = map_l.get(key).num_instances + dom_y;
		    double prob = val + Math.log(cy/(num_training+m));

		    if(max <= prob)
		    {
		    	max = prob;
		    	best_l = key;
		    }
		    
		}
		System.out.println(best_l+"\t"+max);
	}
}

