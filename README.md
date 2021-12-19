# WordFinderApp
## Overview
WordFinderApp is a web-based application that finds all of the English words that can be made from a given list of letters.
## Code
The application consists of three major elements:
- The actual word finding logic is in the `wordfinder` folder. It's written in Java and packaged as a JAR file

- The RESTful service code is in the `wordfinderservice` folder. It's a SpringBoot application that extends `SpringBootServletInitializer`. This allows the code to be packaged as a WAR file and deployed to a web server such as Tomcat

- The web UI is in the `wordfinder-vue` folder. It's a Vue.js 2.x application. After it's built (`npm run build`), the contents of the `dist` folder need to be copied to `wordfinderservice/src/main/resources/static` prior to `wordfinderservice` being packaged into a WAR file
