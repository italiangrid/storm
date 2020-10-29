---
layout: default
title: "StoRM BackEnd v. 1.11.19 release notes"
release_date: "29.10.2020"
rfcs:
  - id: STOR-1260
    title: BoL and PtG requests statuses not updated after recall success
  - id: STOR-1267
    title: Uncaught RuntimeException raised when user.storm.pinned attribute is not found causes SRM_INTERNAL_ERROR during srmReleaseFiles
features:
  - id: STOR-1251
    title: Include jetty handler metrics reporting in storm-backend-metrics log
---

## StoRM Backend v. 1.11.19

Released on **{{ page.release_date }}** with [StoRM v. 1.11.19][release-notes].

### Description

This release:

- fixes a bug introduced with StoRM v1.11.18 about the final update of the status on
database of a srmPtG or srmBoL requests;
- introduces new metrics in storm-backend-metrics.log.

### Bug fixes

{% include list-rfcs.liquid %}

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

If you're upgrading, you can update and restart service:

```bash
yum update storm-backend-server
service storm-backend-server restart
```

or

```
yum update storm-backend-server
systemctl restart storm-backend-server
```

In case of a clean installation please read the [System Administrator Guide][storm-sysadmin-guide].

Read more at:
* the [Quick deploy on CentOS7][quickdeploy] guide;
* [StoRM Puppet module forge page][stormpuppetmodule];
* [StoRM Puppet module generated documentation][stormpuppetmoduledoc] forge page.

[stormpuppetmodule]: https://forge.puppet.com/cnafsd/storm
[stormpuppetmoduledoc]: https://italiangrid.github.io/storm-puppet-module/
[puppetconf]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.19#puppetconfiguration
[quickdeploy]: {{site.baseurl}}/documentation/how-to/basic-storm-standalone-configuration-centos7/1.11.18/

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.19.html
[upgrading]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.19/#upgrading
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.19
[duservice]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.19#duserviceconfiguration
