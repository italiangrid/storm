---
layout: post
title: Upgrading to StoRM v1.11.12
author: enrico
summary: StoRM v1.11.12 requires Java 8 and dismisses the use of gridhttps-plugin component that must be removed
---

StoRM v1.11.12 **requires Java 8** and dismisses the use of `gridhttps-plugin` component that **must be removed**.
<br/>So, please, **read carefully** the following instructions.

### Upgrading from StoRM v1.11.11

Follow the following instructions when you are upgrading StoRM from v1.11.11.

##### 1. Update the involved packages

Services to be updated are:

* storm-backend-server
* storm-native-libs
* storm-frontend-server
* storm-webdav
* yaim-storm

Example:

    $ yum update storm-backend-server storm-frontend-server storm-webdav storm-native-libs yaim-storm

##### 2. Update the namespace schema

You should have a `.rpmnew` file on disk:

    $ cd /etc/storm/backend-server
    $ mv namespace-1.5.0.xsd namespace-1.5.0.xsd.rpmold
    $ mv namespace-1.5.0.xsd.rpmnew namespace-1.5.0.xsd

##### 3. Remove `storm-gridhttps-plugin`

It's a component that is no more used and with old java dependencies that **MUST** be removed:

    $ yum remove storm-gridhttps-plugin

##### 4. Remove old Java versions

Since this version, the `storm-backend-server`, `storm-webdav` and `storm-native-libs` rpms explicitly **REQUIRES JAVA 8**.

`java -version` will tell which is the active version on your system:

    $ java -version
    openjdk version "1.8.0_131"
    OpenJDK Runtime Environment (build 1.8.0_131-b11)
    OpenJDK 64-Bit Server VM (build 25.131-b11, mixed mode)

Remove old java versions installed on your system with the following command:

    $ yum remove java-1.6.0-openjdk java-1.7.0-openjdk java-1.7.0-openjdk-devel

If you have a more complex deployment and you can't remove them, you can try to configure the active JRE using update-alternatives:

    $ update-alternatives --config java

##### 5. Relaunch YAIM configuration

Example:

    $ /opt/glite/yaim/bin/yaim -c -s /etc/storm/siteinfo/storm.def \
      -n se_storm_backend \
      -n se_storm_frontend \
      -n se_storm_gridftp \
      -n se_storm_webdav

### Upgrading from earlier versions

Go to [releases page][releases] and read the upgrading instructions from the release notes.

[releases]: {{site.baseurl}}/releases.html
