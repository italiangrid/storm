---
layout: toc
title: "StoRM's Path Authorization DataBase"
---

# StoRM's Path Authorization DataBase

- [Introduction](#introduction)
- [The LCMAPS mapping](#the-lcmaps-mapping)
- [ACLs: AheadOfTime or JustInTime](#acls-aheadoftime-or-justintime)
- [StoRM's path-authz.db](#storms-path-authzdb)
    - [Evaluation algorithm](#evaluation-algorithm)
    - [Access Control Entries](#access-control-entries)
- [Example](#example)
   - [Configure users and groups mapping](#configure-users-and-groups-mapping)
   - [Configure path authorization database](#configure-path-authorization-database)
   - [Users](#users)
   - [SRM tests](#srm-tests)
   - [Avoid conflicts with Default ACL List](#avoid-conflicts-with-default-acl-list)   

## Introduction

StoRM Path Authorization Database gives to the system administrator a way to customize users permissions on internal paths of a storage area: different local users/groups will have different rights on the same path. For each SRM request, the requester VO user, if authorized, is mapped on one local user or group, depending on its credentials. The system administrator can write a list of ACE (Access Control Entry) in order to permit or deny a list of operations on a particular path. 

## The LCMAPS mapping

StoRM Path Authorization Database system needs a mapping service that transforms user's Grid credentials to Unix credentials: a local user and group. The service used by StoRM is **LCMAPS**.

LCMAPS is the Local Credential Mapping Service and it takes care of translating grid credentials to Unix credentials local to the site by using [the pool account mechanism](http://www.gridsite.org/gridmapdir/). It takes care of ensuring that different individuals on the Grid remain distinct Unix accounts. Using group mappings based on the user's VO attributes, isolation and scheduling priority decisions can be made. 

> *See more on https://wiki.nikhef.nl/grid/LCMAPS*

This mapping can be configured via YAIM through the variables `USERS_CONF` and `GROUPS_CONF`. These variables contain the full absolute path of a couple of files: *users.conf* and *groups.conf*.

The file *users.conf* contains the list of Linux users (pool accounts) to be created. It's a plain list of the users and their IDs. An example of this configuration file can be found into:

```bash
/opt/glite/yaim/examples/users.conf
```

More details can be found in the [User configuration section in the YAIM guide](https://twiki.cern.ch/twiki/bin/view/LCG/YaimGuide400#User_configuration_in_YAIM).

The UNIX users here defined must be created on the service nodes that need them (mainly CE and WNs). The format is the following (fields must not have any white space):

```bash
UID:LOGIN:GID1[,GID2,...]:GROUP1[,GROUP2,...]:VO:FLAG:
```

- UID = user ID. This must be a valid uid. Make sure the number you choose is not assigned to another user.
- LOGIN = login name
- GID1 = primary group ID. This must be a valid gid. Make sure the number you choose is not assigned to another group.
- GID2 = secondary group ID.
- GROUP1 = primary group
- GROUP2 = secondary group
- VO = virtual organization
- FLAG = string to identify special users, further described below

You can customize this file to your site needs. 

The *groups.conf* file defines the user categories that must be accepted by the grid services provided by a site. It indicates for each category to which kind of local accounts the user should be mapped, where applicable. The file has the following format:

```bash
"VOMS_FQAN":GROUP:GID:FLAG:[VO]
```

- VOMS_FQAN = VOMS proxy fully qualified attribute name
- GROUP = UNIX group
- GID = UNIX GID
- FLAG = string to identify special users, further described below
- VO = virtual organization (optional. It allows the VO to be specified explicitly, otherwise it will be derived from the VOMS FQAN

The groups.conf distributed by YAIM is only an example. You can remove the lines that doesn't apply to your site or VO and add new lines if needed. Example:

```bash
"/dteam/ROLE=lcgadmin":::sgm:
"/dteam/ROLE=production":::prd:
"/dteam"::::
```

The *groups.conf* file lists the VOMS proxy primary FQANs that are accepted. If a proxy has a secondary FQAN that matches one of the FQANs listed, the mapped account may receive an extra secondary GID corresponding to the matched FQAN. That GID normally is derived from the corresponding accounts in the users.conf file. If there are no accounts dedicated to that FQAN, the desired extra GID (if any) and GROUP name must be given in groups.conf. 
Note that:

- it is normal for the second and third fields to be empty, as shown in the example;
- the account corresponding to the primary FQAN does not have to belong to any secondary group: the LCMAPS library can set secondary groups independently of what is in /etc/group;
- the order of the lines in *groups.conf* is important: for any FQAN only the first match is taken

The FLAG selects a set of special accounts to be used for the mapping, namely those accounts in users.conf that have the same flag. By default, when the flag is empty, the ordinary pool accounts will be used.


## ACLs: AheadOfTime or JustInTime

Setting ACL on directory and files is foundamental to give to the mapped user and group the necessary permissions to read and write on a storage area (or a part of it).

**AheadOfTime** and **JustInTime** are the two ACL enforcing approach of StoRM.

In case of *AheadOfTime* (**AoT**) approach, StoRM sets up a physical ACL on file and directories for the local group (gid) in which the user is mapped (the mapping is done querying the LCMAPS service con the BE machine passing both user DN and FQANs). *The group ACL remains for the whole lifetime of the file*. 

In case of *JustInTime* (**JiT**) approach, StoRM sets up and ACL for the local user (uid) the user is mapped. *The ACL remains in place only for the lifetime of the SRM request, then StoRM removes it* (this is done to avoid granting access to pool account uid in case of reallocation on different users).

## StoRM's path-authz.db

As said above, StoRM Path Authorization Database gives to the system administrator a way to customize permissions for different groups on access to storage area's internal paths. This is done by using a list of ACE (Access Control Entry), evaluated by a selected algorithm.

The choose of the evaluation algorithm and the definition of the list of ACEs can be done by editing the file:

    /etc/storm/backend-server/path-authz.db.

### Evaluation algorithm

To change the evaluation algorithm, edit the property **algorithm**. By default its value is:

```bash
algorithm=it.grid.storm.authz.path.model.PathAuthzAlgBestMatch
```

> NOTE: Currently, this is the unique supported algorithm that can be specified.

To determine if a request succeeds, the *PathAuthzAlgBestMatch* evaluation algorithm processes the ACE list in a computed order:

- only the ACE which have a local UNIX group that matches the requester subject are considered;
- the order of the ACE is defined on the base of distance from the requested SURL's StFN and the path specified within the ACE;
- each ACE is processed until all of the bits of the requester's access have been checked.

The result will be:

- `NOT_APPLICABLE` if there are no ACE matching with the requester.
- `INDETERMINATE` if there is at least one bit not checked.
- `DENY` if there is at least one bit DENIED for the requestor
- `PERMIT` if all the bits are PERMIT 

### Access Control Entries

The default ACE stored into `path-authz.db` authorization file allows every kind of operation on all the storage areas:

```bash
#--------+-----------------------+---------+
# user   | Path     | Permission | ACE     |
# class  |          | mask       | type    |
#--------+-----------------------+---------+
  @ALL@    /          RLWFDMN      permit
```

Each ACE is composed by the following information:

- USER CLASS contains the name of the UNIX group involved or `@ALL@` to match all the groups;
- PATH is the relative sub-path based on the storage area root;
- the PERMISSION MASK contains from one to all the following letters:
    - `W` :       WRITE_FILE              "Write data on existing files"
    - `R` :       READ_FILE               "Read data"
    - `F` :       MOVE/RENAME             "Move a file"
    - `D` :       DELETE                  "Delete a file or a directory"
    - `L` :       LIST_DIRECTORY          "Listing a directory"
    - `M` :       CREATE_DIRECTORY        "Create a directory"
    - `N` :       CREATE_FILE             "Create a new file"
- ACE TYPE value could be `permit` or `deny` and indicates if the selected operations on that path have to be blocked or allowed.


## Example

Here we're going to show how to configure a simple path authorization filter on our test VO's storage area: *test.vo*

### Configure users and groups mapping

To properly configure StoRM's Path Authorization DataBase it's useful to have the right knowledge about the users/groups involved with LCMAPS mapping. So, first of all, we have to define the pool accounts to be created and the local groups mapping. 

Edit/create:

    /etc/storm/backend-server/storm-users.conf

and add:

- a pool account of 100 users with group name *testvo* and *test.vo* as VO name

```bash
71001:tstvo001:7100:testvo:test.vo::
71002:tstvo002:7100:testvo:test.vo::
71003:tstvo003:7100:testvo:test.vo::
71004:tstvo004:7100:testvo:test.vo::
...
71100:tstvo100:7100:testvo:test.vo::
```

- a pool account of 30 production users with group name *testvoprd*, *test.vo* as VO name and *prd* as FLAG.

```bash
71101:testvoprd001:7170,7100:testvoprd,testvo:test.vo:prd:
71102:testvoprd002:7170,7100:testvoprd,testvo:test.vo:prd:
71103:testvoprd003:7170,7100:testvoprd,testvo:test.vo:prd:
71104:testvoprd004:7170,7100:testvoprd,testvo:test.vo:prd:
...
71129:testvoprd029:7170,7100:testvoprd,testvo:test.vo:prd:
71130:testvoprd030:7170,7100:testvoprd,testvo:test.vo:prd:
```

Edit/create:

    /etc/storm/backend-server/storm-groups.conf

and add:

- a rule that maps *test.vo* users with role equal to `R1` to the pool account with `FLAG = prd` (group name *testvoprd*)
- a rule that maps all the other *test.vo* users to the group *testvo*

```bash
"/test.vo/ROLE=R1":::prd:
"/test.vo"::::
```

To apply the changes, set YAIM's variables:

```bash
USERS_CONF=/etc/storm/backend-server/storm-users.conf
GROUPS_CONF=/etc/storm/backend-server/storm-groups.conf
```

and run yaim-storm configuration, like follow:

```bash
/opt/glite/yaim/bin/yaim \
    -c -s /etc/storm/siteinfo/storm.def \
    -n se_storm_backend \
    -n se_storm_frontend \
    -n se_storm_gridftp
```

> Supposing to have:
> 
>  - site configuration file located to /etc/storm/siteinfo/storm.def
>  - a single storm node with storm-backend, storm-frontend and storm-gridftp installed

### Configure path authorization database

The idea is to create within the *test.vo* storage area a sub-directory *test.vo/PRD* accessible in read/write mode only by users that belong to the local group *testvoprd*.

Edit:

    /etc/storm/backend-server/path-authz.db

as follow:

```bash
#----------+--------------+------------+--------+
# user     | Path         | Permission | ACE    |
# class    |              | mask       | type   |
#----------+--------------+------------+--------+
testvoprd    /test.vo/PRD   RLWFDMN      permit
@ALL@        /test.vo/PRD   RLWFDMN      deny
testvo       /test.vo       RLWFDMN      permit
testvoprd    /test.vo       RLWFDMN      permit
@ALL@        /test.vo       RLWFDMN      deny
@ALL@        /              RLWFDMN      permit
```

This ACE list say that: 

- All the users mapped into *testvoprd* group can do any kind of operation into */test.vo* directory and all its sub-directories (4th ACE), */test.vo/PRD* included (1st ACE).
- All the users mapped into *testvo* group can do any kind of operation into */test.vo* directory and all its sub-directories (3rd ACE), */test.vo/PRD* excluded (2nd ACE).
- All the user mapped into a group different from *testvoprd* and *testvo* can't access */test.vo* and all its sub-directories (3rd,4th and 5th ACEs). 
- No ACEs are defined for the other storage areas (6th ACE).

The order of the ACEs is important because the evaluation algorithm stops after the first match with minimal distance from `Path`. For example, if we invert the first two rows of our ACE list, nobody will be authorized to read or write into */test.vo/PRD*. 

To apply the changes to path-authz.db there's no need to re-run yaim configuration, just restart *storm-backend-server*:

```bash
service storm-backend-server restart
```

### Users

In order to do some test on the path-authz.db configuration showed above, two users with different VOMS attributes have been used:

* **/C=IT/O=IGI/CN=test0**
* **/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Enrico Vianello**

User *Enrico Vianello* has "Role=R1" between its FQANs then, according to [the mapping configuration](#configure-lcmaps-mapping) showed before, it will be associated to *testvoprd* local group.

User *test0* has no "Role=R1" then it will be mapped to *testvo* local group.

### SRM tests

The following tests have been done using:

- [**gfal tool**](https://wiki.chipp.ch/twiki/bin/view/CmsTier3/HowToAccessSe#gfal_tools) for the SRM requests
- [**globus-url-copy**](http://toolkit.globus.org/toolkit/docs/3.2/gridftp/user/globusurlcopy.html) of Globus Toolkit to direct upload/download files
- **acl** package (it's a StoRM prerequisite) to read the ACL set on files/directories

```bash
## Switch to user "Enrico Vianello" ##
$ voms-proxy-init --voms test.vo:/test.vo/Role=R1 --cert vianello-2014.p12

## Create directory /test.vo/tmp ##
$ gfal-mkdir srm://centos6-devel.cnaf.infn.it:8444/test.vo/tmp

## Get ACLs from /test.vo/tmp ##
$ getfacl /storage/test.vo/tmp
getfacl: Removing leading '/' from absolute path names
# file: storage/test.vo/tmp
# owner: storm
# group: storm
user::rwx
group::---
group:testvoprd:r-x
mask::rwx
other::---
## The testvoprd ACL has been added added ##

## Create new local file: ##
$ echo "Hello world!!" > hello.txt

## Upload local file to /test.vo/tmp ##
$ cat hello.txt | gfal-save srm://centos6-devel.cnaf.infn.it:8444/test.vo/tmp/hello.txt

## Check if the uploaded file exists: ##
$ gfal-ls srm://centos6-devel.cnaf.infn.it:8444/test.vo/tmp
hello.txt

## Get ACLs from /test.vo/tmp/hello.txt
$ getfacl /storage/test.vo/tmp/hello.txt 
getfacl: Removing leading '/' from absolute path names
# file: storage/test.vo/tmp/hello.txt
# owner: storm
# group: storm
user::rw-
group::---
group:testvoprd:rw-
mask::rwx
other::---
## The testvoprd ACL has been added added

## Switch to test0 user: ##
$ voms-proxy-init --voms test.vo --cert test0.p12

## Get file content /test.vo/tmp/hello.txt ##
$ gfal-copy srm://centos6-devel.cnaf.infn.it:8444/test.vo/tmp/hello.txt file:///tmp/hello.txt
Copying 1   [DONE]  after 1s 

## Check the updated ACL on /test.vo/tmp/hello.txt ##
$getfacl /storage/test.vo/tmp/hello.txt 
getfacl: Removing leading '/' from absolute path names
# file: storage/test.vo/tmp/hello.txt
# owner: storm
# group: storm
user::rw-
group::---
group:testvo:r--
group:testvoprd:rw-
mask::rwx
other::---
## testvo read permission has been added

## Switch to user "Enrico Vianello" ##
$ voms-proxy-init --voms test.vo:/test.vo/Role=R1 --cert vianello-2014.p12

## Create directory /test.vo/PRD (accessible only to testvoprd users):
$ gfal-mkdir srm://centos6-devel.cnaf.infn.it:8444/test.vo/PRD

## getting ACLs on /test.vo/PRD
$ getfacl /storage/test.vo/PRD/
getfacl: Removing leading '/' from absolute path names
# file: storage/test.vo/PRD/
# owner: storm
# group: storm
user::rwx
group::---
group:testvoprd:r-x
mask::rwx
other::---

## Upload local file to /test.vo/PRD/hello.txt
$ cat hello.txt | gfal-save srm://centos6-devel.cnaf.infn.it:8444/test.vo/PRD/hello.txt

## Check if the uploaded file exists:
$ gfal-ls srm://centos6-devel.cnaf.infn.it:8444/test.vo/PRD
hello.txt

## Get ACLs from /test.vo/PRD/hello.txt
$ getfacl /storage/test.vo/PRD/hello.txt 
getfacl: Removing leading '/' from absolute path names
# file: storage/test.vo/PRD/hello.txt
# owner: storm
# group: storm
user::rw-
group::---
group:testvoprd:rw-
mask::rwx
other::---
## The testvoprd ACL has been added added

## Get uploaded file content:
$ gfal-cat srm://centos6-devel.cnaf.infn.it:8444/test.vo/PRD/hello.txt
Hello World!!

## Switch to test0 user:
$ voms-proxy-init --voms test.vo --cert test0.p12

## Get file content expecting unauthorized failure (1):
$ gfal-cat srm://centos6-devel.cnaf.infn.it:8444/test.vo/PRD/hello.txt
gfal-cat: error: Permission denied

## Get file content expecting unauthorized failure (1):
$ gfal-copy srm://centos6-devel.cnaf.infn.it:8444/test.vo/PRD/hello.txt file:///tmp/hello.txt
Copying 1   [FAILED]  after 2s                                                                                                                                                    gfal-copy: error: Permission denied
```

At this point a question could be raised: how does it works with direct calls from GridFTP? The GridFTP TURLs should be used only within the context of a SRM request (between a srmPtG and a srmRf or between a srmPtP and a srmPd). In this case there's no problem because the SRM request will set the necessary ACL (indipendently from the enforcing approach used). If the GridFTP call is made out of the context of a SRM request the scenary depends on the operations done before and on who has done them, or depends on the Default ACL. It's not sure that the necessary ACL is set (if the enforcing approach is JiT the ACL could even not exist), then the request can fail.

In case of AoT enforcing approach, if almost one user mapped on the same group has already done a SRM request on the resource, then a direct access to the same resource, providing the necessary credentials, will be successful.

For example, at the point of the example above, if user *test0* does a globus-url-copy like the gfal-copy just done, the request will be successful:

```bash
## Switch to user test0 ##

## Check failure on file download passing through StoRM SRM endpoint: ##
$ gfal-copy srm://centos6-devel.cnaf.infn.it:8444/test.vo/PRD/hello.txt file:///tmp/hello.txt
Copying 1   [FAILED]  after 2s                                                                                                                                                    gfal-copy: error: Permission denied
## Resource is not reachable via SRM!

## Check failure on file download by directly accessing via GridFTP ##
$ globus-url-copy -vb gsiftp://centos6-devel.cnaf.infn.it:2811/storage/test.vo/PRD/Hello.txt hello.tx
Source: gsiftp://centos6-devel.cnaf.infn.it:2811/storage/test.vo/PRD/
Dest:   file:///home/vianello/
  Hello.txt  ->  hello.tx

error: globus_ftp_client: the server responded with an error
500 Command failed. : open error: Permission denied
```

## Avoid conflicts with *Default ACL List*

StoRM allows site administrators to set a **Default ACL List** for each storage area (see [backend configuration section](http://italiangrid.github.io/storm/documentation/sysadmin-guide/1.11.8/#beconf) of StoRM's System Administrator guide).

The *Default ACL List* contains one or more pairs of a local user (uid) or group id (gid) and a permission (R, W, RW). All these ACL are automatically added by StoRM to the requested resource, at each read or write request.

Obviously, **if the Default ACL involves users or groups that are used also into the path-authz.db, this configuration can generate conflicts** and unexpected behaviors.

For example, if we add to our *test.vo* storage area the following default ACL:

    STORM_TESTVO_DEFAULT_ACL_LIST=testvo:RW

we will give to all the users with group *testvo* read and write permissions on all the files and directories within the storage area. Default ACL list is propagated onto sub-directories and files when they are created or read by a SRM request. Then, if the site administrator applies it on an already existent filesystem tree, he must manually run a command which fix all the storage area's ACL.

Going back to our example, added the `STORM_TESTVO_DEFAULT_ACL_LIST` and after a re-run of yaim confguration, we can retry to create/read/delete from test.vo, with the following results:

```bash
## Create test0 VOMS proxy for test.vo ##
$ voms-proxy-init --voms test.vo --cert test0.p12

## Check root directory: it's empty ##
$ gfal-ls srm://centos6-devel.cnaf.infn.it:8444/test.vo/

## Attempt to create PRD directory: it should fail! ##
$ gfal-mkdir srm://centos6-devel.cnaf.infn.it:8444/test.vo/PRD
gfal-mkdir: error: Permission denied

## Upload file into test.vo:## 
$ cat hello.txt | gfal-save srm://centos6-devel.cnaf.infn.it:8444/test.vo/Hello.txt

## Switch to user Enrico Vianello ##
$ voms-proxy-init --voms test.vo:/test.vo/Role=R1 --cert vianello-2014.p12
...

## Attempt to create PRD directory: a success is expected!
$ gfal-mkdir srm://centos6-devel.cnaf.infn.it:8444/test.vo/PRD

## Upload file into PRD directory ##
$ cat hello.txt | gfal-save srm://centos6-devel.cnaf.infn.it:8444/test.vo/PRD/Hello.txt

## Switch to test0 user ##
$ voms-proxy-init --voms test.vo --cert test0.p12 

## Download unauthorized file: expected error, success retrieved!!
$ globus-url-copy -vb gsiftp://centos6-devel.cnaf.infn.it:2811/storage/test.vo/PRD/Hello.txt /tmp/Hello.txt
Source: gsiftp://centos6-devel.cnaf.infn.it:2811/storage/test.vo/PRD/
Dest:   file:///tmp/
  Hello.txt

## just to see the different ACL:
$ getfacl /storage/test.vo/PRD/Hello.txt
getfacl: Removing leading '/' from absolute path names
# file: storage/test.vo/PRD/Hello.txt
# owner: storm
# group: storm
user::rw-
group::---
group:testvo:rw-
group:testvoprd:rw-
mask::rwx
other::---
```

As expected, the new ACE:

    group:testvo:rw-

is propagated into all the *test.vo* sub-directories and allow read operations on files that would be unreadable before:

```bash
$ globus-url-copy -vb gsiftp://centos6-devel.cnaf.infn.it:2811/storage/test.vo/PRD/Hello.txt /tmp/Hello.txt
Source: gsiftp://centos6-devel.cnaf.infn.it:2811/storage/test.vo/PRD/
Dest:   file:///tmp/
  Hello.txt
```

So, be careful and check both *Defualt ACL List* and *storm-authz.db* in order to avoid conflicts.



