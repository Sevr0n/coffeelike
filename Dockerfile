# 1. образ с Java 21
FROM eclipse-temurin:21-jdk AS build

# 2. Копируем Gradle wrapper и исходники
WORKDIR /app
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY src ./src

# 3. Собираем jar
RUN ./gradlew bootJar --no-daemon

# 4. Финальный образ
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
