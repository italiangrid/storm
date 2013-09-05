---
layout: default
title: "StoRM v.1.11.2 - release notes"
release_date: 05.09.2013
rfcs:
    - id: STOR-306
      title: StoRM returns NULL fileSize for ptp with expected size
    - id: STOR-305
      title: srmReleaseFiles doesn't release multiple files at once
    - id: STOR-303
      title: StoRM creates too many threads
    - id: STOR-304
      title: Slow db queries makes transfer operations latency increase
    - id: STOR-331
      title: StoRM returns wrong filesize in PtG
    - id: STOR-265
      title: StoRM Gridhttps doesn't register itself to start at boottime
    - id: STOR-259
      title: StoRM native libs call to change_group_ownership now correctly forwards exceptions to the parent java process
    - id: STOR-257
      title: Unable to change STORM_USER via yaim setup of StoRM
    - id: STOR-250
      title: StoRM GPFS get_fileset_quota_info now doesn't leak more file descriptors
    - id: STOR-235
      title: YAIM StoRM does not provide a way to configure the XML-RPC service port
    - id: STOR-314
      title: PutDone on multiple files fails all the SURLs after the first specified
    - id: STOR-323
      title: GPFS Quota computation deadlock
    - id: STOR-295
      title: StoRM does not include timestamp when logging exceptions to stderr log
    - id: STOR-315
      title: Fix how StoRM uses checksum
    - id: STOR-103
      title: StoRM publishes a wrong GLUE2EndpointServingState in one of the two GLUE2Endpoint
    - id: STOR-308
      title: srmMv returns SRM_SUCCESS instead of SRM_FILE_BUSY
    - id: STOR-237
      title: StoRM BackEnd fails during bootstrap because welcome.txt file is not found
    - id: STOR-322
      title: StoRM GridHTTPs server doesn't need to request a TURL from the BE during transfer requests
components:
    - name: StoRM Backend
      package: storm-backend-server
      version: 1.11.2
    - name: StoRM Frontend
      package: storm-frontend-server
      version: 1.8.3
    - name: StoRM GridHTTPs
      package: storm-gridhttps-server
      version: 2.0.2
    - name: StoRM native libs
      package: storm-native-libs
      version: 1.0.2
    - name: YAIM StoRM
      package: yaim-storm
      version: 4.3.3
---

## StoRM v. 1.11.2

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

### Known issues

None at the moment

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.2
