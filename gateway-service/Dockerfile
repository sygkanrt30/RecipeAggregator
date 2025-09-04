FROM eclipse-temurin:21-jre-jammy
LABEL authors="Vyacheslav"
LABEL description="gateway-service"
WORKDIR /app
COPY ./gateway-service/target/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]