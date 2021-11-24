---
layout: toc
title: StoRM clientSRM user guide
---

# StoRM clientSRM user guide

#### Table of contents

* [SRM clients](#srmclients)
  * [Basic concepts](#basicconcepts)
    * [SRM endpoint](#srmendpoint)
    * [SURL](#surl)
* [StoRM ClientSRM](#clientSRM)
  * [Installation](#clientSRMinstall)
  * [Examples](#clientSRMexamples)
    * [srmPing](#srmPing)
    * [srmMkdir](#srmMkdir)
    * [srmRmdir](#srmRmdir)
    * [srmRm](#srmRm)
    * [srmLs](#srmLs)
    * [srmMv](#srmMv)
    * [srmReserveSpace](#srmReserveSpace)
    * [srmGetSpaceMetadata](#srmGetSpaceMetadata)
    * [srmPrepareToPut](#srmPrepareToPut)
    * [srmPutDone](#srmPutDone)
    * [srmStatusOfPrepareToPutRequest](#srmStatusOfPrepareToPutRequest)
    * [srmPrepareToGet](#srmPrepareToGet)
    * [srmStatusOfGetRequest](#srmStatusOfGetRequest)
    * [srmCopy](#srmCopy)
    * [srmStatusOfCopy](#srmStatusOfCopy)

## SRM clients <a name="srmclients">&nbsp;</a>

StoRM distributes clients for contacting SRM services. These clients are not the StoRM clients but the general purpose SRM clients and can be use to contact any Web Service implementing the SRM interface. This document provides some examples of the main functionalities of the SRM specification v2.2.

### Basic concepts <a name="basicconcepts">&nbsp;</a>

In order to use the SRM services you need:

- A valid personal certificate (so you have to do a *voms-proxy-init* or a *grid-proxy-init*).
- A SRM endpoint to contact.
- to be authorized to contact the SRM.

#### SRM endpoint <a name="srmendpoint">&nbsp;</a>

Generally a SRM endpoint is in the form:

	httpg://host:port/service_path (or http, https).

The endpoint that we use in all the following examples is: 

	httpg://ibm139.cnaf.infn.it:8444/

For this endpoint the service path can be left blank.

#### SURL <a name="surl">&nbsp;</a>

A file (or a directory) is identified by a SURL that has the following format:

	srm://host[:port]/[soap_end_point_path?SFN=]site_file_name

where [...] means optional. The sfile ite\_file\_name must begin with a StFN root. The StFN root must be agreed with the SRM administrator. 
An example of SURL is:

	srm://ibm139.cnaf.infn.it:8444/infngrid/test_file.txt

In this example the StFN root is infngrid and the file name is test\_file.txt.
If the SRM requires a service path, the previous example becomes:

	srm://ibm139.cnaf.infn.it:8444/srm/managerv2?SFN=/infngrid/test_file.txt

where the service path is srm/managerv2.
According to the last example the SRM endpoint should be:

	httpg://ibm139.cnaf.infn.it:8444/srm/managerv2

## StoRM clientSRM <a name="clientSRM">&nbsp;</a>

The clientSRM is the binary of the SRM v2.2 Command Line Client. The client can be used to interact with any SRM v2.2 endpoint. 

### Installation <a name="clientSRMinstall">&nbsp;</a>

To install storm-srm-client, install its metapackage RPM: *emi-storm-srm-client-mp*

	$ yum install emi-storm-srm-client-mp

### Examples <a name="clientSRMexamples">&nbsp;</a>

The following are several examples of clientSRM use.

#### srmPing <a name="srmPing">&nbsp;</a>

Check the state of the SRM.

This function works as an "are you alive" type of call. 
The version of the SRM specification implemented by the server is returned. 
To ping a SRM server using the provided SRM client type: 

	$ clientSRM Ping -e httpg://ibm139.cnaf.infn.it:8444/

The server responds with the implemented SRM version:

	============================================================
	Request status:
	  statusCode="SRM_SUCCESS"(0)
	  explanation="SRM server successfully contacted"
	============================================================
	SRM Response:
	  versionInfo="v2.2"
	  otherInfo=NULL
	============================================================

#### srmMkdir <a name="srmMkdir">&nbsp;</a>

Create a directory in a local SRM space.

To create a directory using the provided SRM client type: 

	# clientSRM mkdir -e httpg://ibm139.cnaf.infn.it:8444/ -s srm://ibm139.cnaf.infn.it:8444/infngrid/test_dir/

And you will get something like this:

	============================================================ 
	Request status: 
	  statusCode="SRM_SUCCESS"(0) 
	  explanation="Directory created with success" 
	============================================================

#### srmRmdir <a name="srmRmdir">&nbsp;</a>

Remove an empty directory in a local SRM space.

To remove an empty directory using the provided SRM client type:

	# clientSRM Rmdir -e httpg://ibm139.cnaf.infn.it:8444/ -s srm://ibm139.cnaf.infn.it:8444/infngrid/test_dir/ 
	
	============================================================
	Request status:
	  statusCode="SRM_SUCCESS"(0)
	  explanation="Directory removed with success!"
	============================================================

#### srmRm <a name="srmRm">&nbsp;</a>

Remove SURLs in the storage system. 

To remove the file test_file.txt inside the directory test_dir type:

	# clientSRM Rm -e httpg://ibm139.cnaf.infn.it:8444/ -s srm://ibm139.cnaf.infn.it:8444/infngrid/test_dir/test_file.txt
	
	============================================================
	Request status:
	  statusCode="SRM_SUCCESS"(0)
	  explanation="All files removed"
	============================================================
	SRM Response:
	  arrayOfFileStatuses (size=1)
	      [0] SURL="srm://ibm139.cnaf.infn.it:8444/infngrid/test_dir/test_file.txt"
	      [0] status: statusCode="SRM_SUCCESS"(0)
	                  explanation="File removed"
	============================================================

#### srmLs <a name="srmLs">&nbsp;</a>

Return a list of files with a basic information. 

To list the content of the directory test\_dir together with all its subdirectories type:

	$ clientSRM ls -e httpg://ibm139.cnaf.infn.it:8444/ -s srm://ibm139.cnaf.infn.it:8444/infngrid/test_dir/ -l
	
	============================================================
	Request status:
	  statusCode="SRM_SUCCESS"(0)
	  explanation="All requests successfully completed"
	============================================================
	SRM Response:
	  details (size=3)
	      [0] SURL="srm://ibm139.cnaf.infn.it:8444/infngrid/test_dir"
	      [0] status: statusCode="SRM_SUCCESS"(0)
	                  explanation="Successful request completion."
	      [1] SURL="srm://ibm139.cnaf.infn.it:8444/infngrid/test_dir/test_file01.txt"
	      [1] size=150000
	      [1] lastModificationTime="Wed Jan 14 11:50:09 CET 1970"
	      [1] status: statusCode="SRM_SUCCESS"(0)
	                  explanation="Successful request completion."
	      [2] SURL="srm://ibm139.cnaf.infn.it:8444/infngrid/test_dir/test_file02.txt"
	      [2] size=14
	      [2] lastModificationTime="Wed Jan 14 11:50:09 CET 1970"
	      [2] status: statusCode="SRM_SUCCESS"(0)
	                  explanation="Successful request completion."
	============================================================

#### srmMv <a name="srmMv">&nbsp;</a>

Move a file from one SRM local path to another SRM local path. 

To move the file test\_file00.txt from the directory test\_dir to the directory test\_dir/sub\_test\_dir type:

	$ clientSRM mv -e httpg://ibm139.cnaf.infn.it:8444/ -s srm://ibm139.cnaf.infn.it:8444/infngrid/test_dir/test_file00.txt -t srm://ibm139.cnaf.infn.it:8444/infngrid/test_dir/sub_test_dir/test_file00.txt
	
	============================================================
	Request status:
	  statusCode="SRM_SUCCESS"(0)
	  explanation="SURL moved with success"
	============================================================

#### srmReserveSpace <a name="srmReserveSpace">&nbsp;</a>

Reserve a space in advance for the upcoming requests to get some guarantee on the file management.

To reserve 10MB of space type:

	$ clientSRM reservespace -e httpg://ibm139.cnaf.infn.it:8444/ -a 10000000 -b 5000000
	
	============================================================
	Request status:
	  statusCode="SRM_SUCCESS"(0)
	  explanation="Space Reservation done"
	============================================================
	SRM Response:
	  sizeOfTotalReservedSpace=10000000
	  sizeOfGuaranteedReservedSpace=5000000
	  lifetimeOfReservedSpace=86400
	  spaceToken="9A5294CB-1201-B2C2-839A-646DDA7D4831"
	============================================================

The returned space token has to be used in all the requests that work on this reserved space.

#### srmGetSpaceMetadata <a name="srmGetSpaceMetadata">&nbsp;</a>

Get information of a space, for which a space token must be provided.

For instance, to retrieve information on the space reserved in the previous example we type:

	$ clientSRM getspacemetadata -e httpg://ibm139.cnaf.infn.it:8444/ -s 9A5294CB-1201-B2C2-839A-646DDA7D4831
	
	============================================================
	Request status:
	  statusCode="SRM_SUCCESS"(0)
	  explanation="All requested Space Information returned successfully."
	============================================================
	SRM Response:
	  arrayOfSpaceDetails (size=1)
	      [0] spaceToken="9A5294CB-1201-B2C2-839A-646DDA7D4831"
	      [0] status: statusCode="SRM_SUCCESS"(0)
	                  explanation="Valid space token"
	      [0] owner="/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Alberto Forti"
	      [0] totalSize=10000000
	      [0] guaranteedSize=5000000
	      [0] unusedSize=10000000
	      [0] lifetimeAssigned=86400
	      [0] lifetimeLeft=86180
	============================================================

#### srmPrepareToPut <a name="srmPrepareToPut">&nbsp;</a>

Write files into the storage. 

Upon the client's request, SRM prepares a TURL so that client can write data into the TURL. 
Lifetime (pinning expiration time) is assigned on the TURL. 
When a specified target space token is provided, the files will be located finally in the targeted space associated with the space token. 
It is an asynchronous operation, and a request token is returned. 
The status may be checked through srmStatusOfPutRequest with the returned request token. 
To write an empty file of 150000 bytes inside the directory test\_dir type:

	$ clientSRM ptp -e httpg://ibm139.cnaf.infn.it:8444/ -s srm://ibm139.cnaf.infn.it:8444/infngrid/test_dir/test_file00.txt,150000 -p
	
	============================================================
	Polling request status:
	Current status: SRM_REQUEST_QUEUED (Ctrl+c to stop).
	Current status: SRM_REQUEST_INPROGRESS (Ctrl+c to stop)......
	============================================================
	Request status:
	  statusCode="SRM_SUCCESS"(0)
	  explanation="All chunks successfully handled!"
	============================================================
	SRM Response:

	  remainingTotalRequestTime=0
	  arrayOfFileStatuses (size=1)
	      [0] SURL="srm://ibm139.cnaf.infn.it:8444/infngrid/test_dir/test_file00.txt"
	      [0] status: statusCode="SRM_SPACE_AVAILABLE"(24)
	                  explanation="srmPrepareToPut successfully handled!"
	      [0] TURL="gsiftp://ibm139.cnaf.infn.it:2811/gpfs/infngrid/test_dir/test_file00.txt"
	============================================================

Since the srmPrepareToPut operation is asynchronous, the "-p" option forces the client to poll the SRM server until the request is managed. 
The file can be filled with the desired data using some transfer service, such as gridFTP, and specifying as a destination the returned TURL.

#### srmPutDone <a name="srmPutDone">&nbsp;</a>

Trigger the data transfering.

After a SrmPrepareToPut, the TURL returned can be used to transfer data using the selected protocol. When the transfer has been successfully executed, the SrmPutDone request has to be done in order to tell the system the transfer is finished. As parameter for the SrmPutDone the SrmPrepareToPut REQUESTTOKEN has to be specified. It can be found in the parameter returned by the SrmPrepareToPut

	$ clientSRM pd -e httpg://ibm139.cnaf.infn.it:8444/ -s srm://ibm139.cnaf.infn.it:8444/infngrid/test_dir/test_file00.txt,150000 -t 122f7d26-6d02-4024-89c3-9921d35b791b

#### srmStatusOfPrepareToPutRequest <a name="srmStatusOfPrepareToPutRequest">&nbsp;</a>

Check the status of the previously requested srmPrepareToPut. 

Request token from srmPrepareToPut must be provided. 
To check the status of the srmPrepareToPut operation of the previous example type:

	$ clientSRM statusptp -e httpg://ibm139.cnaf.infn.it:8444/ -t 122f7d26-6d02-4024-89c3-9921d35b791b -vN
	
	============================================================
	Request status:
	  statusCode="SRM_SUCCESS"(0)
	  explanation="All chunks successfully handled!"
	============================================================
	SRM Response:
	  remainingTotalRequestTime=0
	  arrayOfFileStatuses (size=1)
	      [0] SURL="srm://ibm139.cnaf.infn.it:8444/infngrid/test_dir/test_file00.txt"
	      [0] status: statusCode="SRM_SPACE_AVAILABLE"(24)
	                  explanation="srmPrepareToPut successfully handled!"
	      [0] TURL="gsiftp://ibm139.cnaf.infn.it:2811/gpfs/infngrid/test_dir/test_file00.txt"
	============================================================

#### srmPrepareToGet <a name="srmPrepareToGet">&nbsp;</a>

Bring files online upon the client's request and assign TURL so that client can access the file. 

Lifetime (pinning expiration time) is assigned on the TURL. An example:

	$ clientSRM ptg -e httpg://ibm139.cnaf.infn.it:8444/ -s srm://ibm139.cnaf.infn.it:8444/infngrid/test_dir/test_file01.txt -p
	
	============================================================
	Polling request status:
	Current status: SRM_REQUEST_QUEUED (Ctrl+c to stop).
	Current status: SRM_REQUEST_INPROGRESS (Ctrl+c to stop).......
	============================================================
	Request status:
	  statusCode="SRM_SUCCESS"(0)
	  explanation="Request handled!"
	============================================================
	SRM Response:
	  requestToken="51218a32-edc3-4f14-9ae9-89eec2b52146"
	  remainingTotalRequestTime=0
	  arrayOfFileStatuses (size=1)
	      [0] sourceSURL="srm://ibm139.cnaf.infn.it:8444/infngrid/test_dir/test_file01.txt"
	      [0] fileSize=150000
	      [0] status: statusCode="SRM_FILE_PINNED"(22)
	                  explanation="srmPrepareToGet successfully handled!"
	      [0] transferURL="gsiftp://ibm139.cnaf.infn.it:2811/gpfs/infngrid/test_dir/test_file01.txt"
	============================================================

The file *test\_file01.txt* is pinned and the user can transfer it, out of the SE, by executing a gridFTP specifying the returned *transferURL* as source file.

#### srmStatusOfGetRequest <a name="srmStatusOfGetRequest">&nbsp;</a>

Check the status of the previously requested srmPrepareToGet. 

Request token from srmPrepareToGet must be provided. To check the status of the srmPrepareToGet request of the previous example:

	$ clientSRM statusptg -e httpg://ibm139.cnaf.infn.it:8444/ -t 51218a32-edc3-4f14-9ae9-89eec2b52146
	
	============================================================
	Request status:
	  statusCode="SRM_SUCCESS"(0)
	  explanation="Request handled!"
	============================================================
	SRM Response:
	  remainingTotalRequestTime=0
	  arrayOfFileStatuses (size=1)
	      [0] sourceSURL="srm://ibm139.cnaf.infn.it:8444/infngrid/test_dir/test_file01.txt"
	      [0] fileSize=150000
	      [0] status: statusCode="SRM_FILE_PINNED"(22)
	                  explanation="srmPrepareToGet successfully handled!"
	      [0] transferURL="gsiftp://ibm139.cnaf.infn.it:2811/gpfs/infngrid/test_dir/test_file01.txt"
	============================================================

#### srmCopy <a name="srmCopy">&nbsp;</a>

StoRM current version doesn't support SRM copy requests and it will return SRM\_NOT\_SUPPORTED as response. 

#### srmStatusOfCopy <a name="srmStatusOfCopy">&nbsp;</a>

StoRM current version doesn't support SRM copy status requests and it will return SRM\_NOT\_SUPPORTED as response.

