---
layout: default
title: "StoRM Backend v. 1.11.22 release notes"
release_date: "21.06.2023"
rfcs:
  - id: STOR-1506
    title: Only one VO (the first) is listed into storage site report even if a storage area serves multiple VOs
  - id: STOR-1525
    title: Load balancing strategy for StoRM WebDAV server pool
  - id: STOR-1561
    title: CREATE date in the future in table storage_space
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