---
layout: default
title: "StoRM GridHTTPs v. 3.0.2 release notes"
release_date: 19.12.2014
rfcs:
- id: STOR-607
  title: WebDAV partial GET requests fill up disk with temporary files
---

## StoRM GridHTTPs v. 3.0.2

Released on **{{ page.release_date }}** with [StoRM v. 1.11.5]({{ site.baseurl }}/release-notes/StoRM-v1.11.5.html).

### Description

This release fixes a problem in the handling of partial HTTP get requests that
could fill up the disk space on the machine where the storm-gridhttps server
was running.

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Admininistration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

### Known issues

None at the moment

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.5
