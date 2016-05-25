package yu.storm.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.tuple.Values;

public abstract class AbstractTopology {

	private static String topologyName;
	private static TopologyBuilder builder;
	private static Config topologyConfig;


	//use template pattern to design a method

}