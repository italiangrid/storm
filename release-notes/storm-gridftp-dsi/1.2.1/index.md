---
layout: default
title: "StoRM GridFTP server v.1.2.1 release notes"
release_date: "25.07.2018"
rfcs:
  - id: STOR-992	
    title: Sync file contents before setting checksum attribute
---

## StoRM GridFTP server v.1.2.1

Released on **{{ page.release_date }}** with [StoRM v. 1.11.14][release-notes].

### Description

Synchronizing file contents before setting checksum attribute preveins error on checksum computation. 

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.14.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.14