---
layout: default
title: "StoRM v.1.11.14 - release notes"
release_date: "25.07.2018"
rfcs:
  - id: STOR-984
    title: SrmRm does not clean state correctly
  - id: STOR-990
    title: Native libs built vs GPFS v4.x cause BE failing on start
  - id: STOR-992	
    title: Sync file contents before setting checksum attribute
features:
  - id: STOR-989
    title: Configurable SOAP send and receive timeouts
  - id: STOR-991
    title: Update XMLRPC-C libraries to latest super stable
components:
  - name: StoRM Backend
    package: storm-backend-server
    version: 1.11.14
  - name: StoRM Frontend
    package: storm-frontend-server
    version: 1.8.11
  - name: StoRM Native Libs
    package: storm-native-libs
    version: 1.0.5-2
  - name: StoRM GridFTP
    package: storm-globus-gridftp-server
    version: 1.2.1
  - name: StoRM XMLRPC-C
    package: storm-xmlrpc-c
    version: 1.39.12
---

## StoRM v. 1.11.14

Released on **{{ page.release_date }}**.

Supported platforms: <span class="label label-success">CentOS 6</span>

#### Description

This release provides fixes to some outstanding bugs and improvements:

* it fixes the abort of on-going ptg and ptp, when the file linked to the SURL was not found during a srmRm request;
* it makes SOAP send and receive timeouts configurable;
* it fixes backend failure on start due to a malformed GPFS native libs package;
* the XMLRPC C libraries has been upgraded to latest stable version;
* it fixes the setting of checksum attribute that was incorrect in some cases.

#### Released components

{% include list-components.liquid %}

#### Bug fixes

{% include list-rfcs.liquid %}

#### Enhancements

{% include list-features.liquid %}

#### Installation

In case of a clean installation, follow the instructions in the [System Administration Guide][storm-sysadmin-guide].

##### Upgrade

- [Upgrade from StoRM v1.11.13][upgrade-from-13]
- [Upgrade from StoRM v1.11.12][upgrade-from-12]
- [Upgrade from StoRM v1.11.11][upgrade-from-11]
- [Upgrade from earlier versions][upgrade-from-old]

##### Upgrade to UMD-4

UMD-3 repositories are currently EOL so **we encourage to use UMD-4**. Read the upgrade instructions **[here][umd-repos]**.

[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide
[umd-4-page]: http://repository.egi.eu/category/umd_releases/distribution/umd-4
[umd-repos]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.14/#umdrepos
[gc-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.14/#requestsgarbagecollector
[how-to-json-report]: {{site.baseurl}}/documentation/how-to/how-to-publish-json-report/

[upgrade-from-13]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.14/#upgrade13
[upgrade-from-12]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.14/#upgrade12
[upgrade-from-11]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.14/#upgrade11
[upgrade-from-old]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.14/#upgradeold
