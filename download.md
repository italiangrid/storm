---
layout: default
title: StoRM releases
---

# StoRM releases

The current release is [StoRM v.{{site.latest_release}}][latest_release_notes].

{% include download/components-list.html %}

Information about previous releases can be found [here](releases.html) or on the [EMI website](http://www.eu-emi.eu).

### Repository configuration

StoRM packages can be obtained from the UMD repositories for SL6 or from the StoRM product team package repository.

#### UMD

> <span class="label label-info" style="margin-top: -20px;">NEW</span> StoRM v1.11.13 now officially supports UMD4 repo for SL6.

You can find [general UMD 4 installation instructions][UMD4site] on the EGI site, 
but it basically boils down to installing the UMD repositories.

In short:

```shell
# Install UMD rpm pgp-key
rpm --import http://repository.egi.eu/sw/production/umd/UMD-RPM-PGP-KEY
# Download and install UMD release
wget http://repository.egi.eu/sw/production/umd/4/sl6/x86_64/updates/umd-release-4.1.3-1.el6.noarch.rpm
yum localinstall umd-release-4.1.3-1.el6.noarch.rpm
```

Follow the [system administration guide]({{ site.baseurl }}/documentation/sysadmin-guide/) for detailed installation instructions.

#### StoRM

The latest certified [StoRM packages][prod_repo_url] can be found in the StoRM
production repositories for [SL6][prod_repo_sl6].
Note that you should also have UMD repositories installed (as detailed above) for
your setup to work as expected.

To install the StoRM production repository files, run the following commands (as root):

    wget http://italiangrid.github.io/storm/repo/storm_sl6.repo -O /etc/yum.repos.d/storm_sl6.repo

---

### Preview releases

[Preview packages][preview_repo_url] can be found in the preview repositories for [SL6][preview_repo_sl6].

---

### Nightly builds

Development packages are built regularly on our [continuous integration infrastructure][developCI].

Artifacts for the development branch (not recommended for production) can be found in the development
yum repositories for [SL6][devel_repo_sl6].

---

### Source code

The StoRM source is available on [Github](https://github.com) in the following repositories:

- [StoRM Backend](https://github.com/italiangrid/storm)
- [StoRM Frontend](https://github.com/italiangrid/storm-frontend)
- [StoRM WebDAV](https://github.com/italiangrid/storm-webdav)
- [StoRM GridHTTPs Server](https://github.com/italiangrid/storm-gridhttps-server)
- [StoRM Native Libs](https://github.com/italiangrid/storm-native-libs)
- [YAIM StoRM](https://github.com/italiangrid/yaim-storm)
- [StoRM Info Provider](https://github.com/italiangrid/storm-info-provider)
- [StoRM client](https://github.com/italiangrid/storm-client)
- [StoRM XMLRPC api](https://github.com/italiangrid/storm-xmlrpc-api)
- [StoRM GridHTTPs plugin](https://github.com/italiangrid/storm-gridhttps-plugin)
- [StoRM GridFTP](https://github.com/italiangrid/storm-gridftp-dsi)
- [CDMI StoRM](https://github.com/italiangrid/cdmi-storm)


[latest_release_notes]: {{site.baseurl}}{{site.releasenotes_path}}/StoRM-v{{site.latest_release}}.html
[devel_repo_sl6]: {{site.baseurl}}/repo/storm_develop_sl6.repo
[prod_repo_sl6]: {{site.baseurl}}/repo/storm_sl6.repo
[preview_repo_sl6]: {{site.baseurl}}/repo/storm_preview_sl6.repo
[prod_repo_url]: http://ci-01.cnaf.infn.it/download/storm/emi3
[preview_repo_url]: http://ci-01.cnaf.infn.it/download/storm/preview
[developCI]: https://ci.cloud.cnaf.infn.it/job/pkg.storm/job/develop/
[UMD4site]: http://repository.egi.eu/category/umd_releases/distribution/umd-4/
