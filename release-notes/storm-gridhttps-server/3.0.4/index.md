---
layout: default
title: "StoRM GridHTTPs v. 3.0.4 release notes"
release_date: "20.11.2015"
rfcs:
- id: STOR-741
  title: WebDAV MOVE and COPY requests with source equal to destination fail with 412 instead of 403
- id: STOR-834
  title: Update Milton version to face its security vulnerability
---

## StoRM GridHTTPs v. 3.0.4

Released on **{{ page.release_date }}** with [StoRM v. 1.11.10][StoRM-1.11.10].

### Description

This release mainly fixes a security vulnerability that affects the used [Milton library][Milton-site]. Another minor issue has been fixed: when the requested URL is the same resource of the Destination header, the HTTP MOVE and COPY requests fail.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Administration Guide][storm-sysadmin-guide].

### Known issues

None at the moment

[Milton-site]: http://milton.io
[StoRM-1.11.10]: {{site.baseurl}}/release-notes/StoRM-v1.11.10.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/
