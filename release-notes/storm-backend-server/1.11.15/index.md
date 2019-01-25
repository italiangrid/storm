---
layout: default
title: "StoRM BackEnd v. 1.11.15 release notes"
release_date: "03.12.2018"
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

This release mainly improves the service start-up with a significative refactor
of scripts and code. During the update the service will be restarted. This
release also fixes the configuration of the thread limit, set by `maxThreads`
property and removes a lot of unused code and stuff related to deprecated YAIM
variables.

### Bug fixes

{% include list-rfcs.liquid %}

#### Enhancements

{% include list-features.liquid %}

### Installation and configuration

Read carefully the following [instructions][upgrading] before updating,
especially if you're updating from StoRM v1.11.11 or earlier versions.

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.15.html
[upgrading]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15/#upgrading
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15
