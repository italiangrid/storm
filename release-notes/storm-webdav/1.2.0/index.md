---
layout: default
title: "StoRM WebDAV v. 1.2.0 release notes"
release_date: "02.10.2019"
rfcs:
  - id: STOR-1095
    title: StoRM WebDAV default configuration should not depend on iam-test.indigo-datacloud.eu
  - id: STOR-1096
    title: Unreachable OpenID Connect provider causes StoRM WebDAV startup failure
features:
  - id: STOR-1097
    title: Introduce Conscrypt JSSE provider support
  - id: STOR-1098
    title: Update spring boot to 2.1.4.RELEASE
---

## StoRM WebDAV v. 1.2.0

Released on **{{ page.release_date }}** as an update of [StoRM v. 1.11.16][release-notes].

### Description

This release:

* introduces configurable support for Conscrypt in StoRM WebDAV that improves 
TLS performance for Java applications by delegating the handing of cryptographic
operations to boringssl (the Google fork of OpenSSL);
* fixes StoRM WebDAV startup failure due to an unreachable OpenID Connect provider
and some minor configuration issues;
* upgrades spring boot to 2.1.4.RELEASE.

More information can be found in the [StoRM WebDAV service installation and configuration][dav-guide]
guide and in the [StoRM WebDAV support for Third Party Copy transfers][tpc-guide]
guide.

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

Update the StoRM WebDAV package:

    yum update storm-webdav

### Known issues


[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.16.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.16
[dav-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.16/storm-webdav-guide.html
[tpc-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.16/tpc.html
[tpc-technical]: https://twiki.cern.ch/twiki/bin/view/LCG/HttpTpcTechnical
