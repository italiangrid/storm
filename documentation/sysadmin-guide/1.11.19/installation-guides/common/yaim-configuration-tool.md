---
layout: service-guide
title: StoRM YAIM configuration - YAIM
navigation:
  - link: documentation/sysadmin-guide/1.11.19/index.html
    label: Index
  - link: documentation/sysadmin-guide/1.11.19/installation-guides/index.html
    label: Installation Guides
  - link: documentation/sysadmin-guide/1.11.19/installation-guides/common/yaim-configuration-tool.html
    label: YAIM configuration tool
---

# YAIM configuration tool <a name="yaimconfiguration">&nbsp;</a>

StoRM for CentOS 6 is currently configured using the [YAIM][yaim-site] tool.

The aim of YAIM (Yet Another Installation Manager) is to implement a configuration method for the gLite software. YAIM is a set of bash scripts and functions. YAIM is distributed in rpm form and it usually resides in `/opt/glite/yaim`.
In order to configure a site, one or more configuration files are edited and the YAIM script is then executed. Since YAIM is mainly bash, all the configuration files have to follow the bash syntax. For example, no space between the equal sign and the key-value variables are allowed.

WRONG :

```
SITE_NAME = NorthPole
```

CORRECT:

```
SITE_NAME=NorthPole
```

A good syntax test for the site-info.def is to source it:

```
source ./site-info.def
```

and look for errors. YAIM will check this for you anyway.

The most common way to configure a service with YAIM is to create a **site-info.def** file in your configuration directory.
For example, create `/etc/storm/site-info.def` and add all the variables summarized in next sections as your needed.


[yaim-site]: https://twiki.cern.ch/twiki/bin/view/EGEE/YAIM
