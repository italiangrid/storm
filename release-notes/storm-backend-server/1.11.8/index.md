---
layout: default
title: "StoRM BackEnd v. 1.11.8 release notes"
release_date: "13.03.2015"
rfcs:
- id: STOR-776
  title: Inefficient SQL query for surl status checks
- id: STOR-777
  title: Inefficient query used to update SURL status when a releaseFiles is called
- id: STOR-778
  title: Improve efficiency on status PtGs
- id: STOR-779
  title: rm command does not properly abort ongoing PtP requests
---

## StoRM Backend v. 1.11.8

Released on **{{ page.release_date }}** with [StoRM v. 1.11.8]({{ site.baseurl }}/release-notes/StoRM-v1.11.8.html).

### Description

This release provides several bug fixes.
A YAIM reconfiguration is not necessary, just update and then restart your service.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide] of
the [Documentation][storm-documentation] section.

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.8
