---
layout: service-guide
title: StoRM System Administration Guide
redirect_from:
  - /documentation/sysadmin-guide/
---

# StoRM System Administration Guide

> version: 1.11.22

StoRM is a lightweight storage resource manager solution developed at [INFN][Home-INFN], which powers the Italian Tier-1 data center at [INFN-CNAF][Home-INFN-CNAF], as well as more than 30 other sites.

## Upgrading to StoRM 1.11.22 <a name="upgrading">&nbsp;</a>

In case you're updating from **StoRM v1.11.21**, the services that needs to be updated are:

* _storm-backend-server_
* _storm-webdav_
* _storm-dynamic-info-provider_
* _storm-native-libs_

```
yum update storm-backend-server storm-webdav storm-dynamic-info-provider storm-native-libs
```

> It's highly recommended to configure latest StoRM v1.11.22 release with latest StoRM Puppet module v4.0.0 ([post][puppet-post]) which requires a migration to Puppet 7.
> Read carefully the [CHANGELOG][storm-puppet-changelog-v4] because there are several deprecated parameters.

If you are upgrading from **StoRM v1.11.20** (or earlier versions) please follow
[these instructions][upgrade-20] before.

## Quick deployments

The following guides contains examples of a quick all-in-one deployment for the supported platforms.

* [All-in-one deployment on CentOS 7 with Puppet][quick-deployment-centos7]
* [StoRM WebDAV deployment on CentOS 7 with Puppet 7][quick-dav-deployment-centos7]


## System Requirements

All the StoRM components are certified to work on [CentOS 7][CentOS-org]. There are no specific minimum hardware requirements but it is advisable to have at least 4GB of RAM on Backend host.

### Java OpenJDK

Java components run with OpenJDK 11.

```
yum install java-11-openjdk
```

### Native Access Control List support

StoRM Backend uses the ACLs on files and directories to implement part of the security model. Then, ACLs must be enabled on the underlying file-system.

```
yum install acl
```

To enable ACLs (if needed), you must add the `acl` property to the relevant file system in your `/etc/fstab` file. For example:

```
/dev/hda3     /storage      ext3     defaults, acl     1 2
```

Then you need to remount the affected partitions

```
mount -o remount /storage
```

### Native Extendend Attributes support

StoRM Backend and StoRM WebDAV use the Extended Attributes (EA) on files to store some metadata related to the file (e.g. the checksum value); therefore in order to ensure a proper running, the EA support needs to be enabled on the underlying file system.

```
yum install attr
```

To enable EA (if needed) you must add the `user_xattr` property to the relevant file systems in your `/etc/fstab` file. For example:

```
/dev/hda3     /storage     ext3     defaults,acl,user_xattr     1 2
```

Then you need to remount the affected partitions

```
mount -o remount /storage
```

### Fully Qualified Domain Name

Hostname must be a _Fully Qualified Domain Name_ (FQDN).

Check if your hostname is a FQDN:

```
hostname -f
```

The command must return the host FQDN.

If you need to correct it and you are using bind or NIS for host lookups, you can change the FQDN and the DNS domain name, which is part of the FQDN, in the `/etc/hosts` file.

```
# Do not remove the following line, or various programs
# that require network functionality will fail.
127.0.0.1       MYHOSTNAME.MYDOMAIN MYHOSTNAME localhost.localdomain localhost
::1             localhost6.localdomain6 localhost6
```

Set your own MYHOSTNAME and MYDOMAIN and restart the network service:

```
service network restart
```

### Host credentials

Hosts participating to the StoRM-SE which run services such as StoRM Frontend, StoRM Backend, StoRM WebDAV or StoRM Globus GridFTP must be configured with X.509 certificates signed by a trusted Certification Authority (CA).

Usually, the `hostcert.pem` and `hostkey.pem` certificate and private key are located in the `/etc/grid-security` directory. They must have permission `0644` and `0400` respectively:

```
ls -l /etc/grid-security/hostkey.pem
-r-------- 1 root root 887 Mar  1 17:08 /etc/grid-security/hostkey.pem

ls -l /etc/grid-security/hostcert.pem
-rw-r--r-- 1 root root 1440 Mar  1 17:08 /etc/grid-security/hostcert.pem
```

