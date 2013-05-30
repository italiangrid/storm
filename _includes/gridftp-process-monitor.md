### GridFTP process monitor

This guide is about how to configure the gridFTP process monitor in order to balance the transfer load among multiple gridFTP servers.

#### RPMs

* DIM v17 (–nodeps on SL4)

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
