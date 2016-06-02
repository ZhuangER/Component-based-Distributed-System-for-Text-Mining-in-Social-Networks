from kafka import KafkaClient, SimpleProducer, KafkaConsumer
import threading


class Consumer(threading.Thread):
    daemon = True

    def run(self):
        consumer = KafkaConsumer(bootstrap_servers='localhost:9092')
        # consumer.unsubscribe()
        consumer.subscribe(['tfidf'])

        for message in consumer:
            yield (message)
