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
fi

temp_run=`mktemp`

java -jar trec-car/target/trec-car-1.0-SNAPSHOT-jar-with-dependencies.jar --index data $1
java -jar trec-car/target/trec-car-1.0-SNAPSHOT-jar-with-dependencies.jar --baseline data $2 > $temp_run

java -jar trec-car-eval/trec-eval/target/trec-eval-1.0-SNAPSHOT.jar $temp_run $3
