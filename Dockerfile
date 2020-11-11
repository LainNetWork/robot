FROM openjdk:11.0.9-jdk
USER lain
COPY target/*.jar /app/robot.jar
COPY target/deviceInfo.json /app/deviceInfo.json
ENTRYPOINT ["java","-jar","/app/robot.jar","--spring.profiles.active=prod"]