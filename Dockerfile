FROM openjdk:11.0.9-jdk
COPY target/*.jar /app/robot.jar
WORKDIR /home/lain/
COPY chiken/deviceInfo.json /app/deviceInfo.json
ENTRYPOINT ["java","-jar","/app/robot.jar","--spring.profiles.active=prod"]