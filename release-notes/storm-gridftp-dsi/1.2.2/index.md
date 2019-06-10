---
layout: default
title: "StoRM GridFTP server v.1.2.2 release notes"
release_date: "15.06.2019"
features:
  - id: STOR-1094
    title: Support for the CKSUM GridFTP command
---

## StoRM GridFTP server v.1.2.2

Released on **{{ page.release_date }}** with [StoRM v. 1.11.16][release-notes].

### Description

This release introduces the support for the CKSUM command, so that an ADLER32
checksum is returned if already known for a file, or computed on the fly and
stored in an extended attribute.

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.16.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.16
