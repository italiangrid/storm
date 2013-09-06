---
layout: default
title: Cookbook - Useful recipes for StoRM advanced configuration
---

## Cookbook

<img src="{{ site.baseurl }}/assets/images/cookbook.jpg" width="180" style="float:right; margin-top: -30px; margin-right: 0px; margin-bottom: 40px;"/>

### Table of contents

* [How to migrate from classicSE to StoRM](#migrate-from-classicSE)
* [GridFTP process monitor](#gridftp-process-monitor)

### Moving from classicSE to StoRM <a name="migrate-from-classicSE">&nbsp;</a>

Since StoRM does not use a database to store the location of data into the storage system, moving from a classicSE to StoRM is really easy. There are not migration scripts or special procedures: it only requires to install StoRM on the desired host and all your data will be automatically available in GRID through the SRM interface.

#### Instructions

1. Please have a look to the [StoRM requirements]({{site.baseurl}}/documentation/sysadmin-guide/{{site.versions.sysadmin_guide}}/index.html#installprereq).
1. Follow the StoRM [installation]({{site.baseurl}}/documentation/sysadmin-guide/{{site.versions.sysadmin_guide}}/index.html#installationguide) and [configuration]({{site.baseurl}}/documentation/sysadmin-guide/{{site.versions.sysadmin_guide}}/index.html#configuration) guides.
1. After launching YAIM, the StoRM services will be up.

#### Information System

The YAIM installation takes care of installing and configuring all things related to the information system. Once YAIM end, your SE machine will be published as an **SRM 2.2 service**.

#### Catalog

Please follow the instructions [here](https://twiki.cern.ch/twiki/bin/view/LCG/ChangeSeName).

#### Testing the system

The new StoRM installation can be tested by using one of the following SRM clients:

* lcg-utils
* SAM test
* download and use our SRM v2.2 Command Line Client: [clientSRM]({{site.baseurl}}/documentation/clientsrm-guide/)

#### Troubleshooting

For any problem please refer to **storm-support@cnaf.infn.it**.

### GridFTP process monitor <a name="gridftp-process-monitor">&nbsp;</a>

This guide is about how to configure the gridFTP process monitor in order to balance the transfer load among multiple gridFTP servers.

#### RPMs

* DIM v17 (-nodeps on SL4)

  * to avoid forcing the openmotif.i386 package is needed.
  * [RPMs](http://lhcb-daq.web.cern.ch/lhcb-daq/online-rpm-repo/index.html)

* FMC 3.9.7-v1

  * [RPM](http://lhcb-daq.web.cern.ch/lhcb-daq/online-rpm-repo/index.html)

* DIM v18r2 (on client)

  * [Source with Java classes and jdim.so library precompiled for SL3/4](http://dim.web.cern.ch/dim/dim_unix.html)
  * [Documentation and java docs](http://dim.web.cern.ch/dim/)
  * Dimv18r2 jar StoRM build

#### Client (read data)

The client is the host that reads data published by all servers, as retrived from the name server:

*  jdim.so have to be included in JAVA\_LIBRARY\_PATH
*  DIM\_DNS\_NODE have to be setted to the nameserver
*  psViewer can be used to check the whole set of information retrived

#### Servers (publish information)

##### FMC

This is the FMC server:

* set DIM\_DNS\_NODE to the name server host
* Edit file in /etc/sysconfig/fmc
* set to YES only LOG\_SRV
* /etc/init.d/fmc start (use chkconfig to enable start at boot)

##### psSRV

This is the FMC module to read and publish the number of FTP processes running on the machine.

Script in start\_srv su devrb:

	/opt/FMC/sbin/psSrv -l 1 -u 5 -C sshd &

Set also this script to automatically start at boot.

#### Dim DNS server

This is the name server used by the DIM client to know the list of servers that publishes information:

* install DIM v17
* edit /etc/sysconfig/dim with: <code>DNS\_DIM\_NODE=`hostname`</code>
* service dns start

#### Extras

* *did*: is a graphical tool to view the information published
* *psViewer*: is a shell tools on the client machine to view what it is receiving
* other FMC modules allow to monitor cpu usage, network status, file systems, and so on. More investigaion is needed to build interesting monitor on it.
