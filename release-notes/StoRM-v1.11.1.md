---
layout: default
title: "StoRM v.1.11.1 - release notes"
release_date: "03.06.2013"
rfcs:
    - id: STOR-172
      title: StoRM publishes wrong values for storage area sizes in the information system.
    - id: STOR-148
      title: Change GPFS Quota Job to leverage native quota info call
    - id: STOR-10
      title: StoRM should get quota information directly from GPFS filesystem
    - id: STOR-130
      title: Ensure storm-gridhttps-server starts automatically on boot.
    - id: STOR-117
      title: Duplicate PtG on a file incorrectly marks it as busy
    - id: STOR-113
      title: YAIM should not try to configure user permissions on all storage areas
    - id: STOR-109
      title: Ensure StoRM services RPM packaging requires OpenJDK.
features:
    - id: STOR-230
      title: Improve PROPFIND performance.
components:
  - name: StoRM Backend
    package: storm-backend-server
    version: 1.11.1
    platforms:
      - centos5
      - centos6
  - name: StoRM native libs
    package: storm-native-libs
    version: 1.0.1
    platforms:
      - centos5
      - centos6
  - name: StoRM GridHTTPs
    package: storm-gridhttps-server
    version: 2.0.1
    platforms:
      - centos5
      - centos6
  - name: YAIM StoRM
    package: yaim-storm
    version: 4.3.2
    platforms:
      - centos5
      - centos6
  - name: StoRM dynamic info
    package: storm-dynamic-info-provider
    version: 1.7.6
    platforms:
      - centos5
      - centos6
---

## StoRM v. 1.11.1

Released on **{{ page.release_date }}**

### Description

This release provides several bug fixes. This was the first update for StoRM in EMI-3.

### Released components

{% include list-components.liquid %}

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Administration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

### Known issues

None at the moment

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.1
