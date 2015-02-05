---
layout: default
title: "StoRM BackEnd v. 1.11.6 release notes"
release_date: 21.01.2015
rfcs:
- id: STOR-717
  title: SRM Release Files fails in some cases
---

## StoRM Backend v. 1.11.6

Released on **{{ page.release_date }}** with [StoRM v. 1.11.6]({{ site.baseurl }}/release-notes/StoRM-v1.11.6.html).

### Description

This release provides a fix for a regression introduced in 1.11.5 that caused the failure of SRM release files
requests under certain conditions.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Administration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.6
