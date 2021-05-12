---
layout: service-guide
title: StoRM Frontend installation and configuration guide
navigation:
  - link: documentation/sysadmin-guide/1.11.21/index.html
    label: Index
  - link: documentation/sysadmin-guide/1.11.21/installation-guides/index.html
    label: Installation and Configuration guides
  - link: documentation/sysadmin-guide/1.11.21/installation-guides/frontend/index.html
    label: StoRM Frontend
---

# StoRM Frontend installation and configuration guide

## Introduction

The StoRM Frontend service provides a SRM interface for storage management 
and data access supporting VOMS authentication and authorization mechanisms.

## Install the service package

Grab the latest package from the StoRM repository. See instructions
[here][download-page].

Install the metapackage:

```bash
yum install storm-frontend-mp
```

## Service configuration

The Frontend component relies on a single configuration file that contains all the configurable parameters. This file is:

```
/etc/storm/frontend-server/storm-frontend-server.conf
```

containing a list of key-value pairs that can be used to configure the Frontend server. In case a parameter is modified, the Frontend service has to be restarted in order to read the new value.

Currently, the Frontend's configuration parameters can be divided per section as follows.

### Database settings

|  Property Name  |   Description                    |
|:----------------|:---------------------------------|
| `db.host`       | Host for database connection. Default is **localhost**
| `db.user`       | User for database connection. Default is **storm**
| `db.passwd`     | Password for database connection. Default is **password**

### Service settings

|   Property Name                |   Description     |
|:-------------------------------|:------------------|
| `fe.port`                      |  Frontend service port. Default is **8444**
| `fe.threadpool.threads.number` |  Size of the worker thread pool. Default is **50**
| `fe.threadpool.maxpending`     |  Size of the internal queue used to maintain SRM tasks in case there are no free worker threads. Default is **200**
| `fe.gsoap.maxpending`          |  Size of the GSOAP queue used to maintain pending SRM requests. Default is **1000**

### Log settings

|   Property Name   |   Description     |
|:------------------|:------------------|
| `log.filename`    | Full log file name path.<br/>Default is **/var/log/storm/storm-frontend.log**
| `log.debuglevel`  | Logging level. Possible values are: ERROR, WARN, INFO, DEBUG, DEBUG2. Default is **INFO**

### Monitoring settings

|   Property Name           |   Description     |
|:--------------------------|:------------------|
| `monitoring.enabled`      | Enable/disable monitoring. Default is **true**.
| `monitoring.timeInterval` | Time interval in seconds between each monitoring round. Default is **60**.
| `monitoring.detailed`     | Enable/disable detailed monitoring. Default is **false**.

### XML-RPC communication settings

|   Property Name         |   Description     |
|:------------------------|:------------------|
| `be.xmlrpc.host`        | Backend hostname. Default is **localhost**.
| `be.xmlrpc.port`        | Backend XML-RPC server port. Default is **8080**.
| `be.xmlrpc.token`       | Token used for communicating with Backend service. **Mandatory**, has no default.
| `be.xmlrpc.path`        | XML-RPC server path. Default is **/RPC2**.
| `be.xmlrpc.check.ascii` | Enable/disable ASCII checking on strings to be sent via XML-RPC. Default is **true**.

### REST communication settings

|   Property Name       |   Description     |
|:----------------------|:------------------|
| `be.recalltable.port` | REST server port running on the Backend machine. Default is **9998**.

### Blacklisting settings

|   Property Name           |   Description     |
|:--------------------------|:------------------|
| `check.user.blacklisting` | Enable/disable user blacklisting. Default is **false**.
| `argus-pepd-endpoint`     | The complete service endpoint of Argus PEP server. **Mandatory if** `check.user.blacklisting` is true. <br/>Example: _https://argus-pep-host:8154/authz_

### Proxy settings

|   Property Name             |   Description     |
|:----------------------------|:------------------|
| `security.enable.mapping`   | Flag to enable/disable DN-to-userid mapping via gridmap-file. Default is **false**.
| `security.enable.vomscheck` | Flag to enable/disable checking proxy VOMS credentials. Default is **true**.

