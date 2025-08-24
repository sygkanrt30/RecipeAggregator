FROM eclipse-temurin:21-jre-jammy
LABEL authors="Vyacheslav"
LABEL description="search-service-api"
WORKDIR /app
COPY ./search-service-api/target/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]