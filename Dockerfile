FROM maven:3-jdk-11-slim as build

VOLUME /root/.m2

WORKDIR /opt/confirmation
COPY pom.xml /opt/confirmation
COPY . .

RUN mvn clean package


FROM sapmachine/stable:13.0.2 as deploy

LABEL maintainer="Paulo Gräbin <paulograbin@gmail.com>"
LABEL org.label-schema.name="Confirmation backend"
LABEL org.label-schema.description="Confirmation private parts"

EXPOSE 3000

ENV confirmation_mysql_connection=jdbc:mysql://db:3306/confirmation?createDatabaseIfNotExist=true
ENV confirmation_mysql_username=root
ENV confirmation_mysql_password=root

WORKDIR /opt/confirmation/
COPY --from=build /opt/confirmation/target/Confirmation-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "/opt/confirmation/Confirmation-0.0.1-SNAPSHOT.jar"]
