---
layout: default
title: "StoRM FrontEnd v. 1.8.3 release notes"
release_date: 02.09.2013
rfcs:
   - id: STOR-306
     title: StoRM returns NULL fileSize for ptp with expected size
   - id: STOR-331
     title: StoRM returns wrong filesize in PtG
   - id: STOR-257
     title: Unable to change STORM_USER via yaim setup of StoRM
---

## StoRM FrontEnd v. 1.8.3

Released on **{{ page.release_date }}** with [StoRM v. 1.11.2]({{ site.baseurl }}/release-notes/StoRM-v1.11.2.html).

### Description

This release provides several bug fixes.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Admininistration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

### Known issues

None at the moment

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.2
