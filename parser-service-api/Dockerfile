FROM eclipse-temurin:21-jre-jammy
LABEL authors="Vyacheslav"
LABEL description="parser-service-api"
WORKDIR /app
COPY ./parser-service-api/target/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

