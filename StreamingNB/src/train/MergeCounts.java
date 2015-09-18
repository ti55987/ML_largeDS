//package train;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.StringTokenizer;

public class MergeCounts {

	public static void main(String[] args) 
	{
		InputStreamReader is = new InputStreamReader(System.in, Charset.defaultCharset());
		BufferedReader reader = new BufferedReader(is); 
		String event, curr_event, word = null, curr_word;
		int count, V = 0;
        String line;
        
        try {
        	line = reader.readLine();
        	StringTokenizer st = new StringTokenizer(line);
        	curr_event = st.nextToken();
        	event = curr_event;
        	count = Integer.parseInt(st.nextToken());
        	
        	if(event.contains("|"))
        	{
        		StringTokenizer stev = new StringTokenizer(event);
        		curr_word = stev.nextToken("|");
        		word = curr_word;
        		V++;
        	}
        	
			while ((line = reader.readLine()) != null) 
			{
				st = new StringTokenizer(line);
				curr_event = st.nextToken();
	        	
				if(curr_event.contains("|"))
	        	{
	        		StringTokenizer stev = new StringTokenizer(curr_event);
	        		curr_word = stev.nextToken("|");
	        		if(!word.equals(curr_word))
	        		{
	        			V++;
	        			word = curr_word;
	        		}
	        	}
	        	
				if(event.equals(curr_event))
				{
					count += Integer.parseInt(st.nextToken());
				}
				else
				{
					StringBuilder sb = new StringBuilder(event);
					sb.append("\t");
					sb.append(count);
					System.out.println(sb.toString());
					event = curr_event;
					count = Integer.parseInt(st.nextToken());
				}
				
			 }
			// Print out the last event
			StringBuilder sb = new StringBuilder(event);
			sb.append("\t");
			sb.append(count);
			System.out.println(sb.toString());
			System.out.println("Vocabulary_size\t" + V);
		} catch (IOException e) {
			e.printStackTrace();
		}
        

	}

}
