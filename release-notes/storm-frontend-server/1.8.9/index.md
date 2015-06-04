---
layout: default
title: "StoRM Frontend v.1.8.9 release notes"
release_date: "29.05.2015"
rfcs:
- id: STOR-817
  title: A call to SRM Copy on StoRM endpoint should return SRM_NOT_SUPPORTED
- id: STOR-818
<<<<<<< HEAD
  title: StoRM Frontend shutdown should be faster
=======
  title: storm frontend shutdown should be faster
>>>>>>> bbe4a35dbfe47f1427a1e92c522e3ed744250cb6
---

## StoRM Frontend v.1.8.9

Released on **{{ page.release_date }}** with [StoRM v. 1.11.9]({{ site.baseurl }}/release-notes/StoRM-v1.11.9.html).

### Description

StoRM Frontend now correctly returns SRM\_NOT\_SUPPORTED for srmCopy and srmStatusOfCopy requests.
The frontend shutdown procedure has been tweaked to take less time.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].


[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.9
