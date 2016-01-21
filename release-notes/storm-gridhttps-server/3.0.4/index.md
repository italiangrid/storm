---
layout: default
title: "StoRM GridHTTPs v. 3.0.4 release notes"
release_date: "22.01.2016"
rfcs:
  - id: STOR-741
    title: WebDAV MOVE and COPY requests with source equal to destination fail with 412 instead of 403
---

## StoRM GridHTTPs v. 3.0.4

Released on **{{ page.release_date }}** with [StoRM v. 1.11.10][StoRM-1.11.10].

### Description

This release provides a fix for a security vulnerability and another minor bug fix on the returned error code when copy and move operation are done on equal source and destination.

### Bug fixes

{% include list-rfcs.liquid %}

### Security vulnerabilities

More information concerning the security vulnerabilities addressed by this release are going to be published when appropriate at [this URL](https://wiki.egi.eu/wiki/SVG:Advisory-SVG-2015-10134).

### Installation and configuration

- Update and restart package:

```bash
yum update storm-gridhttps-server
service storm-gridhttps-server restart
```

You can find more information about upgrade, clean installation and configuration of StoRM services in the [System Administration Guide][storm-sysadmin-guide].

### Known issues

None at the moment

[StoRM-1.11.10]: {{site.baseurl}}/release-notes/StoRM-v1.11.10.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/
