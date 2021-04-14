---
layout: default
title: "StoRM Frontend v.1.8.14 release notes"
release_date: "12.04.2021"
features:
  - id: STOR-1173
    title: Add the average time in the summary for round monitoring
  - id: STOR-1342
    title: Cleanup frontend codebase
---

## StoRM Frontend v.1.8.14

Released on **{{ page.release_date }}** with [StoRM v. 1.11.20][release-notes].
<span style="float: right; margin-left: 8px;" class="label label-info">centos7</span>

### Description

This release:

* fixes several minor codebase issues on frontend, some of them could cause a memory leak;
* adds the average time in the summary for round frontend's monitoring log.

### Enhancements

{% include list-features.liquid %}

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
[quickdeploy]: {{site.baseurl}}/documentation/how-to/basic-storm-standalone-configuration-centos7/1.11.20/

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.20.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.20
