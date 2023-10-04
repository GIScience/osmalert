# Introduction to Heroku

We use Heroku to deploy our web app for OSM Alert. 

## Overview
- platform for easy deploying of web apps
  - "**zero-configuration**" cloud provider (very quick setup compared to other providers)
- each app runs in an own container ("**dyno**")
- supports many different programming languages (Ruby, Node.js, Go, Java, ...)
- supports deploying docker containers
- **highly scalable**
  - ideally suited for small test deployments
  - also works for production web apps with large amounts of users

## Most important Concepts / Tools
- **concept of "deploying"**
  - taking a built app to Heroku which serves the app to users
    - no need to operate own servers
    - focus just on writing the app (no caring about network or uptime)
  - deployment can be triggered manually (Heroku CLI) or automatically (e.g. GitHub actions)
 
  - **Heroku CLI** (command line interface)
    - create and manage dynos
    - can trigger deployments
    - manage add-ons

- many add-ons available for a web app 
  - Managed **Postgres** databases
    - relational SQL databases with high availability
  - Redis **key-value store**
    - in-memory store especially used for caching
  - Cloud **mail service** ("mailertogo")
  -  **Apache Kafka**
  - and more

## Resources (Links)
- https://www.heroku.com/what
- https://devcenter.heroku.com/articles/getting-started-with-java?singlepage=true
- https://devcenter.heroku.com/articles/heroku-cli