#!/bin/bash

mvn clean package

ECHO "### running taxonomy fix"

java -jar target/dp-zebedee-cli-0.0.1-SNAPSHOT.jar -taxonomyfix /Users/dave/Desktop/zebedee-data/content/zebedee/master /Users/dave/Desktop/zebedee-data/content/zebedee/collections

ECHO "### taxonomy fix complete"