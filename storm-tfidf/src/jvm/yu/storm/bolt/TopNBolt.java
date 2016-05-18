package yu.storm.bolt;

import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;

import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import backtype.storm.task.TopologyContext;
import backtype.storm.task.OutputCollector;

import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;

import yu.storm.tools.Term;

// to get the top n term
// maintain a linklist with size n
// once one tuple comes, insert it into the link list 
// if size beyone n, remove the last element
public class TopNBolt extends BaseRichBolt
{
	private OutputCollector collector;
	private HashMap<String, Double> tfidfMap;
	private HashMap<String, Double> sumMap;
	private Queue<Term> termQueue;
	private HashMap<String, Double> topTerm;
	transient RedisConnection<String,String> redis;

	@Override 
	public void prepare(
		Map map,
		TopologyContext topologyContext,
		OutputCollector outputCollector
	){
		collector = outputCollector;
		tfidfMap = new HashMap<String, Double>();
		sumMap = new HashMap<String, Double>();
		topTerm = new HashMap<String, Double>();

		// instantiate a redis connection
	    RedisClient client = new RedisClient("localhost",6379);
	    // initiate the actual connection
	    redis = client.connect();

		Comparator<Term> tfidfComparator = new Comparator<Term>() {
			@Override
			public int compare(Term t1, Term t2) {
				if (t1.getTfidf() - t2.getTfidf() < 0.0) {
					return -1;
				} else if (t1.getTfidf() - t2.getTfidf() == 0.0) {
					return 0;
				} else {
					return 1;
				}
			}
		};
		termQueue = new PriorityQueue<Term>(20, tfidfComparator);
	}

	
	public void execute(Tuple tuple)
	{
		String term = tuple.getStringByField("term");
		String documentId = tuple.getStringByField("documentId");
		double tfidf = tuple.getDoubleByField("tfidf");
		String tfidfKey = term + " " + documentId;


		double sum = 0.0;
		if (sumMap.get(term) != null) {
			sum = sumMap.get(term);
		}

		double pVal = 0.0;
		if (tfidfMap.get(tfidfKey) != null) {
			pVal = tfidfMap.get(tfidfKey);	
		}
		tfidfMap.put(tfidfKey, tfidf);
		
		double diff = tfidf - pVal;
		sumMap.put(term, sum+diff);

		Term item = new Term(term, sum+diff);
		if (topTerm.get(term) == null){
			if (termQueue.size() <= 20){
				termQueue.add(item);
				topTerm.put(term, sum+diff);
			}
			else {
				termQueue.add(item);
				topTerm.put(term, sum+diff);
				Term temp = termQueue.poll();
				topTerm.remove(temp.getTerm());
			}
		}
		else {
			//Term temp = new Term(term, topTerm.get(term));
			topTerm.put(term, sum+diff);

			for (Term t : termQueue) {
				if (t.getTerm() == term) {
					termQueue.remove(t);
					break;
				}
			}
			termQueue.add(new Term(term, sum+diff));
		}


		String termString = "";
		for (Term temp : termQueue) {
			termString += temp.toString() + ",";
			System.out.println(temp.getTerm());
			System.out.println(temp.getTfidf());
			
		}
		termString = termString.substring(0, termString.length()-1);

		//pubish the word-cloud, when finish one document
		redis.publish("word-cloud", termString);

		/*collector.emit(new Values(term, sum+diff));*/

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer)
	{
/*		outputFieldsDeclarer.declare(
				new Fields("term", "tfidf"));*/
	}

}
