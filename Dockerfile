# Multi-stage Build: Estágio 1 - Compilação
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2 - Imagem Final Executável
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Atualiza os pacotes do sistema operacional Linux Alpine para aplicar patches de segurança recentes
RUN apk update && apk upgrade --no-cache

# Cria um usuário não-root por questões de segurança (Princípio de Menor Privilégio)
RUN addgroup -S sentinelgroup && adduser -S sentineluser -G sentinelgroup
USER sentineluser

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]