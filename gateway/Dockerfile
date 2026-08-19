FROM eclipse-temurin:21-jre-jammy
VOLUME /tmp
ARG JAR_FILE=target/shareit-gateway-postgres.jar
COPY ${JAR_FILE} app.jar
RUN echo "Force rebuild $(date)" > /dev/null
ENTRYPOINT ["sh", "-c", "java -jar /app.jar"]