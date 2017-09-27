### IAM docker image

The IAM service is provided on the following DockerHub repositories:

- indigoiam/iam-login-service
- indigodatacloud/iam-login-service

We keep the images in sync, so the following instructions apply to images
fetched from any of the two repositories.

### Configuration and run

Prepare an environment file, with the configuration options to inject into the
Docker container. See [here](configuration.md) for all variables description and
an example of environment file.

The IAM service is run starting the docker container with the following command:

```console
$ docker run --name iam-login-service \
  --net=iam -p 8080:8080 \
  --env-file=/path/to/iam-login-service/env \
   -v /path/to/keystore.jks:/keystore.jks:ro \
  indigodatacloud/iam-login-service
```

Check the output with:
```console
$ docker logs -f iam-login-service
```
