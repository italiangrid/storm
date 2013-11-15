---
layout: default
title: StoRM Storage Resource Manager - System Administration Guide
version: 1.11.3
---

# StoRM System Administration Guide

version: {{ page.version }}

#### Table of contents

* [Installation Prerequisites](#installprereq)
  * [General EMI 3 installation instructions](#emi3instructions)
  * [System users](#systemusers)
  * [ACL support](#aclsupport)
  * [Extended Attribute support](#easupport)
  * [Storage Area's permissions](#sapermissions)
* [Installation guide](#installationguide)
  * [StoRM Upgrade to EMI3](#upgradetoemi3)
  * [Repository settings](#reposettings)
  * [Install StoRM nodes](#stormnodes)
* [Configuration](#configuration)
  * [General YAIM variables](#yaimvariables)
  * [Frontend configuration](#feconf)
  * [Backend configuration](#beconf)
  * [GridHTTPs configuration](#ghttpconf)
  * [Launching YAIM configuration](#launchyaim)
* [Advanced Configuration](#advconf)
  * [Frontend Advanced Configuration](#fe_advconf)
  * [Backend Advanced Configuration](#be_advconf)
  * [GridFTP Advanced Configuration](#gftp_advconf)
  * [GridHTTPs Advanced Configuration](#ghttp_advconf)
  * [StoRM EMIR Configuration](#emir_advconf)
* [Appendix A](#AppendixA)

## Installation Prerequisites <a name="installprereq">&nbsp;</a>

All the StoRM components are certified to work on Scientific Linux SL5/64 (x86_64) and Scientific Linux SL6/64 (x86_64) both with an EPEL repository for external dependencies. Therefore **install a proper version of Scientific Linux on your machine(s)**.
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

### System users <a name="systemusers">&nbsp;</a>

StoRM Backend has to be run by a specific STORM\_USER. By default STORM\_USER is *storm*, but admins can also configure it (see [Backend Configuration](#beconf)). 
If you need a GridHTTPs node, this service also has to be run by another specific user, STORM\_GRIDHTTPS\_USER, which must belong to the STORM\_USER group. 
By default STORM\_GRIDHTTPS\_USER is *gridhttps*, but admins can also configure it (see [GridHTTPs Configuration](#ghttpconf)). 
It is advisable to manually configure host(s) with this two users before install services. 
For example, to create *storm* and *gridhttps* users you can launch the following commands:

```bash
# add storm user (-M means without an home directory)
$ useradd -M storm
# add gridhttps user (specifying storm as group)
$ useradd gridhttps -M -G storm
```

or, if needed, you can specify users' UID and GID, as follows:

```bash
$ useradd -M storm -u MY_STORM_UID -g MY_STORM_GID
$ useradd gridhttps -M -G storm -u MY_GHTTPS_UID -g MY_GHTTPS_GID
```

Specifying the same UID and GID is nececcary when you are going to install StoRM on a multi-node scenario because users running Backend and GridHTTP services must be the same on every node (UID and GID including).
<br/>
<br/>
For example, if *storm* is the user that runs Backend service on host A and *gridhttps* is the user that runs GridHTTP on host B, both of these hosts **must** have *storm* and *gridhttps* users and groups with respectively **the same GID and UID**.
To satisfy this requirement you can configure a NIS service for the involved hosts and add the two users to the NIS maps. A tutorial on how to setup a NIS service can be found [here](http://www.tldp.org/HOWTO/NIS-HOWTO/index.html).
Another valid solution to share GID and UID among different hosts and provide a user authentication can be found with a client-server LDAP installation, as described in [Appendix A](#AppendixA).

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

If your installation has some storage area that already exist, those storage areas must have the right permissions. 
For example, it needs ${STORM\_USER} as owner of the storage area root directory and recoursively of the all subdirectories and files contained.
To set the correct permissions on a storage area that has ${dir} as root directory, you can launche the following commands:

```bash
chown -RL ${STORM_USER}:${STORM_USER} ${dir}
chmod -R o-rwx,g+r ${dir}
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

## Configuration <a name="configuration">&nbsp;</a>

StoRM is configured by using the YAIM tool, that is a set of configuration scripts that read a set of configuration files.
It's **recommended** to follow the [yaim configuration](#configuration) or the [advanced configuration](#advconf) guides to set up your StoRM deployment.

Optionally, as a *quick start*, you can follow these instructions to quickly configure StoRM.

First of all, download and install the *pre-assembled configuration*:

```bash
$ yum install storm-pre-assembled-configuration
```

and then edit */etc/storm/siteinfo/storm.def* with:

```bash
	STORM_BACKEND_HOST="<your full hostname>"
```

In case of SL6 and SL5.X with X>=9 you probably need to modify also:

```bash
	JAVA_LOCATION="/usr/lib/jvm/java"
```

Then you can configure StoRM by launching YAIM with:

```bash
$ /opt/glite/yaim/bin/yaim -c -d 6 -s /etc/storm/siteinfo/storm.def -n se_storm_backend -n se_storm_frontend -n se_storm_gridftp -n se_storm_gridhttps
```

as better explained [here](#launchyaim).

### General YAIM variables <a name="yaimvariables">&nbsp;</a>

[basic]({{site.baseurl}}/documentation/examples/1.11.2/basic-storm-standalone-configuration.html)

Create a **site-info.def** file in your CONFDIR/ directory. Edit this file by providing a value to the general variables summarized in Tab.1.

| Var. Name		  	| Description	| Mandatory |
|:------------------|:--------------|:---------:|
|SITE\_NAME			|It's the human-readable name of your site used to set the Glue-SiteName attribute.<br/>Example: SITE\_NAME="INFN EMI TESTBED" | Yes
|BDII\_HOST			|BDII hostname.<br/>Example: BDII\_HOST="emitb-bdii-site.cern.ch" | Yes
|NTP\_HOSTS\_IP		|Space separeted list of the IP addresses of the NTP servers (preferably set a local ntp server and a public one, e.g. pool.ntp.org).<br/>Example: NTP\_HOSTS\_IP="131.154.1.103" | Yes
|USERS\_CONF		|Path to the file containing the list of Linux users (pool accounts) to be created. This file must be created by the site administrator. It contains a plain list of the users and their IDs. An example of this configuration file is given in /opt/glite/yaim/examples/users.conf file. More details can be found in the User configuration section in the YAIM guide. | Yes
|GROUPS\_CONF		|Path to the file containing information on the map- ping between VOMS groups and roles to local groups. An example of this configuration file is given in /opt/glite/yaim/examples/groups.conf file. More details can be found in the Group configuration section in the YAIM guide. | Yes
|MYSQL\_PASSWORD	|mysql root password.<br/>Example: MYSQL\_PASSWORD="carpediem" | Yes
|VOS				|List of supported VOs.<br/>Example: VOS="testers.eu-emi.eu dteam" | Yes
|STORM\_BE\_XMLRPC\_TOKEN	|Token used in communication to the StoRM Backend | Yes


{% assign label_title="Table 1" %}
{% assign label_description="General YAIM Variables." %}
{% include documentation/label.html %}

### Frontend configuration <a name="feconf">&nbsp;</a>

Specific YAIM variables are in the following file:

```bash
$ /opt/glite/yaim/examples/siteinfo/services/se_storm_frontend
```

Please copy and edit that file in your CONFDIR/services directory. You have to set at least the STORM\_DB\_PWD variable and check the other variables to evaluate if you like the default set or if you want to change those settings. Tab.2 summaries YAIM variables for StoRM Frontend component.

|	Var. Name							|	Description	|
|:--------------------------------------|:--------------|
|ARGUS\_PEPD\_ENDPOINTS					|The complete service endpoint of Argus PEP server. Mandatory if STORM\_FE\_USER\_BLACKLISTING is true. Example: https://host.domain:8154/authz
|STORM\_BACKEND\_REST\_SERVICES\_PORT	|StoRM backend server rest port. Optional variable. Default value: **9998**
|STORM\_BE\_XMLRPC\_PATH				|StoRM Backend XMLRPC server path. <br/>Optional variable. Default value: **/RPC2**
|STORM\_BE\_XMLRPC\_PORT				|StoRM Backend XMLRPC server port. <br/>Optional variable. Default value: **8080**
|STORM\_CERT\_DIR						|Host certificate directory for StoRM Frontend service. Optional variable. Default value: **/etc/grid-security/${STORM\_USER}**
|STORM\_DB\_HOST						|Host for database connection. <br/>Optional variable. Default value: **localhost**
|STORM\_DB\_PWD							|Password for database connection. **Mandatory**.
|STORM\_DB\_USER						|User for database connection. Default value: **storm**
|STORM\_FE\_ENABLE\_MAPPING				|Enable the check in gridmapfile for client DN. <br/>Optional variable. Available values: true, false. Default value: **false**
|STORM\_FE\_ENABLE\_VOMSCHECK			|Enable the check in gridmapfile for client VOMS attributes. <br/>Optional variable. Available values: true, false. Default value: **false**
|STORM\_FE\_GSOAP\_MAXPENDING			|Max number of request pending in the GSOAP queue. Optional variable. Default value: **2000**
|STORM\_FE\_LOG\_FILE					|StoRM frontend log file.<br/>Optional variable. Default value: **/var/log/storm/storm-frontend.log**
|STORM\_FE\_LOG\_LEVEL					|StoRM Frontend log level.<br/>Optional variable. Available values: KNOWN, ERROR, WARNING, INFO, DEBUG, DEBUG2.<br/>Default value: **INFO**
|STORM\_FE\_MONITORING\_DETAILED		|Flag to enable/disable detailed SRM requests Monitoring. Optional variable. Available values: true, false. Default value: **false**
|STORM\_FE\_MONITORING\_ENABLED			|Flag to enable/disable SRM requests Monitoring.<br/>Optional variable. Available values: true, false. Default value: **true**
|STORM\_FE\_MONITORING\_TIME\_INTERVAL	|Time intervall in seconds between each Monitoring round. Optional variable. Default value: **60**
|STORM\_FE\_THREADS\_MAXPENDING			|Max number of request pending in the Threads queue. Optional variable. Default value: **200**
|STORM\_FE\_THREADS\_NUMBER				|Max number of threads to manage user's requests. Optional variable. Default value: **50**
|STORM\_FE\_USER\_BLACKLISTING			|Flag to enable/disable user blacklisting.<br/>Optional variable. Available values: true, false. Default value: **false**
|STORM\_FE\_WSDL						|WSDL to be returned to a GET request.<br/>Optional variable. Default value: **/usr/share/wsdl/srm.v2.2.wsdl**
|STORM\_FRONTEND\_OVERWRITE				|This parameter tells YAIM to overwrite storm-frondend.conf configuration file.<br/>Optional variable. Available values: true, false. Default value: **true**
|STORM\_FRONTEND\_PORT					|StoRM Frontend service port. Optional variable. Default value: **8444**
|STORM\_PEPC\_RESOURCEID				|Argus StoRM resource identifier. Optional variable. Default value: **storm**
|STORM\_PROXY\_HOME						|Directory used to exchange proxies.<br/>Optional variable. Default value: **/etc/storm/tmp**
|STORM\_USER							|Service user.<br/>Optional variable. Default value: **storm**

{% assign label_title="Table 2" %}
{% assign label_description="Specific StoRM Frontend Variables." %}
{% include documentation/label.html %}

**NOTE** - **_The following table contains the YAIM variables no more used from StoRM v.1.11.2_**:

|	Var. Name					|	Description	|
|:------------------------------|:--------------|
|STORM\_FE\_BE\_XMLRPC\_HOST	|**DELETED** It was used to tell to the Frontend the XMLRPC endpoint. But this endpoint, for the Frontend, is **always** the StoRM Backend hostname which value is already contained by STORM\_BACKEND\_HOST variable. So yaim now sets _be.xmlrpc.host_ frontend's variable with STORM\_BACKEND\_HOST.
|STORM\_FE\_BE\_XMLRPC\_PATH	|**RENAMED** into STORM\_BE\_XMLRPC\_PATH, see Table 2
|STORM\_FE\_BE\_XMLRPC\_PORT	|**RENAMED** into STORM\_BE\_XMLRPC\_PORT, see Table 2

{% assign label_title="Table 3" %}
{% assign label_description="YAIM variables no more used from StoRM v.1.11.2" %}
{% include documentation/label.html %}

### Backend configuration <a name="beconf">&nbsp;</a>

Specific YAIM variables are in the following file:

```bash
$ /opt/glite/yaim/exaples/siteinfo/services/se_storm_backend
```

Please copy and edit that file in your CONFDIR/services directory. 
You have to set at least these variables:

- STORM\_BACKEND\_HOST
- STORM\_DEFAULT\_ROOT
- STORM\_DB\_PWD

and check the other variables to evaluate if you like the default set or if you want to change those settings. Tab.3 summaries YAIM variables for StoRM Backend component.

|	Var. Name							|	Description	|
|:--------------------------------------|:--------------|
|STORM\_ACLMODE							|ACL enforcing mechanism (default value for all Storage Areas). Note: you may change the settings for each SA acting on STORM\_{SA}\_ACLMODE variable. Available values: aot, jit (use aot for WLCG experiments).<br/>Optional variable. Default value: **aot**
|STORM\_ANONYMOUS\_HTTP\_READ			|Storage Area anonymous read access via HTTP. Note: you may change the settings for each SA acting on STORM\_{SA}\_ANONYMOUS\_HTTP\_READ variable.<br/>Optional variable. Available values: true, false. Default value: **false**
|STORM\_AUTH							|Authorization mechanism (default value for all Storage Ar- eas). Note: you may change the settings for each SA acting on $STORM\_{SA}\_AUTH variable Available values: permit-all, deny-all, FILENAME.<br/>Optional variable. Default value: **permit-all**
|STORM\_BACKEND\_HOST					|Host name of the StoRM Backend server. **Mandatory**.
|STORM\_BACKEND\_REST\_SERVICES\_PORT	|StoRM backend server rest port. Optional variable. Default value: **9998**
|STORM\_CERT\_DIR						|Host certificate directory for StoRM Backend service.<br/>Optional variable. Default value: **/etc/grid-security/${STORM\_USER}**
|STORM\_DEFAULT\_ROOT					|Default directory for Storage Areas. **Mandatory**.
|STORM\_DB\_HOST						|Host for database connection.<br/>Optional variable. Default value: **localhost**
|STORM\_DB\_PWD							|Password for database connection. **Mandatory**.
|STORM\_DB\_USER						|User for database connection.<br/>Optional variable. Default value: **storm**
|STORM\_FRONTEND\_HOST\_LIST			|StoRM Frontend service host list: SRM endpoints can be more than one virtual host different from STORM\_BACKEND\_HOST (i.e. dynamic DNS for multiple StoRM Frontends).<br/>Optional variable. Default value: **$STORM\_BACKEND\_HOST**
|STORM\_FRONTEND\_PATH					|StoRM Frontend service path.<br/>Optional variable. Default value: **/srm/managerv2**
|STORM\_FRONTEND\_PORT					|StoRM Frontend service port. Optional variable. Default value: **8444**
|STORM\_FRONTEND\_PUBLIC\_HOST			|StoRM Frontend service public host.<br/>Optional variable. Default value: **$STORM\_BACKEND\_HOST**
|STORM\_FSTYPE							|File System Type (default value for all Storage Areas). Note: you may change the settings for each SA acting on $STORM\_{SA}\_FSTYPE variable.<br/>Optional variable. Available values: posixfs, gpfs. Default value: **posixfs**
|STORM\_GRIDFTP\_POOL\_LIST				|GridFTP servers pool list (default value for all Storage Areas). Note: you may change the settings for each SA acting on $STORM\_{SA}\_GRIDFTP\_POOL\_LIST variable.<br/>ATTENTION: this variable define a list of pair values space-separated: host weight, e.g.: STORM\_GRIDFTP\_POOL\_LIST="host1 weight1, host2 weight2, host3 weight3" Weight has 0-100 range; if not specified, weight will be 100.<br/>Optional variable. Default value: **$STORM\_BACKEND\_HOST**
|STORM\_GRIDFTP\_POOL\_STRATEGY			|Load balancing strategy for GridFTP server pool (default value for all Storage Areas). Note: you may change the settings for each SA acting on $STORM\_{SA}\_GRIDFTP\_POOL\_STRATEGY variable.<br/>Optional variable. Available values: round-robin, smart-rr, random, weight. Default value: **round-robin**
|STORM\_GRIDHTTP\_POOL\_LIST			|GridHTTPs servers pool list (default value for all Storage Areas). Specify here all the endpoints that BE can use when it builds an HTTP TURL. The endpoint retrieved with the TURL will be builded using STORM\_GRIDHTTP\_HTTP\_PORT as port. _Note_: you may change the settings for each SA acting on $STORM\_{SA}\_GRIDHTTP\_POOL\_LIST variable.<br/>ATTENTION: this variable define a list of pair values space-separated: host weight, e.g.: STORM\_GRIDHTTP\_POOL\_LIST="host1 weight1, host2 weight2, host3 weight3" Weight has 0-100 range; if not specified, weight will be 100.<br/>Optional variable. Default value: **$STORM\_BACKEND\_HOST**
|STORM\_GRIDHTTP\_POOL\_STRATEGY		|Load balancing strategy for GridHTTPs servers pool, used when the BE builds an HTTP TURL. Optional variable. Available values: round-robin, smart-rr, random, weight. Default value: **round-robin**
|STORM\_GRIDHTTPS\_ENABLED				|If set to true enables the support of http(s) protocols.<br/>Optional variable. Available values: true, false. Default value: **false**
|STORM\_GRIDHTTPS\_PLUGIN\_CLASSNAME	|GridHTTPs plugin implementation class.<br/>Optional variable. Mandatory to value it.grid.storm.https.GhttpsHTTPSPluginInterface if StoRM GridHTTPs is installed.<br/>Default value: **it.grid.storm.https.HTTPSPluginInterfaceStub**
|STORM\_GRIDHTTPS\_POOL\_LIST			|GridHTTPs servers pool list (default value for all Storage Areas). Specify here all the endpoints that BE can use when it builds an HTTPS TURL. The endpoint retrieved with the TURL will be builded using STORM\_GRIDHTTPS\_HTTPS\_PORT as port. _Note_: you may change the settings for each SA acting on $STORM\_{SA}\_GRIDHTTPS\_POOL\_LIST variable.<br/>ATTENTION: this variable define a list of pair values space-separated: host weight, e.g.: STORM\_GRIDHTTPS\_POOL\_LIST="host1 weight1, host2 weight2, host3 weight3" Weight has 0-100 range; if not specified, weight will be 100.<br/>Optional variable. Default value: **$STORM\_BACKEND\_HOST**
|STORM\_GRIDHTTPS\_POOL\_STRATEGY		|Load balancing strategy for GridHTTPs servers pool, used when the BE builds an HTTPS TURL. Optional variable. Available values: round-robin, smart-rr, random, weight. Default value: **round-robin**
|STORM\_GRIDHTTPS\_SERVER\_USER\_UID	|StoRM GridHTTPs server service user UID. It's the user id of the user specified by the value of the GridHTTPs' configuration variable STORM\_GRIDHTTPS\_USER. Note: from EMI3 this user id is no more tomcat's user uid **Mandatory if STORM\_GRIDHTTPS\_ENABLED is true**
|STORM\_GRIDHTTPS\_SERVER\_GROUP\_UID	|StoRM GridHTTPs server service user GID. It's the group id of the user specified by the value of the GridHTTPs' configuration variable STORM\_GRIDHTTPS\_USER. **Mandatory if STORM\_GRIDHTTPS\_ENABLED is true**
|STORM\_GRIDHTTPS\_HTTP\_PORT			|StoRM GridHTTPs server mapping service port. Optional variable. Default value: **8086**
|STORM\_INFO\_FILE\_SUPPORT				|If set to false, the following variables prevent the corresponding protocol to be published by the StoRM gip.<br/>Optional variable. Available values: true, false. Default value: **true**
|STORM\_INFO\_GRIDFTP\_SUPPORT			|If set to false, the following variables prevent the corresponding protocol to be published by the StoRM gip.<br/>Optional variable. Available values: true, false. Default value: **true**
|STORM\_INFO\_RFIO\_SUPPORT				|If set to false, the following variables prevent the corresponding protocol to be published by the StoRM gip. <br/>Optional variable. Available values: true, false. Default value: **false**
|STORM\_INFO\_ROOT\_SUPPORT				|If set to false, the following variables prevent the corresponding protocol to be published by the StoRM gip.<br/>Optional variable. Available values: true, false. Default value: **false**
|STORM\_INFO\_HTTP\_SUPPORT				|If set to false, the following variables prevent the corresponding protocol to be published by the StoRM gip.<br/>Optional variable. Available values: true, false. Default value: **false**
|STORM\_INFO\_HTTPS\_SUPPORT			|If set to false, the following variables prevent the corresponding protocol to be published by the StoRM gip.<br/>Optional variable. Available values: true, false. Default value: **false**
|STORM\_INFO\_OVERWRITE					|This parameter tells YAIM to overwrite static-file-StoRM.ldif configuration file.<br/>Optional variable. Available values: true, false. Default value: **true**
|STORM\_NAMESPACE\_OVERWRITE			|This parameter tells YAIM to overwrite namespace.xml configuration file. Optional variable. Available values: true, false. Default value: **true**
|STORM\_PROXY\_HOME						|Directory used to exchange proxies.<br/>Optional variable. Default value: **/etc/storm/tmp**
|STORM\_RFIO\_HOST						|Rfio server (default value for all Storage Areas). Note: you may change the settings for each SA acting on $STORM\_{SA}\_RFIO\_HOST variable.<br/>Optional variable. Default value: **$STORM\_BACKEND\_HOST**
|STORM\_ROOT\_HOST						|Root server (default value for all Storage Areas). Note: you may change the settings for each SA acting on $STORM\_{SA}\_ROOT\_HOST variable.<br/>Optional variable. Default value: **$STORM\_BACKEND\_HOST**
|STORM\_SERVICE\_SURL\_DEF\_PORTS		|Comma-separated list of managed SURL's default ports used to check SURL validity.<br/>Optional variable. Default value: **8444**
|STORM\_SIZE\_LIMIT						|Limit Maximum available space on the Storage Area (default value for all Storage Areas).<br/>Note: you may change the settings for each SA acting on $STORM\_{SA}\_SIZE\_LIMIT variable. Optional variable. Available values: true, false. Default value: **true**
|STORM\_STORAGEAREA\_LIST				|List of supported Storage Areas. Usually at least one Storage Area for each VO specified in $VOS should be created.<br/>Optional variable. Default value: **$VOS**
|STORM_STORAGECLASS						|Storage Class type (default value for all Storage Areas). Note: you may change the settings for each SA acting on $STORM\_{SA}\_STORAGECLASS variable. <br/>Optional variable. Available values: T0D1, T1D0, T1D1. No default value.
|STORM\_SURL\_ENDPOINT\_LIST			|StoRM SURL endpoint list. Optional variable. Default values: **srm://${STORM\_FRONTEND\_PUBLIC\_HOST}:<br/>${STORM\_FRONTEND\_PORT}<br/>/${STORM\_FRONTEND\_PATH}**
|STORM\_USER							|Service user. Optional variable. Default value: **storm**
|STORM\_ENDPOINT\_QUALITY\_LEVEL		|Endpoint maturity level to be published by the StoRM gip. Optional variable. Default value: **2**
|STORM\_ENDPOINT\_SERVING\_STATE		|Endpoint serving state to be published by the StoRM gip. Optional variable. Default value: **4**
|STORM\_ENDPOINT\_CAPABILITY			|Capability according to OGSA to be published by the StoRM gip. Optional variable. Default value: **data.management.storage**

{% assign label_title="Table 4" %}
{% assign label_description="Specific StoRM Backend Variables." %}
{% include documentation/label.html %}

Then, for each Storage Area listed in the STORM\_STORAGEAREA\_LIST variable, which is not the name of a valid VO, you have to edit the STORM\_{SA}\_VONAME compulsory variable (detailed in Table 5). {SA} has to be written in capital letters as in the other variables included in the **site-info.def** file, otherwise default values will be used.

<div class="alert alert-warning">
	<h4>Warning!</h4>
For the DNS-like names, that use special characters as '.' or '-' you have to remove the '.' and '-'.<br/>	
For example the {SA} value for the storage area "testers.eu-emi.eu" must be TESTERSEUEMIEU:
<pre>
STORM_TESTERSEUEMIEU_VONAME=testers.eu-emi.eu
</pre>
</div>

For each {SA} listed in STORM\_STORAGEAREA\_LIST you have to set at least these variables: STORM\_{SA}\_ONLINE_SIZE
You can edit the optional variables summarized in Table 5.

|	Var. Name							|	Description	|
|:--------------------------------------|:--------------|
|STORM\_{SA}\_VONAME					|Name of the VO that will use the Storage Area. Use the complete name, e.g., "lights.infn.it" or **'*'** to specify that there is no VO associated to the storage area (it's readable and writable from everyone - less than other filters). This variable becames **mandatory if the value of {SA} is not the name of a VO**.
|STORM\_{SA}\_ANONYMOUS\_HTTP\_READ		|Storage Area anonymous read access via HTTP.<br/>Optional variable. Available values: true, false. Default value: **false**
|STORM\_{SA}\_DN\_C\_REGEX				|Regular expression specifying the format of C (Country) field of DNs that will use the Storage Area. Optional variable.
|STORM\_{SA}\_DN\_O\_REGEX				|Regular expression specifying the format of O (Organization name) field of DNs that will use the Storage Area. Optional variable.
|STORM\_{SA}\_DN\_OU\_REGEX				|Regular expression specifying the format of OU (Organizational Unit) field of DNs that will use the Storage Area. Optional variable.
|STORM\_{SA}\_DN\_L\_REGEX				|Regular expression specifying the format of L (Locality) field of DNs that will use the Storage Area. Optional variable.
|STORM\_{SA}\_DN\_CN\_REGEX				|Regular expression specifying the format of CN (Common Name) field of DNs that will use the Storage Area. Optional variable.
|STORM\_{SA}\_ACCESSPOINT				|Path exposed by the SRM into the SURL. Optional variable. Default value: **{SA}**
|STORM\_{SA}\_ACLMODE					|See STORM\_ACLMODE definition. Optional variable. Default value: **$STORM_ACLMODE**
|STORM\_{SA}\_AUTH						|See STORM\_AUTH definition. Optional variable. Default value: **$STORM_AUTH**
|STORM\_{SA}\_DEFAULT\_ACL\_LIST		|A list of ACL entries that specifies a set of local groups with corresponding permissions (R, W, RW) using the following syntax: groupname1:permission1 \[groupname2:permission2\] \[...\]
|STORM\_{SA}\_FILE\_SUPPORT				|Enable the corresponding protocol. Optional variable. Default value: **$STORM\_INFO\_{PROTOCOL}\_SUPPORT**
|STORM\_{SA}\_GRIDFTP\_SUPPORT			|Enable the corresponding protocol. Optional variable. Default value: **$STORM\_INFO\_{PROTOCOL}\_SUPPORT**
|STORM\_{SA}\_RFIO\_SUPPORT				|Enable the corresponding protocol. Optional variable. Default value: **$STORM\_INFO\_{PROTOCOL}\_SUPPORT**
|STORM\_{SA}\_ROOT\_SUPPORT				|Enable the corresponding protocol. Optional variable. Default value: **$STORM\_INFO\_{PROTOCOL}\_SUPPORT**
|STORM\_{SA}\_HTTP\_SUPPORT				|Enable the corresponding protocol. Optional variable. Default value: **$STORM\_INFO\_{PROTOCOL}\_SUPPORT**
|STORM\_{SA}\_HTTPS\_SUPPORT			|Enable the corresponding protocol. Optional variable. Default value: **$STORM\_INFO\_{PROTOCOL}\_SUPPORT**
|STORM\_{SA}\_FSTYPE					|See STORM\_{SA}\_FSTYPE definition. Optional variable. Available values: posixfs, gpfs. Default value: **$STORM\_FSTYPE**
|STORM\_{SA}\_GRIDFTP\_POOL\_LIST		|See STORM\_GRIDFTP\_POOL\_LIST definition. Optional variable. Default value: **$STORM\_GRIDFTP\_POOL\_LIST**
|STORM\_{SA}\_GRIDFTP\_POOL\_STRATEGY	|See STORM\_GRIDFTP\_POOL\_STRATEGY definition.	Optional variable. Default value: **$STORM_GRIDFTP_POOL_STRATEGY**
|STORM\_{SA}\_ONLINE\_SIZE				|Total size assigned to the Storage Area Expressed in GB. Must be an integer value. **Mandatory**.
|STORM\_{SA}\_USED\_ONLINE\_SIZE		|Storage space currently used in the Storage Area expressed in Bytes. Must be an integer value. Used by YAIM to populate used-space.ini file.
|STORM\_{SA}\_QUOTA						|Enables the quota management for the Storage Area and it works only on GPFS filesystem. Optional variable. Available values: true, false. Default value: **false**
|STORM\_{SA}\_QUOTA\_DEVICE				|GPFS device on which the quota is enabled. It is mandatory if STORM\_{SA}\_QUOTA variable is set. No default value.
|STORM\_{SA}\_QUOTA\_USER				|GPFS quota scope. Only one of the following three will be used (the first one with the highest priority in this order: USER, then GROUP, then FILESET). Optional variable. No default value.
|STORM\_{SA}\_QUOTA\_GROUP				|GPFS quota scope. Only one of the following three will be used (the first one with the highest priority in this order: USER, then GROUP, then FILESET). Optional variable. No default value.
|STORM\_{SA}\_QUOTA\_FILESET			|GPFS quota scope. Only one of the following three will be used (the first one with the highest priority in this order: USER, then GROUP, then FILESET). Optional variable. No default value.
|STORM\_{SA}\_RFIO_HOST					|See STORM\_RFIO\_HOST definition. Optional variable. Default value: **$STORM\_RFIO\_HOST**
|STORM\_{SA}\_ROOT						|Physical storage path for the VO. Optional variable. Default value: **$STORM\_DEFAULT\_ROOT/{SA}**
|STORM\_{SA}\_ROOT\_HOST				|See STORM\_ROOT\_HOST definition. Optional variable. Default value: **$STORM\_ROOT\_HOST**
|STORM\_{SA}\_SIZE\_LIMIT				|See STORM\_SIZE\_LIMIT definition. Default value: **$STORM\_SIZE\_LIMIT**
|STORM\_{SA}\_STORAGECLASS				|See STORM\_STORAGECLASS definition. Available values: T0D1, T1D0, T1D1, null. No default value.
|STORM\_{SA}\_TOKEN						|Storage Area token, e.g: LHCb\_RAW, INFNGRID\_DISK. No default value.

{% assign label_title="Table 5" %}
{% assign label_description="Storage Area Variables." %}
{% include documentation/label.html %}

### GridHTTPs configuration <a name="ghttpconf">&nbsp;</a>

Specific variables are in the following file:
	
```bash
$ /opt/glite/yaim/examples/siteinfo/services/se_storm_gridhttps
```

Please copy and edit that file in your CONFDIR/services directory. You have to set at least these variables:

- STORM\_BACKEND\_HOST

and check the other variables to evaluate if you like the default set or if you want to change those settings. Table 6 summaries YAIM variables for StoRM GridHTTPs component.

|	Var. Name							|	Description	|
|:--------------------------------------|:--------------|
|STORM\_BACKEND\_HOST					|Host name of the StoRM Backend server. **Mandatory**.
|STORM\_BACKEND\_REST\_SERVICES\_PORT	|StoRM Backend server REST port. Optional variable. Default value: **9998**
|STORM\_BE\_XMLRPC\_PORT				|StoRM Backend server XMLRPC port. Optional variable. Default value: **8080**
|STORM\_FRONTEND\_PORT					|StoRM Frontend server SRM port. Optional variable. Default value: **8444**
|STORM\_GRIDHTTPS\_CERT\_DIR			|Host certificate folder for SSL connector. Optional variable. <br/>Default value: **/etc/grid-security/${STORM\_GRIDHTTPS\_USER}**
|STORM\_GRIDHTTPS\_HTTP\_ENABLED		|Flag that enables/disables http connections. Optional variable. Available values: true, false. <br/>Default value: **true**
|STORM\_GRIDHTTPS\_HTTP\_PORT			|StoRM GridHTTPs http port. Optional variable. <br/>Default value: **8085**
|STORM\_GRIDHTTPS\_HTTPS\_PORT			|StoRM GridHTTPs https port Optional variable. <br/>Default value: **8443**
|STORM\_GRIDHTTPS\_USER					|StoRM GridHTTPs service user. Optional variable. <br/>Default value: **gridhttps**
|STORM\_SRM\_ENDPOINT					|StoRM SRM EndPoint. Optional variable. <br/>Default value: **${STORM\_BACKEND\_HOST}:<br/>${STORM\_FRONTEND\_PORT}**
|STORM\_USER							|StoRM Backend service user. Optional variable. <br/>Default value: **storm**
|X509\_CERT\_DIR						|The location of certificates truststore. Optional variable. <br/>Default value: **/etc/grid-security/certificates**
|X509\_HOST\_CERT						|Host certificate location. <br/>Default value: **/etc/grid-security/hostcert.pem**
|X509\_HOST\_KEY						|Host certificate key location. Optional variable. <br/>Default value: **/etc/grid-security/hostkey.pem**
|CANL\_UPDATE\_INTERVAL					|Canl truststore update time interval expressed in milliseconds. Optional variable. Default value: **600000** (1 minute)

{% assign label_title="Table 6" %}
{% assign label_description="Specific StoRM GridHTTPs Variables." %}
{% include documentation/label.html %}

### Launching YAIM configuration <a name="launchyaim">&nbsp;</a>

After having built the **site-info.def** services file, you can configure the needed profile by using YAIM as follows:

```bash
$ /opt/glite/yaim/bin/yaim -c -d 6 -s <site-info.def> -n <profile>
```

But if in your StoRM deployment scenario more than a StoRM service has been installed on a single host you have to provide **a single site-info.def services file** containing **all** the required YAIM variables. Then you can configure all service profiles at once with a single YAIM call:

```bash
$ /opt/glite/yaim/bin/yaim -c -d 6 -s <site-info.def> -n <node_type_1> -n <node_type_2> -n <node_type_3>
```

where for example *node\_type\_1* is *se\_storm\_backend*, *node\_type\_2* is *se\_storm\_frontend* and *node\_type\_3* is *se\_storm\_gridftp*.

> NOTE: if you are configuring on the same host profiles *se\_storm\_backend* and *se\_storm\_frontend*, you have to specify those profiles in this order to YAIM. This is also the case of profiles *se\_storm\_backend* and *se\_storm\_gridhttps*.

In case of a distributed deployment, on every host that run almost one of the StoRM components, you have to run yaim specifying only the profiles of the installed components.

To verify StoRM services launch:

```bash
$ service storm-backend-server status
$ service storm-frontend-server status
$ service storm-globus-gridftp status
$ service storm-gridhttps-server status
```

## Advanced Configuration <a name="advconf">&nbsp;</a>

Please note that most of the configuration parameters of StoRM can be automatically managed directly by YAIM. This means that for standard installation in WLCG site without special requirement is not needed a manual editing of StoRM configuration file, but only a proper tuning of StoRM YAIM variables. On the other hand, with this guide we would like to give to site administrators the opportunity to learn about StoRM details and internal behaviours, in order to allow advanced configuration and ad-hoc set up, to optimize performance and results.

### Frontend Advanced Configuration <a name="fe_advconf">&nbsp;</a>

The Frontend component relies on a single configuration file that contains all the configurable parameters. This file is:
	
	/etc/storm/frontend-server/storm-frontend-server.conf 
	
containing a list of:
	
	key = value

pairs that can be used to configure the Frontend server. In case a parameter is modified, the Frontend service has to be restarted in order to read the new value.

#### Frontend service information: storm-frontend-server.conf <a name="fesi_advconf">&nbsp;</a>

> **_Database settings_**

|	Property Name	|	Description		|
|:------------------|:------------------|
| db.host	| Host for database connection. Default is **localhost**	|
| db.user	| User for database connection. Default is **storm**		|
| db.passwd | Password for database connection. Default is **password**	|

<br/>
> **_Frontend service settings_**

|	Property Name	|	Description		|
|:------------------|:------------------|
|	fe.port							|	Frontend port. Default is **8444**	
|	fe.threadpool.threads.number	|	Size of the worker thread pool. Default is **50**	
|	fe.threadpool.maxpending		|	Size of the internal queue used to maintain SRM tasks in case there are no free worker threads. Default is **200**
|	fe.gsoap.maxpending				|	Size of the GSOAP queue used to maintain pending SRM requests. Default is **2000**

<br/>
> **_Log settings_**

|	Property Name	|	Description		|
|:------------------|:------------------|
|	log.filename	|	Log file name, complete whit path.<br/>Default is **/var/log/storm/storm-frontend.log**
|	log.debuglevel 	|	Loggin level. Possible value are: ERROR, WARN, INFO, DEBUG, DEBUG2. Default is **INFO**

<br/>
> **_Monitoring settings_**

|	Property Name	|	Description		|
|:------------------|:------------------|
|	monitoring.enabled		|	Flag to enable/disable SRM requests Monitoring. Default is **true**
|	monitoring.timeInterval	|	Time intervall in seconds between each Monitoring round. <br/>Default is **60**
|	monitoring.detailed		|	Flag to enable/disable detailed SRM requests Monitoring. <br/>Default is **false**

<br/>
> **_XML-RPC communication settings_**

|	Property Name	|	Description		|
|:------------------|:------------------|
|	be.xmlrpc.host			|	Backend hostname. Default is **localhost**
|	be.xmlrpc.port			|	XML-RPC server port running on the Backend machine.<br/>Default is **8080**
|	be.xmlrpc.token	|	Token used for communicating with the backend service. Mandatory, has no default
|	be.xmlrpc.path			|	XML-RPC server path. Default is **/RPC2**
|	be.xmlrpc.check.ascii	|	Flag to enable/disable ASCII checking on strings to be sent via XML-RPC. Default is **true**

<br/>
> **_REST communication settings_**

|	Property Name	|	Description		|
|:------------------|:------------------|
|	be.recalltable.port		|	REST server port running on the Backend machine. Default is **9998**

<br/>
> **_Blacklisting settings_**

|	Property Name	|	Description		|
|:------------------|:------------------|
|	check.user.blacklisting	|	Flag to enable/disable user blacklisting. Default is **false**
|	argus-pepd-endpoint		|	The complete service endpoint of Argus PEP server. Mandatory if check.user.blacklisting is true. <br/>Example: _https://host.domain:8154/authz_

<br/>
> **_Proxy settings_**

|	Property Name	|	Description		|
|:------------------|:------------------|
|	proxy.dir		|	Directory used by the Frontend to save proxies files in case of requests with delegation. Default is **/var/tmp/storm/proxy**
|	proxy.user		|	Local user owner of proxies files. This have to be the same local user running the backend service. **Mandatory**.
|	security.enable.vomscheck	|	Flag to enable/disable checking proxy VOMS credentials. Default is **true**.
|	security.enable.mapping		|	Flag to enable/disable DN->userid mapping via gridmap-file. Default is **false**

<br/>
> **_General settings_**

|	Property Name	|	Description		|
|:------------------|:------------------|
|	wsdl.file		|	WSDL file, complete with path, to be returned in case of GET request

#### Logging files and logging level <a name="loggingfe_advconf">&nbsp;</a>

The Frontend logs information on the service status and the SRM requests received and managed by the process. The Frontend's log supports different level of logging (ERROR, WARNING, INFO, DEBUG, DEBUG2) that can be set from the dedicated parameter in _storm-frontend-server.conf_ configuration file.
The Frontend log file named _storm-frontend-server.log_ is placed in the _/var/log/storm directory_. At start-up time, the FE prints here the whole set of configuration parameters, this can be useful to check desired values. When a new SRM request is managed, the FE logs information about the user (DN and FQANs) and the requested parameters. 
At each SRM request, the FE logs also this important information:

	03/19 11:51:42 0x88d4ab8 main: AUDIT - Active tasks: 3
	03/19 11:51:42 0x88d4ab8 main: AUDIT - Pending tasks: 0

about the status of the worker pool threads and the pending process queue. _Active tasks_ is the number of worker threads actually running. _Pending tasks_ is the number of SRM requests queued in the worker pool queue. These data gives important information about the Frontend load.

##### Monitoring

Monitoring service, if enabled, provides information about the operations executed in a certain amount of time writing them on file _/var/log/storm/monitoring.log_. This amount of time (called Monitoring Round) is configurable via the configuration property monitoring.timeInterval; its default value is 1 minute.
At each Monitoring Round, a single row is printed on log. This row reports both information about requests that have been performed in the last Monitoring Round and information considering the whole FE execution time (Aggregate Monitoring). Informations reported are generated from both Synchronous and Asynchronous requests and tell the user:

- how many requests have been performed in the last Monitoring Round,
- how many of them were successfull,
- how many failed,
- how many produced an error,
- the average execution time,
- the minimum execution time, 
- the maximum execution time.

This row reports the **Monitoring Summary** and this is the default behaviour of the monitoring service.

**_Example_**:

	03/20 14:19:11 : [# 22927 lifetime=95:33:18] 
				S [OK:47,F:15,E:0,m:0.085,M:3.623,Avg:0.201] 
				A [OK:16,F:0,E:0,m:0.082,M:0.415,Avg:0.136] 
				Last:(S [OK:12,F:5,E:0,m:0.091,M:0.255] 
				A [OK:6,F:0,E:0,m:0.121,M:0.415])
	
Furthermore it can be requested a more detailed Frontend Monitoring activity by setting the configuration property _monitoring.detailed_ to _true_. Doing this, at each Monitoring Round for each kind of srm operation performed in the Monitoring Round (srmls, srmPtp, srmRm, ...) the following information are printed in a section with header "Last round details:":

- how many request succeded,
- how many failed,
- how many produced an error,
- the average execution time,
- the minimum execution time,
- the maximum execution time,
- the execution time standard deviation.

This is called the **Detailed Monitoring Round**. After this, the Monitoring Summary is printed. Then, considering the whole frontend execution time, in a section with header "Details:", a similar detailed summary is printed. This is called the **Aggregate Detailed Monitoring**.

**_Example_**:

	03/20 14:19:11 : Last round details:
	03/20 14:19:11 : [PTP] [OK:3,F:0,E:0,Avg:0.203,Std Dev:0.026,m:0.183,M:0.240]
	03/20 14:19:11 : [Put done] [OK:2,F:0,E:0,Avg:0.155,Std Dev:0.018,m:0.136,M:0.173]
	03/20 14:19:11 : [# 22927 lifetime=95:33:18] 
				S [OK:47,F:15,E:0,m:0.085,M:3.623,Avg:0.201] 
				A [OK:16,F:0,E:0,m:0.082,M:0.415,Avg:0.136]  
				Last:(S [OK:12,F:5,E:0,m:0.091,M:0.255] 
				A [OK:6,F:0,E:0,m:0.121,M:0.415])
	03/20 14:19:11 : Details:
	03/20 14:19:11 : [PTP] [OK:7,F:0,E:0,Avg:0.141,Std Dev:0.057,m:0.085,M:0.240]
	03/20 14:19:11 : [Put done] [OK:5,F:0,E:0,Avg:0.152,Std Dev:0.027,m:0.110,M:0.185]
	03/20 14:19:11 : [Release files] [OK:4,F:0,E:0,Avg:0.154,Std Dev:0.044,m:0.111,M:0.216]
	03/20 14:19:11 : [Rm] [OK:3,F:0,E:0,Avg:0.116,Std Dev:0.004,m:0.111,M:0.122]

**Note**:

- Operations not performed in current Monitoring Round are not printed in Detailed Monitoring Round.
- Operations never performed are not printed in Aggregate Detailed Monitoring.
- Operation performed in current Monitoring Round are aggregated in Aggregate Detailed Monitoring.

##### gSOAP tracefile

If you have problem at gSOAP level, and you have already looked at the troubleshooting section of the StoRM site without finding a solution, and you are brave enough, you could try to find some useful information on the gSOAP log file.
To enable gSOAP logging, set the following environment variables :

	$CGSI_TRACE=1
	$CGSI_TRACEFILE=/tmp/tracefile

and restart the Frontend daemon by calling directly the init script /etc/init.d/storm-frontend-server and see if the error messages contained in /tmp/tracefile could help. Please be very careful, it prints really a huge amount of information.

### Backend Advanced Configuration <a name="be_advconf">&nbsp;</a>

The Backend is the core of StoRM. It executes all SRM requests, interacts with other Grid service, with database to retrieve SRM requests, with file-system to set up space and file, etc. It has a modular architecture made by several internal components. The Backend needs to be configured for two main aspects:

- _Service information_: this section contains all the parameter regarding the StoRM service details. It relies on the **storm.properties** configuration file.
- _Storage information_: this section contains all the information regarding Storage Area and other storage details. It relies on the **namespace.xml** file.

### Backend Service Information: storm.properties <a name="besi_advconf">&nbsp;</a>

The file:

	/etc/storm/backend-server/storm.properties

contains a list of:

	key = value

pairs that represent all the information needed to configure the StoRM Backend service. The most important (and mandatory) parameters are configured by default trough YAIM with a standard installation of StoRM. All the other parameters are optionals and can be used to make advanced tuning of the Backend.
To change/set a new value, or add a new parameter, just edit the *storm.properties* file and restart the Backend daemon. When the BackeEnd starts, it writes into the log file the whole set of parameters read from the configuration file.

> **_Service information_**

|	Property Name						|	Description		|
|:--------------------------------------|:------------------|
|	storm.service.SURL.endpoint			|	List of comma separated strings identifying the StoRM Frontend endpoint(s). This is used by StoRM to understand if a SURL is local. E.g. *srm://storm.cnaf.infn.it:8444/srm/managerv2*
|	storm.service.port					|	SRM service port. Default: **8444**
|	storm.service.SURL.default-ports	|	List of comma separated valid SURL port numbers. Default: **8444**
|	storm.service.FE-public.hostname	|	StoRM Frontend hostname in case of a single Frontend StoRM deployment, StoRM Frontends DNS alias in case of a multiple Frontends StoRM deployment.
|	storm.service.FE-list.hostnames		|	Comma separated list os Frontend(s) hostname(s). Default: **localhost**
|	storm.service.FE-list.IPs			|	Comma separated list os Frontend(s) IP(s). E.g. *131.154.5.127, 131.154.5.128*. Default: **127.0.0.1**
|	proxy.home							|	Directory used to contains delegated proxies used in case of *srmCopy* request. Please note that in case of clustered installation this directory have to be shared between the Backend and the Frontend(s) machines. Default: **/etc/storm/tmp**
|	pinLifetime.default					|	Default *PinLifetime* in seconds used for pinning files in case of *srmPrepareToPut* or *srmPrepareToGet* operation without any pinLifetime specified. Default: **259200**
|	pinLifetime.maximum					|	Maximum *PinLifetime* allowed in seconds.<br/>Default: **1814400**
|	SRM22Client.PinLifeTime				|	Default *PinLifeTime* in seconds used by StoRM in case of *SrmCopy* operation. This value is the one specified in the remote *SrmPrepareToGet* request. Default: **259200**
|	fileLifetime.default				|	Default *FileLifetime* in seconds used for VOLATILE file in case of SRM request without *FileLifetime* parameter specified. Default: **3600**
|	extraslashes.gsiftp					|	Add extra slashes after the "authority" part of a TURL for gsiftp protocol. 
|	extraslashes.rfio					|	Add extra slashes after the "authority" part of a TURL for rfio protocol.
|	extraslashes.root					|	Add extra slashes after the "authority" part of a TURL for root protocol.
|	extraslashes.file					|	Add extra slashes after the "authority" part of a TURL for file protocol.
|	checksum.enabled					|	Flag to enable or not the support of *Adler32* checksum computation. Default: **false**
|	synchcall.directoryManager.maxLsEntry|	Maximum number of entries returned by an *srmLs* call. Since in case of recursive *srmLs* results can be in order of million, this prevent a server overload. Default: **500**
|	directory.automatic-creation		|	Flag to enable authomatic missing directory creation upon *srmPrepareToPut* requests.<br/>Default: **false**
|	directory.writeperm					|	Flag to enable directory write permission setting upon *srmMkDir* requests on created dyrectories. Default: **false**
|	default.overwrite					|	Default file overwrite mode to use upon *srmPrepareToPut* and *srmCopy* requests. Default: **A**. Possible values are: N, A, D. Please note that N stands for *Never*, A stands for *Always* and D stands for *When files differs*.
|	default.storagetype					|	Default File Storage Type to be used for *srmPrepareToPut* and *srmCopy* requests in case is not provided in the request. Default: **V**. Possible values are: V, P, D. Please note that V stands for *Volatile*, P stands for *Permanent* and D stands for *Durable*.

<br/>
> **_Requests garbage collector_**

|	Property Name	|	Description		|
|:------------------|:------------------|
|	purging			|	Flag to enable the purging of expired requests. This garbage collector process cleans all database tables and proxies from the expired SRM requests. An appropriate tuning is needed in case of high throughput of SRM requests required for long time. Default: **true**. Possible values are: true, false.
|	purge.interval	|	Time interval in seconds between successive purging run. Default: **600**.
|	purge.size		|	Number of requests picked up for cleaning from the requests garbage collector at each run. This value is use also by Tape Recall Garbage Collector. Default: **800**
|	purge.delay		|	Initial delay before starting the requests garbage collection process, in seconds. Default: **10**
|	expired.request.time|	Time in seconds to consider a request expired after its submission. Default: **604800**

<br/>
> **_Garbage collector_**

|	Property Name	|	Description		|
|:------------------|:------------------|
|	gc.pinnedfiles.cleaning.delay	|	Initial delay before starting the reserved space, JIT ACLs and pinned files garbage collection process, in seconds. Default: **10**
|	gc.pinnedfiles.cleaning.interval|	Time interval in seconds between successive purging run. Default: **300**

<br/>
> **_Synchronous call_**

|	Property Name	|	Description		|
|:------------------|:------------------|
|	synchcall.xmlrpc.unsecureServerPort	|	Port to listen on for incoming XML-RPC connections from Frontends(s). Default: **8080**
|	synchcall.xmlrpc.maxthread			|	Number of threads managing XML-RPC connection from Frontends(s). A well sized value for this parameter have to be at least equal to the sum of the number of working threads in all FrontEend(s). Default: **100**
|	synchcall.xmlrpc.token.enabled	|	Whether the backend will require a token to be present for accpeting XML-RPC requests. Default: true
|	synchcall.xmlrpc.token	|	The token that the backend will require to be present for accepting XML-RPC requests. Mandatory if synchcall.xmlrpc.token.enabled is true


<br/>
> **_REST interface parameters_**

|	Property Name	|	Description		|
|:------------------|:------------------|
|	storm.rest.services.port	|	REST services port. Default: **9998**

<br/>
> **_Database connection parameters_**

|	Property Name	|	Description		|
|:------------------|:------------------|
|	com.mysql.jdbc.Driver	|	JDBC driver to be used to connect with StoRM database. Default: **com.mysql.jdbc.Driver**
|	storm.service.request-db.protocol	|	Protocol to be used to connect with StoRM database. Default: **jdbc:mysql://**
|	storm.service.request-db.host		|	Host for StoRM database. Default: **localhost**
|	storm.service.request-db.db-name	|	Database name for SRM requests. Default: **storm_db**
|	storm.service.request-db.username	|	Username for database connection. Default: **storm**
|	storm.service.request-db.passwd		|	Password for database connection
|	asynch.db.ReconnectPeriod			|	Database connection refresh time intervall in seconds. Default: **18000**
|	asynch.db.DelayPeriod				|	Database connection refresh initial delay in seconds. Default: **30**
|	persistence.internal-db.connection-pool	|	Enable the database connection pool. Default: **false**
|	persistence.internal-db.connection-pool.maxActive |	Database connection pool max active connections. Default: **10**
|	persistence.internal-db.connection-pool.maxWait		|	Database connection pool max wait time to provide a connection. Default: **50**

<br/>
> **_SRM Requests Picker_**

|	Property Name	|	Description		|
|:------------------|:------------------|
|	asynch.PickingInitialDelay	|	Initial delay before starting to pick requests from the DB, in seconds. Default: **1**
|	asynch.PickingTimeInterval	|	Polling interval in seconds to pick up new SRM requests. Default: **2**
|	asynch.PickingMaxBatchSize	|	Maximum number of requests picked up at each polling time. Default: **100**
|	scheduler.serial			|	**DEPRECATED** Flag to enable the execution of all the request on a single thread. Default: **false**

<br/>
> **_Worker threads_**

|	Property Name	|	Description		|
|:------------------|:------------------|
|	scheduler.crusher.workerCorePoolSize	|	Crusher Scheduler worker pool base size. Default: **10**
|	scheduler.crusher.workerMaxPoolSize		|	Crusher Schedule worker pool max size. Default: **50**
|	scheduler.crusher.queueSize				|	Request queue maximum size.<br/>Default: **2000**
|	scheduler.chunksched.ptg.workerCorePoolSize	|	*PrepareToGet* worker pool base size. Default: **50**
|	scheduler.chunksched.ptg.workerMaxPoolSize	|	*PrepareToGet* worker pool max size. Default: **200**
|	scheduler.chunksched.ptg.queueSize			|	*PrepareToGet* request queue maximum size. Default: **2000**
|	scheduler.chunksched.ptp.workerCorePoolSize	|	*PrepareToPut* worker pool base size. Default: **50**
|	scheduler.chunksched.ptp.workerMaxPoolSize	|	*PrepareToPut* worker pool max size. Default: **200**
|	scheduler.chunksched.ptp.queueSize			|	*PrepareToPut* request queue maximum size. Default: **1000**
|	scheduler.chunksched.bol.workerCorePoolSize	|	*BringOnline* worker pool base size. Default: **50**
|	scheduler.chunksched.bol.workerMaxPoolSize	|	*BringOnline* Worker pool max size. Default: **200**
|	scheduler.chunksched.bol.queueSize			|	*BringOnline* request queue maximum size. Default: **2000**
|	scheduler.chunksched.copy.workerCorePoolSize|	*Copy* worker pool base size. Default: **10**
|	scheduler.chunksched.copy.workerMaxPoolSize	|	*Copy* worker pool max size. Default: **50**
|	scheduler.chunksched.copy.queueSize			|	*Copy* request queue maximum size. Default: **500**

<br/>
> **_HTTP(S) protocol_**

|	Property Name	|	Description		|
|:------------------|:------------------|
|	gridhttps.enabled		|	Flag to enable the support to HTTP and HTTPS protocols. Default: **false**
|	gridhttps.server.host	|	The complete hostname of the host running StoRM GridHTTPs. Default: **localhost**
|	gridhttps.server.port	|	The port on StoRM GridHTTPs host where GridHTTPs accepts HTTP connections. Default:**8088**
|	gridhttps.plugin.classname	|	The complete class-name of the HTTPSPluginInterface implementation to be used. Default: **it.grid.storm.https.HTTPSPluginInterfaceStub**

<br/>
> **_Protocol balancing_**

|	Property Name	|	Description		|
|:------------------|:------------------|
|	gridftp-pool.status-check.timeout	|	Time in milliseconds after which the status of a GridFTP has to be verified. Default: **20000** (20 secs)

<br/>
> **_Tape recall_**

|	Property Name	|	Description		|
|:------------------|:------------------|
|	tape.support.enabled	|	Flag to enable tape support. Default: **false**
|	tape.buffer.group.read	|	System group to be assigned to files migrated from tape storage. Default: **storm-SA-read**
|	tape.buffer.group.write	|	System group to be assigned to files migrated to tape storage. Default: **storm-SA-write**

<br/>
> **_srmCopy parameters_**

|	Property Name	|	Description		|
|:------------------|:------------------|
|	asynch.srmclient.retrytime			|	Timeout for a single *srmPrepareToPut* request execution performed to fulfill *srmCopy* requests in seconds. Default: **60**
|	asynch.srmclient.sleeptime			|	Interval between successive *srmPrepareToPut* request status polling performed to fulfill *srmCopy* requests in seconds. Default: **5**
|	asynch.srmclient.timeout			|	Timeout for *srmPrepareToPut* request execution performed to fulfill *srmCopy* requests in seconds. Default: **180**
|	asynch.srmclient.putdone.sleeptime	|	Interval between consecutive *srmPutDone* attempts performed to fulfill *srmCopy* requests in seconds. Default: **1**
|	asynch.srmclient.putdone.timeout	|	Timeout for *srmPutDone* request execution performed to fulfill *srmCopy* requests in seconds. Default: **60**
|	asynch.srmclient					|	The complete class-name of the *SRMClient* implementation providing SRM client features to be used to perform srm operations to fulfill *srmCopy* requests. Default: **it.grid.storm.asynch.SRM22Client**
|	asynch.srmcopy.gridftp.timeout		|	Timeout for GridFTP connection establishment during file transfer execution performed to fulfill *srmCopy* requests in seconds. Default: **15000**
|	asynch.gridftpclient				|	The complete class-name of the GridFTPTransfer-Client implementation providing GridFTP client features to be used to perform file transfer to fulfill *srmCopy* requests. Default: **it.grid.storm.asynch.NaiveGridFTPTransferClient**

### Backend Storage Information: namespace.xml <a name="besti_advconf">&nbsp;</a>

Information about storage managed by StoRM is stored in a configuration file named namespace.xml located at */etc/storm/backend-server/* on StoRM Backend host. One of the information stored into namespace.xml file is what is needed to perform the ***mapping functionality***.
The *mapping functionality* is the process of retrieving or building the transport URL (TURL) of a file addressed by a Site URL (SURL) together with grid user credential. The Fig 3 shows the different schema of SURL and TURL. 

{% assign image_src="surl-turl-schema.png" %}
{% assign image_width="100%" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 3" %}
{% assign label_description="Site URL and Transfer URL schema." %}
{% include documentation/label.html %}

A couple of quick concepts from SRM:

- The SURL is the logical identifier for a local data entity
- Data access and data transfer are made through the TURLs
- The TURL identify a physical location of a replica
- SRM services retrieve the TURL from a namespace database (like DPNS component in DPM) or build it through other mechanisms (like StoRM)

In StoRM, the mapping functionality is provided by the namespace component (NS).

- The Namespace component works without a database.
- The Namespace component is based on an XML configuration.
- It relies on the physical storage structure.

The basic features of the namespace component are:

- The configuration is modular and structured (representation is based on XML) 
- An efficient structure of namespace configuration lives in memory.
- No access to disk or database is performed
- The loading and the parsing of the configuration file occurs:
	* at start-up of the back-end service 
	* when configuration file is modified


StoRM is different from the other solution, where typically, for every SRM request a query to the data base have to be done in order to establish the physical location of file and build the correct transfer URL.
The namespace functions relies on two kind of parameters for mapping operations derived from the SRM requests, that are:

- the grid user credential (a subject or a service acting on behalf of the subject)
- the SURL

The Fig.4 shows the main concepts of Namespace Component:

- *NS-Filesystem*: is the representation of a Storage Area
- *Mapping rule*: represents the basic rule for the mapping functionalities
- *Approachable rule*: represents the coarse grain access control to the Storage Area.

{% assign image_src="namespace-structure.png" %}
{% assign image_width="80%" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 4" %}
{% assign label_description="Namespace structure." %}
{% include documentation/label.html %}

This is and example of the FS element:

	<filesystem name="dteam-FS" fs_type="ext3">
    	<space-token-description>DTEAM_TOKEN</space-token-description>
		<root>/storage/dteam</root>
		<filesystem-driver>
			it.grid.storm.filesystem.swig.posixfs
		</filesystem-driver>
		<spacesystem-driver>
			it.grid.storm.filesystem.MockSpaceSystem
		</spacesystem-driver>
		<storage-area-authz>
			<authz-db>DTEAM_AUTH</authz-db>
		</storage-area-authz>
		<properties>
			<RetentionPolicy>replica</RetentionPolicy>
			<AccessLatency>online</AccessLatency>
			<ExpirationMode>neverExpire</ExpirationMode>
			<TotalOnlineSize unit="GB" limited-size="true">291</TotalOnlineSize>
			<TotalNearlineSize unit="GB">0</TotalNearlineSize>
      	</properties>
      	<capabilities>
			<aclMode>AoT</aclMode>
			<default-acl>
				<acl-entry>
					<groupName>lhcb</groupName>
					<permissions>RW</permissions>
				</acl-entry>
			</default-acl>
			<trans-prot>
				<prot name="file">
					<schema>file</schema>
				</prot>
				<prot name="gsiftp">
					<id>0</id>
					<schema>gsiftp</schema>
					<host>gsiftp-dteam-01.cnaf.infn.it</host>
					<port>2811</port>
				</prot>
				<prot name="gsiftp">
					<id>1</id>
					<schema>gsiftp</schema>
					<host>gsiftp-dteam-02.cnaf.infn.it</host>
					<port>2811</port>
				</prot>
				<prot name="rfio">
					<schema>rfio</schema>
					<host>rfio-dteam.cnaf.infn.it</host>
					<port>5001</port>
				</prot>
				<prot name="root">
					<schema>root</schema>
					<host>root-dteam.cnaf.infn.it</host>
					<port>1094</port>
				</prot>
			</trans-prot>
			<pool>
				<balance-strategy>round-robin</balance-strategy>
				<members>
					<member member-id="0"></member>
					<member member-id="1"></member>
				</members>
			</pool>
		</capabilities>
		<defaults-values>
			<space lifetime="86400" type="volatile" guarsize="291" 
				totalsize="291"/>
			<file lifetime="3600" type="volatile"/>
		</defaults-values>
	</filesystem>

***Attributes meaning***:

- ```<filesystem name="dteam-FS" fs_type="ext3">``` : The name is the element identifier. It identifies this Storage Area in the namespace domains. The *fs\_type* is the type of the filesystem the Storage Area is built on. Possible values are: *ext3*, *gpfs*. Please note that *ext3* stands for all generic POSIX filesystem (*ext3*, *Lustre*, etc.)
- ```<space-token-description>DTEAM_TOKEN</space-token-description>``` : Storage Area space token description.
- ```<root>/storage/dteam</root>``` : Physical root directory of the Storage Area on the file system.
- ```<filesystem-driver>it.grid.storm.filesystem.swig.posixfs</filesystem-driver>``` : Driver loaded by the Backend for filesystem interaction. This driver is used mainly to set up ACLs on space and files.
- ```<spacesystem-driver>it.grid.storm.filesystem.MockSpaceSystem</spacesystem-driver>``` Driver loaded by the Backend for filesystem interaction. This is driver is used to manage space allocation. (E.g. on GPFS it uses the _gpfs\_prealloc()_ call).

> ***Storage Area properties***

	<properties>
		<RetentionPolicy>replica</RetentionPolicy>
	    <AccessLatency>online</AccessLatency>
	    <ExpirationMode>neverExpire</ExpirationMode>
	    <TotalOnlineSize unit="GB" limited-size="true">291</TotalOnlineSize>
	    <TotalNearlineSize unit="GB">0</TotalNearlineSize>
	</properties>
	
in details:

- ```<RetentionPolicy>replica</RetentionPolicy>``` : Retention Policy of the Storage Area. Possible values are: *replica*, *custodial*.
- ```<AccessLatency>online</AccessLatency>``` : Access Latency of the Storage Area. Possible values: *online*, *nearline*.
- ```<ExpirationMode>neverExpire</ExpirationMode>``` : Expiration Mode of the Storage Area. **Deprecated**.
- ```<TotalOnlineSize unit="GB" limited-size="true">291</TotalOnlineSize>``` Total on-line size of the Storage Area in GigaBytes. In case the attribute *limited-size*="true", StoRM enforce this limit at SRM level. When the space used for the Storage Area is at least equal to the size specified, every further SRM request to write files will fail with SRM\_NO\_FREE\_SPACE error code.
- ```<TotalNearlineSize unit="GB">0</TotalNearlineSize>``` : Total near-line size of the Storage Area. This only means in case the Storage Area is in some way attached to a MSS storage system (such as TSM with GPFS).

> ***Storage area capabilities***: 

	<aclMode>AoT</aclMode>

This is the ACL enforcing approach. Possible values are: *AoT*, *JiT*. In case of *AheadOfTime*(**AoT**) approach StoRM sets up a physical ACL on file and directories for the local group (*gid*) in which the user is mapped. (The mapping is done querying the LCMAPS service con the BE machine passing both user DN and FQANs). The group ACL remains for the whole lifetime of the file. In case of *JustInTime*(**JiT**) approach StoRM sets up and ACL for the local user (*uid*) the user is mapped. The ACL remains in place only for the lifetime of the SRM request, then StoRM removes it. (This is to avoid to grant access to pool account uid in case of reallocation on different users.)

	<default-acl>
		<acl-entry>
			<groupName>lhcb</groupName>
			<permissions>RW</permissions>
		</acl-entry>
	</default-acl>

This is the Default ACL list. A list of ACL entry (that specify a local user (*uid*) or group id (*gid*) and a permission (R,W,RW). This ACL are automatically by StoRM at each read or write request. Useful for use cases where experiment want to allow local access to file on group different than the one that made the SRM request operation.

> **_Access and Transfer protocol supported_**

The ```file``` protocol:
       
	<prot name="file">
		<schema>file</schema>
	</prot>

The **file** protocol means the capability to perform local access on file and directory. If user performs an SRM request (*srmPtG* or *srmPtP*) specifying the file protocol, and it is supported by the selected Storage Area, StoRM return a TURL structured as:
	
	file:///atlas/atlasmcdisk/filename
	
This TURL can be used through GFAL or other SRM clients to perform a direct access on the file.

	<prot name="gsiftp">
		<id>0</id>
		<schema>gsiftp</schema>
		<host>gridftp-dteam.cnaf.infn.it</host>
		<port>2811</port>
	</prot>

The ```gsiftp``` protocol:

The **gsiftp** protocol means the GridFTP transfer system from Globus widely adopted in many Grid environments. This capability element contains all the information about the GridFTP server to use with this Storage Area. Site administrator can decide to have different server (or pools of server) for different Storage Areas. The *id* is the server identifier to be used when defining a pool. The *schema* have to be gsiftp. *host* is the hostname of the server (or the DNS alias used to aggregate more than one server). The *port* is the GridFTP server port, typically 2811. If user performs an SRM request (*srmPtG* or *srmPtP*) specifying the *gsiftp* protocol, and it is supported by the selected Storage Area, StoRM return a TURL structured as: 

	gsiftp://gridftp-dteam.cnaf.infn.it:2811/atlas/atlasmcdisk/filename.

The ```rfio``` protocol:

	<prot name="rfio">
		<schema>rfio</schema>
		<host>rfio-dteam.cnaf.infn.it</host>
		<port>5001</port>
	</prot>
	
This capability element contains all the information about the **rfio** server to use with this Storage Area. Like for GridFTP, site administrator can decide to have different server (or pools of server) for different Storage Areas. The *id* is the server identifier. The *schema* have to be rfio. *host* is the hostname of the server (or the DNS alias used to aggregate more than one server). The *port* is the rfio server port, typically 2811. If user performs an SRM request (*srmPtG* or *srmPtP*) specifying the rfio protocol, and it is supported by the selected Storage Area, StoRM return a TURL structured as: 

	rfio://rfio-dteam.cnaf.infn.it:5001/atlas/atlasmcdisk/filename.

The ```root``` protocol:

	<prot name="root">
		<schema>root</schema>
		<host>root-dteam.cnaf.infn.it</host>
		<port>1094</port>
	</prot>

This capability element contains all the information about the **root** server to use with this Storage Area. Like for other protocols, site administrator can decide to have different server (or pools of server) for different Storage Areas. The *id* is the server identifier. The *schema* have to be root. *host* is the hostname of the server (or the DNS alias used to aggregate more than one server). The *port* is the root server port, typically 1094. If user performs an SRM request (*srmPtG* or *srmPtP*) specifying the root protocol, and it is supported by the selected Storage Area, StoRM return a TURL structured as: 

	root://root-dteam.cnaf.infn.it:1094/atlas/atlasmcdisk/filename.

> ***Pool of protocol servers***

	<pool>
		<balance-strategy>round-robin</balance-strategy>
		<members>
			<member member-id="0"></member>
			<member member-id="1"></member>
		</members>
	</pool>

Here is defined a *pool of protocol servers*. Within the pool element pool *members* are declared identified by their *id*, the list of members have to be homogenious with respect to their schema. This id is the server identifier specified in the *prot* element. The *balance-strategy* represent the load balancing strategy with which the pool has to be managed. Possible values are: *round-robin*, *smart-rr*, *random* and *weight*.
<br/>
**NOTE**: Protocol server pooling is currently available only for gsiftp servers.
<br/>
Load balancing strategies details:

* *round-robin* At each TURL construction request the strategy returns the next server following the round-robin approach: a circular list with an index starting from the head and incrementd at each request.

* *smart-rr* An enhanced version of *round-robin*. The status of pool members is monitored and maintained in a cache. Cache entries has a validity life time that is refreshed when expired. If the member chosen by *round-robin* is marked as not responsive another iteration of *round-robin* is performed.

* *random* At each TURL construction request the strategy returns a random member of the pool.

* *weight* An enhanced version of *round-robin*. When a server is chosen the list index will not be moved forward (and the server will be choosen again in next request) for as many times as specified in its *weight*.

**NOTE**: The weight has to be specified in a *weight* element inside the member element:

	<pool>
		<balance-strategy>WEIGHT</balance-strategy>
		<members>
			<member member-id="0">
				<weight>5</weight>
    		</member>
    		<member member-id="1">
      			<weight>1</weight>
    		</member>
  		</members>
	</pool>
	
> ***Default values***

	<defaults-values>
		<space lifetime="86400" type="volatile" guarsize="291" totalsize="291"/>
		<file lifetime="3600" type="volatile"/>
	</defaults-values>

> ***Mapping rules***

A **mapping rule** define how a certain NS-Filesystem, that correspond to a Storage Area in SRM meaning of terms, is exposed in Grid:

	<mapping-rules>
		<map-rule name="dteam-maprule">
			<stfn-root>/dteam</stfn-root>
			<mapped-fs>dteam-FS</mapped-fs>
		</map-rule>
	</mapping-rules>

The ```<stfn-root>``` is the path used to build SURL referring to that Storage Area. The mapping rule above define that the NS-Filesystem named *dteam-FS* has to be mapped in the */dteam* SURL path. Following the NS-Filesystem element defined in the previous section, the SURL: 

	srm://storm-fe.cr.cnaf.infn.it:8444/dteam/testfile
	
following the root expressed in the *dteam-FS* NF-Filesystem element, is mapped in the physical root path on the file system: 

	/storage/dteam
	
This approach works similar to an alias, from the SURL *stfn-root* path to the NS-Filesystem root.

> ***Approachable rules***

**Approachable rules** defines which users (or which class of users) can approach a certain Storage Area, always expressed as NS-Filesystem element. If a user can approach a Storage Area, he can use it for all SRM operations. If a user is not allowed to approach a Storage Area, and he try to specify it in any SRM request, he will receive an SRM\_INVALID\_PATH. In practics, if a user cannot approach a Storage Area, for him that specific path does not exists at all.
Here is an example of approachable rule for the *dteam-FS* element:

	<approachable-rules>
		<app-rule name="dteam-rule">
			<subjects>
				<dn>*</dn>
				<vo-name>dteam</vo-name>
			</subjects>
			<approachable-fs>dteam-FS</approachable-fs>
			<space-rel-path>/</space-rel-path>
		</app-rule>
	</approachable-rules>

- ```<dn>*</dn>``` means that everybody can access the storage Area. Here you can define regular expression on DN fields to define more complex approachable rules.

- ```<vo-name>*</vo-name>``` means that everybody belonging to a VO access the storage Area. Please note that user without FQANs extension will not be recognized as belonging to VOs then they will not be allowed to approach the SA. Remove or comment this line if the Storage Area have to be open to users without VOMS extensions.

- ```<vo-name>dteam</vo-name>``` means that only users belonging to the VO dteam will be allowed to access the Storage Area. This entry can be a list of comma separeted VO-name.

### Backend Storage Usage Initialization: used-space.ini <a name="besui_advconf">&nbsp;</a>

StoRM maintains the information about the status of managed storage areas (such as free, used, busy, available, guaranteed and reserved space), and store them into the DB. Whenever it is consumed or released some storage space by creating or deleting files, the status is updated and stored in the DB. The storage space status stored into the DB is authorative. The information about the Storage Space stored into the DB are used also as information source for the Information Provider through the DIP (Dynamic Info Provider). There are cases in which the status of a storage area must be initialized, for example in the case of a fresh StoRM installation configured to manage a storage space already populated with files, where the space used is not zero.
There are different methods for initialize the Storage Area status, some executed within StoRM (GPFS quota and/or background-DU). In this section it is described how an administrator can initialize the status of a Storage Area by editing a configuration file, the used-space.ini configuration file, that it will be parsed at bootstrap time and only one time.
The structure of the content of **used-space.ini** is quite simple: a list of sections corresponding to the Storage Area in which are defined the used size, and eventually, the checktime.
For each Storage Area to be initializated there is a section named with the same alias *space-token-description* defined in the *namespace.xml*, that are defined with YAIM variables STORM\{SA}\_ACCESSPOINT. Within the section there are two properties: *usedsize* and *checktime*:

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

> **NOTE**: running YAIM on StoRM Backend profile will produce a new used-space.ini file and backup any existent version with the extension .bkp_. Take this into account if you want to produce the used-space.ini file by hand.
 
### Backend logging: logging.xml <a name="belog_advconf">&nbsp;</a>

The Backend log files provide information on the execution process of all SRM requests. All the Backend log files are placed in the */var/log/storm* directory. Backend logging operations are based on the *logback* framework. Logback provides a way to set the level of verbosity depending on the use case. The level supported are FATAL, ERROR, INFO, WARN, DEBUG. The **/etc/storm/backend-server/logging.xml** contains this information:

	<logger name="it.grid.storm" additivity="false">
		<level value="DEBUG" />
        <appender-ref ref="PROCESS" />
	</logger>

the *value* can be setted to the desired log level. Please be careful that logging operation can impact on system performance (even 30% slower with DEBUG in the worst case). The suggest logging level for production endpoint is INFO. In case the log level is modified, the Backend have to be restarted to read the new value.


From [StoRM 1.11.2]({{ site.baseurl }}/release-notes/storm-backend-server/1.11.2/) the Backend log files have been unified in one and only file:

-  **storm-backend.log**. All the information about the SRM execution process, error or warning are logged here depending on the log level. At startup time, the BE logs here all the storm.properties value, this can be useful to check value effectively used by the system. After that, the BE logs the result of the namespace initialization, reporting errors or misconfiguration. At the INFO level, the BE logs for each SRM operation at least who have request the operation (DN and FQANs), on which files (SURLs) and the operation result. At DEBUG level, much more information are printed regarding the status of many StoRM internal component, depending on the SRM request type. DEBUG level has to be used carefully only for troubleshooting operation. If ERROR or FATAL level are used, the only event logged in the file are due to error condition.

StoRM provides a bookkeeping framework that elaborates informations on SRM requests processed by the system to provide user-friendly aggregated data that can be used to get a quick view on system health.

- **heartbeat.log**
This useful file contains information on the SRM requests process by the system from its startup, adding new information at each beat. The beat time interval can be configured, by default is 60 seconds. At each beat, the hearthbeat component logs an entry.

An hearthbeat.log entry example:

	[#.....71 lifetime=1:10.01]
		Heap Free:59123488 SYNCH [500] ASynch [PTG:2450 PTP:3422]
		Last:( [#PTG=10 OK=10 M.Dur.=150] [#PTP=5 OK=5 M.Dur.=300] )


|	Log		|	Meaning		|
|:----------|:--------------|
|```#......71```			|Log entry number
|```lifetime=1:10.01```		|Lifetime from last startup, hh:mm:ss
|```Heap Free:59123488```	|BE Process free heap size in Bytes
|```SYNCH [500]```			|Number of Synchronous SRM requests executed in the last beat
|```ASynch [PTG:2450 PTP:3422]```	|Number of *srmPrepareToGet* and *srmPrepareToPut* requests executed from start-up.
|```Last:( [#PTG=10 OK=10 M.Dur.=150]```	|Number of *srmPrepareToGet* executed in the last beat, with the number of request terminated with success (OK=10) and average time in millisecond (M.Dur.=150)
|```[#PTP=5 OK=5 M.Dur.=300]```		|Number of srmPrepareToPut executed in the last beat, with number of request terminated with success and average time in milliseconds.
			
This log information can be really useful to gain a global view on the overall system status. A tail on this file is the first thing to do if you want to check the health of your StoRM installation. From here you can understand if the system is receiving SRM requests or if the system is overloaded by SRM request or if PtG and PtP are running without problem or if the interaction with the filesystem is exceptionally low (in case the M.Dur. is much more than usual).

### Backend Space Authorization: authz.db <a name="besa_advconf">&nbsp;</a>

Space authorization component define access control policy on the Storage Area manged by StoRM. It allows to define rules as: *users* (expressed in terms of regular expression on FQANs or DN), *operation* (READ/WRITE/others) and *target Storage Area*. This rules are stored in a file named **authz.db** located at */etc/storm/backend-server/*.
<br/>
<br/>
The complete list of the operations is showed into the following table:

|	Operation name			|	Code	|	Description				|
|:--------------------------|:---------:|:--------------------------|
|	RELEASE\_SPACE			|	D		|	Release Space			|
|	UPDATE\_SPACE			|	U		|	Update Space			|
|	READ\_FROM\_SPACE		|	R		|	Read from space			|
|	WRITE\_TO\_SPACE		|	W		|	Write to space			|
|	STAGE\_TO\_SPACE		|	S		|	Stage in space			|
|	REPLICATE\_FROM\_SPACE	|	C		|	Replicate from space	|
|	PURGE\_FROM\_SPACE		|	P		|	Purge from space		|
|	QUERY\_SPACE			|	Q		|	Query space				|

Th *authz.db* file contains all the rule defining access policies for a Storage Area, and it is expressed in the *auth_db* element of the NS-Filesystem element referring to that Storage Area.

	ace.1=dn:/DC=ch/DC=cern/OU=Organic Units/OU=Users/CN=lmagnoni/CN=576235/CN=Luca Magnoni:DURWSCP:AL
	ace.2=dn:/O=GermanGrid/OU=DESY/CN=Tigran Mkrtchyan:S:ALLOW
	ace.3=fqan:EVERYONE:RQ:ALLOW
	ace.4=fqan:EVERYONE:S:DENY
	ace.5=fqan:dteam/Role=production:RSWQP:ALLOW
	ace.6=fqan:dteam/Role=lcgamin:DURWSPQM:ALLOW
	ace.7=fqan:dteam/Role=NULL:RSQ:ALLOW
	ace.8=fqan:EVERYONE:DURWSPQMC:DENY

The *evaluation algorithm* is taken from the NFS4 approach.

## GridFTP Advanced Configuration <a name="gftp_advconf">&nbsp;</a>

At each transfer request, the GridFTP uses LCMAPS to get user mapping and start a new processes on behalf of the user to proceed with data transfer. GridFTP relies on a different db file to get the plugin to use. Obviously LCMAPS has to answer to GridFTP requests and StoRM requests in coeherent way.
The GridFTP uses the LCMAPS configuration file located at */etc/lcmaps/lcmaps.db*.

### GridFTP logging files and logging level <a name="gftplog_advconf">&nbsp;</a>

GridFTP produce two separated log files:

- */var/log/storm/gridftp-session.log* for the command session information

- */var/log/storm/globus-gridftp.log* for the transfer logs

The logging level can be specified by editing the configuration file:

	/etc/globus-gridftp-server/gridftp.gfork

The supported logging levels are: ERROR, WARN, INFO, DUMP and ALL.

## GridHTTPs Advanced Configuration <a name="ghttp_advconf">&nbsp;</a>

The EMI3 GridHTTPs is the component responsible to provide:

- HTTP(s) file-transfer capabilities: it's possible to GET/PUT data via HTTP protocol but this is authorized only if a valid SRM prepare-to-get or SRM prepare-to-put has been successfully done before;
- a WebDAV interface to the StoRM endpoint that conceals the details of the SRM protocol and allows users to mount remote Grid storage as a volume on their own desktops;
- a mapping-service used by Backend that convert a real file path to a valid file-transfer URL.

The GridHTTPs component relies on a single configuration file that contains all the configurable parameters. This file is:

	/etc/storm/gridhttps-server/server.ini 

containing a list of:

	key = value

pairs that can be used to configure the GridHTTPs server. In case a parameter is modified, the GridHTTPs service has to be restarted in order to read the new value.

### GridHTTPs service information: server.ini <a name="ghttpsi_advconf">&nbsp;</a>

EMI3 StoRM GridHTTPs server no longer needs Tomcat, cause it is now a web component residing in an embedded Jetty server. About Jetty server and its connectors configuration you can manage the following variables:

|	Var. name			|	Description				|
|:----------------------|:--------------------------|
|http.enabled					|Flag to enable anonymous webdav and file-transfer connections. Available values: true, false. Default value: **true**
|http.port				|Gridhttps http port for anonymous webdav and file-transfer connections. Default value: **8085**
|https.port				|Gridhttps https port for secure webdav and file-transfer connections. Default value: **8443**
|mapper.servlet.port	|Mapping-service http port.<br/>Default value: **8086**
|max.active.threads		|Maximum number of active threads for server's requests.<br/>Default value: **150**
|max.queued.threads		|Maximum number of queued threads for server's requests.<br/>Default value: **300**
|x509.host-certificate	|x509 host certificate for SSL connector.<br/>Default value: **/etc/grid-security/gridhttps/hostcert.pem**
|x509.host-key			|x509 host key for SSL connector.<br/>Default value: **/etc/grid-security/gridhttps/hostkey.pem**
|x509.truststore.directory	|Truststore location.<br/>Default value: **/etc/grid-security/certificates**
|x509.truststore.refresh-interval	|Canl truststore update time interval expressed in milliseconds.<br/>Default value: **600000** (1 minute)

<br/>
GridHTTPs' log file is configurable:

|	Var. name			|	Description				|
|:----------------------|:--------------------------|
|log.configuration-file	|GridHTTPs logging configuration file.<br/>Default value: **/etc/storm/gridhttps-server/logback.xml**

<br/>
GridHTTPs interacts with StoRM Backend to configure itself in bootstrap phase, to check user's authorization access to resources, to perform SRM operation, to set checksum value on a file, etc. So it needs to know information about BE location and ports:

|	Var. name			|	Description				|
|:----------------------|:--------------------------|
|backend.hostname		|StoRM Backend server full hostname. <br/>**Mandatory**
|backend.authorization-service.port	|StoRM Backend server REST port.<br/>Default value: **9998**
|backend.srm-service.port	|StoRM Backend server XMLRPC port.<br/>Default value: **8080**
|backend.xmlrpc.token	|Token used for communicating with the backend service. Mandatory, has no default

<br/>
GridHTTPs works with SURLs so it needs to know a valid SRM endpoint:

|	Var. name			|	Description				|
|:----------------------|:--------------------------|
|srm.endpoint			|StoRM SRM EndPoint.<br/>Default value: **$STORM\_BACKEND\_HOSTNAME:8444**

<br/>
GridHTTPs manage file transfers and file creation. So it computes checksum during transfers. The computed checksum type is *adler32*. This capability can be disabled.

|	Var. name			|	Description				|
|:----------------------|:--------------------------|
|compute-checksum		|If compute-checksum is true, for every file created a valid adler32 checksum value is computed. Available values: true, false.<br/>Default value: **true**

### GridHTTPs' logging files and logging level <a name="ghttplog_advconf">&nbsp;</a>

GridHTTPs' log files are located in */var/log/storm/* directory. They are the followings:

- **storm-gridhttps-server.log** For managed requests
This is the main log file of StoRM GridHTTPs. All the information about the WebDAV, HTTP file-transfer and mapping requests, error or warning are logged here depending on the log level. At the INFO level, the GridHTTPs logs, for each operation, who have request the operation (DN and FQANs if not anonymous), on which file(s) (SURLs) and the operation result. At DEBUG level, much more information are printed regarding the status of many StoRM internal component, depending on the request type. DEBUG level has to be used carefully only for troubleshooting operation. If ERROR or FATAL level are used, the only event logged in the file are due to error condition.

- **storm-backend.stdout**
This file contains the standard out of the GridHTTPs process. Usually it does not contains any useful information.

- **storm-backend.stderr**
This file contains the event logged as ERROR or FATAL conditions. This event logs are presents both in the *storm-gridhttps-server.log* file and here.

The logging level of these files can be specified editing the configuration file **logback.xml** located in */etc/storm/gridhttps-server/* directory modifying the *level* value of:

	<logger name="it.grid.storm" level="INFO">
		<appender-ref ref="PROCESS" />
	</logger>

The supported logging levels are: FATAL, ERROR, WARN, INFO, DEBUG and TRACE.
<br/><br/>
The suggest logging level for production endpoint is INFO. In case the log level is modified, GridHTTPs service has to be restarted to read the new value.

### GridHTTPs plugin information: storm.gridhttps.plugin.properties <a name="ghttpplug_advconf">&nbsp;</a>

StoRM GridHTTPs Plugin is shipped with StoRM Backend metapackage and it is installed on Backend host. Its configuration information are stored in:

	/etc/storm/gridhttps-plugin/storm.gridhttps.plugin.properties

This file contains a list of:

	key = value

pairs that can be used to configure the GridHTTPs Plugin.
The GridHTTPs Plugin lives within Backend Java process; in case a parameter is modified, the Backend service have to be restarted in order to read the new value.

|	Property name		|	Description				|
|:----------------------|:--------------------------|
|gridhttps.server.user.uid	|The User ID associated to the local user running the GridHTTPs server service
|gridhttps.server.user.gid	|The primary Group ID associated to the local user running the GridHTTPs server service

## StoRM EMIR Configuration <a name="emir_advconf">&nbsp;</a>

You can use EMIR-SERP to publish StoRM information to EMIR. EMIR-SERP uses the information already available in the resource resource bdii (aka ERIS) and publish it to an EMIR DSR endpoint.
<br/>
<br/>
First check that the resource bdii is up and running. Executing

	ldapsearch -x -h localhost -p 2170 -b 'GLUE2GroupID=resource,o=glue' objectCLass=GLUE2Service
	
it should return two services for each vo installed on the machine. If this is not the case, there is some problem with your installation.
Then install serp:

	sudo yum install emir-serp

and edit the configuration file */etc/emi/emir-serp/emir-serp.ini*, providing the url for the EMIR DSR and the url for the resource bdii

	...
	url = http://emitbdsr1.cern.ch:9126
	...
	[servicesFromResourceBDII]
	resource_bdii_url = ldap://localhost:2170/GLUE2GroupID=resource,o=glue
	...
	
You can change the update interval

	# Period of registration/update messages
	# Mandatory configuration parameter
	# Value is given in hours
	period = 1
	
and the time of registration entry validity:

	# Time of registration entry validity
	# Mandatory configuration parameter
	# Value is given in hours
	validity = 2

You might want to set the logging level to debug the first time you start the service:

	verbosity = debug

Start the service:

	sudo service emir-serp start

Verify the pubblication by inspecting this <a href="http://emitbdsr1.cern.ch:9126/services">page</a> searching for an entity with "Name" attribute equal to StoRM YAIM variable "SITE\_NAME". It is recommended to set back the logging level to error and restart the service. Stopping emier-serp will cause the entry to be deleted.

## Appendix A <a name="AppendixA">&nbsp;</a>

### A.1 How-to configure LDAP Server to share users' accounts

This is a short tutorial that wants to describe how to install and configure a LDAP Server in order to share users' accounts whitin a local network. In particular, we will see how to install and configure a client/server OpenLDAP service on Scientific Linux hosts.

#### A.1.1 OpenLDAP Server installation and configuration <a name="LDAPServerIC">&nbsp;</a>

To install OpenLDAP service on server, as root user on a SL5 host, we have to install *openldap-servers* package:

```bash
$ yum install openldap-servers
```

Instead, as root user on a SL6 host we have to install both *openldap* and *openldap-servers* packages:

```bash
$ yum install openldap openldap-servers
```

OpenLDAP installs several files in /etc and other places. The *slapd* daemon's configuration file is slapd.conf and can be found in /etc/openldap. First of all make sure service is not running:

```bash
(SL5) $ service ldap stop
(SL6) $ service slapd stop
```

Then, edit **/etc/openldap/slapd.conf**. We have to edit 5 entries: *database*, *suffix*, *rootdn*, *rootpw* and *directory*. It should look something like:

	  database        bdb
	  suffix          "dc=example,dc=com"
	  rootdn          "cn=Manager,dc=example,dc=com"
	  # Cleartext passwords, especially for the rootdn, should be avoided. 
	  # See slappasswd(8) and slapd.conf(5) for details.
	  # Use of strong authentication encouraged.
	  # rootpw        secret
	  directory 	  /var/lib/ldap
	
For example, define ourselves as a company called storm.cnaf.infn.it. We can leave default value for *database* entry, *bdb*, that stands for Berkeley Database. The *suffix* entry is our main domain or organization. So, we can changed it to:

	  suffix          "dc=storm,dc=cnaf,dc=infn.it"

The dc stands for domainComponent. Next, the *rootdn*. This means root Distinguished Name. Every entry in an LDAP database has a distinguished name. The default is Manager. In our example we will use root instead of Manager, so according to the suffix we have to change it to:

	  rootdn          "cn=root,dc=storm,dc=cnaf,dc=infn.it"

Next the *password*. If we desire an encrypted password, we can launch slappasswd command, insert our password and replace *secret* with the outputted string. So it will look something like:

	  rootpw         {SSHA}ca6CWAHXogaQ2Cib9sxOYRwHRzyKoSXA

We can leave default value for *directory* entry: /var/lib/ldap.
Now we have to clean up previous LDAP content and configuration with the following command:

```bash
(SL5) $ rm -rf /var/lib/ldap/*
(SL6) $ rm -rf /etc/openldap/slapd.d/*
```

Then we have to create the DB_CONFIG file into /var/lib/ldap/ directory (or copy it from OpenLDAP example file /etc/openldap/DB_CONFIG.example).

**/etc/openldap/DB_CONFIG.example**:

	  # $OpenLDAP: pkg/ldap/servers/slapd/DB_CONFIG,v 1.3.2.4 2007/12/18 11:53:27 ghenry Exp $
  	  # Example DB_CONFIG file for use with slapd(8) BDB/HDB databases.
  	  #
  	  # See the Oracle Berkeley DB documentation
  	  #	<http://www.oracle.com/technology/documentation/berkeley-db/db/ref/env/db_config.html>
  	  # for detail description of DB_CONFIG syntax and semantics.
  	  #
	  # Hints can also be found in the OpenLDAP Software FAQ
  	  # <http://www.openldap.org/faq/index.cgi?file=2>
	  # in particular:
	  #   <http://www.openldap.org/faq/index.cgi?file=1075>
	  # Note: most DB_CONFIG settings will take effect only upon rebuilding
	  # the DB environment.
	  # one 0.25 GB cache
	  set_cachesize 0 268435456 1
	  # Data Directory
	  #set_data_dir db
	  # Transaction Log settings
	  set_lg_regionmax 262144
	  set_lg_bsize 2097152
	  #set_lg_dir logs
	  # Note: special DB_CONFIG flags are no longer needed for "quick"
	  # slapadd(8) or slapindex(8) access (see their -q option).
	
To initialize the LDAP database we have to create a pair of files, one for the organization and one for the root DN. For example, create storm.cnaf.infn.it.ldif as follow:

	  dn: dc=storm,dc=cnaf,dc=infn.it
	  objectClass: dcObject
	  objectClass: organization
	  dc: storm
	  o: StoRM
	
It contains the organization entry. Then create root.storm.cnaf.infn.it.ldif as follow:

	  dn: cn=root,dc=storm,dc=cnaf,dc=infn.it
	  objectClass: organizationalRole
	  cn: root

and this contains the root DN. Now we have to initialize DB files for content in /var/lib/ldap directory:

```bash
$ echo "" | slapadd -f /etc/openldap/slapd.conf
```

This is required, otherwise you will get this error:
	  
	  bdb_db_open: database "dc=example,dc=com": db_open(/var/lib/ldap/id2entry.bdb) failed:
	  No such file or directory (2).

Now, only if host is SL6, convert configuration file into dynamic configuration under /etc/openldap/slapd.d directory:
	
```bash  
$ slaptest -f /etc/openldap/slapd.conf -F /etc/openldap/slapd.d
```

For both SL5 and SL6, set permissions:

```bash
	  chown -R ldap:ldap /var/lib/ldap 
```

and, only on SL6, add:

```bash
	  chown -R ldap:ldap /etc/openldap/slapd.d
```

Now we can initialize LDAP DB with already defined initial content, by launching the following commands:

```bash
	  slapadd -l storm.cnaf.infn.it.ldif
	  slapadd -l root.storm.cnaf.infn.it.ldif
```

So we are ready to add to our LDAP database the necessary users and groups. In particular we need storm and gridhttps users and also relative groups. But, how can we organize our directory tree? In a UNIX file system, the top level is the root. Underneath the root you have numerous files and directories. As mentioned above, LDAP directories are set up in much the same manner. Into the directory's base are conventionally created containers that logically separate data. For historical reasons, most LDAP directories set these logical separations up as OU entries. OU stands for "Organizational Unit", which in X.500 was used to indicate the functional organization within a company. Current LDAP implementations have kept the ou= naming convention. In our case, we can define a pair of Organizationa Unit: People and Group as follow:

**People.storm.cnaf.infn.it.ldif**

	  dn: ou=People,dc=storm,dc=cnaf,dc=infn.it
  	  objectClass: organizationalUnit
	  objectClass: top
	  ou: People

**Group.storm.cnaf.infn.it.ldif**

	  dn: ou=Group,dc=storm,dc=cnaf,dc=infn.it
	  objectClass: organizationalUnit
	  objectClass: top
 	  ou: Group

Then, we can define storm and gridhttps users and groups, as follow: 

**storm.People.storm.cnaf.infn.it.ldif**:

	  dn: uid=storm,ou=People,dc=storm,dc=cnaf,dc=infn.it
	  objectClass: account
	  objectClass: posixAccount
	  objectClass: top
	  objectClass: shadowAccount
	  cn: storm
	  gidNumber: 494
	  homeDirectory: /home/storm
	  uid: storm
	  uidNumber: 495
	
**storm.Group.storm.cnaf.infn.it.ldif**:

	  dn: cn=storm,ou=Group,dc=storm,dc=cnaf,dc=infn.it
	  objectClass: posixGroup
	  objectClass: top
	  cn: storm
	  gidNumber: 494
	
**gridhttps.People.storm.cnaf.infn.it.ldif**:

	  dn: uid=gridhttps,ou=People,dc=storm,dc=cnaf,dc=infn.it
	  objectClass: account
	  objectClass: posixAccount
	  objectClass: top
  	  objectClass: shadowAccount
	  cn: gridhttps
	  gidNumber: 504
	  homeDirectory: /home/gridhttps
	  uid: gridhttps
	  uidNumber: 503

**gridhttps.Group.storm.ldif**:

	  dn: cn=gridhttps,ou=Group,dc=storm,dc=cnaf,dc=infn.it
	  objectClass: posixGroup
	  objectClass: top
	  cn: gridhttps
	  gidNumber: 504

Set the various GIDs and UIDs values as you want. Then we can add the users and groups defined to the LDAP server database, with the following commands:

	  slapadd -l storm.Group.storm.cnaf.infn.it.ldif
	  slapadd -l gridhttps.Group.storm.cnaf.infn.it.ldif
	  slapadd -l storm.People.storm.cnaf.infn.it.ldif
	  slapadd -l gridhttps.People.storm.cnaf.infn.it.ldif
	
Using a free program like Apache Directory Studio we can easily connect to the LDAP server and add other entries, export ldif configurations, etc. Important: as you can see from the files above, a user must contain account, posixAccount and shadowAccount objectClasses, a group instead must define only a posixGroup objectClass.

To start server:

```bash
(SL5) $ service ldap start
(SL6) $ service slapd start
```

#### A.1.2 OpenLDAP Client installation and configuration <a name="LDAPClientIC">&nbsp;</a>

To install OpenLDAP service on a client, as root user on a SL5 host, we have to install *openldap-clients* and *nss_ldap* packages:

```bash
$ yum install openldap-clients nss_ldap
```

Instead, as root user on a SL6 host we have to install both *openldap-clients* and *nss-pam-ldapd* packages:

```bash
$ yum install openldap-clients nss-pam-ldapd
```

Be sure that service *nscd* is stopped:

```bash
$ service nscd status
```

Modify **/etc/nsswitch.conf** by adding "*ldap*" to the following lines:
	     
	  passwd: files ldap
	  shadow: files ldap
	  group: files ldap

If SL5, modify both **/etc/ldap.conf** and **/etc/openldap/ldap.conf** by adding:
	
	  uri ldap://<ldap-server-hostname>
	  base dc=storm,dc=cnaf,dc=infn.it

If SL6 modify both **/etc/openldap/ldap.conf** and **/etc/nslcd.conf** by adding: 

	  uri ldap://<ldap-server-hostname>
	  base dc=storm,dc=cnaf,dc=infn.it

#### A.1.3 Test client-server LDAP installation <a name="TestLDAP">&nbsp;</a>

From a configured client we need to know UIDs and/or GIDs of server's LDAP users. That users has not to be defined as UNIX-users on clients. To query the LDAP server from one of the clients type, for example, you can list all the contents in db:

```bash
$ ldapsearch -x -b 'dc=storm,dc=cnaf,dc=infn.it'
```

you can search a particular uid or group:

```bash
$ ldapsearch -x "uid=storm"
$ ldapsearch -x "group=storm"
```

or you can get the UID or GID of a username: 

```bash
$ id -u storm
$ id -g storm
```

Verify that the obtained values are equals to the previous defined.
