---
layout: default
title: "StoRM BackEnd v. 1.11.9 release notes"
release_date: "29.05.2015"
rfcs:
- id: STOR-280
  title: used-space.ini can be used to initialize quota managed storage areas
- id: STOR-474
  title: Change LCMAPS logging setup in StoRM
- id: STOR-792
  title: Avoid du on quota-enabled storage areas at Backend launch
---

## StoRM Backend v. 1.11.9

Released on **{{ page.release_date }}** with [StoRM v. 1.11.9]({{ site.baseurl }}/release-notes/StoRM-v1.11.9.html).

### Description

This release fixes the improper launch of command line du on quota-enabled storage areas at Backend launch.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.9
