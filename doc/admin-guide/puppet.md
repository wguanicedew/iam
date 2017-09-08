## Automatic setup with Puppet

The Puppet module can be  found [here](https://github.com/indigo-iam/puppet-indigo-iam).

The module provided configures only the IAM Login Service, 
using the precompiled packages.
It also can generate automatically the keystore.

The setup of the database is out of the scope of this module, 
as well as the setup of the reverse proxy.

However, the module provides an example of setup of both the 
Login Service and Nginx as reverse proxy, 
using the official Nginx Puppet module.

For more detailed information about the Indigo IAM Puppet module usage, 
see the documentation in the GitHub repo.