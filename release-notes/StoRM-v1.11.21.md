---
layout: default
title: "StoRM v.1.11.21 - release notes"
release_date: "12.05.2021"
components:
  - name: StoRM Backend
    package: storm-backend-server
    version: 1.11.21
    platforms:
      - centos7
  - name: StoRM WebDAV
    package: storm-webdav
    version: 1.4.1
    platforms:
      - centos7
  - name: StoRM Frontend
    package: storm-frontend-server
    version: 1.8.15
    platforms:
      - centos7
  - name: StoRM Utils
    package: storm-utils
    version: 1.0.0
    platforms:
      - centos7
rfcs:
  - id: STOR-1395
    title: StoRM Backend service enters failed state when stopped
  - id: STOR-1397
    title: Upgrading to StoRM v1.11.20 could break connections with MariaDB
  - id: STOR-1398
    title: Ensure MariaDB is started before StoRM Frontend on boot
  - id: STOR-1400
    title: StoRM WebDAV service enters failed state when stopped
  - id: STOR-1401
    title: Ensure MariaDB is started before StoRM Backend on boot
features:
  - id: STOR-1430
    title: Provide a set of scripts to update Storage usage information
---

## StoRM v. 1.11.21

Released on **{{ page.release_date }}**.

#### Description

This release:

* fixes the [known issue][known-issue-post] about the upgrade to StoRM v1.11.20 which could break connections with MariaDB
* fixes the boot order for both Frontend and Backend ensuring that mariadb service is started before StoRM services;
* fixes the failed state shown on stop/restart of the Java services due to a misunderstood exit code meaning;
* provides a set of scripts that can be used to edit from command line the storage space info related to a storage area.

#### Released components

{% include list-components.liquid %}

#### Bug fixes

{% include list-rfcs.liquid %}

#### Enhancements

{% include list-features.liquid %}

#### Installation/Upgrade

If you're upgrading from StoRM v1.11.20, upgrade all the released packages:

```
yum update -y storm-backend-mp storm-backend-server storm-webdav storm-frontend-server
```

Ensure that **Java 11 is set as your default runtime**. 

Now, you can restart services:

```
systemctl restart storm-backend-server storm-frontend-server storm-webdav
```

In case you have any kind of questions or problems please contact us.

Read more info about upgrading [here][upgrade-from-20]


[downloads-page]: {{site.baseurl}}/download.html#stable-releases
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide

[upgrade-from-20]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.21/upgrading/

[known-issue-post]: {{site.baseurl}}/2021/04/30/storm-v1.11.20-known-issue.html