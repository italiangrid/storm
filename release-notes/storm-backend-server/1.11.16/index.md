---
layout: default
title: "StoRM BackEnd v. 1.11.16 release notes"
release_date: "15.06.2019"
rfcs:
features:
---

## StoRM Backend v. 1.11.16

Released on **{{ page.release_date }}** with [StoRM v. 1.11.16][release-notes].

### Description

This release:

...

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

Update package:

```
yum update storm-backend-server
```

and run YAIM.

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.16.html
[upgrading]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.16/#upgrading
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.16
