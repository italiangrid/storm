---
layout: default
title: "YAIM StoRM v.4.3.11 release notes"
release_date: "25.07.2018"
rfcs:
  - id: STOR-1017
    title: The gridmapdir owner is not set using STORM_USER configuration variable
---

## YAIM StoRM v. 4.3.11

Released on **{{ page.release_date }}** with [StoRM v. 1.11.14]({{ site.baseurl }}/release-notes/StoRM-v1.11.14.html).

### Description

This release fixes pool account mapping failure when a user different from `storm` is used.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

Update package and relaunch YAIM configuration:

    $ yum update yaim-storm
    $ /opt/glite/yaim/bin/yaim -c -s /etc/storm/siteinfo/storm.def -n se_storm_backend

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Administration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.14
