ARG VERSION=8u151

FROM openjdk:${VERSION}-jdk

COPY webhook-entrypoint-1.0-all.jar /bin/runner/run.jar
WORKDIR /bin/runner

CMD ["java","-jar","run.jar"]
