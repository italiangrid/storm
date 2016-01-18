---
layout: default
title: "StoRM v.1.11.10 - release notes"
release_date: "25.01.2016"
rfcs:
- id: STOR-234
  title: Storm BE does not manage correctly abort requests of expired tokens
- id: STOR-741
  title: WebDAV MOVE and COPY requests with source equal to destination fail with 412 instead of 403
- id: STOR-835
  title: Improper management of SURL status can lead to PutDone errors and locked SURLs
- id: STOR-837
  title: Missing GlueSAPath from Storage Areas BDII info
features:
- id: STOR-700
  title: Add support for RFC 3230 in StoRM WebDAV service
components:
    - name: StoRM Backend
      package: storm-backend-server
      version: 1.11.10
    - name: StoRM WebDAV
      package: storm-webdav
      version: 1.0.4
    - name: StoRM GridHTTPs server
      package: storm-gridhttps-server
      version: 3.0.4
    - name: StoRM Info Provider
      package: storm-dynamic-info-provider
      version: 1.7.9
---

## StoRM v. 1.11.10

Released on **{{ page.release_date }}**

### Description

This release provides fixes and improvements as can be seen from the
individual components release notes.

### Released components

{% include list-components.liquid %}

### Bug fixes

{% include list-rfcs.liquid %}

### Improvements

{% include list-features.liquid %}

### Installation and configuration

This release doesn't require a YAIM reconfiguration. The updated services must
be restarted:

```bash
service storm-backend-server restart
service storm-webdav restart
/usr/libexec/storm-info-provider configure
```

The restart of *storm-gridhttps-server* has not been reported cause it's highly recommended to update it to *storm-webdav* component.

You can find more information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/
