---
layout: default
title: "StoRM v.1.11.3 - release notes"
release_date: "20.12.2013"
rfcs:
    - id: STOR-450
      title: StoRM Gridhttps initializes VOMS validation in an unsafe way
    - id: STOR-431
      title: File /etc/sysconfig/storm-frontend-server is replaced by yum
    - id: STOR-415
      title: Fix StoRM documentation typo
    - id: STOR-376
      title: StoRM GridHTTPs' fileTransfer and WebDAV requests on different context-paths
    - id: STOR-368
      title: StoRM frontend crashes when SSL connection errors are raised by argus pep_client library
    - id: STOR-217
      title: Service crashes on status of async operations
    - id: STOR-293
      title: During the start-up the file welcome.txt is not found
components:
    - name: StoRM Backend
      package: storm-backend-server
      version: 1.11.3
    - name: StoRM Frontend
      package: storm-frontend-server
      version: 1.8.4
    - name: StoRM GridHTTPs
      package: storm-gridhttps-server
      version: 3.0.0
    - name: YAIM StoRM
      package: yaim-storm
      version: 4.3.4
    - name: StoRM SRM client
      package: storm-srm-client
      version: 1.6.1
---

## StoRM v. 1.11.3

Released on **{{ page.release_date }}**

### Description

This release provides fixes for security vulnerabilities that were recently reported, and several bug fixes.

<span class="label label-info">Important</span> A security authentication token is now used
to secure all communication among the storm-frontend and grihttps services and the 
backend. The token is configured using the `STORM_BE_XMLRPC_TOKEN` YAIM variable for
the three services. More details in the [System administrator guide][storm-sysadmin-guide].

<span class="label label-info">Important</span> The webdav service now responds on 
the `/webdav/[storage-area]` path. More details in the [WebDAV interface guide][storm-webdav-guide]. 

<span class="label label-info">Important</span> Please ensure that storage area file permissions are 
correctly set. Follow these [instructions][file-perms-section] before running the gridhttps server.

### Released components

{% include list-components.liquid %}

### Security vulnerabilities

More information concerning the security vulnerabilities addressed by this release are going to be published when appropriate at [this URL](https://wiki.egi.eu/wiki/SVG:Advisory-SVG-2012-4598).

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Admininistration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

### Known issues

None at the moment

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.3
[file-perms-section]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.3/#sapermissions
[storm-webdav-guide]: {{site.baseurl}}/documentation/webdav-guide/3.0.0
