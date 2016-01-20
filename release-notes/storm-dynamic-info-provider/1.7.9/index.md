---
layout: default
title: "StoRM Info Provider v.1.7.9 release notes"
release_date: "22.01.2016"
rfcs:
- id: STOR-837
  title: Missing GlueSAPath from Storage Areas BDII info
---

## StoRM Info Provider v. 1.7.9

Released on **{{ page.release_date }}** with [StoRM v. 1.11.10]({{ site.baseurl }}/release-notes/StoRM-v1.11.10.html).

### Description

This release fixes a missing Glue2 field, not published on the BDII.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

Update `storm-dynamic-info-provider` package:

```shell
yum update storm-dynamic-info-provider
```

If you previously used `yaim-storm` to configure your site, to re-configure the info provider launch:

```shell
/usr/libexec/storm-info-provider configure
```

Otherwise, re-configure as your needed. Then, restart _bdii_ service:

```shell
service bdii restart
```

You can find more information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide]. <br/> See the [Documentation][storm-documentation] section.

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/
