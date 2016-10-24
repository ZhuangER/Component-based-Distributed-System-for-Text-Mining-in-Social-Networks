from kafka import KafkaClient, SimpleProducer, SimpleConsumer
import twitter_api
import sys

def realtime_producer(restrict=None):
    twitter_api.stream(restrict)

def timeline_producer(twitter_account, count):
    count = int(count)
    kafka = KafkaClient("localhost:9092")
    kafka_producer = SimpleProducer(kafka)
    text_list = twitter_api.user_timeline(twitter_account, count)
    for text in text_list:
        kafka_producer.send_messages("twitter",text)
    kafka.close()
    return

def query_text_producer(text, count):
    count = int(count)
    kafka = KafkaClient("localhost:9092")
    kafka_producer = SimpleProducer(kafka)
    text_list = twitter_api.search(text, count)
    for text in text_list:
        kafka_producer.send_messages("twitter",text)
    kafka.close()
    return

def query_location_producer(lat, lng, radius, count):
    count = int(count)
    kafka = KafkaClient("localhost:9092")
    kafka_producer = SimpleProducer(kafka)
    text_list = twitter_api.area_search(lat, lng, radius, count)
    for text in text_list:
        kafka_producer.send_messages("twitter",text)
    kafka.close()
    return

def favorite_list_producer(id, count):
    count = int(count)
    kafka = KafkaClient("localhost:9092")
    kafka_producer = SimpleProducer(kafka)
    text_list = twitter_api.favorite_list(id, count)
    for text in text_list:
        kafka_producer.send_messages("twitter",text)
    kafka.close()
    return



if __name__ == '__main__':

    # if set count as 0, the twitter will fetch all results
    import time
    time.sleep(60)
    if sys.argv[1] == "realtime":
        if len(sys.argv) > 2:
            realtime_producer(restrict=sys.argv[2])
        else:
            realtime_producer()
    elif sys.argv[1] == "user-timeline":
        timeline_producer(sys.argv[2], sys.argv[3])
    elif sys.argv[1] == "query-by-text":
        query_text_producer(sys.argv[2], sys.argv[3])
    elif sys.argv[1] == "query-by-location":
        query_location_producer(sys.argv[2], sys.argv[3], sys.argv[4], sys.argv[5])
    elif sys.argv[1] == "favorite_list":
        favorite_list_producer(sys.argv[2], sys.argv[3])

