---
layout: default
title: StoRM releases
---

## StoRM

{% include download/current-release.html %}

### Components

{% include download/components-list.html %}

### Repositories <a name="stable-releases">&nbsp;</a>

> <span class="label label-success">README</span> You **must** have **UMD repositories** installed for your setup to work as
expected. Read and follow [UMD repositories installation instructions][umd-install-guide].

> <span class="label label-success">README</span> Since
[StoRM v1.11.18]({{site.baseurl}}/release-notes/StoRM-v1.11.18.html)
the package repository **has been migrated**. Be sure to upgrade your repository configuration.

All the StoRM components are certified to work on RHEL 6 and RHEL 7 platforms.

[<i class="icon-search"></i> Browse all StoRM's repositories][storm-repo-index].

{% include download/repositories.html %}


#### Install Repositories

Install StoRM RHEL6 package repository as follow:

```shell
yum-config-manager --add-repo https://repo.cloud.cnaf.infn.it/repository/storm/stable/storm-stable-centos6.repo
```

Install StoRM RHEL7 package repository as follow:

```shell
yum-config-manager --add-repo https://repo.cloud.cnaf.infn.it/repository/storm/stable/storm-stable-centos7.repo
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

[storm-repo-index]: https://repo.cloud.cnaf.infn.it/repository/storm/index.html

[umd-install-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.18/#umdrepos
