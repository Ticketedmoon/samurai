docker build -t skybreak/samurai/opensearch:2.14.0 .
docker run -i \
  -p 127.0.0.1:9200:9200 \
  -p 127.0.0.1:9600:9600 \
  -e "discovery.type=single-node"  \
  -e "OPENSEARCH_INITIAL_ADMIN_PASSWORD=XXX" \
  skybreak/samurai/opensearch:2.14.0