import time, sys
from kafka import KafkaClient, SimpleProducer, SimpleConsumer
import subprocess

timeout = time.time() + 60
# run stream in background
def producer(geo=None):
	if geo != None:
		producer_thread = subprocess.Popen(["python","tools/producer.py", "realtime", "geo"], stdout=subprocess.PIPE)
	else:
		producer_thread = subprocess.Popen(["python","tools/producer.py", "realtime"], stdout=subprocess.PIPE)
	return producer_thread.pid
# run kafka consumer
def consumer(seconds):
	time.sleep(5)
	cnt = 0 
	kafka = KafkaClient("localhost:9092")
	consumer = SimpleConsumer(kafka, "test", "twitter")
	start_time = time.time()
	for msg in consumer:
		if time.time() >= start_time + seconds:
			return cnt
		cnt += 1

	return cnt

def connection_time():
	cnt = 0
	kafka = KafkaClient("localhost:9092")
	consumer = SimpleConsumer(kafka, "test", "twitter")
	start_time = time.time()
	for msg in consumer:
		cnt += 1
		if cnt > 0:
			return time.time() - start_time

if __name__ == "__main__":
	pid = producer()
	print consumer(int(sys.argv[1]))
	subprocess.call(['kill', str(pid)])

	

	# pid = producer()
	# print connection_time()
	# subprocess.call(['kill', str(pid)])
