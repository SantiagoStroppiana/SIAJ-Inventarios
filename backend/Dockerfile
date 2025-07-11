# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the original JAR (forget about the name change for now)
COPY --from=build /app/target/backend-1.0-SNAPSHOT.jar app.jar

EXPOSE 7000

# Use ENTRYPOINT instead of CMD
ENTRYPOINT ["java", "-jar", "app.jar"]