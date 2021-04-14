---
layout: default
title: "StoRM GridFTP server v.1.2.4 release notes"
release_date: "07.08.2020"
rfcs:
  - id: STOR-1102
    title: Investigate errors on transferred files through GridFTP that leave empty files with an adler32 checksum for a non-empty file
  - id: STOR-1186
    title: Set KillMode to control-group and not process to avoid orphan transfer processes on StoRM GridFTP
features:
  - id: STOR-1185
    title: Allow redirection of LCMAPS logging to a particular file (instead of syslog) through the puppet module
---

## StoRM GridFTP server v.1.2.4

Released on **{{ page.release_date }}** with [StoRM v. 1.11.18][release-notes].
<span style="float: right; margin-left: 8px;" class="label label-success">centos6</span>
<span style="float: right; margin-left: 8px;" class="label label-info">centos7</span>

### Description

This release:
* mitigates errors on transferred files that leave empty files with an adler32 checksum for a non-empty file;
* fixes KillMode to control-group to avoid orphan transfer processes on StoRM GridFTP;
* allows redirection of LCMAPS logging to a particular file (instead of syslog) through the puppet module.

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

On RHEL6, update package:

```
yum update storm-globus-gridftp-server
```

and run YAIM.

On RHEL7, to install and configure StoRM GridFTP you can use StoRM Puppet module as follows:

```bash
puppet module install cnafsd-storm
```

```puppet
include storm::gridftp
```

Read more at:
* [StoRM Puppet module forge page][stormpuppetmodule];
* [StoRM Puppet module generated documentation][stormpuppetmoduledoc] forge page;
* [Puppet Configuration][puppetconf] section of [System Administration Guide][storm-sysadmin-guide];
* the [Quick deploy on CentOS7][quickdeploy] guide.

[stormpuppetmodule]: https://forge.puppet.com/cnafsd/storm
[stormpuppetmoduledoc]: https://italiangrid.github.io/storm-puppet-module/
[puppetconf]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.18#puppetconfiguration
[quickdeploy]: {{site.baseurl}}/documentation/how-to/basic-storm-standalone-configuration-centos7/1.11.18/

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.18.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.18
