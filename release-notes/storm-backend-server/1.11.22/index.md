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
  - id: STOR-1510
    title: Storage space info creation timestamp must not be overwritten during UPDATE queries
---

## StoRM Backend v. 1.11.22

Released on **{{ page.release_date }}** with [StoRM v. 1.11.22][release-notes].
<span style="float: right; margin-left: 8px;" class="label label-info">centos7</span>

### Description

This release mainly:

* adds WebDAV pools support with a load balancing strategy as already present for GridFTP endpoints;
* fixes bug on storage space table update;
* fixes not published VOs in case of multiple support.

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


[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.22.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.22
[quickdeploy]: {{site.baseurl}}/documentation/documentation/sysadmin-guide/1.11.22/quick-deployments/centos7/index.html
[stormpuppetmodule]: https://forge.puppet.com/cnafsd/storm
[stormpuppetmoduledoc]: https://italiangrid.github.io/storm-puppet-module/
