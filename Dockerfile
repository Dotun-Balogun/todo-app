# --- Build stage ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# Cache dependencies separately from source so code-only changes rebuild faster.
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# --- Run stage ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/todo-app.jar app.jar

# Render sets PORT; application-prod.properties reads it via ${PORT:8080}.
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
