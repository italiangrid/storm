---
layout: service-guide
title: StoRM System Administration Guide - StoRM user setup
navigation:
  - link: documentation/sysadmin-guide/1.11.19/index.html
    label: Index
  - link: documentation/sysadmin-guide/1.11.19/service-user/index.html
    label: StoRM user setup
---

## StoRM user setup <a name="serviceuser">&nbsp;</a>

The StoRM Frontend, Backend and WebDAV services run by default as user **storm**.
This user is created by StoRM rpms but it's a good practice to initialize it
before components installation.

You can use the following commands to create the StoRM user on the machines
where you are deploying the services:

```shell
useradd -M storm
```

The option ```-M``` means 'without an home directory'.
You could also use specific user and group IDs as follows:

```shell
useradd -M storm -u 1234 -g 1234
```

On CentOS 7 nodes you can use [cnafsd-storm][storm-puppet-module] Puppet module to initialize
the needed users. This can be done by using [```storm::users```][storm-users] class as follow:

Create default storm and edguser:

```puppet
# add storm and edguser users and groups
include storm::users
```

Or customize users and groups:

```puppet
class { 'storm::users':
  # The list of defined users. In this case: storm and edguser have been defined.
  # Refer to puppetlabs/accounts Accounts::User::Hash
  # https://github.com/puppetlabs/puppetlabs-accounts/blob/main/types/user/hash.pp
  users  => {
    'edguser' => {
      'comment' => 'Edguser user',
      'groups'  => [ edguser, storm, ],
      'uid'     => '1200',
      'gid'     => '1200',
      'home'    => '/home/edguser',
    },
    'storm' => {
      'comment' => 'StoRM user',
      'groups'  => [ storm, edguser, ],
      'uid'     => '1000',
      'gid'     => '1000',
      'home'    => '/home/storm',
    },
  },
  # Any other group different from storm and edguser
  # Refer to puppetlabs/accounts Accounts::Group::Hash
  # https://github.com/puppetlabs/puppetlabs-accounts/blob/main/types/group/hash.pp
  groups => { },
}
```

Keep UIDs and GIDs aligned for StoRM users and groups on distributed deployments (i.e. when the services are installed on different machines).


{% include_relative userlimits.md %}
{% include_relative sapermissions.md %}


[storm-puppet-module]: https://forge.puppet.com/cnafsd/storm
[storm-users]: https://italiangrid.github.io/storm-puppet-module/puppet_classes/storm_3A_3Ausers.html