FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/car-user-api-1.0.0.jar deploy_ghactions-1.0.0.jar
EXPOSE 8080
CMD ["java", "-jar", "deploy_ghactions-1.0.0.jar"]
