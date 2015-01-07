---
layout: default
title: "StoRM v.1.11.5 - release notes"
release_date: "07.01.2015"
rfcs:
- id: STOR-320
  title: Storm publishes inconsistent storage capacity information
- id: STOR-394
  title: Changes on the yaim STORM_FRONTEND_PORT variable doesn't update Frontend's configuration file
- id: STOR-419
  title: StoRM Native Libs LCMAPS package should include dependencies to the needed lcmaps plugins
- id: STOR-459
  title: Multiple srmPtg on the same file can block StoRM Backend
- id: STOR-605
  title: StoRM frontend leaks memory when Argus callout is enabled
- id: STOR-607
  title: WebDAV partial GET requests fill up disk with temporary files
- id: STOR-609
  title: StoRM doesn't publish on BDII according to the EGI profile for GLUE 2.0
- id: STOR-651
  title: StoRM incorrectly considers small files as migrated to tape on GPFS 3.5
- id: STOR-668
  title: StoRM Backend doesn't create the missing directory structure of a SURL in case of a srmPrepareToPut with directory.automatic-creation enabled
- id: STOR-671
  title: StoRM publishes inconsistent values due to an approximation problem
- id: STOR-678
  title: Storage area used/free sizes stored on db are misaligned with real values
features:
- id: STOR-654
  title: Refactor StoRM dynamic info provider service
- id: STOR-692
  title: Clean Backend's log file from useless INFO messages
components:
    - name: StoRM Backend
      package: storm-backend-server
      version: 1.11.5
    - name: StoRM Frontend
      package: storm-frontend-server
      version: 1.8.6
    - name: StoRM GridHTTPs
      package: storm-gridhttps-server
      version: 3.0.2
    - name: StoRM Dynamic Info Provider
      package: storm-dynamic-info-provider
      version: 1.7.7
    - name: StoRM Native Libs
      package: storm-native-libs
      version: 1.0.3
    - name: YAIM StoRM
      package: yaim-storm
      version: 4.3.6
---

## StoRM v. 1.11.5

Released on **{{ page.release_date }}**

### Description

This release provides several bug fixes.
  
### Released components

{% include list-components.liquid %}

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Admininistration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.5
