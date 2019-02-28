---
layout: default
title: "StoRM BackEnd v. 1.11.15 release notes"
release_date: "28.02.2019"
rfcs:
  - id: STOR-1026
    title: Backend does not honor XMLRPC maxThreads settings
features:
  - id: STOR-1020
    title: Remove functions and code related to old and deprecated variables
  - id: STOR-1028
    title: Ged rid of the storm-backend command server
---

## StoRM Backend v. 1.11.15

Released on **{{ page.release_date }}** with [StoRM v. 1.11.15][release-notes].

### Description

This release:

* fixes the `maxThread` Backend setting which wasn't honored as expected;
* improves Backend's startup thanks to a refactoring of the init scripts
(read more [here][upgrading-trouble] about troubleshooting);
* clears old code and stuff related to deprecated variables.


### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

Read carefully the following [instructions][upgrading] before updating,
especially if you're updating from StoRM v1.11.11 or earlier versions.

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.15.html
[upgrading]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15/#upgrading
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15

[upgrading-trouble]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15#important1
