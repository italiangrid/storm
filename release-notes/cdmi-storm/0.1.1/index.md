---
layout: default
title: "CDMI StoRM v.0.1.1 release notes"
release_date: "07.08.2020"
---

## CDMI StoRM v. 0.1.1

Released on **{{ page.release_date }}** with [StoRM v. 1.11.18]({{ site.baseurl }}/release-notes/StoRM-v1.11.18.html).

<span style="float: right; margin-left: 8px;" class="label label-info">centos7</span>

### Description

CDMI StoRM implements the Java Service Provider Interface [cdmi-spi][cdmi-spi] to provide a plugin 
for the [INDIGO DataCloud CDMI server][cdmiqos] in order to support StoRM as storage back-end and 
allow users to negotiate the Quality of Service of stored data.

This release fixes a security vulnerability due to a dependency with Jackson JSON JAVA libraries.

### Installation and configuration

Read the [INDIGO DataCloud CDMI server installation guide][cdmiserverguide] and the [CDMI StoRM admin guide][cdmistormguide] to learn how to install and 
configure the INDIGO CDMI server with StoRM Backend plugin.

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Admininistration Guide][storm-sysadmin-guide] of
the [Documentation][storm-documentation] section.

[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.18

[cdmi-spi]: https://github.com/indigo-dc/cdmi-spi
[cdmiqos]: https://github.com/indigo-dc/cdmi

[cdmiserverguide]: https://indigo-dc.gitbooks.io/cdmi-qos/content/doc/installing_cdmi-qos.html
[cdmistormguide]: https://github.com/italiangrid/cdmi-storm/blob/master/doc/admin.md