---
layout: default
title: "YAIM StoRM v.4.3.12 release notes"
release_date: "28.02.2019"
rfcs:
  - id: STOR-1019
    title: Fix namespace configuration syntax error
features:
  - id: STOR-844
    title: Change the way info-provider knows if a webdav endpoint must be published
  - id: STOR-1020
    title: Remove functions and code related to old and deprecated variables
  - id: STOR-1038
    title: YAIM should exit with error in case StoRM Backend daemon start fails
  - id: STOR-1067
    title: Make YAIM able to configure storm-webdav organizations
---

## YAIM StoRM v. 4.3.12

Released on **{{ page.release_date }}** with [StoRM v. 1.11.15][release-notes].

### Description

This release:

* allows to specify multiple WebDAV endpoints by using the new YAIM variable
`STORM_WEBDAV_POOL_LIST`, as a comma separated list
(read more [here][webdav-pool-list]). The old strategy used to
publish the StoRM WebDAV endpoint is still supported but deprecated;
* exits with error in case StoRM Backend daemon fails on start-up;
* removes stuff related to deprecated variables;
* fixes some configuration syntax errors.

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

Update package and relaunch YAIM configuration:

    $ yum update yaim-storm
    $ /opt/glite/yaim/bin/yaim -c -s /etc/storm/siteinfo/storm.def -n se_storm_backend

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide] of
the [Documentation][storm-documentation] section.

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.15.html
[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15

[webdav-pool-list]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15#important2
