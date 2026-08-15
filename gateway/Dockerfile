FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY target/shareit-gateway-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]