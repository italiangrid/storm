---
layout: default
title: "StoRM native libs v.1.0.6 release notes"
release_date: "12.04.2021"
features:
  - id: STOR-1357
    title: StoRM Backend and native libs should run with Java 11
---

## StoRM native-libs v. 1.0.6-2

Released on **{{ page.release_date }}** with [StoRM v. 1.11.20][release-notes].
<span style="float: right; margin-left: 8px;" class="label label-info">centos7</span>

### Description

This release requires and install Java 11.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

The native libs are installed as dependency of StoRM Backend. <br/>
Upgrading StoRM backend, also the native libraries will be updated too.

In case of a clean installation please read the [System Administrator Guide][storm-sysadmin-guide].

Read more at:
* [StoRM Puppet module forge page][stormpuppetmodule]

[stormpuppetmodule]: https://forge.puppet.com/cnafsd/storm

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.20.html
[upgrading]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.20/upgrading
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.20
