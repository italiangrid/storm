---
layout: default
title: "StoRM v.1.11.15 - release notes"
release_date: "28.02.2019"
rfcs:
  - id: STOR-1019
    title: Fix namespace configuration syntax error
  - id: STOR-1025
    title: Configurable size limit for the request queue
  - id: STOR-1026
    title: Backend does not honor XMLRPC maxThreads settings
  - id: STOR-1037
    title: The published WebDAV endpoint ends with /webdav which is obsolete and broken without an ending slash
features:
  - id: STOR-844
    title: Change the way info-provider knows if a webdav endpoint must be published
  - id: STOR-1018
    title: Support for third-party copy in StoRM WebDAV service
  - id: STOR-1020
    title: Remove functions and code related to old and deprecated variables
  - id: STOR-1028
    title: Ged rid of the storm-backend command server
  - id: STOR-1029
    title: Address frontend compilation warnings
  - id: STOR-1038
    title: YAIM should exit with error in case StoRM Backend daemon start fails
  - id: STOR-1039
    title: Implement suggested changes in JSON report file
  - id: STOR-1067
    title: Make YAIM able to configure storm-webdav organizations
components:
  - name: StoRM Backend
    package: storm-backend-server
    version: 1.11.15
  - name: StoRM Frontend
    package: storm-frontend-server
    version: 1.8.12
  - name: StoRM Info Provider
    package: storm-dynamic-info-provider
    version: 1.8.1
  - name: StoRM WebDAV
    package: storm-webdav
    version: 1.1.0
  - name: YAIM StoRM
    package: yaim-storm
    version: 4.3.12
---

## StoRM v. 1.11.15

Released on **{{ page.release_date }}**.

Supported platforms: <span class="label label-success">CentOS 6</span>

#### Description

This release:

* implements proper limits for the incoming requests of frontend;
* fixes the `synchcall.xmlrpc.maxthread` setting for XMLRPC requests;
* improves backend's startup logic thanks to a refactoring of the init scripts;
* allows to specify multiple WebDAV endpoints to be published through the info
provider, by using a new YAIM variable `STORM_WEBDAV_POOL_LIST`
(read more [here][webdav-pool-list]);
* fixes minor issues on YAIM and storm-info-provider;
* implements basic support for Third-Party-Copy in the StoRM WebDAV service
(read more [here][webdav-tpc-aliases]);

#### Released components

{% include list-components.liquid %}

#### Bug fixes

{% include list-rfcs.liquid %}

#### Enhancements

{% include list-features.liquid %}

#### Installation

In case of a clean installation, follow the instructions in the [System Administration Guide][storm-sysadmin-guide].

##### Upgrade

- [Upgrade from StoRM v1.11.14][upgrade-from-14]
- [Upgrade from StoRM v1.11.13][upgrade-from-13]
- [Upgrade from StoRM v1.11.12][upgrade-from-12]
- [Upgrade from StoRM v1.11.11][upgrade-from-11]
- [Upgrade from earlier versions][upgrade-from-old]

##### Upgrade StoRM repository

Starting from StoRM v1.11.14 the production repository has been migrated.
In addiction, beta and nightly yum repositories have been created.

Read how to install/upgrade StoRM repositories into the [Downloads][downloads-page] section.

##### Upgrade to UMD-4

UMD-3 repositories are currently EOL so **we encourage to use UMD-4**. Read the upgrade instructions **[here][umd-repos]**.

[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide
[umd-4-page]: http://repository.egi.eu/category/umd_releases/distribution/umd-4
[umd-repos]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15/#umdrepos
[gc-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15/#requestsgarbagecollector
[how-to-json-report]: {{site.baseurl}}/documentation/how-to/how-to-publish-json-report/
[downloads-page]: {{site.baseurl}}/download.html#stable-releases

[upgrade-from-14]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15/#upgrade14
[upgrade-from-13]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15/#upgrade13
[upgrade-from-12]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15/#upgrade12
[upgrade-from-11]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15/#upgrade11
[upgrade-from-old]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15/#upgradeold

[webdav-tpc-aliases]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15#important2
[webdav-pool-list]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15#important3
