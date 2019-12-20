---
layout: default
title: StoRM v. 1.11.10 release notes
release_date: 22.01.2016
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
    platforms:
      - centos5
      - centos6
  - name: StoRM WebDAV
    package: storm-webdav
    version: 1.0.4
    platforms:
      - centos5
      - centos6
  - name: StoRM GridHTTPs Server
    package: storm-gridhttps-server
    version: 3.0.4
    platforms:
      - centos5
      - centos6
  - name: StoRM Info Provider
    package: storm-dynamic-info-provider
    version: 1.7.9
    platforms:
      - centos5
      - centos6
---

## StoRM v. 1.11.10

Released on **{{ page.release_date }}**.

Supported platforms: <span class="label label-warning">CentOS 5</span> <span class="label label-success">CentOS 6</span>

#### Description

This release provides fixes and improvements as can be seen from the individual components release notes.
<br/>
It's **HIGHLY RECOMMENDED** to upgrade your installation to the version of StoRM WebDAV included in this release, 
that provides fixes for a security vulnerability affecting the Milton webdav library.

#### Released components

{% include list-components.liquid %}

#### Bug fixes

{% include list-rfcs.liquid %}

#### Enhancements

{% include list-features.liquid %}

#### Upgrade from earlier StoRM versions

Re-configure the info provider service as follow.

Launch configure command:

    /usr/libexec/storm-info-provider configure

and restart the involved services and the BDII:

    service storm-backend-server restart
    service storm-webdav restart
    service bdii restart

Alternatively, you can simply run YAIM.

#### Clean install

Follow the instructions in the [System Administration Guide][storm-sysadmin-guide].

[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.10
