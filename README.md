#Development Instructions
When updating the application it's simplest to run the Spring Boot application `wordfinderservice` and the
Vue application `wordfinder-vue` separately.

##wordfinderservice Local Execution
In `pom.xml` comment out the `<scope>provided</scope>` for `spring-boot-starter-tomcat`
Run the application. The server will listen on port 5000.

##wordfinder-vue Local Execution
In `WordFinderService.js` uncomment the `baseUrl` set to `localhost:5000` and comment out the one set to `'.'`

#Deployment Instructions

## wordfinderservice Deployment Prep

- In `pom.xml` ensure the scope of `spring-boot-starter-tomcat` is set to `provided`

## wordfinder-vue Deployment Prep

- In `WordFinderService.js` ensure the `baseUrl` is set to `'.'`

## Create the WAR File
- Build `wordfinder` by running `mvn clean install`
- Build the Vue application by running `npm run build`
- From a shell in the `wordfinder-vue` directory run the script `copyForDeploy.sh`. The script copies the contents of the `dist` folder to the `resources/static` folder under `wordfinderservice`
- Build `wordfinderservice` by running `mvn clean install`. The WAR file is created in the `target` folder