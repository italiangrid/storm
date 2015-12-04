---
layout: default
title: "StoRM WebDAV v. 1.0.4 release notes"
release_date: "20.11.2015"
features:
- id: STOR-700
  title: Add support for RFC 3230 in StoRM WebDAV service
- id: STOR-834
  title: Update Milton version to face its security vulnerability
---

## StoRM WebDAV v. 1.0.4

Released on **{{ page.release_date }}** as an update of [StoRM v. 1.11.10][release-notes].

### Description

This release mainly fixes a security vulnerability that affects the used [Milton library][Milton-site] and add the support for [RFC-3230][RFC-3230]. In shorts, RFC-3230 allows a client to request the checksum for a file, then HEAD and GET responses should include a header like:

```{html}
  Digest: adler32=8a23d4f8
```

The checksum type and value must be read into the Digest header.

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
