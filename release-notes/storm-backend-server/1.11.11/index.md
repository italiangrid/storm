---
layout: default
title: "StoRM BackEnd v. 1.11.11 release notes"
release_date: "10.05.2016"
rfcs:
  - id: STOR-867
    title: Backend initialization fails when storage area has a default acl list
features:
  - id: STOR-695
    title: Improve performance of StoRM BE Garbage Collector
  - id: STOR-890
    title: Finer grained metrics for the storm-backend
---

## StoRM Backend v. 1.11.11

Released on **{{ page.release_date }}** with [StoRM v. 1.11.11]({{ site.baseurl }}/release-notes/StoRM-v1.11.11.html).

### Description

This release:

* fixes a StoRM backend issue that prevented correct startup on Java 6 and 7

and introduces:

* a mechanism to gather metrics about synchronous operations load and
  performance (see [sys-admin guide logging section][metrics-log]);
* changes to the Request Garbage Collector's default configuration, which now
  implements an adaptive mechanism to keep the request database dimension under
  control in high-load scenarios (see [sys-admin guide requests-garbage-collector section][gc-guide]).

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

To upgrade to StoRM v.1.11.11, first of all update the involved packages:

    yum update -y storm-backend-server

Check your `/etc/storm/backend-server` directory, you should find a new file:
`logging.xml.rpmnew`. Backup your current logging configuration and replace it
with the newest:

    cd /etc/storm/backend-server
    cp logging.xml logging.xml.backup
    cp logging.xml.rpmnew logging.xml

Restart StoRM Backend service:

    service storm-backend-server restart

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.11

[metrics-log]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.11/#storm-backend-metricslog
[gc-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.11/#requests-garbage-collector
