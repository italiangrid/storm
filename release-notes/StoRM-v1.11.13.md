---
layout: default
title: "StoRM v.1.11.13 - release notes"
release_date: "14.02.2018"
rfcs:
  - id: STOR-950
    title: Failure on updating recall task status
  - id: STOR-951
    title: Pool account mapping fails since lcmaps-plugins-basic v1.6.4
  - id: STOR-955
    title: Garbage Collector ignore timestamps on cleaning recall tasks
features:
  - id: STOR-954
    title: StoRM backend should garbage collect requests that are stuck in SRM_IN_PROGRESS for a configurable amount of time
  - id: STOR-982
    title: JSON storage usage reporting
components:
  - name: StoRM Backend
    package: storm-backend-server
    version: 1.11.13
  - name: YAIM StoRM
    package: yaim-storm
    version: 4.3.10
  - name: StoRM Info Provider
    package: storm-dynamic-info-provider
    version: 1.8.0
---

## StoRM v. 1.11.13

Released on **{{ page.release_date }}**.

Supported platforms: <span class="label label-success">CentOS 6</span>

#### Description

This release provides fixes to some outstanding bugs:

* fixes a problem that prevented correct status update for tape recalls created through the REST endpoint and handled by GEMSS;

* fixes pool account mapping failures observed when StoRM is deployed with lcmaps-plugins-basic >= 1.6.3;

* enhances the request garbage collector so that PrepareToPut requests that are stuck in the state SRM_REQUEST_INPROGRESS are automatically expired after a configurable amount of time.

This amount of time can be configured through the new property `expired.inprogress.time`.
Its default value is **2592000** secs (1 month).
Add/edit it into your `storm.properties` file.

```bash
expired.request.ptp.time = 2592000
```

* fixes a bug in the garbage collector so that now only recall requests older than a configurable amount of time are garbage collected.

This amount of time can be configured through the property `expired.request.time` which is already used for other asynch requests cleared by the Garbage Collector.

* adds the ability to generate a storage usage JSON report, following the rules and format defined by WLCG.

#### Released components

{% include list-components.liquid %}

#### Bug fixes

{% include list-rfcs.liquid %}

#### Enhancements

{% include list-features.liquid %}

#### Upgrading from v1.11.12

Update packages:

* storm-backend-server
* yaim-storm
* storm-dynamic-info-provider

Example:

    $ yum update storm-backend-server yaim-storm storm-dynamic-info-provider

And reconfigure the StoRM services with YAIM.

Example:

    $ /opt/glite/yaim/bin/yaim -c -s /etc/storm/siteinfo/storm.def -n se_storm_backend

#### Upgrading from older versions

Read the following [instructions][upgrading-old].

#### Clean install

Follow the instructions in the [System Administration Guide][storm-sysadmin-guide].

[upgrading-old]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.12/#upgrading
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide