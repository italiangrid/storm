---
layout: default
title: "StoRM Frontend v.1.8.12 release notes"
release_date: "28.02.2019"
rfcs:
  - id: STOR-1021
    title: Request queue length monitoring in the FE
  - id: STOR-1025
    title: Configurable size limit for the request queue
features:
  - id: STOR-1020
    title: Remove functions and code related to old and deprecated variables
  - id: STOR-1029
    title: Address frontend compilation warnings
---

## StoRM Frontend v.1.8.12

Released on **{{ page.release_date }}** with [StoRM v. 1.11.15][release-notes].

### Description

This release:

* avoids an indefinitely growth of the threads queue size due to the the
incoming requests to Frontend and the consequent block of the acceptor thread;
* print queue length information into `monitoring.log`; 
* clears old code and stuff related to deprecated variables.

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

Update package:

```
yum update storm-frontend-server
```

and run YAIM (**mandatory**).

**Important**: StoRM Frontend 1.8.12 introduces changes in the main
configuration file:

```
/etc/storm/frontend-server/storm-frontend-server.conf
```

The useless parameters:

```
# Proxy information
proxy.dir = ...
proxy.user = ...
```

have been deprecated and no more supported. Frontend won't start if these
variables are not deleted. Latest YAIM version automatically fix it.

If your configuration file is for some reason managed by puppet or other
tools, update your server configuration.

You can find information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.15.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15
