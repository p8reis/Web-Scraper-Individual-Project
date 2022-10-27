FROM eclipse-temurin

RUN apt-get update \
 && apt-get upgrade -y \
 && apt-get install -y --no-install-recommends --no-install-suggests \
            ca-certificates \
 && update-ca-certificates \
    \
 # Install tools for building
 && toolDeps=" \
        curl bzip2 \
    " \
 && apt-get install -y --no-install-recommends --no-install-suggests \
            $toolDeps \
    \
 # Install dependencies for Firefox
 && apt-get install -y --no-install-recommends --no-install-suggests \
            `apt-cache depends firefox-esr | awk '/Depends:/{print$2}'` \
            # additional 'firefox-esl' dependencies which is not in 'depends' list
            libasound2 libxt6 libxtst6 \
    \
 # Download and install Firefox
 && curl -fL -o /tmp/firefox.tar.bz2 \
         https://ftp.mozilla.org/pub/firefox/releases/106.0.2/linux-x86_64/en-GB/firefox-106.0.2.tar.bz2 \
 && tar -xjf /tmp/firefox.tar.bz2 -C /tmp/ \
    && mv /tmp/firefox /opt/firefox \
    \
    && curl -fL -o /tmp/geckodriver.tar.gz \
         https://github.com/mozilla/geckodriver/releases/download/v0.32.0/geckodriver-v0.32.0-linux-aarch64.tar.gz \
    && tar -xzf /tmp/geckodriver.tar.gz -C /tmp/ \
    && chmod +x /tmp/geckodriver

ENV MOZ_HEADLESS=1

EXPOSE 4444

CMD ["--binary=/opt/firefox/firefox", "--log=debug"]


COPY target/WebScraping-1.jar /tmp/

RUN mkdir -p /tmp/DetectionsOutput

ENTRYPOINT ["java","-jar","/tmp/WebScraping-1.jar"]