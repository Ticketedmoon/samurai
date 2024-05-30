# samurai
A search catalog for games with personalisable features

### Technologies
 - Java
 - SpringBoot
 - Apache Kafka
 - Apache Solr (OpenSearch)
 - Kubernetes
 - AWS

### Execution Guide

#### Docker

```
docker build -t skybreak/samurai/opensearch:2.14.0
```

````
docker run -d -p 9200:9200 -p 9600:9600 -e "discovery.type=single-node" -e "OPENSEARCH_INITIAL_ADMIN_PASSWORD=XXX" opensearchproject/opensearch:latest

docker run -i -p 127.0.0.1:9200:9200 -p 127.0.0.1:9600:9600 -e "discovery.type=single-node" -e "OPENSEARCH_INITIAL_ADMIN_PASSWORD=XXX" skybreak/samurai/opensearch:2.14.0

docker run -i -p 127.0.0.1:9200:9200 -p 127.0.0.1:9600:9600 -e "discovery.type=single-node" skybreak/samurai/opensearch:2.14.0
````

```
docker run \
-p 9200:9200 -p 9600:9600 \
-e "discovery.type=single-node" \
-v /path/to/custom-opensearch.yml:/usr/share/opensearch/config/opensearch.yml \
opensearchproject/opensearch:latest
```

By reviewing each part of the command, you can see that it:

- Maps ports 9200 and 9600 (HOST_PORT:CONTAINER_PORT).
- Sets discovery.type to single-node so that bootstrap checks don’t fail for this single-node deployment.
- Uses the -v flag to pass a local file called custom-opensearch.yml to the container, replacing the opensearch.yml file
  included with the image.
- Requests the opensearchproject/opensearch:latest image from Docker Hub.
- Runs the container.

### Compose

```
docker compose up -d
docker compose ps
docker compose logs <serviceName>
docker compose down
```

### Kafka

In our Springboot application, Kafka topics can be created and configured via application config.

The spring boot application will automatically create Kafka topics on the specified Kafka broker when it is launched.

To get the topic configuration details on the server, run this command:

```
kafka-topics.sh --bootstrap-server localhost:9092 --topic <topic_name> --describe
```

You should see output similar to the following:

![kafka topic details](https://media.geeksforgeeks.org/wp-content/uploads/20221122145422/Screenshotfrom202211221356322.png)

### Testing

```
curl https://localhost:9200 -ku 'admin:XXX'
```