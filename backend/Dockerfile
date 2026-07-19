FROM maven:3.9.16-sapmachine-25 AS build
LABEL authors="Michael Terletskyi"

WORKDIR /app

COPY checkstyle ./checkstyle

COPY pom.xml .

COPY security/pom.xml ./security/
COPY identity/pom.xml ./identity/
COPY application/pom.xml ./application/

RUN mvn dependency:go-offline -B

COPY security/src ./security/src
COPY identity/src ./identity/src
COPY application/src ./application/src

RUN mvn clean package -DskipTests

FROM sapmachine:25-jre

WORKDIR /app

COPY --from=build /app/application/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]