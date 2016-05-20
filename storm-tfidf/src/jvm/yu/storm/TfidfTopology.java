package yu.storm;

import java.util.Arrays;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.tuple.Values;

import storm.trident.testing.FixedBatchSpout;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

import yu.storm.bolt.*;




class TfidfTopology
{
  private static String[] mimeTypes = new String[] { "application/pdf", "text/html", "text/plain" };

  private static final int DEFAULT_RUNTIME_IN_SECONDS = 60;
  private static final int TOP_N = 20;

  public static void main(String[] args) throws Exception
  {

    String spoutId = "twitter-spout";
    String tfidfId = "tfidf-bolt";
    String intermediateRankerId = "intermediateRanker";
    String totalRankerId = "finalRanker";
    String reporterId = "report-bolt";
    // create the topology
    TopologyBuilder builder = new TopologyBuilder();




    // create kafka spout
    String zks = "localhost:2181";
    String topic = "first";
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
    builder.setSpout(spoutId, new KafkaSpout(spoutConf), 1);
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
    builder.setBolt(reporterId, new RankingsReportBolt(), 1).globalGrouping(totalRankerId);

    // set topology
    /*builder.setSpout("kafka-spout", new KafkaSpout(spoutConf), 1); 
    builder.setBolt("document-fetch-bolt", new DocumentFetchBolt(mimeTypes), 10).shuffleGrouping("kafka-spout");*/
    
    /*builder.setSpout("kafka-spout", new KafkaSpout(spoutConf), 1); 
    builder.setBolt("sentiment-bolt", new SentimentBolt(), 10).shuffleGrouping("kafka-spout");
    builder.setBolt("regex-bolt", new RegexBolt(), 10).shuffleGrouping("sentiment-bolt");
    builder.setBolt("count-bolt", new CountBolt(), 10).fieldsGrouping("regex-bolt", new Fields("countryName"));
    builder.setBolt("report-bolt", new ReportBolt(), 1).globalGrouping("count-bolt");*/
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
