---
layout: default
title: "StoRM native libs v.1.0.7 release notes"
release_date: "21.06.2020"
rfcs:
  - id: STOR-1503
    title: Avoid loading and enforcing ACL mask and leave it to be automatically updated

---

## StoRM native-libs v. 1.0.7

Released on **{{ page.release_date }}** with [StoRM v. 1.11.22][release-notes].
<span style="float: right; margin-left: 8px;" class="label label-info">centos7</span>

This release avoid loading and enforcing ACL mask from a file and leave it as automatically enforced by the underline Filesystem.

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

Upgrade package:

```bash
yum update storm-native-libs storm-native-libs-gpfs
```

and restart StoRM Backend:

```
systemctl restart storm-backend-server
```

Read more at:
* [StoRM Puppet module forge page][stormpuppetmodule];
* [StoRM Puppet module generated documentation][stormpuppetmoduledoc];
* the [Quick deploy on CentOS7][quickdeploy] guide.

[stormpuppetmodule]: https://forge.puppet.com/cnafsd/storm
[stormpuppetmoduledoc]: https://italiangrid.github.io/storm-puppet-module/

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.22.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.22

[quickdeploy]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.22/quick-deployments/centos7/
