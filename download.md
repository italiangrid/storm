---
layout: default
title: StoRM Storage Resource Manager
---

# StoRM releases

StoRM packages can be obtained from the EMI repository or from the StoRM product team package repository.

### Repository configuration 

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

### Current release

The current release is [StoRM v.1.11.3]({{ site.baseurl }}/release-notes/StoRM-v1.11.3.html).

{% include download/component-table.html %}

---

### Previous releases

Information about previous releases can be found [here](releases.html) or on the [EMI website](http://www.eu-emi.eu).

---

### Nightly builds

Development versions are built regularly on our [continuous integration infrastructure](http://radiohead.cnaf.infn.it:9999/view/STORM/). 

Artifacts for the last commit can be found on our yum repos for [SL5](http://radiohead.cnaf.infn.it:9999/view/STORM/job/storm-repo_SL5/lastSuccessfulBuild/artifact/storm.repo) or [SL6](http://radiohead.cnaf.infn.it:9999/view/STORM/job/storm-repo_SL6/lastSuccessfulBuild/artifact/storm.repo).

Install the development repositories like

	wget http://radiohead.cnaf.infn.it:9999/view/STORM/job/storm-repo_SL5/lastSuccessfulBuild/artifact/storm.repo -O /etc/yum.repos.d/storm.repo

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
