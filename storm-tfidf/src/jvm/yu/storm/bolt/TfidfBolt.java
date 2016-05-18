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

public class TfidfBolt extends BaseRichBolt {
	private OutputCollector collector;
	private double d;
	private Map<String, Integer> dfCountMap;
	private Map<String, Integer> tfCountMap;

	@Override 
	public void prepare(
		Map map,
		TopologyContext topologyContext,
		OutputCollector outputCollector
	)
	{
		collector = outputCollector;
		dfCountMap = new HashMap<String, Integer>();
		tfCountMap = new HashMap<String, Integer>();
	}

	
	public void execute(Tuple tuple)
	{
		// judge the tuple comes from which bolt
		if (tuple.getFields().get(0).equals("dCount")) {

			d = (double)tuple.getIntegerByField("dCount");

		} else if (tuple.getFields().get(0).equals("dfKey")) {

			String dfKey = tuple.getStringByField("dfKey");
			Integer dfValue = tuple.getIntegerByField("dfValue");
			dfCountMap.put(dfKey, dfValue);
/*			if (dfCountMap.get(dfKey) == null) {
				dfCountMap.put(dfKey, 1);
			}
			else {
				Integer val = dfCountMap.get(dfKey);
				dfCountMap.put(dfKey, ++val);
			}*/

		} else if (tuple.getFields().get(0).equals("tfKey")) {

			String tfKey =tuple.getStringByField("tfKey");
			Integer tfValue =tuple.getIntegerByField("tfValue");

			String documentId = tfKey.split("DELIMITER")[0];
			String term = tfKey.split("DELIMITER")[1];
			double tf = (double)tfValue;
			tfCountMap.put(tfKey, tfValue);
/*			if (tfCountMap.get(tfKey) == null) {
				tfCountMap.put(tfKey, tfValue);
			}
			else {
				Integer val = tfCountMap.get(tfKey);				
				tfCountMap.put(tfKey, ++val);
				tf = (double)val;
			}*/

			//calculate tfidf value
			if (dfCountMap.get(term) != null) {
				double df = (double)dfCountMap.get(term);
				double tfidf = tf * Math.log(d / (1.0 + df));
				collector.emit(new Values(term, documentId, tfidf));
			}

		}

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer)
	{
		outputFieldsDeclarer.declare(
				new Fields("term","documentId","tfidf"));
	}

}