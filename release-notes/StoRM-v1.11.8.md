---
layout: default
title: "StoRM v.1.11.8 - release notes"
release_date: "13.03.2015"
rfcs:
- id: STOR-750
  title: Single quote in certificate subject causes failures in StoRM async requests
- id: STOR-751
  title: storm-info-provider-rpm misses dependency on python-simplejson on SL5
- id: STOR-776
  title: Inefficient SQL query for surl status checks
- id: STOR-777
  title: Inefficient query used to update SURL status when a releaseFiles is called
- id: STOR-778
  title: Improve efficiency on status PtGs
- id: STOR-779
  title: rm command does not properly abort ongoing PtP requests
- id: STOR-780
  title: StoRM Info Provider 'configure' fails with 'too many values to unpack'
components:
    - name: StoRM Backend
      package: storm-backend-server
      version: 1.11.8
    - name: StoRM Frontend
      package: storm-frontend-server
      version: 1.8.8
    - name: StoRM Info Provider
      package: storm-dynamic-info-provider
      version: 1.7.8
---

## StoRM v. 1.11.8

Released on **{{ page.release_date }}**

### Description

This release provides several bug fixes. 

### Released components

{% include list-components.liquid %}

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide] of
the [Documentation][storm-documentation] section.

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.8