---
layout: default
title: "StoRM v.1.11.12 - release notes"
release_date: "01.09.2017"
rfcs:
  - id: STOR-282
    title: Fix overlapping virtual filesystems error in StoRI children creation
  - id: STOR-898
    title: Storage-area resolution fails on moving resources through different storage-areas
  - id: STOR-925
    title: GPFS drops dev prefix in mtab causing StoRM backend sanity check to fail
  - id: STOR-929
    title: Fix StoRM Recall Interface
features:
  - id: STOR-441
    title: Migrate RESTFul services to Jersey 2.x
  - id: STOR-930
    title: Add Metadata Endpoint
  - id: STOR-945
    title: Move to Java 1.8
  - id: STOR-946
    title: Remove storm-gridhttps-plugin configuration
components:
  - name: StoRM Backend
    package: storm-backend-server
    version: 1.11.12
  - name: StoRM Frontend
    package: storm-frontend-server
    version: 1.8.10
  - name: StoRM WebDAV
    package: storm-webdav
    version: 1.0.5
  - name: YAIM StoRM
    package: yaim-storm
    version: 4.3.9
  - name: StoRM Native Libs
    package: storm-native-libs
    version: 1.0.5
  - name: CDMI StoRM
    package: cdmi-storm
    version: 0.1.0
---

## StoRM v. 1.11.12

Released on **{{ page.release_date }}**.

Supported platforms: <span class="label label-success">CentOS 6</span>

#### Description

This release provides fixes to some outstanding bugs and improvements:

* requires Java 8;
* fixes the insertion of a file tape recall task in the [recall task management REST API][recall-task-rest-api];
* fixes a couple of problems related to namespace and storage area resolution;
* fixes a bug that causes sanity check failures with GPFS version >= 4.2.2;
* adds a [REST API][metadata-rest-api] to query metadata (online/offline status, checksum, etc.)
	about filesystem resources managed by StoRM. The main client of this API is the INDIGO-Datacloud [CDMI StoRM plugin][cdmi-storm-plugin];
* removes the deprecated storm-gridhttps-plugin.

#### Released components

{% include list-components.liquid %}

#### Bug fixes

{% include list-rfcs.liquid %}

#### Enhancements

{% include list-features.liquid %}

#### Upgrade from earlier StoRM versions

Follow the following instructions when you are upgrading StoRM to v1.11.12.

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

#### Clean install

Follow the instructions in the [System Administration Guide][storm-sysadmin-guide].

[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.12
[recall-task-rest-api]: https://github.com/italiangrid/storm/tree/develop/src/main/java/it/grid/storm/tape/recalltable/resources
[metadata-rest-api]: https://github.com/italiangrid/storm/tree/develop/src/main/java/it/grid/storm/rest/metadata
[cdmi-storm-plugin]: {{site.baseurl}}/release-notes/cdmi-storm/0.1.0/
