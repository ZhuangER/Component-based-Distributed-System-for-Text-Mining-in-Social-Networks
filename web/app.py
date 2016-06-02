# -*- coding: utf-8 -*- 

from flask import Flask, render_template, Response, request, jsonify, flash, session, redirect, url_for
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
        
        # data collection
        kafka = KafkaClient("localhost:9092")
        kafka_producer = SimpleProducer(kafka)
        if collection == "realtime":
            # run real-time stream collection
            pass
        elif collection == "user-timeline":
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
            
            visualization_topic  = "sentiment"
            pass
        elif processing == "tfidf":
            # subprocess.call(['storm', 'jar', 'jar/trends.jar', 'yu.storm.TrendsTopology'])
            visualization_topic  = "tfidf"
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