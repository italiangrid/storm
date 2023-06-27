---
layout: default
title: StoRM Info Provider v.1.8.3 release notes
release_date: "21.06.2023"
rfcs:
  - id: STOR-1506
    title: Only one VO (the first) is listed into storage site report even if a storage area serves multiple VOs
---

## StoRM Info Provider v. 1.8.3

Released on **{{ page.release_date }}** with [StoRM v. 1.11.22][release-notes].
<span style="float: right; margin-left: 8px;" class="label label-info">centos7</span>

### Description

This release fixes the info published about the storage areas supported VOs. In fact, in case multiple VOs are configured, only the first one was published into storage site report.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

This package needs StoRM Backend version >= 1.11.22.
You can update package as follows:

```
yum update storm-dynamic-info-provider
```

and run configuration again:

```
/usr/libexec/storm-info-provider configure
```

Read more at:
* [StoRM Puppet module forge page][stormpuppetmodule];
* [StoRM Puppet module generated documentation][stormpuppetmoduledoc];
* the [Quick deploy on CentOS7][quickdeploy] guide.

[stormpuppetmodule]: https://forge.puppet.com/cnafsd/storm
[stormpuppetmoduledoc]: https://italiangrid.github.io/storm-puppet-module/

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.22.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.22

[quickdeploy]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.22/quick-deployments/centos7/