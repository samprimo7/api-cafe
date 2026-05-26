# Dockerfile sencillo en dos etapas:
# 1) build: compila el jar con Maven
# 2) runtime: imagen ligera de Java 21 para ejecutarlo

# ----- Etapa de build -----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B clean package -DskipTests

# ----- Etapa de runtime -----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
