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



class TweetTopology
{
  public static void main(String[] args) throws Exception
  {
    // create the topology
    TopologyBuilder builder = new TopologyBuilder();

    /*
     * In order to create the spout, you need to get twitter credentials
     * If you need to use Twitter firehose/Tweet stream for your idea,
     * create a set of credentials by following the instructions at
     *
     * https://dev.twitter.com/discussions/631
     *
     */
    // now create the tweet spout with the credentials
    // credential

    String zks = "localhost:2181";
    String topic = "mytopic";
    String zkRoot = "/storm"; // default zookeeper root configuration for storm
    String id = "word";
         
    BrokerHosts brokerHosts = new ZkHosts(zks);
    SpoutConfig spoutConf = new SpoutConfig(brokerHosts, topic, zkRoot, id);
    spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
    spoutConf.forceFromStart = false;
    spoutConf.zkServers = Arrays.asList(new String[] {"localhost"});
    spoutConf.zkPort = 2181;



    builder.setSpout("kafka-spout", new KafkaSpout(spoutConf), 1); // Kafka我们创建了一个5分区的Topic，这里并行度设置为5
    builder.setSpout("sentiment-bolt", new SentimentBolt(), 10).shuffleGrouping("kafka-spout");
    // attach the tweet spout to the topology - parallelism of 1
    //builder.setSpout("tweet-spout", tweetSpout, 1);

    // attach the parse tweet bolt using shuffle grouping
 /*   builder.setBolt("parse-tweet-bolt", new ParseTweetBolt(), 10).shuffleGrouping("tweet-spout");*/
    builder.setBolt("regex-bolt", new RegexBolt(), 10).shuffleGrouping("sentiment-bolt");
    builder.setBolt("count-bolt", new CountBolt(), 10).fieldsGrouping("regex-bolt", new Fields("countryName"));
    builder.setBolt("report-bolt", new ReportBolt(), 1).globalGrouping("count-bolt");


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
