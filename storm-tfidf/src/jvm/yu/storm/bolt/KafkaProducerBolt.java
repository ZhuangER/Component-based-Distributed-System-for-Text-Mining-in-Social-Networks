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

public class KafkaProducerBolt extends BaseRichBolt{

	transient RedisConnection<String,String> redis;
	@Override
	public void prepare(
		Map                     map,
		TopologyContext         topologyContext,
		OutputCollector         outputCollector) {

		// instantiate a redis connection
		RedisClient client = new RedisClient("localhost",6379);
		// initiate the actual connection
		redis = client.connect();

		//CountryCodeConvert.initCountryCodeMapping();

		//collector = outputCollector;

	}


	@Override
	public void execute(Tuple tuple)
	{

		Rankings rankableList = (Rankings) tuple.getValue(0);

		for (Rankable r: rankableList.getRankings()){
		String word = r.getObject().toString();
		Long count = r.getCount();
		redis.publish("word-cloud", word + "|" + Long.toString(count));
		}

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer)
	{
		// nothing to add - since it is the final bolt
	}
}