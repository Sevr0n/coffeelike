# Stage 1: Build jar
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY src ./src

RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Minimal JRE (ручной набор модулей)
FROM eclipse-temurin:21-jdk-alpine AS jre-build
WORKDIR /jre

RUN $JAVA_HOME/bin/jlink \
    --add-modules java.base,java.logging,java.sql,java.naming,java.desktop,jdk.unsupported,java.management,java.security.jgss,java.instrument,java.xml \
    --strip-java-debug-attributes \
    --no-man-pages \
    --no-header-files \
    --strip-debug \
    --compress=2 \
    --output /jre-minimal

# Stage 3: Final runtime
FROM alpine:3.20 AS final
WORKDIR /app
RUN apk add --no-cache ca-certificates

COPY --from=jre-build /jre-minimal /jre
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["/jre/bin/java", "-jar", "app.jar"]
