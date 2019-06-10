---
layout: default
title: "StoRM v.1.11.16 - release notes"
release_date: "15.06.2019"
rfcs:
  - id: STOR-1095
    title: StoRM WebDAV default configuration should not depend on iam-test.indigo-datacloud.eu
  - id: STOR-1096
    title: Unreachable OpenID Connect provider causes StoRM WebDAV startup failure
features:
  - id: STOR-1094
    title: Support for the CKSUM GridFTP command
  - id: STOR-1097
    title: Introduce Conscrypt JSSE provider support
  - id: STOR-1098
    title: Update spring boot to 2.1.4.RELEASE
components:
  - name: StoRM Backend
    package: storm-backend-server
    version: 1.11.16
  - name: StoRM WebDAV
    package: storm-webdav
    version: 1.2.0
  - name: StoRM GridFTP
    package: storm-gridftp-dsi
    version: 1.2.2
---

## StoRM v. 1.11.16

Released on **{{ page.release_date }}**.

Supported platforms: <span class="label label-success">CentOS 6</span>

#### Description

This release:

*  introduces the support for the CKSUM command, so that an ADLER32 checksum is
returned if already known for a file, or computed on the fly and stored in an
extended attribute;
* (other webdav stuff)

#### Released components

{% include list-components.liquid %}

#### Bug fixes

{% include list-rfcs.liquid %}

#### Enhancements

{% include list-features.liquid %}

#### Installation

In case of a clean installation, follow the instructions in the [System Administration Guide][storm-sysadmin-guide].

##### Upgrade

- [Upgrade from StoRM v1.11.15][upgrade-from-15]
- [Upgrade from earlier versions][upgrade-from-14]

##### Upgrade StoRM repository

Starting from StoRM v1.11.14 the production repository has been migrated.
In addiction, beta and nightly yum repositories have been created.

Read how to install/upgrade StoRM repositories into the [Downloads][downloads-page] section.

##### Upgrade to UMD-4

UMD-3 repositories are currently EOL so **we encourage to use UMD-4**. Read the upgrade instructions **[here][umd-repos]**.

[umd-4-page]: http://repository.egi.eu/category/umd_releases/distribution/umd-4

[how-to-json-report]: {{site.baseurl}}/documentation/how-to/how-to-publish-json-report/
[downloads-page]: {{site.baseurl}}/download.html#stable-releases

[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide
[umd-repos]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.16/#umdrepos
[gc-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.16/#requestsgarbagecollector
[upgrade-from-15]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.16/#upgrading
[upgrade-from-14]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.16/#upgrading-earlier

[webdav-tpc-aliases]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.16#important2
[webdav-pool-list]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.16#important3
