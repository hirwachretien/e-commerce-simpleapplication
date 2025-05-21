# Use a minimal JDK image
FROM eclipse-temurin:17-jdk as build

WORKDIR /app

# Copy Maven wrapper and pom
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (improves build caching)
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Package stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (optional, for documentation)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]