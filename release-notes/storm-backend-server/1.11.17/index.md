---
layout: default
title: "StoRM BackEnd v. 1.11.17 release notes"
release_date: "17.12.2019"
rfcs:
  - id: STOR-821
    title: service storm-backend-server status returns 0 even if backend is not running
---

## StoRM Backend v. 1.11.17

Released on **{{ page.release_date }}** with [StoRM v. 1.11.17][release-notes].

### Description

This release fixes a minor issue on the init script.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

Update package:

```
yum update storm-backend-server
```

and run YAIM.

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.17.html
[upgrading]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.17/#upgrading
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.17
