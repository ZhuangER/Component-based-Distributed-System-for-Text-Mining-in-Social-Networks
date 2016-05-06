package yu.storm;

import java.util.Arrays;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;
import backtype.storm.spout.SchemeAsMultiScheme;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

import yu.storm.bolt.CountBolt;
import yu.storm.bolt.PersistenceBolt;
import yu.storm.bolt.RegexBolt;
import yu.storm.bolt.ReportBolt;
import yu.storm.bolt.SentimentBolt;


class TweetTopology
{
  public static void main(String[] args) throws Exception
  {
    // create the topology
    TopologyBuilder builder = new TopologyBuilder();


    // create kafka spout
    String zks = "localhost:2181";
    String topic = "mytopic";
    String zkRoot = "/storm"; // default zookeeper root configuration for storm
    String id = "word";
         
    BrokerHosts brokerHosts = new ZkHosts(zks);
    SpoutConfig spoutConf = new SpoutConfig(brokerHosts, topic, zkRoot, id);
    spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
    spoutConf.forceFromStart = true;
    spoutConf.zkServers = Arrays.asList(new String[] {"localhost"});
    spoutConf.zkPort = 2181;
    //spoutConf.bufferSizeBytes = 1024;


    // set topology
    builder.setSpout("kafka-spout", new KafkaSpout(spoutConf), 1); 
    builder.setBolt("sentiment-bolt", new SentimentBolt(), 10).shuffleGrouping("kafka-spout");
    builder.setBolt("regex-bolt", new RegexBolt(), 10).shuffleGrouping("sentiment-bolt");
    builder.setBolt("count-bolt", new CountBolt(), 10).fieldsGrouping("regex-bolt", new Fields("countryName"));
    builder.setBolt("report-bolt", new ReportBolt(), 1).globalGrouping("count-bolt");
    //builder.setBolt("persistence-bolt", new PersistenceBolt(), 1).globalGrouping("count-bolt");

    // create the default config object
    Config conf = new Config();

    // set the config in debugging mode
    conf.setDebug(true);

    if (args != null && args.length > 0) {

      // run it in a live cluster

      // set the number of workers for running all spout and bolt tasks
      conf.setNumWorkers(3);

      // create the topology and submit with config
      StormSubmitter.submitTopology(args[0], conf, builder.createTopology());

    } else {

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
