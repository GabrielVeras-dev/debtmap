# ── Stage 1: Build ──────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B 2>/dev/null || true

COPY src ./src

RUN mvn package -DskipTests -B --no-transfer-progress

RUN java -Djarmode=layertools \
         -jar target/*.jar extract \
         --destination target/extracted

# ── Stage 2: Runtime ────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime

RUN addgroup -S debtmap && adduser -S debtmap -G debtmap
USER debtmap

WORKDIR /app

COPY --from=builder --chown=debtmap:debtmap /build/target/extracted/dependencies/ ./
COPY --from=builder --chown=debtmap:debtmap /build/target/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=debtmap:debtmap /build/target/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=debtmap:debtmap /build/target/extracted/application/ ./

EXPOSE 8080

ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "org.springframework.boot.loader.launch.JarLauncher"]