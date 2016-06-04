# -*- coding: utf-8 -*- 

from flask import Flask, render_template, Response, request, jsonify, flash, session, redirect, url_for
import redis
import csv
import os, sys
import subprocess
import time



lib_path = os.path.abspath(os.path.join('tools'))
sys.path.append(lib_path)

import twitter_api
from funcThread import FuncThread
# import pycountry


from kafka import KafkaClient, SimpleProducer, SimpleConsumer



app = Flask(__name__)
r = redis.StrictRedis(host='127.0.0.1', port=6379, db=0)

# indicate the output topic of data processing
# is the input topic of data visualization
visualization_topic = None




@app.route('/')
@app.route('/component', methods=['GET', 'POST'])
def component():
    global visualization_topic 
    if request.method == 'POST':
        collection = request.form.get('data-collection')
        processing = request.form.get('data-processing')
        visualization = request.form.get('data-visualization')
        persistence = request.form.get('data-persistence')
        
        def timeline_producer(twitter_account, count):
            kafka = KafkaClient("localhost:9092")
            kafka_producer = SimpleProducer(kafka)
            text_list = twitter_api.user_timeline(twitter_account, count)
            for text in text_list:
                kafka_producer.send_messages("twitter",text)
            return

        def query_text_producer(text, count):
            kafka = KafkaClient("localhost:9092")
            kafka_producer = SimpleProducer(kafka)
            text_list = twitter_api.search(text, count)
            for text in text_list:
                kafka_producer.send_messages("twitter",text)
            return

        def query_location_producer(lat, lng, radius, count):
            kafka = KafkaClient("localhost:9092")
            kafka_producer = SimpleProducer(kafka)
            text_list = twitter_api.area_search(lat, lng, radius, count)
            for text in text_list:
                kafka_producer.send_messages("twitter",text)
            return

        def favorite_list_producer(id, count):
            kafka = KafkaClient("localhost:9092")
            kafka_producer = SimpleProducer(kafka)
            text_list = twitter_api.favorite_list(id, count)
            for text in text_list:
                kafka_producer.send_messages("twitter",text)
            return

        # data collection
        if collection == "realtime":
            # run real-time stream collection
            # kafka_args = ['java', '-cp', 'jar/realtimeTwitterProducer.jar', 'com.producer.TwitterProducer', 'localhost:9092', 'twitter', 'old', 'async']
            # producer_proc = subprocess.Popen(kafka_args, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            # time.sleep(20)
            # producer_proc.kill() 
            pass

        elif collection == "user-timeline":
            twitter_account = request.form.get('twitter-account')
            print twitter_account
            t = FuncThread(target=timeline_producer, args=(twitter_account, 10))
            t.start()
            # join() will lead thread block
            # t.join()
            t.stop()


            # print twitter_account
        elif collection == "query-by-text":
            query_text = request.form.get('query-text')
            t = FuncThread(target=query_text_producer, args=(query_text, 10))
            t.start()
            t.stop()
            # print query_text
        elif collection == "query-by-location":
            # query_location = request.form.get('query-location')
            # t = FuncThread(target=query_location_producer, args=())
            # t.start()
            # t.stop()
            # print query_location
            pass
        elif collection == "favorite-list":
            twitter_account = request.form.get('twitter-account')
            t = FuncThread(target=favorite_list_producer, args=(twitter_account, 10))
            t.start()
            t.stop()
            # print favorite_list
        # run kafka producer thread
        



        if processing == "sentiment":
            # storm_args = ['storm', 'jar', 'jar/sentiment.jar', 'yu.storm.SentimentTopology']
            # storm_proc = subprocess.Popen(storm_args, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            # storm_proc.kill()
            
            visualization_topic  = "sentiment"
            pass
        elif processing == "trends":
            # storm_args = ['storm', 'jar', 'jar/trends.jar', 'yu.storm.TrendsTopology']
            # storm_proc = subprocess.Popen(storm_args, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            # storm_proc.kill()
            visualization_topic  = "trends"
            pass
        elif processing == "word-count":

            visualization_topic  = "word-count"
            pass
        elif processing == "top-n":

            visualization_topic  = "top-n"
            pass
        #print visualization_topic 

        # according to storm topology
        # choose kafka topic
        if visualization == "map":
            return redirect(url_for('map', topic = visualization_topic))
        elif visualization == "line-graph":
            return redirect(url_for('line', topic = visualization_topic))
        elif visualization == "bar-chart":
            return redirect(url_for('bar', topic = visualization_topic))
        elif visualization == "radar-chart":
            return redirect(url_for('radar', topic = visualization_topic))
        elif visualization == "pie-chart":
            return redirect(url_for('pie', topic = visualization_topic))
        elif visualization == "word-cloud":
            return redirect(url_for('word_cloud', topic = visualization_topic))

        if persistence == "user-timeline":
            pass
        elif persistence == "query-by-text":
            pass

    return render_template("component.html")

@app.route('/topology', methods=['GET'])
def topology():
    collection = request.form.get('data-collection')
    processing = request.form.get('data-processing')
    visualization = request.form.get('data-visualization')
    persistence = request.form.get('data-persistence')
    print collection, processing, visualization, persistence
    return str(collection)

@app.route('/visualization/map/<topic>')
def map(topic):
    return render_template("visualization/map.html", topic=topic)

@app.route('/visualization/line/<topic>')
def line(topic):
    return render_template("visualization/line.html", topic=topic)

@app.route('/visualization/bar/<topic>')
def bar(topic):
    return render_template("visualization/bar.html", topic=topic)

@app.route('/visualization/radar/<topic>')
def radar(topic):
    return render_template("visualization/radar.html", topic=topic)

@app.route('/visualization/pie/<topic>')
def pie(topic):
    return render_template("visualization/pie.html", topic=topic)

@app.route('/visualization/word-cloud/<topic>')
def word_cloud(topic):
    return render_template("visualization/word-cloud.html", topic=topic)

@app.route('/_twitter_area_query')
def twitter_area_query():
    lat = request.args.get('lat', "", type=str)
    lng = request.args.get('lng', "", type=str)
    radius = request.args.get('radius', "", type=int)
    print lat, lng, radius
    return jsonify()

# stream function should be called by visualization function
# @app.route('/stream', methods = ['GET'])
# def kafka_stream():
#     global visualization_topic 
#     topic = visualization_topic 
#     print "DEBUG stream topic: " + topic
#     kafka = KafkaClient("localhost:9092")
#     consumer = SimpleConsumer(kafka, "python", topic)
#     topic = None

#     def gen():
#         for message in consumer:
#             yield 'data: %s\n\n' %str(message.message.value)

#     print "DEBUG: Kafka Stream Connected"
#     return Response(gen(), mimetype="text/event-stream")

if __name__ == '__main__':
    app.run(threaded=True, host='0.0.0.0', debug=True)