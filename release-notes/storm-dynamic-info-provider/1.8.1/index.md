---
layout: default
title: StoRM Info Provider v.1.8.1 release notes
release_date: "26.02.2019"
rfcs:
  - id: STOR-1037
    title: The published WebDAV endpoint ends with /webdav which is obsolete and broken without an ending slash
features:
  - id: STOR-844
    title: Change the way info-provider knows if a webdav endpoint must be published
  - id: STOR-1020
    title: Remove functions and code related to old and deprecated variables
  - id: STOR-1039
    title: Implement suggested changes in JSON report file
---

## StoRM Info Provider v. 1.8.1

Released on **{{ page.release_date }}** with [StoRM v. 1.11.15][release-notes].

### Description

This release fixes JSON report file as follow:

1. The root of the JSON is an object with only one key: `storageservice`. Its value is an object where everything else is put.
2. The `endpoints` key has been renamed to `storageendpoints`.
3. The `shares` key has been renamed to `storageshares`.
4. Fixed share's VO list when multiple VOs are defined as an array.
5. The name of the shares matches the corresponding space token.

Check the [How-To][how-to-json-report] documentation section to learn how to
configure StoRM to share the JSON report.

This release also allows to specify multiple WebDAV endpoints by using the new
YAIM variable `STORM_WEBDAV_POOL_LIST`, as a comma separated list.
The old strategy used to publish the StoRM WebDAV endpoint is still supported
but deprecated.

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

Update package:

    yum update storm-dynamic-info-provider

Re-configure info provider:

    /usr/libexec/storm-info-provider configure

Restart BDII service:

    service bdii restart

Alternatively, you can simply update the package and run YAIM.

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.15.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/
[how-to-json-report]: {{site.baseurl}}/documentation/how-to/how-to-publish-json-report/