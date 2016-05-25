package yu.storm.topology;

import java.util.Arrays;
import java.util.Properties;

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



/*public class TokenizeTopology {

	private final String topologyName;
	private static TopologyBuilder builder;
	private static Config topologyConfig;

	public TokenizeTopology(String topologyName) {
	    builder = new TopologyBuilder();
	    this.topologyName = topologyName;
	    topologyConfig = createTopologyConfiguration();

	    wireTopology();
	}

	private void wireTopology() {
	    String spoutId = "kafka-spout";

	    Properties configs = new Properties();

	    KafkaSpoutBuilder kafkaSpoutBuilder = new KafkaSpoutBuilder(configs);
	    KafkaSpout kafkaSpout =  kafkaSpoutBuilder.buildKafkaSpout();
	    
	    // set topology
	    builder.setSpout(spoutId, kafkaSpout, 1);

  	}

}*/