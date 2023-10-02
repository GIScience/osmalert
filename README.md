# OSM Alert

Code for the ISE Practical 2023.

The user website is [here](https://giscience.github.io/osmalert/).

## Local Build

### Requirements

- Java >= 17 installed and runnable in path, e.g. via [SDKMAN](https://sdkman.io/)

[//]: # (- Chrome browser installed and runnable in path)

### Build and Run on the command line

```bash
./gradlew build
java -jar web/build/libs/web.jar
```

[//]: # (The first build can take some time due to Chrome driver installation.)

The website should now be accessible on `http://localhost:8080`

### IntelliJ Configuration

> Settings | Build, Execution, Deployment | Build Tools | Gradle | Build and Run using: `Gradle`

This makes sure that all modules are correctly built and resources processed.

> Settings | Editor | Code Style | ☑️Enable EditorConfig Support

To adapt IntelliJ's formatting rules to `osmalert` guidelines. 
 
## Project Structure

- webapp
- flinkservice
- flinkjobjar

## Heroku Deployment

### Initial setup by project admins

- Heroku cloud mail service add-on was created:
  `heroku addons:create mailertogo:micro`
- Projects were created:
    - `heroku create --stack heroku-22 -a osmalert-web --region eu`.
    - `heroku create -a osmalert-flink-docker --region eu`
- Mail-Addon was created (and attached to `osmalert-flink-docker`):
  `heroku addons:create mailertogo:micro -a osmalert-flink-docker`
- All users were added as collaborator to the projects:
    - `heroku access:add user.name@example.org -a osmalert-web`
    - `heroku access:add user.name@example.org -a osmalert-flink-docker`



### Requirements

- Local installation of Docker
- [Heroku](https://www.heroku.com/) account.
- Local installation of [Heroku CLI](https://devcenter.heroku.com/articles/heroku-cli).


### Manual Deployment

#### Webapp


```bash
cd webapp
heroku login

./deploy_webapp_to_heroku.sh

heroku logs --tail -a osmalert-web
```

Now the web app is accessible at: 
`https://osmalert-web-0773365646a7.herokuapp.com`



#### Docker Container for Flink Job Manager


```bash
cd heroku-flink/job-manager
heroku login

./deploy_flink_to_heroku.sh

heroku logs --tail -a osmalert-flink-docker
```

Now the flink dashboard is accessible at:
https://osmalert-flink-docker-d0c317ac495f.herokuapp.com


### Deployment through GitHub Action

Run action "Deploy App" on https://github.com/???

### Viewing the Logs

```bash
heroku logs -a <projectName> --tail
```

### Restarting the App

```bash
heroku restart -a <projectName>
```


## Static Website

The static website is hosted on [GitHub Pages](https://giscience.github.io/osmalert/)

The source code for it is in the `docs` folder.
