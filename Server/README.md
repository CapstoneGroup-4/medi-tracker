# Build

How to build?

Using the following command:

```bash
cd MediTrackerUserServer

```

```mvn

mvn clean install -DMYSQL_PASS=***** -DREDIS_PASS=*****

```

replace ***** with the password of your MySQL and Redis server.
The build will create a jar file in the target folder.

# Run
Using docker-compose:

```bash
docker-compose up --build
```

The server will be running on port 9080.

You can use 
```bash 
docker ps
``` 
to check the status of the container.

# API Documentation
The API documentation is available at the following URL:
http://47.116.123.99:9080/swagger-ui/index.html