Check if your certificate is expired as follow:

```
openssl x509 -checkend 0 -in /etc/grid-security/hostcert.pem
```

To change permissions, if necessary:

```
chmod 0400 /etc/grid-security/hostkey.pem
chmod 0644 /etc/grid-security/hostcert.pem
```

### NTP service

NTP service must be installed and running.

```
yum install ntp
systemctl enable ntpd
systemctl start ntpd
```

## Getting started

### Install StoRM stable repository

The latest certified StoRM packages can be installed from StoRM production repository:

- [Browse RHEL7 packages][StoRM-stable-browse-rhel7]

To install StoRM stable repository, run the following command (as root):

```shell
yum-config-manager --add-repo https://repo.cloud.cnaf.infn.it/repository/storm/storm-stable-centos7.repo
```

Alternatively to StoRM stable, also UMD4 repository can be used to install the released StoRM packages.

### Install external repositories

StoRM components require also the following repositories to be installed:

* EPEL
* UMD4
* EGI Trust Anchors

#### EPEL repositories

StoRM depends on [EPEL][epel-repo] repositories.

Install them as follows:

```shell
yum install epel-release
```

#### UMD repositories

StoRM depends also on [UMD][UMD-instructions] repositories.

Install them as follows:

```shell
yum install umd-release
```

#### EGI Trust Anchors Repository

Install *EGI Trust Anchors repository* by following [EGI instructions][egi-instructions].

In short:

```shell
wget http://repository.egi.eu/sw/production/cas/1/current/repo-files/EGI-trustanchors.repo -O /etc/yum.repos.d/EGI-trustanchors.repo
yum install ca-policy-egi-core
```

The *DAG repository* must be disabled. If needed, set to 0 the enabled property in your */etc/yum.repos.d/dag.repo* file.


### Create StoRM user

StoRM components such as Frontend, Backend and WebDAV, run by default as user **storm**. This user is created by StoRM rpms but it's a good practice to initialize it before.

You can use the following commands to create the StoRM user on the machines
where you are deploying the services:

```shell
useradd -M storm
```

The option ```-M``` means 'without an home directory'.
You could also use specific user and group IDs as follows:

```shell
useradd -M storm -u 1234 -g 1234
```

Keep UIDs and GIDs aligned for StoRM users and groups on distributed deployments (i.e. when the services are installed on different machines).

#### File limits

It's recommended to raise the number of open files for `storm` user.
Put these settings in `/etc/security/limits.conf` or in a file contained in the `/etc/security/limits.d` directory (recommended):

```
storm hard nofile 65535
storm soft nofile 65535
```

Edit the total amount of opened files as your needed.

#### Storage Area's permissions

All the Storage Areas managed by StoRM needs to be owned by `storm` user.
This means that, for example, the storage-area *test* root directory permissions 
must be:

```shell
drwxr-x---+  2 storm storm
```

The site administrator has to take care of it. To set the correct permissions
on a storage area, you can launch the following commands
(assuming that storm runs as user *storm*, which is the default):

```shell
chown -RL storm:storm <sa-root-directory>
chmod -R 750 <sa-root-directory>
```

Site administrator must also make traversable by other users the parent
directories of each storage-area root directory (that's usually the same
directory for all the storage-areas):

```shell
chmod o+x <sa-root-directory-parent>
```

### Create local pool-accounts

StoRM Backend and Globus GridFTP map the authenticated users to a local account through [LCMAPS][lcmaps-home] service.

You can create your local accounts in several ways. Here we describe how to initialise them with YAIM and with an experimental Puppet module.

#### Create pool-accounts with YAIM

In order to create your local pool accounts with YAIM, you need to define a `users.conf` file and assign its absolute path to the YAIM variable `USERS_CONF`.

The file *users.conf* contains the list of Linux users (pool accounts) to be created. It's a plain list of the users and their IDs. An example of this configuration file can be found into:

```bash
/opt/glite/yaim/examples/users.conf
```

