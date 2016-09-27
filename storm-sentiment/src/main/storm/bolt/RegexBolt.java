package storm.bolt;

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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import storm.tools.EmoticonSentiment;

import org.apache.log4j.Logger;

/**
 * A bolt that matches emoticons and emoji in the tweet
 */
public class RegexBolt extends BaseRichBolt {
  private OutputCollector collector;
  private Map<String, String> emoticonAndScore;
  private static Logger LOG = Logger.getLogger(RegexBolt.class);

  @Override
  public void prepare( Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
    collector = outputCollector;
  }

  @Override
  public void execute(Tuple tuple) {
    try {
      // get the word from the 1st column of incoming tuple

      String originalTweet = tuple.getStringByField("tweet").split("DELIMITER")[0];
      String geoinfo = tuple.getStringByField("tweet").split("DELIMITER")[1];
      int sentiment = tuple.getIntegerByField("sentiment");
      String countryName = tuple.getStringByField("tweet").split("DELIMITER")[3];
      
      emoticonAndScore = EmoticonSentiment.getScoreIfEmoticonPresent(originalTweet);
      int matchedEmoticonScore = Integer.parseInt(emoticonAndScore.get("score"));
      String matchedEmoticon = emoticonAndScore.get("emoticon");
      
      LOG.debug("\tDEBUG RegexBolt\t Emotion: " + matchedEmoticon + " Score: " + emoticonAndScore.get("score") + "\n");

      collector.emit(new Values(originalTweet,geoinfo,matchedEmoticonScore, matchedEmoticon, sentiment, countryName));
    } 
    catch(Exception e) {
      LOG.debug("RegexBolt Exception in Execute function\n");
      LOG.debug(e);
    }
  }
  
  @Override
  public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
    outputFieldsDeclarer.declare(new Fields("original-tweet", "geoinfo", "matchedEmoticonScore", "matchedEmoticon", "sentiment", "countryName"));
  }
}
