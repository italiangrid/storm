---
layout: default
title: "StoRM BackEnd v. 1.11.5 release notes"
release_date: 19.12.2014
rfcs:
- id: STOR-394
  title: Changes on the yaim STORM_FRONTEND_PORT variable doesn't update Frontend's configuration file
- id: STOR-459
  title: Multiple srmPtg on the same file can block StoRM Backend
- id: STOR-651
  title: StoRM incorrectly considers small files as migrated to tape on GPFS 3.5
- id: STOR-678
  title: Storage area used/free sizes stored on database are misaligned with real values
- id: STOR-668
  title: StoRM Backend doesn't create the missing directory structure of a SURL in case of a srmPrepareToPut with directory.automatic-creation enabled
features:
- id: STOR-654
  title: Refactor StoRM dynamic info provider service
- id: STOR-692
  title: Clean Backend's log file from useless INFO messages
---

## StoRM Backend v. 1.11.5

Released on **{{ page.release_date }}** with [StoRM v. 1.11.5]({{ site.baseurl }}/release-notes/StoRM-v1.11.5.html).

### Description

This release provides several bug fixes.

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Admininistration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

### Known issues

None at the moment

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.5
