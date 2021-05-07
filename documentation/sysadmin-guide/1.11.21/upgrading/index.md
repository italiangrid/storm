---
layout: service-guide
title: StoRM System Administration Guide - Upgrade to StoRM 1.11.21
navigation:
  - link: documentation/sysadmin-guide/1.11.21/index.html
    label: Index
  - link: documentation/sysadmin-guide/1.11.21/upgrading/index.html
    label: Upgrading
---

## Upgrade to StoRM 1.11.21 <a name="upgrading">&nbsp;</a>

In case you're updating from **StoRM v1.11.20**, the services that needs to be updated are:

* _storm-backend-server_
* _storm-webdav_
* _storm-frontend_

First of all upgrade all the released packages:

```
yum update -y storm-backend-mp storm-backend-server storm-webdav storm-frontend-server
```

This should install also the newest `storm-utils` rpm as a dependency.

Remember that **Java 11 must be set as your default runtime**.

If you are upgrading from **StoRM v1.11.19** (or earlier versions) please follow
[these instructions][upgrade-19] before.

[upgrade-19]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.20/upgrading/
