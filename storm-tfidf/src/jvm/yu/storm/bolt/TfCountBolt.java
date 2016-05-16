package yu.storm.bolt;

import backtype.storm.Config;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;

import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
//import udacity.storm.spout.RandomSentenceSpout;
import backtype.storm.utils.Utils;
import backtype.storm.task.TopologyContext;
import backtype.storm.task.OutputCollector;

import java.util.HashMap;
import java.util.Map;


//tf: the number of times a given term(t) appears in a given document(d) 
public class TfCountBolt extends BaseRichBolt
{
	private OutputCollector collector;
	private Map<String, Integer> tfCountMap;

	@Override 
	public void prepare(
		Map map,
		TopologyContext topologyContext,
		OutputCollector outputCollector
	)
	{
		collector = outputCollector;
		tfCountMap = new HashMap<String, Integer>();
	}

	
	public void execute(Tuple tuple)
	{
		String documentId = tuple.getStringByField("documentId");
		String term = tuple.getStringByField("term");
		// the tfKey is determined by both documentId and term
		String tfKey = documentId + "DELIMITER" + term;

		// count number of sentiment of countries
		if (tfCountMap.get(tfKey) == null) {
			tfCountMap.put(tfKey, 1);
		}
		else {
			Integer val = tfCountMap.get(tfKey);
			tfCountMap.put(tfKey, ++val);
		}
		
		collector.emit(new Values(tfKey, tfCountMap.get(tfKey)));
		//collector.ack(tuple);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer)
	{
		outputFieldsDeclarer.declare(
				new Fields("tfKey", "tfValue"));
	}
}
