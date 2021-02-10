# Bearer File Refresher

Writes the access token from Keycloak to a token file at your own interval.
It was created for scraping a protected metrics resource with Prometheus, where you can use a file for the current bearer token.

See [Prometheus config](https://prometheus.io/docs/prometheus/latest/configuration/configuration/#scrape_config).

## Use with Docker

You need to setup a few environment variables.

**Required**
```
KEYCLOAK_REALM=master
KEYCLOAK_CLIENT_ID=client
KEYCLOAK_CLIENT_SECRET=secret
KEYCLOAK_USER_USERNAME=username
KEYCLOAK_USER_PASSWORD=password
```

Optional
```
KEYCLOAK_BASE_PATH=http://localhost:8080
REFRESH_INTERVAL=5m
VERBOSE=false
BEARER_FILE_NAME=bearer-token-file
```
Available options for refresh intervals are `s` seconds, `m` minutes or `h` hours.

The full path of the token file is `/tmp/refreshed/{BEARER_FILE_NAME}` which is `/tmp/refreshed/bearer-token-file` by default.

There is a resource on port `8080` with path `/refresh` you can call with `PUT` to trigger the function at any time.

```
curl --location --request PUT 'http://localhost:8080/refresh' 
```

## Example usage
```
docker run --name token-refresher \
-v /tmp:/tmp/refreshed \
-p 8080:8080 \
-e KEYCLOAK_REALM=master \
-e KEYCLOAK_CLIENT_ID=client \
-e KEYCLOAK_CLIENT_SECRET=secret \
-e KEYCLOAK_USER_USERNAME=username \
-e KEYCLOAK_USER_PASSWORD=password \
-e VERBOSE=true \
-e REFRESH_INTERVAL=4m \
-e KEYCLOAK_BASE_PATH=http://localhost:8080 \
stelzo/bearer-file-refresher
```


## Build your own

It is a basic Maven project, but for Docker Hub it was build into a native executable with GraalVM to minimize the JVM footprint.

You can do the same by
```shell script
./mvnw package -Pnative
```

If you don't have GraalVM installed, append `-Dquarkus.native.container-build=true` to this command to build in a container.
But this will always create a Linux executable.

Easiest solution is still using as a Docker container. To build it natively into a Docker container, use:
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true
```

If you run it without a container, I suggest changing `bearer.file.path` in `src/main/java/de/stelzo/resources/application.properties`.
This is the configuration you overwrite with environment variables when using in a container.

## Technology
The image was build using Quarkus with GraalVM. I know it is overkill and this is basically on the logical level of a basic shell script but I wanted to do something with that framework and GraalVM.

## License
Author: Christopher Sieh <[stelzo@steado.de](mailto:stelzo@steado.de)>

This tool is licensed under the MIT License.
