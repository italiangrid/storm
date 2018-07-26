---
layout: default
title: StoRM releases
---

# StoRM releases

The current release is [StoRM v.{{site.latest_release}}][latest_release_notes].

{% include download/components-list.html %}

Information about previous releases can be found [here](releases.html) or on the [EMI website](http://www.eu-emi.eu).

### Stable releases <a name="stable-releases">&nbsp;</a>

Latest StoRM packages released can be found in the [StoRM stable SL6 repository][stable-repoview].

To install StoRM stable repository:

- clean all old StoRM repository files:

```
rm -rf /etc/yum.repos.d/storm_*.repo
```

- download stable repository [.repo][stable-repo-file] file:

```
wget https://repo.cloud.cnaf.infn.it/repository/storm/stable/storm-stable-centos6.repo -O /etc/yum.repos.d/storm-stable-centos6.repo
```

Note that you should also have UMD repositories installed for your setup 
to work as expected.

Read and follow [UMD repositories installation instructions][umd-install-guide].

---

### Preview releases

Packages ready to be tested but not officially released can be found in the [StoRM beta SL6 repository][beta-repoview].

Download beta repository [.repo][beta-repo-file] file as follow:

```
wget https://repo.cloud.cnaf.infn.it/repository/storm/beta/storm-beta-centos6.repo -O /etc/yum.repos.d/storm-beta-centos6.repo
```

---

### Nightly builds

Development packages are built regularly on our continuous integration infrastructure.
The nigthly built packages can be found in the [StoRM nightly SL6 repository][nightly-repoview].

Download nightly repository [.repo][nightly-repo-file] file as follow:

```
wget https://repo.cloud.cnaf.infn.it/repository/storm/nightly/storm-nightly-centos6.repo -O /etc/yum.repos.d/storm-nightly-centos6.repo
```

---

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


[latest_release_notes]: {{site.baseurl}}{{site.releasenotes_path}}/StoRM-v{{site.latest_release}}.html

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