#!/bin/bash

VERSION=0.0.1

cat ~/.pass/github-docker.pac | docker login docker.pkg.github.com -u alterationx10 --password-stdin
sbt "clean; docker:stage"
cd target/docker/stage

docker build -t docker.pkg.github.com/k8ty-app/k8ty-api/${VERSION}:latest .
docker push docker.pkg.github.com/k8ty-app/k8ty-api/${VERSION}:latest
