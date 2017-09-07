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

* requires Java 8;
* fixes the insertion of a file tape recall task in the [recall task management REST API][recall-task-rest-api];
* fixes a couple of problems related to namespace and storage area resolution;
* fixes a bug that causes sanity check failures with GPFS version >= 4.2.2;
* adds a [REST API][metadata-rest-api] to query metadata (online/offline status, checksum, etc.)
    about filesystem resources managed by StoRM. The main client of this API is the INDIGO-Datacloud [CDMI StoRM plugin][cdmi-storm-plugin];
* removes the deprecated storm-gridhttps-plugin.

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
[recall-task-rest-api]: https://github.com/italiangrid/storm/tree/develop/src/main/java/it/grid/storm/tape/recalltable/resources
[metadata-rest-api]: https://github.com/italiangrid/storm/tree/develop/src/main/java/it/grid/storm/rest/metadata
[cdmi-storm-plugin]: {{site.baseurl}}/release-notes/cdmi-storm/0.1.0/

