---
layout: default
title: "StoRM GridHTTPs v. 3.0.4 release notes"
release_date: "18.12.2015"
rfcs:
- id: STOR-741
  title: WebDAV MOVE and COPY requests with source equal to destination fail with 412 instead of 403
---

## StoRM GridHTTPs v. 3.0.4

Released on **{{ page.release_date }}** with [StoRM v. 1.11.10][StoRM-1.11.10].

### Description

This release:

- updates the version of [Milton library][Milton-site];
- fixes the returned HTTP status code when MOVE or COPY requested URL identify the same resource of the Destination header.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

Update the package:

    yum update storm-gridhttps-server

and then, restart the service:

    service storm-gridhttps-server restart

There's no need to re-run YAIM.

You can find more information about upgrade, clean installation and configuration of StoRM services in the [System Administration Guide][storm-sysadmin-guide].

### Known issues

None at the moment

[Milton-site]: http://milton.io
[StoRM-1.11.10]: {{site.baseurl}}/release-notes/StoRM-v1.11.10.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/
