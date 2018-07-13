---
layout: default
title: "StoRM Frontend v.1.8.11 release notes"
release_date: "25.07.2018"
features:
  - id: STOR-989
    title: Configurable SOAP send and receive timeouts
---

## StoRM Frontend v.1.8.11

Released on **{{ page.release_date }}** with [StoRM v. 1.11.14][release-notes].

### Description

This release makes SOAP send and receive timeouts configurable.

#### Enhancements

{% include list-features.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.14.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.12
[recallinterface]: https://github.com/italiangrid/storm/tree/develop/src/main/java/it/grid/storm/tape/recalltable/resources
