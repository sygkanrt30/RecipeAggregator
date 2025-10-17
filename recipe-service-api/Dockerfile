FROM eclipse-temurin:21-jre-jammy
LABEL authors="Vyacheslav"
LABEL description="recipe-service"
WORKDIR /app
COPY ./recipe-service-api/target/*.jar /app/app.jar
RUN mkdir -p /app/logs
VOLUME /app/logs
ENTRYPOINT ["java", "-jar", "app.jar"]