# social-network-analysi-with-lambda-architecture
Social network analysis with Lambda Architecture


# Components
- Apache Kafka
- Apache Cassandra
- Apache Storm
- Redis
- Flask
- D3.js
- Mapbox.js
- Leaflet.js


# File Structure
- kafka-twitter-producer
	collect twitter data and provide data to the Storm

- web
	contains data visualization part

- Storm-code
	Use Storm to process data

# Some Useful Configuration

## Kafka
in file **server.properties**  
log.retention.hours and log.retention.bytes



# Website functions
- Search university's location
- search university with its name (unicode support)
## TODO
- add slider to modify the size of search circle
- add leaflet Time-Slider 
- use geocode to fast search surrounded buildings
- distributed web crawler with hadoop


# More general targets:
Design a draggable distributed system


# Package tool
Apache Maven

# Components
- data source component
- data extract component 
- NLP component
- visualization component

# Connection tools
- redis
- kafka
- pubnub


documentFetche: url -> document
tokenize: document -> words
tfidf: words -> score
sentiment: sentence/document -> sentiment



# Reference
