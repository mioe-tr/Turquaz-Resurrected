# Turquaz Resurrected — API container.
# Build context: repository root.

# --- Stage 1: derleyici --------------------------------------------------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Önce yalnız POM'ları kopyala — Maven bağımlılık cache'ini katmana ayır
COPY pom.xml ./
COPY core/pom.xml core/pom.xml
COPY persistence/pom.xml persistence/pom.xml
COPY api/pom.xml api/pom.xml
COPY desktop-swt/pom.xml desktop-swt/pom.xml

# Bağımlılıkları indir (sources yok, testler atlanır)
RUN mvn -B -pl api -am -DskipTests dependency:go-offline -q || true

# Kaynakları kopyala ve sadece api + zincirini paketle
COPY core/src core/src
COPY persistence/src persistence/src
COPY api/src api/src

RUN mvn -B -pl api -am -DskipTests -Dmaven.test.skip=true package

# --- Stage 2: çalıştırıcı ------------------------------------------------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Sadece Spring Boot jar'ını al, original/jar.original'ı değil
COPY --from=build /workspace/api/target/turquaz-api-*.jar /app/turquaz-api.jar

# Sağlık denetimi için curl
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

EXPOSE 8080

ENV JAVA_OPTS=""
ENV SPRING_PROFILES_ACTIVE=""

HEALTHCHECK --interval=10s --timeout=5s --start-period=30s --retries=10 \
    CMD curl -fsS http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/turquaz-api.jar"]
