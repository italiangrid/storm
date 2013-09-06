---
layout: default
title: "StoRM GridHTTPs v. 2.0.1 release notes"
release_date: "03.06.2013"
rfcs:
   - id: STOR-109
     title: Ensure StoRM services RPM packaging requires OpenJDK.
features:
    - id: STOR-230
      title: Improve PROPFIND performance.
---

## StoRM GridHTTPs v. 2.0.1

Released on **{{ page.release_date }}** with [StoRM v. 1.11.1]({{ site.baseurl }}/release-notes/StoRM-v1.11.1.html).

### Description

This release fixes the missed OpenJDK v1.6 package requirement and improves the PROPFIND method's performance by implementing a new interface provided by Milton's developer, useful to optimize the number of srmLs done.

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Admininistration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

### Known issues

None at the moment

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.1