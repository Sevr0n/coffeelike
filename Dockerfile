# ========== Stage 1: Build jar ==========
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY src ./src

RUN ./gradlew bootJar --no-daemon

# ========== Stage 2: Create custom JRE ==========
FROM eclipse-temurin:21-jdk-alpine AS jdk-build
WORKDIR /jre

# Собираем JRE с модулями для Spring Boot + Tomcat
RUN $JAVA_HOME/bin/jlink \
    --add-modules java.base,java.logging,java.sql,java.naming,java.desktop,jdk.unsupported,java.management,java.security.jgss,java.instrument,java.xml,java.net.http \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /jre-minimal


# Stage 3: Final runtime
FROM alpine:3.20 AS final
WORKDIR /app
COPY --from=jdk-build /jre-minimal /jre
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["/jre/bin/java", "-jar", "app.jar"]