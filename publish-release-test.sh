docker stop /blade-cli-publish-release-test 
docker rm /blade-cli-publish-release-test

docker build -t blade-cli-publish-release-test-image . && \
docker run -m 4g -d -p 	2222:22 -p 8081:8081 --name blade-cli-publish-release-test blade-cli-publish-release-test-image

until $(curl --output /dev/null --silent --head --fail http://localhost:8081/nexus/); do
  printf '.'
  sleep 5
done

docker exec -it -w /tmp/build blade-cli-publish-release-test git clone https://github.com/liferay/liferay-blade-cli.git && \
docker exec -it -w /tmp/build/liferay-blade-cli blade-cli-publish-release-test ./gradlew -PlocalNexus :extensions:maven-profile:publish && \
docker exec -it -w /tmp/build/liferay-blade-cli blade-cli-publish-release-test ./gradlew -PlocalNexus --refresh-dependencies --continue clean check :cli:smokeTests --scan && \
docker exec -it -w /tmp/build/liferay-blade-cli blade-cli-publish-release-test ./gradlew -PlocalNexus --refresh-dependencies :cli:publish