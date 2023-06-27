---
layout: default
title: StoRM releases
---

## StoRM

{% include download/current-release.html %}

### Components

{% include download/components-list.html %}

### Platforms

* **CentOS 7** <span class="label label-success">SUPPORTED</span> - All the latest StoRM components are certified to work on **RHEL 7 platform**.

### Repositories <a name="stable-releases">&nbsp;</a>

[<i class="icon-search"></i> Browse all StoRM's repositories][storm-repo-index].

{% include download/repositories.html %}

> <span class="label label-success">README</span> You **must** have **UMD repositories** installed for your setup to work as
expected. Read and follow [UMD repositories installation instructions][umd-install-guide].

#### Install Repositories

Install StoRM RHEL7 package repository as follow:

```shell
yum-config-manager --add-repo https://repo.cloud.cnaf.infn.it/repository/storm/storm-stable-centos7.repo
```

StoRM RHEL6 package repository is still available and can be installed as follow:

```shell
yum-config-manager --add-repo https://repo.cloud.cnaf.infn.it/repository/storm/storm-stable-centos6.repo
```

### Source code

The StoRM source is available on [Github](https://github.com) in the following repositories:

- [StoRM Backend](https://github.com/italiangrid/storm)
- [StoRM Frontend](https://github.com/italiangrid/storm-frontend)
- [StoRM WebDAV](https://github.com/italiangrid/storm-webdav)
- [StoRM Native Libs](https://github.com/italiangrid/storm-native-libs)
- [YAIM StoRM](https://github.com/italiangrid/yaim-storm)
- [StoRM Info Provider](https://github.com/italiangrid/storm-info-provider)
- [StoRM client](https://github.com/italiangrid/storm-client)
- [StoRM XMLRPC api](https://github.com/italiangrid/storm-xmlrpc-api)
- [StoRM GridFTP](https://github.com/italiangrid/storm-gridftp-dsi)
- [CDMI StoRM](https://github.com/italiangrid/cdmi-storm)
- [StoRM Puppet module](https://github.com/italiangrid/storm-puppet-module)
- [StoRM Utils](https://github.com/italiangrid/storm-utils)

[storm-repo-index]: https://repo.cloud.cnaf.infn.it/repository/storm/index.html

[umd-install-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.22/repositories/index.html