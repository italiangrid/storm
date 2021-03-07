---
layout: default
title: StoRM Info Provider v.1.8.2 release notes
release_date: "07.08.2020"
rfcs:
  - id: STOR-1212
    title: Change the way Info Provider checks if Backend is running
  - id: STOR-1213
    title: WebDAV endpoint not published in case the latest logic is used
  - id: STOR-1214
    title: Wrong storage space values are published in case multiple storage area have the same VO
---

## StoRM Info Provider v. 1.8.2

Released on **{{ page.release_date }}** with [StoRM v. 1.11.18][release-notes].
<span style="float: right; margin-left: 8px;" class="label label-success">centos6</span>
<span style="float: right; margin-left: 8px;" class="label label-info">centos7</span>

### Description

This release:

* changes the way Info Provider checks if Backend is running by calling the REST endpoint;
* fixes the list of endpoint published when using STORM\_WEBDAV\_POOL\_LIST YAIM variable;
* fixes wrong storage space values published in case multiple storage area have the same VO.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

On RHEL6, update package:

```
yum update storm-dynamic-info-provider
```

and run YAIM.

On RHEL7, the info provider is installed through StoRM Puppet Backend class. You can tune some info by using the following variables:

```puppet
class { 'storm::backend':
  ...
  info_config_file => '/etc/storm/info-provider/storm-yaim-variables.conf',
  info_sitename => 'This is the name of the site',
  info_storage_default_root => '/storage',
  info_endpoint_quality_level => 2,
  ...
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
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/
[how-to-json-report]: {{site.baseurl}}/documentation/how-to/how-to-publish-json-report/
