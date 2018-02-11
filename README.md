# Blockchain
Blockchain Test Network

##Build from source:

````
./mvnw install dockerfile:build
````
## Docker repository:
````
docker pull gosho/blockchain
````

## Play with docker-compose:
Spawn whatever number of nodes  you like, by simple editing the docker-compose.yml file.
Nodes can be added with or without port mapping. Nodes without exposed ports can be discovered with the build in peers/discovery.  
````
docker-compose -d up
docker-compose down
````


#Description
Description of the API can be found under **http://host:port/swagger-ui.html** after the application is started.
Where host is typically you 'localhost' and the port is 8080 if is not mapped to something else.

Communication between nodes is achieved with websockets. Only the miner communicate with nodes by REST requests.