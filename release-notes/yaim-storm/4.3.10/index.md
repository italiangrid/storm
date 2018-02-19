---
layout: default
title: "YAIM StoRM v.4.3.10 release notes"
release_date: "19.02.2018"
rfcs:
  - id: STOR-951
    title: Pool account mapping fails since lcmaps-plugins-basic v1.6.4
---

## YAIM StoRM v. 4.3.10

Released on **{{ page.release_date }}** with [StoRM v. 1.11.13]({{ site.baseurl }}/release-notes/StoRM-v1.11.13.html).

### Description

This release fixes pool account mapping failure that happens with lcmaps-plugins-basic versions greater than 1.6.3, by setting storm user as the owner of the gridmap directory.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

Update package and relaunch YAIM configuration:

    $ yum update yaim-storm
    $ /opt/glite/yaim/bin/yaim -c -s /etc/storm/siteinfo/storm.def -n se_storm_backend

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Administration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.13
