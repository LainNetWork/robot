FROM openjdk:11.0.9-jdk
CMD ["cp","/usr/share/zoneinfo/Asia","/etc/localtime"]
COPY target/*.jar /app/robot.jar
ENTRYPOINT ["java","-jar","/app/robot.jar","--spring.profiles.active=prod"]