FROM openjdk:8-jre-alpine
COPY target/HttpServer-1.0-SNAPSHOT.jar /myapp/
COPY webroot/ /myapp/webroot/
WORKDIR /myapp
RUN sh -c 'touch HttpServer-1.0-SNAPSHOT.jar'
ENTRYPOINT ["java","-jar","HttpServer-1.0-SNAPSHOT.jar"]