### General settings

|   Property Name   |   Description     |
|:------------------|:------------------|
| `wsdl.file`       | WSDL file, complete with path, to be returned in case of GET request.

## Configure the service with YAIM

StoRM Frontend can be configured with YAIM tool on **CentOS 6 platform**.

Read more about YAIM tool [here][yaim-configuration-tool] and what are the [general YAIM variables][general-yaim-variables] for a StoRM deployment.

Here is a minimal YAIM configuration example for a Frontend node:

```bash
## The site name
SITE_NAME="storm-testbed"

## BDII hostname
BDII_HOST="emitb-bdii-site.cern.ch"

## List of NTP hosts
NTP_HOSTS_IP="131.154.1.103 193.206.144.10"

## Users configuration
USERS_CONF="/etc/storm/siteinfo/storm-users.conf"

## Groups configuration
GROUPS_CONF="/etc/storm/siteinfo/storm-groups.conf"

## Supported VOs.
VOS="dteam"

## Backend hostname
STORM_BACKEND_HOST="storm-backend.example.org"

## Database hostname and password
STORM_DB_HOST=$STORM_BACKEND_HOST
STORM_DB_PWD="secret"

## Backend's XMLRPC secret token
STORM_BE_XMLRPC_TOKEN="secret"
```

To configure the service with yaim, run the following command:

```bash
/opt/glite/yaim/bin/yaim -c -s SITEINFO.def -n se_storm_frontend
```

## Configure the service with Puppet

The [StoRM puppet module][storm-puppet] can be used to configure the service on **CentOS 7 platform**. 

The module contains the `storm::frontend` class that installs the metapackage _storm-frontend-mp_ and allows site administrator to configure _storm-frontend-server_ service by managing the following files:

- /etc/storm/frontend-server/storm-frontend-server.conf
- /etc/sysconfig/storm-frontend-server

The whole list of StoRM Frontend class parameters can be found [here](https://italiangrid.github.io/storm-puppet-module/puppet_classes/storm_3A_3Afrontend.html).

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

## Logging <a name="logging">&nbsp;</a>

The Frontend logs information on the service status and the SRM requests received and managed by the process. The Frontend's log supports different level of logging (ERROR, WARNING, INFO, DEBUG, DEBUG2) that can be set from the dedicated parameter in _storm-frontend-server.conf_ configuration file.
The Frontend log file named _storm-frontend-server.log_ is placed in the _/var/log/storm directory_. At start-up time, the FE prints here the whole set of configuration parameters, this can be useful to check desired values. When a new SRM request is managed, the FE logs information about the user (DN and FQANs) and the requested parameters.
At each SRM request, the FE logs also this important information:

```shell
03/19 11:51:42 0x88d4ab8 main: AUDIT - Active tasks: 3
03/19 11:51:42 0x88d4ab8 main: AUDIT - Pending tasks: 0
```

about the status of the worker pool threads and the pending process queue. _Active tasks_ is the number of worker threads actually running. _Pending tasks_ is the number of SRM requests queued in the worker pool queue. These data gives important information about the Frontend load.

### The monitoring.log file

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

### gSOAP tracefile

If you have problem at gSOAP level, and you have already looked at the troubleshooting section of the StoRM site without finding a solution, and you are brave enough, you could try to find some useful information on the gSOAP log file.
To enable gSOAP logging, set the following environment variables:

```bash
$ export CGSI_TRACE=1
$ export CGSI_TRACEFILE=/tmp/tracefile
```

and restart the Frontend daemon by calling directly the init script */etc/init.d/storm-frontend-server* and see if the error messages contained in */tmp/tracefile* could help. Please be very careful, it prints really a huge amount of information.

[download-page]: {{site.baseurl}}/download.html

[yaim-configuration-tool]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.20/installation-guides/common/yaim-configuration-tool.html
[general-yaim-variables]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.20/installation-guides/common/general-yaim-variables.html

[storm-puppet]: https://forge.puppet.com/cnafsd/storm
