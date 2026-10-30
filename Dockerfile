FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
ARG MODULE
COPY . .
RUN mvn -B clean package -Dmaven.test.skip=true -Dmaven.wagon.http.retryHandler.count=5 -pl ${MODULE} -am

FROM eclipse-temurin:21-jre
WORKDIR /app
ARG MODULE
COPY --from=build /app/${MODULE}/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
