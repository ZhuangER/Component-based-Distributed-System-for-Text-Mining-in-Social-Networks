# -*- coding: utf-8 -*- 

from flask import Flask, render_template, Response, request, jsonify, flash, session, redirect, url_for
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

# indicate the output topic of data processing
# is the input topic of data visualization
visualization_topic = None
thread_list = []


# @app.after_request
# def aftr_request(response):
#     print 'after request'
#     print request.url
#     return response

# @app.teardown_request
# def teardown_request(exception):
#     print 'teardown request'
#     print request.url
#     for pid in thread_list:
#         subprocess.call(['kill', str(pid)])


@app.route('/')
@app.route('/component', methods=['GET', 'POST'])
def component():
    global visualization_topic 
    if request.method == 'POST':
        collection = request.form.get('data-collection')
        processing = request.form.get('data-processing')
        visualization = request.form.get('data-visualization')
        persistence = request.form.get('data-persistence')
        
        # data collection
        if collection == "realtime":
            # run real-time stream collection
            # kafka_args = ['java', '-cp', 'jar/realtimeTwitterProducer.jar', 'com.producer.TwitterProducer', 'localhost:9092', 'twitter', 'old', 'async']
            # producer_proc = subprocess.Popen(kafka_args, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            # time.sleep(20)
            # producer_proc.kill() 
            if visualization == "map":
                producer_thread = subprocess.Popen(["python","tools/producer.py", collection, "geo"], stdout=subprocess.PIPE)
            else:
                producer_thread = subprocess.Popen(["python","tools/producer.py", collection], stdout=subprocess.PIPE)
            # producer_thread.kill()
            thread_list.append(producer_thread.pid)
            print thread_list


        elif collection == "user-timeline":
            twitter_account = request.form.get('twitter-account')
            print twitter_account
            producer_thread = subprocess.Popen(["python","tools/producer.py", collection, twitter_account, "100"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            # producer_thread.kill()
            thread_list.append(producer_thread.pid) 


            # print twitter_account
        elif collection == "query-by-text":
            query_text = request.form.get('query-text')
            producer_thread = subprocess.Popen(["python","tools/producer.py", collection, query_text, "100"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            thread_list.append(producer_thread.pid) 
            # print query_text
        elif collection == "query-by-location":
            # query_location = request.form.get('query-location')
            # producer_thread = subprocess.Popen(["python","tools/producer.py", collection, twitter_account, "100"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            # producer_thread.kill()
            # thread_list.append(producer_thread.pid) 
            pass
        elif collection == "favorite-list":
            twitter_account = request.form.get('twitter-account')
            producer_thread = subprocess.Popen(["python","tools/producer.py", collection, twitter_account, "100"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            thread_list.append(producer_thread.pid) 
            # print favorite_list
        # run kafka producer thread
        



        if processing == "sentiment":
            storm_args = ['storm', 'jar', 'jar/sentiment.jar', 'yu.storm.SentimentTopology', 'twitter', 'web']
            storm_proc = subprocess.Popen(storm_args) #, stdout=subprocess.PIPE, stderr=subprocess.PIPE
            thread_list.append(storm_proc.pid)
            # pass
        elif processing == "trends":
            storm_args = ['storm', 'jar', 'jar/trends.jar', 'yu.storm.TrendsTopology', 'twitter', 'web']
            storm_proc = subprocess.Popen(storm_args) #, stdout=subprocess.PIPE, stderr=subprocess.PIPE
            thread_list.append(storm_proc.pid)

            # pass
        elif processing == "word-count":
            storm_args = ['storm', 'jar', 'jar/wordCount.jar', 'yu.storm.WordCountTopology', 'twitter', 'web']
            storm_proc = subprocess.Popen(storm_args) #, stdout=subprocess.PIPE, stderr=subprocess.PIPE
            thread_list.append(storm_proc.pid)

        elif processing == "tfidf":
            storm_args = ['storm', 'jar', 'jar/tfidf.jar', 'yu.storm.TfidfTopology', 'twitter', 'web']
            storm_proc = subprocess.Popen(storm_args) #, stdout=subprocess.PIPE, stderr=subprocess.PIPE
            thread_list.append(storm_proc.pid)
            # pass
        elif processing == "top-n":
            storm_args = ['storm', 'jar', 'jar/topN.jar', 'yu.storm.TopNTweetTopology', 'twitter', 'web']
            storm_proc = subprocess.Popen(storm_args) #, stdout=subprocess.PIPE, stderr=subprocess.PIPE
            thread_list.append(storm_proc.pid)


        if visualization == "map":
            return redirect(url_for('map'))
        elif visualization == "line-graph":
            return redirect(url_for('line'))
        elif visualization == "bar-chart":
            return redirect(url_for('bar'))
        elif visualization == "radar-chart":
            return redirect(url_for('radar'))
        elif visualization == "pie-chart":
            return redirect(url_for('pie'))
        elif visualization == "word-cloud":
            return redirect(url_for('word_cloud'))

        if persistence == "user-timeline":
            pass
        elif persistence == "query-by-text":
            pass

    return render_template("component.html")

# @app.teardown_request
# def teardown_request(exception):
#     print "tear down"
#     for pid in thread_list:
#         subprocess.call(['kill', pid])

@app.route('/clear_threads')
def clear_threads():
    print "DEBUG:"
    while thread_list:
        pid = thread_list.pop()
        print pid
        subprocess.call(['kill', str(pid)])
    return "success clear all threads"

@app.route('/topology', methods=['GET'])
def topology():
    collection = request.form.get('data-collection')
    processing = request.form.get('data-processing')
    visualization = request.form.get('data-visualization')
    persistence = request.form.get('data-persistence')
    print collection, processing, visualization, persistence
    return str(collection)

@app.route('/visualization/map')
def map():

    return render_template("visualization/map.html")

@app.route('/visualization/line')
def line():
    return render_template("visualization/line.html")

@app.route('/visualization/bar')
def bar():
    return render_template("visualization/bar.html")

@app.route('/visualization/radar')
def radar():
    return render_template("visualization/radar.html")

@app.route('/visualization/pie')
def pie():
    return render_template("visualization/pie.html")

@app.route('/visualization/word-cloud/')
def word_cloud():
    return render_template("visualization/word-cloud.html")

@app.route('/_twitter_area_query')
def twitter_area_query():
    lat = request.args.get('lat', "", type=str)
    lng = request.args.get('lng', "", type=str)
    radius = request.args.get('radius', "", type=int)
    print lat, lng, radius
    return jsonify()

# stream function should be called by visualization function
@app.route('/stream', methods = ['GET'])
def kafka_stream():
    # global visualization_topic 
    # topic = visualization_topic 
    # print "DEBUG stream topic: " + topic
    topic = "web"
    kafka = KafkaClient("localhost:9092")
    consumer = SimpleConsumer(kafka, "python", topic)
    consumer.seek(offset=0, whence=2)
    # topic = None

    def gen():
        for message in consumer:
            yield 'data: %s\n\n' %str(message.message.value)

    print "DEBUG: Kafka Stream Connected"
    return Response(gen(), mimetype="text/event-stream")

if __name__ == '__main__':
    app.run(threaded=True, host='0.0.0.0', debug=True)