---
layout: default
title: "StoRM GridHTTPs v. 2.0.3 release notes"
release_date: 05.12.2013
rfcs:
- id: STOR-450
  title: StoRM Gridhttps initializes VOMS validation in an unsafe way
- id: STOR-415
  title: StoRM documentation typo
- id: STOR-376
  title: StoRM GridHTTPs' fileTransfer and WebDAV requests on different context-paths
---

## StoRM GridHTTPs v. 2.0.3

Released on **{{ page.release_date }}** with [StoRM v. 1.11.3]({{ site.baseurl }}/release-notes/StoRM-v1.11.3.html).

### Description

This release provides fixes for security vulnerabilities that were recently reported, and a few bug fixes.

### Security vulnerabilities

More information concerning the security vulnerabilities addressed by this release are going to be published when appropriate at [this URL](https://wiki.egi.eu/wiki/SVG:Advisory-SVG-2012-4598).

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Admininistration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

### Known issues

None at the moment

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.3