---
layout: default
title: StoRM WebDAV v. 1.0.4 release notes
release_date: 22.01.2016
rfcs: []
features:
  - id: STOR-700
    title: Add support for RFC 3230 in StoRM WebDAV service
---

## StoRM WebDAV v. 1.0.4

Released on **{{ page.release_date }}** with [StoRM v. 1.11.10][release-notes].

### Description

This release provides a fix for a security vulnerability that was recently
reported, and adds support for [RFC-3230][rfc-3230].
It explains how to get checksum type and value of the stored resources.
From this release, each HEAD and GET response will include a header like:

    Digest: adler32=8a23d4f8

to be compliant with [RFC-3230][rfc-3230] specific.

### Security vulnerabilities

More information concerning the security vulnerabilities addressed by this
release are going to be published when appropriate at
[this URL][vulnerability-URL].

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

Update and restart package:

    yum update storm-webdav
    service storm-webdav restart

You can find more information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.10.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.10

[rfc-3230]: https://tools.ietf.org/html/rfc3230
[vulnerability-URL]: https://wiki.egi.eu/wiki/SVG:Advisory-SVG-2015-10134