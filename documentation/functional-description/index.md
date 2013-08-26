---
layout: default
title: StoRM Storage Resource Manager - Functional Description
---

# StoRM

* [Introduction](#introduction)
* [Storage Resource Manager](#srm)
  * [SRM interface](#srminterface)
  * [SRM concepts](#srmconcepts)
  * [SRM functionalities](#srmfunctionalities)
* [StoRM Components](#stormcomponents)
  * [StoRM Front-End](#stormfrontend)
      * [GSI Authentication](#gsiauth)
      * [Pool of Worker Threads](#poolwt)
      * [XML-RPC communication](#xmlrpccommunication)
      * [Asynchronous requests](#asynchreq)
  * [StoRM Back-End](#stormbackend)
    
## Introduction <a name="introduction">&nbsp;</a>

The StoRM service is a storage resource manager for generic disk based
storage systems separating the data management layer from the underlying
storage systems. It implements the [SRM interface version 2.2](https://sdm.lbl.gov/srm-wg/doc/SRM.v2.2.html). 

StoRM provides a flexible, configurable, scalable and high performance SRM
solution. It supports standard Grid access protocols as well as direct
access (native POSIX I/O call) on data, fostering the integration of non
Grid aware application providing local access on shared storage. Another
important characteristic of StoRM is the capability to identify the
physical location of a requested data without querying any database
service but evaluating a configuration file, an XML schema that
describes the storage namespace and input parameters as the logical
identifier and SRM attributes. StoRM relies on the underlying file
system structure to identify the physical data position.

## Storage Resource Manager <a name="srm">&nbsp;</a>

Storage Resource Managers are middleware services whose function is to **provide dynamic space 
allocation and file management of shared storage components**. Files are no longer permanent entities 
on the storage, but dynamic ones that can appear or disappear according to the user's specification. 
SRMs **do not perform file transfers**, but can invoke middleware components that perform this job (such as GridFTP).

### SRM interface <a name="srminterface">&nbsp;</a>

SRM services agree on a standard interface (the SRM interface, Fig. 1) 
to hide storage characteristics and to allow interoperability.

{% assign image_src="SRM.png" %}
{% assign image_width="500px" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 1" %}
{% assign label_description="The SRM interface." %}
{% include documentation/label.html %}

### SRM concepts <a name="srmconcepts">&nbsp;</a>

The [SRM interface version 2.2](https://sdm.lbl.gov/srm-wg/doc/SRM.v2.2.html) is mainly based on these concepts:

* **lifetime of a file**: volatile with a fixed lifetime, durable or permanent
* **file pinning**: to ensure a file is not canceled during operation
* **space pre-allocation**: to ensure the request space is available for the whole life of the application since the beginning
* **storage classes**: to identify different quality of storage resources.

### SRM functionalities <a name="srmfunctionalities">&nbsp;</a>

The SRM functionalities can be grouped in the following categories:

* **Data management**: provides capabilities to manage the data stored in a SRM, preparing envinronment for access operation (staging, etc.) and for receiving new data.
* **Space management**: space can be reserved by user to have the guarantee of availability when the transfer operation takes place.
* **Directory management**: this is a set of UNIX-like directory operation used to manage the SRM namespace for creating, moving and deleting directories and files.
* **Query functions**: this set of function is used to discover information on a specific SRM endpoint.

SRM service provides provides two classes of methods:

* **Asynchronous methods**, non blocking call. Return a token corresponding to the request, the client can retrieve at any time the status of the request 
by addressing it through such a token. This is the case for data management functionalities.
* **Synchronous methods**, blocking call. The control is returned to the client only when the request is completed. 
This is the case of directory management and space management functions.

## StoRM Components <a name="stormcomponents">&nbsp;</a>

StoRM has a multi-layer architecture (Fig.2) characterized by two main stateless components, 
named Front-End (FE) and Back-End (BE), and a database used to store SRM requests and the StoRM metadata. 

{% assign image_src="storm_architecture.png" %}
{% assign image_width="200px" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 2" %}
{% assign label_description="Simple StoRM Service Architecture schema<br/>with one BackEnd and one FrontEnd." %}
{% include documentation/label.html %}

The service is characterized by several components, some of which are mandatory, while others are optional:

- **mandatory components**: *FrontEnd* (FE), *BackEnd* (BE), Dynamic Info Provider (DIP);

- **optional components**: *GridFTP*, *GridHTTPs*, *Client*.

A modular architecture decouples the StoRM logic from the different file system supported, and a set of 
plug-in mechanisms allows an easy integration of new file systems. With this approach data centre is able 
to choose the preferred underlying storage system maintaining the same SRM service. To more details look 
at the Functional Description Guide.
The modular architecture of StoRM permits that service can be deployed on a multi-node scenario where 
its components are installed and configured on different hosts. Pools of FE, GridFTP and GridHTTPs are 
possible, as you can see from Fig.3.

{% assign image_src="storm_distributed.png" %}
{% assign image_width="700px" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 3" %}
{% assign label_description="Example of distributed StoRM Service Architecture<br/>with one BackEnd, different pools of FrontEnds, GridHTTPs and GridFTPs." %}
{% include documentation/label.html %}

### StoRM Fron-End <a name="stormfrontend">&nbsp;</a>

The Front-End component exposes the SRM web service interface, manages user authentication and stores the data 
of the SRM requests into the database. It is mainly written in C/C++. It relies on the GSOAP framework to expose 
the SRM interface and it uses the CGSI-GSOAP plugin to manage secure connection with clients.

#### GSI Authentication <a name="gsiauth">&nbsp;</a>

The GSI authentication is managed by the FE. To properly establish a secure connection between SRM client and StoRM server, 
the Certificate Authorities (CA) RPMs and the VOMS server RPMs have to be up to date both on the client and server machine. 
Once the secure connection has been established, the FE parse the user proxy, getting DN and VOMS FQANs. 
The FE then perform a first check on user identity verifying if the requestor identity is mapped in a local user 
on the FE machine. This is done querying the LCMAPS service on the FE machine with user credential, 
passing both DN (and FQANS) . If a mapping for the user exists (whatever it is) the SRM request go ahead, 
otherwise it got an error at GSOAP level (this check is optional from StoRM v.1.4 and it can be disabled, 
see the [FE configuration section](http://localhost:4000/documentation/sysadmin-guide/1.11.2/#feconf)).

{% assign image_src="FrontEnd.png" %}
{% assign image_width="300px" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 4" %}
{% assign label_description="StoRM Frontend Component." %}
{% include documentation/label.html %}

#### Pool of Worker Threads <a name="poolwt">&nbsp;</a>

The FE uses a pool of worker threads to manage SRM requests. 
Once a request has been authorized, the FE assigns it as a new task for a worker thread. 
In case there are no free threads in the system, the request is maintained in an internal queue. 
The size of the pool and the size of the queue are important parameters, their value have to be defined 
depending on hardware resources and performance required. 
Depending on the type of SRM request, each thread should have two main task to do, 
as explained in the next paragraph.

#### XML-RPC communication <a name="xmlrpccommunication">&nbsp;</a>

Synchronous SRM requests are a category of SRM calls that return the control to the client only 
when the request has been executed by the system. 
Most of the SRM call belongs to this category: 

- Namespace operations (srmLs, srmMkdir,etc.)
- Discovery operation (srmPing)
- Space operations (srmReserveSpace, srmGetSpaceMetadata, etc.)

For this type of request, the FE perform a direct communication to the Backend using a RPC approach, 
based on the XML-RPC protocol. XML-RPC is a simple protocol to exchange XML structured data over HTTP. 
The Back-End provides an XML-RPC server and the FE(s) acts as client. 
A worker threads in case of synchronous requests performs this steps:

* structure the SRM data in XML
* send a request to the BE XML-RPC server
* wait until the execution
* get result from XML and unmarshall it in SOAP
* return the control to the client

{% assign image_src="SynchReq.png" %}
{% assign image_width="400px" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 5" %}
{% assign label_description="Management of SRM synchronous request." %}
{% include documentation/label.html %}

#### Asynchronous requests <a name="asynchreq">&nbsp;</a>

Asynchronous SRM requests are a category of SRM calls that return the control to the client 
as soon as the request has taken in charge by the system. Clients get a request token that can be use 
to retrieve the status of the request in a second time. 
The operations:

- srmPrepareToPut
- srmPrepareToGet
- srmBringOnLine
- srmCopy 

belong to this category. 
For this type of requests, the FE insert the SRM data into the Database and the BackEnd retrieve 
the new request to execute with a polling mechanism. The BE process the SRM request and updates 
the information into the DB. The FE manages also the srmStatusOf[PtG-PtP-etc] request simply querying 
the status of the request into the database.

## StoRM Back-End <a name="stormbackend">&nbsp;</a>

The Back-End is the core of the StoRM service, it executes all SRM functionalities. 
It takes care of file and space metadata management, enforces authorization permissions 
on files and interacts with external Grid services. It is mainly written in Java.
