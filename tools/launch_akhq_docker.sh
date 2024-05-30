docker pull tchiotludo/akhq:0.25.0

docker run -d \
    -p 8090:8080 \
    -v ./application.yml:/app/application.yml \
    tchiotludo/akhq:0.25.0