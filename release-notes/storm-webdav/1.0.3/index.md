---
layout: default
title: "StoRM WebDAV v. 1.0.3 release notes"
release_date: 29.05.2015
rfcs:
- id: STOR-795
  title: Storage area matching fails with COPY/MOVE
---

## StoRM WebDAV v. 1.0.3

Released on **{{ page.release_date }}** with [StoRM v. 1.11.9][release-notes].

### Description

This releases fixes an issue in the path resolution logic that could cause
authorization failures on COPY and MOVE requests.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

Check the the [StoRM WebDAV installation and configuration guide][storm-webdav-guide]
for detailed installation and configuration information.

For the other StoRM services, check the the [System Administration
Guide][storm-sysadmin-guide].

[release-notes]: {{ site.baseurl }}/release-notes/StoRM-v1.11.9.html
[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.9
[storm-webdav-guide]: {{ site.baseurl }}/documentation/sysadmin-guide/1.11.9/storm-webdav-guide.html
