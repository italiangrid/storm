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

- Update package:

```bash
yum update storm-dynamic-info-provider
```

- Re-configure info provider:

```bash
/usr/libexec/storm-info-provider configure
```

- Restart BDII service:

```bash
service bdii restart
```

Alternatively, you can simply run YAIM after the update.

You can find more information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide]. <br/> See the [Documentation][storm-documentation] section.

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/
