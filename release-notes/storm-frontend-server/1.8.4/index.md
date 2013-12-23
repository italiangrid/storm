---
layout: default
title: "StoRM FrontEnd v. 1.8.4 release notes"
release_date: 09.12.2013
rfcs:
- id: STOR-431
  title: File /etc/sysconfig/storm-frontend-server is replaced by yum
- id: STOR-368
  title: StoRM frontend crashes when SSL connection errors are raised by argus pep_client library
- id: STOR-217
  title: Service crashes on status of async operations
---

## StoRM Frontend v. 1.8.4

Released on **{{ page.release_date }}** with [StoRM v. 1.11.3]({{ site.baseurl }}/release-notes/StoRM-v1.11.3.html).

### Description

This release provides fixes for security vulnerabilities that were recently reported, and a few bug fixes.

<span class="label label-info">Important</span> A security authentication token is now used
to secure all communication among the storm-frontend and grihttps services and the 
backend. The token is configured using the `STORM_BE_XMLRPC_TOKEN` YAIM variable for
the three services. More details in the [System administrator guide][storm-sysadmin-guide].

### Security vulnerabilities

More information concerning the security vulnerabilities addressed by this release are going to be published when appropriate at [this URL](https://wiki.egi.eu/wiki/SVG:Advisory-SVG-2012-4598).

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Admininistration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

### Known issues

None at the moment

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.3
