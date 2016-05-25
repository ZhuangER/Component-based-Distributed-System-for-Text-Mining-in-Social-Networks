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

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Locale;

import yu.storm.tools.Rankings;
import yu.storm.tools.Rankable;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import yu.storm.tools.KafkaProducer;

public class KafkaProducerBolt extends BaseRichBolt{
	public static KafkaProducer producer;
	String brokerList = "";
    String topic = "";
    String age = "";
    String sync = "";

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
		producer.produce("");

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer)
	{
		// nothing to add - since it is the final bolt
	}
}