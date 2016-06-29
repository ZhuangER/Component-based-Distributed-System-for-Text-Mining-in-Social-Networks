import time, sys
from kafka import KafkaClient, SimpleProducer, KafkaConsumer
import subprocess

def consumer(seconds):
	time.sleep(5)
	cnt = 0 
	# kafka = KafkaClient("localhost:9092")
	# consumer = SimpleConsumer(kafka, "12212", "web")
	consumer = KafkaConsumer('web', 
							bootstrap_servers=['localhost:9092'],
							auto_offset_reset='latest')
	start_time = time.time()
	for msg in consumer:
		if time.time() >= start_time + seconds:
			return cnt
		# print cnt
		# print msg
		cnt += 1

	# return cnt

if __name__ == "__main__":
	print consumer(int(60))
	print consumer(int(60))
	print consumer(int(60))
	print consumer(int(60))
	print consumer(int(sys.argv[1]))