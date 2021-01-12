FROM openjdk:11.0.9-jdk
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
COPY target/*.jar /app/robot.jar
COPY device.json device.json
ENTRYPOINT ["java","-jar","/app/robot.jar","--spring.profiles.active=prod"]