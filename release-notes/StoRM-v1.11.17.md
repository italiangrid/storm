---
layout: default
title: "StoRM v.1.11.17 - release notes"
release_date: "17.12.2019"
rfcs:
  - id: STOR-821
    title: service storm-backend-server status returns 0 even if backend is not running
features:
  - id: STOR-1088
    title: SystemD support for StoRM WebDAV
  - id: STOR-1091
    title: SystemD support for StoRM GridFTP
components:
  - name: StoRM Backend
    package: storm-backend-server
    version: 1.11.17
    platforms:
      - centos6
  - name: StoRM WebDAV
    package: storm-webdav
    version: 1.2.1
    platforms:
      - centos6
      - centos7
  - name: StoRM GridFTP
    package: storm-gridftp-dsi
    version: 1.2.3
    platforms:
      - centos6
      - centos7
---

## StoRM v. 1.11.17

Released on **{{ page.release_date }}**.

#### Description

This release introduces the support for CentOS 7 for StoRM WebDAV and StoRM GridFTP.

#### Released components

{% include list-components.liquid %}

#### Bug fixes

{% include list-rfcs.liquid %}

#### Enhancements

{% include list-features.liquid %}

#### Installation

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

##### Upgrade StoRM repository

Starting from StoRM v1.11.14 the StoRM package repository has been migrated.
Read how to install/upgrade StoRM packages in the [Downloads][downloads-page] section.

[downloads-page]: {{site.baseurl}}/download.html#stable-releases

[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide
[upgrade-from-16]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.17/#upgrading
[upgrade-from-15]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.17/#upgrading-earlier
