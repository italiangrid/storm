---
layout: service-guide
title: StoRM System Administration Guide - Quick deploy StoRM WebDAV - Centos 7
---

# Quick StoRM WebDAV deployment on CentOS 7 with Puppet 7

Assuming that:

- the host satisfies the [StoRM System Requirements][SYSTEM-REQUIREMENTS]
- the [Getting Started][GETTING-STARTED] sections related to repositories have been followed

we will deploy all the services in 2 steps:

* [Prepare node](#prepare-node)
* [Install services](#services)

## Prepare node

This part of the guide explains how to brief prepare a node to install StoRM WebDAV service.
We are aware that there are a lot of ways to do these things and each site administratos knows what is better for his site.
This guide is ispired from how we test the clean deployment of our service.

### Host certificate and key

Be sure you have your host certificate and key stored at `/etc/grid-security`. Check also if your host certificate is not expired.

```
$ ls /etc/grid-security/hostcert.pem
$ ls /etc/grid-security/hostkey.pem
$ openssl x509 -in /etc/grid-security/hostcert.pem -noout -text
```

### Install Puppet 7

Install Puppet 7 as follow:

```bash
rpm -Uvh https://yum.puppet.com/puppet7-release-el-7.noarch.rpm
rpm -Uvh https://yum.puppet.com/puppet-tools-release-el-7.noarch.rpm
wget http://yum.puppet.com/RPM-GPG-KEY-puppet-20250406
rpm --import RPM-GPG-KEY-puppet-20250406
yum install -y puppet
```

### Install Puppet modules

Install the following puppet modules:

```shell
# Puppet Standard Library
puppet module install puppetlabs-stdlib --version '8.6.0'
puppet module install puppetlabs-apt --version '8.5.0'
# fetch-crl and all CA certificates
puppet module install puppet-fetchcrl --version '5.1.0'
# EPEL repo
puppet module install puppet-epel --version '4.1.0'
puppet module install puppetlabs-firewall --version '5.0.0'
puppet module install saz-sudo --version '14.0.0'
# UMD4 repo
puppet module install cnafsd-umd4
# VOMS VO configuration
puppet module install cnafsd-voms
# StoRM services and utils
puppet module install cnafsd-storm
```

### Setup node

Apply this **setup.pp**:

```puppet
include epel
include umd4
include fetchcrl

# install and configure dteam vo
include voms::dteam

# storage root directories for all the storage areas
# Just for test purpose. In production you should not need this part.
$storage_area_root_directories = [
  '/storage/dteam',
  '/storage/dteam/disk',
  '/storage/dteam/tape',
]
storm::rootdir { '/storage': }
storm::sarootdir { $storage_area_root_directories: }
```

```bash
puppet apply setup.pp
```

## Services

### Example of manifest.pp

The following configuration install and configure StoRM WebDAV service.
A pair of dteam VO storage areas are defined for Backend and WebDAV services. 

Example of **manifest.pp**:

```puppet
# Edit this value for your FQDN hostname
$host='storm-test.example.org'

class { 'storm::webdav':
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


[SYSTEM-REQUIREMENTS]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.22/#system-requirements
[GETTING-STARTED]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.22/#getting-started

[puppet-module-docs]: https://italiangrid.github.io/storm-puppet-module/

[sysadmin-index]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.22/
