# run cassandra
# enable the environment parameters
. /etc/profile
# run cassandra server in background
cassandra
# run kafka server in background
cd /opt/kafka
./bin/kafka-server-start.sh config/server.properties &

