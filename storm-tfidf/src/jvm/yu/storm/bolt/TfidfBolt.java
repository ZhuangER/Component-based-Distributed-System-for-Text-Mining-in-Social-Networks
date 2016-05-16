package yu.storm.bolt;

import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;

import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import backtype.storm.task.TopologyContext;
import backtype.storm.task.OutputCollector;

import java.util.Set;
import java.util.HashSet;

class TfidfBolt{
	private OutputCollector collector;
	private Interger dCount;

	@Override 
	public void prepare(
		Map map,
		TopologyContext topologyContext,
		OutputCollector outputCollector
	)
	{
		collector = outputCollector;
	}

	
	public void execute(Tuple tuple)
	{
		// judge the tuple comes from which bolt
		if (tuple.getFields().get(0).equals("dCount")) {
			dCount = tuple.getStringByField("dCount");
		}

		String documentId = tuple.getStringByField("documentId");
		
		// count number of sentiment of countries
		dCount.add(documentId);
	
		collector.emit(new Values(dCount.size()));
		//collector.ack(tuple);

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer)
	{
		outputFieldsDeclarer.declare(
				new Fields("dCount"));
	}

}