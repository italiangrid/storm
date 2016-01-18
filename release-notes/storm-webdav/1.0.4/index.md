---
layout: default
title: "StoRM WebDAV v. 1.0.4 release notes"
release_date: "25.01.2016"
features:
- id: STOR-700
  title: Add support for RFC 3230 in StoRM WebDAV service
---

## StoRM WebDAV v. 1.0.4

Released on **{{ page.release_date }}** as an update of [StoRM v. 1.11.10][release-notes].

### Description

This release provides fixes for security vulnerabilities that were recently reported, and adds the support for [RFC-3230][RFC-3230].
It explains how to get checksum type and value of the stored resources. From this release, each HEAD and GET response will include a header like:

```{html}
  Digest: adler32=8a23d4f8
```

to be compliant to the [RFC-3230][RFC-3230] specific.

### Security vulnerabilities

More information concerning the security vulnerabilities addressed by this release are going to be published when appropriate at [this URL](https://wiki.egi.eu/wiki/SVG:Advisory-SVG-2015-10134).

### New features

{% include list-features.liquid %}

### Installation and configuration

Check the the [StoRM WebDAV installation and configuration guide][storm-webdav-guide] for detailed installation and configuration information.

For the other StoRM services, check the the [System Administration Guide][storm-sysadmin-guide].

[Milton-site]: http://milton.io
[RFC-3230]: https://tools.ietf.org/html/rfc3230
[release-notes]: {{ site.baseurl }}/release-notes/StoRM-v1.11.10.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/
[storm-webdav-guide]: {{ site.baseurl }}/documentation/sysadmin-guide/1.11.9/storm-webdav-guide.html
