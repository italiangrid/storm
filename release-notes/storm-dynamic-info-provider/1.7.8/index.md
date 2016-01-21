---
layout: default
title: "StoRM Info Provider v.1.7.8 release notes"
release_date: 13.03.2015
rfcs:
- id: STOR-751
  title: storm-info-provider-rpm misses dependency on python-simplejson on SL5
- id: STOR-780
  title: StoRM Info Provider 'configure' fails with 'too many values to unpack'
---

## StoRM Info Provider v. 1.7.8

Released on **{{ page.release_date }}** with [StoRM v. 1.11.8]({{ site.baseurl }}/release-notes/StoRM-v1.11.8.html).

### Description

This release fix a dependency issue on the SL5 packaging and provides more
robust info provider configuration parsing.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Admininistration Guide][storm-sysadmin-guide] of
the [Documentation][storm-documentation] section.

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.8
