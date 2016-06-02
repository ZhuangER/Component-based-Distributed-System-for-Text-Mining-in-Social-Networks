# -*- coding: utf-8 -*- 

from flask import Flask, render_template, Response, request, jsonify, flash, g
import redis
import csv
import os, sys


lib_path = os.path.abspath(os.path.join('tools'))
sys.path.append(lib_path)

import twitter_api
# import pycountry


from kafka import KafkaClient, SimpleProducer, SimpleConsumer



app = Flask(__name__)
r = redis.StrictRedis(host='127.0.0.1', port=6379, db=0)


@app.route('/stream', methods = ['GET'])
def kafka_stream():
    topic = request.args.get('topic')
    print topic
    kafka = KafkaClient("localhost:9092")
    consumer = SimpleConsumer(kafka, "python", topic)
    topic = None

    def gen():
        for message in consumer:
            yield 'data: %s\n\n' %str(message.message.value)

    print "DEBUG: Kafka Stream Connected"
    return Response(gen(), mimetype="text/event-stream")


@app.route('/')
@app.route('/component', methods=['GET'])
def component():
    return render_template("component.html")


@app.route('/word-cloud')
def word_cloud():
    return render_template("word-cloud.html")



@app.route('/_twitter_query')
def twitter_query():
    kafka = KafkaClient("localhost:9092")
    kafka_producer = SimpleProducer(kafka)
    query_text = request.args.get('query', "", type=str)
    # Pass the query to the web crawler or API
    
    if twitter_query != "":
        # twitter_api.search(twitter_query)
        basic_info = twitter_api.screen_name_search(query_text)
        if basic_info == {}:
            return jsonify(message = "There is no twitter account available!")
        text_list = twitter_api.user_timeline(basic_info["screen_name"])
        for text in text_list:
            #print text
            kafka_producer.send_messages("twitter",text)
    # if api reach its rate limits, move to web crawler
    return jsonify(message = "")

@app.route('/_twitter_area_query')
def twitter_area_query():
    lat = request.args.get('lat', "", type=str)
    lng = request.args.get('lng', "", type=str)
    radius = request.args.get('radius', "", type=int)
    print lat, lng, radius
    return jsonify()


if __name__ == '__main__':
    app.run(threaded=True, host='0.0.0.0', debug=True)