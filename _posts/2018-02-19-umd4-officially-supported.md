---
layout: post
title: Migrate to UMD-4 repositories
author: enrico
summary: StoRM v1.11.13 officially supports UMD-4 repository for SL6.<br/>Migrating from UMD-3 is encouraged.
---

StoRM v1.11.11 has been the latest StoRM release within the UMD-3 repositories.
<br/>
Latest StoRM versions still support UMD-3 repositories but **a migration to UMD-4 repositories is encouraged**.
<br/>
Installation instructions can be found within [System Administration Guide][syasdmin-guide].

In short:

    yum remove umd-release
    rpm --import http://repository.egi.eu/sw/production/umd/UMD-RPM-PGP-KEY
    yum localinstall http://repository.egi.eu/sw/production/umd/4/sl6/x86_64/updates/umd-release-4.1.3-1.el6.noarch.rpm

You can find all the complete installation instructions at [EGI Software Repository][umd-4-page] page.

[syasdmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.13/#umdrepos
[umd-4-page]: http://repository.egi.eu/category/umd_releases/distribution/umd-4
[ki-gridmapdir]: https://ggus.eu/?mode=ticket_info&ticket_id=127387