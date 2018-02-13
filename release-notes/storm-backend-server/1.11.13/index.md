---
layout: default
title: "StoRM BackEnd v. 1.11.13 release notes"
release_date: "14.02.2018"
rfcs:
  - id: STOR-950
    title: Failure on updating recall task status
  - id: STOR-955
    title: Garbage Collector ignore timestamps on cleaning recall tasks
features:
  - id: STOR-954
    title: StoRM backend should garbage collect requests that are stuck in SRM_IN_PROGRESS for a configurable amount of time
---

## StoRM Backend v. 1.11.13

Released on **{{ page.release_date }}** with [StoRM v. 1.11.13]({{ site.baseurl }}/release-notes/StoRM-v1.11.13.html).

### Description

This release provides fixes to some outstanding bugs:

* fixes a problem that prevented correct status update for tape recalls created through the REST endpoint and handled by GEMSS;

* enhances the request garbage collector so that PrepareToPut requests that are stuck in the state SRM_REQUEST_INPROGRESS are automatically expired after a configurable amount of time.

This amount of time can be configured through the new property `expired.inprogress.time`.
Its default value is **2592000** secs (1 month).
Add/edit it into your `storm.properties` file.

```bash
expired.request.ptp.time = 2592000
```

* fixes a bug in the garbage collector so that now only recall requests older than a configurable amount of time are garbage collected.

This amount of time can be configured through the property `expired.request.time` which is already used for other asynch requests cleared by the Garbage Collector.

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

##### Upgrading from v1.11.12

Update storm-backend-server and relaunch yaim:

    $ yum update storm-backend-server
    $ /opt/glite/yaim/bin/yaim -c -s /etc/storm/siteinfo/storm.def -n se_storm_backend

##### Upgrading from older versions

Read the following [instructions][upgrading-old].

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[upgrading-old]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.12/#upgrading
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.13
