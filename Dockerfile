# Сборка gateway
FROM eclipse-temurin:21-jre-jammy AS gateway
WORKDIR /app
COPY shareit-gateway/target/shareit-gateway-0.0.1-SNAPSHOT.jar gateway.jar
ENTRYPOINT ["java", "-jar", "gateway.jar"]

# Сборка server
FROM eclipse-temurin:21-jre-jammy AS server
WORKDIR /app
COPY shareit-server/target/shareit-server-0.0.1-SNAPSHOT.jar server.jar
ENTRYPOINT ["java", "-jar", "server.jar"]