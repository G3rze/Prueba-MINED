# Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/prueba-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9988
ENTRYPOINT ["java","-jar","app.jar"]
