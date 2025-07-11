# Multi-stage build optimizado para Railway
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Establecer directorio de trabajo
WORKDIR /app

# Copiar solo pom.xml primero (para cache de dependencias)
COPY pom.xml .

# Descargar dependencias (se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar con assembly plugin para generar JAR con dependencias
RUN mvn clean package -DskipTests -B

# Verificar que el JAR se generó correctamente
RUN ls -la target/ && echo "Verificando contenido del JAR:" && jar -tf target/app.jar | head -20

# Imagen final más liviana
FROM eclipse-temurin:21-jre

# Crear usuario no-root para seguridad
RUN addgroup --system --gid 1001 appgroup && \
    adduser --system --uid 1001 --gid 1001 appuser

WORKDIR /app

# Copiar JAR con dependencias
COPY --from=build /app/target/app.jar app.jar

# Cambiar propietario del archivo
RUN chown appuser:appgroup app.jar

# Cambiar a usuario no-root
USER appuser

# Exponer puerto (Railway asigna automáticamente)
EXPOSE 7000

# Comando para ejecutar la aplicación
CMD ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]