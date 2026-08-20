FROM eclipse-temurin:21-jre-jammy
VOLUME /tmp
RUN rm -f /app/target/*.jar || true
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java -jar /app.jar"]