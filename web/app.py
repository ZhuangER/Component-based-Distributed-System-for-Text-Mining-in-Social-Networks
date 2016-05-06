from flask import Flask, render_template, Response, request
import redis
import os, sys
lib_path = os.path.abspath(os.path.join('tools'))
sys.path.append(lib_path)

import twitter_api
# import pycountry

app = Flask(__name__)
r = redis.StrictRedis(host='127.0.0.1', port=6379, db=0)

def event_stream():
    pubsub = r.pubsub()
    pubsub.subscribe('WordCountTopology')
    for message in pubsub.listen():
        print message
        # print message['data'].split('DELIMITER')[3]
        yield 'data: %s\n\n' % message['data']


@app.route('/')
@app.route('/map')
def show_map():
  #Basic d3 view = basic.html and app.js
    return render_template("world_map.html")

@app.route('/ca_map')
def ca_map():
	return render_template("ca_map.html")

@app.route('/test')
def test():
    # Receive query data through AJAX
    query = request.args.get('query', "", type=str)
    # Pass the query to the web crawler or API
    if query != "":
        twitter_api.search(query)

    # if api reach its rate limits, move to web crawler
    # send json data to the producer
    #print query
    return render_template("test.html")

@app.route('/stream')
def stream():
    return Response(event_stream(), mimetype="text/event-stream")

if __name__ == '__main__':
    app.run(threaded=True, host='0.0.0.0', debug=True)