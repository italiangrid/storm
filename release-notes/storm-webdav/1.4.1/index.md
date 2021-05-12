---
layout: default
title: "StoRM WebDAV v. 1.4.1 release notes"
release_date: "12.05.2021"
rfcs:
  - id: STOR-1400
    title: StoRM WebDAV service enters failed state when stopped
---

## StoRM WebDAV v. 1.4.1

Released on **{{ page.release_date }}** as an update of [StoRM v. 1.11.21][release-notes].
<span style="float: right; margin-left: 8px;" class="label label-info">centos7</span>

### Description

This release fixes the failed state shown on stop/restart of the service due to a misunderstood exit code meaning.

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

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.20.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.20

[quickdeploy]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.20/quick-deployments/centos7/
[dav-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.20/installation-guides/storm-webdav/storm-webdav-guide/

