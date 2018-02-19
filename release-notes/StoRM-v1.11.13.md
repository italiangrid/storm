---
layout: default
title: "StoRM v.1.11.13 - release notes"
release_date: "19.02.2018"
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

* fixes a problem on status update for tape recalls created through the REST endpoint;

* fixes pool account mapping failures observed when StoRM is deployed with lcmaps-plugins-basic >= 1.6.3;

* enhances the request garbage collector so that PrepareToPut requests that are stuck in the state SRM_REQUEST_INPROGRESS are automatically expired after a configurable amount of time.

This amount of time can be configured through the new property `expired.inprogress.time` (read more [here][gc-guide]).
<br/>Its default value is **2592000** secs (1 month).
Add/edit it into your `storm.properties` file.

```bash
expired.request.ptp.time = 2592000
```

* fixes a bug in the garbage collector so that now only recall requests older than a configurable amount of time are garbage collected.

This amount of time can be configured through the property `expired.request.time` (read more [here][gc-guide])
which is already used for other asynch requests cleared by the Garbage Collector.<br/>Its default value is **604800** secs (1 week).
Add/edit it into your `storm.properties` file.

```bash
expired.request.time = 604800
```

* adds the ability to generate a storage usage JSON report, following the rules and format defined by [WLCG][wlcg] (read more [here][how-to-json-report]).

This release supports both UMD-3 repository and UMD-4 repository for SL6, but **a migration to UMD-4 repositories is encouraged**.
<br/>
Installation instructions can be found within [System Administration Guide][umd-sysadmin].

#### Released components

{% include list-components.liquid %}

#### Bug fixes

{% include list-rfcs.liquid %}

#### Enhancements

{% include list-features.liquid %}

#### Installation

In case of a clean installation, follow the instructions in the [System Administration Guide][storm-sysadmin-guide].

##### Upgrading from StoRM v1.11.12

Services to be updated are:

* storm-backend-server
* storm-dynamic-info-provider
* yaim-storm

Run update:

    $ yum update storm-backend-server yaim-storm storm-dynamic-info-provider

and reconfigure StoRM Backend node with YAIM.
Example with configuration file `/etc/storm/siteinfo/storm.def`:

    $ /opt/glite/yaim/bin/yaim -c -s /etc/storm/siteinfo/storm.def -n se_storm_backend

##### Upgrading from earlier versions

<span class="label btn-warning" style="margin-top: -20px;">WARN</span> If you are upgrading StoRM from v1.11.11, read the following [upgrading to v1.11.12 instructions][upgrading-post].

If you are upgrading from versions earlier than v1.11.11, find and read the release notes's upgrading instructions [here][releases].

[upgrading-post]: {{site.baseurl}}/2017/09/02/upgrading-to-storm-1-11-12.html
[upgrading]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.13/#upgrading
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide
[how-to-json-report]: {{site.baseurl}}/documentation/how-to/how-to-publish-json-report/
[umd-4-page]: http://repository.egi.eu/category/umd_releases/distribution/umd-4
[umd-sysadmin]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.13/#umdrepos
[wlcg]: http://wlcg.web.cern.ch/
[gc-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.13/#requestsgarbagecollector
[releases]: {{site.baseurl}}/releases.html