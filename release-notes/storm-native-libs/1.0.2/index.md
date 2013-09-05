---
layout: default
title: "StoRM native libs v.1.0.2 release notes"
release_date: 05.09.2013
rfcs:
    - id: STOR-259
      title: StoRM native libs call to change_group_ownership does not correctly forward exceptions to the parent java process
    - id: STOR-250
      title: StoRM GPFS get_fileset_quota_info leaks file descriptors
---

## StoRM native-libs v. 1.0.2

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
