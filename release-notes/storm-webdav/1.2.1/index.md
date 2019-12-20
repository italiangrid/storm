---
layout: default
title: "StoRM WebDAV v. 1.2.1 release notes"
release_date: "17.12.2019"
features:
  - id: STOR-1088
    title: SystemD support for StoRM WebDAV
---

## StoRM WebDAV v. 1.2.1

Released on **{{ page.release_date }}** as an update of [StoRM v. 1.11.17][release-notes].

### Description

This release introduces the support for SystemD and CentOS 7 as a supported platform.

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

Update the StoRM WebDAV package:

    yum update storm-webdav

and run YAIM.



[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.17.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.17
[dav-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.17/storm-webdav-guide.html
