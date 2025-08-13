# ========== Stage 1: Build jar ==========
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY src ./src

# Собираем слойный jar
RUN ./gradlew bootJar --no-daemon
RUN java -Djarmode=layertools -jar build/libs/*.jar extract

# ========== Stage 2: Create custom JRE ==========
FROM eclipse-temurin:21-jdk AS jre-build
RUN $JAVA_HOME/bin/jlink \
    --add-modules java.base,java.logging,java.sql,java.naming,java.desktop,jdk.unsupported,java.management,java.security.jgss,java.instrument,java.xml \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /jre-minimal


# Stage 3: Final runtime
FROM gcr.io/distroless/cc AS final
WORKDIR /app
COPY --from=jre-build /jre-minimal /jre
# Копируем слои по отдельности
COPY --from=build /app/dependencies/ ./
COPY --from=build /app/spring-boot-loader/ ./
COPY --from=build /app/snapshot-dependencies/ ./
COPY --from=build /app/application/ ./

EXPOSE 8081
ENTRYPOINT ["/jre/bin/java", "org.springframework.boot.loader.launch.JarLauncher"]
