FROM maven:3.9-eclipse-temurin-19 AS builder
WORKDIR /app
COPY pom.xml .
COPY src/ src/
RUN mvn clean package -DskipTests

FROM eclipse-temurin:19-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/test-1.0-SNAPSHOT.jar /app/app.jar
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]