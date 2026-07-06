# ---------- Etapa 1: compilar el jar con Maven ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw
# Descarga las dependencias primero para aprovechar la cache de Docker
RUN ./mvnw -q -B dependency:go-offline || true
COPY src ./src
RUN ./mvnw -q -B -DskipTests package

# ---------- Etapa 2: imagen final, solo el JRE + el jar ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
