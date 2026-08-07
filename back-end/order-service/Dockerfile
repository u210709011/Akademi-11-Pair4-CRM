# Build context is back-end/ (this module depends on the reactor's shared modules).
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace
COPY . .
RUN --mount=type=cache,target=/root/.m2,id=maven-repo \
    sh mvnw -pl order-service -am -DskipTests package -B

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/order-service/target/order-service-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
