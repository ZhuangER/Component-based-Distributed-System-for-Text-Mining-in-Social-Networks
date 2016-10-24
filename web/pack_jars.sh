# !/bin/bash
# package all jar with macen and move to the folder web/jar
for i in "tfidf" "sentiment" "topN" "wordcount" "trends"; do
	if [ ! -f jar/$i.jar ];then
		cd ../storm-$i
		sudo mvn clean package
		mv target/yu-storm-hack-0.0.1-SNAPSHOT-jar-with-dependencies.jar ../web/jar/$i.jar
		cd ../web
	fi
done