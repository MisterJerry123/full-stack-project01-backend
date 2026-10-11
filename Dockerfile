FROM eclipse-temurin:21-jre

WORKDIR /app

COPY build/libs/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xms128m", "-Xmx384m", "-jar", "app.jar"]
