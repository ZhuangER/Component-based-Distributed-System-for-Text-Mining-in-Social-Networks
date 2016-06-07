package yu.storm;

import java.util.Arrays;

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

class TopNTweetTopology
{
  public static void main(String[] args) throws Exception
  {
    // create the topology
    TopologyBuilder builder = new TopologyBuilder();
    
    int TOP_N = 10;
    String zks = "localhost:2181";
    // String topic = "twitter";
    String topic = args[0];
    String zkRoot = "/storm"; // default zookeeper root configuration for storm
    String id = "word";
         
    BrokerHosts brokerHosts = new ZkHosts(zks);
    SpoutConfig spoutConf = new SpoutConfig(brokerHosts, topic, zkRoot, id);
    spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
    spoutConf.forceFromStart = false;
    spoutConf.zkServers = Arrays.asList(new String[] {"localhost"});
    spoutConf.zkPort = 2181;
    //spoutConf.bufferSizeBytes = 1024;
    //

    // attach the tweet spout to the topology - parallelism of 1
    builder.setSpout("kafka-spout", new KafkaSpout(spoutConf), 1);
    builder.setBolt("parse-bolt", new ParseTweetBolt(),10).shuffleGrouping("kafka-spout");
    builder.setBolt("intermediate-ranker", new IntermediateRankingsBolt(TOP_N), 4).fieldsGrouping("kafka", new Fields("word"));
    builder.setBolt("total-ranker", new TotalRankingsBolt(TOP_N), 1).globalGrouping("intermediate-ranker");
    // attach the report bolt using global grouping - parallelism of 1
    builder.setBolt("report-bolt", new KafkaProducerBolt(args[1]), 1).globalGrouping("total-ranker");

    // create the default config object
    Config conf = new Config();

    // set the config in debugging mode
    conf.setDebug(true);

    if (args != null && args.length > 2) {

      // run it in a live cluster

      // set the number of workers for running all spout and bolt tasks
      conf.setNumWorkers(3);

      // create the topology and submit with config
      StormSubmitter.submitTopology(args[2], conf, builder.createTopology());

    } else if (args != null && args.length == 2) {

      // run it in a simulated local cluster

      // set the number of threads to run - similar to setting number of workers in live cluster
      conf.setMaxTaskParallelism(4);

      // create the local cluster instance
      LocalCluster cluster = new LocalCluster();

      // submit the topology to the local cluster
      cluster.submitTopology("tweet-word-count", conf, builder.createTopology());

      // let the topology run for 300 seconds. note topologies never terminate!
      Utils.sleep(300000000);

      // now kill the topology
      cluster.killTopology("tweet-word-count");

      // we are done, so shutdown the local cluster
      cluster.shutdown();
    }
  }
}
