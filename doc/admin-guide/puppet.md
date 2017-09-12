## Automatic service provisioning with Puppet

The Puppet module can be  found [here](https://github.com/indigo-iam/puppet-indigo-iam).

The module configures the IAM Login Service packages installation,
configuration and the automatic generation of JSON keystore.

The setup of the MySQL database used by the service as well as the setup of the
reverse proxy are *not covered* by this module.

However, the module provides an example of setup of both the
Login Service and Nginx as reverse proxy,
using the official Nginx Puppet module.

For more detailed information about the Indigo IAM Puppet module usage,
see the documentation in the
[GitHub repo](https://github.com/indigo-iam/puppet-indigo-iam).
