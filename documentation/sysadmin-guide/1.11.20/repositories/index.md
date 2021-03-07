---
layout: service-guide
title: StoRM System Administration Guide - Repositories
navigation:
  - link: documentation/sysadmin-guide/1.11.20/index.html
    label: Index
  - link: documentation/sysadmin-guide/1.11.20/repositories/index.html
    label: Repositories
---

## Repositories <a name="repositories">&nbsp;</a>

Before installing StoRM components, make sure you have checked the prerequisites and installed the needed repositories:

* EPEL
* UMD4
* EGI Trust Anchors
* StoRM

StoRM packages can be obtained from StoRM product team package repository and the UMD repositories.
The EPEL and EGI Trust Anchors repositories are also required.

{% include_relative epel.md %}
{% include_relative umd.md %}

{% include_relative egi.md %}
{% include_relative storm.md %}
