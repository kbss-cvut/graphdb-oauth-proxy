FROM maven:3-eclipse-temurin-17 as build

WORKDIR /gdb-proxy

COPY pom.xml pom.xml

RUN mvn -B de.qaware.maven:go-offline-maven-plugin:resolve-dependencies

COPY src src

RUN mvn package -B -DskipTests=true

FROM eclipse-temurin:17-jdk-alpine as runtime
COPY --from=build  /gdb-proxy/target/graphdb-oauth-proxy.jar graphdb-oauth-proxy.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/graphdb-oauth-proxy.jar"]
