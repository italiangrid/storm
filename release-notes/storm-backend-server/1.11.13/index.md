---
layout: default
title: "StoRM BackEnd v. 1.11.13 release notes"
release_date: "23.10.2017"
rfcs:
  - id: STOR-950
    title: Failure on updating recall task status
  - id: STOR-954
    title: StoRM backend should garbage collect requests that are stuck in SRM_IN_PROGRESS for a configurable amount of time
  - id: STOR-955
    title: Garbage Collector ignore timestamps on cleaning recall tasks
---

## StoRM Backend v. 1.11.13

Released on **{{ page.release_date }}** with [StoRM v. 1.11.13]({{ site.baseurl }}/release-notes/StoRM-v1.11.13.html).

### Description

This release provides fixes to some outstanding bugs:

* fixes gemss failure of recall task final status update;
* fixes garbage collector removal of SRM ptp requests that are stuck in SRM_IN_PROGRESS after long time;
* fixes garbage collector wrong removal of latest completed recall tasks.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

##### Upgrading from v1.11.12

Update storm-backend-server and relaunch yaim:

    $ yum update storm-backend-server
    $ /opt/glite/yaim/bin/yaim -c -s /etc/storm/siteinfo/storm.def -n se_storm_backend

##### Upgrading from old versions

Read the following [instructions][upgrading-old].

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[upgrading-old]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.12/#upgrading
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.13
