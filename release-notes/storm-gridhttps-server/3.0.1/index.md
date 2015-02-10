---
layout: default
title: "StoRM GridHTTPs v. 3.0.1 release notes"
release_date: 23.05.2014
rfcs:
- id: STOR-479
  title: Associate properly the WebDAV returned code to the type of SRMOperationException
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
- id: STOR-519
  title: The Gridhttps should allow to specify on which address it should listen
features:
- id: STOR-524
  title: Proper use of string formatting in log calls
---

## StoRM GridHTTPs v. 3.0.1

Released on **{{ page.release_date }}** with [StoRM v. 1.11.4]({{ site.baseurl }}/release-notes/StoRM-v1.11.4.html).

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
