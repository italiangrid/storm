---
layout: default
title: StoRM Storage Resource Manager - System Administration Guide - Installation
version: 1.11.4
---

# StoRM System Administration Guide <a name="top">&nbsp;</a>

version: {{ page.version }}

[<< System Administration Guide][sysadmin_index]

#### Table of contents

* [Installation Prerequisites](#installprereq)
  * [General EMI 3 installation instructions](#emi3instructions)
  * [System users and file limits](#systemusers)
  * [ACL support](#aclsupport)
  * [Extended Attribute support](#easupport)
  * [Storage Area's permissions](#sapermissions)
* [Installation guide](#installationguide)
  * [StoRM Upgrade to EMI3](#upgradetoemi3)
  * [Repository settings](#reposettings)
  * [Install StoRM nodes](#stormnodes)

## Installation Prerequisites <a name="installprereq">&nbsp;</a>

All the StoRM components are certified to work on Scientific Linux SL5/64 (x86\_64) and Scientific Linux SL6/64 (x86\_64) both with an EPEL repository for external dependencies. Therefore **install a proper version of Scientific Linux on your machine(s)**.
All the information about the OS Scientific Linux can be found at [here](http://www.scientificlinux.org). SL5 and SL6 are also available in the [SL5.X](http://linuxsoft.cern.ch/scientific/5x/) and [SL6.X](http://linuxsoft.cern.ch/scientific/6x/) repositories respectively mirrored at CERN. There are no specific minimum hardware requirements but it is advisable to have at least 1GB of RAM on Backend host.

### General EMI 3 installation instructions <a name="emi3instructions">&nbsp;</a>

Official releases are done in the contest of the EMI project so follow the [general EMI 3 installation instructions](https://twiki.cern.ch/twiki/bin/view/EMI/GenericInstallationConfigurationEMI3) as first installation prerequisite.
In particular, check the followings:

#### NTP service must be installed

**To check**:

```bash
$ rpm -qa | grep ntp-
ntp-4.2.2p1-9.el5_4.1
$ chkconfig --list | grep ntpd
ntpd            0:off   1:off   2:on    3:on    4:on    5:on    6:off
```
	
**To install**:

```bash
$ yum install ntp
$ chkconfig ntpd on
$ service ntpd restart
```

#### Hostname must be set correctly

Hostname must be a *Fully Qualified Domain Name* (FQDN).

**To check**:

```bash
$ hostname -f
```

The command must return the host FQDN.

**To correct**: Unless you are using bind or NIS for host lookups you can change the FQDN and the DNS domain name, which is part of the FQDN, in the /etc/hosts file.

```bash
$ cat /etc/hosts

# Do not remove the following line, or various programs
# that require network functionality will fail.
127.0.0.1       MYHOSTNAME.MYDOMAIN MYHOSTNAME localhost.localdomain localhost
::1             localhost6.localdomain6 localhost6
```

Set your own MYHOSTNAME and MYDOMAIN and restart the network service:

```bash
$ service network restart
```

#### Host needs a valid X.509 certificate

Hosts participating to the StoRM-SE (FE, BE, GridHTTP and GridFTP hosts) service must be configured with X.509 certificates signed by a trusted Certification Authority (CA). 
Usually, the **hostcert.pem** and **hostkey.pem** certificates are located in the /etc/grid-security/ directory, and they must have permission 0644 and 0400 respectively:

**To check**:

```bash
$ ls -l /etc/grid-security/hostkey.pem
-r-------- 1 root root 887 Mar  1 17:08 /etc/grid-security/hostkey.pem
$ ls -l /etc/grid-security/hostcert.pem
-rw-r--r-- 1 root root 1440 Mar  1 17:08 /etc/grid-security/hostcert.pem
$ openssl x509 -checkend 0 -in hostcert.pem
```

Certificate will not expire.

**To change permission**:

```bash
$ chmod 0400 /etc/grid-security/hostkey.pem
$ chmod 0644 /etc/grid-security/hostcert.pem
```

### System users and file limits <a name="systemusers">&nbsp;</a>

The StoRM frontend and backend services run by default as user **storm** (to 
change the default settings see [Backend Configuration](#beconf)). 

The StoRM GridHTTPs server runs as user **gridhttps** (this also can be configured,
see [GridHTTPs Configuration](#ghttpconf)). 

You can use the following commands to create the StoRM users on the machines
where you are deploying the services:

```bash
# add storm user (-M means without an home directory)
$ useradd -M storm
# add gridhttps user (specifying storm as group)
$ useradd gridhttps -M -G storm
```

You could also use specific user and group IDs as follows (change
the text contained in angled brackets with the appropriate
numerical value for your installation):

```bash
$ useradd -M storm -u <MY_STORM_UID> -g <MY_STORM_GID>
$ useradd gridhttps -M -G storm -u <MY_GHTTPS_UID> -g <MY_GHTTPS_GID>
```

<span class="label label-info">Important</span> Keep UIDs and GIDs aligned for
the StoRM users and groups on distributed deployments (i.e. when the services
are installed on different machines). This can be done using NIS (see a tutorial
[here](http://www.tldp.org/HOWTO/NIS-HOWTO/index.html)) or LDAP (see [Appendix
A](#AppendixA)).

#### File limits

The following settings are recommended to safely run the StoRM services.  Put
these settings in `/etc/security/limits.conf` or in a file contained in the
`/etc/security/limits.d` directory (recommended):

```bash
# StoRM frontend and backend services
storm hard nofile 8192
storm soft nofile 8192
# StoRM GridHTTPs service
gridhttps hard nofile 65535
gridhttps soft nofile 65535
```

### ACL support <a name="aclsupport">&nbsp;</a>

StoRM uses the ACLs on files and directories to implement the security model. In so doing, StoRM uses the native access to the file system. Therefore in order to ensure a proper running, ACLs need to be enabled on the underlying file system (sometimes they are enabled by default) and work properly.

**To check**:
	
```bash
$ touch test
$ setfacl -m u:storm:rw test
```

Note: the storm user adopted to set the ACL entry **must** exist.

```bash
$ getfacl test
# file: test
# owner: root
# group: root
user::rw-
user:storm:rw-
group::r--
mask::rw-
other::r--
$ rm -f test
```

If the getfacl and setfacl commands are not available on your host you have to **install** *acl* package:

```bash
$ yum install acl
```

To **enable** ACL (if needed), you must add the acl property to the relevant file system in your /etc/fstab file. For example:

```bash
$ vi /etc/fstab
  ...
/dev/hda3     /storage		ext3     defaults, acl     1 2
  ...
```
 
Then you need to remount the affected partitions as follows:

```bash
$ mount -o remount /storage
```

This is valid for different file system types (i.e., ext3, xfs, gpfs and others).

### Extended Attribute support <a name="easupport">&nbsp;</a>

StoRM uses the Extended Attributes (EA) on files to store some metadata related to the file (e.g. the checksum value); therefore in order to ensure a proper running, the EA support needs to be enabled on the underlying file system and work properly.
Note: Depending on OS kernel distribution, for Reiser3, ext2 and ext3 file systems, the default kernel configuration should not enable the EA.

**To check**:

```bash
$ touch testfile
$ setfattr -n user.testea -v test testfile
$ getfattr -d testfile
# file: testfile
user.testea="test"
$ rm -f testfile

If the getfattr and setfattrl commands are not available on your host, **install** *attr* package:

```bash
$ yum install attr
```

To **enable** EA (if needed) you must add the *user_xattr* property to the relevant file systems in your /etc/fstab file. For example:

```bash
$ vi /etc/fstab
  ...
/dev/hda3     /storage     ext3     defaults,acl,user_xattr     1 2
  ...
```

Then you need to remount the affected partitions as follows:

```bash
$ mount -o remount /storage
```

### Storage Area's permissions <a name="sapermissions">&nbsp;</a>

All the Storage Areas managed by StoRM needs to be owned by the STORM\_USER. This means that if STORM\_USER is _storm_, for example, the storage-area _test_ root directory permissions must be:

```bash
drwxr-x---+  2 storm storm
```
YAIM-StoRM doesn't set the correct permissions if the SA's root directory already exists. So, the site administrator has to take care of it. 
To set the correct permissions on a storage area, you can launch the following commands (assuming that storm runs as user `storm`, which
is the default):

```bash
chown -RL storm:storm <sa-root-directory>
chmod -R o-rwx,g+r <sa-root-directory>
```

If the storm GridHTTPs server is also enabled for a storage area, you also have
to make sure that the GridHTTPs can access the files. To set the correct
permissions, you could use the following command (assuming that the gridhttps
server runs as user `gridhttps`, which is the default): 

```bash
find <sa-root-directory> -type d -exec setfacl -m g:gridhttps:x {} \;
```

## Installation guide <a name="installationguide">&nbsp;</a>

### StoRM Upgrade to EMI3 <a name="upgradetoemi3">&nbsp;</a>

In order to upgrade your current version of StoRM from EMI1 or EMI2 to EMI3 you need to install the EMI3 repos.<br>
Depending on your platform, download and install the right EMI release package, as described in the [Repository settings](#emireposettings) section.

Then execute:

```bash
$ yum clean all
$ yum -y update
```

<div class="alert alert-error">
	<h4>Important!</h4>
	If you are upgrading a StoRM installation that runs on top of GPFS, be sure to install the `storm-native-libs-gpfs` package after the update has completed, issuing
the following command:
<pre>
$ yum install storm-native-libs-gpfs
</pre>
</div>

If you are also upgrading the StoRM GridHTTPs server component, after the installation you can remove tomcat because it's no more used by EMI3 GridHTTPs. Of course, you can do this if you are not using tomcat for other purposes:

```bash
$ yum remove tomcat5
```

To configure your StoRM services please read the [Configuration](#configuration) section.
<br/>
An example of YAIM use for configuring all the services on the same host is reported below:

```bash
$ /opt/glite/yaim/bin/yaim -c -d 6 -s /etc/storm/siteinfo/storm.def -n se_storm_backend -n se_storm_frontend -n se_storm_gridftp -n se_storm_gridhttps
```

Please take a look at the [Launching YAIM configuration](#launchyaim) section for further details.

### Repository settings <a name="reposettings">&nbsp;</a>

In order to install all the stuff requested by StoRM, some repositories have to be necessarily configured in the /etc/yum.repos.d directory. 
These are EPEL, EGI and EMI and have to be installed, as prerequisite, as we have already seen in the paragraph [general EMI 3 installation instructions](#emi3instructions).

#### Common repository settings <a name="commonreposettings">&nbsp;</a>

To install **EPEL Repository** download and install the EPEL release file.

SL5:

```bash
$ wget http://archives.fedoraproject.org/pub/epel/5/x86_64/epel-release-5-4.noarch.rpm
$ yum localinstall --nogpgcheck epel-release-5-4.noarch.rpm
```

SL6:

```bash    
$ wget http://www.nic.funet.fi/pub/mirrors/fedora.redhat.com/pub/epel/6/x86_64/epel-release-6-8.noarch.rpm
$ yum localinstall --nogpgcheck epel-release-6-8.noarch.rpm
```

To install **EGI Trust Anchors Repository** follow [EGI instructions](https://wiki.egi.eu/wiki/EGI_IGTF_Release#Using_YUM_package_management).

You must disable the **DAG repository** if enabled. To check if it is enabled:

```bash	
$ grep enabled /etc/yum.repos.d/dag.repo
 enabled=0
```

To disable the DAG repository, if needed, you must set to 0 the enabled property in your /etc/yum.repos.d/dag.repo file:

```bash
$ vi /etc/yum.repos.d/dag.repo
  ...
 enabled=0
  ...
```

#### EMI Repository settings <a name="emireposettings">&nbsp;</a>

To install **EMI repository** download and install the EMI release file:

SL5:

```bash
$ wget http://emisoft.web.cern.ch/emisoft/dist/EMI/3/sl5/x86_64/base/emi-release-3.0.0-2.el5.noarch.rpm
$ yum localinstall --nogpgcheck emi-release-3.0.0-2.el5.noarch.rpm
```

SL6:

```bash
$ wget http://emisoft.web.cern.ch/emisoft/dist/EMI/3/sl6/x86_64/base/emi-release-3.0.0-2.el6.noarch.rpm
$ yum localinstall --nogpgcheck emi-release-3.0.0-2.el6.noarch.rpm
```

#### StoRM Repository settings

StoRM can also be installed from StoRM PT own repositories.
Note that the StoRM PT repositories only provide the latest version of the certified StoRM packages.
You still need to install EMI3 repositories (as detailed above) for installations to work as expected.

To install the repository files, run the following commands (as root):

```bash
    (SL5) $ wget http://italiangrid.github.io/storm/repo/storm_sl5.repo -O /etc/yum.repos.d/storm_sl5.repo
    (SL6) $ wget http://italiangrid.github.io/storm/repo/storm_sl6.repo -O /etc/yum.repos.d/storm_sl6.repo
```

### Install StoRM nodes <a name="stormnodes">&nbsp;</a>

In order to install StoRM components refresh the yum cache:

```bash
$ yum clean all
```

Install the StoRM metapackages you need in every node partecipating to the StoRM instance.

```bash
$ yum install emi-storm-backend-mp
   ...
$ yum install emi-storm-frontend-mp
   ...
$ yum install emi-storm-globus-gridftp-mp
   ...
$ yum install emi-storm-gridhttps-mp
   ...
```

The storm-srm-client is distributed with the UI EMI components, but if you need it on your node you can install it using the command:

```bash
$ yum install emi-storm-srm-client-mp
```

<hr/>

#### >> [go to configuration]({{ site.baseurl }}/documentation/sysadmin-guide/{{ page.version }}/configuration) 

[sysadmin_index]: {{site.baseurl}}/documentation/sysadmin-guide/{{ version }}
