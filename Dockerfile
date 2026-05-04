FROM eclipse-temurin:17-jdk-jammy

RUN apt-get update && \
    apt-get install -y python3 python3-pip python3-venv && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

RUN python3 -m venv .venv && \
    .venv/bin/pip install --no-cache-dir youtube-transcript-api requests

COPY src/main/resources/scripts/ src/main/resources/scripts/

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
