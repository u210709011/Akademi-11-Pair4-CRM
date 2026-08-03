# Build context is back-end/ (this module depends on the reactor's shared modules).
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace
COPY . .
RUN --mount=type=cache,target=/root/.m2,id=maven-repo \
    ./mvnw -pl customer-service -am -DskipTests package -B

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/customer-service/target/customer-service-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
