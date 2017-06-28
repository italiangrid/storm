---
layout: default
title: "YAIM StoRM v.4.3.9 release notes"
release_date: "30.06.2017"
features:
  - id: STOR-946
    title: Remove storm-gridhttps-plugin configuration
---

## YAIM StoRM v. 4.3.9

Released on **{{ page.release_date }}** with [StoRM v. 1.11.12]({{ site.baseurl }}/release-notes/StoRM-v1.11.12.html).

### Description

This release contains the necessary code fixes due the removal of all the _storm-gridhttps-plugin_ related stuff.

If used to configure the dismissed _storm-gridhttps-server_ component, now it requires `STORM_GRIDHTTPS_USER` to be equal to `STORM_USER`.

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Administration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.12
