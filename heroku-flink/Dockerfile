
# extend the offical flink docker image from here: https://github.com/apache/flink-docker/tree/master/1.17/scala_2.12-java11-ubuntu
FROM --platform=linux/amd64 flink:1.17-scala_2.12-java11

#for local execution on Apple Silicon processors
#FROM  flink:1.17-scala_2.12-java11

#replace standard script with customized version
COPY docker-entrypoint-osmalert.sh /docker-entrypoint.sh

CMD /docker-entrypoint.sh
