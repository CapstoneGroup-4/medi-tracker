# Use Maven with JDK 11 to build the project in the first stage
FROM maven:3.8.6-jdk-11 AS MAVEN_BUILD

# Copy the pom.xml file and source code into the container
COPY src/ src/
COPY pom.xml ./

# Build and package the code, skipping any tests to speed up the build process
RUN mvn --no-transfer-progress clean package -Dmaven.test.skip=true

# In the second stage, use a JRE to run the application
FROM openjdk:11-jre
ARG CC_SERVER_PORT=9999

# Setup tini for better signal handling
ENV TINI_VERSION v0.19.0
ADD https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini /tini
RUN chmod +x /tini

# Create a user and group for running the application
RUN addgroup --system javauser && useradd -g javauser javauser

# Copy only the built jar file from the first stage
COPY --chown=javauser:javauser --from=MAVEN_BUILD /target/chaincode.jar /chaincode.jar
COPY --chown=javauser:javauser docker/docker-entrypoint.sh /docker-entrypoint.sh

# Set the container's port and the environment variable
ENV PORT $CC_SERVER_PORT
EXPOSE $CC_SERVER_PORT

USER javauser
ENTRYPOINT ["/tini", "--", "/docker-entrypoint.sh"]
