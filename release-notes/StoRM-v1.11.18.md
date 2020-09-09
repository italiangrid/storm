---
layout: default
title: "StoRM v.1.11.18 - release notes"
release_date: "07.08.2020"
rfcs:
  - id: STOR-1102
    title: Investigate errors on transferred files through GridFTP that leave empty files with an adler32 checksum for a non-empty file
  - id: STOR-1176
    title: SrmRm file does not exist should not be logged as ERROR
  - id: STOR-1186
    title: Set KillMode to control-group and not process to avoid orphan transfer processes on StoRM GridFTP
  - id: STOR-1197
    title: StoRM Webdav should drop Authorization header in TPC redirects
  - id: STOR-1207
    title: StoRM WebDAV leaks file descriptors when Conscrypt is enabled
  - id: STOR-1212
    title: Change the way Info Provider checks if Backend is running
  - id: STOR-1213
    title: WebDAV endpoint not published in case the latest logic is used
  - id: STOR-1214
    title: Wrong storage space values are published in case multiple storage area have the same VO
  - id: STOR-1217
    title: StoRM WebDAV does not set content-length header correctly for large files
  - id: STOR-1203
    title: Conscrypt should be disabled by default
  - id: STOR-1252
    title: Align the namespace.xml generated via YAIM StoRM with the one produced by the Puppet module
  - id: STOR-1219
    title: Fix ACL setting problems on storm-native-libs built against GPFS > 3.4
features:
  - id: STOR-892
    title: Log as ERROR only internal errors
  - id: STOR-932
    title: Make Background DU configurable to run periodically in order to update used space info on db
  - id: STOR-1036
    title: Fix useless verbosity in log
  - id: STOR-1170
    title: curl_global_init should be called before spawning threads
  - id: STOR-1172
    title: Upgrade jackson-databind to version 2.9.10.1 or later
  - id: STOR-1174
    title: Include thread pool and jetty handler metrics reporting in storm-backend-metrics log
  - id: STOR-1175
    title: Understand what is the purpose of the recallBuckets map and whether it can be removed
  - id: STOR-1185
    title: Allow redirection of LCMAPS logging to a particular file (instead of syslog) through the puppet module
  - id: STOR-1189
    title: Separate java.io.tmpDir jvm variable from generic jvm options and move it inside systemd unit
  - id: STOR-1198
    title: Add Date to Backend's metrics log
  - id: STOR-1201
    title: Update spring boot to 2.2.6 release
  - id: STOR-1206
    title: StoRM WebDAV out and err file missing in CENTOS 7 configuration
  - id: STOR-1216
    title: Include mysql-connector-java into maven dependencies
  - id: STOR-1089
    title: SystemD support for StoRM Backend
  - id: STOR-1090
    title: SystemD support for StoRM Frontend
components:
  - name: StoRM Backend
    package: storm-backend-server
    version: 1.11.18
    platforms:
      - centos6
      - centos7
  - name: StoRM Frontend
    package: storm-frontend-server
    version: 1.8.13
    platforms:
      - centos6
      - centos7
  - name: StoRM WebDAV
    package: storm-webdav
    version: 1.3.0
    platforms:
      - centos6
      - centos7
  - name: StoRM GridFTP
    package: storm-gridftp-dsi
    version: 1.2.4
    platforms:
      - centos6
      - centos7
  - name: StoRM Native Libs
    package: storm-native-libs
    version: 1.0.6
    platforms:
      - centos6
      - centos7
  - name: StoRM Info Provider
    package: storm-dynamic-info-provider
    version: 1.8.2
    platforms:
      - centos6
      - centos7
  - name: YAIM StoRM
    package: yaim-storm
    version: 4.3.13
    platforms:
      - centos6
      - centos7
  - name: CDMI StoRM
    package: cdmi-storm
    version: 0.1.1
    platforms:
      - centos7
  - name: StoRM XMLRPC-C
    package: storm-xmlrpc-c
    version: 1.39.12
    platforms:
      - centos7
