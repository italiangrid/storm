---
layout: default
title: "StoRM WebDAV v. 1.4.2 release notes"
release_date: "21.06.2023"
rfcs:
  - id: STOR-1396
    title: Ensure adler32 checksums are always 8 chars long
  - id: STOR-1450
    title: Increase default timeout for TPC to 30 seconds
  - id: STOR-1500
    title: When redis is disabled the health indicator for redis should be disabled
  - id: STOR-1574
    title: Old java/canl creates problems with encoding of subject/issuer names in self-signed certificates
  - id: STOR-1440
    title: StoRM WebDAV should configure a bigger heap by default
  - id: STOR-1497
    title: Upgrade canl-java to v2.6.0
  - id: STOR-1515
    title: StoRM WebDAV metrics on TPC.pull/push.throughput
  - id: STOR-1555
    title: Upgrade jQuery version
  - id: STOR-1556
    title: Remove TRACE from allowed methods
  - id: STOR-1557
    title: Upgrade Spring Boot version to the latest
  - id: STOR-1558
    title: Update bouncycastle version to 1.67
---

## StoRM WebDAV v. 1.4.2

Released on **{{ page.release_date }}** as an update of [StoRM v. 1.11.22][release-notes].
<span style="float: right; margin-left: 8px;" class="label label-info">centos7</span>

### Description

This release:

* upgrades significant dependencies (spring-boot, canl, bouncycastle, jQuery)
* removes the support for TRACE method
* tunes some default values (default TPC timeout, default heap size, etc.)

and fixes other minor bugs/issues.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

If you're upgrading, you can update and restart service:

```
yum update storm-webdav
systemctl restart storm-webdav
```

In case of a clean installation please read the [System Administrator Guide][storm-sysadmin-guide].

Read more at:
* [StoRM Puppet module forge page][stormpuppetmodule];
* [StoRM Puppet module generated documentation][stormpuppetmoduledoc];
* the [Quick deploy on CentOS7][quickdeploy] guide.

[stormpuppetmodule]: https://forge.puppet.com/cnafsd/storm
[stormpuppetmoduledoc]: https://italiangrid.github.io/storm-puppet-module/

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.22.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.22

[quickdeploy]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.22/quick-deployments/centos7/
[dav-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.22/installation-guides/storm-webdav/storm-webdav-guide/

