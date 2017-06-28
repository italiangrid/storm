---
layout: default
title: "StoRM Frontend v.1.8.10 release notes"
release_date: "30.06.2017"
rfcs:
  - id: STOR-929
    title: Fix StoRM Recall Interface
---

## StoRM Frontend v.1.8.10

Released on **{{ page.release_date }}** with [StoRM v. 1.11.12]({{ site.baseurl }}/release-notes/StoRM-v1.11.12.html).

### Description

This release contains the necessary code fixes due the add of the token to the communication with 
the Backend recall interface (more info [here][recallinterface]).

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.12
[recallinterface]: https://github.com/italiangrid/storm/tree/develop/src/main/java/it/grid/storm/tape/recalltable/resources
