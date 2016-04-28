package yu.storm;


package yu.storm;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

public class TweetSpout extends KafkaSpout {



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


}