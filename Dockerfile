# multistage dockerfile, dont need do local gradlew build
# -------- Stage 1: Build --------
FROM gradle:8.10-jdk17 AS builder
WORKDIR /app

# Kopieer Gradle config & wrapper eerst (voor caching)
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Download dependencies (dit versnelt builds later)
RUN ./gradlew dependencies || return 0

# Kopieer volledige project
COPY . .

# Build de jar (tests overslaan voor snelheid, haal -x test weg als je wel tests wil draaien)
RUN ./gradlew build -x test

# -------- Stage 2: Run --------
FROM openjdk:17-jdk-slim
WORKDIR /app

# Kopieer jar van build stage
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
