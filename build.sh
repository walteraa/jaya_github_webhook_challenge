#!/bin/bash

declare -a services=("entrypoint-service" "consumer-service" "query-service")


for serv in "${services[@]}"
do
    pushd "$serv"
    ./gradlew clean build shadowJar -x test
    cp ./Dockerfile ./build/libs/
    docker build -t "$serv":dev ./build/libs/
    popd
done
