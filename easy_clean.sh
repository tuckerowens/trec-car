#!/usr/bin/env bash

cd ~/trec-car/trec-car-eval/trec-eval/
mvn clean

cd ~/trec-car/trec-car
mvn clean



rm -rf ~/trec-car/index
rm -rf ~/trec-car/data

rm -rf ~/trec-car/trec-car
rm -rf ~/trec-car/trec-car-eval

