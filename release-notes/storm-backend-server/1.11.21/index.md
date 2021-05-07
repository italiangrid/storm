---
layout: default
title: "StoRM Backend v. 1.11.21 release notes"
release_date: "07.05.2021"
rfcs:
  - id: STOR-1395
    title: StoRM Backend service enters failed state when stopped
  - id: STOR-1397
    title: Upgrading to StoRM v1.11.20 could break connections with MariaDB
  - id: STOR-1401
    title: Ensure MariaDB is started before StoRM Backend on boot
---

## StoRM Backend v. 1.11.21

Released on **{{ page.release_date }}** with [StoRM v. 1.11.21][release-notes].
<span style="float: right; margin-left: 8px;" class="label label-info">centos7</span>

### Description

This release:

* fixes the [known issue][known-issue-post] about the upgrade to StoRM v1.11.20 which could break connections with MariaDB;
* fixes the boot order ensuring that mariadb service is started before StoRM Backend;
* fixes the failed state shown on stop/restart of the service due to a misunderstood exit code meaning.

We should be able to suppress this by adding the exit code into the unit file as a "success" exit status:

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

If you're upgrading, you can update and restart StoRM backend service as follow:

```
yum update storm-backend-mp storm-backend-server
```

During the upgrade, the service will be restarted.
In case you have any kind of questions or problems please contact us.

In case of a clean installation please read the [System Administrator Guide][storm-sysadmin-guide].

Read more at:
* the [Quick deploy on CentOS7][quickdeploy] guide;
* [StoRM Puppet module forge page][stormpuppetmodule];
* [StoRM Puppet module generated documentation][stormpuppetmoduledoc] forge page.


[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.21.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.21
[quickdeploy]: {{site.baseurl}}/documentation/documentation/sysadmin-guide/1.11.21/quick-deployments/centos7/index.html
[stormpuppetmodule]: https://forge.puppet.com/cnafsd/storm
[stormpuppetmoduledoc]: https://italiangrid.github.io/storm-puppet-module/

[known-issue-post]: {{site.baseurl}}/2021/04/30/storm-v1.11.20-known-issue.html