# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build with Spring Boot plugin
RUN mvn clean package -DskipTests -B

# DEBUGGING: Show all files in target
RUN echo "=== TODOS LOS ARCHIVOS EN TARGET ===" && ls -la target/

# DEBUGGING: Show JAR size
RUN echo "=== TAMAÑO DEL JAR ===" && du -h target/app.jar

# DEBUGGING: Verify JAR contains Javalin
RUN echo "=== VERIFICANDO JAVALIN EN JAR ===" && jar -tf target/app.jar | grep -i javalin || echo "JAVALIN NO ENCONTRADO!"

# DEBUGGING: Show first 100 entries in JAR
RUN echo "=== PRIMERAS 100 ENTRADAS DEL JAR ===" && jar -tf target/app.jar | head -100

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the correct JAR
COPY --from=build /app/target/app.jar app.jar

# Verify JAR exists and show size
RUN ls -la app.jar && echo "JAR copiado correctamente"

# Test if JAR is executable
RUN java -jar app.jar --help 2>/dev/null || echo "JAR no es ejecutable directamente"

EXPOSE 7000

# Run the application
CMD ["java", "-jar", "app.jar"]