---
layout: post
title: Updating to UMD-4
author: enrico
summary: StoRM v1.11.13 officially supports UMD-4 repository for SL6.<br/>Migrating from UMD-3 is encouraged and needed to install latest StoRM versions.
---

StoRM v1.11.11 is the latest StoRM released into UMD-3 repositories.<br/>
From version 1.11.12, StoRM packages have been released **only into UMD-4** for SL6 repositories.<br/>
You can find all the complete installation instructions at [EGI Software Repository][umd-4-page] page.

In short, you can follow the instructions reported below, in case you're installing them on a clean host or upgrading from UMD-3.

### Clean installation

Install UMD rpm pgp-key:

    rpm --import http://repository.egi.eu/sw/production/umd/UMD-RPM-PGP-KEY

Download and install UMD release:

    wget http://repository.egi.eu/sw/production/umd/4/sl6/x86_64/updates/umd-release-4.1.3-1.el6.noarch.rpm
    yum localinstall umd-release-4.1.3-1.el6.noarch.rpm

### Upgrading from UMD-3

Remove old UMD repositories:

    yum remove umd-release

And install UMD-4 repositories for SL6:

    yum localinstall http://repository.egi.eu/sw/production/umd/4/sl6/x86_64/updates/umd-release-4.1.3-1.el6.noarch.rpm



[umd-4-page]: http://repository.egi.eu/category/umd_releases/distribution/umd-4