package yu.storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.testing.TestWordSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Locale;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;

import yu.storm.tools.*;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;



/**
 * A bolt that prints the word and count to redis
 */
public class ReportBolt extends BaseRichBolt
{
  // place holder to keep the connection to redis
  transient RedisConnection<String,String> redis;
  @Override
  public void prepare(
      Map                     map,
      TopologyContext         topologyContext,
      OutputCollector         outputCollector)
  {
    // instantiate a redis connection
    RedisClient client = new RedisClient("localhost",6379);
    // initiate the actual connection
    redis = client.connect();

    CountryCodeConvert.initCountryCodeMapping();

  }


  @Override
  public void execute(Tuple tuple)
  {
	  String tweet = tuple.getStringByField("tweet");
    String geoinfo = tuple.getStringByField("geoinfo");
    int personalSentiment = tuple.getIntegerByField("personalSentiment");
    double countrySentiment = tuple.getDoubleByField("countrySentiment");
    String countryName = tuple.getStringByField("countryName");
    System.out.println("\t\t\tDEBUG ReportBolt: " + "Tweet countrySentiment:" + String.valueOf(countrySentiment));

    countryName = CountryCodeConvert.iso2CountryCodeToIso3CountryCode(countryName);

    redis.publish("WordCountTopology", geoinfo + "DELIMITER" + tweet + "DELIMITER" + String.valueOf(personalSentiment) + "DELIMITER" + countryName + "DELIMITER" + String.valueOf(countrySentiment));
  }

  public void declareOutputFields(OutputFieldsDeclarer declarer)
  {
    // nothing to add - since it is the final bolt
  }
}
