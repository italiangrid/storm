---
layout: default
title: "StoRM native libs v.1.0.4 release notes"
release_date: 29.05.2015
rfcs:
- id: STOR-471
  title: Streamline LCAS/LCMAPS configuration for the StoRM Backend and gridftp
---

## StoRM native-libs v. 1.0.4

Released on **{{ page.release_date }}** with [StoRM v. 1.11.9]({{ site.baseurl }}/release-notes/StoRM-v1.11.9.html).

### Description

This release removes SYS\_LOG destination of LCMAPS logging by turning it into only USR\_LOG.

### Bug fixes and improvements

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Admininistration Guide][storm-sysadmin-guide] of
the [Documentation][storm-documentation] section.

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.9
