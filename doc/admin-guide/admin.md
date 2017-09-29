# Deployment and Administration guide

This is the INDIGO IAM deployment and administration guide.

IAM Login Service can be deployed in two different ways:
 - as a Docker container (see [here](docker.md));
 - as systemd daemon from precompiled packages (see [here](packages.md)).

Also a Puppet module is provided to simplify the installation
and setup. This module leveraging on the precompiled packages.
See [here](puppet.md) and the [GitHub repository](https://github.com/indigo-iam/puppet-indigo-iam)
for more usage information.

### Summary

  * [Administration](admin.md)
  * [Configuration parameters](configuration.md)
  * [Deploy with Docker](docker.md)
  * [Deploy from packages](packages.md)
  * [Automate with Puppet](puppet.md)
  * [Audit log](audit_log.md)
  * [Health checks](health_endpoints.md)
