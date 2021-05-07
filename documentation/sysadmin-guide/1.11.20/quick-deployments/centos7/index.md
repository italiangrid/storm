---
layout: service-guide
title: StoRM System Administration Guide - Quick deploy - Centos 7
redirect_from:
  /documentation/how-to/basic-storm-standalone-configuration-centos7/1.11.18/
navigation:
  - link: documentation/sysadmin-guide/1.11.20/index.html
    label: Index
  - link: documentation/sysadmin-guide/1.11.20/quick-deployments/index.html
    label: Quick Deployments Instructions
  - link: documentation/sysadmin-guide/1.11.20/quick-deployments/centos7/index.html
    label: CentOS 7
---

# Quick all-in-one deployment on CentOS 7 with Puppet

The simplest example of a StoRM deployment can be done by installing all the components on a single host.

Assuming that:

- the host satisfies the [StoRM Installation Prerequisites][INSTALL-PREREQ]
- the StoRM repositories have been installed (see [repositories section][REPOSETTINGS]).

we will deploy all the services after preparing the node:

* [Prepare node](#node)
* [Install services](#services)

## Prepare node <a name="node">&nbsp;</a>

This part of the guide explains how to brief prepare a node to install all the StoRM services.
We are aware that there are a lot of ways to do these things and each site administratos knows what is better for his site.
This guide is ispired from how we test the clean deployment of our services.

### Host certificate and key

Be sure you have your host certificate and key stored at `/etc/grid-security`. Check also if your host certificate is not expired.

```
$ ls /etc/grid-security/hostcert.pem
$ ls /etc/grid-security/hostkey.pem
$ openssl x509 -in /etc/grid-security/hostcert.pem -noout -text
```

### Install Puppet

Install Puppet as follow:

```bash
rpm -Uvh https://yum.puppetlabs.com/puppet5/el/7/x86_64/puppet5-release-5.0.0-6.el7.noarch.rpm
yum install -y puppet
```

### Install Puppet modules

Install the needed puppet modules:

```bash
# EPEL repo
puppet module install puppet-epel
# UMD4 repo
puppet module install cnafsd-umd4
# NTP service
puppet module install puppetlabs-ntp
# fetch-crl and all CA certificates
puppet module install puppet-fetchcrl
# voms
puppet module install lcgdm-voms
# bdii
puppet module install cnafsd-bdii
# storm services and utils
puppet module install cnafsd-storm
# lcmaps module (only for test purpose)
puppet module install cnafsd-lcmaps
```

### Setup node

Apply this **setup.pp**:

```puppet
include epel
include umd4
include ntp
include fetchcrl

# install and configure dteam vo
include voms::dteam

# add storm and edguser users and groups
include storm::users

# storage root directories for all the storage areas
# Just for test purpose. In production you should not need this part.
$storage_area_root_directories = [
  '/storage/dteam',
  '/storage/dteam/disk',
  '/storage/dteam/tape',
]
storm::rootdir { '/storage': }
storm::sarootdir { $storage_area_root_directories: }

# install all StoRM repositories and enable only stable repo
# install also UMD4 repo and EPEL
class { 'storm::repo':
  enabled      => ['stable'],
}

# This class installs LCMAPS and LCAS and configure them with some default files stored into the module.
# LCMAPS class is used ONLY FOR TEST PURPOSE. In production, configure LCMAPS/LCAS and pool accounts on your own with YAIM.
class { 'lcmaps':
  pools => [{
    'name'     => 'dteam',
    'size'     => 100,
    'vo'       => 'dteam',
    'group'    => 'dteam',
    'groups'   => ['dteam'],
    'gid'      => 9100,
    'base_uid' => 9100,
    'role'     => 'NULL',
  }],
}

# install bdii
class { 'bdii':
  firewall   => false,
  bdiipasswd => 'supersecretpassword', # avoid service reloading at each run of Puppet agent
}

Class['storm::users']
-> Class['storm::repo']
-> Class['lcmaps']
```

```bash
puppet apply setup.pp
```

## Install services <a name="services">&nbsp;</a>

### Example of manifest.pp

The following configuration install and configure StoRM Backend, Frontend, GridFTP and WebDAV services on the same host.
A pair of dteam VO storage areas are defined for Backend and WebDAV services. 

Example of **manifest.pp**:

```puppet
$host='storm-test.example.org'

include storm::db

Class['storm::db']
-> Class['storm::backend']
-> Class['storm::frontend']
-> Class['storm::gridftp']
-> Class['storm::webdav']

class { 'storm::backend':
  db_username           => 'storm',
  db_password           => 'storm',
  gsiftp_pool_members   => [
    {
      'hostname' => $host,
    },
  ],
  hostname              => $host,
  service_du_enabled    => true,
  srm_pool_members      => [
    {
      'hostname' => $host,
    }
  ],
  storage_areas         => [
    {
      'name'          => 'dteam-disk',
      'root_path'     => '/storage/dteam/disk',
      'access_points' => ['/disk'],
      'vos'           => ['dteam'],
      'online_size'   => 50,
    },
    {
      'name'          => 'dteam-tape',
      'root_path'     => '/storage/dteam/tape',
      'access_points' => ['/tape'],
      'vos'           => ['dteam'],
      'online_size'   => 500,
    },
  ],
  transfer_protocols    => ['file', 'gsiftp', 'webdav'],
  xmlrpc_security_token => 'NS4kYAZuR65XJCq',
  webdav_pool_members   => [
    {
      'hostname' => $host,
    },
  ],
}

class { 'storm::frontend':
  be_xmlrpc_host  => $host,
  be_xmlrpc_token => 'NS4kYAZuR65XJCq',
  db_user         => 'storm',
  db_passwd       => 'storm',
}

class { 'storm::gridftp':
  redirect_lcmaps_log => true,
  llgt_log_file       => '/var/log/storm/storm-gridftp-lcmaps.log',
}

class { 'storm::webdav':
  hostnames     => [$host],
  storage_areas => [
    {
      'name'          => 'dteam-disk',
      'root_path'     => '/storage/dteam/disk',
      'access_points' => ['/disk'],
      'vos'           => ['dteam'],
    },
    {
      'name'          => 'dteam-tape',
      'root_path'     => '/storage/dteam/tape',
      'access_points' => ['/tape'],
      'vos'           => ['dteam'],
    },
  ],
}
```

Then apply it as follow:

```bash
puppet apply manifest.pp
```

Go to [StoRM Puppet module site][puppet-module-docs] to read all the configuration values for each StoRM Puppet class.

### Enable GPFS native libs on StoRM Backend

If you're running StoRM Backend on GPFS file system and you need to install the GPFS native libs, enable the installation through the Puppet module as follows:

```puppet
class { 'storm::backend':
  # ...
  install_native_libs_gpfs => true,
  # ...
}
```

### Enable HTTP as transfer protocol for SRM

To enable HTTP as transfer protocol for SRM prepare-to-get and prepare-to-put requests, you must add `webdav` protocol to the list of your *transfer_protocols* and define at least one member for *webdav_pool_members*. You can re-define the default list of transfer protocols by adding your *storm::backend::transfer_protocols* variable and/or you can override this list by adding a specific *transfer_protocols* for each storage area:

```puppet
class { 'storm::backend':
  # ...
  'webdav_pool_members' => [
    {
      'hostname' => 'webdav.test.example',
    },
  ],
  # defines the default list of transfer protocols for each storage area:
  'transfer_protocols'  => ['file', 'gsiftp', 'webdav'], 
  'storage_areas'       => [
    {
      'name'          => 'sa-http-enabled',
      'root_path'     => '/storage/sa-http-enabled',
      'access_points' => ['/sa-http-enabled'],
      'vos'           => ['test.vo'],
      'online_size'   => 40,
    },
    {
      'name'               => 'sa-no-http-enabled',
      'root_path'          => '/storage/sa-no-http-enabled',
      'access_points'      => ['/sa-no-http-enabled'],
      'vos'                => ['test.vo'],
      'online_size'        => 40,
      # disable webdav protocol for this storage area
      'transfer_protocols' => ['file', 'gsiftp'],
    },
    # ...
  ],
  # ...
}
```

The *manifest.pp* showed above includes the HTTP transfer protocol for all the storage area defined.
By default, *storm::backend::transfer_protocols* includes only `file` and `gsiftp`.


### MariaDB server configuration

The installation of MariaDB server is not done by StoRM Backend class. The assumption is that a site administrator prefers to install and tune database as its needed. Anyway, an utility class is provided by StoRM module to install a MariaDB server and add all the necessary grants and users.

Examples of StoRM Database usage:

```puppet
class { 'storm::db':
  root_password => 'supersupersecretword',
  storm_password => 'supersecretword', # same of db_password (Backend) and db_passwd (Frontend)
}
```

The whole list of StoRM Database class parameters can be found [here](https://italiangrid.github.io/storm-puppet-module/puppet_classes/storm_3A_3Adb.html).


[INSTALL-PREREQ]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.19/installation-prerequisites/
[REPOSETTINGS]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.19/repositories/

[puppet-module-docs]: https://italiangrid.github.io/storm-puppet-module/

[sysadmin-index]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.19/
