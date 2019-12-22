FROM maven:3-jdk-13-alpine AS mvn
WORKDIR /build
COPY . /build
ENV MAVEN_OPTS "-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=WARN -Dorg.slf4j.simpleLogger.showDateTime=true -Djava.awt.headless=true"
ENV MAVEN_CLI_OPTS "--batch-mode --errors --fail-at-end --show-version -DinstallAtEnd=true -DdeployAtEnd=true"
RUN mvn $MAVEN_CLI_OPTS clean package

FROM openjdk:14-jdk-alpine
WORKDIR /app
COPY --from=mvn /build/target/countries-runnable.jar /app/app.jar
ENV _JAVA_OPTIONS "-Xmx32m -Xms32m -XX:+ExitOnOutOfMemoryError"
CMD java -jar /app/app.jar
EXPOSE 8080
