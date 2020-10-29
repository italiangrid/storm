---
layout: default
title: "StoRM WebDAV v. 1.3.1 release notes"
release_date: "29.10.2020"
rfcs:
  - id: STOR-1259
    title: StoRM WebDAV sends response body for HEAD requests resulting in errors
features:
  - id: STOR-1201
    title: Update spring boot to 2.2.6 release
---

## StoRM WebDAV v. 1.3.1

Released on **{{ page.release_date }}** as an update of [StoRM v. 1.11.19][release-notes].

### Description

This release:

- fixes the body response for HEAD requests, due to a Spring Boot issue;
- allows disabling TLS client authentication in Third Party Copy;
- fixes several other minor issues.

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

If you're upgrading, you can update and restart service:

```bash
yum update storm-webdav
service storm-webdav restart
```

or

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

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.19.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.19

[quickdeploy]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.19/quick-deployments/centos7/
[dav-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.19/installation-guides/storm-webdav/storm-webdav-guide/

