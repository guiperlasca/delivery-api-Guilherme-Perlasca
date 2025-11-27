# Estágio 1: Build (Compilação)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copia apenas o pom.xml primeiro para cachear dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código fonte e compila
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Runtime (Execução)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Cria um usuário não-root por segurança
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copia o JAR gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta da aplicação
EXPOSE 8080

# Comando para iniciar
ENTRYPOINT ["java", "-jar", "app.jar"]
