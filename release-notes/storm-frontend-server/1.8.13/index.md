---
layout: default
title: "StoRM Frontend v.1.8.13 release notes"
release_date: "07.08.2020"
features:
  - id: STOR-1170
    title: curl_global_init should be called before spawning threads
  - id: STOR-1090
    title: SystemD support for StoRM Frontend
---

## StoRM Frontend v.1.8.13

Released on **{{ page.release_date }}** with [StoRM v. 1.11.18][release-notes].
<span style="float: right; margin-left: 8px;" class="label label-success">centos6</span>
<span style="float: right; margin-left: 8px;" class="label label-info">centos7</span>

### Description

This release mainly introduces the support to CentOS 7.

### Enhancements

{% include list-features.liquid %}

### Installation and configuration


On RHEL6, update package:

```
yum update storm-frontend-server
```

and run YAIM.

On RHEL7, to install and configure StoRM Frontend you can use StoRM Puppet module as follows:

```bash
puppet module install cnafsd-storm
```

```puppet
class { 'storm::frontend':
  be_xmlrpc_host  => 'backend-host.example.org',
  be_xmlrpc_token => 'NS4kYAZuR65XJCq',
  db_user         => 'storm',
  db_passwd       => 'storm',
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
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.18
