# --- ETAPA 1: Compilación (Cambiado a Java 21) ---
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copiamos el pom y el código fuente para compilar
COPY pom.xml .
COPY src ./src

# Compilamos el jar empaquetado saltándonos los tests
RUN mvn clean package -DskipTests

# --- ETAPA 2: Ejecución en Producción ---
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# 🎯 Forzamos a copiar el JAR que NO es el .original o especificamos su patrón exacto:
COPY --from=builder /app/target/auth_service-0.0.1-SNAPSHOT.jar app.jar

# Si tu archivo se llama diferente (míralo en tu pom.xml el <artifactId> y <version>), 
# puedes usar esta alternativa que descarta el .original:
# COPY --from=builder /app/target/*[^o][^r][^i][^g][^i][^n][^a][^l].jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]