More details can be found in the [User configuration section in the YAIM guide][YAIM-users-configuration].

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

**Example**

We want to create the following pool accounts:
- a pool account of 100 users with group name `testvo` and `test.vo` as VO name
- a pool account of 30 production users with group name `testvoprd`, `test.vo` as VO name and `prd` as FLAG.

> This example is done with YAIM, therefore we need to install it    in case:
> ```
> yum install -y glite-yaim-core attr
> ```

In order to do this we have to create the following file:

* `storm-users.conf`

This file contains the definition of all the users (with their relative groups):

```bash
71001:tstvo001:7100:testvo:test.vo::
71002:tstvo002:7100:testvo:test.vo::
71003:tstvo003:7100:testvo:test.vo::
71004:tstvo004:7100:testvo:test.vo::
...
71100:tstvo100:7100:testvo:test.vo::
71101:testvoprd001:7170,7100:testvoprd,testvo:test.vo:prd:
71102:testvoprd002:7170,7100:testvoprd,testvo:test.vo:prd:
71103:testvoprd003:7170,7100:testvoprd,testvo:test.vo:prd:
71104:testvoprd004:7170,7100:testvoprd,testvo:test.vo:prd:
...
71129:testvoprd029:7170,7100:testvoprd,testvo:test.vo:prd:
71130:testvoprd030:7170,7100:testvoprd,testvo:test.vo:prd:
```

To apply the changes, define YAIM's variables into a site.def configuration file:

```bash
USERS_CONF=/path/to/storm-users.conf
VOS=test.vo
```

You need a proper YAIM profile `/opt/glite/yaim/node-info.d/se_config_users` defined as follows:

```
se_config_users_FUNCTIONS="
config_users
"
```

The only function used in this profile is the one that creates the users.
Then, you can run yaim configuration as follow:

```bash
/opt/glite/yaim/bin/yaim \
    -c -s site.def \
    -n se_config_users
```

#### Create pool-accounts with Puppet

An experimental Puppet module has been developed by CNAF Software Development team: `cnafsd-lcmaps` [link][cnafsd-lcmaps].
This module:
- installs and configures LCMAPS
- create local pool accounts

This module is available on puppet forge:

```
puppet module install cnafsd-lcmaps
```

**Example:**

Use this module as follow to install LCMAPS:

```puppet
include lcmaps
```

If you want to also define your own pool-account use:

```puppet
class { 'lcmaps':
  pools => [
    {
      'name' => 'poolname',
      'size' => 200,
      'base_uid' => 1000,
      'group' => 'poolgroup',
      'gid' => 1000,
      'vo' => 'poolVO',
    },
  ],
}
```

Pool accounts mandatory data:

* `name`, the name of the pool;
* `size`, the size of pool;
* `base_uid`, the first uid of the generated accounts;
* `group`, the name of the promary group of each account;
* `gid`, the group id of the primary group;
* `vo`, the VO name.

Optional parameters:

* `groups`, non primary groups for each account;
* `role`, the VOMS role (if not defined is NULL);
* `capability`, the VOMS capability (if not defined is NULL).


### Install the supported VOs

The Virtual Organizations your StoRM deployment has to support, must be installed on node. This means that, for each VO server, you need to define:

- `/etc/grid-security/vomsdir/{VO-NAME}/${VO-SERVER-HOST}.lsc`

Read more at [Configuring VOMS trust anchors][VOMS-trust-conf] within VOMS clients guide.

We developed a simple Puppet module `cnafsd-voms` in order to easy install/configure specific VOs on your node.

This module is available on puppet forge:

```
puppet module install cnafsd-voms
```

You can use this module as follow:

```
include voms::dteam
```

The built-in VO already configured are: `alice`, `atlas`, `cms`, `dteam`, `escape`, `lhcb`, `ops`, `wlcg`.

Or you can define another custom VO as follows:

