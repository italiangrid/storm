---
layout: default
title: Troubleshooting - StoRM common issues - analysis and solution
assetsdir: ../assets
rootdir: ..
---

## Troubleshooting - StoRM common issues: analysis and solution

<img src="{{ page.assetsdir }}/images/troubleshooting.jpeg" width="250" style="float:right;"/>
### Table of contents

* [System behavior in case of daemons problem](#sys-behavior-in-case-of-daemons-problem)
  * [Frontend down](#fe-down)
  * [Backend down](#be-down)
* [Problem starting the StoRM daemons](#problem-starting-daemons)
  * [Frontend problem](#fe-starting-problem)
  * [Backend problem](#be-starting-problem)
* [Authorization problem](#auth-problem)
  * [Frontend Auth problem](#fe-auth-problem)
  * [Backend Auth problem](#be-auth-problem)
  * [GridFTP Server](#gftp-server)

<a name="sys-behavior-in-case-of-daemons-problem">&nbsp;</a>
### System behavior in case of daemons problem

These are the typical errors in case one or more StoRM daemons are not running properly on your system. These tests are done with the simple SRMv2.2 Command Line Client available with StoRM (see the [StoRM clientSRM user guide](clientSRM-guide.html)).

<a name="fe-down">&nbsp;</a>
#### Frontend down

In case the StoRM Frontend is down, a simple *srmPing* request will fail with the error:

	$clientSRM ping  -e httpg://your_storm_host:8444
	
	============================================================
	Sending Ping request to: vgrid05.cnaf.infn.it:8444
	============================================================
	Request status:
	gSoap code: 12
	
	soap_print_fault:
	SOAP FAULT: SOAP-ENV:Client
	"CGSI-gSOAP: Could not open connection !"
	Detail: TCP connect failed in tcp_connect()
	
	
	soap_print_fault_location:
	============================================================
	Please note you will get the same error if the endpoint is wrong, as for wrong hostname or port.

<a name="be-down">&nbsp;</a>
#### Backend down

In case the Backend daemon is down, you could get different kinds of errors depending on the SRM request you are trying:

**srmPing operation**

	$  ./clientSRM ping -e httpg://your_storm_host:8444  

	============================================================
	Sending Ping request to: httpg://vgrid05.cnaf.infn.it:8444
	============================================================
	Request status:
	  statusCode="SRM_SUCCESS"(0)
	  explanation="SRM server successfully contacted"  
	============================================================
	SRM Response:
	  versionInfo="v2.2"
	  otherInfo (size=2)
		[0] key="backend_type"
		[0] value="StoRM"
		[1] key="backend_version"
		[1] value="FE:1.3.20 BE:ERROR"
	============================================================

**srmLs/MkDir/Mv/Rm/Rmdir**

	$ ./clientSRM ls -e httpg://your_storm_host:8444 -s httpg://vgrid05.cnaf.infn.it:8444/dteam    
	============================================================
	Sending Ls request to: httpg://vgrid05.cnaf.infn.it:8444
	============================================================
	Request status:
	  statusCode="SRM_INTERNAL_ERROR"(14)
	  explanation="Client transport failed to execute the RPC.  HTTP response: 0"
	============================================================
	SRM Response:
	============================================================  

**srmPrepareToPut/srmPrepareToGet/srmCopy **

	$./clientSRM ptp -e httpg://vgrid05.cnaf.infn.it:8444 -s httpg://vgrid05.cnaf.infn.it:8444  /dteam/f1  -p

	============================================================
	Sending PtP request to: httpg://vgrid05.cnaf.infn.it:8444
	============================================================
	Polling request status:
	Current status: SRM_REQUEST_QUEUED (Ctrl+c to stop polling).....................
	============================================================

<a name="problem-starting-daemons">&nbsp;</a>
### Problem starting the StoRM daemons

<a name="fe-starting-problem">&nbsp;</a>
#### Frontend problem

Frontend fails to start up if:

* MySQL daemon is down. The Frontend service tries to open a connection with DB: if the connection fails, the FE service will remain stopped.
* There is a MySQL authorization problem during the database connection. Please check STORM\_DB\_USER with STORM\_DB\_PASSWORD and STORM\_DB\_HOST can connect to DB.
* There is a permission problem on the Frontend installation directory. Please check that everything belongs to STORM\_USER.

<a name="be-starting-problem">&nbsp;</a>
#### Backend problem

Backend fails to start up if:

* The storage file system is not mounted.
* The configured storage directory does no exits.
* The configuration file is not semantically correct.
* Permission problem on the Backend installation directory. Please check that everything belongs to STORM\_USER

<a name="auth-problem">&nbsp;</a>
### Authorization problem

One of the most common issues for Grid services is to have some kinds of misconfigurations in the host machine that produce an authorization problem.

<a name="fe-auth-problem">&nbsp;</a>
#### Frontend Auth problem

In case the Frontend host machine is not properly configured for what concern authorization, as for:

* missing the host certificate
* missing user in the file /etc/grid-security/grid-mapfile
* not updated CA rpms.

You will get the following error messages:

	$./clientSRM  ping -e httpg://your_storm_fqdn:8444  

	============================================================
	Sending Ping request to: vgrid06.cnaf.infn.it:8444
	============================================================
	Request status:
	gSoap code: -1

	soap_print_fault:
	SOAP FAULT: SOAP-ENV:Client
	"CGSI-gSOAP: Error reading token data header: Connection closed"
  
	soap_print_fault_location:
	============================================================

**Enabling GSOAP tracefile**

Set the following environment variables :

	$CGSI_TRACE=1 
	$CGSI_TRACEFILE=/tmp/tracefile

and restart the Frontend daemon.
Within the file /tmp/tracefile there are also the error messages.

<a name="be-auth-problem">&nbsp;</a>
#### Backend Auth problem

In case the Backend host machine is not properly configured for what concern authorization, you will have different behaviour depending on the case.
In case of:

* missing the host certificate
* not updated CA rpms

the Backend service will be able to perform simple SRM requests, but this misconfiguration will cause problem in any case of interaction with other Grid services, as for the srmCopy operation.

But in case of:

* missing user (or user FQAN ) in the /etc/grid-security/gridmapfile and /etc/grid-security/groupmapfile:

In case of operation that requires ACL set up, as for srmPrepareToPut, srmPrepareToGet, Mkdir, etc, user will get:

	$ clientSRM  ptp -e vgrid01.cnaf.infn.it:8444 -s srm://vgrid01.cnaf.infn.it:8444/dteam/test8  

	============================================================
	Sending PtP request to: vgrid01.cnaf.infn.it:8444
	============================================================
	Polling request status: 
	Current status: SRM_REQUEST_QUEUED (Ctrl+c to stop polling)..
	============================================================
	Request status:
	  statusCode="SRM_FAILURE"(1)
	  explanation="All chunks failed!"
	============================================================
	SRM Response:
	  requestToken="37d3d1c0-38db-465a-baac-17edd878dd43"
	  remainingTotalRequestTime=0
	  arrayOfFileStatuses (size=1)
	      [0] SURL="srm://vgrid01.cnaf.infn.it:8444/dteam/test8"
	      [0] status: statusCode="SRM_FAILURE"(1)
	                  explanation="Unable to map grid credentials to local user!"
	      [0] estimatedWaitTime=-1
	============================================================

<a name="gftp-server">&nbsp;</a>
#### GridFTP Server

It is really important that also the GridFTP server host machine be properly configured in term of authorization.

In case of:

* missing the host certificates

trying a simple file transfer in /tmp will produce:

	$ globus-url-copy -vb file:///home/lucamag/clientSRM_sl4/clientSRM    gsiftp://vgrid06.cnaf.infn.it:2811/tmp/prova

	error: globus_ftp_client: the server responded with an error
	530 530-globus_xio: Server side credential failure
	530-globus_gsi_gssapi: Error with gss credential handle
	530-globus_credential: Valid credentials could not be found in any of the possible locations   specified by the credential search order.
	530-Valid credentials could not be found in any of the possible locations specified by the   credential search order.
	530-
	530-Attempt 1
	530-
	530-globus_credential: Error reading host credential
	530-globus_sysconfig: Could not find a valid certificate file: The host cert could not be found   in: 
	530-1) env. var. X509_USER_CERT
	530-2) /etc/grid-security/hostcert.pem
	530-3) $GLOBUS_LOCATION/etc/hostcert.pem
	530-4) $HOME/.globus/hostcert.pem
	530-
	530-The host key could not be found in:
	530-1) env. var. X509_USER_KEY
	530-2) /etc/grid-security/hostkey.pem
	530-3) $GLOBUS_LOCATION/etc/hostkey.pem
	530-4) $HOME/.globus/hostkey.pem
	...

and in case of

* not updated CA rpms

it will produce:

	$ globus-url-copy -vb file:///home/lucamag/clientSRM_sl4/clientSRM    gsiftp://vgrid06.cnaf.infn.it:2811/tmp/prova

	error: globus_ftp_client: the server responded with an error
	530 530-globus_xio: Server side credential failure
	530-globus_sysconfig: Could not find a valid trusted CA certificates directory
	530-globus_sysconfig: Could not find a valid trusted CA certificates directory
	530-globus_sysconfig: File does not exist: /etc/grid-security/certificates/ is not a valid   directory
	530 End.

But the two most significative error messages a site admin needs to know are:

**Missing user (or user FQAN ) in the /etc/grid-security/grid-mapfile**

	$ globus-url-copy -vb file:///home/lucamag/clientSRM_sl4/clientSRM    gsiftp://vgrid06.cnaf.infn.it:2811/tmp/prova

	Source: file:///home/lucamag/clientSRM_sl4/
	Dest:   gsiftp://vgrid06.cnaf.infn.it:2811/tmp/
	  clientSRM  ->  prova


	error: an end-of-file was reached
	globus_xio: An end of file occurred

**Wrong permission on file**

	$ globus-url-copy -vb file:///home/lucamag/clientSRM_sl4/clientSRM    gsiftp://vgrid06.cnaf.infn.it:2811/tmp/prova_root

	error: globus_ftp_client: the server responded with an error
	500 500-Command failed. : globus_l_gfs_file_open failed.
	500-globus_xio: Unable to open file /tmp/prova_root
	500-globus_xio: System error in open: Permission denied
	500-globus_xio: A system call failed: Permission denied
	500 End.
