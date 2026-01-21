# multistage dockerfile, dont need do local gradlew build
# -------- Stage 1: Build --------
FROM gradle:8.10-jdk17 AS builder
WORKDIR /app

# Kopieer Gradle config & wrapper eerst (voor caching)
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Download dependencies (dit versnelt builds later)
RUN ./gradlew dependencies --no-daemon || return 0

# Kopieer volledige project
COPY . .

# Build de jar (tests overslaan voor snelheid, haal -x test weg als je wel tests wil draaien)
RUN ./gradlew bootJar --no-daemon -x test

# -------- Stage 2: Run --------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Kopieer jar van build stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Add PostgreSQL client for debugging (optional)
RUN apt-get update && apt-get install -y postgresql-client && rm -rf /var/lib/apt/lists/*

EXPOSE 8080

# Add healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Set default Spring profile to prod
ENV SPRING_PROFILES_ACTIVE=prod

# Run with prod profile and proper JVM settings optimized for Render
ENTRYPOINT ["java", \
    "-Xms128m", \
    "-Xmx512m", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=200", \
    "-XX:+UseStringDeduplication", \
    "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dserver.tomcat.connection-timeout=60000", \
    "-Dspring.datasource.hikari.connection-timeout=45000", \
    "-jar", \
    "app.jar"]
