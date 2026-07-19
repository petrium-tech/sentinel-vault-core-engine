# =========================================================================
# ESTÁGIO 1: COMPILAÇÃO (BUILD ENGINE)
# =========================================================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# =========================================================================
# ESTÁGIO 2: EXECUÇÃO (RUNTIME BLINDADO)
# =========================================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# DevSecOps: Usuário de sistema não-privilegiado (Least Privilege)
RUN addgroup -S sentinelgroup && adduser -S sentineluser -G sentinelgroup
USER sentineluser

COPY --from=builder /build/target/sentinel-vault-*.jar app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]