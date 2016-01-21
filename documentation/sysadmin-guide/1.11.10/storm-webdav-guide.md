---
layout: toc
title: StoRM WebDAV installation and configuration guide
---

# StoRM WebDAV Installation and configuration guide

## Introduction

The StoRM WebDAV service replaces the StoRM GridHTTPS service.

## Install the service package

Grap the latest package from the StoRM repository. See instructions
[here]({{ site.baseurl }}/download.html).

Note that storm-webdav is supported **only** on SL6.

```bash
yum install storm-webdav
```

## Configure the service with YAIM

StoRM webdav provides minimal support for YAIM.

Minimal example configuration:

```bash
## The site name
SITE_NAME="storm-testbed"

## List of NTP hosts
NTP_HOSTS_IP="131.154.1.103 193.206.144.10"

## Location of the JVM. Java 7 is required
JAVA_LOCATION="/usr/lib/jvm/jre-1.7.0"

## Users configuration
USERS_CONF=/etc/storm/siteinfo/storm-users.conf

## Groups configuration
GROUPS_CONF=/etc/storm/siteinfo/storm-groups.conf

## Supported VOs.
VOS="testers.eu-emi.eu dteam"

## List of storage areas
STORM_STORAGEAREA_LIST="testers.eu-emi.eu tape"

## Root for the storage area directories
STORM_DEFAULT_ROOT="/storage"

## Enables authenticated read access to the testers.eu-emi.eu
## storage area to all clients authenticated with a trusted certificate
STORM_TESTERSEUEMIEU_AUTHENTICATED_HTTP_READ=true

## Sets the dteam VO as the trusted VO for storage area
## tape
STORM_TAPE_VONAME=dteam
```

The above configuration will configure two storage areas, `testers.eu-emi.eu`
and `tape`. Access to the `testers.eu-emi.eu` storage area will be granted to
all members of the VO `testers.eu-emi.eu` (this is configured by default when
the storage area name is identical to the VO name) authenticated with a valid
VOMS proxy certificate.

In addition, access is granted to all clients authenticated with a valid X.509
certificate signed by a trusted CA.

Access to the `tape` storage area is granted to all members of the dteam VO.

To configure the service with yaim, run the following command:

```
/opt/glite/yaim/bin/yaim -c -s SITEINFO.def -n se_storm_webdav
```

## Service configuration

The storm-webdav service configuration lives in `/etc/sysconfig/storm-webdav` file.
Normally you shouldn't change anything.

### Storage area configuration

StoRM WebDAV service configuration lives in the directory `/etc/storm/webdav`.

Each storage area is configured in a properties file. StoRM WebDAV will look
for configuration in all files ending with _.properties_ in this directory.
**If no configuration files are found, the StoRM WebDAV service will not start.**

For an example storage area configuration file see the `sa.properties.template` file:

```bash
# This is an example of StoRM WebDAV storage area configuration

# Name of the storage area
name=sa

# Root path for the storage area. Files will be served from this path, which must exist and
# must be accessible from the user that runs the storm webdav service
rootPath=/tmp

# Comma separated list of storage area access points.
accessPoints=/sa

# Comma separated list of VOMS VOs supported in this storage area
vos=testers.eu-emi.eu

# Enables read access to users authenticated with an X.509 certificate issued by
# a trusted CA (users without VOMS credentials).
# Defaults to false, which means that all users need to authenticate with a VOMS credential
# authenticatedReadEnabled=false

# Enables read access to anonymous users. Defaults to false.
# anonymousReadEnabled=false

# Enables VO map files for this storage area. Defaults to true.
# voMapEnabled=true

# VO map normally grants read-only access to storage area files. To grant
# write access set this flag to true. Defaults to false.
# voMapGrantsWriteAccess=false
```

### The VO map-files

When VO map files are enabled, users can authenticate to the StoRM webdav
service using the certificate in their browser and be granted VOMS attributes
if their subject is listed in one of the supported VO map-file.
You can configure whether users listed in VO map files will be granted read-only
or write permissions in the storage area configuration in the `/etc/storm/webdav/sa.d`
directory.

This mechanism is very similar to the traditional Gridmap file but is just used
to know whether a given user is registered as a member in a VOMS managed VO and
not to map his/her certificate subject to a local unix account.

