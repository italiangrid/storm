---
layout: default
title: "StoRM v.1.11.9 - release notes"
release_date: "29.05.2015"
rfcs:
- id: STOR-280
  title: used-space.ini can be used to initialize quota managed storage areas
- id: STOR-471
  title: Streamline LCAS/LCMAPS configuration for the StoRM Backend and gridftp
- id: STOR-782
  title: Check storm-info-provider exit code in order to block yaim execution in case of error
- id: STOR-789
  title: Make ntp.conf creation/overwrite optional
- id: STOR-792
  title: Avoid du on quota-enabled storage areas at Backend launch
- id: STOR-794
  title: Missing glite-info-provider-service dependency
- id: STOR-795
  title: Storage area matching fails with COPY/MOVE
- id: STOR-817
  title: A call to SRM Copy on StoRM endpoint should return SRM_NOT_SUPPORTED
components:
    - name: StoRM Backend
      package: storm-backend-server
      version: 1.11.9
    - name: StoRM Frontend
      package: storm-frontend-server
      version: 1.8.9
    - name: StoRM WebDAV
      package: storm-webdav
      version: 1.0.3
    - name: StoRM Info Provider
      package: storm-dynamic-info-provider
      version: 1.7.8-2
    - name: YAIM StoRM
      package: yaim-storm
      version: 4.3.8
    - name: StoRM Native Libs
      package: storm-native-libs
      version: 1.0.4
---

## StoRM v. 1.11.9

Released on **{{ page.release_date }}**

### Description

This release provides fixes and improvements as can be seen from the
individual components release notes.

### Released components

{% include list-components.liquid %}

### Bug fixes and improvements

{% include list-rfcs.liquid %}

### Installation and configuration

This release doesn't require a YAIM reconfiguration. The updated services must
be restarted:

```bash
service storm-backend-server restart
service storm-frontend-server restart
service storm-webdav restart
```

You can find more information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.9
