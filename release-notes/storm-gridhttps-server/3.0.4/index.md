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

This release provides fixes for security vulnerabilities and a few bug fixes.

### Bug fixes

{% include list-rfcs.liquid %}

### Security vulnerabilities

More information concerning the security vulnerabilities addressed by this release are going to be published when appropriate at [this URL](https://wiki.egi.eu/wiki/SVG:Advisory-SVG-2015-10134).

### Installation and configuration

Update the package:

    yum update storm-gridhttps-server

and then, restart the service:

    service storm-gridhttps-server restart

There's no need to re-run YAIM.

You can find more information about upgrade, clean installation and configuration of StoRM services in the [System Administration Guide][storm-sysadmin-guide].

### Known issues

None at the moment

[StoRM-1.11.10]: {{site.baseurl}}/release-notes/StoRM-v1.11.10.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/
