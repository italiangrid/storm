---
layout: default
title: "StoRM v.1.11.8 - release notes"
release_date: "13.03.2015"
rfcs:
- id: STOR-779
  title: rm command does not properly abort ongoing PtP requests
- id: STOR-750
  title: Single quote in certificate subject causes failures in StoRM async requests
- id: STOR-751
  title: storm-info-provider-rpm misses dependency on python-simplejson on SL5
- id: STOR-776
  title: Inefficient SQL query for surl status checks
- id: STOR-777
  title: Inefficient query used to update SURL status when a releaseFiles is called
- id: STOR-778
  title: Improve efficiency on status PtGs
- id: STOR-780
  title: StoRM Info Provider 'configure' fails with 'too many values to unpack'
components:
  - name: StoRM Backend
    package: storm-backend-server
    version: 1.11.8
    platforms:
      - centos5
      - centos6
  - name: StoRM Frontend
    package: storm-frontend-server
    version: 1.8.8
    platforms:
      - centos5
      - centos6
  - name: StoRM Info Provider
    package: storm-dynamic-info-provider
    version: 1.7.8
    platforms:
      - centos5
      - centos6
---

## StoRM v. 1.11.8

<span class="label label-warning">CentOS 5</span> <span class="label label-success">CentOS 6</span>

Released on **{{ page.release_date }}**

### Description

This release fixes a [critical issue][STOR-779] that prevented the proper
cleanup of PtP requests state after an srmRm was called on a SURL. As a
consequence, a SURL could remain stuck and it would be impossible to perform
other PtPs on that SURL without cleaning the database state by hand. **All sites
should upgrade to 1.11.8 at their earliest convenience to avoid being affected
by this issue**.

This release also provides other fixes and improvements as can be seen from the
individual components release notes.

### Released components

{% include list-components.liquid %}

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

This release requires a YAIM reconfiguration of the backend service, in order
to apply changes to the StoRM database schema.

You can find more information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[STOR-779]: https://issues.infn.it/jira/browse/STOR-779
[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.8
