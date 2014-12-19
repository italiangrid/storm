---
layout: default
title: "StoRM Frontend v.1.8.6 release notes"
release_date: 19.12.2014
rfcs:
- id: STOR-605
  title: StoRM frontend leaks memory when Argus callout is enabled
---

## StoRM Frontend v.1.8.6

Released on **{{ page.release_date }}** with [StoRM v. 1.11.5]({{ site.baseurl }}/release-notes/StoRM-v1.11.5.html).

### Description

This release fixes a memory leak on the storm frontend that was triggered when the Argus authorization callout
was enabled.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [System Admininistration Guide][storm-sysadmin-guide] of the [Documentation][storm-documentation] section.

### Known issues

None at the moment

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.5
