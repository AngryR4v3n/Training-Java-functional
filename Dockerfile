FROM maven:3.8.5-openjdk-17-slim
COPY ./target/FunctionalRequests /opt/Airport
COPY ./pom.xml /opt/Airport
WORKDIR /opt/Airport
RUN mvn install
ENTRYPOINT ["java", "AirportProgram"]
