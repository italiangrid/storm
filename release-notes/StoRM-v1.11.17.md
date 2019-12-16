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

This release mainly introduces CentOS 7 as a supported platform for both StoRM WebDAV and StoRM GridFTP.

#### Released components

{% include list-components.liquid %}

#### Bug fixes

{% include list-rfcs.liquid %}

#### Enhancements

{% include list-features.liquid %}

#### Installation

In case of a clean installation, follow the instructions in the [System Administration Guide][storm-sysadmin-guide].

##### Upgrade

- [Upgrade from StoRM v1.11.16][upgrade-from-16]
- [Upgrade from earlier versions][upgrade-from-15]

##### Upgrade StoRM repository

Starting from StoRM v1.11.14 the production repository has been migrated.
In addiction, beta and nightly yum repositories have been created.

Read how to install/upgrade StoRM repositories into the [Downloads][downloads-page] section.

[downloads-page]: {{site.baseurl}}/download.html#stable-releases

[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide
[upgrade-from-16]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.17/#upgrading
[upgrade-from-15]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.17/#upgrading-earlier

[webdav-tpc-aliases]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.17#important2
[webdav-pool-list]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.17#important3
