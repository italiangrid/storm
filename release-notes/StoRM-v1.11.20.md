---
layout: default
title: "StoRM v.1.11.20 - release notes"
release_date: "12.04.2021"
components:
  - name: StoRM Backend
    package: storm-backend-server
    version: 1.11.20
    platforms:
      - centos7
  - name: StoRM Native Libs
    package: storm-native-libs
    version: 1.0.6-2
    platforms:
      - centos7
  - name: StoRM WebDAV
    package: storm-webdav
    version: 1.4.0
    platforms:
      - centos7
  - name: StoRM Frontend
    package: storm-frontend-server
    version: 1.8.14
    platforms:
      - centos7
rfcs:
  - id: STOR-1298
    title: StoRM WebDAV rpm doesn't set the proper ownership on /var/log/storm
  - id: STOR-1332
    title: Login with OIDC button displayed only on storage area index page
  - id: STOR-1335
    title: Login with OIDC button not shown for error pages
features:
  - id: STOR-1173
    title: Add the average time in the summary for round monitoring
  - id: STOR-1336
    title: Add support for externalized session management
  - id: STOR-1342
    title: Cleanup frontend codebase
  - id: STOR-1351
    title: StoRM webdav should include user traceability information in access log
  - id: STOR-1357
    title: StoRM Backend and native libs should run with Java 11
  - id: STOR-1358
    title: StoRM WebDAV package should install Java 11
---

## StoRM v. 1.11.20

Released on **{{ page.release_date }}**.

#### Description

This release:

* requires and install Java 11 for all the Java components;
* fixes some bugs about StoRM WebDAV OIDC login button;
* fixes a ownership issue on logging directory on StoRM WebDAV rpm;
* adds support for externalized session management for StoRM WebDAV;
* includes user traceability information in StoRM WebDAV access log
* fixes several minor codebase issues on frontend, some of them could cause a memory leak;
* adds the average time in the summary for round frontend's monitoring log.

#### Released components

{% include list-components.liquid %}

#### Bug fixes

{% include list-rfcs.liquid %}

#### Enhancements

{% include list-features.liquid %}

#### Installation/Upgrade

If you're upgrading from StoRM v1.11.19, first of all upgrade all the released packages:

```
yum update -y storm-backend-server storm-webdav storm-frontend-server
```

The update will upgrade also the native libraries as a dependency. <br/>
After the successful upgrade the services will be restarted and you should have both Java 1.8 and Java 11 installed, but
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

In case you have any kind of questions or problems please contact us.

Read more info about upgrading [here][upgrade-from-19]


[downloads-page]: {{site.baseurl}}/download.html#stable-releases
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide

[upgrade-from-19]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.20/upgrading/