FROM eclipse-temurin:21-jre-jammy
LABEL authors="Vyacheslav"
LABEL description="recipe-service-api"
WORKDIR /app
COPY ./recipe-service-api/target/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]