---

## StoRM v. 1.11.18

Released on **{{ page.release_date }}**.

#### Description

This release introduces the support for CentOS 7 for all StoRM components.

It also provides fixes to some outstanding bugs, in particular:

* fixes errors on published storage space occupancy in case multiple storage area shares the same VO;
* fixes not published WebDAV endpoints when latest logic is used;
* fixes not dropped Authorization header in WebDAV TPC redirects;
* fixes leaked file descriptors when Conscrypt is enabled on StoRM WebDAV;
* sets correctly HTTP content-length for large files;
* fixes errors on transferred files through GridFTP that leave empty files with an adler32 checksum for a non-empty file;
* fixes KillMode on GridFTP systemd unit.

It also provides several improvements, in particular:

* fixes wrong ERROR log messages when file does not exist on srmRm requests;
* changes the way info provider checks if Backend is running;
* introduces a Background DU Service (disabled by default) that periodically updates the storage space info for non-GPFS storage areas (read more info [here][duservice]);
* adds Date and thread pools metrics in the metrics logged info;
* updates spring boot to 2.2.6 release for StoRM WebDAV;
* adds SystemD support for StoRM Backend and StoRM Frontend.

#### Released components

{% include list-components.liquid %}

#### Bug fixes

{% include list-rfcs.liquid %}

#### Enhancements

{% include list-features.liquid %}

#### Installation

##### Upgrade StoRM repository

This release introduces a **new StoRM production repository migration**.
The old stable repository won't be updated with this release.

If you want to install StoRM v1.11.18 **update your repositories**:

```
yum-config-manager --disable storm-stable-centos6 storm-beta-centos6 storm-nightly-centos6
```

and add the new ones:

```
yum-config-manager --add-repo https://repo.cloud.cnaf.infn.it/repository/storm/storm-stable-centos6.repo
yum-config-manager --add-repo https://repo.cloud.cnaf.infn.it/repository/storm/storm-beta-centos6.repo
yum-config-manager --add-repo https://repo.cloud.cnaf.infn.it/repository/storm/storm-nightly-centos6.repo
yum-config-manager --disable storm-nightly-centos6
```

or, in case of RHEL7:

```
yum-config-manager --add-repo https://repo.cloud.cnaf.infn.it/repository/storm/storm-stable-centos7.repo
yum-config-manager --add-repo https://repo.cloud.cnaf.infn.it/repository/storm/storm-beta-centos7.repo
yum-config-manager --add-repo https://repo.cloud.cnaf.infn.it/repository/storm/storm-nightly-centos7.repo
yum-config-manager --disable storm-nightly-centos7
```

All the links to the repository files can be found in the [Downloads][downloads-page] section.

##### Upgrade/Install packages

On RHEL6, update packages:

```
yum update storm-backend-server storm-frontend-server storm-webdav storm-globus-gridftp-server yaim-storm
```

and run YAIM.

On RHEL7, to install and configure StoRM components you can use StoRM Puppet module.
Read more at:
* [StoRM Puppet module forge page][stormpuppetmodule];
* [StoRM Puppet module generated documentation][stormpuppetmoduledoc] forge page;
* [Puppet Configuration][puppetconf] section of [System Administration Guide][storm-sysadmin-guide];
* the [Quick deploy on CentOS7][quickdeploy] guide.

[stormpuppetmodule]: https://forge.puppet.com/cnafsd/storm
[stormpuppetmoduledoc]: https://italiangrid.github.io/storm-puppet-module/
[puppetconf]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.18#puppetconfiguration
[quickdeploy]: {{site.baseurl}}/documentation/how-to/basic-storm-standalone-configuration-centos7/1.11.18/

[downloads-page]: {{site.baseurl}}/download.html#stable-releases
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide

[upgrade-from-17]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.18/#upgrading
[duservice]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.18#duserviceconfiguration
