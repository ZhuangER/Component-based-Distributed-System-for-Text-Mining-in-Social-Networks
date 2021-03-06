package storm.tools;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Properties;

// Simple wrapper to the old scala producer, to make the counting code cleaner
public class KafkaProducer {
    private Properties kafkaProps = new Properties();
    private Producer<String, String> producer;
    private ProducerConfig config;

    private String topic;

    public KafkaProducer(String topic) {
        this.topic = topic;
    }

    public void configure(String brokerList, String sync) {
        kafkaProps.put("metadata.broker.list", brokerList);
        kafkaProps.put("serializer.class", "kafka.serializer.StringEncoder");
        kafkaProps.put("request.required.acks", "1");
        kafkaProps.put("producer.type", sync);
        kafkaProps.put("send.buffer.bytes","550000");
        kafkaProps.put("receive.buffer.bytes","550000");

        config = new ProducerConfig(kafkaProps);
    }

    public void start() {
        producer = new Producer<String, String>(config);
    }

    public void produce(String s) {
        KeyedMessage<String, String> message = new KeyedMessage<String, String>(topic, null, s);
        producer.send(message);
    }

    public void close() {
        producer.close();
    }
}
