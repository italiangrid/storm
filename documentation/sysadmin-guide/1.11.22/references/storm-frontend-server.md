---
layout: service-guide
title: StoRM Frontend configuration file REFERENCE
---

# StoRM Frontend configuration file reference

The Frontend component relies on a single configuration file that contains all the configurable parameters. This file is:

```
/etc/storm/frontend-server/storm-frontend-server.conf
```

containing a list of key-value pairs that can be used to configure the Frontend server. In case a parameter is modified, the Frontend service has to be restarted in order to read the new value.

Currently, the Frontend's configuration parameters can be divided per section as follows.

## Database settings

|  Property Name  |   Description                    |
|:----------------|:---------------------------------|
| `db.host`       | Host for database connection. Default is **localhost**
| `db.user`       | User for database connection. Default is **storm**
| `db.passwd`     | Password for database connection. Default is **password**

## Service settings

|   Property Name                |   Description     |
|:-------------------------------|:------------------|
| `fe.port`                      |  Frontend service port. Default is **8444**
| `fe.threadpool.threads.number` |  Size of the worker thread pool. Default is **50**
| `fe.threadpool.maxpending`     |  Size of the internal queue used to maintain SRM tasks in case there are no free worker threads. Default is **200**
| `fe.gsoap.maxpending`          |  Size of the GSOAP queue used to maintain pending SRM requests. Default is **1000**

## Log settings

|   Property Name   |   Description     |
|:------------------|:------------------|
| `log.filename`    | Full log file name path.<br/>Default is **/var/log/storm/storm-frontend.log**
| `log.debuglevel`  | Logging level. Possible values are: ERROR, WARN, INFO, DEBUG, DEBUG2. Default is **INFO**

## Monitoring settings

|   Property Name           |   Description     |
|:--------------------------|:------------------|
| `monitoring.enabled`      | Enable/disable monitoring. Default is **true**.
| `monitoring.timeInterval` | Time interval in seconds between each monitoring round. Default is **60**.
| `monitoring.detailed`     | Enable/disable detailed monitoring. Default is **false**.

## XML-RPC communication settings

|   Property Name         |   Description     |
|:------------------------|:------------------|
| `be.xmlrpc.host`        | Backend hostname. Default is **localhost**.
| `be.xmlrpc.port`        | Backend XML-RPC server port. Default is **8080**.
| `be.xmlrpc.token`       | Token used for communicating with Backend service. **Mandatory**, has no default.
| `be.xmlrpc.path`        | XML-RPC server path. Default is **/RPC2**.
| `be.xmlrpc.check.ascii` | Enable/disable ASCII checking on strings to be sent via XML-RPC. Default is **true**.

## REST communication settings

|   Property Name       |   Description     |
|:----------------------|:------------------|
| `be.recalltable.port` | REST server port running on the Backend machine. Default is **9998**.

# Blacklisting settings

|   Property Name           |   Description     |
|:--------------------------|:------------------|
| `check.user.blacklisting` | Enable/disable user blacklisting. Default is **false**.
| `argus-pepd-endpoint`     | The complete service endpoint of Argus PEP server. **Mandatory if** `check.user.blacklisting` is true. <br/>Example: _https://argus-pep-host:8154/authz_

## Proxy settings

|   Property Name             |   Description     |
|:----------------------------|:------------------|
| `security.enable.mapping`   | Flag to enable/disable DN-to-userid mapping via gridmap-file. Default is **false**.
| `security.enable.vomscheck` | Flag to enable/disable checking proxy VOMS credentials. Default is **true**.

## General settings

|   Property Name   |   Description     |
|:------------------|:------------------|
| `wsdl.file`       | WSDL file, complete with path, to be returned in case of GET request.