FROM eclipse-temurin:21-jre-jammy
LABEL authors="Vyacheslav"
LABEL description="search-service-api"
WORKDIR /app
COPY ./search_service_api/target/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]