---
layout: default
title: StoRM Storage Resource Manager - Functional Description
---

# StoRM

* [Storage Resource Manager](#srm)
  * [SRM concepts](#srmconcepts)
  * [SRM functionalities](#srmfunctionalities)
* [Service Overview](#overview)
  * [StoRM Components](#components)

## Storage Resource Manager <a name="srm">&nbsp;</a>


Storage Resource Managers are middleware services whose function is to **provide dynamic space 
allocation and file management of shared storage components**. Files are no longer permanent entities 
on the storage, but dynamic ones that can appear or disappear according to the user's specification. 
SRMs **do not perform file transfers**, but can invoke middleware components that perform this job (such as GridFTP).

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

## Service Overview <a name="overview">&nbsp;</a>

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
