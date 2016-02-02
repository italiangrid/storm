---
layout: default
title: "StoRM v.1.11.11 - release notes"
release_date: "05.02.2016"
rfcs:
- id: STOR-867
  title: Backend initialization fails when storage area has a default acl list
components:
    - name: StoRM Backend
      package: storm-backend-server
      version: 1.11.11
---

## StoRM v. 1.11.11

Released on **{{ page.release_date }}**

### Description

This release provides an important bug fix on StoRM Backend due to a covariance issue on Java code.
Then, it's **HIGHLY RECOMMENDED** to upgrade your installation.

### Released components

{% include list-components.liquid %}

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

Packages can be obtained from our repositories (see the instructions in the [download section][download-page]) and will soon be available on the EMI-3 repository.

After the upgrade, simply restart StoRM Backend:

```bash
service storm-backend-server restart
```

Alternatively, you can run YAIM.

You can find more information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide][storm-sysadmin-guide].

[storm-documentation]: {{site.baseurl}}/documentation.html
[download-page]: {{site.baseurl}}/download.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/
