## Deployment with precompiled packages

Since IAM 1.0.0, precompiled packages are available
to install IAM Login service.

Supported platforms:
 - CentOS 7
 - Ubuntu 16.04

Packages and repo files are hosted on https://repo.cloud.cnaf.infn.it/repository/indigo-iam 
public repository.

#### Add Indigo repository

First add the Indigo IAM repository.

On CentOS:
```console
$ sudo wget -O /etc/yum.repos.d/indigo-iam.repo https://repo.cloud.cnaf.infn.it/repository/indigo-iam/repofiles/rhel/indigoiam-stable-el7.repo
```

On Ubuntu 16.04:
```console
$ sudo wget -O /etc/apt/sources.list.d/indigo-iam.list https://repo.cloud.cnaf.infn.it/repository/indigo-iam/repofiles/ubuntu/indigoiam-stable-xenial.list
```

**WARNING: The following steps are required only on Ubuntu**
The packages are served on HTTPS, on Ubuntu
install the support to fetch on this protocol:

```console
$ sudo apt-get install -y apt-transport-https
```

Then, since the repository is unsigned, skip the GPG check with the
following configuration option:

```console
$ sudo echo 'APT::Get::AllowUnauthenticated yes;' > /etc/apt/apt.conf.d/99auth
```

#### Installation

Refresh the repository cache and install the IAM login service package.

On CentOS:
```console
$ sudo yum makecache
$ sudo yum install -y iam-login-service
```

On Ubuntu:
```console
$ sudo apt-get update -y
$ sudo apt-get install -y iam-login-service
```

#### Configuration

The configuration file contains the environment variables
used by the Login Service for the configuration.
The description of all the variables and the can be found [here](admin.md).

This file is located under different path, according the OS platform.

On Centos
```
/etc/sysconfig/iam-login-service
```

On Ubuntu:
```
/etc/default/iam-login-service
```

#### Run the service

The service is managed by Systemd, so to run it use:
```console
$ sudo systemctl start iam-login-service
```

Check the output from the journal:
```console
$ sudo journalctl -fu iam-login-service
```