```
voms::vo { 'test.vo':
  servers => [
    {
      server => 'vgrid02.cnaf.infn.it',
      port   => 15000,
      dn     => '/DC=org/DC=terena/DC=tcs/C=IT/ST=Lazio/L=Frascati/O=Istituto Nazionale di Fisica Nucleare/CN=vgrid02.cnaf.infn.it',
      ca_dn  => '/C=NL/ST=Noord-Holland/L=Amsterdam/O=TERENA/CN=TERENA eScience SSL CA 3',
    },
  ],
}
```

## Installation and configuration guides

Once you have satisfied the above prerequisites, we can procede with the StoRM components installation and configuration.

## StoRM Backend

The StoRM Backend service is the core of the StoRM SRM service. It executes all SRM functionalities and takes care of file and space metadata management. It also enforces authorization permissions on files and interacts with external Grid services.

### Installation

Grab the latest package from the StoRM repository. See instructions [here][download-page].

Install the service as follows:

```bash
yum install storm-backend-server
```

The installation from metapackage is deprecated since StoRM v1.11.22 release:

```bash
yum install storm-backend-mp
```

### Configuration

StoRM Backend configuration mainly relies on two files:

* `/etc/storm/backend-server/storm.properties`

    This file contains a list of key-value pairs that represent all the information needed to configure the service. As soon as service starts, it dumps into the log file the whole set of parameters read from the configuration file. Read the [full StoRM properties reference][storm-properties-ref].

* `/etc/storm/backend-server/namespace.xml`

    This file contains the storage areas details needed to perform the **mapping functionality** and the **storage area capabilities** which are the **access and transfer protocols** supported, etc. This file is auto-generated by the configuration tools (Puppet or YAIM before) but you can find more information about its structure and content [here][storm-namespace-ref]


#### Used space initialization

An administrator can initialize the status of a Storage Area by editing a configuration file, the `used-space.ini` configuration file, that it's parsed once at Backend's bootstrap time.
See [this configuration example][used-space-example] for more info.


### Puppet example

The [StoRM puppet module][storm-puppet] can be used to configure the service on **CentOS 7 platform**. 

The module contains the `storm::backend` class that installs the metapackage _storm-backend-mp_ and allows site administrator to configure _storm-backend-server_ service.

> **Prerequisites**: A MySQL or MariaDB server with StoRM databases must exist. Databases can be empty. If you want to use this module to install MySQL client and server and init databases, please read about next section about StoRM database utility class.

The Backend class installs:

- _storm-backend-server_
- _storm-dynamic-info-provider_

Then, the Backend class configures _storm-backend-server_ service by managing the following files:

- /etc/storm/backend-server/storm.properties
- /etc/storm/backend-server/namespace.xml
- /etc/systemd/system/storm-backend-server.service.d/storm-backend-server.conf
- /etc/systemd/system/storm-backend-server.service.d/filelimit.conf

and deploys StoRM databases. In addiction, this class configures and run StoRM Info Provider by managing the following file:

- /etc/storm/info-provider/storm-yaim-variables.conf

The whole list of StoRM Backend class parameters can be found [here][storm-puppet-backend].

Example of StoRM Backend configuration:

```puppet
class { 'storm::backend':
  # default value for all the storage areas
  transfer_protocols    => ['file', 'gsiftp', 'webdav'],
  xmlrpc_security_token => 'NS4kYAZuR65XJCq',
  # the first SRM endpoint is the one published on BDII
  srm_pool_members      => [
    {
      'hostname' => 'frontend.test.example',
    }
  ],
  # all the GFTP endpoints are published on BDII
  gsiftp_pool_members   => [
    {
      'hostname' => 'gridftp.test.example',
    },
  ],
  # all the DAV endpoints are published on BDII
  webdav_pool_members   => [
    {
      'hostname' => 'webdav.test.example',
    },
  ],
  storage_areas         => [
    {
      'name'          => 'dteam-disk',
      'root_path'     => '/storage/disk',
      'access_points' => ['/disk'],
      'vos'           => ['dteam'],
      'online_size'   => 40,
    },
    {
      'name'          => 'dteam-tape',
      'root_path'     => '/storage/tape',
      'access_points' => ['/tape'],
      'vos'           => ['dteam'],
      'online_size'   => 40,
      'nearline_size' => 80,
      'fs_type'       => 'gpfs',
      'storage_class' => 'T1D0',
    },
  ],
}
```

