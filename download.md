---
layout: default
title: StoRM releases
---

## StoRM

{% include download/current-release.html %}

### Components

{% include download/components-list.html %}

### Repositories <a name="stable-releases">&nbsp;</a>

> <span class="label label-success">README</span> Since
[StoRM v1.11.14]({{site.baseurl}}/release-notes/StoRM-v1.11.14.html)
the **stable repository has been migrated** and beta and nightly repositories have
been added. You should clear the old StoRM stable `.repo` file. Don't forget to
disable beta and nightly repositories if you want to install them.

All the StoRM components are certified to work on RHEL 6 platforms.
Some components have also been built for RHEL 7.

[<i class="icon-search"></i> Browse all StoRM's repositories][storm-repo-index].

{% include download/repositories.html %}

> <span class="label label-success">README</span> You should also have UMD repositories installed for your setup to work as
expected. Read and follow [UMD repositories installation instructions][umd-install-guide].

#### Install Repositories

```shell
# Install StoRM RHEL6 stable repository
yum-config-manager --add-repo https://repo.cloud.cnaf.infn.it/repository/storm/stable/storm-stable-centos6.repo

# Install StoRM RHEL6 beta repository and disable it
yum-config-manager --add-repo https://repo.cloud.cnaf.infn.it/repository/storm/beta/storm-beta-centos6.repo
yum-config-manager --disable storm-beta-centos6

# Install StoRM RHEL6 nightly repository and disable it
yum-config-manager --add-repo https://repo.cloud.cnaf.infn.it/repository/storm/nightly/storm-nightly-centos6.repo
yum-config-manager --disable storm-nightly-centos6
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

[storm-repo-index]: https://repo.cloud.cnaf.infn.it/repository/storm/index.html

[stable-repo]: https://repo.cloud.cnaf.infn.it/#browse/browse:storm:stable
[stable-repoview]: https://repo.cloud.cnaf.infn.it/repository/storm/stable/el6/x86_64/repoview/index.html
[stable-repo-file]: https://repo.cloud.cnaf.infn.it/repository/storm/stable/storm-stable-centos6.repo

[beta-repo]: https://repo.cloud.cnaf.infn.it/#browse/browse:storm:beta
[beta-repoview]: https://repo.cloud.cnaf.infn.it/repository/storm/beta/el6/x86_64/repoview/index.html
[beta-repo-file]: https://repo.cloud.cnaf.infn.it/repository/storm/beta/storm-beta-centos6.repo

[nightly-repo]: https://repo.cloud.cnaf.infn.it/#browse/browse:storm:nightly
[nightly-repoview]: https://repo.cloud.cnaf.infn.it/repository/storm/nightly/el6/x86_64/repoview/index.html
[nightly-repo-file]: https://repo.cloud.cnaf.infn.it/repository/storm/nightly/storm-nightly-centos6.repo

[umd-install-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.14/#umdrepos