#### How to enable VO map-files

VO map-files support is disabled by default in StoRM WebDAV.

Open `/etc/sysconfig/storm-webdav` and set:

```bash
STORM_WEBDAV_VO_MAP_FILES_ENABLE=true
```

to enable VO map-file support.

#### Format and location

A VO map-file is a _.csv_ file listing for each line:

```
[certificate subject],[issuer],[email]
```

It can be easily generated for a given VO using the `voms-admin` command line utility.

VO map-files by default live in the `/etc/storm/webdav/vo-mapfiles.d` directory.
For each VO, a file named `VONAME.vomap` is put in the `/etc/storm/webdav/vo-mapfiles.d` directory.

#### File examples

The file `/etc/storm/webdav/vo-mapfiles.d/test.vomap` with the following content:

```bash
/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Andrea Ceccanti,/C=IT/O=INFN/CN=INFN CA,andrea.ceccanti@cnaf.infn.it
/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Enrico Vianello,/C=IT/O=INFN/CN=INFN CA,enrico.vianello@cnaf.infn.it
```

will grant the `test` VO membership to clients authenticated with the above subjects.

#### Generate a map-file for a specific VO

To generate a VO map-file for the `cms` VO, you could run the following command

```shell
voms-admin --host voms.cern.ch --vo cms list-users > /etc/storm/webdav/vo-mapfiles.d/cms.vomap
```

You need to know a valid VOMS server FQDN that supports the desired VO. In our case, it's _voms.cern.ch_.

## Service operation

### Starting and stopping the service

Start the service:

```
service storm-webdav start
```

Stop the service:

```
service storm-webdav stop
```

Check service status:

```
service storm-webdav status
```

Check that the service responds:

```
$ curl http://localhost:8085/status/ping
pong
```

Print JVM thread stacks:

```
$ curl http://localhost:8085/status/threads
Reference Handler id=2 state=WAITING
    - waiting on <0x519b1696> (a java.lang.ref.Reference$Lock)
    - locked <0x519b1696> (a java.lang.ref.Reference$Lock)
    at java.lang.Object.wait(Native Method)
    at java.lang.Object.wait(Object.java:503)
    at java.lang.ref.Reference$ReferenceHandler.run(Reference.java:133)

Finalizer id=3 state=WAITING
    - waiting on <0x3c854594> (a java.lang.ref.ReferenceQueue$Lock)
    - locked <0x3c854594> (a java.lang.ref.ReferenceQueue$Lock)
    at java.lang.Object.wait(Native Method)
    at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:135)
    at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:151)
    at java.lang.ref.Finalizer$FinalizerThread.run(Finalizer.java:209)

Signal Dispatcher id=4 state=RUNNABLE
...
```

### Service metrics

Get service metrics:

```
$ curl http://localhost:8085/status/metrics?pretty=true
{
  "version" : "3.0.0",
  "gauges" : {
    "jvm.gc.Copy.count" : {
      "value" : 1
    },
    "jvm.gc.Copy.time" : {
      "value" : 29
    },
    "jvm.gc.MarkSweepCompact.count" : {
      "value" : 0
    },
    "jvm.gc.MarkSweepCompact.time" : {
      "value" : 0
    },
    "jvm.memory.heap.committed" : {
      "value" : 259522560
    },
    "jvm.memory.heap.init" : {
      "value" : 268435456
    },
    "jvm.memory.heap.max" : {
      "value" : 518979584
    },
    ...

```

### Service logs

The service logs live in the `/var/log/storm/webdav` directory.

- `storm-webdav-server.log` provides the main service log
- `storm-webdav-server-access.log` provides an http access log
- `storm-webdav-server-metrics.log` provides a metrics log, similar to the StoRM backend heartbeat

### Access points

By default a storage area named `sa` is accessible at the URL:

```
https://hostname:8443/sa
```

or, if anonymous access is granted, at:

```
http://hostname:8085/sa
```

For backward compatibility with the StoRM GridHTTPs also:

```
https://hostname:8443/webdav/sa
```

and

```
http://hostname:8085/webdav/sa
```

will work, but it's not needed anymore to specify the `webdav` prefix when issuing
requests to the service.
