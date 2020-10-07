---
layout: default
title: "StoRM WebDAV v. 1.3.0 release notes"
release_date: "07.08.2020"
rfcs:
  - id: STOR-1197
    title: StoRM Webdav should drop Authorization header in TPC redirects
  - id: STOR-1207
    title: StoRM WebDAV leaks file descriptors when Conscrypt is enabled
  - id: STOR-1217
    title: StoRM WebDAV does not set content-length header correctly for large files
  - id: STOR-1203
    title: Conscrypt should be disabled by default
  - id: STOR-1206
    title: StoRM WebDAV out and err file missing in CENTOS 7 configuration
features:
  - id: STOR-1189
    title: Separate java.io.tmpDir jvm variable from generic jvm options and move it inside systemd unit
  - id: STOR-1201
    title: Update spring boot to 2.2.6 release
---

## StoRM WebDAV v. 1.3.0

Released on **{{ page.release_date }}** as an update of [StoRM v. 1.11.18][release-notes].

### Description

This release:

- updates Spring Boot to v.2.2.6 release;
- fixes output and error log redirection on CentOS7;
- separates java.io.tmpDir jvm variable from generic jvm options and moves it inside systemd unit;
- disables Conscrypt by default;
- fixes Authorization header in TPC redirects;
- fixes content-length header for large files.

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

On RHEL6, update package:

```bash
yum update storm-webdav
```

and run YAIM.

On RHEL7, to install and configure StoRM WebDAV you can use StoRM Puppet module as follows:

```bash
puppet module install cnafsd-storm
```

```puppet
class { 'storm::webdav':
  storage_areas => [
    {
      'name'                       => 'dteam',
      'root_path'                  => '/storage/dteam',
      'access_points'              => ['/dteam'],
      'vos'                        => ['dteam'],
    },
  ],
  hostnames     => ['webdav-host.example.org'],
}
```

Read more at:
* [StoRM Puppet module forge page][stormpuppetmodule];
* [StoRM Puppet module generated documentation][stormpuppetmoduledoc];
* the [Quick deploy on CentOS7][quickdeploy] guide.

[stormpuppetmodule]: https://forge.puppet.com/cnafsd/storm
[stormpuppetmoduledoc]: https://italiangrid.github.io/storm-puppet-module/
[quickdeploy]: {{site.baseurl}}/documentation/how-to/basic-storm-standalone-configuration-centos7/1.11.18/

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.18.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.18
[dav-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.18/storm-webdav-guide.html

