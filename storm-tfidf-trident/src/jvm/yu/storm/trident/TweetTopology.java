package yu.storm.trident;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import yu.trident.operator.*;
import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.operation.builtin.Count;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

import java.util.Arrays;

public class TweetTopology {

    public static StormTopology buildTopology() {
        TridentTopology topology = new TridentTopology();

        String zks = "localhost:2181";
        String topic = "mytopic";
        String zkRoot = "/storm"; // default zookeeper root configuration for storm
        String id = "word";
             
        BrokerHosts brokerHosts = new ZkHosts(zks);
        SpoutConfig spoutConf = new SpoutConfig(brokerHosts, topic, zkRoot, id);
/*        spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());*/
        spoutConf.forceFromStart = true;
        spoutConf.zkServers = Arrays.asList(new String[] {"localhost"});
        spoutConf.zkPort = 2181;

        KafkaSpout spout = new KafkaSpout(spoutConf);

        Stream inputStream = topology.newStream("twitter", spout);

        /*inputStream.each(new Fields("event"), new DiseaseFilter())
                .each(new Fields("event"), new CityAssignment(), new Fields("city"))
                .each(new Fields("event", "city"), new HourAssignment(), new Fields("hour", "cityDiseaseHour"))
                .groupBy(new Fields("cityDiseaseHour"))
                .persistentAggregate(new OutbreakTrendFactory(), new Count(), new Fields("count")).newValuesStream()
                .each(new Fields("cityDiseaseHour", "count"), new OutbreakDetector(), new Fields("alert"))
                .each(new Fields("alert"), new DispatchAlert(), new Fields());*/
        return topology.build();
    }

    public static void main(String[] args) throws Exception {
        Config conf = new Config();
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("cdc", conf, buildTopology());
        Thread.sleep(200000);
        cluster.shutdown();
    }
}
