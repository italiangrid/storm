---
layout: default
title: "StoRM Frontend v.1.8.8 release notes"
release_date: "13.03.2015"
rfcs:
- id: STOR-750
  title: Single quote in certificate subject causes failures in StoRM async requests
---

## StoRM Frontend v.1.8.8

Released on **{{ page.release_date }}** with [StoRM v. 1.11.8]({{ site.baseurl }}/release-notes/StoRM-v1.11.8.html).

### Description

This release fixes the error occoured when user's DN contains a single quote.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Administration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.


[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.8
