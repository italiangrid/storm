---
layout: default
title: "StoRM WebDAV v. 1.4.0 release notes"
release_date: "12.04.2021"
rfcs:
  - id: STOR-1298
    title: StoRM WebDAV rpm doesn't set the proper ownership on /var/log/storm
  - id: STOR-1332
    title: Login with OIDC button displayed only on storage area index page
  - id: STOR-1335
    title: Login with OIDC button not shown for error pages
features:
  - id: STOR-1336
    title: Add support for externalized session management
  - id: STOR-1351
    title: StoRM webdav should include user traceability information in access log
  - id: STOR-1358
    title: StoRM WebDAV package should install Java 11
---

## StoRM WebDAV v. 1.4.0

Released on **{{ page.release_date }}** as an update of [StoRM v. 1.11.20][release-notes].
<span style="float: right; margin-left: 8px;" class="label label-info">centos7</span>

### Description

This release:

* requires and install Java 11;
* fixes some bugs related to the OIDC login button;
* fixes logging directory ownership;
* adds support for externalized session management;
* includes user traceability information in access log.

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

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

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.20.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.20

[quickdeploy]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.20/quick-deployments/centos7/
[dav-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.20/installation-guides/storm-webdav/storm-webdav-guide/

