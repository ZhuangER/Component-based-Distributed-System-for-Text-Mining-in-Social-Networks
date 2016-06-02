package yu.storm.spout;

import backtype.storm.spout.SchemeAsMultiScheme;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

import java.util.Arrays;
import java.util.Properties;

import yu.storm.common.KafkaSpoutConfig;

public class KafkaSpoutBuilder{

    public Properties configs = null;

    public KafkaSpoutBuilder(Properties configs) {
        this.configs = configs;
    }

    public KafkaSpout buildKafkaSpout() {
        String topic = KafkaSpoutConfig.TOPIC;
        String zkRoot = KafkaSpoutConfig.ZKROOT; // default zookeeper root configuration for storm
        String id = KafkaSpoutConfig.ID;
        return this.buildKafkaSpout(topic, zkRoot, id);
    }

    public KafkaSpout buildKafkaSpout(String topic) {
        String zkRoot = KafkaSpoutConfig.ZKROOT; // default zookeeper root configuration for storm
        String id = KafkaSpoutConfig.ID;
        
        return this.buildKafkaSpout(topic, zkRoot, id);
    }
    
    public KafkaSpout buildKafkaSpout(String topic, String zkRoot, String id) {
        BrokerHosts brokerHosts = new ZkHosts(KafkaSpoutConfig.ZKS);
        String groupId = KafkaSpoutConfig.CONSUMERGROUP;
        SpoutConfig spoutConf = new SpoutConfig(brokerHosts, topic, zkRoot, id);
        spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
        spoutConf.forceFromStart = false;
        spoutConf.zkServers = Arrays.asList(new String[] {"localhost"});
        spoutConf.zkPort = 2181;
        //spoutConf.bufferSizeBytes = 1024;
        KafkaSpout kafkaSpout = new KafkaSpout(spoutConf);
        return kafkaSpout;
    }

}