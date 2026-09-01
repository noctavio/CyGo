CyGo
0. Ensure you are running Java17 as that is what the project supports. Download the binaries
from `https://adoptium.net/temurin/releases/?version=17`

This project also requires MySQL workbench and services to run locally, the file `application.properties` 
exposes credentials for this reason. Unless I setup a remote db this is the status quo

Download it somewhere like the following "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.1+1"
### KEEP IN MIND THIS IS NEEDS TO BE RUN ON `EVERY TERMINAL INSTANCE`
- Run the following 
 > export JAVA_HOME="/c/Program Files/Eclipse Adoptium/jdk-17.0.20.1+1"
 > export PATH="$JAVA_HOME/bin:$PATH"
 > java -version 
 ensure the version states -> openjdk version `17.x.x` 

1. Command Line (Make sure that you are in the folder containing `pom.xml`) 
### This assumes you are using GitBash otherwise you need to install maven on your machine
- Run the following
 > cd Backend/SpringBoot/springboot
 > ./mvnw clean package
 > cd target
 > java -jar springboot-backend-0.0.1-SNAPSHOT.jar

2. IDE : Right click on Application.java and run as Java Application