---
layout: toc
title: StoRM Storage Resource Manager - Functional Description
redirect_from:
  - /documentation/functional-description/
---

# StoRM: a Manager for Storage Resource in Grid

### Table of contents

* [Introduction](#introduction)
  * [SRM history](#srm-history)
  * [SRM interface](#srm-interface)
* [StoRM Architecture](#storm-architecture)
  * [StoRM Frontend](#storm-frontend)
      * [GSI Authentication](#gsi-authentication)
      * [Pool of Worker Threads](#pool-of-worker-threads)
      * [XML-RPC communication](#xml-rpc-communication)
      * [Asynchronous requests](#asynchronous-requests)
  * [StoRM Backend](#storm-backend)
      * [Internal macro components](#internal-macro-components)
      * [SRM requests from database](#srm-requests-from-database)
      * [File System driver](#file-system-driver)
  * [StoRM GridHTTPs Server](#storm-gridhttps-server)
* [StoRM security](#storm-security)
  * [Credential management](#credential-management)
  * [User access management](#user-access-management)
  * [Permission enforcement: JiT or AoT](#permission-enforcement-jit-or-aot)
  * [StoRM default ACL](#storm-default-acl)
* [Deployment Schemas](#deployment-schemas)

## Introduction

A **Storage Resource** can be composed by different storage systems: disk-only systems, tape archiving systems, or by a combination of both. The basic logical entities of a storage resource are _space_ and _file_. Space must be allocated when a new file have to be stored into a storage resource, and files could be dinamically removed to create the necessary space. This is the main goal of Storage Resource Managers (SRMs) middleware services whose function is to provide dynamic space allocation and file management of shared storage components. SRMs services agree on a **standard interface** to hide storage dependent characteristics and to allow interoperability between different storage systems.

{% assign image_src="SRM.png" %}
{% assign image_width="500px" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 1" %}
{% assign label_description="SRM interface allows a standard management of heterogeneous resources." %}
{% include documentation/label.html %}

### SRM history

In origin, the Storage Resource was exposed without a management interface. The first storage server in the Grid was based on Globus GridFTP and the supported protocols were NFS/file, rfio, root and gsiftp. This solution had some limitations. There was no possibility to query the service itself about its status, space available, etc. It was hard to manage the growing of space managed and there was no explicit support for tape backend (pre-staging, pool selection, etc.). So the need for a standard interface to manage the storage resource in Grid was recognized and the SRM v1.1 interface was defined. It was implemented by all major storage providers: CASTOR, dCache and DPM.

The main functions provided were:

- Get, getRequestStatus, pin, unpin
- Put, setFileStatus, Copy
- getProtocols, AdvisoryDelete, FileMetaData

The main features provided were:

- Asynchronous operations
- Support for bulk requests
- Protocol negotiation

But the main problems were:

- Missing reference implementation/No clear specifications
- No space management
- No explicit quality of storage management
- No abort operations
- No stagin operations
- etc.

### SRM interface

The [SRM interface version 2.2](https://sdm.lbl.gov/srm-wg/doc/SRM.v2.2.html) is mainly based on these concepts:

* **lifetime of a file**: volatile with a fixed lifetime, durable or permanent
* **file pinning**: to ensure a file is not canceled during operation
* **space pre-allocation**: to ensure the request space is available for the whole life of the application since the beginning
* **storage classes**: to identify different quality of storage resources.

The SRM v2.2 functionalities can be grouped in the following categories:

* **Data management**: provides capabilities to manage the data stored in a SRM, preparing envinronment for access operation (staging, etc.) and for receiving new data.
* **Space management**: space can be reserved by user to have the guarantee of availability when the transfer operation takes place.
* **Directory management**: this is a set of UNIX-like directory operation used to manage the SRM namespace for creating, moving and deleting directories and files.
* **Query functions**: this set of function is used to discover information on a specific SRM endpoint.

A SRM service provides two classes of methods:

* **Asynchronous methods**, non blocking call. Return a token corresponding to the request, the client can retrieve at any time the status of the request
by addressing it through such a token. This is the case for data management functionalities.
* **Synchronous methods**, blocking call. The control is returned to the client only when the request is completed.
This is the case of directory management and space management functions.

Files are no longer permanent entities on the storage, but dynamic ones that can appear or disappear according to the user's specification. SRMs **do not perform file transfers**, but can invoke middleware components that perform this job (such as GridFTP).

Files in the Grid can be referred by different names:

* _Logical File Name_ (LFN): an alias created by a user to refer to some item of data
* _Grid Unique IDentifier_ (GUID): a non-human-readable unique identifier for an item of data
* _Site URL_ (SURL): the location of an actual piece of data on a storage system
* _Transport URL_ (TURL): temporary locator of a replica + access protocol understood by a SE.

While the _GUIDs_ and _LFNs_ identify a file irrespective of its location, the SURLs and TURLs contain information about where a pshysicak replica is located, and how it can be accessed.

{% assign image_src="surlturl.png" %}
{% assign image_width="600px" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 2" %}
{% assign label_description="How a file is identified in Grid" %}
{% include documentation/label.html %}

The mapping from a SURL to a TURL is managed by the Storage Resource Management (SRM) service.

StoRM is a Storage Resource Manager that relies on a parallel file system or a standard Posix file system backend. StoRM takes advantage of high performance parallel file systems like GPFS (from IBM). Also standard POSIX file systems are supported (XFS from SGI and ext3).

{% assign image_src="stormroleinasite.png" %}
{% assign image_width="600px" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 3" %}
{% assign label_description="StoRM role in a site." %}
{% include documentation/label.html %}

It allows direct POSIX access ("file://" transfer protocol support) from applications to file and space; however other data access protocols are supported (like "rfio://"). POSIX data access is performed without interacting with an external service, with great performance improvement.

## StoRM Architecture

StoRM provides a flexible, configurable, scalable and high performance SRM
solution. It supports standard Grid access protocols as well as direct access (native POSIX I/O call) on data, fostering the integration of non Grid aware application providing local access on shared storage. Another important characteristic of StoRM is the capability to identify the physical location of a requested data without querying any database service but evaluating a configuration file, an XML schema that describes the storage namespace and input parameters as the logical identifier and SRM attributes. StoRM relies on the underlying file system structure to identify the physical data position.

StoRM has a multi-layer architecture (Fig.4) characterized by two main stateless components, named Frontend (FE) and Backend (BE), and a database used to store SRM requests and the StoRM metadata.

{% assign image_src="storm_architecture.png" %}
{% assign image_width="220px" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 4" %}
{% assign label_description="Simple StoRM Service Architecture schema<br/>with one Backend and one Frontend." %}
{% include documentation/label.html %}

The service is characterized by several components, some of which are mandatory, while others are optional:

- **mandatory components**: *Frontend* (FE), *Backend* (BE), Dynamic Info Provider (DIP);

- **optional components**: *GridFTP*, *GridHTTPs Server*, *Client*.

The Front-end (FE) has responsibilities of:

* expose a web service interface
* manage connection with authorized clients
* store asynchronous request into a database
* retrieve asynchronous request status
* co-operate directly with Back-end for synchronous calls
* manage user authentication
* co-operate with external authorization services to enforce security policy on service

The Database:

* store SRM asynchronous requests with their status
* store application data

The Back-end (BE) has responabilities of:

* execute all synchronous (active) actions
* get asynchronous request from the database
* execute all asynchronous actions
* bind with underlying file systems
* enforce authorization policy on files
* manage SRM file and space metadata

The GridHTTPs:

* expose a WebDAV interface
* co-operate directly with Back-end with synchronous XML-RPC calls
* co-operate directly with Back-end to check user authorization

A modular architecture decouples the StoRM logic from the different file system supported, and a set of plug-in mechanisms allows an easy integration of new file systems. With this approach data centre is able to choose the preferred underlying storage system maintaining the same SRM service. To more details look
at the Functional Description Guide.
The modular architecture of StoRM permits that service can be deployed on a multi-node scenario where its components are installed and configured on different hosts. Pools of FE, GridFTP and GridHTTPs are
possible, as you can see from Fig.5.

{% assign image_src="storm_distributed.png" %}
{% assign image_width="800px" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 5" %}
{% assign label_description="Example of distributed StoRM Service Architecture<br/>with one Backend, different pools of Frontends, GridHTTPs and GridFTPs." %}
{% include documentation/label.html %}

### StoRM Frontend

The Frontend component exposes the SRM web service interface, manages user authentication and stores the data of the SRM requests into the database. It's mainly written in C/C++. It relies on the GSOAP framework to expose the SRM interface and it uses the CGSI-GSOAP plugin to manage secure connection with clients.

#### GSI Authentication

The GSI authentication is managed by the FE. To properly establish a secure connection between SRM client and StoRM server,
the Certificate Authorities (CA) RPMs and the VOMS server RPMs have to be up to date both on the client and server machine.
Once the secure connection has been established, the FE parse the user proxy, getting DN and VOMS FQANs.
The FE then perform a first check on user identity verifying if the requestor identity is mapped in a local user
on the FE machine. This is done querying the LCMAPS service on the FE machine with user credential,
passing both DN (and FQANS) . If a mapping for the user exists (whatever it is) the SRM request go ahead,
otherwise it got an error at GSOAP level (this check is optional from StoRM v.1.4 and it can be disabled,
see the [FE configuration section]({{ site.baseurl }}/documentation/sysadmin-guide/{{ site.sysadmin_guide_version }}/#feconf)).

{% assign image_src="FrontEnd.png" %}
{% assign image_width="300px" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 6" %}
{% assign label_description="StoRM Frontend Component." %}
{% include documentation/label.html %}

#### Pool of Worker Threads

The FE uses a pool of worker threads to manage SRM requests.
Once a request has been authorized, the FE assigns it as a new task for a worker thread.
In case there are no free threads in the system, the request is maintained in an internal queue.
The size of the pool and the size of the queue are important parameters, their value have to be defined
depending on hardware resources and performance required.
Depending on the type of SRM request, each thread should have two main task to do,
as explained in the next paragraph.

#### XML-RPC communication

Synchronous SRM requests are a category of SRM calls that return the control to the client only
when the request has been executed by the system.
Most of the SRM call belongs to this category:

- Namespace operations (srmLs, srmMkdir,etc.)
- Discovery operation (srmPing)
- Space operations (srmReserveSpace, srmGetSpaceMetadata, etc.)

For this type of request, the FE perform a direct communication to the Backend using a RPC approach,
based on the XML-RPC protocol. XML-RPC is a simple protocol to exchange XML structured data over HTTP.
The Backend provides an XML-RPC server and the FE(s) acts as client.
A worker threads in case of synchronous requests performs this steps:

* structure the SRM data in XML
* send a request to the BE XML-RPC server
* wait until the execution
* get result from XML and unmarshall it in SOAP
* return the control to the client

{% assign image_src="SynchReq.png" %}
{% assign image_width="400px" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 7" %}
{% assign label_description="Management of SRM synchronous request." %}
{% include documentation/label.html %}

#### Asynchronous requests

Asynchronous SRM requests are a category of SRM calls that return the control to the client
as soon as the request has taken in charge by the system. Clients get a request token that can be use
to retrieve the status of the request in a second time.
The operations:

- srmPrepareToPut
- srmPrepareToGet
- srmBringOnLine
- srmCopy

belong to this category.
For this type of requests, the FE insert the SRM data into the Database and the Backend retrieve
the new request to execute with a polling mechanism. The BE process the SRM request and updates
the information into the DB. The FE manages also the srmStatusOf[PtG-PtP-etc] request simply querying
the status of the request into the database.

### StoRM Backend

The Backend is the core of the StoRM service, it executes all SRM functionalities.
It takes care of file and space metadata management, enforces authorization permissions
on files and interacts with external Grid services. It is mainly written in Java.

#### Internal macro components

StoRM Backend has the following internal macro components:

* Asynchronous request manager
* Synch request manager
* XML-RPC server
* Persistence manager
* Namespace component
* Autorization component
* Filesystem manager

#### SRM requests from database

The Picker component retrieves the specified amount of new SRM requests from the Database at each time interval, and forward them to a Scheduler. The Scheduler takes care of forward the request to the right worker thread as a new task to be executed. The request status is updated into the Database with all the information concerining request results, error and other data. This data are accessible from the FE to answer to a srmStatusOf\* requests. This pattern is shown in Fig.8.

{% assign image_src="reqfromdb.png" %}
{% assign image_width="400px" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 8" %}
{% assign label_description="Retrieving new SRM requests from database." %}
{% include documentation/label.html %}

Most of the parameters characterizing this architecture are configurable, see the [BE Configuration section]({{ site.baseurl }}/documentation/sysadmin-guide/{{ site.sysadmin_guide_version }}/#beconf) for more details.

#### File System driver

StoRM interacts with the different file systems supported through a driver mechanism, as shown in Fig.7. Each driver is a native libraries written mainly in C/C++, since most of the file system provides C libraries for the advanced API. StoRM BE uses JNI to connect with drivers. The functionalities provided by each driver are:

* ACL management
* Space management

The drivers available with StoRM are:

* _posixfs Generic_ driver for posix file system. It relies on the standard ```setfacl()```, ```getfacl()``` syscall for ACL management, and it does not provide any advanced space management capabilities.
* _GPFS_ specific driver that relies on GPFS advanced API, such as ```gpfs_prealloc``` for space management and ```gpfs_set_acl()``` for ACL management.

This driver mechanism implements a common interface and decouple StoRM internal logic from the different functionalities provided by the underlying storage system. The drivers are loaded at run time following the storage namespace configuration. A single StoRM server is able to work on different file system at the same time, and with this flexible approach it can be easily adapted to support new kind of file systems or other storage resources.

### StoRM GridHTTPs Server

StoRM GridHTTPs Server component provides to a StoRM endpoint both HTTP(s) file transfer capabilities and a WebDAV interface (see specifications on [WebDAV site](http://www.webdav.org/specs/rfc2518.html)).

#### The WebDAV interface

StoRM GridHTTPs Server component provides a brand-new WebDAV interface that conceals the details of the SRM protocol and allows users to mount remote Grid storage as a volume on their own desktops. It represents a single entry point to the storage data both for file management and transferring by providing different authentication models (from typical grid x.509 proxies and standard x.509 certificates to anonymous http read access), maintaining at the same time full compliance with present Grid standards.
StoRM GridHTTPs Server's WebDAV interface is based on the [Milton](http://milton.io/) free, apache licensed, module for basic WebDAV.

#### The file-transfer functionality

StoRM GridHTTPs Server also provides HTTP(s) file transfer capabilities that means that it's possible to GET/PUT file data via HTTP protocol. The operation is authorized only if a valid SRM prepare-to-get or SRM prepare-to-put has been successfully done on that file before.

## StoRM security

StoRM provides a strong and flexible security mechanism that is able to fulfill requirement from several different scenario.

### Credential management

StoRM rely on user credential for what concern user authentication and authorization. StoRM is able to support VOMS extension, and to use that to define access policy (complete VOMS-awareness).

{% assign image_src="credentialmanagement.png" %}
{% assign image_width="390px" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 9" %}
{% assign label_description="Credential management in StoRM." %}
{% include documentation/label.html %}

### User access management

There are several steps StoRM does to manage access to file:

1. User makes a request with his proxy (hopefully with VOMS extensions)
2. StoRM checks if the user can perform the requested operation on the required resource
3. StoRM ask user mapping to the LCMAPS service
4. StoRM enforce a real ACL on the file and directories requested
5. Jobs running on behalf of the user can perform a direct access on the data

{% assign image_src="useraccessmanagement.png" %}
{% assign image_width="530px" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 10" %}
{% assign label_description="User access management." %}
{% include documentation/label.html %}

### Permission enforcement: JiT or AoT

StoRM queries the LCMAPs service passing user credentials to get information on the local ```user id``` and ```group id``` a certain Grid user have to be mapped in according to his identity. This mapping have to be coherent with the one that take place on the Computing Element. Once StoRM has the mapping, the enforcement on file and directories take place in two way, in according with the configuration of Storage Area expressed in the namespace.xml file.

* **JiT (Just In Time)**. With this approach ACL entry is set up for the **user (uid)**, even if it's a pool account, and it will remain in place only for the duration of the SRM operation. StoRM takes care of removing the ACL entry when the operation is finished (at PutDone, or ReleaseFile time), or when the pin lifetime expires.
* **AoT (Ahead Of Time)**. With this approach ACL entry is set up for the **group (gid)** of the user and it will remain in place for all the file lifetime. This is the standard approach in WLCG community.

### StoRM default ACL

StoRM also allows to define default ACLs, a list of ACL entries that will be applied automatically on each read (srmPrepareToGet) and write (srmPrepareToPut) operation. This is useful in case of experiment ise cases, such as the CMS one, that want to allow local access to file on group different from the one that made the SRM request operation. These default ACLs have to be set up on the desired storage area in the namespace.xml file.

## Deployment Schemas

The multi-modular architecture of StoRM permits different deployment schemas. Site administrators can install all the components on the same host (**simplest schema**) or distribute them on different hosts (**distributed schema**), eventually by replicating some of them (**clustered schema**). The simplest deployment needs the following parts to be installed:

* **one single Frontend**
* **one single Backend**
* **one single GridFTP**
* a **MySQL server**

{% assign image_src="simplestschema.png" %}
{% assign image_width="300px" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 11" %}
{% assign label_description="Simplest deployment schema: all the components in the same host." %}
{% include documentation/label.html %}

This kind of deployment is adapt for small SE where the transfer traffic is low and there are few requests for second (this not means SE with small space size).
If there is the need of a more scalable SE, or your transfer traffic and load are more significant, the site administrator can distribute the components in different hosts. For example the simplest deployment seen before could become as described in Fig. 12:

{% assign image_src="distributedschema.png" %}
{% assign image_width="400px" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 11" %}
{% assign label_description="Distributed deployment schema: the components live in different hosts." %}
{% include documentation/label.html %}

where each component lives on a different host. In case of an high transfer traffic and/or an high load, the critical components could be installed in cluster:

{% assign image_src="clusteredschema.png" %}
{% assign image_width="600px" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 12" %}
{% assign label_description="Clustered deployment schema: GridFTPs and FEs are replicated and the database is distributed." %}
{% include documentation/label.html %}

This solution is adapt for a scalable SE.
