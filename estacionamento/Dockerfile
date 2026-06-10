FROM maven:3.9-eclipse-temurin-11 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests -q

FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=build /app/target/estacionamento-1.0-SNAPSHOT.jar app.jar
EXPOSE 7000
ENTRYPOINT ["java", "-jar", "app.jar"]
