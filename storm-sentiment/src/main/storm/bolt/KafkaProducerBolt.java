package storm.bolt;

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

import storm.tools.KafkaProducer;

import org.apache.log4j.Logger;

public class KafkaProducerBolt extends BaseRichBolt{
	private static Logger LOG = Logger.getLogger(KafkaProducerBolt.class);
	private static KafkaProducer producer;
	String brokerList = "localhost:9092";
    String topic = "web";
    String sync = "sync";

    public KafkaProducerBolt(String kafkaTopic) {
    	this.topic = kafkaTopic;
    }

	@Override
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		producer = new KafkaProducer(topic);
		producer.configure(brokerList, sync);
		producer.start();
	}

	/**
	 * [execute description]
	 * @param tuple [description]
	 * output format: 
	 * tweet + screen_name + create_at + geo + countryName = personalSentiment + countrySentiment (n/a)
	 */
	@Override
	public void execute(Tuple tuple) {
		try {
			String tweet = tuple.getStringByField("tweet");
	    	int personalSentiment = tuple.getIntegerByField("personalSentiment");
	    	String countrySentiment = tuple.getStringByField("countrySentiment");
	    	System.out.println("\t\t\tDEBUG ReportBolt: " + "Tweet countrySentiment:" + String.valueOf(countrySentiment));

	    	producer.produce(tweet + "DELIMITER" + String.valueOf(personalSentiment) + "DELIMITER" + String.valueOf(countrySentiment));

		}
		catch (Exception e){
			LOG.debug("KafkaProducerBolt Exception in Execute function\n");
      		LOG.debug(e);
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// nothing to add - since it is the final bolt
	}
}