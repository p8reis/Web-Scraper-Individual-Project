FROM eclipse-temurin

# installs geckodriver
RUN apt-get update                             \
 && apt-get install -y --no-install-recommends firefox \
 && rm -fr /var/lib/apt/lists/*                \
 && curl -L https://github.com/mozilla/geckodriver/releases/download/v0.32.0/geckodriver-v0.32.0-linux-aarch64.tar.gz | tar xz -C /tmp/ \
 && apt-get purge -y ca-certificates curl

COPY target/WebScraping-1.jar /tmp/
RUN mkdir -p /tmp/DetectionsOutput

ENTRYPOINT ["java","-jar","/tmp/WebScraping-1.jar"]