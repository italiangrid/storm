---
layout: default
title: "StoRM native libs v.1.0.3 release notes"
release_date: 19.12.2014
rfcs:
- id: STOR-651
  title: StoRM incorrectly considers small files as migrated to tape on GPFS 3.5
- id: STOR-419
  title: StoRM Native Libs LCMAPS package should include dependencies to the needed lcmaps plugins
---

## StoRM native-libs v. 1.0.3

Released on **{{ page.release_date }}** with [StoRM v. 1.11.5]({{ site.baseurl }}/release-notes/StoRM-v1.11.5.html).

### Description

This release provides several bug fixes.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Admininistration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

### Known issues

None at the moment

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.5
