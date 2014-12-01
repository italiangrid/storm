---
layout: default
title: "StoRM dynamic info v.1.7.7 release notes"
release_date: 05.12.2014
rfcs:
- id: STOR-320
  title: Storm publishes inconsistent storage capacity information
- id: STOR-609
  title: StoRM doesn't publish on BDII according to the EGI profile for GLUE 2.0
- id: STOR-671
  title: StoRM publishes inconsistent values due to an approximation problem
features:
- id: STOR-654
  title: Refactor StoRM dynamic info provider service
---

## StoRM dynamic-info-provider v. 1.7.7

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
