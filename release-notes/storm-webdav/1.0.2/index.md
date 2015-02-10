---
layout: default
title: "StoRM WebDAV v. 1.0.2 release notes"
release_date: 09.02.2015
rfcs:
- id: STOR-346
  title: WebDAV DELETE response is 401 UNAUTHORIZED instead of 404 NOT EXISTS for authorized users on nonexistent resources
- id: STOR-632
  title: StoRM WebDAV service handles multi-range partial get incorrectly
- id: STOR-669
  title: HTTP requests fail if path contains trailing slashes
---

## StoRM WebDAV v. 1.0.2

Released on **{{ page.release_date }}** with [StoRM v. 1.11.7][release-notes].

### Description

This is the first official release of the StoRM WebDAV service, which replaces the storm-gridhttps server.

The StoRM WebDAV service provides performance improvements and resolves some
problems previously found in the storm-gridhttps-server, in particular:

- incorrect handling of multi-range partial get requests
- incorrect handling of multiple slashes in incoming requests

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

Check the the [StoRM WebDAV installation and configuration guide][storm-webdav-guide] 
for detailed installation and configuration information.

For the other StoRM services, check the the [System Administration
Guide][storm-sysadmin-guide].

#### Changes since the 1.0.0 preview release

Some sites installed the storm-webdav 1.0.0 previed release. The location of
configuration and log files has changed in this release to be better aligned
with other StoRM services.

Packaging changes described in the following table:

|  | 1.0.0 location | 1.0.2 location |
-------|----------------|----------------|
| Configuration directory | `/etc/storm-webdav` | `/etc/storm/webdav` |
| Service logs | `/var/log/storm-webdav` | `/var/log/storm/webdav` |

If you installed the beta version, move configuration files to the new
directory and restart the service.

[release-notes]: {{ site.baseurl }}/release-notes/StoRM-v1.11.7.html
[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.7
[storm-webdav-guide]: {{ site.baseurl }}/documentation/sysadmin-guide/1.11.7/storm-webdav-guide.html
