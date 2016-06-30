package yu.storm;

import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.tuple.Values;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

import yu.storm.bolt.*;
import yu.storm.spout.KafkaSpoutBuilder;



class LocalTestTopology
{
  private static String[] mimeTypes = new String[] { "application/pdf", "text/html", "text/plain" };

  private static final int DEFAULT_RUNTIME_IN_SECONDS = 60;
  private static final int TOP_N = 20;
  private final String topologyName;
  private static TopologyBuilder builder;
  private static Config topologyConfig;
  private final int runtimeInSeconds;

  public LocalTestTopology(String topologyName,String spoutTopic, String reportTopic) {
    builder = new TopologyBuilder();
    this.topologyName = topologyName;
    topologyConfig = createTopologyConfiguration();
    runtimeInSeconds = DEFAULT_RUNTIME_IN_SECONDS;

    wireTopology(spoutTopic, reportTopic);
  }

  public static Config createTopologyConfiguration() {
    Config conf = new Config();
    conf.setDebug(true);
    return conf;
  }

  private void wireTopology(String spoutTopic, String reportTopic) {
    String spoutId = "kafka-spout";
    String tfidfId = "tfidf-bolt";
    String intermediateRankerId = "intermediateRanker";
    String totalRankerId = "finalRanker";
    String reporterId = "report-bolt";

    Properties configs = new Properties();

    KafkaSpoutBuilder kafkaSpoutBuilder = new KafkaSpoutBuilder(configs);
    KafkaSpout kafkaSpout =  kafkaSpoutBuilder.buildKafkaSpout(spoutTopic);
    
    // set topology
    builder.setSpout(spoutId, kafkaSpout, 1);
    builder.setBolt("document-fetch-bolt", new DocumentFetchBolt(mimeTypes), 10).shuffleGrouping(spoutId);
    /*builder.setBolt("dcount-bolt",new DCountBolt(), 1).globalGrouping("test-spout");*/
    builder.setBolt("tokenize-bolt", new TokenizeBolt(), 10).shuffleGrouping("document-fetch-bolt");
    builder.setBolt("filter-bolt", new TermFilterBolt(), 10).shuffleGrouping("tokenize-bolt");
    builder.setBolt("dfcount-bolt", new DfCountBolt(), 5).fieldsGrouping("filter-bolt", new Fields("term"));
    builder.setBolt("tfcount-bolt", new TfCountBolt(), 5).fieldsGrouping("filter-bolt", new Fields("term", "documentId"));
    builder.setBolt(tfidfId, new TfidfBolt(), 1)/*.globalGrouping("dcount-bolt")*/
                                                    .globalGrouping("dfcount-bolt")
                                                    .globalGrouping("tfcount-bolt");
    //builder.setBolt("topn-bolt", new TopNBolt(), 1).globalGrouping("tfidf-bolt");
    builder.setBolt(intermediateRankerId, new IntermediateRankingsBolt(TOP_N), 4).fieldsGrouping(tfidfId, new Fields("term"));
    builder.setBolt(totalRankerId, new TotalRankingsBolt(TOP_N)).globalGrouping(intermediateRankerId);
    builder.setBolt(reporterId, new KafkaProducerBolt(reportTopic), 1).globalGrouping(totalRankerId);


  }


  public static void main(String[] args) throws Exception
  {
    String topologyName = "tfidf-topology";
    LocalTestTopology tfidf = new LocalTestTopology(topologyName, args[0], args[1]);


    // if (args != null && args.length > 2) {

    //   // run it in a live cluster

    //   // set the number of workers for running all spout and bolt tasks
    //   topologyConfig.setNumWorkers(3);

    //   // create the topology and submit with config
    //   StormSubmitter.submitTopology(args[2], topologyConfig, builder.createTopology());

    // } else if (args != null && args.length == 2) {

      // run it in a simulated local cluster

      // set the number of threads to run - similar to setting number of workers in live cluster
    topologyConfig.setMaxTaskParallelism(Integer.parseInt(args[2]));

    // create the local cluster instance
    LocalCluster cluster = new LocalCluster();

    // submit the topology to the local cluster
    cluster.submitTopology("tweet-word-count", topologyConfig, builder.createTopology());

    // let the topology run for 300 seconds. note topologies never terminate!
    Utils.sleep(300000000);

    // now kill the topology
    cluster.killTopology("tweet-word-count");

    // we are done, so shutdown the local cluster
    cluster.shutdown();
    // }
  }
}
