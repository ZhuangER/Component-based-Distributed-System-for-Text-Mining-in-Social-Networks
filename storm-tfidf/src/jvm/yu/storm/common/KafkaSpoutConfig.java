package yu.storm.common;

public class KafkaSpoutConfig {

	public static final String ZKS = "localhost:2181";
    public static final String TOPIC = "twitter";
    public static final String ZKROOT = "/storm";
    public static final String ID = "word";
    public static final boolean FORCEFROMSTART = false;
    public static final String CONSUMERGROUP = "kafka.consumer.group";
}
	