#FROM maven:3-jdk-11-slim as baseImage
#
#WORKDIR /opt/confirmation
#COPY pom.xml /opt/confirmation
#RUN mvn dependency:go-offline


FROM paulograbin/confirmationbase as build

WORKDIR /opt/confirmation
COPY pom.xml /opt/confirmation
COPY . .

RUN mvn package -Dmaven.test.skip=true


FROM gcr.io/distroless/java:11 as deploy

LABEL maintainer="Paulo Gräbin <paulograbin@gmail.com>"
LABEL org.label-schema.name="Confirmation backend"
LABEL org.label-schema.description="Confirmation private parts"

EXPOSE 3000

WORKDIR /opt/confirmation/
COPY --from=build /opt/confirmation/target/Confirmation-1.0.jar .
ENTRYPOINT ["java", "-jar", "/opt/confirmation/Confirmation-1.0.jar"]
