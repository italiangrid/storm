---
layout: service-guide
title: StoRM System Administration Guide - Quick deploy
navigation:
  - link: documentation/sysadmin-guide/1.11.19/index.html
    label: Index
  - link: documentation/sysadmin-guide/1.11.19/quick-deployments/index.html
    label: Quick Deployments
---

## All-in-one deployment examples <a name="quickdeploy">&nbsp;</a>

The following guides contains examples of quick all-in-one deployments for CentOS 6 and CentOS 7.

The simplest example of a StoRM deployment can be done by installing all the components on a single host,
assuming that:

- the host satisfies the [StoRM Installation Prerequisites][INSTALL-PREREQ]
- the StoRM repositories have been installed (see [repositories section][REPOSETTINGS]).

Currently, administrators can use YAIM to configure StoRM on CentOS 6 and the provided StoRM Puppet module for CentOS 7 platform.

* [All-in-one deployment on CentOS 7 with Puppet](centos7/index.html)
* [All-in-one deployment on CentOS 6 with YAIM](centos6/index.html)


[INSTALL-PREREQ]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.19/installation-prerequisites/index.html
[REPOSETTINGS]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.19/repositories/index.html