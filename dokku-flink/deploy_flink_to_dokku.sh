# Connect to existing app:
s3d-client connect osmalert-flink

git push dokku

# Deployment hack to work around strange dokku behaviour concerning port mapping:
# 1. port mapping setzen: 80:8081
# 2. restart app
# 3. Port mapping löschen -> Blauer Fehlerscreen
# 4. restart app
s3d-dokku ports:set osmalert-flink http:80:8081
s3d-dokku ps:restart osmalert-flink
s3d-dokku ports:clear osmalert-flink
s3d-dokku ps:restart osmalert-flink
