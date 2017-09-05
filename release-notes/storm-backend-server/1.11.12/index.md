---
layout: default
title: "StoRM BackEnd v. 1.11.12 release notes"
release_date: "01.09.2017"
rfcs:
  - id: STOR-282
    title: Fix overlapping virtual filesystems error in StoRI children creation
  - id: STOR-898
    title: Storage-area resolution fails on moving resources through different storage-areas
  - id: STOR-925
    title: GPFS drops dev prefix in mtab causing StoRM backend sanity check to fail
  - id: STOR-929
    title: Fix StoRM Recall Interface
features:
  - id: STOR-441
    title: Migrate RESTFul services to Jersey 2.x
  - id: STOR-930
    title: Add Metadata Endpoint
  - id: STOR-945
    title: Move to Java 1.8
  - id: STOR-946
    title: Remove storm-gridhttps-plugin configuration
---

## StoRM Backend v. 1.11.12

Released on **{{ page.release_date }}** with [StoRM v. 1.11.12]({{ site.baseurl }}/release-notes/StoRM-v1.11.12.html).

### Description

This release provides fixes to some outstanding bugs and improvements:

* fixes/adds the insert of a recall task through the REST interface (more info [here](https://github.com/italiangrid/storm/tree/develop/src/main/java/it/grid/storm/tape/recalltable/resources));
* fixes a minor bug on the virtual filesystem returned during StoRI children creation;
* fixes a minor bug on the storage-area resolution when moving resources through different storage-areas;
* fixes bug that makes sanity check failing with latest GPFS versions;
* adds a REST metadata endpoint (more info [here](https://github.com/italiangrid/storm/tree/develop/src/main/java/it/grid/storm/rest/metadata));
* requires Java 8;
* removes all the unused stuff about the dismissed storm-gridhttps-plugin.

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

This release **requires Java 8** and dismisses the use of gridhttps-plugin component that **must be removed**.<br/>
So, please, **read carefully the [installation notes][upgrading]**.

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[upgrading]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.12/#upgrading
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.12
