/*package yu.storm.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.tuple.Values;

import yu.storm.spout.KafkaSpoutBuilder;
import yu.storm.bolt.KafkaProducerBolt;

import java.util.Properties;

public abstract class AbstractTopology {

	private static String topologyName;
	private static TopologyBuilder builder;
	private static Config topologyConfig;
	String spoutId = "kafka-spout";
	String reportId = "report-bolt";
	String lastBoltId = "last-bolt";


	//use template pattern to design a method
	private void topologyTemplate() throws InterruptedException{

		Properties configs = new Properties();

    	KafkaSpoutBuilder kafkaSpoutBuilder = new KafkaSpoutBuilder(configs);
    	KafkaSpout kafkaSpout =  kafkaSpoutBuilder.buildKafkaSpout();
    	builder.setSpout(spoutId, kafkaSpout, 1);
    	this.addOtherBolts();
    	builder.setBolt(reportId).globalGrouping(lastBoltId);

	}



	abstract void addOtherBolts() {

	}

	public void runLocally() throws InterruptedException {
    	StormRunner.runTopologyLocally(builder.createTopology(), topologyName, topologyConfig, runtimeInSeconds);
  	}

  	public void runRemotely() throws Exception {
    	StormRunner.runTopologyRemotely(builder.createTopology(), topologyName, topologyConfig);
  	}


}*/