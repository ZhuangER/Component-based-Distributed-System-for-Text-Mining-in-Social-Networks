# !/bin/bash
# package all jar with macen and move to the folder web/jar
for i in "tfidf" "sentiment" "topN" "wordcount" "trends"; do
	if [ ! -f jar/$i.jar ];then
		if [ ! -D jar ]; then
			mkdir jar
		fi
		cd ../storm-$i
		sudo mvn clean package
		sudo mv target/yu-storm-hack-0.0.1-SNAPSHOT-jar-with-dependencies.jar ../web/jar/$i.jar
		cd ../web
	fi
done