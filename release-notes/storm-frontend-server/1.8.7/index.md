---
layout: default
title: "StoRM Frontend v.1.8.7 release notes"
release_date: "09.02.2015"
rfcs:
- id: STOR-690
  title: StoRM returns SRM_SUCCESS as request status for SBOL when one of the multiple SURL is still SRM_REQUEST_QUEUED or SRM_IN_PROGRESS
---

## StoRM Frontend v.1.8.7

Released on **{{ page.release_date }}** with [StoRM v. 1.11.7]({{ site.baseurl }}/release-notes/StoRM-v1.11.7.html).

### Description

This release fixes the returned SRM status of a srmStatusBol called on multiple SURL and not entirely ended.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Administration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.


[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.7
