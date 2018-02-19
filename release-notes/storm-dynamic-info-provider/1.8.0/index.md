---
layout: default
title: StoRM Info Provider v.1.8.0 release notes
release_date: "19.02.2018"
rfcs:
  - id: STOR-982
    title: JSON storage usage reporting
---

## StoRM Info Provider v. 1.8.0

Released on **{{ page.release_date }}** with [StoRM v. 1.11.13][release-notes].

### Description

This release adds the ability to generate a storage report in JSON which follows the rules and format defined in the 
[Storage Resource Reporting Proposal for storage providers][proposal] ([example][example]).

If a site administrator wants to configure StoRM to share this report, [here][how-to-json-report] is a guide to:

- periodically refresh report
- give read permissions to all the authenticated users of one (or more) VO
- make it accessible through a WebDAV endpoint.


### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

Update package:

    yum update storm-dynamic-info-provider

Re-configure info provider:

     /usr/libexec/storm-info-provider configure

Restart BDII service:

     service bdii restart

Alternatively, you can simply update the package and run YAIM.

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.13.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/
[how-to-json-report]: {{site.baseurl}}/documentation/how-to/how-to-publish-json-report/

[proposal]: https://docs.google.com/document/d/1yzCvKpxsbcQC5K9MyvXc-vBF1HGPBk4vhjw3MEXoXf8/edit#
[example]: https://twiki.cern.ch/twiki/pub/LCG/AccountingTaskForce/storage_service_v4.txt