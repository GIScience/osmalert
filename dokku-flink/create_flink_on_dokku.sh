s3d-client create osmalert-flink
s3d-dokku builder-dockerfile:set osmalert-flink dockerfile-path dokku-flink/Dockerfile
s3d-dokku resource:limit osmalert-flink --memory 4gb
s3d-dokku nginx:set osmalert-flink client-max-body-size 0
s3d-dokku ports:set osmalert-flink http:80:8081 # Only suffices when https is disabled

# Set all environment variables:
 s3d-client config:set osmalert-flink KAFKA_BROKER="pkc-75m1o.europe-west3.gcp.confluent.cloud:9092"
 s3d-client config:set osmalert-flink KAFKA_USER=JD2DE7HRPIWILPPV
 s3d-client config:set osmalert-flink KAFKA_PASSWORD=<kafka user password>
 s3d-client config:set osmalert-flink KAFKA_TOPIC=int.ohsome.now.contributions.enriched.withchangesets
 s3d-client config:set osmalert-flink MAILERTOGO_SMTP_HOST=smtp.web.de
 s3d-client config:set osmalert-flink MAILERTOGO_SMTP_USER=osmalert@web.de
 s3d-client config:set osmalert-flink MAILERTOGO_SMTP_PASSWORD=<web.de user password>
 s3d-client config:set osmalert-flink MAILERTOGO_SMTP_PORT=587