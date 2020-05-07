---
layout: toc
title: StoRM Storage Resource Manager - System Administration Guide
version: 1.11.17
redirect_from:
  - /documentation/sysadmin-guide/
---

# StoRM System Administration Guide

version: {{ page.version }}

**Table of contents**

* [Upgrade from StoRM v1.11.16](#upgrading)
* [Installation Prerequisites](#prerequisites)
  * [Platform](#platform)
  * [Requirements](#requirements)
  * [System users and file limits](#limits)
  * [Storage Area's permissions](#sapermissions)
* [Installation guide](#installation)
  * [Repositories](#repositories)
  * [StoRM components](#components)
* [Configuration](#stormconfiguration)
  * [General YAIM variables](#generalyaimvars)
  * [StoRM Frontend variables](#frontendyaimvars)
  * [StoRM Backend variables](#backendyaimvars)
  * [StoRM WebDAV variables](#webdavyaimvars)
  * [Launching YAIM](#launchingyaim)
* [Advanced Configuration](#advancedconfiguration)
  * [StoRM Frontend service](#stormfrontendservice)
  * [StoRM Backend service](#stormbackendservice)
  * [StoRM GridFTP service](#stormgridftpservice)
* [Puppet Configuration](#puppetconfiguration)
  * [StoRM WebDAV](#puppetwebdav)
  * [StoRM GridFTP](#puppetgridftp)
* [Logging](#logg)
  * [StoRM Frontend logging](#stormfrontendlogging)
  * [StoRM Backend logging](#stormbackendlogging)
  * [StoRM WebDAV logging](#stormwebdavlogging)
  * [GridFTP logging](#stormgridftplogging)
* [Information Service](#informationservice)
  * [StoRM Info Provider](#storminfoprovider)
* [CDMI StoRM](#cdmistorm)
  * [Introduction](#cdmistormintro)
  * [Installation and Configuration](#cdmistorminstall)

------

## Upgrade from StoRM v1.11.16 <a name="upgrading">&nbsp;</a>

The services that needs to be updated are:

* `storm-backend-server`
* `storm-webdav`
* `storm-globus-gridftp-server`

#### Update packages

 ```bash
yum update storm-backend-server storm-webdav storm-globus-gridftp-server
```

Split this command properly if you have a distributed deployment.
Then, run YAIM.

## Upgrade from earlier versions <a name="upgrading-earlier">&nbsp;</a>

If you are upgrading from StoRM v1.11.15 or earlier versions please follow
[these instructions][upgrade-15] before.

## Installation Prerequisites <a name="prerequisites">&nbsp;</a>

### Platform <a name="platform">&nbsp;</a>

All the StoRM components are certified to work on **Scientific Linux SL6/64** (x86_64) with an EPEL repository
for external dependencies.

All the information about the OS Scientific Linux can be found at [here][Scientific Linux].

There are no specific minimum hardware requirements but it is advisable to have at least 1GB of RAM on Backend host.

### Requirements <a name="requirements">&nbsp;</a>

Each StoRM deployment requires some services and packages installed.

#### NTP service <a name="ntpservice">&nbsp;</a>

NTP service must be installed.

    yum install ntp
    chkconfig ntpd on
    service ntpd restart

#### Hostname <a name="fqdnhostname">&nbsp;</a>

Hostname must be a *Fully Qualified Domain Name* (FQDN).

To check if your hostname is a FQDN, run:

    hostname -f

The command must return the host FQDN.

If you need to correct it and you are using bind or NIS for host lookups, you can change the FQDN
and the DNS domain name, which is part of the FQDN, in the /etc/hosts file.

    # Do not remove the following line, or various programs
    # that require network functionality will fail.
    127.0.0.1       MYHOSTNAME.MYDOMAIN MYHOSTNAME localhost.localdomain localhost
    ::1             localhost6.localdomain6 localhost6

Set your own MYHOSTNAME and MYDOMAIN and restart the network service:

    service network restart

#### Host credentials <a name="x509host">&nbsp;</a>

Hosts participating to the StoRM-SE (Frontend, Backend, WebDAV and GridFTP hosts) service must be configured
with X.509 certificates signed by a trusted Certification Authority (CA).
Usually, the **hostcert.pem** and **hostkey.pem** certificates are located in the */etc/grid-security* directory,
and they must have permission *0644* and *0400* respectively:

    ls -l /etc/grid-security/hostkey.pem
    -r-------- 1 root root 887 Mar  1 17:08 /etc/grid-security/hostkey.pem

    ls -l /etc/grid-security/hostcert.pem
    -rw-r--r-- 1 root root 1440 Mar  1 17:08 /etc/grid-security/hostcert.pem

Check if certificate is expired as follow:

    openssl x509 -checkend 0 -in /etc/grid-security/hostcert.pem

To change permissions, if necessary:

    chmod 0400 /etc/grid-security/hostkey.pem
    chmod 0644 /etc/grid-security/hostcert.pem

#### ACL support <a name="acl">&nbsp;</a>

StoRM uses the ACLs on files and directories to implement the security model.
In so doing, StoRM uses the native access to the file system. Therefore in order to ensure a proper running,
ACLs need to be enabled on the underlying file-system (sometimes they are enabled by default) and work properly.

If the *getfacl* and *setfacl* commands are not available on your host you have to **install** *acl* package:

    yum install acl

To check if all properly works, try to set an acl to a test file as follow:

    touch test
    setfacl -m u:storm:rw test

Note: the ```storm``` user adopted to set the ACL entry **MUST** exist.

    getfacl test

Should return the following values:

    # file: test
    # owner: root
    # group: root
    user::rw-
    user:storm:rw-
    group::r--
    mask::rw-
    other::r--

To enable ACLs (if needed), you must add the acl property to the relevant file system in your */etc/fstab* file.
For example:

    /dev/hda3     /storage      ext3     defaults, acl     1 2

Then you need to remount the affected partitions as follows:

    mount -o remount /storage

This is valid for different file system types (i.e., ext3, xfs, gpfs and others).

#### Extended Attribute support <a name="attr">&nbsp;</a>

StoRM uses the Extended Attributes (EA) on files to store some metadata related to the file (e.g. the checksum value);
therefore in order to ensure a proper running, the EA support needs to be enabled on the underlying file system and
work properly.

If the *getfattr* and *setfattrl* commands are not available on your host, install ```attr``` package:

    yum install attr

To check if all properly works, try to set an extendend attribute to a test file:

    touch testfile
    setfattr -n user.testea -v test testfile
    getfattr -d testfile

It should return:

    # file: testfile
    user.testea="test"

To enable EA (if needed) you must add the *user_xattr* property to the relevant file systems in your */etc/fstab* file.
For example:

    /dev/hda3     /storage     ext3     defaults,acl,user_xattr     1 2

Then you need to remount the affected partitions as follows:

    mount -o remount /storage

### System users and file limits <a name="limits">&nbsp;</a>

The StoRM Frontend, Backend and WebDAV services run by default as user **storm**.
It's recommended to keep the default settings and use the same user for all
the services.

You can use the following commands to create the StoRM user on the machines
where you are deploying the services:

    useradd -M storm

The option ```-M``` means 'without an home directory'.
You could also use specific user and group IDs as follows (change
the text contained in angled brackets with the appropriate
numerical value for your installation):

    useradd -M storm -u MY_STORM_UID -g MY_STORM_GID

{% assign label_caption="Important" %}
{% include open_note.liquid %}
> Keep UIDs and GIDs aligned for StoRM users and groups on distributed deployments (i.e. when the services are installed on different machines).<br/>
> This can be done using NIS (see a tutorial [here][how-to-nis] or LDAP (see [How-to example][LDAPconfiguration]).

#### File limits

The following settings are recommended to safely run the StoRM services.
Put these settings in */etc/security/limits.conf* or in a file contained in the
*/etc/security/limits.d* directory (recommended):

    storm hard nofile 65535
    storm soft nofile 65535

### Storage Area's permissions <a name="sapermissions">&nbsp;</a>

All the Storage Areas managed by StoRM needs to be owned by the STORM_USER.
This means that if STORM_USER is *storm*, for example, the storage-area *test*
root directory permissions must be:

    drwxr-x---+  2 storm storm

YAIM-StoRM doesn't set the correct permissions if the SA's root directory
already exists. So, the site administrator has to take care of it. To set the
correct permissions on a storage area, you can launch the following commands
(assuming that storm runs as user *storm*, which is the default):

    chown -RL storm:storm <sa-root-directory>
    chmod -R 750 <sa-root-directory>

Site administrator must also make traversable by other users the parent
directories of each storage-area root directory (that's usually the same
directory for all the storage-areas):

    chmod o+x <sa-root-directory-parent>

## Installation guide <a name="installation">&nbsp;</a>

Before installing storm components, install the required repositories.

### Repositories <a name="repositories">&nbsp;</a>

StoRM packages can be obtained from the UMD repositories and/or from the StoRM product team package repository.
The EGI Trust Anchors repository is also required.

#### EPEL repositories <a name="epelrepos">&nbsp;</a>

StoRM depends on EPEL 6 repositories.

Install them as follows:

    yum localinstall https://dl.fedoraproject.org/pub/epel/6/x86_64/Packages/e/epel-release-6-8.noarch.rpm

#### UMD Repositories <a name="umdrepos">&nbsp;</a>

StoRM depends on UMD repositories.

Because UMD-3 is currently EOL (and it will receive only emergency updates), **we encourage to install UMD-4**, which is officially supported, without any caveat, from StoRM v1.11.13.

##### Clean UMD-4 installation

Install UMD pgp key:

    rpm --import http://repository.egi.eu/sw/production/umd/UMD-RPM-PGP-KEY

Install latest UMD-4 for SL6:

    yum localinstall http://repository.egi.eu/sw/production/umd/4/sl6/x86_64/updates/umd-release-4.1.3-1.el6.noarch.rpm

##### Upgrading from UMD-3

Clear old UMD repositories eventually installed before:

    yum remove umd-release

Install latest UMD-4 for SL6:

    yum localinstall http://repository.egi.eu/sw/production/umd/4/sl6/x86_64/updates/umd-release-4.1.3-1.el6.noarch.rpm

More information about UMD installation can be found [here][UMD-instructions].

#### EGI Trust Anchors Repository <a name="egitrustrepo">&nbsp;</a>

Install *EGI Trust Anchors repository* by following [EGI instructions][egi-instructions].

In short:

    wget http://repository.egi.eu/sw/production/cas/1/current/repo-files/EGI-trustanchors.repo -O /etc/yum.repos.d/EGI-trustanchors.repo
    yum install ca-policy-egi-core

The *DAG repository* must be disabled. If needed, set to 0 the enabled property in your */etc/yum.repos.d/dag.repo* file.

#### StoRM Repositories <a name="stormrepos">&nbsp;</a>

The latest certified StoRM package can be found in the StoRM production repository for [SL6][stable-storm-repoview].
Note that you should also have UMD repositories installed (as detailed above) for your setup to work as expected.

To install the StoRM production repository files, run the following commands (as root):

    wget https://repo.cloud.cnaf.infn.it/repository/storm/stable/storm-stable-centos6.repo -O /etc/yum.repos.d/storm-stable-centos6.repo

### Install StoRM components <a name="components">&nbsp;</a>

In order to install StoRM components refresh the yum cache:

    yum clean all

To install the StoRM metapackages necessary to the SRM interface, install:

    yum install emi-storm-backend-mp
    yum install emi-storm-frontend-mp
    yum install emi-storm-globus-gridftp-mp

If your StoRM Backend runs on top of GPFS, be sure to install the `storm-native-libs-gpfs` package:

    yum install storm-native-libs-gpfs

If you want to add a WebDAV endpoint install also:

    yum install storm-webdav

The storm-srm-client is distributed with the UI EMI components,
but if you need it on your node you can install it using the command:

    yum install emi-storm-srm-client-mp


## Configuration <a name="stormconfiguration">&nbsp;</a>

StoRM is currently configured by using the YAIM tool, that is a set of configuration scripts that read a set of
configuration files.
If you want to go through the configuration, see the [advanced configuration](#advanced-configuration) guide.

As a quick start, you can follow these instructions to quickly configure a standalone StoRM deployment.

Create a single site configuration file `/etc/storm/siteinfo/storm.def` as follow:

```bash
# The human-readable name of your site.
SITE_NAME="sample-storm-deployment"

# A valid BDII hostname.
BDII_HOST="emitb-bdii-site.cern.ch"

# A space separated list of the IP addresses of the NTP servers.
# Preferably set a local ntp server and a public one, e.g. pool.ntp.org)
NTP_HOSTS_IP="77.242.176.254"

# The path to the file containing the list of Linux users (pool accounts) to be created. This file must be created by the site administrator and contains a plain list of the users and their IDs. An example of this configuration file is given in /opt/glite/yaim/examples/users.conf file.
USERS_CONF=/etc/storm/siteinfo/storm-users.conf

# The path to the file containing information on the mapping between VOMS groups and roles to local groups. An example of this configuration file is given in /opt/glite/yaim/examples/groups.conf file.
GROUPS_CONF=/etc/storm/siteinfo/storm-groups.conf

# MySQL root password
MYSQL_PASSWORD="storm"

# Domain name (used by StoRM Info Provider)
MY_DOMAIN="cnaf.infn.it"

# A space separated list of supported VOs
VOS="dteam"

# The FQDN of Backend's host.
STORM_BACKEND_HOST=`hostname -f`

# A valid path of the installed java libraries.
JAVA_LOCATION="/usr/lib/jvm/java"

# The default root directory of the Storage Areas.
STORM_DEFAULT_ROOT="/storage"

# A valid password for database connection.
STORM_DB_PWD=storm

# Token used to communicate with Backend service.
STORM_BE_XMLRPC_TOKEN=secretpassword

# The list of the managed storage areas.
STORM_STORAGEAREA_LIST=$VOS

# For each storage area it's mandatory to set the relative maximum online size.
STORM_DTEAM_ONLINE_SIZE=10

# Enable HTTP Transfer Protocol
STORM_INFO_HTTP_SUPPORT=true

# Enable HTTPS Transfer Protocol
STORM_INFO_HTTPS_SUPPORT=true

# Publish the following WebDAV endpoints
STORM_WEBDAV_POOL_LIST=https://$STORM_BACKEND_HOST:8443,http://$STORM_BACKEND_HOST:8085
```

Example of `storm-users.conf` file:

```
71001:dteam001:7100:dteam:dteam::
71002:dteam002:7100:dteam:dteam::
71003:dteam003:7100:dteam:dteam::
71004:dteam004:7100:dteam:dteam::
[...]
71999:dteam999:7100:dteam:dteam::
```

Example of `storm-groups.conf` file:

```
"/dteam"::::
```

Then you can run YAIM specifying the proper components profiles:

    /opt/glite/yaim/bin/yaim -c -s /etc/storm/siteinfo/storm.def \
    -n se_storm_backend \
    -n se_storm_frontend \
    -n se_storm_gridftp \
    -n se_storm_webdav

as better explained [here](#launching-yaim).

### General YAIM variables <a name="generalyaimvars">&nbsp;</a>

Create a **site-info.def** file in your CONFDIR/ directory.
Edit this file by providing a value to the general variables summarized in Tab.1.

| Var. Name              | Description   | Mandatory |
|:-----------------------|:--------------|:---------:|
|`SITE_NAME`             |It's the human-readable name of your site used to set the Glue-SiteName attribute.<br/>Example: `SITE_NAME="INFN EMI TESTBED"` | Yes
|`BDII_HOST`             |BDII hostname.<br/>Example: `BDII_HOST="emitb-bdii-site.cern.ch"` | Yes
|`NTP_HOSTS_IP`          |Space separated list of the IP addresses of the NTP servers (preferably set a local ntp server and a public one, e.g. pool.ntp.org). If defined, /etc/ntp.conf will be overwritten during YAIM configuration. If not defined, the site administrator will be manage on his own the ntp service and its configuration. <br/>Example: `NTP_HOSTS_IP="131.154.1.103"` | No
|`USERS_CONF`            |Path to the file containing the list of Linux users (pool accounts) to be created. This file must be created by the site administrator. It contains a plain list of the users and their IDs. An example of this configuration file is given in /opt/glite/yaim/examples/users.conf file. More details can be found in the User configuration section in the YAIM guide. | Yes
|`GROUPS_CONF`           |Path to the file containing information on the map- ping between VOMS groups and roles to local groups. An example of this configuration file is given in /opt/glite/yaim/examples/groups.conf file. More details can be found in the Group configuration section in the YAIM guide. | Yes
|`MYSQL_PASSWORD`        |MySQL root password.<br/>Example: `MYSQL_PASSWORD="carpediem"` | Yes
|`VOS`                   |List of supported VOs.<br/>Example: `VOS="test.vo dteam"` | Yes
|`STORM_BE_XMLRPC_TOKEN` |Token used in communication to the StoRM Backend | Yes


{% assign label_title="Table 1" %}
{% assign label_id="Table1" %}
{% assign label_description="General YAIM Variables." %}
{% include documentation/label.html %}

### StoRM Frontend variables <a name="frontendyaimvars">&nbsp;</a>

Frontend specific YAIM variables are in the following file:

    /opt/glite/yaim/examples/siteinfo/services/se_storm_frontend

Please copy and edit that file in your CONFDIR/services directory.
You have to set at least the `STORM_DB_PWD` variable and check the other variables to evaluate
if you like the default set or if you want to change those settings.
[Table 2](#Table2) summaries YAIM variables for StoRM Frontend component.

|   Var. Name                         |   Description |
|:------------------------------------|:--------------|
|`ARGUS_PEPD_ENDPOINTS`               |The complete service endpoint of Argus PEP server. Mandatory if `STORM_FE_USER_BLACKLISTING` is true. Example: https://host.domain:8154/authz
|`STORM_BACKEND_REST_SERVICES_PORT`   |StoRM backend server rest port. Optional variable. Default value: **9998**
|`STORM_BE_XMLRPC_PATH`               |StoRM Backend XMLRPC server path. <br/>Optional variable. Default value: **/RPC2**
|`STORM_BE_XMLRPC_PORT`               |StoRM Backend XMLRPC server port. <br/>Optional variable. Default value: **8080**
|`STORM_CERT_DIR`                     |Host certificate directory for StoRM Frontend service. Optional variable. Default value: **/etc/grid-security/${STORM_USER}**
|`STORM_DB_HOST`                      |Host for database connection. <br/>Optional variable. Default value: **localhost**
|`STORM_DB_PWD`                       |Password for database connection. **Mandatory**.
|`STORM_DB_USER`                      |User for database connection. Default value: **storm**
|`STORM_FE_ENABLE_MAPPING`            |Enable the check in gridmapfile for client DN. <br/>Optional variable. Available values: true, false. Default value: **false**
|`STORM_FE_ENABLE_VOMSCHECK`          |Enable the check in gridmapfile for client VOMS attributes. <br/>Optional variable. Available values: true, false. Default value: **false**
|`STORM_FE_GSOAP_MAXPENDING`          |Max number of request pending in the GSOAP queue. Optional variable. Default value: **2000**
|`STORM_FE_LOG_FILE`                  |StoRM frontend log file.<br/>Optional variable. Default value: **/var/log/storm/storm-frontend.log**
|`STORM_FE_LOG_LEVEL`                 |StoRM Frontend log level.<br/>Optional variable. Available values: KNOWN, ERROR, WARNING, INFO, DEBUG, DEBUG2.<br/>Default value: **INFO**
|`STORM_FE_MONITORING_DETAILED`       |Flag to enable/disable detailed SRM requests Monitoring. Optional variable. Available values: true, false. Default value: **false**
|`STORM_FE_MONITORING_ENABLED`        |Flag to enable/disable SRM requests Monitoring.<br/>Optional variable. Available values: true, false. Default value: **true**
|`STORM_FE_MONITORING_TIME_INTERVAL`  |Time intervall in seconds between each Monitoring round. Optional variable. Default value: **60**
|`STORM_FE_THREADS_MAXPENDING`        |Max number of request pending in the Threads queue. Optional variable. Default value: **200**
|`STORM_FE_THREADS_NUMBER`            |Max number of threads to manage user's requests. Optional variable. Default value: **50**
|`STORM_FE_USER_BLACKLISTING`         |Flag to enable/disable user blacklisting.<br/>Optional variable. Available values: true, false. Default value: **false**
|`STORM_FE_WSDL`                      |WSDL to be returned to a GET request.<br/>Optional variable. Default value: **/usr/share/wsdl/srm.v2.2.wsdl**
|`STORM_FRONTEND_OVERWRITE`           |This parameter tells YAIM to overwrite storm-frondend.conf configuration file.<br/>Optional variable. Available values: true, false. Default value: **true**
|`STORM_FRONTEND_PORT`                |StoRM Frontend service port. Optional variable. Default value: **8444**
|`STORM_PEPC_RESOURCEID`              |Argus StoRM resource identifier. Optional variable. Default value: **storm**
|`STORM_USER`                         |Service user.<br/>Optional variable. Default value: **storm**

{% assign label_title="Table 2" %}
{% assign label_id="Table2" %}
{% assign label_description="Specific StoRM Frontend Variables." %}
{% include documentation/label.html %}

### StoRM Backend variables <a name="backendyaimvars">&nbsp;</a>

Backend specific YAIM variables are in the following file:

    /opt/glite/yaim/exaples/siteinfo/services/se_storm_backend

Please copy and edit that file in your CONFDIR/services directory.
You have to set at least these variables:

- `STORM_BACKEND_HOST`
- `STORM_DEFAULT_ROOT`
- `STORM_DB_PWD`

and check the other variables to evaluate if you like the default set or if you want to change those settings.
[Table 3](#Table3) summaries YAIM variables for StoRM Backend component.

|   Var. Name                       |   Description |
|:----------------------------------|:--------------|
|`STORM_ACLMODE`                    |ACL enforcing mechanism (default value for all Storage Areas). Note: you may change the settings for each SA acting on `STORM_[SA]_ACLMODE` variable. Available values: aot, jit (use aot for WLCG experiments).<br/>Optional variable. Default value: **aot**
|`STORM_ANONYMOUS_HTTP_READ`        |Storage Area anonymous read access via HTTP. Note: you may change the settings for each SA acting on `STORM_[SA]_ANONYMOUS_HTTP_READ` variable.<br/>Optional variable. Available values: true, false. Default value: **false**
|`STORM_AUTH`                       |Authorization mechanism (default value for all Storage Areas). Note: you may change the settings for each SA acting on `STORM_[SA]_AUTH` variable Available values: permit-all, deny-all, FILENAME.<br/>Optional variable. Default value: **permit-all**
|`STORM_BACKEND_HOST`               |Host name of the StoRM Backend server. **Mandatory**.
|`STORM_BACKEND_REST_SERVICES_PORT` |StoRM backend server rest port. Optional variable. Default value: **9998**
|`STORM_CERT_DIR`                   |Host certificate directory for StoRM Backend service.<br/>Optional variable. Default value: **/etc/grid-security/STORM_USER**
|`STORM_DEFAULT_ROOT`               |Default directory for Storage Areas. **Mandatory**.
|`STORM_DB_HOST`                    |Host for database connection.<br/>Optional variable. Default value: **localhost**
|`STORM_DB_PWD`                     |Password for database connection. **Mandatory**.
|`STORM_DB_USER`                    |User for database connection.<br/>Optional variable. Default value: **storm**
|`STORM_FRONTEND_HOST_LIST`         |StoRM Frontend service host list: SRM endpoints can be more than one virtual host different from `STORM_BACKEND_HOST` (i.e. dynamic DNS for multiple StoRM Frontends).<br/>Mandatory variable. Default value: **STORM_BACKEND_HOST**
|`STORM_FRONTEND_PATH`              |StoRM Frontend service path.<br/>Optional variable. Default value: **/srm/managerv2**
|`STORM_FRONTEND_PORT`              |StoRM Frontend service port. Optional variable. Default value: **8444**
|`STORM_FRONTEND_PUBLIC_HOST`       |StoRM Frontend service public host. It's used by StoRM Info Provider to publish the SRM endpoint into the Resource BDII.<br/>Mandatory variable. Default value: **STORM_BACKEND_HOST**
|`STORM_FSTYPE`                     |File System Type (default value for all Storage Areas). Note: you may change the settings for each `SA` acting on `STORM_[SA]_FSTYPE` variable.<br/>Optional variable. Available values: posixfs, gpfs and test. Default value: **posixfs**
|`STORM_GRIDFTP_POOL_LIST`          |GridFTP servers pool list (default value for all Storage Areas). Note: you may change the settings for each `SA` acting on `STORM_[SA]_GRIDFTP_POOL_LIST` variable.<br/>ATTENTION: this variable define a list of pair values space-separated: host weight, e.g.: `STORM_GRIDFTP_POOL_LIST="host1 weight1, host2 weight2, host3 weight3"` Weight has 0-100 range; if not specified, weight will be 100.<br/>Mandatory variable. Default value: **STORM_BACKEND_HOST**
|`STORM_GRIDFTP_POOL_STRATEGY`      |Load balancing strategy for GridFTP server pool (default value for all Storage Areas). Note: you may change the settings for each `SA` acting on `STORM_[SA]_GRIDFTP_POOL_STRATEGY` variable.<br/>Optional variable. Available values: round-robin, smart-rr, random, weight. Default value: **round-robin**
|`STORM_INFO_FILE_SUPPORT`          |If set to false, the following variables prevent the corresponding protocol to be published by the StoRM gip.<br/>Optional variable. Available values: true, false. Default value: **true**
|`STORM_INFO_GRIDFTP_SUPPORT`       |If set to false, the following variables prevent the corresponding protocol to be published by the StoRM gip.<br/>Optional variable. Available values: true, false. Default value: **true**
|`STORM_INFO_RFIO_SUPPORT`          |If set to false, the following variables prevent the corresponding protocol to be published by the StoRM gip. <br/>Optional variable. Available values: true, false. Default value: **false**
|`STORM_INFO_ROOT_SUPPORT`          |If set to false, the following variables prevent the corresponding protocol to be published by the StoRM gip.<br/>Optional variable. Available values: true, false. Default value: **false**
|`STORM_INFO_HTTP_SUPPORT`          |If set to false, the following variables prevent the corresponding protocol to be published by the StoRM gip.<br/>Optional variable. Available values: true, false. Default value: **false**
|`STORM_INFO_HTTPS_SUPPORT`         |If set to false, the following variables prevent the corresponding protocol to be published by the StoRM gip.<br/>Optional variable. Available values: true, false. Default value: **false**
|`STORM_NAMESPACE_OVERWRITE`        |This parameter tells YAIM to overwrite namespace.xml configuration file. Optional variable. Available values: true, false. Default value: **true**
|`STORM_RFIO_HOST`                  |Rfio server (default value for all Storage Areas). Note: you may change the settings for each `SA` acting on `STORM_[SA]_RFIO_HOST` variable.<br/>Optional variable. Default value: **STORM_BACKEND_HOST**
|`STORM_ROOT_HOST`                  |Root server (default value for all Storage Areas). Note: you may change the settings for each `SA` acting on `STORM_[SA]_ROOT_HOST` variable.<br/>Optional variable. Default value: **STORM_BACKEND_HOST**
|`STORM_SERVICE_SURL_DEF_PORTS`     |Comma-separated list of managed SURL's default ports used to check SURL validity.<br/>Optional variable. Default value: **8444**
|`STORM_SIZE_LIMIT`                 |Limit Maximum available space on the Storage Area (default value for all Storage Areas).<br/>Note: you may change the settings for each `SA` acting on `STORM_[SA]_SIZE_LIMIT` variable. Optional variable. Available values: true, false. Default value: **true**
|`STORM_STORAGEAREA_LIST`           |List of supported Storage Areas. Usually at least one Storage Area for each VO specified in VOS should be created.<br/>Optional variable. Default value: **VOS**
|`STORM_STORAGECLASS`               |Storage Class type (default value for all Storage Areas). Note: you may change the settings for each `SA` acting on `STORM_[SA]_STORAGECLASS` variable. <br/>Optional variable. Available values: T0D1, T1D0, T1D1. No default value.
|`STORM_SURL_ENDPOINT_LIST`         |This is a comma separated list of the SRM endpoints managed by the Backend. A SURL is accepted only if this list contains the endpoint specified. It's an optional variable with default value: **srm://STORM_FRONTEND_PUBLIC_HOST:STORM_FRONTEND_PORT/STORM_FRONTEND_PATH**. So, if you want to accept requests with incoming SURLs that has the ip address instead of the FQDN hostname, add the full srm endpoint to this list.
|`STORM_USER`                       |Service user. Optional variable. Default value: **storm**
|`STORM_ENDPOINT_QUALITY_LEVEL`     |Endpoint maturity level to be published by the StoRM gip. Optional variable. Default value: **2**
|`STORM_ENDPOINT_SERVING_STATE`     |Endpoint serving state to be published by the StoRM gip. Optional variable. Default value: **4**
|`STORM_ENDPOINT_CAPABILITY`        |Capability according to OGSA to be published by the StoRM gip. Optional variable. Default value: **data.management.storage**

{% assign label_title="Table 3" %}
{% assign label_id="Table3" %}
{% assign label_description="Specific StoRM Backend Variables." %}
{% include documentation/label.html %}

Then, for each Storage Area listed in the `STORM_STORAGEAREA_LIST` variable, which is not the name of a valid VO,
you have to edit the `STORM_[SA]_VONAME` compulsory variable (detailed in [Table 4](#Table4)).
`SA` has to be written in capital letters as in the other variables included in the **site-info.def** file,
otherwise default values will be used.

{% assign label_caption="Warning" %}
{% include open_note.liquid %}
> For the DNS-like names, that use special characters as '.' or '-' you have to remove the '.' and '-'.<br/>
> For example the `SA` value for the storage area "test.vo" must be TESTVO:

    STORM_TESTVO_VONAME=test.vo

For each storage area `SA` listed in `STORM_STORAGEAREA_LIST` you have to set at least these variables: `STORM_[SA]_ONLINE_SIZE`
You can edit the optional variables summarized in [Table 5](#Table5).

|   Var. Name                         |   Description |
|:------------------------------------|:--------------|
|`STORM_[SA]_VONAME`                  |Name of the VO that will use the Storage Area. Use the complete name, e.g., "lights.infn.it" to specify that there is no VO associated to the storage area (it's readable and writable from everyone - less than other filters). This variable becomes **mandatory if the value of SA is not the name of a VO**.
|`STORM_[SA]_ANONYMOUS_HTTP_READ`     |Storage Area anonymous read access via HTTP.<br/>Optional variable. Available values: true, false. Default value: **false**
|`STORM_[SA]_ACCESSPOINT`             |List space-separated of paths exposed by the SRM into the SURL. Optional variable. Default value: `SA`
|`STORM_[SA]_ACLMODE`                 |See `STORM_ACLMODE` definition. Optional variable. Default value: **STORM_ACLMODE**
|`STORM_[SA]_AUTH`                    |See `STORM_AUTH` definition. Optional variable. Default value: **STORM_AUTH**
|`STORM_[SA]_DEFAULT_ACL_LIST`        |A list of ACL entries that specifies a set of local groups with corresponding permissions (R, W, RW) using the following syntax: groupname1:permission1 [groupname2:permission2] [...]
|`STORM_[SA]_DN_C_REGEX`              |Regular expression specifying the format of C (Country) field of DNs that will use the Storage Area. Optional variable.
|`STORM_[SA]_DN_O_REGEX`              |Regular expression specifying the format of O (Organization name) field of DNs that will use the Storage Area. Optional variable.
|`STORM_[SA]_DN_OU_REGEX`             |Regular expression specifying the format of OU (Organizational Unit) field of DNs that will use the Storage Area. Optional variable.
|`STORM_[SA]_DN_L_REGEX`              |Regular expression specifying the format of L (Locality) field of DNs that will use the Storage Area. Optional variable.
|`STORM_[SA]_DN_CN_REGEX`             |Regular expression specifying the format of CN (Common Name) field of DNs that will use the Storage Area. Optional variable.
|`STORM_[SA]_FILE_SUPPORT`            |Enable the corresponding protocol. Optional variable. Default value: **STORM_INFO_FILE_SUPPORT**
|`STORM_[SA]_GRIDFTP_SUPPORT`         |Enable the corresponding protocol. Optional variable. Default value: **STORM_INFO_GRIDFTP_SUPPORT**
|`STORM_[SA]_RFIO_SUPPORT`            |Enable the corresponding protocol. Optional variable. Default value: **STORM_INFO_RFIO_SUPPORT**
|`STORM_[SA]_ROOT_SUPPORT`            |Enable the corresponding protocol. Optional variable. Default value: **STORM_INFO_ROOT_SUPPORT**
|`STORM_[SA]_HTTP_SUPPORT`            |Enable the corresponding protocol. Optional variable. Default value: **STORM_INFO_HTTP_SUPPORT**
|`STORM_[SA]_HTTPS_SUPPORT`           |Enable the corresponding protocol. Optional variable. Default value: **STORM_INFO_HTTPS_SUPPORT**
|`STORM_[SA]_FSTYPE`                  |See `STORM_[SA]_FSTYPE` definition. Optional variable. Available values: posixfs, gpfs. Default value: **STORM_FSTYPE**
|`STORM_[SA]_GRIDFTP_POOL_LIST`       |See `STORM_GRIDFTP_POOL_LIST` definition. Optional variable. Default value: **STORM_GRIDFTP_POOL_LIST**
|`STORM_[SA]_GRIDFTP_POOL_STRATEGY`   |See `STORM_GRIDFTP_POOL_STRATEGY` definition. Optional variable. Default value: **STORM_GRIDFTP_POOL_STRATEGY**
|`STORM_[SA]_ONLINE_SIZE`             |Total size assigned to the Storage Area Expressed in GB. Must be an integer value. **Mandatory**.
|`STORM_[SA]_ORGS`                    |Comma separated list of the supported authorization servers used by StoRM WebDAV with bearer tokens. No default value.
|`STORM_[SA]_QUOTA`                   |Enables the quota management for the Storage Area and it works only on GPFS filesystem. Optional variable. Available values: true, false. Default value: **false**
|`STORM_[SA]_QUOTA_DEVICE`            |GPFS device on which the quota is enabled. It is mandatory if `STORM_[SA]_QUOTA` variable is set. No default value.
|`STORM_[SA]_QUOTA_USER`              |GPFS quota scope. Only one of the following three will be used (the first one with the highest priority in this order: USER, then GROUP, then FILESET). Optional variable. No default value.
|`STORM_[SA]_QUOTA_GROUP`             |GPFS quota scope. Only one of the following three will be used (the first one with the highest priority in this order: USER, then GROUP, then FILESET). Optional variable. No default value.
|`STORM_[SA]_QUOTA_FILESET`           |GPFS quota scope. Only one of the following three will be used (the first one with the highest priority in this order: USER, then GROUP, then FILESET). Optional variable. No default value.
|`STORM_[SA]_RFIO_HOST`               |See `STORM_RFIO_HOST` definition. Optional variable. Default value: **STORM_RFIO_HOST**
|`STORM_[SA]_ROOT`                    |Physical storage path for the VO. Optional variable. Default value: **STORM_DEFAULT_ROOT/`SA`**
|`STORM_[SA]_ROOT_HOST`               |See `STORM_ROOT_HOST` definition. Optional variable. Default value: **STORM_ROOT_HOST**
|`STORM_[SA]_SIZE_LIMIT`              |See `STORM_SIZE_LIMIT` definition. Default value: **STORM_SIZE_LIMIT**
|`STORM_[SA]_STORAGECLASS`            |See `STORM_STORAGECLASS` definition. Available values: `T0D1`, `T1D0`, `T1D1`, null. No default value.
|`STORM_[SA]_TOKEN`                   |Storage Area token, e.g: `LHCb_RAW`, `INFNGRID_DISK`. No default value.
|`STORM_[SA]_USED_ONLINE_SIZE`        |Storage space currently used in the Storage Area expressed in Bytes. Must be an integer value. Used by YAIM to populate used-space.ini file.

{% assign label_title="Table 4" %}
{% assign label_id="Table4" %}
{% assign label_description="Storage Area Variables." %}
{% include documentation/label.html %}

### StoRM WebDAV variables <a name="webdavyaimvars">&nbsp;</a>

Follow [StoRM WebDAV service installation and configuration][webdav-guide] guide
to properly configure the service.

Read also [StoRM WebDAV support for Third Party Copy transfers][tpc-guide] guide
to properly configure the support for Third Party Copy transfers.

### Launching YAIM <a name="launchingyaim">&nbsp;</a>

After having built the **site-info.def** file, you can configure the needed profile by using YAIM as follows:

    /opt/glite/yaim/bin/yaim -c -d 6 -s <site-info.def> -n <profile-name>

But if in your StoRM deployment scenario more than a StoRM service has been installed on a single host you have to provide **a single site-info.def services file** containing **all** the required YAIM variables. Then you can configure all service profiles at once with a single YAIM call:

    /opt/glite/yaim/bin/yaim -c -d 6 -s siteinfo.def -n se_storm_backend se_storm_frontend se_storm_gridftp se_storm_webdav

> **NOTE**: if you are configuring on the same host profiles *se_storm_backend* and *se_storm_frontend*, you have to specify those profiles in this order to YAIM. This is also the case of profiles *se_storm_backend* and *se_storm_webdav*.

In case of a distributed deployment, on every host that run almost one of the StoRM components, you have to run YAIM specifying only the profiles of the installed components.

To check StoRM services status run:

    service storm-backend-server status
    service storm-frontend-server status
    service storm-globus-gridftp status
    service storm-webdav status

## Advanced Configuration <a name="advancedconfiguration">&nbsp;</a>

Please note that most of the configuration parameters of StoRM can be automatically managed directly by YAIM. This means that for standard installation in WLCG site without special requirement is not needed a manual editing of StoRM configuration file, but only a proper tuning of StoRM YAIM variables. On the other hand, with this guide we would like to give to site administrators the opportunity to learn about StoRM details and internal behaviours, in order to allow advanced configuration and ad-hoc set up, to optimize performance and results.

### StoRM Frontend service <a name="stormfrontendservice">&nbsp;</a>

The Frontend component relies on a single configuration file that contains all the configurable parameters. This file is:

    /etc/storm/frontend-server/storm-frontend-server.conf

containing a list of:

    key = value

pairs that can be used to configure the Frontend server.

{% assign label_caption="Important" %}
{% include open_note.liquid %}
> In case a parameter is modified, the Frontend service has to be restarted in order to read the new value.

Currently, the Frontend's configuration parameters can be divided per section as follows.

#### Database settings

|   Property Name   |   Description     |
|:------------------|:------------------|
| `db.host`   | Host for database connection. Default is **localhost**
| `db.user`   | User for database connection. Default is **storm**
| `db.passwd` | Password for database connection. Default is **password**

#### Service settings

|   Property Name   |   Description     |
|:------------------|:------------------|
| `fe.port`                      |  Frontend service port. Default is **8444**
| `fe.threadpool.threads.number` |  Size of the worker thread pool. Default is **50**
| `fe.threadpool.maxpending`     |  Size of the internal queue used to maintain SRM tasks in case there are no free worker threads. Default is **200**
| `fe.gsoap.maxpending`          |  Size of the GSOAP queue used to maintain pending SRM requests. Default is **1000**

#### Log settings

|   Property Name   |   Description     |
|:------------------|:------------------|
| `log.filename`    | Full log file name path.<br/>Default is **/var/log/storm/storm-frontend.log**
| `log.debuglevel`  | Logging level. Possible values are: ERROR, WARN, INFO, DEBUG, DEBUG2. Default is **INFO**

#### Monitoring settings

|   Property Name   |   Description     |
|:------------------|:------------------|
| `monitoring.enabled`      | Enable/disable monitoring. Default is **true**.
| `monitoring.timeInterval` | Time interval in seconds between each monitoring round. Default is **60**.
| `monitoring.detailed`     | Enable/disable detailed monitoring. Default is **false**.

#### XML-RPC communication settings

|   Property Name   |   Description     |
|:------------------|:------------------|
| `be.xmlrpc.host`        | Backend hostname. Default is **localhost**.
| `be.xmlrpc.port`        | Backend XML-RPC server port. Default is **8080**.
| `be.xmlrpc.token`       | Token used for communicating with Backend service. **Mandatory**, has no default.
| `be.xmlrpc.path`        | XML-RPC server path. Default is **/RPC2**.
| `be.xmlrpc.check.ascii` | Enable/disable ASCII checking on strings to be sent via XML-RPC. Default is **true**.

#### REST communication settings

|   Property Name   |   Description     |
|:------------------|:------------------|
| `be.recalltable.port` | REST server port running on the Backend machine. Default is **9998**.

#### Blacklisting settings

|   Property Name   |   Description     |
|:------------------|:------------------|
| `check.user.blacklisting` | Enable/disable user blacklisting. Default is **false**.
| `argus-pepd-endpoint`     | The complete service endpoint of Argus PEP server. **Mandatory if** `check.user.blacklisting` is true. <br/>Example: _https://argus-pep-host:8154/authz_

#### Proxy settings

|   Property Name   |   Description     |
|:------------------|:------------------|
| `security.enable.mapping`   | Flag to enable/disable DN-to-userid mapping via gridmap-file. Default is **false**.
| `security.enable.vomscheck` | Flag to enable/disable checking proxy VOMS credentials. Default is **true**.

#### General settings

|   Property Name   |   Description     |
|:------------------|:------------------|
| `wsdl.file`       | WSDL file, complete with path, to be returned in case of GET request.

### StoRM Backend service <a name="stormbackendservice">&nbsp;</a>

The Backend is the core of StoRM. It executes all SRM requests, interacts with other Grid service, with database to retrieve SRM requests, with file-system to set up space and file, etc. It has a modular architecture made by several internal components. The Backend needs to be configured for two main aspects:

- _service information_: this section contains all the parameter regarding the StoRM service details. It relies on the **storm.properties** configuration file.
- _storage information_: this section contains all the information regarding Storage Area and other storage details. It relies on the **namespace.xml** file.

Both `storm.properties` and `namespace.xml` configuration file default location is:

    /etc/storm/backend-server

The `storm.properties` configuration file contains a list of:

    key = value

pairs that represent all the information needed to configure the StoRM Backend service. The most important (and mandatory) parameters are configured by default trough YAIM with a standard installation of StoRM. All the other parameters are optionals and can be used to make advanced tuning of the Backend. To change/set a new value, or add a new parameter, just edit the *storm.properties* file and restart the Backend daemon. When the BackEnd starts, it writes into the log file the whole set of parameters read from the
configuration file.

The `namespace.xml` configuration file contains the storage area info like what is needed to perform the **mapping functionality**, the **storage area capabilities**, which are the **access and transfer protocols** supported, etc..

#### Service information

|   Property Name                               |   Description     |
|:----------------------------------------------|:------------------|
|   ```storm.service.SURL.endpoint```           |   List of comma separated strings identifying the StoRM Frontend endpoint(s). This is used by StoRM to understand if a SURL is local. E.g. *srm://storm.cnaf.infn.it:8444/srm/managerv2*. <br/> If you want to accept SURL with the ip address instead of the FQDN hostname you have to add the proper endpoint (E.g. IPv4: *srm://192.168.100.12:8444/srm/managerv2* or IPv6: *srm://[2001:0db8::1428:57ab]:8444/srm/managerv2*. Default value: **srm://```storm.service.FE-public.hostname```:8444/srm/managerv2**
|   ```storm.service.port```                    |   SRM service port. Default: **8444**
|   ```storm.service.SURL.default-ports```      |   List of comma separated valid SURL port numbers. Default: **8444**
|   ```storm.service.FE-public.hostname```      |   StoRM Frontend hostname in case of a single Frontend StoRM deployment, StoRM Frontends DNS alias in case of a multiple Frontends StoRM deployment.
|   ```storm.service.FE-list.hostnames```       |   Comma separated list os Frontend(s) hostname(s). Default: **localhost**
|   ```storm.service.FE-list.IPs```             |   Comma separated list os Frontend(s) IP(s). E.g. *131.154.5.127, 131.154.5.128*. Default: **127.0.0.1**
|   ```proxy.home```                            |   Directory used to contains delegated proxies used in case of *srmCopy* request. Please note that in case of clustered installation this directory have to be shared between the Backend and the Frontend(s) machines. Default: **/etc/storm/tmp**
|   ```pinLifetime.default```                   |   Default *PinLifetime* in seconds used for pinning files in case of *srmPrepareToPut* or *srmPrepareToGet* operation without any pinLifetime specified. Default: **259200**
|   ```pinLifetime.maximum```                   |   Maximum *PinLifetime* allowed in seconds.<br/>Default: **1814400**
|   ```fileLifetime.default```                  |   Default *FileLifetime* in seconds used for VOLATILE file in case of SRM request without *FileLifetime* parameter specified. Default: **3600**
|   ```extraslashes.gsiftp```                   |   Add extra slashes after the "authority" part of a TURL for gsiftp protocol.
|   ```extraslashes.rfio```                     |   Add extra slashes after the "authority" part of a TURL for rfio protocol.
|   ```extraslashes.root```                     |   Add extra slashes after the "authority" part of a TURL for root protocol.
|   ```extraslashes.file```                     |   Add extra slashes after the "authority" part of a TURL for file protocol.
|   ```synchcall.directoryManager.maxLsEntry``` |   Maximum number of entries returned by an *srmLs* call. Since in case of recursive *srmLs* results can be in order of million, this prevent a server overload. Default: **500**
|   ```directory.automatic-creation```          |   Flag to enable automatic missing directory creation upon *srmPrepareToPut* requests.<br/>Default: **false**
|   ```directory.writeperm```                   |   Flag to enable directory write permission setting upon *srmMkDir* requests on created directories. Default: **false**
|   ```default.overwrite```                     |   Default file overwrite mode to use upon *srmPrepareToPut* and *srmCopy* requests. Default: **A**. Possible values are: N, A, D. Please note that N stands for *Never*, A stands for *Always* and D stands for *When files differs*.
|   ```default.storagetype```                   |   Default File Storage Type to be used for *srmPrepareToPut* and *srmCopy* requests in case is not provided in the request. Default: **V**. Possible values are: V, P, D. Please note that V stands for *Volatile*, P stands for *Permanent* and D stands for *Durable*.

#### Requests garbage collector <a name="requestsgarbagecollector">&nbsp;</a>

The request garbage collector process cleans database from the expired asynchronous SRM requests. The value of `expired.request.time` defines how many seconds are necessary to a request, after its submission, to be considered expired. An appropriate tuning is needed in case of high throughput of SRM requests required for long time.

|   Property Name            |   Description     |
|:---------------------------|:------------------|
| `purging`                  |  Enable the request garbage collector. Default: **true**. Possible values are: true, false.
| `purge.interval`           |  Time interval in seconds between successive purging run. Default: **600**.
| `purge.size`               |  Number of requests picked up for cleaning from the requests garbage collector at each run. This value is use also by Tape Recall Garbage Collector. Default: **800**
| `purge.delay`              |  Initial delay before starting the requests garbage collection process, in seconds. Default: **10**
| `expired.request.time`     |  Time in seconds to consider a request expired after its submission. Default: **604800** seconds (1 week). From **StoRM 1.11.13** it is used also to identify how much time is needed to consider a completed recall task as cleanable.
| `expired.inprogress.time`  |  Time in seconds to consider an in-progress ptp request as expired. Default: **2592000** seconds (1 month)

#### Expired put requests agent

This agent:

* transits ongoing srmPtP to SRM_FILE_LIFETIME_EXPIRED if the request pin-lifetime is expired (see `pinLifetime.default` variable into Service Information section).
* transits to SRM_FAILURE the srmPtP that after `expired.inprogress.time` seconds are still in SRM_REQUEST_INPROGRESS.

The agent runs each `transit.interval` seconds and updates all the expired requests.

|   Property Name    |   Description     |
|:-------------------|:------------------|
| `transit.interval` |   Time interval in seconds between successive agent run. Default: **3000**.
| `transit.delay`    |   Initial delay before starting the agent process, in seconds. Default: **60**

#### Garbage collector

|   Property Name   |   Description     |
|:------------------|:------------------|
|   ```gc.pinnedfiles.cleaning.delay```     |   Initial delay before starting the reserved space, JIT ACLs and pinned files garbage collection process, in seconds. Default: **10**
|   ```gc.pinnedfiles.cleaning.interval```  |   Time interval in seconds between successive purging run. Default: **300**

#### Synchronous call

|   Property Name   |   Description     |
|:------------------|:------------------|
|   ```synchcall.xmlrpc.unsecureServerPort```   |   Port to listen on for incoming XML-RPC connections from Frontends(s). Default: **8080**
|   ```synchcall.xmlrpc.maxthread```            |   Number of threads managing XML-RPC connection from Frontends(s). A well sized value for this parameter have to be at least equal to the sum of the number of working threads in all FrontEend(s). Default: **100**
|   ```synchcall.xmlrpc.token.enabled```        |   Whether the backend will require a token to be present for accepting XML-RPC requests. Default: true
|   ```synchcall.xmlrpc.token```                |   The token that the backend will require to be present for accepting XML-RPC requests. Mandatory if synchcall.xmlrpc.token.enabled is true

#### REST interface parameters

|   Property Name   |   Description     |
|:------------------|:------------------|
|   ```storm.rest.services.port```  |   REST services port. Default: **9998**

#### Database connection parameters

|   Property Name   |   Description     |
|:------------------|:------------------|
|   ```com.mysql.jdbc.Driver``` |   JDBC driver to be used to connect with StoRM database. Default: **com.mysql.jdbc.Driver**
|   ```storm.service.request-db.protocol``` |   Protocol to be used to connect with StoRM database. Default: **jdbc:mysql://**
|   ```storm.service.request-db.host``` |   Host for StoRM database. Default: **localhost**
|   ```storm.service.request-db.db-name```  |   Database name for SRM requests. Default: **storm_db**
|   ```storm.service.request-db.username``` |   Username for database connection. Default: **storm**
|   ```storm.service.request-db.passwd```   |   Password for database connection
|   ```asynch.db.ReconnectPeriod``` |   Database connection refresh time intervall in seconds. Default: **18000**
|   ```asynch.db.DelayPeriod``` |   Database connection refresh initial delay in seconds. Default: **30**
|   ```persistence.internal-db.connection-pool```   |   Enable the database connection pool. Default: **false**
|   ```persistence.internal-db.connection-pool.maxActive```     |   Database connection pool max active connections. Default: **10**
|   ```persistence.internal-db.connection-pool.maxWait```   |   Database connection pool max wait time to provide a connection. Default: **50**

#### SRM Requests Picker

|   Property Name   |   Description     |
|:------------------|:------------------|
|   ```asynch.PickingInitialDelay```    |   Initial delay before starting to pick requests from the DB, in seconds. Default: **1**
|   ```asynch.PickingTimeInterval```    |   Polling interval in seconds to pick up new SRM requests. Default: **2**
|   ```asynch.PickingMaxBatchSize```    |   Maximum number of requests picked up at each polling time. Default: **100**
|   ```scheduler.serial```          |   **DEPRECATED** Flag to enable the execution of all the request on a single thread. Default: **false**

#### Worker threads

|   Property Name   |   Description     |
|:------------------|:------------------|
|   ```scheduler.crusher.workerCorePoolSize```  |   Crusher Scheduler worker pool base size. Default: **10**
|   ```scheduler.crusher.workerMaxPoolSize```       |   Crusher Schedule worker pool max size. Default: **50**
|   ```scheduler.crusher.queueSize```               |   Request queue maximum size.<br/>Default: **2000**
|   ```scheduler.chunksched.ptg.workerCorePoolSize```   |   *PrepareToGet* worker pool base size. Default: **50**
|   ```scheduler.chunksched.ptg.workerMaxPoolSize```    |   *PrepareToGet* worker pool max size. Default: **200**
|   ```scheduler.chunksched.ptg.queueSize```            |   *PrepareToGet* request queue maximum size. Default: **2000**
|   ```scheduler.chunksched.ptp.workerCorePoolSize```   |   *PrepareToPut* worker pool base size. Default: **50**
|   ```scheduler.chunksched.ptp.workerMaxPoolSize```    |   *PrepareToPut* worker pool max size. Default: **200**
|   ```scheduler.chunksched.ptp.queueSize```            |   *PrepareToPut* request queue maximum size. Default: **1000**
|   ```scheduler.chunksched.bol.workerCorePoolSize```   |   *BringOnline* worker pool base size. Default: **50**
|   ```scheduler.chunksched.bol.workerMaxPoolSize```    |   *BringOnline* Worker pool max size. Default: **200**
|   ```scheduler.chunksched.bol.queueSize```            |   *BringOnline* request queue maximum size. Default: **2000**
|   ```scheduler.chunksched.copy.workerCorePoolSize```  |   *Copy* worker pool base size. Default: **10**
|   ```scheduler.chunksched.copy.workerMaxPoolSize```   |   *Copy* worker pool max size. Default: **50**
|   ```scheduler.chunksched.copy.queueSize```           |   *Copy* request queue maximum size. Default: **500**

#### HTTP(S) protocol

|   Property Name   |   Description     |
|:------------------|:------------------|
|   ```gridhttps.enabled```     |   Flag to enable the support to HTTP and HTTPS protocols. Default: **false**

#### Protocol balancing

|   Property Name   |   Description     |
|:------------------|:------------------|
|   ```gridftp-pool.status-check.timeout``` |   Time in milliseconds after which the status of a GridFTP has to be verified. Default: **20000** (20 secs)

#### Tape recall

|   Property Name   |   Description     |
|:------------------|:------------------|
|   ```tape.support.enabled```  |   Flag to enable tape support. Default: **false**
|   ```tape.buffer.group.read```    |   System group to be assigned to files migrated from tape storage. Default: **storm-SA-read**
|   ```tape.buffer.group.write```   |   System group to be assigned to files migrated to tape storage. Default: **storm-SA-write**

#### Storage information: namespace.xml

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

```xml
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
            <fixed>permit-all</fixed>
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
```

***Attributes meaning***:

- ```<filesystem name="dteam-FS" fs_type="ext3">``` : The name is the element identifier. It identifies this Storage Area in the namespace domains. The *fs\_type* is the type of the filesystem the Storage Area is built on. Possible values are: *ext3*, *gpfs*. Please note that *ext3* stands for all generic POSIX filesystem (*ext3*, *Lustre*, etc.)
- ```<space-token-description>DTEAM_TOKEN</space-token-description>``` : Storage Area space token description.
- ```<root>/storage/dteam</root>``` : Physical root directory of the Storage Area on the file system.
- ```<filesystem-driver>it.grid.storm.filesystem.swig.posixfs</filesystem-driver>``` : Driver loaded by the Backend for filesystem interaction. This driver is used mainly to set up ACLs on space and files.
- ```<spacesystem-driver>it.grid.storm.filesystem.MockSpaceSystem</spacesystem-driver>``` Driver loaded by the Backend for filesystem interaction. This is driver is used to manage space allocation. (E.g. on GPFS it uses the _gpfs_prealloc()_ call).

> ***Storage Area properties***

```xml
    <properties>
        <RetentionPolicy>replica</RetentionPolicy>
        <AccessLatency>online</AccessLatency>
        <ExpirationMode>neverExpire</ExpirationMode>
        <TotalOnlineSize unit="GB" limited-size="true">291</TotalOnlineSize>
        <TotalNearlineSize unit="GB">0</TotalNearlineSize>
    </properties>
```

in details:

- ```<RetentionPolicy>replica</RetentionPolicy>``` : Retention Policy of the Storage Area. Possible values are: *replica*, *custodial*.
- ```<AccessLatency>online</AccessLatency>``` : Access Latency of the Storage Area. Possible values: *online*, *nearline*.
- ```<ExpirationMode>neverExpire</ExpirationMode>``` : Expiration Mode of the Storage Area. **Deprecated**.
- ```<TotalOnlineSize unit="GB" limited-size="true">291</TotalOnlineSize>``` Total on-line size of the Storage Area in GigaBytes. In case the attribute *limited-size*="true", StoRM enforce this limit at SRM level. When the space used for the Storage Area is at least equal to the size specified, every further SRM request to write files will fail with SRM_NO_FREE_SPACE error code.
- ```<TotalNearlineSize unit="GB">0</TotalNearlineSize>``` : Total near-line size of the Storage Area. This only means in case the Storage Area is in some way attached to a MSS storage system (such as TSM with GPFS).

> ***Storage area capabilities***:

```xml
    <aclMode>AoT</aclMode>
```

This is the ACL enforcing approach. Possible values are: *AoT*, *JiT*. In case of *AheadOfTime*(**AoT**) approach StoRM sets up a physical ACL on file and directories for the local group (*gid*) in which the user is mapped. (The mapping is done querying the LCMAPS service con the BE machine passing both user DN and FQANs). The group ACL remains for the whole lifetime of the file. In case of *JustInTime*(**JiT**) approach StoRM sets up and ACL for the local user (*uid*) the user is mapped. The ACL remains in place only for the lifetime of the SRM request, then StoRM removes it. (This is to avoid to grant access to pool account uid in case of reallocation on different users.)

```xml
    <default-acl>
        <acl-entry>
            <groupName>lhcb</groupName>
            <permissions>RW</permissions>
        </acl-entry>
    </default-acl>
```

This is the Default ACL list. A list of ACL entry (that specify a local user (*uid*) or group id (*gid*) and a permission (R,W,RW). This ACL are automatically by StoRM at each read or write request. Useful for use cases where experiment want to allow local access to file on group different than the one that made the SRM request operation.

> **_Access and Transfer protocol supported_**

The ```file``` protocol:

```xml
    <prot name="file">
        <schema>file</schema>
    </prot>
```

The **file** protocol means the capability to perform local access on file and directory. If user performs an SRM request (*srmPtG* or *srmPtP*) specifying the file protocol, and it is supported by the selected Storage Area, StoRM return a TURL structured as:

    file:///atlas/atlasmcdisk/filename

This TURL can be used through GFAL or other SRM clients to perform a direct access on the file.

```xml
    <prot name="gsiftp">
        <id>0</id>
        <schema>gsiftp</schema>
        <host>gridftp-dteam.cnaf.infn.it</host>
        <port>2811</port>
    </prot>
```

The ```gsiftp``` protocol:

The **gsiftp** protocol means the GridFTP transfer system from Globus widely adopted in many Grid environments. This capability element contains all the information about the GridFTP server to use with this Storage Area. Site administrator can decide to have different server (or pools of server) for different Storage Areas. The *id* is the server identifier to be used when defining a pool. The *schema* have to be gsiftp. *host* is the hostname of the server (or the DNS alias used to aggregate more than one server). The *port* is the GridFTP server port, typically 2811. If user performs an SRM request (*srmPtG* or *srmPtP*) specifying the *gsiftp* protocol, and it is supported by the selected Storage Area, StoRM return a TURL structured as:

    gsiftp://gridftp-dteam.cnaf.infn.it:2811/atlas/atlasmcdisk/filename.

The ```rfio``` protocol:

```xml
    <prot name="rfio">
        <schema>rfio</schema>
        <host>rfio-dteam.cnaf.infn.it</host>
        <port>5001</port>
    </prot>
```

This capability element contains all the information about the **rfio** server to use with this Storage Area. Like for GridFTP, site administrator can decide to have different server (or pools of server) for different Storage Areas. The *id* is the server identifier. The *schema* have to be rfio. *host* is the hostname of the server (or the DNS alias used to aggregate more than one server). The *port* is the rfio server port, typically 2811. If user performs an SRM request (*srmPtG* or *srmPtP*) specifying the rfio protocol, and it is supported by the selected Storage Area, StoRM return a TURL structured as:

    rfio://rfio-dteam.cnaf.infn.it:5001/atlas/atlasmcdisk/filename.

The ```root``` protocol:

```xml
    <prot name="root">
        <schema>root</schema>
        <host>root-dteam.cnaf.infn.it</host>
        <port>1094</port>
    </prot>
```

This capability element contains all the information about the **root** server to use with this Storage Area. Like for other protocols, site administrator can decide to have different server (or pools of server) for different Storage Areas. The *id* is the server identifier. The *schema* have to be root. *host* is the hostname of the server (or the DNS alias used to aggregate more than one server). The *port* is the root server port, typically 1094. If user performs an SRM request (*srmPtG* or *srmPtP*) specifying the root protocol, and it is supported by the selected Storage Area, StoRM return a TURL structured as:

    root://root-dteam.cnaf.infn.it:1094/atlas/atlasmcdisk/filename.

> ***Pool of protocol servers***

```xml
    <pool>
        <balance-strategy>round-robin</balance-strategy>
        <members>
            <member member-id="0"></member>
            <member member-id="1"></member>
        </members>
    </pool>
```

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

```xml
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
```

> ***Default values***

```xml
    <defaults-values>
        <space lifetime="86400" type="volatile" guarsize="291" totalsize="291"/>
        <file lifetime="3600" type="volatile"/>
    </defaults-values>
```

> ***Mapping rules***

A **mapping rule** define how a certain NS-Filesystem, that correspond to a Storage Area in SRM meaning of terms, is exposed in Grid:

```xml
    <mapping-rules>
        <map-rule name="dteam-maprule">
            <stfn-root>/dteam</stfn-root>
            <mapped-fs>dteam-FS</mapped-fs>
        </map-rule>
    </mapping-rules>
```

The ```<stfn-root>``` is the path used to build SURL referring to that Storage Area. The mapping rule above define that the NS-Filesystem named *dteam-FS* has to be mapped in the */dteam* SURL path. Following the NS-Filesystem element defined in the previous section, the SURL:

    srm://storm-fe.cr.cnaf.infn.it:8444/dteam/testfile

following the root expressed in the *dteam-FS* NF-Filesystem element, is mapped in the physical root path on the file system:

    /storage/dteam

This approach works similar to an alias, from the SURL *stfn-root* path to the NS-Filesystem root.

> ***Approachable rules***

**Approachable rules** defines which users (or which class of users) can approach a certain Storage Area, always expressed as NS-Filesystem element. If a user can approach a Storage Area, he can use it for all SRM operations. If a user is not allowed to approach a Storage Area, and he try to specify it in any SRM request, he will receive an SRM\_INVALID\_PATH. In practics, if a user cannot approach a Storage Area, for him that specific path does not exists at all.
Here is an example of approachable rule for the *dteam-FS* element:

```xml
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
```

- `<dn>*</dn>` means that everybody can access the storage Area. Here you can define regular expression on DN fields to define more complex approachable rules.

- `<vo-name>dteam</vo-name>` means that only users belonging to the VO dteam will be allowed to access the Storage Area. This entry can be a list of comma separeted VO-name.

#### Used space initialization

An administrator can initialize the status of a Storage Area by editing a configuration file, the `used-space.ini` configuration file, that it's parsed once at Backend's bootstrap time.
See [this configuration example][used-space-example] for more info.

### StoRM GridFTP service <a name="stormgridtfpservice">&nbsp;</a>

At each transfer request, the GridFTP uses LCMAPS to get user mapping and start
a new processes on behalf of the user to proceed with data transfer. GridFTP
relies on a different db file to get the plugin to use. Obviously LCMAPS has to
answer to GridFTP requests and StoRM requests in coeherent way. The GridFTP
uses the LCMAPS configuration file located at */etc/lcmaps/lcmaps.db*.

#### Enable checksum

To enable checksum calculation on the fly, the following parameter needs to be enabled into the GridFTP host's site-info.def:

```bash
GRIDFTP_WITH_DSI="yes"
```

If all works, during transfers you should see `-dsi StoRM -allowed-modules StoRM` as option for each globus-gridftp-server execution:

``` bash
$ ps -ax | grep globus-gridftp-server
/usr/sbin/globus-gridftp-server -p 2811 -d error,warn,info -l /var/log/storm/storm-gridftp-session.log -Z /var/log/storm/storm-globus-gridftp.log -dsi StoRM -allowed-modules StoRM -no-detach -config-base-path / -inetd -config-base-path /
```


#### IPC Channel

The IPC channel is used between a Globus GridFTP server head node and its
disk servers, e.g. for striped transfers (read more into
the [GridFTP System Administrator’s Guide][gridftp-admin-striped]).
In the default behavior of StoRM deployment the IPC channel is not used.
In fact, StoRM is mainly installed on a single host with one gridftp server
which read/write directly on disk.
In the cases it is a distributed deployment, there are usually n gridftp servers
which read/write data directly on disk, behind a haproxy or a dns for example,
so there are no separate frontends and one or more disk node servers.
However, it's important to know that **the IPC channel must be kept firewalled for any hosts outside the SE system**.

{% assign label_caption="Important" %}
{% include open_note.liquid %}
>**The IPC channel must be kept firewalled for any hosts outside the SE system**.

## Puppet Configuration <a name="puppetconfiguration">&nbsp;</a>

The [StoRM Puppet module](https://github.com/italiangrid/storm-puppet-module) allows administrators to 
easily configure StoRM services. Currently, the supported services are:

- StoRM WebDAV
- StoRM Globus GridFTP server

### StoRM WebDAV <a name="puppetwebdav">&nbsp;</a>

The main StoRM WebDAV configuration parameters are:

- `user_name`: the Unix user that runs storm-webdav service. Default: **storm**.
- `storage_areas`: the list of `Storm::Webdav::StorageArea` elements (more info below).
- `oauth_issuers`: the list of `Storm::Webdav::OAuthIssuer` elements that means the supported OAuth providers (more info below).
- `hostnames`: the list of hostname and aliases supported for Third-Party-Copy.
- `http_port` and `https_port`: the service ports. Default: **8085**, **8443**.
- `storage_root_dir`: the path of the storage areas root directory. Default: **/storage**.

Read more about StoRM WebDAV configuration parameters at the [online documentation](https://italiangrid.github.io/storm-puppet-module/puppet_classes/storm_3A_3Awebdav.html).

The [`Storm::Webdav::StorageArea`](https://italiangrid.github.io/storm-puppet-module/puppet_data_type_aliases/Storm_3A_3AWebdav_3A_3AStorageArea.html) type :

- `name`: The name of the storage area. Required.
- `root_path`: The path of the storage area root directory. Required.
- `access_points`: A list of logic path used to access storage area's root. Required.
- `vos`: A list of one or more Virtual Organization names of the users allowed to read/write into the storage area. Required.
- `orgs`: A list of one or more Organizations. Optional.
- `authenticated_read_enabled`: A boolean value used to enable the read of the storage area content to authenticated users. Required.
- `anonymous_read_enabled`: A boolean value used to enable anonymous read access to storage area content. Required.
- `vo_map_enabled`: A boolean value used to enable the use of the VO gridmap files. Required.
- `vo_map_grants_write_access`: A boolean value used to grant write access to the VO users read from grifmap file. Optional.

The [`Storm::Webdav::OAuthIssuer`](https://italiangrid.github.io/storm-puppet-module/puppet_data_type_aliases/Storm_3A_3AWebdav_3A_3AOAuthIssuer.html) type:

- `name`: the organization name. Required.
- `issuer`: the issuer URL. Required.

Example of usage:

```puppet
class { 'storm::webdav':
  storage_root_dir => '/storage',
  storage_areas => [
    {
      name                       => 'test.vo',
      root_path                  => '/storage/test.vo',
      access_points              => ['/test.vo'],
      vos                        => ['test.vo', 'test.vo.2'],
      authenticated_read_enabled => false,
      anonymous_read_enabled     => false,
      vo_map_enabled             => false,
    },
    {
      name                       => 'test.vo.2',
      root_path                  => '/storage/test.vo.2',
      access_points              => ['/test.vo.2'],
      vos                        => ['test.vo.2'],
      authenticated_read_enabled => false,
      anonymous_read_enabled     => false,
      vo_map_enabled             => false,
    },
  ],
  oauth_issuers => [
    {
      name   => 'indigo-dc',
      issuer => 'https://iam-test.indigo-datacloud.eu/',
    },
  ],
  hostnames => ['localhost', 'alias.for.localhost'],
}
```

### StoRM GridFTP <a name="puppetgridftp">&nbsp;</a>

The StoRM GridFTP configuration parameters are:

- `port`: the port used by GridFTP server service. Default: **2811**.
- `port_range`: the range of ports used by transfer sockets; format is 'MIN,MAX'. Default: **'20000,25000'**.
- `connections_max`: the number of max allowed connections to server. Default: **2000**.

Read more about StoRM GridFTP configuration parameters at the [online documentation](https://italiangrid.github.io/storm-puppet-module/puppet_classes/storm_3A_3Agridftp.html).

Example of usage:

```puppet
class { 'storm::gridftp':
  port            => 2811,
  port_range      => '20000,25000',
  connections_max => 2000,
}
```

## Logging <a name="logg">&nbsp;</a>

This section is dedicated to all the log information provided by all the StoRM services. By default, all the log files are saved into `/var/log/storm` directory.

### StoRM Frontend Logging <a name="stormfrontendlogging">&nbsp;</a>

The Frontend logs information on the service status and the SRM requests received and managed by the process. The Frontend's log supports different level of logging (ERROR, WARNING, INFO, DEBUG, DEBUG2) that can be set from the dedicated parameter in _storm-frontend-server.conf_ configuration file.
The Frontend log file named _storm-frontend-server.log_ is placed in the _/var/log/storm directory_. At start-up time, the FE prints here the whole set of configuration parameters, this can be useful to check desired values. When a new SRM request is managed, the FE logs information about the user (DN and FQANs) and the requested parameters.
At each SRM request, the FE logs also this important information:

    03/19 11:51:42 0x88d4ab8 main: AUDIT - Active tasks: 3
    03/19 11:51:42 0x88d4ab8 main: AUDIT - Pending tasks: 0

about the status of the worker pool threads and the pending process queue. _Active tasks_ is the number of worker threads actually running. _Pending tasks_ is the number of SRM requests queued in the worker pool queue. These data gives important information about the Frontend load.

#### monitoring.log

Monitoring service, if enabled, provides information about the operations
executed in a certain amount of time writing them on file
_/var/log/storm/monitoring.log_. This amount of time (called Monitoring Round)
is configurable via the configuration property monitoring.timeInterval; its
default value is 1 minute. At each Monitoring Round, a single row is printed on
log. This row reports both information about requests that have been performed
in the last Monitoring Round and information considering the whole FE execution
time (Aggregate Monitoring). Informations reported are generated from both
Synchronous and Asynchronous requests and tell the user:

- how many requests have been performed in the last Monitoring Round,
- how many of them were successful,
- how many failed,
- how many produced an error,
- the average execution time,
- the minimum execution time,
- the maximum execution time.

This row reports the **Monitoring Summary** and this is the default behavior of the monitoring service.

**_Example_**:

    03/20 14:19:11 : [# 22927 lifetime=95:33:18] S [OK:47,F:15,E:0,m:0.085,M:3.623,Avg:0.201] A [OK:16,F:0,E:0,m:0.082,M:0.415,Avg:0.136]
      Last:(S [OK:12,F:5,E:0,m:0.091,M:0.255] A [OK:6,F:0,E:0,m:0.121,M:0.415])

Furthermore it can be requested a more detailed Frontend Monitoring activity by setting the configuration property _monitoring.detailed_ to _true_. Doing this, at each Monitoring Round for each kind of SRM operation performed in the Monitoring Round (srmls, srmPtp, srmRm, ...) the following information are printed in a section with header "Last round details:":

- how many request succeeded,
- how many failed,
- how many produced an error,
- the average execution time,
- the minimum execution time,
- the maximum execution time,
- the execution time standard deviation.

This is called the **Detailed Monitoring Round**. After this, the Monitoring Summary is printed. Then, considering the whole Frontend execution time, in a section with header "Details:", a similar detailed summary is printed. This is called the **Aggregate Detailed Monitoring**.

**_Example_**:

    03/20 14:19:11 : Last round details:
    03/20 14:19:11 : [PTP] [OK:3,F:0,E:0,Avg:0.203,Std Dev:0.026,m:0.183,M:0.240]
    03/20 14:19:11 : [Put done] [OK:2,F:0,E:0,Avg:0.155,Std Dev:0.018,m:0.136,M:0.173]
    03/20 14:19:11 : [# 22927 lifetime=95:33:18] S [OK:47,F:15,E:0,m:0.085,M:3.623,Avg:0.201] A [OK:16,F:0,E:0,m:0.082,M:0.415,Avg:0.136]
      Last:(S [OK:12,F:5,E:0,m:0.091,M:0.255] A [OK:6,F:0,E:0,m:0.121,M:0.415])
    03/20 14:19:11 : Details:
    03/20 14:19:11 : [PTP] [OK:7,F:0,E:0,Avg:0.141,Std Dev:0.057,m:0.085,M:0.240]
    03/20 14:19:11 : [Put done] [OK:5,F:0,E:0,Avg:0.152,Std Dev:0.027,m:0.110,M:0.185]
    03/20 14:19:11 : [Release files] [OK:4,F:0,E:0,Avg:0.154,Std Dev:0.044,m:0.111,M:0.216]
    03/20 14:19:11 : [Rm] [OK:3,F:0,E:0,Avg:0.116,Std Dev:0.004,m:0.111,M:0.122]

**Note**:

- Operations not performed in current Monitoring Round are not printed in Detailed Monitoring Round.
- Operations never performed are not printed in Aggregate Detailed Monitoring.
- Operation performed in current Monitoring Round are aggregated in Aggregate Detailed Monitoring.

#### gSOAP tracefile

If you have problem at gSOAP level, and you have already looked at the troubleshooting section of the StoRM site without finding a solution, and you are brave enough, you could try to find some useful information on the gSOAP log file.
To enable gSOAP logging, set the following environment variables:

```bash
$ export CGSI_TRACE=1
$ export CGSI_TRACEFILE=/tmp/tracefile
```

and restart the Frontend daemon by calling directly the init script */etc/init.d/storm-frontend-server* and see if the error messages contained in */tmp/tracefile* could help. Please be very careful, it prints really a huge amount of information.

### StoRM Backend Logging <a name="stormbackendlogging">&nbsp;</a>

The Backend log files provide information on the execution process of all SRM requests. All the Backend log files are placed in the `/var/log/storm` directory. Backend logging is based on *logback* framework. Logback provides a way to set the level of verbosity depending on the use case. The level supported are FATAL, ERROR, INFO, WARN, DEBUG.

The file

    /etc/storm/backend-server/logging.xml

contains the following information:

```xml
<logger name="it.grid.storm" additivity="false">
    <level value="DEBUG" />
    <appender-ref ref="PROCESS" />
</logger>
```

the *value* can be set to the desired log level. Please be careful, because logging operations can impact on system performance (even 30% slower with DEBUG in the worst case). The suggest logging level for production endpoint is INFO. In case the log level is modified, the Backend has not to be restarted to read the new value.

StoRM Backend log files are the followings:

* `storm-backend.log`, the main log file with each single request and errors are logged;
* `heartbeat.log`, an aggregated log that shows the number of synch and asynch requests occoured from startup and on last minute;
* `storm-backend-metrics.log`, a finer grained monitoring of incoming synchronous requests, contains metrics for individual types of synchronous requests.   

#### storm-backend.log

The main Backend service log file is:

- `storm-backend.log`. All the information about the SRM execution process, error or warning are logged here depending on the log level. At startup time, the BE logs here all the storm.properties value, this can be useful to check value effectively used by the system. After that, the BE logs the result of the namespace initialization, reporting errors or misconfiguration. At the INFO level, the BE logs for each SRM operation at least who have request the operation (DN and FQANs), on which files (SURLs) and the operation result. At DEBUG level, much more information are printed regarding the status of many StoRM internal component, depending on the SRM request type. DEBUG level has to be used carefully only for troubleshooting operation. If ERROR or FATAL level are used, the only event logged in the file are due to error condition.

#### heartbeat.log

StoRM provides a bookkeeping framework that elaborates informations on SRM requests processed by the system to provide user-friendly aggregated data that can be used to get a quick view on system health.

- `heartbeat.log`. This useful file contains information on the SRM requests process by the system from its startup, adding new information at each beat. The beat time interval can be configured, by default is 60 seconds. At each beat, the heartbeat component logs an entry.

A `heartbeat.log` entry example:

```bash
    [#.....71 lifetime=1:10.01]
        Heap Free:59123488 SYNCH [500] ASynch [PTG:2450 PTP:3422]
        Last:( [#PTG=10 OK=10 M.Dur.=150] [#PTP=5 OK=5 M.Dur.=300] )
```

|   Log     |   Meaning     |
|:----------|:--------------|
| `#......71`            | Log entry number
| `lifetime=1:10.01`     | Lifetime from last startup, hh:mm:ss
| `Heap Free:59123488`   | BE Process free heap size in Bytes
| `SYNCH [500]`          | Number of Synchronous SRM requests executed in the last beat
| `ASynch [PTG:2450 PTP:3422]` | Number of _srmPrepareToGet_ and _srmPrepareToPut_ requests executed from start-up.
| `Last:( [#PTG=10 OK=10 M.Dur.=150]` | Number of _srmPrepareToGet_ executed in the last beat, with the number of request terminated with success (OK=10) and average time in millisecond (M.Dur.=150)
| `[#PTP=5 OK=5 M.Dur.=300]` | Number of srmPrepareToPut executed in the last beat, with number of request terminated with success and average time in milliseconds.

This log information can be really useful to gain a global view on the overall system status. A tail on this file is the first thing to do if you want to check the health of your StoRM installation. From here you can understand if the system is receiving SRM requests or if the system is overloaded by SRM request or if PtG and PtP are running without problem or if the interaction with the filesystem is exceptionally low (in case the M.Dur. is much more than usual).

#### storm-backend-metrics.log

A finer grained monitoring of incoming synchronous requests is provided by this log file. It contains metrics for individual types of synchronous requests.

A `storm-backend-metrics.log` entry example:

```bash
16:57:03.109 - synch.ls [(m1_count=286, count=21136) (max=123.98375399999999, min=4.299131, mean=9.130859862802883, p95=20.736006, p99=48.147704999999995) (m1_rate=4.469984951030006, mean_rate=0.07548032009470132)] duration_units=milliseconds, rate_units=events/second
```

|   Log                    |   Meaning           |
|:-------------------------|:--------------------|
| `synch.ls`               | Type of operation.
| `m1_count=286`           | Number of operation of the last minute.
| `count=21136`            | Number of operations from last startup.
| `max=123.98375399999999` | Maximum duration of last bunch.
| `min=4.299131`           | Minimum duration of last bunch.
| `mean=9.130859862802883` | Duration average of last bunch
| `p95=20.736006`          | The 95% of last bunch operations lasted less then 20.73ms
| `p99=48.147704999999995` | The 99% of last bunch operations lasted less then 48.14ms

Here is the list of current logged operations:

|   Operation          |   Description       |
|:---------------------|:--------------------|
| `synch`              | Synch operations summary
| `synch.af`           | Synch srmAbortFiles operations
| `synch.ar`           | Synch srmAbortRequest operations
| `synch.efl`          | Synch srmExtendFileLifetime operations
| `synch.gsm`          | Synch srmGetSpaceMetadata operations
| `synch.gst`          | Synch srmGetSpaceToken operations
| `synch.ls`           | Synch srmLs operations
| `synch.mkdir`        | Synch srmMkDir operations
| `synch.mv`           | Synch srmMv operations
| `synch.pd`           | Synch srmPd operations
| `synch.ping`         | Synch srmPing operations
| `synch.rf`           | Synch srmRf operations
| `synch.rm`           | Synch srmRm operations
| `synch.rmDir`        | Synch srmRmDir operations
| `fs.aclOp`           | Acl set/unset on filesystem operations
| `fs.fileAttributeOp` | File attribute set/unset on filesystem operations
| `ea`                 | Extended attributes operations


### StoRM WebDAV Logging <a name="stormwebdavlogging">&nbsp;</a>

The service logs live in the `/var/log/storm/webdav` directory.

- `storm-webdav-server.log` provides the main service log
- `storm-webdav-server-access.log` provides an http access log
- `storm-webdav-server-metrics.log` provides a metrics log, similar to the StoRM backend metrics.

### GridFTP Logging <a name="stormgridftplogging">&nbsp;</a>

GridFTP produce two separated log files:

- */var/log/storm/gridftp-session.log* for the command session information
- */var/log/storm/globus-gridftp.log* for the transfer logs

The logging level can be specified by editing the configuration file:

    /etc/globus-gridftp-server/gridftp.gfork

The supported logging levels are: ERROR, WARN, INFO, DUMP and ALL.

#### Redirect LCMAPS logging

Administrators can redirect the LCMAPS logging to a different log file than the one used by syslog by setting the `LLGT_LOG_FILE` environment variable.
As example, consider the following setup for the gridftp service:

    vim /etc/sysconfig/globus-gridftp

insert:

    export LLGT_LOG_FILE="/var/log/storm/storm-gridftp-lcmaps.log"

After restarting the service, all LCMAPS calls will be logged to the new file.


## Information service <a name="informationservice">&nbsp;</a>

StoRM Info Provider is the StoRM component that manages how and what
information are published on the BDII service.
To learn how to configure it refer to the [StoRM Info Provider administrator guide][dip-guide].

## CDMI StoRM <a name="cdmistorm">&nbsp;</a>

### Introduction <a name="cdmistormintro">&nbsp;</a>

CDMI StoRM is a plugin for the [INDIGO DataCloud CDMI server][indigo-cdmi-server] in order to support StoRM
as storage back-end and allow users to negotiate the Quality of Service of stored data through CDMI.

In short, through the CDMI server users/admins can:

- read the status of a resource (access latency, number of replicas, size, etc..);
- schedule, if allowed, a change of its related Quality of Service.

Currently, in StoRM use case, changing the QoS of a resource means recalling a file from tape.

{% assign image_src="cdmi-storm.png" %}
{% assign image_width="30%" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 5" %}
{% assign label_description="CDMI StoRM architecture overview." %}
{% include documentation/label.html %}

Refer to the [CDMI server user guide][indigo-cdmi-server-user-guide] for all the details about INDIGO DataCloud QoS management.

### Installation and Configuration <a name="cdmistorminstall">&nbsp;</a>

You can found CDMI StoRM details about installation and configuration [here][indigo-cdmi-deployment-guide].


[Scientific Linux]: http://www.scientificlinux.org
[SL6]: http://linuxsoft.cern.ch/scientific/6x/
[UMD-instructions]: http://repository.egi.eu/category/umd_releases/distribution/umd-4/
[how-to-nis]: http://www.tldp.org/HOWTO/NIS-HOWTO/index.html
[egi-instructions]: https://wiki.egi.eu/wiki/EGI_IGTF_Release#Using_YUM_package_management
[SPLguide]: https://twiki.cern.ch/twiki/bin/view/EGEE/SimplifiedPolicyLanguage
[pap_admin_CLI]: https://twiki.cern.ch/twiki/bin/view/EGEE/AuthZPAPCLI
[gridftp-admin-striped]: http://toolkit.globus.org/toolkit/docs/6.0/gridftp/admin/index.html#gridftp-admin-striped
[X509_SA_conf_example]: {{site.baseurl}}/documentation/how-to/storage-area-configuration-examples/1.11.3/index.html#sa-anonymous-rw-x509
[LDAPconfiguration]: {{site.baseurl}}/documentation/how-to/how-to-share-users-openldap/1.11.4/
[webdav-guide]: storm-webdav-guide.html
[tpc-guide]: tpc.html
[dip-guide]: storm-info-provider.html
[used-space-example]: {{site.baseurl}}/documentation/how-to/how-to-initialize-storage-area-used-space/
[releases]: {{site.baseurl}}/releases.html

[upgrade-15]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.16/#upgrading

[stable-storm-repoview]: https://repo.cloud.cnaf.infn.it/repository/storm/stable/el6/x86_64/repoview/index.html

[indigo-cdmi-spi]: https://github.com/indigo-dc/cdmi-spi
[indigo-cdmi-server]: https://github.com/indigo-dc/cdmi
[indigo-cdmi-server-user-guide]: https://indigo-dc.gitbooks.io/cdmi-qos/content/doc/api_walkthrough.html
[indigo-cdmi-deployment-guide]: https://github.com/italiangrid/cdmi-storm/blob/master/doc/admin.md
