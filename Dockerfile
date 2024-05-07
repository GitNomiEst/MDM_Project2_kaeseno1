FROM openjdk:19-jdk

# Copy Files
WORKDIR /usr/src/app
COPY . .

#Set executable permissions for mvnw
RUN chmod +x mvnw

# Install
RUN ./mvnw -Dmaven.test.skip=true package

# Docker Run Command
EXPOSE 8080
CMD ["java","-jar","/usr/src/app/target/mdm_project2_kaeseno1-0.0.1-SNAPSHOT.jar"]