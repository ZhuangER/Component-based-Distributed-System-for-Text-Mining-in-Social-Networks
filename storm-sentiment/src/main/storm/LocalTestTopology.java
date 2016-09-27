package yu.storm;

import java.util.Arrays;
import java.util.UUID;

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
import yu.storm.bolt.RegexBolt;
import yu.storm.bolt.SentimentBolt;
import yu.storm.bolt.KafkaProducerBolt;


class LocalTestTopology
{
  public static void main(String[] args) throws Exception
  {
    // create the topology
    TopologyBuilder builder = new TopologyBuilder();


    // create kafka spout
    String zks = "localhost:2181";
    // String topic = "twitter";
    String topic = args[0];
    String zkRoot = "/" + topic; // default zookeeper root configuration for storm
    String id = UUID.randomUUID().toString();
         
    BrokerHosts brokerHosts = new ZkHosts(zks);
    SpoutConfig spoutConf = new SpoutConfig(brokerHosts, topic, zkRoot, id);
    spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
    spoutConf.forceFromStart = true;
    spoutConf.zkServers = Arrays.asList(new String[] {"localhost"});
    spoutConf.zkPort = 2181;
    //spoutConf.bufferSizeBytes = 1024;


    // set topology
    builder.setSpout("kafka-spout", new KafkaSpout(spoutConf), Integer.parseInt(args[3])); 
    builder.setBolt("sentiment-bolt", new SentimentBolt(), Integer.parseInt(args[4])).shuffleGrouping("kafka-spout");
    builder.setBolt("regex-bolt", new RegexBolt(), Integer.parseInt(args[5])).shuffleGrouping("sentiment-bolt");
    builder.setBolt("count-bolt", new CountBolt(), Integer.parseInt(args[6])).fieldsGrouping("regex-bolt", new Fields("countryName"));
    builder.setBolt("report-bolt", new KafkaProducerBolt(args[1]), Integer.parseInt(args[7])).globalGrouping("count-bolt");

    // create the default config object
    Config conf = new Config();

    // set the config in debugging mode
    conf.setDebug(true);

    // if (args != null && args.length > 2) {

    //   // run it in a live cluster

    //   // set the number of workers for running all spout and bolt tasks
    //   conf.setNumWorkers(3);

    //   // create the topology and submit with config
    //   StormSubmitter.submitTopology(args[2], conf, builder.createTopology());

    // } else if (args != null && args.length == 2) {

      // run it in a simulated local cluster

    // set the number of threads to run - similar to setting number of workers in live cluster
    conf.setMaxTaskParallelism(Integer.parseInt(args[2]));

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
    // }
  }
}
