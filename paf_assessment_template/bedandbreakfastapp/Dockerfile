FROM eclipse-temurin:23-noble AS compiler
# or eclipse-temurin:23-noble
# can be AS builder also

ARG COMPILE_DIR=/code_folder

WORKDIR ${COMPILE_DIR}

COPY src src
COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY .mvn .mvn

RUN chmod a+x ./mvnw && ./mvnw package -Dmaven.test.skip=true

# Stage 2
FROM eclipse-temurin:23-jre-noble

ARG DEPLOY_DIR=/app

WORKDIR ${DEPLOY_DIR}

COPY --from=compiler /code_folder/target/assessment-0.0.1-SNAPSHOT.jar assessment.jar

ENV SERVER_PORT=3000

EXPOSE ${SERVER_PORT}

ENTRYPOINT [ "java", "-jar", "assessment.jar" ]