FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /workspace

COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew

COPY src ./src
RUN ./gradlew clean bootJar --no-daemon \
    && cp build/libs/community-*.jar /workspace/app.jar

FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S -g 1000 tunelog && adduser -S -D -u 1000 -G tunelog tunelog \
    && mkdir -p /app/uploads \
    && chown -R tunelog:tunelog /app

WORKDIR /app

COPY --from=build --chown=tunelog:tunelog /workspace/app.jar app.jar

USER tunelog

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
