---
layout: default
title: "StoRM Frontend v.1.8.12 release notes"
release_date: "26.02.2019"
rfcs:
  - id: STOR-1025
    title: Configurable size limit for the request queue
features:
  - id: STOR-1020
    title: Remove functions and code related to old and deprecated variables
  - id: STOR-1029
    title: Address frontend compilation warnings
---

## StoRM Frontend v.1.8.12

Released on **{{ page.release_date }}** with [StoRM v. 1.11.15][release-notes].

### Description

This release:

* avoids an indefinitely growth of the threads queue size due to the the
incoming requests to Frontend and the consequent block of the acceptor thread;
* clears old code and stuff related to deprecated variables.

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

Update package:

    yum update storm-frontend-server

Restart service:

    service storm-frontend-server restart

Alternatively, you can simply update the package and run YAIM.

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.14.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.12
[recallinterface]: https://github.com/italiangrid/storm/tree/develop/src/main/java/it/grid/storm/tape/recalltable/resources
