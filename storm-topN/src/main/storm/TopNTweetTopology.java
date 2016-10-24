package storm;

import java.util.Arrays;
import java.util.UUID;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

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
import backtype.storm.spout.SchemeAsMultiScheme;

import org.apache.log4j.Logger;

import storm.bolt.*;

class TopNTweetTopology {
  private static final Logger LOG = Logger.getLogger(TopNTweetTopology.class);
  private static final String spoutId = "kafka-spout";
  private static final String parseId = "parse-bolt";
  private static final String interRankerId = "intermediate-ranker";
  private static final String totalRankerId = "total-ranker";
  private static final String reportId = "kafka-producer";
  private static final String topologyName = "topN-topology";
  

  public static void main(String[] args) throws Exception {
    // create the topology
    TopologyBuilder builder = new TopologyBuilder();
    
    int TOP_N = 10;
    String zks = "localhost:2181";
    String topic = args[0];
    String zkRoot = "/" + topic; // default zookeeper root configuration for storm
    String id = UUID.randomUUID().toString();
         
    BrokerHosts brokerHosts = new ZkHosts(zks);
    SpoutConfig spoutConf = new SpoutConfig(brokerHosts, topic, zkRoot, id);
    spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
    spoutConf.forceFromStart = false;
    spoutConf.zkServers = Arrays.asList(new String[] {"localhost"});
    spoutConf.zkPort = 2181;
    // spoutConf.forceStartOffsetTime(-1);
    //spoutConf.bufferSizeBytes = 1024;

    // attach the tweet spout to the topology - parallelism of 1
    builder.setSpout(spoutId, new KafkaSpout(spoutConf), 1);
    builder.setBolt(parseId, new ParseTweetBolt(),10).shuffleGrouping(spoutId);
    builder.setBolt(interRankerId, new IntermediateRankingsBolt(TOP_N), 4).fieldsGrouping(parseId, new Fields("word"));
    builder.setBolt(totalRankerId, new TotalRankingsBolt(TOP_N), 1).globalGrouping(interRankerId);
    // attach the report bolt using global grouping - parallelism of 1
    builder.setBolt(reportId, new KafkaProducerBolt(args[1]), 1).globalGrouping(totalRankerId);

    Config conf = new Config();
    conf.setDebug(true);

    if (args != null && args.length > 2) {
      LOG.info("Running in cluster mode");

      conf.setNumWorkers(3);

      StormSubmitter.submitTopology(args[2], conf, builder.createTopology());

    } 
    else if (args != null && args.length == 2) {
      LOG.info("Running in local mode");

      conf.setMaxTaskParallelism(4);

      LocalCluster cluster = new LocalCluster();

      cluster.submitTopology(topologyName, conf, builder.createTopology());

      Utils.sleep(300000000);

      cluster.killTopology(topologyName);
      cluster.shutdown();
    }
  }
}
