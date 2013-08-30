---
layout: default
title: "StoRM GridHTTPs v. 2.0.2 release notes"
release_date: 02.09.2013
rfcs:
    - id: STOR-315
      title: Fix how StoRM uses checksum
features:
    - id: STOR-322
      title: StoRM GridHTTPs server doesn't need to request a TURL from the BE during transfer requests
---

## StoRM GridHTTPs v. 2.0.2

Released on **{{ page.release_date }}**

### Description

This release provides several bug fixes.

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Admininistration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

### Known issues

None at the moment

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.2
