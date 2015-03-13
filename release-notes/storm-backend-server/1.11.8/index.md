---
layout: default
title: "StoRM BackEnd v. 1.11.8 release notes"
release_date: "13.03.2015"
rfcs:
- id: STOR-779
  title: rm command does not properly abort ongoing PtP requests
- id: STOR-776
  title: Inefficient SQL query for surl status checks
- id: STOR-777
  title: Inefficient query used to update SURL status when a releaseFiles is called
- id: STOR-778
  title: Improve efficiency on status PtGs
---

## StoRM Backend v. 1.11.8

Released on **{{ page.release_date }}** with [StoRM v. 1.11.8]({{ site.baseurl }}/release-notes/StoRM-v1.11.8.html).

### Description

This release fixes a [critical issue][STOR-779] that prevented the proper
cleanup of PtP requests state after an srmRm was called on a SURL. As a
consequence, a SURL could remain stuck and it would be impossible to perform
other PtPs on that SURL without cleaning the database state by hand. **All sites
should upgrade to 1.11.8 at their earliest convenience to avoid being affected
by this issue**.

This release also introduces peformance improvements in the handling of SRM
`releaseFiles` and `statusPrepareToGet` requests.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

This release requires a YAIM reconfiguration of the backend service, in order
to apply changes to the StoRM database schema.

<span class="label label-warning">IMPORTANT</span> 
In order to avoid issues with locked SURLs as consequeunce of [this
issue][STOR-779], a database cleanup is recommended. To drop the storm
database:

- log into the StoRM MySQL database as `root`
- drop the `storm_db` and `storm_BE_ISAM` databases
- run YAIM to recreate the databases from scratch

If you cannot afford to drop the database (e.g., you cannot stop the service),
see [this GIST][STOR-779-workaround] for scripts and advice on how _stuck_
surls can be unlocked. You will need to run YAIM anyway after upgrading the packages
to apply the schema changes introduced by this release.

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[STOR-779-workaround]: https://gist.github.com/andreaceccanti/2d1dc8eee162f2b7fbad
[STOR-779]: https://issues.infn.it/jira/browse/STOR-779
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.8
