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

The brand new StoRM WebDAV component 

fixes a problem in the handling of partial HTTP get requests that
could fill up the disk space on the machine where the storm-gridhttps server
was running.

### Bug fixes

The following issues list contains several storm-gridhttps-server open bugs those had been fixed with the new StoRM WebDAV component:

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Administration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

### Known issues

None at the moment

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.7
