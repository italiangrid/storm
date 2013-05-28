---
layout: default
title: StoRM Storage Resource Manager
---

# StoRM releases

Releases are done in the context of the EMI project.

You can find [general EMI 3 installation instructions](https://twiki.cern.ch/twiki/bin/view/EMI/GenericInstallationConfigurationEMI3) on the EMI site, but it basically boils down to installing the EMI repository

	rpm --import http://emisoft.web.cern.ch/emisoft/dist/EMI/3/RPM-GPG-KEY-emi
	wget http://emisoft.web.cern.ch/emisoft/dist/EMI/3/sl5/x86_64/base/emi-release-3.0.0-2.el5.noarch.rpm
	yum localinstall -y emi-release-3.0.0-2.el5.noarch.rpm

for having the StoRM packages available
	
	yum search storm

Follow the [system administration guide](sysadmin-guide.html) for detailed installation instructions.

## Current release

The current release is [StoRM v. 1.11.1](release-notes-v1_11_1.html).

## Previous releases

### StoRM v. 1.11.0

This was the StoRM released in EMI-3.

See the [release notes](http://www.eu-emi.eu/releases/emi-3-montebianco/products/-/asset_publisher/5dKm/content/storm-se-2) on the EMI project web pages.

### StoRM v. 1.10.0

This was the first update for StoRM in EMI-2.

See the [release notes](http://www.eu-emi.eu/emi-2-matterhorn/updates/-/asset_publisher/9AgN/content/update-1-20-07-2012#StoRM_SE_v_1_10_0_task_30816) on the EMI project web pages.

### StoRM v. 1.9.0

This was the StoRM released with EMI-2.

See the [release notes](http://www.eu-emi.eu/emi-2-matterhorn-products/-/asset_publisher/B4Rk/content/storm-se-1) on the EMI project web pages.

---

# Testing versions

We are going provide a repository for testing versions, i.e versions for which the development has finished and can be passed to early adopters for the staged roll-out.

---

# Development versions

Development versions are built regularly on our [continuos integration infrastructure](http://radiohead.cnaf.infn.it:9999/view/STORM/). 

Artifacts for the last commit can be found on our yum repos for [SL5](http://radiohead.cnaf.infn.it:9999/view/STORM/job/storm-repo_SL5/lastSuccessfulBuild/artifact/storm.repo) or [SL6](http://radiohead.cnaf.infn.it:9999/view/STORM/job/storm-repo_SL6/lastSuccessfulBuild/artifact/storm.repo).

Install the development repositories like

	wget http://radiohead.cnaf.infn.it:9999/view/STORM/job/storm-repo_SL5/lastSuccessfulBuild/artifact/storm.repo -O /etc/yum.repos.d/storm.repo
