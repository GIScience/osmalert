s3d-client create osmalert-flink
s3d-dokku builder-dockerfile:set osmalert-flink dockerfile-path heroku-flink/Dockerfile
s3d-dokku resource:limit osmalert-flink --memory 4gb

# Set all environment variables:
# _todo_