Starting from Puppet module v2.0.0, the management of Storage Site Report has been improved.
Site administrators can add script and cron described in the [how-to][how-to-publish-json-report] using a defined type `storm::backend::storage_site_report`.
For example:

```puppet
storm::backend::storage_site_report { 'storage-site-report':
  report_path => '/storage/info/report.json', # the internal storage area path
  minute      => '*/20', # set cron's minute
}
```

#### StoRM database class

The StoRM database utility class installs _mariadb_ server and releated rpms and configures _mysql_ service by managing the following files:

- /etc/my.cnf.d/server.cnf;
- /etc/systemd/system/mariadb.service.d/limits.conf.

The whole list of StoRM Database class parameters can be found [here][storm-puppet-db].

Examples of StoRM Database usage:

```puppet
class { 'storm::db':
  root_password => 'supersupersecretword',
  storm_password => 'supersecretword',
}
```

### Service logs

The Backend log files provide information on the execution process of all SRM requests. All the Backend log files are placed in the _/var/log/storm_ directory. Backend logging is based on *logback* framework. Logback provides a way to set the level of verbosity depending on the use case. The level supported are FATAL, ERROR, INFO, WARN, DEBUG.

The file

```
/etc/storm/backend-server/logging.xml
```

contains the following information:

```xml
<logger name="it.grid.storm" additivity="false">
    <level value="INFO" />
    <appender-ref ref="PROCESS" />
</logger>
```

the *value* can be set to the desired log level. Please be careful, because logging operations can impact on system performance (even 30% slower with DEBUG in the worst case). The suggest logging level for production endpoint is INFO. In case the log level is modified, the Backend has not to be restarted to read the new value.

StoRM Backend log files are the followings:

* **storm-backend.log**. This is the main log file with each single request and errors are logged. All the information about the SRM execution process, error or warning are logged here depending on the log level. At startup time, the BE logs here all the storm.properties value, this can be useful to check value effectively used by the system. After that, the BE logs the result of the namespace initialization, reporting errors or misconfiguration. At the INFO level, the BE logs for each SRM operation at least who have request the operation (DN and FQANs), on which files (SURLs) and the operation result. At DEBUG level, much more information are printed regarding the status of many StoRM internal component, depending on the SRM request type. DEBUG level has to be used carefully only for troubleshooting operation. If ERROR or FATAL level are used, the only event logged in the file are due to error condition.

