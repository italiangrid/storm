---
layout: default
title: "StoRM BackEnd v. 1.11.18 release notes"
release_date: "07.08.2020"
rfcs:
  - id: STOR-1176
    title: SrmRm file does not exist should not be logged as ERROR
features:
  - id: STOR-892
    title: Log as ERROR only internal errors
  - id: STOR-932
    title: Make Background DU configurable to run periodically in order to update used space info on db
  - id: STOR-1036
    title: Fix useless verbosity in log
  - id: STOR-1174
    title: Include thread pool and jetty handler metrics reporting in storm-backend-metrics log
  - id: STOR-1175
    title: Understand what is the purpose of the recallBuckets map and whether it can be removed
  - id: STOR-1198
    title: Add Date to Backend's metrics log
  - id: STOR-1216
    title: Include mysql-connector-java into maven dependencies
  - id: STOR-1089
    title: SystemD support for StoRM Backend

---

## StoRM Backend v. 1.11.18

Released on **{{ page.release_date }}** with [StoRM v. 1.11.18][release-notes].

### Description

This release introduces the support for CentOS 7.

It also provides fixes to some outstanding bugs, and introduces several improvements, in particular:

* fixes wrong ERROR log messages when file does not exist on srmRm requests;
* introduces a Background DU Service (disabled by default) that periodically updates the storage space info for non-GPFS storage areas (read more info [here][duservice]);
* adds Date and thread pools metrics in the metrics logged info.


This release fixes a minor issue on the init script.

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

On RHEL6, update package:

```
yum update storm-backend-server
```

and run YAIM.

On RHEL7, to install and configure StoRM Backend you can use StoRM Puppet module as follows:

```bash
puppet module install cnafsd-storm
```

```puppet
class { 'storm::backend':
  hostname                 => 'backend-host.example.org',
  mysql_server_install     => true,
  transfer_protocols       => ['file', 'gsiftp', 'webdav'],
  xmlrpc_security_token    => 'NS4kYAZuR65XJCq',
  db_username              => 'storm',
  db_password              => 'storm',
  service_du_enabled       => true,
  srm_pool_members         => [
    {
      'hostname' => $host,
    }
  ],
  gsiftp_pool_members   => [
    {
      'hostname' => $host,
    },
  ],
  webdav_pool_members   => [
    {
      'hostname' => $host,
    },
  ],
  storage_areas         => [
    {
      'name'          => 'test.vo',
      'root_path'     => '/storage/dteam',
      'access_points' => ['/dteam'],
      'vos'           => ['dteam'],
      'online_size'   => 40,
    },
  ],
}
```

Read more at:
* [StoRM Puppet module forge page][stormpuppetmodule];
* [StoRM Puppet module generated documentation][stormpuppetmoduledoc] forge page;
* [Puppet Configuration][puppetconf] section of [System Administration Guide][storm-sysadmin-guide];
* the [Quick deploy on CentOS7][quickdeploy] guide.

[stormpuppetmodule]: https://forge.puppet.com/cnafsd/storm
[stormpuppetmoduledoc]: https://italiangrid.github.io/storm-puppet-module/
[puppetconf]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.18#puppetconfiguration
[quickdeploy]: {{site.baseurl}}/documentation/how-to/basic-storm-standalone-configuration-centos7/1.11.18/

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.18.html
[upgrading]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.18/#upgrading
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.18
[duservice]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.18#duserviceconfiguration
