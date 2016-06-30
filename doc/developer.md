# Developer guide

## How to build

The IAM is a Spring Boot Java application.

To build it, you will need:

- Java 8
- Maven

Build command:

  mvn package

## Setting up the docker-based development environment

You will need:

- Docker 1.11.1
- Docker compose >= 1.7

You can start a development/testing environment with the following command:

```bash
  export POM_VERSION=$(sh utils/print-pom-version.sh)
  docker-compose build
  docker-compose up
```

Besides POM_VERSION, the docker-compose.yml file requires that you set some
environment variables for it to run properly, mainly to provide OAuth client
credentials for external authentication mechanisms (Google, Github,...).

## Building Docker production images

Check the following folders:

- [iam-login-service](iam-login-service/docker)
- [iam-test-client](iam-test-client/docker)

## Related projects

This project builds upon the following projects/technologies:

- [Spring Boot][spring-boot]
- [MitreID OpenID-Connect client and server libraries][mitre]

[mitre]: https://github.com/mitreid-connect/OpenID-Connect-Java-Spring-Server
[spring-boot]: http://projects.spring.io/spring-boot/