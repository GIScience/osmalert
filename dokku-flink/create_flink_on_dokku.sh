s3d-client create osmalert-flink
s3d-dokku builder-dockerfile:set osmalert-flink dockerfile-path dokku-flink/Dockerfile
s3d-dokku resource:limit osmalert-flink --memory 4gb

# Set all environment variables:
 s3d-client config:set osmalert-flink KAFKA_BROKER="pkc-75m1o.europe-west3.gcp.confluent.cloud:9092"
 s3d-client config:set osmalert-flink KAFKA_USER=JD2DE7HRPIWILPPV
 s3d-client config:set osmalert-flink KAFKA_PASSWORD=<kafka user password>
 s3d-client config:set osmalert-flink KAFKA_TOPIC=int.ohsome.now.contributions.enriched.withchangesets
 s3d-client config:set osmalert-flink MAILERTOGO_SMTP_HOST=smtp.web.de
 s3d-client config:set osmalert-flink MAILERTOGO_SMTP_USER=osmalert@web.de
 s3d-client config:set osmalert-flink MAILERTOGO_SMTP_PASSWORD=<web.de user password>
 s3d-client config:set osmalert-flink MAILERTOGO_SMTP_PORT=587
 s3d-client config:set osmalert-flink MAILERTOGO_URL="smtp://7ead94725baf40d45113d83f7c957a5e:e5804977201699617be0485c549eecc17a9ef8a7c3936a1a687ff1937a34c7be@:587?authentication=plain"