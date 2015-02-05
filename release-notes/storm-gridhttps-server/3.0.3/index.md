---
layout: default
title: "StoRM GridHTTPs v. 3.0.3 release notes"
release_date: 05.02.2015
rfcs:
- id: STOR-741
  title: WebDAV MOVE and COPY with source equal to destination fail with 412 instead of 403
---

## StoRM GridHTTPs v. 3.0.3

Released on **{{ page.release_date }}** with [StoRM v. 1.11.7]({{ site.baseurl }}/release-notes/StoRM-v1.11.7.html).

### Description

This release fixes a problem in the handling of partial HTTP get requests that
could fill up the disk space on the machine where the storm-gridhttps server
was running.

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Administration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

### Known issues

None at the moment

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.7
