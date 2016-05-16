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


//df: how frequently a given term (t) appears across across all documents
public class DfCountBolt extends BaseRichBolt
{
	private OutputCollector collector;
	private Map<String, Integer> dfCountMap;

	@Override 
	public void prepare(
		Map map,
		TopologyContext topologyContext,
		OutputCollector outputCollector
	)
	{
		collector = outputCollector;
		dfCountMap = new HashMap<String, Integer>();
	}

	
	public void execute(Tuple tuple)
	{
		String documentId = tuple.getStringByField("documentId");
		String term = tuple.getStringByField("term");

		// count number of term across all documents
		if (dfCountMap.get(term) == null) {
			dfCountMap.put(term, 1);
		}
		else {
			Integer val = dfCountMap.get(term);
			dfCountMap.put(term, ++val);
		}
		
		collector.emit(new Values(term, dfCountMap.get(term)));
		//collector.ack(tuple);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer)
	{
		outputFieldsDeclarer.declare(
				new Fields("dfKey", "dfValue"));
	}

}
