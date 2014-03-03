---
layout: default
title: "YAIM StoRM v.4.3.5 release notes"
release_date: 17.03.2014
rfcs:
- id: STOR-506
  title: YAIM-StoRM does not support configuring multiple access points for a given storage area
- id: STOR-561
  title: YAIM does not setup STORM_GRIDHTTPS_USER environment variable
features:
- id: STOR-504
  title: StoRM should publish information only about VOs which have configured storage areas
- id: STOR-560
  title: PtG ACL setup should be configurable
---

## YAIM StoRM v. 4.3.5

Released on **{{ page.release_date }}** with [StoRM v. 1.11.4]({{ site.baseurl }}/release-notes/StoRM-v1.11.4.html).

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
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.4
