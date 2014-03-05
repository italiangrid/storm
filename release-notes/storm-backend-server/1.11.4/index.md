---
layout: default
title: "StoRM BackEnd v. 1.11.4 release notes"
release_date: 17.03.2014
rfcs:
- id: STOR-307
  title: StoRM returns SRM_INVALID_PATH instead of SRM_AUTHORIZATION_FAILURE
- id: STOR-501
  title: StoRM Backend fails to map the correct VFS when storage-area accesspoints are nested
- id: STOR-505
  title: StoRM does not correctly prefix SURLs when multiple mapping rules are defined for a storage area
- id: STOR-515
  title: Improved Space Helper messaging
- id: STOR-520
  title: If 'logging.xml' file is malformed, during bootstrap phase, the Backend dies without writing the parsing error on stderr log file
features:
- id: STOR-524
  title: Proper use of string formatting in log calls
- id: STOR-560
  title: PtG ACL setup should be configurable
---

## StoRM Backend v. 1.11.4

Released on **{{ page.release_date }}** with [StoRM v. 1.11.4]({{ site.baseurl }}/release-notes/StoRM-v1.11.4.html).

### Description

This release provides several bug fixes.

<span class="label label-info">Important</span> From StoRM v1.11.3 a security authentication token is used
to secure all communication among the storm-frontend and grihttps services and the 
backend. The token is configured using the `STORM_BE_XMLRPC_TOKEN` YAIM variable for
the three services. More details in the [System administrator guide][storm-sysadmin-guide].

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Admininistration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

### Known issues

None at the moment

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.4
