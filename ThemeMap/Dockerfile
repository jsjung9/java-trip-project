FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY src ./src
RUN mvn --batch-mode -DskipTests package

FROM eclipse-temurin:17-jre
RUN useradd --system --uid 10001 appuser
WORKDIR /app
COPY --from=build /workspace/target/theme-map-api-1.0.0.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
