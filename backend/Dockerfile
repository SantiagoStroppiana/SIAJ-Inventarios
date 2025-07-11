# Usa una imagen oficial con Java 21 y Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copia todo el proyecto
COPY . .

# Compila el proyecto
RUN mvn clean package -DskipTests

# Imagen final
FROM eclipse-temurin:21

WORKDIR /app

COPY --from=build /app/target/backend-1.0-SNAPSHOT.jar app.jar

EXPOSE 7000

CMD ["java", "-jar", "app.jar"]
