# download zookeeper
sudo wget http://apache.mirror.gtcomm.net/zookeeper/zookeeper-3.4.6/zookeeper-3.4.6.tar.gz
sudo tar -zxvf zookeeper-3.4.6.tar.gz
rm zookeeper-3.4.6.tar.gz

sudo mkdir /opt/zookeeper
sudo mv zookeeper-3.4.6 /opt/zookeeper
cd /opt/zookeeper/zookeeper-3.4.6
cp conf/zoo_sample.cfg conf/zoo.cfg
# confige zoo.cfg file
#tickTime=2000
#initLimit=10
#syncLimit=5
#dataDir=/opt/zookeeper/zkdata
#dataLogDir=/opt/zookeeper/logs
#clientPort=2181
#server.1=stormnode1:2888:3888
#server.2=stormnode2:2888:3888
#server.3=stormnode3:2888:3888

sudo mkdir /opt/zookeeper/zkdata
sudo mkdir /opt/zookeeper/logs

sudo su
echo "1" > /opt/zookeeper/zkdata/myid
# echo "2" and "3" to the different node 

# modify /etc/profile
# add to the end of file
#export ZOOKEEPER_HOME=/opt/zookeeper/zookeeper-3.4.6
#export PATH=$ZOOKEEPER_HOME/bin:$PATH

#effect the configure PATH
. /etc/profile

#start zookeeper service
zkServer.sh start

#check zookeeper status
zkServer.sh status
# NOTICE HERE before running zookeeper please make sure java has been installed. command sudo apt-get update -y sudo apt-get -y install default-jdk 
# if any problems occur, use ps -ef | grep -i zookeeper jps to check or zkServer.sh start-foreground
