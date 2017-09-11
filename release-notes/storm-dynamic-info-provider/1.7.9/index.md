---
layout: default
title: StoRM Info Provider v.1.7.9 release notes
release_date: 22.01.2016
rfcs:
  - id: STOR-837
    title: Missing GlueSAPath from Storage Areas BDII info
---

## StoRM Info Provider v. 1.7.9

Released on **{{ page.release_date }}** with [StoRM v. 1.11.10][release-notes].

### Description

This release fixes a missing Glue2 field, not published on the BDII.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

Update package:

    yum update storm-dynamic-info-provider

Re-configure info provider:

     /usr/libexec/storm-info-provider configure

Restart BDII service:

     service bdii restart

Alternatively, you can simply update the package and run YAIM.

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.10.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.10