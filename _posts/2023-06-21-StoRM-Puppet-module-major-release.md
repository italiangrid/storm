---
layout: post
title: StoRM Puppet mudule v4.0.0 released
author: enrico
summary: With its latest release, StoRM Puppet module has been updated to Puppet v7
---

> It's highly recommended to configure latest StoRM v1.11.22 release with latest StoRM Puppet module.

Due to the fact that both Puppet v5.x and v6.x have reached EOL, the v4.0.0 release of StoRM Puppet module is supported only on Puppet v7.

### How to install Puppet 7 on RHEL 7

First of all install Puppet 7 repositories:

```
rpm -Uvh https://yum.puppet.com/puppet7-release-el-7.noarch.rpm
rpm -Uvh https://yum.puppet.com/puppet-tools-release-el-7.noarch.rpm
```

Install Puppet latest GPG key:

```
wget http://yum.puppet.com/RPM-GPG-KEY-puppet-20250406
rpm --import RPM-GPG-KEY-puppet-20250406 && \
```

Finally, you can install Puppet service:

```
yum install -y puppet
```

### Puppet v4.0.0 CHANGELOG

- Puppet version >= 7.0.0 is required
- Added `storm::gridftp::data_interface` parameter in order to support a configuration behind a public IP
- Added `storm::gridftp::log_level` parameter in order to allow logging level
- Removed `storm::webdav::ensure_empty_storage_area_dir`: storage area directory is now automatically purged of not managed properties files
- Added `storm::webdav::tpc_enable_expect_continue_threshold` in order to set a threshold from which an header with `Expect: 100 continue` is added
- Added `storm::webdav::user` parameter in order to allow the setting of a custom user used to run the service
- Added `storm::webdav::tape_wellknown_source` parameter in order to specify which JSON file has to be loaded as content of the well-known endpoint for WLCG Tape REST API implementation
- Within `update-site-report` script, JSON file is now moved and not copied to avoid the growth of useless files in `tmp` directory
- Removed `storm::backend::debug*` parameters
- Removed `storm::backend::install_native_libs_gpfs` parameter because the installation of the GPFS libs is done if at least one storage area has `fs_type` GPFS.
- Added a retry mechanism if StoRM Info Provider configure command fails
- Removed the custom management of systemctl daemon reload because now it's supported by Puppet 7

More info [here](https://forge.puppet.com/modules/cnafsd/storm/changelog)