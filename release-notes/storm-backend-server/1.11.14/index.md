---
layout: default
title: "StoRM BackEnd v. 1.11.14 release notes"
release_date: "25.07.2018"
rfcs:
  - id: STOR-984
    title: SrmRm does not clean state correctly
---

## StoRM Backend v. 1.11.14

Released on **{{ page.release_date }}** with [StoRM v. 1.11.14][release-notes].

### Description

This release fixes srmRm by adding a missing abort of all the active srmPtP or srmPtP requests when the target file is not found on disk.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

Read carefully the following [instructions][upgrading] before updating, expecially from StoRM v1.11.11 and earlier.

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.14.html
[upgrading]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.14/#upgrading
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.14