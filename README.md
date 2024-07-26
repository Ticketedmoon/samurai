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

##### Compose

```
docker compose up -d
docker compose ps
docker compose logs <serviceName>
docker compose down
```

##### Volume Management

```
docker inspect zookeeper | jq '.[].Mounts[] | .Type ,.Destination'
docker inspect kafka | jq '.[].Mounts[] | .Type ,.Destination' # Inspect volume links in container 
docker compose down -v # Remove volumes
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

### Some Thoughts on SQL vs NoSql

Personally I generally consider SQL as a sane default for most applications and important data. NoSQL is very useful in many categories however and can accompany your SQL db in tasks such as:

- storing upvotes/downvotes. This is what Reddit does actually! Important data is stored in PostgreSQL but votes are stored in Cassandra. This is a perfect example actually - very high throughput database when it comes to writing but at a cost of potentially losing some records. Except nobody really cares if your upvote doesn't go through from time to time.

- caching - Redis for instance is great for this. It's most common use case is being a giant Hashmap that can help out if your users ask for the same piece of information. And oh boy is it fast at that. And who cares if you may lose data in case of power loss? It's just cache, you just fetch data from your primary data store in that case.

- very rapidly changing schemas - frankly speaking, changing schema in SQL is a pain. Adding a column isn't a problem nowadays but heavens forbid you try to change column type - in a large enough application this may mean as much as a whole DAY of downtime. There are some ways around that (but they DO come with cons) and programmers often abuse stuff like json columns or add more and more auxilary tables to deal with it. Many noSQL databases on the other hand have a much more lax approach and a common solution is to just put a "version" column so your application knows whether it should apply previous logic as it's still running on an older schema (and transform a row into a newer version after) vs latest one. That being said - you still should make a schema as good as you can for your noSQL database if that's possible and put some thoughts into it. Otherwise you end up with crap as well.

- unusual types of searches - an example is full text search. Imagine that for instance you have a song database and your records are it's lyrics. Now imagine that someone wants to find a song name based just on one line from it (and probably not even perfectly, eg. they might forget a comma or one of the words). Under MySQL normally this would be O(n) LIKE search. Very slow, very expensive to run and results are not even that good. However there are also databases like Elasticsearch made with this exact case in mind. They actually can return "closest" match and they can index actual words/sentences. So rather than wait 3 minutes you will wait about 5 seconds for the results. Similarly there are some databases really good at datetime ranges etc.

- scalability - SQL databases do not really scale automatically. The only thing you can really do easily is master and read replicas but if you actually NEED a lot of writes then you will have to implement complex sharding logic yourself. On the other hand some NoSQL databases essentially go "hey, just give me 10 new servers, I will redistribute data accordingly and then use map-reduce for all queries so they use all the machines!". But here's one important caveat - while what I said above is true a properly maintained SQL server filled with SSDs can take unbelievable loads before you really need to go into multi cluster noSQL solution (not even kidding, NVMe filled PostgreSQL database has IOPS measured in millions).

In reality, most complex applications generally also use more than one database based on your specific requirements (so it's not SQL vs noSQL dispute, it just gives you more options). SQL however does offer very sane and very safe defaults making it imho a very good deal when starting out.

### Testing

```
curl https://localhost:9200 -ku 'admin:XXX'
```
