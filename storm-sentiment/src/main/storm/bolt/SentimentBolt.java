package storm.bolt;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import backtype.storm.Config;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import storm.tools.SentenceSentiment;
import storm.tools.TweetExtractor;

import org.apache.log4j.Logger;



public class SentimentBolt extends BaseRichBolt {

  private OutputCollector collector;
  private static Logger LOG = Logger.getLogger(SentimentBolt.class);
       
  @Override
  public void prepare( Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        SentenceSentiment.init();
        collector = outputCollector;         
  }

  @Override
  public void execute(Tuple tuple) {
    try {
      String line = tuple.getString(0);

      String originalTweet;
      String extractedTweet;

      if (line != null && line.split("DELIMITER").length > 2) {
        originalTweet = line.split("DELIMITER")[0];

        LOG.debug("\t DEBUG SPOUT: BEFORE EXTRACTOR \n");
        LOG.debug("\t " + originalTweet + "\n");
        extractedTweet = TweetExtractor.tweetRemover(originalTweet);
        LOG.debug("\t DEBUG SPOUT: AFTER EXTRACTOR \n");
        LOG.debug("\t " + extractedTweet);
        LOG.debug("\t DEBUG SPOUT: BEFORE SENTIMENT \n");
        int sentiment = SentenceSentiment.findSentiment(extractedTweet);
        LOG.debug("\t DEBUG SPOUT: AFTER SENTIMENT " +  Integer.toString(sentiment) + " for \t" + originalTweet + "\n" );
        collector.emit(new Values(line, sentiment));
      }
    }
    catch (Exception e) {
      LOG.debug("\t DEBUG SentimentBolt:\tSentimentBolt Exception in Execute function\n");
      LOG.debug(e);
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
      declarer.declare(new Fields("tweet", "sentiment"));         
  }
}