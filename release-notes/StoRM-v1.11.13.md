---
layout: default
title: "StoRM v.1.11.13 - release notes"
release_date: "23.10.2017"
rfcs:
  - id: STOR-950
    title: Failure on updating recall task status
  - id: STOR-951
    title: Pool account mapping fails since lcmaps-plugins-basic v1.6.4
  - id: STOR-954
    title: StoRM backend should garbage collect requests that are stuck in SRM_IN_PROGRESS for a configurable amount of time
  - id: STOR-955
    title: Garbage Collector ignore timestamps on cleaning recall tasks
components:
  - name: StoRM Backend
    package: storm-backend-server
    version: 1.11.13
  - name: YAIM StoRM
    package: yaim-storm
    version: 4.3.10
---

## StoRM v. 1.11.13

Released on **{{ page.release_date }}**.

Supported platforms: <span class="label label-success">CentOS 6</span>

#### Description

This release provides fixes to some outstanding bugs:

* fixes gemss failure of recall task final status update;
* fixes pool account mapping failure that happens with lcmaps-plugins-basic versions greater than 1.6.3, by setting storm user as the owner of the gridmap directory;
* fixes garbage collector removal of SRM ptp requests that are stuck in SRM_IN_PROGRESS after long time: set the amount of seconds needed to consider an IN-PROGRESS ptp as expired by adding/editing `expired.request.ptp.time` variable value (default is 2592000L secs = 1 month) into storm.properties configuration file; 
* fixes garbage collector wrong removal of just completed recall-tasks: now it removes only a bunch of the ones that are `expired.request.time` seconds older (as well as the other asynch requests).


#### Released components

{% include list-components.liquid %}

#### Bug fixes

{% include list-rfcs.liquid %}

#### Upgrading from v1.11.12

Update packages:

* storm-backend-server
* yaim-storm

Example:

    $ yum update storm-backend-server yaim-storm

And relaunch YAIM configuration.

Example:

    $ /opt/glite/yaim/bin/yaim -c -s /etc/storm/siteinfo/storm.def -n se_storm_backend

#### Upgrading from old versions

Read the following [instructions][upgrading-old].

#### Clean install

Follow the instructions in the [System Administration Guide][storm-sysadmin-guide].

[upgrading-old]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.12/#upgrading
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide