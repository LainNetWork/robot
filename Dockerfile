FROM openjdk:11.0.9-jdk
COPY target/*.jar /app/robot.jar
ENTRYPOINT ["java","-jar","/app/robot.jar","--spring.profiles.active=prod"]