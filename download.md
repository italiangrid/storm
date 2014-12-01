---
layout: default
title: StoRM releases
---

# StoRM releases

The current release is [StoRM v.1.11.5]({{ site.baseurl }}/release-notes/StoRM-v1.11.5.html).

{% include releases.liquid %}

Information about previous releases can be found [here](releases.html) or on the [EMI website](http://www.eu-emi.eu).

### Repository configuration 

StoRM packages can be obtained from the EMI repository or from the StoRM product team package repository.

#### EMI 3 

You can find [general EMI 3 installation instructions](https://twiki.cern.ch/twiki/bin/view/EMI/GenericInstallationConfigurationEMI3) on the EMI site, but it basically boils down to installing the EMI repository

	rpm --import http://emisoft.web.cern.ch/emisoft/dist/EMI/3/RPM-GPG-KEY-emi
	wget http://emisoft.web.cern.ch/emisoft/dist/EMI/3/sl5/x86_64/base/emi-release-3.0.0-2.el5.noarch.rpm
	yum localinstall -y emi-release-3.0.0-2.el5.noarch.rpm

Follow the [system administration guide]({{ site.baseurl }}/documentation/sysadmin-guide/{{ site.sysadmin_guide_version}}/) for detailed installation instructions.

#### StoRM

Note that the StoRM PT repositories only provide the latest version of the certified StoRM packages.
You still need to install EMI3 repositories (as detailed above) for installations to work as expected.

To install the repository files, run the following commands (as root):

    (SL5) # wget http://italiangrid.github.io/storm/repo/storm_sl5.repo -O /etc/yum.repos.d/storm_sl5.repo
    (SL6) # wget http://italiangrid.github.io/storm/repo/storm_sl6.repo -O /etc/yum.repos.d/storm_sl6.repo

---

### Preview releases

Preview versions can be found in the preview repositories for [SL5][preview_repo_sl5] and [SL6][preview_repo_sl6].

---

### Nightly builds

Development versions are built regularly on our [continuous integration infrastructure](http://radiohead.cnaf.infn.it:9999/view/STORM/).

Artifacts for the development branch (not recommended for production) can be found in the development
yum repositories for [SL5][devel_repo_sl5] and [SL6][devel_repo_sl6].

---

### Source code

The StoRM source is available on [Github](https://github.com) in the following repositories:

- [StoRM Backend](https://github.com/italiangrid/storm)
- [StoRM Frontend](https://github.com/italiangrid/storm-frontend)
- [StoRM GridHTTPs Server](https://github.com/italiangrid/storm-gridhttps-server)
- [StoRM Native Libs](https://github.com/italiangrid/storm-native-libs)
- [YAIM StoRM](https://github.com/italiangrid/yaim-storm)
- [StoRM info provider](https://github.com/italiangrid/storm-info-provider)
- [StoRM client](https://github.com/italiangrid/storm-client)
- [StoRM XMLRPC api](https://github.com/italiangrid/storm-xmlrpc-api)
- [StoRM GridHTTPs plugin](https://github.com/italiangrid/storm-gridhttps-plugin)
- [StoRM GridFTP](https://github.com/italiangrid/storm-gridftp-dsi)

[devel_repo_sl5]: {{site.baseurl}}/repo/storm_develop_sl5.repo
[devel_repo_sl6]: {{site.baseurl}}/repo/storm_develop_sl6.repo
[preview_repo_sl5]: {{site.baseurl}}/repo/storm_preview_sl5.repo
[preview_repo_sl6]: {{site.baseurl}}/repo/storm_preview_sl6.repo
