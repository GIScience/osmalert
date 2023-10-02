#!/usr/bin/env bash

docker build -t osmalert/flink .


docker run  -e PORT=7777  -p 8081:7777 osmalert/flink

