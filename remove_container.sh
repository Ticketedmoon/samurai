docker image prune -a
docker container stop $(docker container list | grep "skybreak/samurai/opensearch" | awk '{print $1}')
docker rm $(docker container list | grep "skybreak/samurai/opensearch" | awk '{print $1}')
docker rmi --force $(docker images | grep "skybreak/samurai/opensearch" | awk '{print $3}')
docker ps