package yu.storm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;
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

import yu.storm.bolt.KafkaProducerBolt;

public class WordCountTopology {

     public static class KafkaWordSplitter extends BaseRichBolt {

          // private static final Log LOG = LogFactory.getLog(KafkaWordSplitter.class);
          private static final long serialVersionUID = 886149197481637894L;
          private OutputCollector collector;
         
          @Override
          public void prepare(Map stormConf, TopologyContext context,
                    OutputCollector collector) {
               this.collector = collector;              
          }

          @Override
          public void execute(Tuple input) {
               String line = input.getString(0);
               // LOG.info("RECV[kafka -> splitter] " + line);
               String[] words = line.split("\\s+");
               for(String word : words) {
                    // LOG.info("EMIT[splitter -> counter] " + word);
                    collector.emit(new Values(word));
               }
               // collector.ack(input);
          }

          @Override
          public void declareOutputFields(OutputFieldsDeclarer declarer) {
               declarer.declare(new Fields("word"));         
          }
         
     }
    
     public static class WordCounter extends BaseRichBolt {

          // private static final Log LOG = LogFactory.getLog(WordCounter.class);
          private static final long serialVersionUID = 886149197481637894L;
          private OutputCollector collector;
          private Map<String, Integer> countMap;
         
          @Override
          public void prepare(Map stormConf, TopologyContext context,
                    OutputCollector collector) {
               this.collector = collector;    
               this.countMap = new HashMap<String, Integer>();
          }

          @Override
          public void execute(Tuple input) {
               String word = input.getString(0);
               // LOG.info("RECV[splitter -> counter] " + word + " : " + count);
               if (countMap.get(word) == null) {
                    // not present, add the word with a count of 1
                    countMap.put(word, 1);
               } else {
                    // already there, hence get the count
                    Integer val = countMap.get(word);

                    // increment the count and save it to the map
                    countMap.put(word, ++val);
               }
               collector.emit(new Values(word, countMap.get(word)));
               // collector.ack(input);
               // LOG.info("CHECK statistics map: " + this.counterMap);
          }

          @Override
          public void declareOutputFields(OutputFieldsDeclarer declarer) {
               declarer.declare(new Fields("word", "count"));         
          }
     }
    
     public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException, InterruptedException {
          String zks = "localhost:2181";
          String topic = args[0];
          String zkRoot = "/storm"; // default zookeeper root configuration for storm
          String id = "word";
         
          BrokerHosts brokerHosts = new ZkHosts(zks);
          SpoutConfig spoutConf = new SpoutConfig(brokerHosts, topic, zkRoot, id);
          spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
          spoutConf.forceFromStart = false;
          spoutConf.zkServers = Arrays.asList(new String[] {"localhost"});
          spoutConf.zkPort = 2181;
         
          TopologyBuilder builder = new TopologyBuilder();
          builder.setSpout("kafka-reader", new KafkaSpout(spoutConf), 1); // Kafka我们创建了一个5分区的Topic，这里并行度设置为5
          builder.setBolt("word-splitter", new KafkaWordSplitter(), 10).shuffleGrouping("kafka-reader");
          builder.setBolt("word-counter", new WordCounter(), 10).fieldsGrouping("word-splitter", new Fields("word"));
          builder.setBolt("report-bolt", new KafkaProducerBolt(args[1])).globalGrouping("word-counter");

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