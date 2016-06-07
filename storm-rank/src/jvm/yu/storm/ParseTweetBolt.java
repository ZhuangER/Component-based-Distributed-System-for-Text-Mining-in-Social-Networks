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

import java.util.Map;
import java.util.Arrays;

/**
 * A bolt that parses the tweet into words
 */
public class ParseTweetBolt extends BaseRichBolt 
{
  // To output tuples from this bolt to the count bolt
  OutputCollector collector;

  @Override
  public void prepare(
      Map                     map,
      TopologyContext         topologyContext,
      OutputCollector         outputCollector) 
  {
    // save the output collector for emitting tuples
    collector = outputCollector;
  }

  @Override
  public void execute(Tuple tuple) 
  {
    // get the 1st column 'tweet' from tuple
    String line = tuple.getString(0);
    if (line.contains("DELIMITER")) {
      String wordCount = line.split("DELIMITER")[5];
      if (wordCount.contains("|")) {
        String word = wordCount.split("\\|")[0];
        int count = Integer.parseInt(line.split("\\|")[1]); 
        Long longCount = (long) count;

        collector.emit(new Values(word, (Long)longCount));
      }
    }
    else {
      String word = line.split("\\|")[0];
      int count = Integer.parseInt(line.split("\\|")[1]);
      Long longCount = (long) count;

      collector.emit(new Values(word, (Long)longCount));
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) 
  {
    // tell storm the schema of the output tuple for this spout
    // tuple consists of a single column called 'tweet-word'
    declarer.declare(new Fields("word", "count"));
  }
}