/*package yu.storm.spout;



class TwitterKafkaSpout{
    String zks = "localhost:2181";
    String topic = "mytopic";
    String zkRoot = "/storm"; // default zookeeper root configuration for storm
    String id = "word";
         
    BrokerHosts brokerHosts = new ZkHosts(zks);
    SpoutConfig spoutConf = new SpoutConfig(brokerHosts, topic, zkRoot, id);
    spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
    spoutConf.forceFromStart = true;
    spoutConf.zkServers = Arrays.asList(new String[] {"localhost"});
    spoutConf.zkPort = 2181;
    //spoutConf.bufferSizeBytes = 1024;

    new KafkaSpout(spoutConf);

}*/