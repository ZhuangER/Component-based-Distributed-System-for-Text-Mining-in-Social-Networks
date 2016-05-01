package yu.storm.bolt;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import yu.storm.tools.SentimentAnalyzer;
import yu.storm.tools.TweetExtractor;



public class SentimentBolt extends BaseRichBolt {

     //private static final Log LOG = LogFactory.getLog(KafkaWordSplitter.class);
     private static final long serialVersionUID = 886149197481637894L;
     private OutputCollector collector;
     //LinkedBlockingQueue<String> queue = null;
         
     @Override
     public void prepare(
      Map                     map,
      TopologyContext         topologyContext,
      OutputCollector         outputCollector) 
     {
          // save the collector for emitting tuples
          //queue = new LinkedBlockingQueue<String>(1000);
          SentimentAnalyzer.init();
          collector = outputCollector;         
     }

     @Override
     public void execute(Tuple tuple) {
          String line = tuple.getString(0);
          String geoInfo;
          String originalTweet;
          String extractedTweet;
          String countryName;
          //int sentiment = 1;
          //queue.offer(line);

         if (line != null && line.split("DELIMITER").length > 2)
         {   

             originalTweet = line.split("DELIMITER")[0];
             geoInfo = line.split("DELIMITER")[1];
             // String urlInfo = line.split("DELIMITER")[2];
             //countryName = line.split("DELIMITER")[3];
             System.out.print(originalTweet+"\n");
             System.out.print(geoInfo+"\n");
         
              if(geoInfo != null && !geoInfo.equals("n/a"))
              {
                  //Utils.sleep(1000);
                  System.out.print("\t DEBUG SPOUT: BEFORE EXTRACTOR \n");
                  System.out.print("\t " + originalTweet + "\n");
                  extractedTweet = TweetExtractor.tweetRemover(originalTweet);
                  System.out.print("\t DEBUG SPOUT: AFTER EXTRACTOR \n");
                  System.out.print("\t " + extractedTweet);
                  System.out.print("\t DEBUG SPOUT: BEFORE SENTIMENT \n");
                  int sentiment = SentimentAnalyzer.findSentiment(extractedTweet);
                  //int sentiment = 1;
                  System.out.print("\t DEBUG SPOUT: AFTER SENTIMENT (" + String.valueOf(sentiment) + ") for \t" + originalTweet + "\n");
                  collector.emit(new Values(line, sentiment));
              }

         }
/*         if (line != null && line.split("DELIMITER").length > 2) {
              collector.emit(new Values(line, sentiment));
          }*/
         //collector.ack(tuple);
     }

     @Override
     public void declareOutputFields(OutputFieldsDeclarer declarer) {
          declarer.declare(new Fields("tweet", "sentiment"));         
     }
         
}