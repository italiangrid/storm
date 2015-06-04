---
layout: default
title: "YAIM StoRM v.4.3.8 release notes"
release_date: "29.05.2015"
rfcs:
- id: STOR-782
  title: Check storm-info-provider exit code in order to block yaim execution in case of error
- id: STOR-789
  title: Make ntp.conf creation/overwrite optional
---

## YAIM StoRM v. 4.3.8

Released on **{{ page.release_date }}** with [StoRM v. 1.11.9]({{ site.baseurl }}/release-notes/StoRM-v1.11.9.html).

### Description

This release provides the following improvements:

- YAIM execution now blocks in case of errors
- ```NTP_HOSTS_IP``` is no more a mandatory variable in order to allow site administrators to manage their own ntp.conf file

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Administration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.


[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.9
