# --- ETAPA 1: Compilación (Cambiado a Java 21) ---
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copiamos el pom y el código fuente para compilar
COPY pom.xml .
COPY src ./src

# Compilamos el jar empaquetado saltándonos los tests
RUN mvn clean package -DskipTests

# --- ETAPA 2: Ejecución en Producción (Cambiado a JRE 21) ---
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Nos traemos el archivo .jar generado en la etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Exponemos el puerto del microservicio
EXPOSE 8081

# Comando para arrancar tu aplicación de Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]