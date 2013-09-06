---
layout: default
title: "YAIM StoRM v.4.3.3 release notes"
release_date: 05.09.2013
rfcs:
    - id: STOR-103
      title: StoRM publishes a wrong GLUE2EndpointServingState in one of the two GLUE2Endpoint
    - id: STOR-235
      title: YAIM StoRM does not provide a way to configure the XML-RPC service port
    - id: STOR-257
      title: Unable to change STORM_USER via yaim setup of StoRM
    - id: STOR-315
      title: Fix how StoRM uses checksum
---

## YAIM StoRM v. 4.3.3

Released on **{{ page.release_date }}** with [StoRM v. 1.11.2]({{ site.baseurl }}/release-notes/StoRM-v1.11.2.html).

### Description

This release provides several bug fixes.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Admininistration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

### Known issues

None at the moment

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.2
