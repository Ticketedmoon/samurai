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

Compose

```
docker compose up -d
docker compose ps
docker compose logs <serviceName>
docker compose down
```

Testing

```
curl https://localhost:9200 -ku 'admin:XXX'
```