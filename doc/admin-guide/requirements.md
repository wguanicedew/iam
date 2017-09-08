## Requirements

#### Docker

In order to run the service as Docker container, you will need:

- Docker v. 1.11.1 or greater

If you want to use docker-compose to deploy the service, you will also need

- docker-compose v.1.7.0 or greater

#### MariaDB/MySQL

The IAM service stores information in a mariadb/mysql database.

#### NginX

The IAM service is designed to run as a backend Java application behind an
NGINX reverse proxy (it could run equally well behing apache, but we tested it
behind NGINX).

### Deployment Tips
In headless servers, running `haveged` daemon is recommended to generate more
entropy.
Before run IAM Login service, check the available entropy with:

```console
$ cat /proc/sys/kernel/random/entropy_avail
```

If the obtained value is less than 1000, then `haveged` daemon is mandatory.

On CentOS only, add EPEL repository:
```console
$ sudo yum install -y epel-release
```
 Then, install Haveged:
```console
$ sudo yum install -y haveged
```
or in Ubuntu:
```console
$ sudo apt-get install -y haveged
```

Enable and run it:
```console
$ sudo systemctl enable haveged
$ sudo systemctl start haveged
```
