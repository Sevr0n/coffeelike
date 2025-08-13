# Stage 1: Build jar
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY src ./src

RUN ./gradlew bootJar --no-daemon -x test
RUN jar xf build/libs/*.jar

RUN jdeps --ignore-missing-deps \
    --recursive \
    --multi-release 21 \
    --print-module-deps \
    --class-path 'BOOT-INF/lib/*' \
    build/libs/*.jar > deps.info

# Stage 2: Minimal JRE
FROM eclipse-temurin:21-jdk-alpine AS jre-build
WORKDIR /jre

COPY --from=build /app/deps.info .

RUN $JAVA_HOME/bin/jlink \
    --add-modules $(cat deps.info) \
    --strip-java-debug-attributes \
    --no-man-pages \
    --no-header-files \
    --strip-debug \
    --compress=zip-9 \
    --output /jre-minimal

# Stage 3: Final runtime
FROM alpine:3.20 AS final
WORKDIR /app
RUN apk add --no-cache ca-certificates

COPY --from=jre-build /jre-minimal /jre
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["/jre/bin/java", "-jar", "app.jar"]
