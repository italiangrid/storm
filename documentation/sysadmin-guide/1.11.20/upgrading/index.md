---
layout: service-guide
title: StoRM System Administration Guide - Upgrade to StoRM 1.11.20
navigation:
  - link: documentation/sysadmin-guide/1.11.20/index.html
    label: Index
  - link: documentation/sysadmin-guide/1.11.20/upgrading/index.html
    label: Upgrading
---

## Upgrade to StoRM 1.11.20 <a name="upgrading">&nbsp;</a>

In case you're updating from **StoRM v1.11.19**, the services that needs to be updated are:

* _storm-backend-server_
* _storm-native-libs_
* _storm-webdav_
* _storm-frontend_

First of all upgrade all the released packages:

```
yum update -y storm-backend-server storm-webdav storm-frontend-server
```

The update will upgrade also the native libraries as a dependency. <br/>
After the successful upgrade, the services will be restarted and you should have both Java 1.8 and Java 11 installed, but
**Java 11 must be set as your default runtime**. None of the latest StoRM Java components still need Java 1.8 so it can be safely removed as follows:

```
yum remove java-1.8.0-openjdk java-1.8.0-openjdk-headless
```

You shouldn't see any storm components within the involved dependencies. <br/>
If you cannot remove it, you can also set java 11 as default runtime JDK by running:

```
update-alternatives --config java
```

and select the proper Java 11 option. <br/>

Now, you can restart services:

```
systemctl restart storm-backend-server storm-frontend-server storm-webdav
```

Split the above commands properly if you have a distributed deployment.
In case you have any kind of questions or problems please contact us.

If you are upgrading from **StoRM v1.11.18** (or earlier versions) on CentOS 6 please follow
[these instructions][upgrade-18] before.

[upgrade-18]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.19/upgrading/