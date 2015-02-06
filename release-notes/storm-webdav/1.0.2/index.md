---
layout: default
title: "StoRM WebDAV v. 1.0.2 release notes"
release_date: 05.02.2015
rfcs:
- id: STOR-346
  title: WebDAV DELETE response is 401 UNAUTHORIZED instead of 404 NOT EXISTS for authorized users on nonexistent resources
- id: STOR-632
  title: StoRM WebDAV service handles multi-range partial get incorrectly
- id: STOR-669
  title: HTTP requests fail if path contains trailing slashes
---

## StoRM WebDAV v. 1.0.2

Released on **{{ page.release_date }}** with [StoRM v. 1.11.7]({{ site.baseurl }}/release-notes/StoRM-v1.11.7.html).

### Description

This is the first official release of the StoRM WebDAV service, which replaces the storm-gridhttps server.

The StoRM WebDAV service provides performance improvements and resolves some
problems previously found in the storm-gridhttps-server, in particular:

- incorrect handling of multi-range partial get requests
- incorrect handling of multiple slashes in incoming requests

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide] of
the [Documentation][storm-documentation] section.

A new section describing the StoRM WebDAV service has just been added.

### Known issues

None at the moment

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.7
