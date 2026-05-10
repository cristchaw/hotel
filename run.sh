#!/bin/bash

cd src

javac -cp "../lib/*:." */*.java *.java

java -cp ".:../lib/*" Main