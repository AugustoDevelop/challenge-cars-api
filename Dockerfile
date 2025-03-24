FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests

# Estágio de execução
FROM amazoncorretto:17-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar /app/application.jar

# Configurações JVM
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
EXPOSE 8080

ENTRYPOINT exec java $JAVA_OPTS -jar /app/application.jar
