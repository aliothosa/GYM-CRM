# syntax=docker/dockerfile:1

# ============================================================
# Build stage
# ============================================================
FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /workspace

COPY . .

RUN chmod +x mvnw \
    && ./mvnw \
        -pl trainer-workload-service \
        -am \
        clean package \
        -DskipTests \
    && JAR_FILE="$(find trainer-workload-service/target \
        -maxdepth 1 \
        -type f \
        -name '*.jar' \
        ! -name '*.jar.original' \
        ! -name '*-plain.jar' \
        | head -n 1)" \
    && test -n "${JAR_FILE}" \
    && cp "${JAR_FILE}" /workspace/application.jar


# ============================================================
# Runtime stage
# ============================================================
FROM eclipse-temurin:21-jre-jammy

RUN groupadd --system gymcrm \
    && useradd \
        --system \
        --gid gymcrm \
        --create-home \
        --home-dir /app \
        gymcrm

WORKDIR /app

RUN mkdir -p /app/logs \
    && chown -R gymcrm:gymcrm /app

COPY --from=build \
     --chown=gymcrm:gymcrm \
     /workspace/application.jar \
     /app/application.jar

USER gymcrm

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/application.jar"]