---
layout: service-guide
title: StoRM Globus GridFTP installation and configuration guide
navigation:
  - link: documentation/sysadmin-guide/1.11.20/index.html
    label: Index
  - link: documentation/sysadmin-guide/1.11.20/installation-guides/index.html
    label: Installation Guides
  - link: documentation/sysadmin-guide/1.11.20/installation-guides/index.html#gridftp
    label: StoRM GridFTP DSI plugin
  - link: documentation/sysadmin-guide/1.11.20/installation-guides/gridftp/index.html
    label: Installation and configuration guide
---

# StoRM GridFTP installation and configuration guide

## Introduction

StoRM GridFTP DSI is a plugin of Globus GridFTP that computes the ADLER32 checksum on incoming file transfers and stores this value on an extended attribute of the file.

## Install the service package

Grab the latest package from the StoRM repository. See instructions
[here][download-page].

Install the metapackage:

```bash
yum install storm-globus-gridftp-mp
```

## Service configuration

The Globus Gridftp server configuration relies on a single file that contains all the configurable parameters. This file is:

```
/etc/grid-security/gridftp.conf
```

It contains a list of key-value (space separated) pairs that can be used to configure the server and the loading of StoRM GriDFTP DSI plugin. The full list of supported parameters can be read from Globus GridFTP documentation [here](https://gridcf.org/gct-docs/6.2/gridftp/admin/index.html#gridftp-configuring).

### Configuration properties

|   Property Name    |   Description     |
|:-------------------|:------------------|
| `port`             | Port on which a frontend will listen for client control channel connections, or on which a data node will listen for connections from a frontend. If not set a random port will be chosen and printed via the logging mechanism.
| `connections_max`  | Maximum concurrent connections allowed. Only applies when running in daemon mode. Unlimited if not set.
| `port_range`       | Port range to use for incoming connections. The format is "startport,endport". This, along with -data-interface, can be used to enable operation behind a firewall and/or when NAT is involved. This is the same as setting the environment variable GLOBUS\_TCP\_PORT\_RANGE.
| `config_base_path` | Base path to use when config and log path options are not full paths. By default this is the current directory when the process is started.
| `log_level`        | A comma separated list of levels from: ERROR, WARN, INFO, TRANSFER, DUMP, ALL. TRANSFER includes the same statistics that are sent to the separate transfer log when -log-transfer is used. Example: error,warn,info. You may also specify a numeric level of 1-255. The default level is ERROR.
| `log_single`       | Path of a single file to log all activity to. If neither this option or log_unique is set, logs will be written to stderr unless the execution mode is detached or inetd, in which case logging will be disabled.
| `log_transfer`     | Log netlogger style info for each transfer into this file. You may also use the log-level of TRANSFER to include this info in the standard log.
| `load_dsi_module` | With value "StoRM" it loads StoRM DSI module.
| `allowed_modules` | With value "StoRM" it allows StoRM DSI module.


At each transfer request, StoRM Globus GridFTP uses LCMAPS to get user mapping and start a new processes on behalf of the user to proceed with data transfer. GridFTP relies on a different db file to get the plugin to use. Obviously LCMAPS has to
answer to GridFTP requests and StoRM requests in coeherent way. The GridFTP uses the LCMAPS configuration file located at:

```
/etc/lcmaps/lcmaps.db
```

### IPC Channel

The IPC channel is used between a Globus GridFTP server head node and its disk servers, e.g. for striped transfers (read more into the [GridFTP System Administrator’s Guide][gridftp-admin-striped]).
In the default behavior of StoRM deployment the IPC channel is not used.
In fact, StoRM is mainly installed on a single host with one gridftp server which read/write directly on disk.
In the cases it is a distributed deployment, there are usually n gridftp servers which read/write data directly on disk, behind a haproxy or a dns for example, so there are no separate frontends and one or more disk node servers.
However, it's important to know that **the IPC channel must be kept firewalled for any hosts outside the SE system**.

> **The IPC channel must be kept firewalled for any hosts outside the SE system**.

## Configure the service with YAIM

StoRM Globus GridFTP server can be configured with YAIM tool on **CentOS 6 platform**.

Read more about YAIM tool [here][yaim-configuration-tool] and what are the [general YAIM variables][general-yaim-variables] for a StoRM deployment.

StoRM Globus GridFTP provides minimal support for YAIM. The following table summaries the general YAIM variables used for StoRM GridFTP component.

| Variable name             | Description                                                 |
|:--------------------------|:------------------------------------------------------------|
| `GRIDFTP_WITH_DSI`        | Enable StoRM module to compute checksum. Default: **false** |
| `GLOBUS_TCP_PORT_RANGE`   | GridFTP transfers port range.                               |
| `GRIDFTP_CONNECTIONS_MAX` | Set up maximum number of connections.                       |

## Configure the service with Puppet

The [StoRM puppet module][storm-puppet] can be used to configure the service on **CentOS 7 platform**. 

The module contains the `storm::gridftp` class that installs the metapackage _storm-globus-gridftp-mp_ and allows site administrator to configure _storm-globus-gridftp_ service by managing the following files:

- `/etc/grid-security/gridftp.conf`, the main configuration file;
- `/etc/sysconfig/storm-globus-gridftp`, with some environment variables.

The whole list of StoRM GridFTP class parameters can be found [here](https://italiangrid.github.io/storm-puppet-module/puppet_classes/storm_3A_3Agridftp.html).

Examples of StoRM Gridftp configuration:

```puppet
class { 'storm::gridftp':
  redirect_lcmaps_log => true,
  llgt_log_file       => '/var/log/storm/storm-gridftp-lcmaps.log',
}
```

## Logging

GridFTP produce two separated log files:

- */var/log/storm/gridftp-session.log* for the command session information
- */var/log/storm/globus-gridftp.log* for the transfer logs

The logging level can be specified by editing the configuration file:

    /etc/globus-gridftp-server/gridftp.gfork

The supported logging levels are: ERROR, WARN, INFO, DUMP and ALL.

### Redirect LCMAPS logging

Administrators can redirect the LCMAPS logging to a different log file than the one used by syslog by setting the `LLGT_LOG_FILE` environment variable.
As example, consider the following setup for the gridftp service:

    vim /etc/sysconfig/globus-gridftp

insert:

    export LLGT_LOG_FILE="/var/log/storm/storm-gridftp-lcmaps.log"

After restarting the service, all LCMAPS calls will be logged to the new file.


[yaim-configuration-tool]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.20/installation-guides/common/yaim-configuration-tool.html
[general-yaim-variables]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.20/installation-guides/common/general-yaim-variables.html

[gridftp-admin-striped]: http://toolkit.globus.org/toolkit/docs/6.0/gridftp/admin/index.html#gridftp-admin-striped
[storm-puppet]: https://forge.puppet.com/cnafsd/storm
