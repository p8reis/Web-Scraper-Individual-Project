FROM eclipse-temurin

COPY target/WebScraping-1.jar /tmp/

RUN mkdir -p /tmp/DetectionsOutput

ENTRYPOINT ["java","-jar","/tmp/WebScraping-1.jar"]