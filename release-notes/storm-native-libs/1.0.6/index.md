---
layout: default
title: "StoRM native libs v.1.0.6 release notes"
release_date: "07.08.2020"
rfcs:
  - id: STOR-1219
    title: Fix ACL setting problems on storm-native-libs built against GPFS > 3.4

---

## StoRM native-libs v. 1.0.6

Released on **{{ page.release_date }}** with [StoRM v. 1.11.18][release-notes].
<span style="float: right; margin-left: 8px;" class="label label-success">centos6</span>
<span style="float: right; margin-left: 8px;" class="label label-info">centos7</span>

### Bug fixes

{% include list-rfcs.liquid %}

### Installation and configuration

On RHEL6, update package:

```bash
yum update storm-native-libs storm-native-libs-gpfs
```

and run YAIM.

On RHEL7, the native libs are installed through StoRM Backend Puppet module. The GPFS libs are not installed by default.
They have to be enabled by adding a:

```puppet
class { 'storm::backend':
  ...
  install_native_libs_gpfs => true,
  ...
}
```

Read more at:
* [StoRM Puppet module forge page][stormpuppetmodule];
* [StoRM Puppet module generated documentation][stormpuppetmoduledoc] forge page;
* [Puppet Configuration][puppetconf] section of [System Administration Guide][storm-sysadmin-guide];
* the [Quick deploy on CentOS7][quickdeploy] guide.

[stormpuppetmodule]: https://forge.puppet.com/cnafsd/storm
[stormpuppetmoduledoc]: https://italiangrid.github.io/storm-puppet-module/
[puppetconf]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.18#puppetconfiguration
[quickdeploy]: {{site.baseurl}}/documentation/how-to/basic-storm-standalone-configuration-centos7/1.11.18/

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.18.html
[storm-documentation]: {{site.baseurl}}/documentation.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.18
