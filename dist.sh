#!/usr/bin/env bash
grunt --gruntfile ui/Gruntfile.js
mvn clean package -Pdist -DskipTests