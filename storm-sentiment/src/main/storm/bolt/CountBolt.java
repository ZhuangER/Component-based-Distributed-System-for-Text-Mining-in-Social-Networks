package storm.bolt;

import backtype.storm.Config;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import backtype.storm.task.OutputCollector;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Locale;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;

import org.apache.log4j.Logger;


public class CountBolt extends BaseRichBolt {
	private OutputCollector collector;
	private Map<String, Integer> SentimentDistribution;
	private Map<String, Integer> CountMap;
	private static Logger LOG = Logger.getLogger(CountBolt.class);

	@Override 
	public void prepare( Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		collector = outputCollector;
		SentimentDistribution = new HashMap<String, Integer>();
		CountMap = new HashMap<String, Integer>();
	}

	/**
	 * emit: tweet, geoinfo, countrySentiment, personalSentiment, countryName
	 * @param tuple [description]
	 */
	public void execute(Tuple tuple) {
		try {
			String tweet = tuple.getStringByField("original-tweet");
			String geoinfo = tuple.getStringByField("geoinfo");
			String matchedEmoticon = tuple.getStringByField("matchedEmoticon");
			int matchedEmoticonScore = tuple.getIntegerByField("matchedEmoticonScore");
			int personalSentiment = tuple.getIntegerByField("sentiment");
			String countryName = tuple.getStringByField("countryName");
			String sentimentKey = countryName + " " + String.valueOf(personalSentiment);
			double countrySentiment = 0;
			
			// emoticon has higher privilege than word
			if(matchedEmoticonScore != 0){
				personalSentiment = matchedEmoticonScore;
			}

			// count number of sentiment of countries
			if (CountMap.get(countryName) == null) {
				CountMap.put(countryName, 1);
			}
			else {
				Integer val = CountMap.get(countryName);
				CountMap.put(countryName, ++val);
			}

			// count sentiment of country
			if (SentimentDistribution.get(countryName) == null){
				SentimentDistribution.put(countryName, personalSentiment);
			}
			else {
				Integer tmp = SentimentDistribution.get(countryName);
				SentimentDistribution.put(countryName, tmp + personalSentiment);
			}
			
			// because sentiment range is between 0 to 4, to match 5 value to [0, 1]
			countrySentiment = (SentimentDistribution.get(countryName)*1.0) / (CountMap.get(countryName)*1.0) / 4.0;

			LOG.debug("\t\tCountBolt\tDEBUG EMIT Tweet " + tweet + ", geoinfo" + geoinfo + ", matcedEmoticon: " + matchedEmoticon + ", sentimentKey: " + sentimentKey + ", countrySentiment: " + countrySentiment + ", personalSentiment: " + personalSentiment + ", countryName: " + countryName);
			collector.emit(new Values(tweet, geoinfo, countrySentiment, personalSentiment, countryName));
		}
		catch (Exception e) {
			LOG.debug("CountBolt Exception in Execute function\n");
      		LOG.debug(e);
		}
		

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declare(
				new Fields("tweet", "geoinfo", "countrySentiment", "personalSentiment", "countryName"));
	}

}
