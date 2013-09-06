---
layout: default
title: "StoRM BackEnd v. 1.11.2 release notes"
release_date: 06.09.2013
rfcs:
 - id: STOR-305
   title: srmReleaseFiles doesn't release multiple files at once
 - id: STOR-303
   title: StoRM creates too many threads
 - id: STOR-304
   title: Slow db queries makes transfer operations latency increase
 - id: STOR-331
   title: StoRM returns wrong filesize in PtG
 - id: STOR-257
   title: Unable to change STORM_USER via yaim setup of StoRM
 - id: STOR-314
   title: PutDone on multiple files fails all the SURLs after the first specified
 - id: STOR-323
   title: GPFS Quota computation deadlock
 - id: STOR-295
   title: StoRM does not include timestamp when logging exceptions to stderr log
 - id: STOR-308
   title: srmMv returns SRM_SUCCESS instead of SRM_FILE_BUSY
 - id: STOR-237
   title: StoRM BackEnd fails during bootstrap because welcome.txt file is not found
---

## StoRM BackEnd v. 1.11.2

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

