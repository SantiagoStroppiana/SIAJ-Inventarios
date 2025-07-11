# Etapa de construcción
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -B

# Etapa de ejecución
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto esperado por Railway
EXPOSE 7000

CMD ["java", "-jar", "app.jar"]
