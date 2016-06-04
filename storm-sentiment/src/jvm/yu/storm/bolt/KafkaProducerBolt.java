package yu.storm.bolt;

import backtype.storm.Config;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import java.util.Map;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import yu.storm.tools.KafkaProducer;

public class KafkaProducerBolt extends BaseRichBolt{
	public static KafkaProducer producer;
	String brokerList = "localhost:9092";
    String topic = "sentiment";
    String sync = "sync";

	@Override
	public void prepare(
		Map                     map,
		TopologyContext         topologyContext,
		OutputCollector         outputCollector) {

		producer = new KafkaProducer(topic);
		producer.configure(brokerList, sync);
		producer.start();
	}


	@Override
	public void execute(Tuple tuple)
	{
		String tweet = tuple.getStringByField("tweet");
    	int personalSentiment = tuple.getIntegerByField("personalSentiment");
    	String countrySentiment = tuple.getStringByField("countrySentiment");
    	System.out.println("\t\t\tDEBUG ReportBolt: " + "Tweet countrySentiment:" + String.valueOf(countrySentiment));

    	producer.produce(tweet + "DELIMITER" + String.valueOf(personalSentiment) + "DELIMITER" + String.valueOf(countrySentiment));

    	// output formats
    	// tweet + screen_name + create_at + geo + countryName = personalSentiment + countrySentiment (n/a)
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer)
	{
		// nothing to add - since it is the final bolt
	}
}