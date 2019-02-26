---
layout: default
title: "StoRM WebDAV v. 1.1.0 release notes"
release_date: "26.02.2019"
features:
  - id: STOR-1018
    title: Support for third-party copy in StoRM WebDAV service
---

## StoRM WebDAV v. 1.1.0

Released on **{{ page.release_date }}** as an update of [StoRM v. 1.11.15][release-notes].

### Description

This release implements basic support for Third-Party-Copy.

Useful links:

- [LCGDM HTTP/WebDAV Third Party Copy extension](https://svnweb.cern.ch/trac/lcgdm/wiki/Dpm/WebDAV/Extensions#ThirdPartyCopies)
- [LCG twiki on HTTP/WebDAV Third-Party-Copy](https://twiki.cern.ch/twiki/bin/view/LCG/HttpTpc)
- [HTTP/WebDAV Third-Party-Copy Technical Details](https://twiki.cern.ch/twiki/bin/view/LCG/HttpTpcTechnical)

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

Update package:

    yum update storm-webdav

Restart service:

    service storm-webdav restart

Alternatively, you can simply update the package and run YAIM.

Check the the [StoRM WebDAV installation and configuration guide][storm-webdav-guide]
for detailed installation and configuration information.

For the other StoRM services, check the the [System Administration Guide][storm-sysadmin-guide].

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.15.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15
[storm-webdav-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15/storm-webdav-guide.html
