from flask import Flask, render_template, Response
import redis
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
    return render_template("test.html")

@app.route('/stream')
def stream():
    return Response(event_stream(), mimetype="text/event-stream")

if __name__ == '__main__':
    app.run(threaded=True, host='0.0.0.0', debug=True)