
heroku plugins:install java
../gradlew build
heroku deploy:jar build/libs/webapp.jar -a osmalert-web
