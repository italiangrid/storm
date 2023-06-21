---
layout: default
title: "StoRM v.1.11.22 - release notes"
release_date: "21.06.2023"
components:
  - name: StoRM Backend
    package: storm-backend-server
    version: 1.11.22
    platforms:
      - centos7
  - name: StoRM WebDAV
    package: storm-webdav
    version: 1.4.2
    platforms:
      - centos7
  - name: StoRM Info Provider
    package: storm-dynamic-info-provider
    version: 1.8.3
    platforms:
      - centos7
  - name: StoRM Native Libs
    package: storm-native-libs
    version: 1.0.7
    platforms:
      - centos7
rfcs:
  - id: STOR-1396
    title: Ensure adler32 checksums are always 8 chars long
  - id: STOR-1450
    title: Increase default timeout for TPC to 30 seconds
  - id: STOR-1500
    title: When redis is disabled the health indicator for redis should be disabled
  - id: STOR-1503
    title: Avoid loading and enforcing ACL mask and leave it to be automatically updated
  - id: STOR-1506
    title: Only one VO (the first) is listed into storage site report even if a storage area serves multiple VOs
  - id: STOR-1574
    title: Old java/canl creates problems with encoding of subject/issuer names in self-signed certificates
  - id: STOR-1440
    title: StoRM WebDAV should configure a bigger heap by default
  - id: STOR-1497
    title: Upgrade canl-java to v2.6.0
  - id: STOR-1515
    title: StoRM WebDAV metrics on TPC.pull/push.throughput
  - id: STOR-1525
    title: Load balancing strategy for StoRM WebDAV server pool
  - id: STOR-1555
    title: Upgrade jQuery version
  - id: STOR-1556
    title: Remove TRACE from allowed methods
  - id: STOR-1557
    title: Upgrade Spring Boot version to the latest
  - id: STOR-1558
    title: Update bouncycastle version to 1.67
  - id: STOR-1561
    title: CREATE date in the future in table storage_space
---

## StoRM v. 1.11.22

Released on **{{ page.release_date }}**.

#### Description

This release:

* upgrades significant dependencies for StoRM WebDAV (spring-boot, canl, bouncycastle, jQuery)
* introduce the support for WebDAV server pools into StoRM Backend
* removes the support for TRACE method in StoRM WebDAV
* tunes some default values of StoRM WebDAV (default TPC timeout, default heap size, etc.)

and fixes other minor bugs/issues.

#### Released components

{% include list-components.liquid %}

#### Bug fixes

{% include list-rfcs.liquid %}

#### Installation/Upgrade

If you're upgrading from StoRM v1.11.21, upgrade all the released packages:

```
yum update -y storm-backend-server storm-webdav storm-dynamic-info-provider storm-native-libs storm-native-libs-gpfs
```

Now, you can restart services:

```
systemctl restart storm-backend-server storm-webdav
```

And re-configure info provider:

```
/usr/libexec/storm-info-provider configure
```

In case you have any kind of questions or problems please contact us.

Read more info about upgrading [here][upgrade-from-21]

[downloads-page]: {{site.baseurl}}/download.html#stable-releases
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide

[upgrade-from-21]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.22/upgrading/