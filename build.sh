#!/bin/sh

rm *.class;
javac -cp "imgscalr.jar:." FindSimilarApp.java;
rm FindSimilar.jar;
jar -cfm FindSimilar.jar MANIFEST *.class;
cp FindSimilar.jar ~/findsimilar/;
