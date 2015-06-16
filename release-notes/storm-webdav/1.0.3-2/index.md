---
layout: default
title: "StoRM WebDAV v. 1.0.3-2 release notes"
release_date: 15.06.2015
rfcs:
- id: STOR-795
  title: Storage area matching fails with COPY/MOVE
---

## StoRM WebDAV v. 1.0.3-2

Released on **{{ page.release_date }}** as an update of [StoRM v. 1.11.9][release-notes].

### Description

There was an error in the construction of the previously released 1.0.3-1
storm-webdav package, which included a service jar that didn't actually provide
a fix for the [STOR-795][STOR-795] issue. This release properly fixes the issue
in the path resolution logic that could cause authorization failures on COPY
and MOVE requests.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

Check the the [StoRM WebDAV installation and configuration guide][storm-webdav-guide]
for detailed installation and configuration information.

For the other StoRM services, check the the [System Administration
Guide][storm-sysadmin-guide].

[STOR-795]: https://issues.infn.it/browse/STOR-795
[release-notes]: {{ site.baseurl }}/release-notes/StoRM-v1.11.9.html
[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.9
[storm-webdav-guide]: {{ site.baseurl }}/documentation/sysadmin-guide/1.11.9/storm-webdav-guide.html
