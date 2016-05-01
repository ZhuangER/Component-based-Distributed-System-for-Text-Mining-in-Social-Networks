package yu.storm.bolt;

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

import yu.storm.tools.CassandraConnection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;



/**
 * A bolt persistence data with cassandra
 */
public class PersistenceBolt extends BaseRichBolt
{
  //private OutputCollector collector;

  @Override
  public void prepare(
      Map                     map,
      TopologyContext         topologyContext,
      OutputCollector         outputCollector)
  {
    // instantiate a cassandra connection
    CassandraConnection cassandraClient = new CassandraConnection();
    
    cassandraClient.connect("127.0.0.1");
    System.out.println("Already connected!");
    cassandraClient.close();
    System.out.println("Already close!");
    
    //collector = outputCollector;
  }


  @Override
  public void execute(Tuple tuple)
  {
    // TODO persistence operations
    System.out.println("Cassandra Bolt is Working!");
  }

  public void declareOutputFields(OutputFieldsDeclarer declarer)
  {
    // nothing to add - since it is the final bolt
  }
}
