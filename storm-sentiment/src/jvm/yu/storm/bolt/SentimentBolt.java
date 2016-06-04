package yu.storm.bolt;

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

import yu.storm.tools.SentimentAnalyzer;
import yu.storm.tools.TweetExtractor;



public class SentimentBolt extends BaseRichBolt {

     private static final long serialVersionUID = 886149197481637894L;
     private OutputCollector collector;
         
     @Override
     public void prepare(
      Map                     map,
      TopologyContext         topologyContext,
      OutputCollector         outputCollector) 
     {
          SentimentAnalyzer.init();
          collector = outputCollector;         
     }

     @Override
     public void execute(Tuple tuple) {
          String line = tuple.getString(0);
          
          String originalTweet;
          String extractedTweet;

         if (line != null && line.split("DELIMITER").length > 2)
         {
            originalTweet = line.split("DELIMITER")[0];

            System.out.print("\t DEBUG SPOUT: BEFORE EXTRACTOR \n");
            System.out.print("\t " + originalTweet + "\n");
            extractedTweet = TweetExtractor.tweetRemover(originalTweet);
            System.out.print("\t DEBUG SPOUT: AFTER EXTRACTOR \n");
            System.out.print("\t " + extractedTweet);
            System.out.print("\t DEBUG SPOUT: BEFORE SENTIMENT \n");
            int sentiment = SentimentAnalyzer.findSentiment(extractedTweet);
            System.out.print("\t DEBUG SPOUT: AFTER SENTIMENT (" + String.valueOf(sentiment) + ") for \t" + originalTweet + "\n");
            collector.emit(new Values(line, sentiment));
         }
     }

     @Override
     public void declareOutputFields(OutputFieldsDeclarer declarer) {
          declarer.declare(new Fields("tweet", "sentiment"));         
     }
         
}