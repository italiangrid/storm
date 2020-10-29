---
layout: default
title: "StoRM v.1.11.19 - release notes"
release_date: "29.10.2020"
rfcs:
  - id: STOR-1259
    title: StoRM WebDAV sends response body for HEAD requests resulting in errors
  - id: STOR-1260
    title: BoL and PtG requests statuses not updated after recall success
  - id: STOR-1267
    title: Uncaught RuntimeException raised when user.storm.pinned attribute is not found causes SRM_INTERNAL_ERROR during srmReleaseFiles
features:
  - id: STOR-1201
    title: Update spring boot to 2.2.6 release
  - id: STOR-1251
    title: Include jetty handler metrics reporting in storm-backend-metrics log
components:
  - name: StoRM Backend
    package: storm-backend-server
    version: 1.11.19
    platforms:
      - centos6
      - centos7
  - name: StoRM WebDAV
    package: storm-webdav
    version: 1.3.1
    platforms:
      - centos6
      - centos7
---

## StoRM v. 1.11.19

Released on **{{ page.release_date }}**.

#### Description

This release:

- fixes a bug introduced with StoRM v1.11.18 about the final update of the status on
database of a srmPtG or srmBoL requests;
- introduces new metrics in storm-backend-metrics.log;
- fixes a storm-webdav bug about the returned body content in case of HEAD requests.

#### Released components

{% include list-components.liquid %}

#### Bug fixes

{% include list-rfcs.liquid %}

#### Enhancements

{% include list-features.liquid %}

#### Installation/Upgrade

If you're upgrading from StoRM v1.11.18 you can simply update packages and restart services:

```
yum update -y storm-backend-server storm-webdav
```

on RHEL6:

```
service storm-backend-server restart
service storm-webdav restart
```

on RHEL7:

```
systemctl restart storm-backend-server
systemctl restart storm-webdav
```

Read more info about upgrading [here][upgrade-from-18]


[downloads-page]: {{site.baseurl}}/download.html#stable-releases
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide

[upgrade-from-18]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.19/#upgrading