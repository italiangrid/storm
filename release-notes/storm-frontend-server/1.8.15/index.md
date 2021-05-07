---
layout: default
title: "StoRM Frontend v.1.8.15 release notes"
release_date: "07.05.2021"
rfcs:
  - id: STOR-1398
    title: Ensure MariaDB is started before StoRM Frontend on boot
---

## StoRM Frontend v.1.8.15

Released on **{{ page.release_date }}** with [StoRM v. 1.11.21][release-notes].
<span style="float: right; margin-left: 8px;" class="label label-info">centos7</span>

### Description

This release fixes the boot order ensuring that mariadb service is started before StoRM Frontend.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

Upgrade package and restart service as follow:

```
yum update storm-frontend-server
systemctl restart storm-frontend-server
```

Read more at:
* [StoRM Puppet module forge page][stormpuppetmodule];
* [StoRM Puppet module generated documentation][stormpuppetmoduledoc] forge page;
* the [Quick deploy on CentOS7][quickdeploy] guide.

[stormpuppetmodule]: https://forge.puppet.com/cnafsd/storm
[stormpuppetmoduledoc]: https://italiangrid.github.io/storm-puppet-module/
[quickdeploy]: {{site.baseurl}}/documentation/documentation/sysadmin-guide/1.11.21/quick-deployments/centos7/index.html

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.21.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.21
