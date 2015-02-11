---
layout: default
title: "YAIM StoRM v.4.3.7 release notes"
release_date: "09.02.2015"
rfcs:
- id: STOR-652
  title: yaim-storm asks for a mandatory variable even if it's been defined
- id: STOR-701
  title: StoRM should be able to serve ptg requests for the "xroot" protocol
---

## YAIM StoRM v. 4.3.7

Released on **{{ page.release_date }}** with [StoRM v. 1.11.7]({{ site.baseurl }}/release-notes/StoRM-v1.11.7.html).

### Description

This release provides the ability to support xroot as transfer protocol.
In order to enable this protocol alias, a YAIM reconfiguration of the backend
service is required.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Administration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.


[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.7
