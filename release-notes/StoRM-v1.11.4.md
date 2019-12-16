---
layout: default
title: "StoRM v.1.11.4 - release notes"
release_date: "23.05.2014"
rfcs:
- id: STOR-256
  title: StoRM doesn't survive an update of MySQL
- id: STOR-307
  title: StoRM returns SRM_INVALID_PATH instead of SRM_AUTHORIZATION_FAILURE
- id: STOR-479
  title: Associate properly the WebDAV returned code to the type of SRMOperationException
- id: STOR-500
  title: StoRM monitoring.log no longer updated in 1.11.3
- id: STOR-501
  title: StoRM Backend fails to map the correct VFS when storage-area accesspoints are nested
- id: STOR-505
  title: StoRM does not correctly prefix SURLs when multiple mapping rules are defined for a storage area
- id: STOR-506
  title: YAIM-StoRM does not support configuring multiple access points for a given storage area
- id: STOR-509
  title: WebDAV MKCOL response is 503 instead of 403 for non-anonymous unauthorized requests
- id: STOR-510
  title: WebDAV DELETE response is 503 instead of 403 for non-anonymous unauthorized requests
- id: STOR-511
  title: WebDAV PUT response is 503 instead of 403 for non-anonymous unauthorized requests
- id: STOR-512
  title: WebDAV MOVE response is 503 instead of 403 for non-anonymous unauthorized requests
- id: STOR-513
  title: WebDAV COPY response is 503 instead of 403 for non-anonymous unauthorized requests
- id: STOR-514
  title: WebDAV PROPFIND response is 503 instead of 403 for non-anonymous unauthorized requests
- id: STOR-515
  title: Improved Space Helper messaging
- id: STOR-519
  title: The Gridhttps should allow to specify on which address it should listen
- id: STOR-520
  title: If 'logging.xml' file is malformed, during bootstrap phase, the Backend dies without writing the parsing error on stderr log file
- id: STOR-561
  title: YAIM does not setup STORM_GRIDHTTPS_USER environment variable
- id: STOR-598
  title: heartbeat.log average duration logged in microsec instead of millisec
- id: STOR-602
  title: Synchronous PTG or PTP are logged twice into heartbeat.log
features:
- id: STOR-504
  title: StoRM should publish information only about VOs which have configured storage areas
- id: STOR-524
  title: Proper use of string formatting in log calls
- id: STOR-560
  title: PtG ACL setup should be configurable
components:
  - name: StoRM Backend
    package: storm-backend-server
    version: 1.11.4
    platforms:
      - centos5
      - centos6
  - name: StoRM Frontend
    package: storm-frontend-server
    version: 1.8.5
    platforms:
      - centos5
      - centos6
  - name: StoRM GridHTTPs
    package: storm-gridhttps-server
    version: 3.0.1
    platforms:
      - centos5
      - centos6
  - name: YAIM StoRM
    package: yaim-storm
    version: 4.3.5
    platforms:
      - centos5
      - centos6
---

## StoRM v. 1.11.4

Released on **{{ page.release_date }}**

### Description

This release provides several bug fixes.
  
<span class="label label-info">Important</span> Since version 1.11.3 the WebDAV service responds on 
the `/webdav/[storage-area]` path. More details in the [WebDAV interface guide][storm-webdav-guide]. 

<span class="label label-info">Important</span> Since version 1.11.3 a security authentication token is used
to secure all communication among the storm-frontend and grihttps services and the 
backend. The token is configured using the `STORM_BE_XMLRPC_TOKEN` YAIM variable for
the three services. More details in the [System administrator guide][storm-sysadmin-guide].

<span class="label label-info">Important</span> Please ensure that storage area file permissions are 
correctly set. Follow these [instructions][file-perms-section] before running the gridhttps server.

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
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.4
[file-perms-section]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.4/#sapermissions
[storm-webdav-guide]: {{site.baseurl}}/documentation/webdav-guide/3.0.1
