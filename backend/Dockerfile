# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the JAR with dependencies (shade plugin output)
COPY --from=build /app/target/app.jar app.jar

EXPOSE 7000

# Run the application
CMD ["java", "-jar", "app.jar"]