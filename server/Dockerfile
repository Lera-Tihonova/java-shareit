FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw
RUN ./mvnw dependency:resolve
COPY src src
RUN ./mvnw clean package -DskipTests
ENTRYPOINT ["sh", "-c", "java -jar target/shareit-server-0.0.1-SNAPSHOT.jar"]