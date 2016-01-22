---
layout: default
title: "StoRM BackEnd v. 1.11.10 release notes"
release_date: "22.01.2016"
rfcs:
- id: STOR-234
  title: Storm BE does not manage correctly abort requests of expired tokens
- id: STOR-835
  title: Improper management of SURL status can lead to PutDone errors and locked SURLs
---

## StoRM Backend v. 1.11.10

Released on **{{ page.release_date }}** with [StoRM v. 1.11.10]({{ site.baseurl }}/release-notes/StoRM-v1.11.10.html).

### Description

This release fixes an improper management of SURL status that can lead to PutDone errors and locked SURLs.
It fixes also a minor issue related to the retrieved error message in case an expired token is used with srmAbort.

### Bug fixes and improvements

{% include list-rfcs.liquid %}

### Installation and configuration

- Update and restart package:

```bash
yum update storm-backend-server
service storm-backend-server restart
```

You can find more information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/
