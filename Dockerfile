FROM openjdk:11.0.9-jdk
COPY target/*.jar /app/robot.jar
COPY /home/lain/chiken/deviceInfo.json /app/deviceInfo.json
ENTRYPOINT ["java","-jar","/app/robot.jar"]