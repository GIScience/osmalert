
# extend the offical flink docker image from here: https://github.com/apache/flink-docker/tree/master/1.17/scala_2.12-java11-ubuntu
# FROM --platform=linux/amd64 flink:1.17-scala_2.12-java11

FROM  flink:1.17-scala_2.12-java11

#replace standard script with customized version
COPY dokku-flink/docker-entrypoint-osmalert.sh /docker-entrypoint.sh

CMD /docker-entrypoint.sh
