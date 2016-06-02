# -*- coding: utf-8 -*- 

from flask import Flask, render_template, Response, request, jsonify, flash, g, redirect, url_for
import redis
import csv
import os, sys
import subprocess


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
@app.route('/component', methods=['GET', 'POST'])
def component():
    if request.method == 'POST':
        collection = request.form.get('data-collection')
        processing = request.form.get('data-processing')
        visualization = request.form.get('data-visualization')
        persistence = request.form.get('data-persistence')
        
        # data collection
        kafka = KafkaClient("localhost:9092")
        kafka_producer = SimpleProducer(kafka)
        if collection == "user-timeline":
            twitter_account = request.form.get('twitter-account')
            # print twitter_account
        elif collection == "query-by-text":
            query_text = request.form.get('query-text')
            # print query_text
        elif collection == "query-by-location":
            query_location = request.form.get('query-location')
            # print query_location
        elif collection == "favorite-list":
            favorite_list = request.form.get('favorite-list')
            # print favorite_list

        if processing == "sentiment":
            # subprocess.call(['storm', 'jar', 'jar/sentiment.jar', 'yu.storm.SentimentTopology'])
            pass
        elif processing == "tfidf":
            # subprocess.call(['storm', 'jar', 'jar/tfidf.jar', 'yu.storm.TfidfTopology'])
            pass
        elif processing == "word-count":
            pass
        elif processing == "top-n":
            pass

        if visualization == "map":
            return redirect(url_for('map'))
        elif visualization == "line-graph":
            return redirect(url_for('line'))
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

@app.route('/visualization/radar')
def radar():
    return render_template("visualization/radar.html")

@app.route('/visualization/pie')
def pie():
    return render_template("visualization/pie.html")

@app.route('/visualization/word-cloud')
def word_cloud():
    return render_template("visualization/word-cloud.html")


# all data collection send data through twitter topic
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