---
layout: default
title: StoRM Storage Resource Manager - FAQ
---

# Frequently asked questions
<br>
<a name="sys_supported">&nbsp;</a>
### Which file systems are supported by StoRM?

StoRM is able to manage files on every POSIX file system. (GPFS, Lustre, xfs, ext3, ext4, reiserFS, etc.) 
Nevertheless, StoRM is able to leverage on advanced functionalities provided by GPFS, Lustre and other cluster file system. 
There are also some work in progress to make a StoRM cloud… (Amazon S3 LINK)

<a name="lustre">&nbsp;</a>
### StoRM works better on GPFS than Lustre?

No, StoRM is able to manage data on Lustre as good as on GPFS. The only difference are:

* Lustre does not have a gpfs_prealloc() like functions, so StoRM does no provide any guaranteed dynamic space reservation features (please note that this functionality is not used in WLCG)

* Lustre does not provide (yet) quota on fileset on GPFS or similar concept , so you cannot bind your Storage Areas with a true file system quota.

<a name="lustre_driver">&nbsp;</a>
### Why StoRM does not provides a specific driver for Lustre?

Writing a specific driver makes sense just if one wants to use advanced Lustre features by means of specific Lustre APIs further to the POSIX ones. Currently there are no special features StoRM have to use on Lustre available only via API, so the POSIX driver works great over Lustre.

<a name="plat_support">&nbsp;</a>
### Which platform are supported by StoRM?

StoRM is released for SL4 32 bit platform.
x86-64 version is not yet available.

<a name="local_data">&nbsp;</a>
### I have some local data, can I make them available in Grid via SRM using StoRM?

Yes, and it's really easy. The procedure is pretty much the same that migrating from a classic storage element (a pure GridFTP server) to an SRM based one. Have a look here.

<a name="fprotocol_support">&nbsp;</a>
### What does it means StoRM support ''file'' protocol?

Supporting the file protocol means the capability to allow job to direct access data for read or write operations. In case of cluster file system, the worker nodes belong to the fs cluster and see the storage as a local file system. StoRM allow jobs to perform a direct access to the files and directory.

<a name="data_shared">&nbsp;</a>
### Can I read my data locally when they are shared in Grid using StoRM?

Yes, you do can. StoRM sets up ACL for gid (in case the Storage Area is configured in Ahead of Time (AoT) mode) or for uid (in case the SA is in Just in Time (JiT) mode ) allowing local user to read data created through SRM.

<a name="local_grid_data">&nbsp;</a>
### Can I write my data locally and make them available in Grid using StoRM?

Yes. Even if it's not a good choice, since we always suggest to use SRM to manage space and create files, StoRM is able to support it. To manage file and directory StoRM requires proper rights on them. In case files are created directly by local user, ownership will remain bound with to the local user identity. To make this data readable from StoRM you can use the default acl feature of your file system. Default ACL defines ACL entries on a certain directory that are automatically applied to each file contained in it and inherited from each subdirectory. Setting up a default ACL entry on your Storage Area root for storm:rwxc will make all data created in it accessible via SRM using StoRM.

<a name="cool">&nbsp;</a>
### Ok, StoRM is cool, but which sites are really using it?

Since StoRM has joned the WLCG world a little bit later than other systems for data management, sometimes the unfounded idea “it is new and cool but not really adopted in WLCG…” still cames out. This is not true.
StoRM is used in production at the INFN-CNAF Tier1 since 2007. Now it manges all the data over a GPFS file system (over 1 PByte of data), taking over the use of Castor at CNAF for disk resources. 
It used in Italian Tier2s, like Milan and other over GPFS and in other T2 site such as Weizmann.
It is used in Spanish Cloud Tier2, LIP, IFIC over the Lustre file system.
Since it easy and simple it has been adopted by many minor sites migrating from classicSE to SRM based one.
StoRM has also been adopted in Economics and Finances Grid by the EGRID project.
Currently there are (at the time I write) 27 running instances of StoRM . For up to date view look at the WLCG monitoring page:

* WLCG sites using StoRM
* or you can query the gLite bdii with (please remove the TOREMOVE string before do the query):

      ldapsearch -x -h egee-bdii.cnaf.infn.it -p2170 -b o=grid "(&(TOREMOVEobjectClass=GlueSE)(GlueSEImplementationName=StoRM))" | grep GlueSEName

This map shows also some information on the sites running StoRM, but It could be not up to date since it have to be manually updated:

* StoRM meets Google maps ;)

<a name="tools">&nbsp;</a>
### Does StoRM is able to work with ''FTS'', ''GFAL'' or ''lcg-utils'' tools?

Yes, thanks to the SRM 2.2 interface. This interface is an agreement between all SRM provider (StoRM, Castor, DPM, dCache and BestMan). Client tools adopted in WLCG for transfer or access capabilites, such as FTS, GFAL or lcg-utils, can rely on this uniform interface to manage data on different kind storage systems. StoRM is currently used with such tools in many WLCG production endpoints.

<a name="lfc">&nbsp;</a>
### and with the LFC catalogue?
Yes.

<a name="data_transfer">&nbsp;</a>
### Does StoRM provides data transfer capability?
No. StoRM provides the SRM data management capabilities only. The data transfer capabilities are provided by external service such as the GridFTP tool. When StoRM is installed, by default a GridFTP server is configured on the Backend machine, but StoRM is able to work with external pools of GridFTP server (to maximize data throughput), even a different pool for each Storage Area. StoRM is able to work also with other protocols such as rfio. Have a look at the configuration guide for more information.
That's means: if you have poor data transfer throughput performance, look at your GridFTP servers configuration and at the underlying file system settings.

<a name="data_access">&nbsp;</a>
### Does StoRM provides data access capability?
No. As above, StoRM provides SRM functionalities only. StoRM supports many data access protocol, but it does not provide any access capability. Access to data can be performed by application with the desired Grid protocols specified in the SRM request (file, rfio, root, etc.). The main advantages of StoRM is the file:// protocol support, that means, in case the file system is shared by the worker node, application can perform direct access to data. In such condition, the file system configuration and tuning, both for Lustre and GPFS, is the most important aspect to gain data access performance.
