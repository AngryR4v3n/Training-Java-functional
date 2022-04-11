FROM maven:3.8.5-openjdk-17-slim
COPY ./target/classes /opt/Airport
COPY ./pom.xml /opt/Airport
WORKDIR /opt/Airport
RUN mvn clean install
ENTRYPOINT ["java", "AirportProgram"]
