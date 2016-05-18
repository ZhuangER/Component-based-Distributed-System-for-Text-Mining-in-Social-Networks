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

	@Override 
	public void prepare(
		Map map,
		TopologyContext topologyContext,
		OutputCollector outputCollector
	){
		collector = outputCollector;
		tfidfMap = new HashMap<String, Double>();
		sumMap = new HashMap<String, Double>();

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
		termQueue.add(item);



		collector.emit(new Values(term, sum+diff));



		//collector.ack(tuple);

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer)
	{
		outputFieldsDeclarer.declare(
				new Fields("term", "tfidf"));
	}

}
