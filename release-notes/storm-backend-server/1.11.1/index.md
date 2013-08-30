---
layout: default
title: "StoRM BackEnd v. 1.11.1 release notes"
release_date: "03.06.2013"
rfcs:
   - id: STOR-172
     title: StoRM now correctly publishes information about storage area sizes in the information system.
   - id: STOR-148
     title: StoRM now leverages quota limits information gathered from the underlying GPFS file-system to compute a storage area size
   - id: STOR-10
     title: StoRM now gets quota information directly from GPFS file-system
   - id: STOR-117
     title: Duplicate prepare-to-get calls on a SURL are now correctly handled
   - id: STOR-109
     title: The Java JDK dependency has been fixed so that all StoRM packages explicitly requires OpenJDK.
---

## StoRM BackEnd v. 1.11.1

Released on **{{ page.release_date }}**

### Description

This release provides several bug fixes.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Admininistration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

### Known issues

None at the moment

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.1

