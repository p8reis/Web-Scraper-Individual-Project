FROM eclipse-temurin

COPY WebScraping-1.jar /tmp/

RUN mkdir -p /tmp/DetectionsOutput

EXPOSE 8080
ENTRYPOINT ["java","-jar","/tmp/WebScraping-1.jar"]