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

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Locale;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import storm.tools.Rankings;
import storm.tools.Rankable;
import storm.tools.KafkaProducer;

import org.apache.log4j.Logger;


public class KafkaProducerBolt extends BaseRichBolt{
	private static KafkaProducer producer;
	private static final Logger LOG = Logger.getLogger(KafkaProducerBolt.class);
	String brokerList = "localhost:9092";
    String topic = "web";
    String sync = "sync";

    public KafkaProducerBolt(String kafkaTopic) {
    	topic = kafkaTopic;
    }

	@Override
	public void prepare( Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		producer = new KafkaProducer(topic);
		producer.configure(brokerList, sync);
		producer.start();
	}


	@Override
	public void execute(Tuple tuple) {
		try {
			Rankings rankableList = (Rankings) tuple.getValue(0);
			for (Rankable r: rankableList.getRankings()){
				String word = r.getObject().toString();
				Long count = r.getCount();
				producer.produce(word + "|" + Long.toString(count));
			}
		}
		catch (Exception e) {
			LOG.debug("CountBolt Exception in Execute function\n");
			LOG.debug(e);
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// nothing to add - since it is the final bolt
	}
}