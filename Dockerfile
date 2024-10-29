FROM gradle:8.10.2-jdk21-alpine AS build
USER gradle
WORKDIR /home/gradle/workdir

COPY --chown=gradle:gradle build.gradle .
COPY --chown=gradle:gradle settings.gradle .
COPY --chown=gradle:gradle src src

RUN gradle bootJar

FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp
RUN addgroup app
RUN adduser --no-create-home --ingroup app --disabled-password app
COPY --chown=app:app --from=build /home/gradle/workdir/build/libs/*.jar /app/

WORKDIR /app
USER app
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "spring-boot-readiness.jar"]