* **heartbeat.log**. This is an aggregated log that shows the number of synch and asynch requests occoured from startup and on last minute. This useful file contains information on the SRM requests process by the system from its startup, adding new information at each beat. The beat time interval can be configured, by default is 60 seconds. At each beat, the heartbeat component logs an entry.

    A _heartbeat.log_ entry example:

    ```
    [#.....71 lifetime=1:10.01] Heap Free:59123488 SYNCH [500] ASynch [PTG:2450 PTP:3422] Last:( [#PTG=10 OK=10 M.Dur.=150] [#PTP=5 OK=5 M.Dur.=300] )
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

* **storm-backend-metrics.log**. A finer grained monitoring of incoming synchronous requests, contains metrics for individual types of synchronous requests.

    A _storm-backend-metrics.log_ entry example:

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







## StoRM Frontend

The StoRM Frontend service provides a SRM interface for storage management 
and data access supporting VOMS authentication and authorization mechanisms.

### Installation

Grab the latest package from the StoRM repository. See instructions
[here][download-page].

Install the metapackage:

```bash
yum install storm-frontend-mp
```

### Configuration

The Frontend component relies on a single configuration file that contains all the configurable parameters. This file is:

```
/etc/storm/frontend-server/storm-frontend-server.conf
```

containing a list of key-value pairs that can be used to configure the Frontend server. In case a parameter is modified, the Frontend service has to be restarted in order to read the new value.

Read the [full StoRM Frontend configuration reference][storm-frontend-conf-ref].

### Puppet example

The [StoRM puppet module][storm-puppet] can be used to configure the service on **CentOS 7 platform**. 

The module contains the `storm::frontend` class that installs the metapackage _storm-frontend-mp_ and allows site administrator to configure _storm-frontend-server_ service by managing the following files:

- /etc/storm/frontend-server/storm-frontend-server.conf
- /etc/sysconfig/storm-frontend-server

The whole list of StoRM Frontend class parameters can be found [here][storm-puppet-frontend].

Example of StoRM Frontend configuration done through `storm::frontend` class:

```puppet
class { 'storm::frontend':
  be_xmlrpc_host  => 'backend.test.example',
  be_xmlrpc_token => 'NS4kYAZuR65XJCq',
  db_host         => 'backend.test.example',
  db_user         => 'storm',
  db_passwd       => 'storm',
}
```

### Service logs

The Frontend logs information on the service status and the SRM requests received and managed by the process. The Frontend's log supports different level of logging (ERROR, WARNING, INFO, DEBUG, DEBUG2) that can be set from the dedicated parameter in _storm-frontend-server.conf_ configuration file.
The Frontend log file named _storm-frontend-server.log_ is placed in the _/var/log/storm directory_. At start-up time, the FE prints here the whole set of configuration parameters, this can be useful to check desired values. When a new SRM request is managed, the FE logs information about the user (DN and FQANs) and the requested parameters.
At each SRM request, the FE logs also this important information:

```shell
03/19 11:51:42 0x88d4ab8 main: AUDIT - Active tasks: 3
03/19 11:51:42 0x88d4ab8 main: AUDIT - Pending tasks: 0
```

about the status of the worker pool threads and the pending process queue. _Active tasks_ is the number of worker threads actually running. _Pending tasks_ is the number of SRM requests queued in the worker pool queue. These data gives important information about the Frontend load.

**Monitoring logs**

Monitoring service, if enabled, provides information about the operations executed in a certain amount of time writing them on file _/var/log/storm/monitoring.log_. This amount of time (called Monitoring Round) is configurable via the configuration property ```monitoring.timeInterval```; its default value is 1 minute. At each Monitoring Round, a single row is printed on
log. This row reports both information about requests that have been performed in the last Monitoring Round and information considering the whole FE execution time (Aggregate Monitoring). Informations reported are generated from both Synchronous and Asynchronous requests and tell the user:

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

Furthermore it can be requested a more detailed Frontend Monitoring activity by setting the configuration property ```monitoring.detailed``` to _true_. Doing this, at each Monitoring Round for each kind of SRM operation performed in the Monitoring Round (srmls, srmPtp, srmRm, ...) the following information are printed in a section with header "_Last round details:_":

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

### gSOAP tracefile logs

If you have problem at gSOAP level, and you have already looked at the troubleshooting section of the StoRM site without finding a solution, and you are brave enough, you could try to find some useful information on the gSOAP log file.
To enable gSOAP logging, set the following environment variables:

```bash
$ export CGSI_TRACE=1
$ export CGSI_TRACEFILE=/tmp/tracefile
```

and restart the Frontend daemon by calling directly the init script */etc/init.d/storm-frontend-server* and see if the error messages contained in */tmp/tracefile* could help. Please be very careful, it prints really a huge amount of information.


## StoRM WebDAV

### Installation

Grab the latest package from the StoRM repository. See instructions
[here][download-page].

Install the service package:

```bash
yum install storm-webdav
```

Start the service with:

```bash
$ systemctl start storm-webdav
```

Stop the service with:

```bash
$ systemctl stop storm-webdav
```

Check the service status with:

```bash
$ systemctl status storm-webdav
```

StoRM WebDAV has an health endpoint that can be used to grab its status:

```bash
$ curl http://storm-webdav:8085/actuator/health -s | jq
{
  "status": "UP"
}
```

Also the service metrics can be accessed at the following URL:

```bash
$ curl http://storm-webdav:8085/status/metrics?pretty=true -s | jq
{
  "version" : "4.0.0",
  "gauges" : {
    "jvm.gc.G1-Old-Generation.count" : {
      "value" : 0
    },
    "jvm.gc.G1-Old-Generation.time" : {
      "value" : 0
    }
    ...
}
```

**Access points:**

A storage area named `sa` is accessible by default at the URL
`https://hostname:8443/sa`.

If the service is configured such to allow anonymous access, the default URL
is `http://hostname:8085/sa`.


### Configuration

The storm-webdav service configuration lives in the `/etc/systemd/system/storm-webdav.service.d/storm-webdav.conf` file. 
Typically the configuration works out of the box, but changes are required, for instance, to
enable third-party transfer support.

Read the [full StoRM WebDAV configuration reference][storm-webdav-conf-ref].

### Puppet example

The [StoRM puppet module][storm-puppet] can be used to configure the service on CENTOS 7.

We recommend to use directly the StoRM WebDAV YAML configuration to tune your
deployment configuration, instead of using variables defined at the puppet
level, i.e.:

```puppet
# Install service and configure enviroment variables
class { 'storm::webdav':
  hostnames => ['storm-webdav.test.example'],
}
# Configure your application.yml
storm::webdav::application_file { 'application.yml':
  source => '/path/to/the/application.yml',
}
# Storage Area configuration files (one for each storage area)
storm::webdav::storage_area_file { 'test.vo.properties':
  source => '/path/to/the/test.vo.properties',
}
storm::webdav::storage_area_file { 'test.vo.2.properties':
  source => '/path/to/the/test.vo.2.properties',
}
```

### Service logs

The service logs live in the `/var/log/storm/webdav` directory.

Here the following logs are saved:

- `storm-webdav-server.log`, which provides the main service log;
- `storm-webdav-server-access.log`, which provides the http access log.



## StoRM Globus GridFTP DSI plugin

StoRM GridFTP DSI is a plugin of Globus GridFTP that computes the ADLER32 checksum on incoming file transfers and stores this value on an extended attribute of the file.

### Installation

Grab the latest package from the StoRM repository. See instructions
[here][download-page].

Install the metapackage:

```bash
yum install storm-globus-gridftp-mp
```

### Configuration

The Globus Gridftp server configuration relies on a single file that contains all the configurable parameters. This file is:

```
/etc/grid-security/gridftp.conf
```

It contains a list of key-value (space separated) pairs that can be used to configure the server and the loading of StoRM GriDFTP DSI plugin. The full list of supported parameters can be read from Globus GridFTP documentation [here](https://gridcf.org/gct-docs/6.2/gridftp/admin/index.html#gridftp-configuring).

The mandatory properties to be set are:

|   Property Name    |   Description     |
|:-------------------|:------------------|
| `load_dsi_module` | With value "StoRM" it loads StoRM DSI module.
| `allowed_modules` | With value "StoRM" it allows StoRM DSI module.


At each transfer request, StoRM Globus GridFTP uses LCMAPS to get user mapping and start a new processes on behalf of the user to proceed with data transfer. GridFTP relies on a different db file to get the plugin to use. Obviously LCMAPS has to answer to GridFTP requests and StoRM requests in coeherent way. The GridFTP uses the LCMAPS configuration file located at:

```
/etc/lcmaps/lcmaps.db
```

**IPC Channel**

The IPC channel is used between a Globus GridFTP server head node and its disk servers, e.g. for striped transfers (read more into the [GridFTP System Administrator’s Guide][gridftp-admin-striped]).
In the default behavior of StoRM deployment the IPC channel is not used.
In fact, StoRM is mainly installed on a single host with one gridftp server which read/write directly on disk.
In the cases it is a distributed deployment, there are usually n gridftp servers which read/write data directly on disk, behind a haproxy or a dns for example, so there are no separate frontends and one or more disk node servers.
However, it's important to know that **the IPC channel must be kept firewalled for any hosts outside the SE system**.

> **The IPC channel must be kept firewalled for any hosts outside the SE system**.

### Puppet example

The [StoRM puppet module][storm-puppet] can be used to configure the service on **CentOS 7 platform**. 

The module contains the `storm::gridftp` class that installs the metapackage _storm-globus-gridftp-mp_ and allows site administrator to configure _storm-globus-gridftp_ service by managing the following files:

- `/etc/grid-security/gridftp.conf`, the main configuration file;
- `/etc/sysconfig/storm-globus-gridftp`, with some environment variables.

The whole list of StoRM GridFTP class parameters can be found [here][storm-puppet-gridftp].

Examples of StoRM Gridftp configuration:

```puppet
class { 'storm::gridftp':
  redirect_lcmaps_log => true,
  llgt_log_file       => '/var/log/storm/storm-gridftp-lcmaps.log',
}
```

### Service logs

GridFTP produce two separated log files:

- */var/log/storm/gridftp-session.log* for the command session information
- */var/log/storm/globus-gridftp.log* for the transfer logs

The logging level can be specified by editing the configuration file:

    /etc/globus-gridftp-server/gridftp.gfork

The supported logging levels are: ERROR, WARN, INFO, DUMP and ALL.

**LCMAPS logging**

Administrators can redirect the LCMAPS logging to a different log file than the one used by syslog by setting the `LLGT_LOG_FILE` environment variable.
As example, consider the following setup for the gridftp service:

```
vim /etc/sysconfig/globus-gridftp
```

insert:

```
export LLGT_LOG_FILE="/var/log/storm/storm-gridftp-lcmaps.log"
```

After restarting the service, all LCMAPS calls will be logged to the new file.



[upgrade-20]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.21/upgrading/
[puppet-post]: {{site.baseurl}}/2023/06/21/StoRM-Puppet-module-major-release.html

[quick-deployment-centos7]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.22/quick-deployments/centos7/
[quick-dav-deployment-centos7]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.22/quick-deployments/webdav/

[Home-INFN]: https://home.infn.it/it/
[Home-INFN-CNAF]: https://www.cnaf.infn.it/
[CentOS-org]: https://www.centos.org/
[StoRM-stable-browse-rhel7]: https://repo.cloud.cnaf.infn.it/service/rest/repository/browse/storm-rpm-stable/centos7/
[epel-repo]: https://fedoraproject.org/wiki/EPEL/it
[UMD-instructions]: http://repository.egi.eu/category/umd_releases/distribution/umd-4/
[egi-instructions]: https://wiki.egi.eu/wiki/EGI_IGTF_Release#Using_YUM_package_management
[lcmaps-home]: https://wiki.nikhef.nl/grid/LCMAPS
[YAIM-users-configuration]: https://twiki.cern.ch/twiki/bin/view/LCG/YaimGuide400#User_configuration_in_YAIM
[cnafsd-lcmaps]: https://forge.puppet.com/modules/cnafsd/lcmaps/readme
[VOMS-trust-conf]: https://italiangrid.github.io/voms/documentation/voms-clients-guide/3.0.4/#voms-trust
[download-page]: {{site.baseurl}}/download.html
[how-to-publish-json-report]: http://italiangrid.github.io/storm/documentation/how-to/how-to-publish-json-report
[storm-properties-ref]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.22/references/storm-properties
[storm-namespace-ref]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.22/references/storm-namespace-conf
[storm-frontend-conf-ref]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.22/references/storm-frontend-server
[storm-webdav-conf-ref]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.22/references/storm-webdav-conf
[storm-puppet]: https://forge.puppet.com/cnafsd/storm
[storm-puppet-changelog-v4]: https://forge.puppet.com/modules/cnafsd/storm/changelog#400
[storm-puppet-backend]: https://italiangrid.github.io/storm-puppet-module/puppet_classes/storm_3A_3Abackend.html
[storm-puppet-frontend]: https://italiangrid.github.io/storm-puppet-module/puppet_classes/storm_3A_3Afrontend.html
[storm-puppet-gridftp]: https://italiangrid.github.io/storm-puppet-module/puppet_classes/storm_3A_3Agridftp.html
[storm-puppet-db]: https://italiangrid.github.io/storm-puppet-module/puppet_classes/storm_3A_3Adb.html
[gridftp-admin-striped]: http://toolkit.globus.org/toolkit/docs/6.0/gridftp/admin/index.html#gridftp-admin-striped

[used-space-example]: {{site.baseurl}}/documentation/how-to/how-to-initialize-storage-area-used-space/