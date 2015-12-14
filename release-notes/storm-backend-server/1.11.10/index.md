---
layout: default
title: "StoRM BackEnd v. 1.11.10 release notes"
release_date: "18.12.2015"
rfcs:
- id: STOR-234
  title: Storm BE does not manage correctly abort requests of expired tokens
- id: STOR-835
  title: Improper management of SURL status can lead to PutDone errors and locked SURLs
---

## StoRM Backend v. 1.11.10

Released on **{{ page.release_date }}** with [StoRM v. 1.11.10]({{ site.baseurl }}/release-notes/StoRM-v1.11.10.html).

### Description

This release fixes a minor issue related to the retrieved error message in case of a srmAbort done specifying an expired token.

### Bug fixes and improvements

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/
