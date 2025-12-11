############################################
# Stage 1: Builder
############################################
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Set working directory
WORKDIR /app

# Copy dependency descriptors first (cache optimization)
COPY pom.xml .
RUN mvn -q dependency:resolve dependency:resolve-plugins

# Copy remaining project files
COPY src/ ./src/

# Build the jar (skip tests to speed up)
RUN mvn -q clean package -DskipTests


############################################
# Stage 2: Runtime
############################################
FROM eclipse-temurin:21-jre-alpine

# Set timezone to UTC (required)
ENV TZ=UTC

RUN apk update && apk add --no-cache \
    python3 \
    py3-pip \
    tzdata \
    curl \
    bash \
    busybox-suid \
    cronie && \
    ln -sf python3 /usr/bin/python && \
    rm -rf /var/cache/apk/*

# Install Python requests library
RUN pip3 install --break-system-packages requests




# Set UTC timezone explicitly
RUN ln -sf /usr/share/zoneinfo/UTC /etc/localtime && echo "UTC" > /etc/timezone

# Create app directories
RUN mkdir -p /data \
    && mkdir -p /cron \
    && mkdir -p /cron-output

# Copy application jar from builder stage
COPY --from=builder /app/target/*.jar /app/app.jar

# Copy cron script and cron config
COPY docker/run_2fa_cron.sh /cron/run_2fa_cron.sh
COPY docker/cron/2fa-cron /etc/crontabs/root

# Ensure exec permissions
RUN chmod +x /cron/run_2fa_cron.sh && \
    chmod 0644 /etc/crontabs/root && \
    chmod -R 755 /data /cron /cron-output

# Expose application port
EXPOSE 8080

############################################
# Start cron + Spring Boot
############################################
CMD ["sh", "-c", "crond && java -jar /app/app.jar"]

# Copy cron job
COPY docker/cron/2fa-cron /etc/cron.d/2fa-cron
RUN chmod 0644 /etc/cron.d/2fa-cron && crontab /etc/cron.d/2fa-cron

# Copy cron script
COPY docker/scripts /app/scripts
RUN chmod +x /app/scripts/log_2fa_cron.py
