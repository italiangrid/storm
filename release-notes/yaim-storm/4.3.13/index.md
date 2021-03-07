---
layout: default
title: "YAIM StoRM v.4.3.13 release notes"
release_date: "07.08.2020"
rfcs:
  - id: STOR-1252
    title: Align the namespace.xml generated via YAIM StoRM with the one produced by the Puppet module
---

## YAIM StoRM v. 4.3.12

Released on **{{ page.release_date }}** with [StoRM v. 1.11.18][release-notes].
<span style="float: right; margin-left: 8px;" class="label label-success">centos6</span>

### Description

This release provides an internal fix and fixes some typos.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

Update package and relaunch YAIM configuration:

    $ yum update yaim-storm
    $ /opt/glite/yaim/bin/yaim -c -s /etc/storm/siteinfo/storm.def -n se_storm_backend

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide] of
the [Documentation][storm-documentation] section.

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.18.html
[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.18
