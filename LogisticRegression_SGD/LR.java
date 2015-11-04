import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

public class LR {

	static double overflow = 20;
	static int N ; // dictionary size
	static int class_num = 14;
	static final String[] strs = {"nl","el","ru","sl","pl","ca","fr",
			"tr","hu","de","hr","es","ga","pt"};
	
	public static void main(String[] args) 
	{
		// command : LR <vocabulary size> <learning rate>
		//<regularization coefficient> <max iterations>
		
		N = Integer.parseInt(args[0]);
		double learning_rate = Double.parseDouble(args[1]);
		double regularization = 2*Double.parseDouble(args[2]);
		int T  = Integer.parseInt(args[3]);
		int training_sz  = Integer.parseInt(args[4]);
		String test_file = args[5];
		
		List<String> Y = new ArrayList<String>();
		List<List<Double>> weights = new ArrayList<List<Double>>();
		List<Integer> A = new ArrayList<Integer>();
		
		InputStreamReader is = new InputStreamReader(System.in, Charset.defaultCharset());
		BufferedReader reader = new BufferedReader(is); 
		String line = null;
		int t = 1, iter = 0, k = 0;
		double rate = 0;
		
		//Training
		init(Y, weights, A);
		try {
			while (t <= T && (line = reader.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(line);
				HashSet<String> set = new HashSet<String>();
				StringTokenizer labels = new StringTokenizer(st.nextToken());
				int[] feature = new int[N];
				double[] prob = new double[class_num];
				int i = 0;
				
				while(labels.hasMoreElements())
				{
					set.add(labels.nextToken(","));
				}
				
				while(st.hasMoreElements())
				{
					int id = hash(st.nextToken());
					feature[id] = 1;	
				}
				//predict p;
				for (List<Double> W : weights)
				{
					prob[i++] = predict(W, feature);
				}
				
				rate = learning_rate/Math.pow(t,2);
				k++;
				
				for(i = 0; i < N; i++)
				{
					if(feature[i] > 0)
					{
						for (int j= 0; j < class_num; j++)
						{	
							List<Double> B = weights.get(j);
						    int y = (set.contains(Y.get(j))) ? 1: 0;
						    double move = rate * (y-prob[j]) * feature[i];
						    double b = Math.pow(1- rate*regularization, k-A.get(i));
						    
						    B.set(i, B.get(i)*b + move);
						}
						A.set(i, k);
					}
				}
				
				
				if(iter++ % training_sz == 0) t++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Testing
		testing(Y ,weights ,test_file);

	}
	
	public static void testing(List<String> Y ,List<List<Double>> weights, 
				String test_file)
	{
		try {
			BufferedReader reader= new BufferedReader(new FileReader(test_file));
			String line = null;
			while((line = reader.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(line);
				String irrelevant = st.nextToken();//ignore labels
				int[] feature = new int[N];
				double prob = 0;
				
				while(st.hasMoreElements())
				{
					int id = hash(st.nextToken());
					feature[id] = 1;		
				}
				//predict p;
				StringBuilder sb = new StringBuilder();
				for (int i= 0; i < class_num; i++)
				{
					prob = predict(weights.get(i), feature);
					sb.append(Y.get(i)+"\t"+prob + ",");
				}
				
				if(sb.length() > 0)
				{
					sb.deleteCharAt(sb.length()-1);//get rid of the ',' at last
					System.out.println(sb.toString());
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static double predict(List<Double> w, int[] feature)
	{
		double p = 0;
		for(int i = 0; i < N; i++)
		{
			if(feature[i] > 0)
			{
				p += w.get(i)*(double)feature[i];
			}
		}
		if(p > overflow) p = overflow;
		else if(p < -overflow) p = -overflow;
		//double exp = Math.exp(p);
		return 1/(1 + Math.exp(-p));
	}
	
	public static void init_weight(List<Double> list)
	{
		for(int i = 0; i < N; i++) list.add(0.0);
	}
	
	public static void init(List<String> Y, List<List<Double>> weights, List<Integer> A)
	{
		for(String l : strs)
		{
			Y.add(l);
			List<Double> w = new ArrayList<Double>();
			init_weight(w);
			weights.add(w);
		}
		
		for(int i = 0; i < N; i++) A.add(0);
	}
	
	public static int hash(String word)
	{
		int id = word.hashCode() % N;
		return (id < 0) ? id + N : id;
	}
}
