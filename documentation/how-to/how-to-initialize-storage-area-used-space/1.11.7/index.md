---
layout: default
title: StoRM How-To - Initialize Storage Area used space
---

#### [Back to How-To]({{site.baseurl}}/documentation/examples)

# StoRM Backend Storage Usage Initialization

StoRM maintains the information about the status of managed storage areas (such as free, used, busy, available, guaranteed and reserved space), and store them into the DB. Whenever it is consumed or released some storage space by creating or deleting files, the status is updated and stored in the DB. The storage space status stored into the DB is authorative. The information about the Storage Space stored into the DB are used also as information source for the Information Provider through the DIP (Dynamic Info Provider). There are cases in which the status of a storage area must be initialized, for example in the case of a fresh StoRM installation configured to manage a storage space already populated with files, where the space used is not zero.
There are different methods for initialize the Storage Area status, some executed within StoRM (GPFS quota and/or background-DU). In this section it is described how an administrator can initialize the status of a Storage Area by editing a configuration file, the used-space.ini configuration file, that it will be parsed at bootstrap time and only one time.
The structure of the content of **used-space.ini** is quite simple: a list of sections corresponding to the Storage Area in which are defined the used size, and eventually, the checktime.
For each Storage Area to be initializated there is a section named with the same alias *space-token-description* defined in the *namespace.xml*, that are defined with YAIM variables STORM\_{SA}\_ACCESSPOINT. Within the section there are two properties: *usedsize* and *checktime*:

- *usedsize*: The used space in the Storage Area expressed in Bytes. Must be an value without digits after the decimal mark. **MANDATORY**
- *checktime*: The timestamp of the time to wich the usedsize computation refers. Must be a date in RFC-2822 format. Optional.

Here is a sample of *used-space.ini*:

	[sa-alias-1]
	checktime = Fri, 23 Sep 2011 11:56:53 +0200
	usedsize = 1848392893847
	[sa-alias-2]
	checktime = Fri, 16 Sep 2011 10:22:17 +0200
	usedsize = 2839937589367
	[sa-alias-3]
	usedsize = 1099511627776

This file can be produced in two ways:

1. by hand after StoRM Backend service configuration

	* write your own used-space.ini file adding a section for each Storage Area you want to initialize

	* as section name use the *space-token-description* value as in namespace.xml

	* set the value of usedsize property as in the example.

	* set the value of checktime property as in the example. To obtain an RFC-2822 timestamp of the current time you can execute the command *date --rfc-2822*

2. by YAIM at StoRM Backend service configuration time

	* add a variable STORM\_{SA}\_USED\_ONLINE\_SIZE to your YAIM configuration file for each Storage Area you want to initialize where {SA} is the name of the Storage Area as in STORM\_STORAGEAREA\_LIST YAIM variable

	* run YAIM on StoRM profiles installed on this host

StoRM Backend will load used-space.ini file at bootstrap and initialize the used space of newly created Storge Areas to its values.

> **NOTE**: running YAIM on StoRM Backend profile will produce a new used-space.ini file and backup any existent version with the extension .bkp\_. Take this into account if you want to produce the used-space.ini file by hand.