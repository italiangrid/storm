---
layout: default
title: "StoRM BackEnd v. 1.11.11 release notes"
release_date: "05.02.2016"
rfcs:
- id: STOR-867
  title: Backend initialization fails when storage area has a default acl list
---

## StoRM Backend v. 1.11.11

Released on **{{ page.release_date }}** with [StoRM v. 1.11.11]({{ site.baseurl }}/release-notes/StoRM-v1.11.11.html).

### Description

This release fixes a covariance issue on Java code that causes a StoRM Backend error on start, when a default acl is defined on a storage area.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

- Update and restart package:

```bash
yum update storm-backend-server
service storm-backend-server restart
```

You can find more information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/
