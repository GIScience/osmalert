

heroku container:login
heroku container:push web -a osmalert-flink-docker
heroku container:release web -a osmalert-flink-docker
