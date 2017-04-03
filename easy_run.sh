#!/usr/bin/env bash



if [ ! -d "trec-car" ]; then
  git clone https://github.com/tuckerowens/trec-car.git
fi

if [ ! -d "trec-car-eval" ]; then
  git clone https://github.com/tuckerowens/trec-car-eval.git
fi

cd trec-car-eval/trec-eval/
mvn package
cd ../..
cd trec-car/
mvn package assembly:single
cd ..

if [ ! -d "index" ]; then
  mkdir index
  java -jar trec-car/target/trec-car-1.0-SNAPSHOT-jar-with-dependencies.jar --index index $1
fi



java -jar trec-car/target/trec-car-1.0-SNAPSHOT-jar-with-dependencies.jar --baseline index $2 > runfile


