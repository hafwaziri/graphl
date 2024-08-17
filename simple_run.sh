#!/bin/bash 

cd parser
python3 parser.py -o

cd ..

find . -name "*.java" > sources.txt
javac --release 21 -d out -cp ".:lib/jackson/*:lib/junit/*" -Xlint:none @sources.txt
rm sources.txt

java -cp "out:lib/jackson/*:lib/junit/*" Tools.JSONParser -o
