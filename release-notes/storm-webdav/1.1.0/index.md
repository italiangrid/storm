---
layout: default
title: "StoRM WebDAV v. 1.1.0 release notes"
release_date: "26.02.2019"
features:
  - id: STOR-1018
    title: Support for third-party copy in StoRM WebDAV service
---

## StoRM WebDAV v. 1.1.0

Released on **{{ page.release_date }}** as an update of [StoRM v. 1.11.15][release-notes].

### Description

This release implements basic support for Third-Party-Copy, which needs to be
properly configured in order to make service working after the update
(read more [here][webdav-tpc-aliases]).

Read both [StoRM WebDAV service installation and configuration guide][dav-guide] and
[Third-Party-Copy guide][tpc-guide] for more info.

Other useful links:

- [LCGDM HTTP/WebDAV Third Party Copy extension](https://svnweb.cern.ch/trac/lcgdm/wiki/Dpm/WebDAV/Extensions#ThirdPartyCopies)
- [LCG twiki on HTTP/WebDAV Third-Party-Copy](https://twiki.cern.ch/twiki/bin/view/LCG/HttpTpc)
- [HTTP/WebDAV Third-Party-Copy Technical Details](https://twiki.cern.ch/twiki/bin/view/LCG/HttpTpcTechnical)

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

Update package:

    yum update storm-webdav

After the service update, admins MUST declare all the hostnames that has to be
recognized as 'local' when a _Destination_ header is specified.
As better explained into [this guide][tpc-guide], a list of
`STORM_WEBDAV_HOSTNAME_{N}` variables must be set into
`/etc/sysconfig/storm-webdav`.

If you have a single WebDAV endpoint, with `storm.example` as FQDN for example,
append the following line to `/etc/sysconfig/storm-webdav`:

```bash
STORM_WEBDAV_HOSTNAME_0="storm.example"
```

Then restart the service:

```
service storm-webdav restart
```

Alternatively, you can re-run YAIM after the fix.

Check the the [StoRM WebDAV installation and configuration guide][storm-webdav-guide]
for detailed installation and configuration information.

For the other StoRM services, check the the [System Administration Guide][storm-sysadmin-guide].

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.15.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15
[dav-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15/storm-webdav-guide.html
[tpc-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15/tpc.html

[webdav-tpc-aliases]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15#important2
