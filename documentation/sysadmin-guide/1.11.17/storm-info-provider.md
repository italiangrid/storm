---
layout: toc
title: StoRM Info Provider administration guide
---


# StoRM Info Provider

StoRM Info Provider is the StoRM component that manages how and what
information are published on the BDII.

## The BDII service

The WLCG Information System is used to discover services and get status
information about WLCG resources. The **BDII** (Berkeley Database Information
Index) is a Perl/BDB 'glue' used to manage LDAP updates.
See [Grid Information System](http://gridinfo.web.cern.ch/) page for more
details.

By default, the BDII uses three directories to obtain information sources:

* **ldif**: static LDIF files should be placed in this directory;
* **provider**: here information providers are placed and run once at BDII
startup;
* **plugin**: scripts periodically run to update information.

These directories are located by default into `/var/lib/bdii/gip`.

## Configuration

StoRM Info Provider has not a configuration file, its behavior and outputs
depend on the site configuration which is processed by YAIM and stored
into */etc/storm/info-provider/storm-yaim-variables.conf*.

There are some YAIM variables used both to configure StoRM services and Info
Provider and some other specific variables which affects only the published
information (e.g. the SRM and WebDAV endpoints).

| Var. Name                   | Description   |
|:----------------------------|:--------------|
|`SITE_NAME`                  | It's the human-readable name of your site. |

|`STORM_ENDPOINT_QUALITY_LEVEL` | Endpoint maturity level to be published. Optional variable. Available values are: 0 (development), 1 (testing), 2 (pre-production) or 3 (production). Default value: **2**
|`STORM_ENDPOINT_SERVING_STATE` | Endpoint serving state to be published. Optional variable. Available values are: 1, (closed) or 4 (production). Default value: **4**
|`STORM_ENDPOINT_CAPABILITY`    | Capability according to OGSA to be published. Optional variable. Default value: **data.management.storage**

|`STORM_FRONTEND_PATH`              |StoRM Frontend service path.<br/>Optional variable. Default value: **/srm/managerv2**
|`STORM_FRONTEND_PORT`              |StoRM Frontend service port. Optional variable. Default value: **8444**
|`STORM_FRONTEND_PUBLIC_HOST`       |StoRM Frontend service public host. It's used by StoRM Info Provider to publish the SRM endpoint into the Resource BDII.<br/>Mandatory variable. Default value: **STORM_BACKEND_HOST**

|`STORM_INFO_FILE_SUPPORT`    | Trigger the `file` protocol to be published as transfer protocol.<br/>Optional variable. There's also a variable for a more specific Storage Area configuration. Default value: **true**
|`STORM_INFO_GRIDFTP_SUPPORT` | Trigger the `gsiftp` protocol to be published as transfer protocol.<br/>Optional variable. There's also a variable for a more specific Storage Area configuration. Default value: **true**
|`STORM_INFO_RFIO_SUPPORT`    | Trigger the `rfio` protocol to be published as transfer protocol.<br/>Optional variable. There's also a variable for a more specific Storage Area configuration. Default value: **false**
|`STORM_INFO_ROOT_SUPPORT`    | Trigger the `root` (or `xroot`) protocol to be published as transfer protocol.<br/>Optional variable. There's also a variable for a more specific Storage Area configuration. Default value: **false**
|`STORM_INFO_HTTP_SUPPORT`    | Trigger the `HTTP` protocol to be published as transfer protocol.<br/>Optional variable. There's also a variable for a more specific Storage Area configuration. Default value: **false**
|`STORM_INFO_HTTPS_SUPPORT`   | Trigger the `HTTPS` protocol to be published as transfer protocol.<br/>Optional variable. There's also a variable for a more specific Storage Area configuration. Default value: **false**

|`STORM_WEBDAV_POOL_LIST` | StoRM WebDAV endpoints. It's used by StoRM Info Provider to publish the WebDAV endpoints into the Resource BDII.<br/>Optional variable, **mandatory if the administrator wants to publish a WebDAV endpoint**. |

### Publish the SRM endpoint

StoRM Info Provider allows to publish one and unique SRM endpoint. The
endpoint published will be:

    httpg://${STORM_FRONTEND_PUBLIC_HOST}:${STORM_FRONTEND_PORT}${STORM_FRONTEND_PATH}

### Publish one or more WebDAV endpoints

From version [1.8.2][info-provider-182] StoRM Info Provider allows to publish
more than one WebDAV endpoint. Use `STORM_WEBDAV_POOL_LIST` to share the
endpoints.

It's recommended to migrate to the new YAIM variable if you were publishing
WebDAV endpoint info with `STORM_GRIDHTTPS_PUBLIC_HOST` variable.
Old configuration variables are still supported within StoRM v1.11.15, but the
upcoming versions won't support them.

For example, if:

- your WebDAV endpoint's hostname is `storm-webdav.example.org`
- it's detached from Backend's hostname
- you kept default http and https ports

you should have set:

```
STORM_GRIDHTTPS_ENABLED=true
STORM_GRIDHTTPS_PUBLIC_HOST=storm-webdav.example.org
```

If you changed the default ports you should also have values for:

```
STORM_GRIDHTTPS_HTTP_PORT=...
STORM_GRIDHTTPS_HTTPS_PORT=...
```

Your published endpoints will be:

- `http://storm-webdav.example.org:8085`
- `https://storm-webdav.example.org:8443`

Then you can copy this endpoints and paste them into the following YAIM
variable:

```
STORM_WEBDAV_POOL_LIST=http://storm-webdav.example.org:8085,https://storm-webdav.example.org:8443
```

and then remove `STORM_GRIDHTTPS_ENABLED`, `STORM_GRIDHTTPS_PUBLIC_HOST` and
also `STORM_GRIDHTTPS_HTTP_PORT` and `STORM_GRIDHTTPS_HTTPS_PORT`, if defined.


## Usage

    /usr/libexec/storm-info-provider -h

    usage: storm-info-provider [-h] [-v LOG_LEVEL] [-o LOG_FILENAME]
                           {configure,get-static-ldif,get-update-ldif} ...

Options:

* `-v`: `LOG_LEVEL` can be 10 (DEBUG), 20 (INFO - default), 30 (WARNING) and 40 (ERROR)
* `-o`: all the log messages are printed on stderr by default but they can be redirected to an external `LOG_FILENAME` by specifying this option

Example of usage with `configure`:

    /usr/libexec/storm-info-provider configure -h

    usage: storm-info-provider configure [-h] [-f FILEPATH]
                                     [-g {glue13,glue2,all}]
    optional arguments:
      -h, --help            show this help message and exit
      -f FILEPATH
      -g {glue13,glue2,all}


Options:

* `-f`: the path of the file which contains all the StoRM related YAIM variables with their key-value pairs (default is `/etc/storm/info-provider/storm-yaim-variables.conf` which is the file created by yaim-storm)
* `-g`: GLUE version selector (default: _all_)

During configuration, yaim-storm creates `storm-yaim-variables.conf` and runs the StoRM Dynamic Info Provider script as follow:

    /usr/libexec/storm-info-provider -v LOG_LEVEL configure -g all -f /etc/storm/info-provider/storm-yaim-variables.conf

Example of output with `LOG_LEVEL=20`:

    /usr/libexec/storm-info-provider configure

    2014-09-04 10:40:34,271 root        : INFO Successfully created /etc/storm/info-provider/glite-info-glue13-service-storm.conf !
    2014-09-04 10:40:34,271 root        : INFO Successfully created /var/lib/bdii/gip/provider/storm-glue13-provider !
    2014-09-04 10:40:34,272 root        : INFO Successfully created /var/lib/bdii/gip/plugin/storm-glue13-plugin !
    2014-09-04 10:40:34,321 root        : INFO Successfully created /var/lib/bdii/gip/ldif/storm-glue13-static.ldif !
    2014-09-04 10:40:34,322 root        : INFO Successfully created /etc/storm/info-provider/glite-info-glue2-service-storm.conf !
    2014-09-04 10:40:34,322 root        : INFO Successfully created /etc/storm/info-provider/glite-info-glue2-service-storm-endpoint-srm.conf !
    2014-09-04 10:40:34,322 root        : INFO Successfully created /var/lib/bdii/gip/provider/storm-glue2-provider !
    2014-09-04 10:40:34,323 root        : INFO Successfully created /var/lib/bdii/gip/plugin/storm-glue2-plugin !
    2014-09-04 10:40:34,365 root        : INFO Successfully created /var/lib/bdii/gip/ldif/storm-glue2-static.ldif !
    2014-09-04 10:40:34,365 root        : INFO Received configure - It took 0.04 sec

Example of usage with `get-static-ldif`:

    /usr/libexec/storm-info-provider get-static-ldif -h

    usage: storm-info-provider get-static-ldif [-h] [-f FILEPATH]
                                               [-g {glue13,glue2}]
    optional arguments:
      -h, --help         show this help message and exit
      -f FILEPATH
      -g {glue13,glue2}

Options:

* `-f`: the path of the file which contains all the StoRM related YAIM variables with their key-value pairs (default is `/etc/storm/info-provider/storm-yaim-variables.conf` which is the file created by yaim-storm)
* `-g`: GLUE version selector (default: _glue2_)

Example of a filtered output to obtain only the `dn` of the generated entries:

    /usr/libexec/storm-info-provider get-static-ldif -g glue13 2>/dev/null | grep dn

    dn: GlueSEUniqueID=cloud-vm45.cloud.cnaf.infn.it,mds-vo-name=resource,o=grid
    dn: GlueSALocalID=tape:custodial:nearline,GlueSEUniqueID=cloud-vm45.cloud.cnaf.infn.it,mds-vo-name=resource,o=grid
    dn: GlueVOInfoLocalID=testers.eu-emi.eu,GlueSALocalID=tape:custodial:nearline,GlueSEUniqueID=cloud-vm45.cloud.cnaf.infn.it,mds-vo-name=resource,o=grid
    dn: GlueSALocalID=igi:replica:online,GlueSEUniqueID=cloud-vm45.cloud.cnaf.infn.it,mds-vo-name=resource,o=grid
    dn: GlueSALocalID=noauth:replica:online,GlueSEUniqueID=cloud-vm45.cloud.cnaf.infn.it,mds-vo-name=resource,o=grid
    dn: GlueSALocalID=nested:replica:online,GlueSEUniqueID=cloud-vm45.cloud.cnaf.infn.it,mds-vo-name=resource,o=grid
    dn: GlueVOInfoLocalID=testers.eu-emi.eu,GlueSALocalID=nested:replica:online,GlueSEUniqueID=cloud-vm45.cloud.cnaf.infn.it,mds-vo-name=resource,o=grid
    dn: GlueSALocalID=dteam:replica:online,GlueSEUniqueID=cloud-vm45.cloud.cnaf.infn.it,mds-vo-name=resource,o=grid
    dn: GlueVOInfoLocalID=dteam,GlueSALocalID=dteam:replica:online,GlueSEUniqueID=cloud-vm45.cloud.cnaf.infn.it,mds-vo-name=resource,o=grid
    dn: GlueSALocalID=testerseuemieu:replica:online,GlueSEUniqueID=cloud-vm45.cloud.cnaf.infn.it,mds-vo-name=resource,o=grid
    dn: GlueVOInfoLocalID=testers.eu-emi.eu,GlueSALocalID=testerseuemieu:replica:online,GlueSEUniqueID=cloud-vm45.cloud.cnaf.infn.it,mds-vo-name=resource,o=grid
    dn: GlueSEControlProtocolLocalID=srm_v2.2,GlueSEUniqueID=cloud-vm45.cloud.cnaf.infn.it,mds-vo-name=resource,o=grid
    dn: GlueSEAccessProtocolLocalID=file,GlueSEUniqueID=cloud-vm45.cloud.cnaf.infn.it,mds-vo-name=resource,o=grid
    dn: GlueSEAccessProtocolLocalID=gsiftp,GlueSEUniqueID=cloud-vm45.cloud.cnaf.infn.it,mds-vo-name=resource,o=grid
    dn: GlueSEAccessProtocolLocalID=xroot,GlueSEUniqueID=cloud-vm45.cloud.cnaf.infn.it,mds-vo-name=resource,o=grid
    dn: GlueSEAccessProtocolLocalID=http,GlueSEUniqueID=cloud-vm45.cloud.cnaf.infn.it,mds-vo-name=resource,o=grid
    dn: GlueSEAccessProtocolLocalID=https,GlueSEUniqueID=cloud-vm45.cloud.cnaf.infn.it,mds-vo-name=resource,o=grid
    dn: GlueSEAccessProtocolLocalID=webdav,GlueSEUniqueID=cloud-vm45.cloud.cnaf.infn.it,mds-vo-name=resource,o=grid

The action `get-static-ldif` is not used by the installed scripts. However its functionality is internally used by the `configure` action to generate the static ldif files.

Example of usage with `get-update-ldif`:

    /usr/libexec/storm-info-provider get-update-ldif -h
    usage: storm-info-provider get-static-ldif [-h] [-f FILEPATH]
                                               [-g {glue13,glue2}]
    optional arguments:
      -h, --help         show this help message and exit
      -f FILEPATH
      -g {glue13,glue2}

Options:

* `-f`: the path of the file which contains all the StoRM related YAIM variables with their key-value pairs (default is `/etc/storm/info-provider/storm-yaim-variables.conf` which is the file created by yaim-storm)
* `-g`: GLUE version selector (default: _glue2_)

The plugin files created during `configure` phase runs StoRM DIP `get-update-ldif`.
If StoRM service is down, an error is logged and user obtains the LDIF output useful to update the serving-state value of the endpoints.

Example of a filtered output to obtain only the `dn` of the generated entries:

    /usr/libexec/storm-info-provider get-update-ldif -g glue2 2>/dev/null | grep dn

    dn: GLUE2EndpointID=cloud-vm45.cloud.cnaf.infn.it/storage/endpoint/SRM,GLUE2ServiceID=cloud-vm45.cloud.cnaf.infn.it/storage,GLUE2GroupID=resource,o=glue
    dn: GLUE2EndpointID=cloud-vm45.cloud.cnaf.infn.it/storage/endpoint/HTTP,GLUE2ServiceID=cloud-vm45.cloud.cnaf.infn.it/storage,GLUE2GroupID=resource,o=glue
    dn: GLUE2EndpointID=cloud-vm45.cloud.cnaf.infn.it/storage/endpoint/HTTPS,GLUE2ServiceID=cloud-vm45.cloud.cnaf.infn.it/storage,GLUE2GroupID=resource,o=glue
    dn: GLUE2StorageServiceCapacityID=cloud-vm45.cloud.cnaf.infn.it/storage/capacity/online,GLUE2ServiceID=cloud-vm45.cloud.cnaf.infn.it/storage,GLUE2GroupID=resource,o=glue
    dn: GLUE2StorageServiceCapacityID=cloud-vm45.cloud.cnaf.infn.it/storage/capacity/nearline,GLUE2ServiceID=cloud-vm45.cloud.cnaf.infn.it/storage,GLUE2GroupID=resource,o=glue
    dn: GLUE2ShareID=cloud-vm45.cloud.cnaf.infn.it/storage/share/tape,GLUE2ServiceID=cloud-vm45.cloud.cnaf.infn.it/storage,GLUE2GroupID=resource,o=glue
    dn: GLUE2StorageShareCapacityID=cloud-vm45.cloud.cnaf.infn.it/storage/share/tape/capacity/online,GLUE2ShareID=cloud-vm45.cloud.cnaf.infn.it/storage/share/tape,GLUE2ServiceID=cloud-vm45.cloud.cnaf.infn.it/storage,GLUE2GroupID=resource,o=glue
    dn: GLUE2StorageShareCapacityID=cloud-vm45.cloud.cnaf.infn.it/storage/share/tape/capacity/nearline,GLUE2ShareID=cloud-vm45.cloud.cnaf.infn.it/storage/share/tape,GLUE2ServiceID=cloud-vm45.cloud.cnaf.infn.it/storage,GLUE2GroupID=resource,o=glue
    dn: GLUE2ShareID=cloud-vm45.cloud.cnaf.infn.it/storage/share/igi,GLUE2ServiceID=cloud-vm45.cloud.cnaf.infn.it/storage,GLUE2GroupID=resource,o=glue
    dn: GLUE2StorageShareCapacityID=cloud-vm45.cloud.cnaf.infn.it/storage/share/igi/capacity/online,GLUE2ShareID=cloud-vm45.cloud.cnaf.infn.it/storage/share/igi,GLUE2ServiceID=cloud-vm45.cloud.cnaf.infn.it/storage,GLUE2GroupID=resource,o=glue
    dn: GLUE2ShareID=cloud-vm45.cloud.cnaf.infn.it/storage/share/noauth,GLUE2ServiceID=cloud-vm45.cloud.cnaf.infn.it/storage,GLUE2GroupID=resource,o=glue
    dn: GLUE2StorageShareCapacityID=cloud-vm45.cloud.cnaf.infn.it/storage/share/noauth/capacity/online,GLUE2ShareID=cloud-vm45.cloud.cnaf.infn.it/storage/share/noauth,GLUE2ServiceID=cloud-vm45.cloud.cnaf.infn.it/storage,GLUE2GroupID=resource,o=glue
    dn: GLUE2ShareID=cloud-vm45.cloud.cnaf.infn.it/storage/share/nested,GLUE2ServiceID=cloud-vm45.cloud.cnaf.infn.it/storage,GLUE2GroupID=resource,o=glue
    dn: GLUE2StorageShareCapacityID=cloud-vm45.cloud.cnaf.infn.it/storage/share/nested/capacity/online,GLUE2ShareID=cloud-vm45.cloud.cnaf.infn.it/storage/share/nested,GLUE2ServiceID=cloud-vm45.cloud.cnaf.infn.it/storage,GLUE2GroupID=resource,o=glue
    dn: GLUE2ShareID=cloud-vm45.cloud.cnaf.infn.it/storage/share/dteam,GLUE2ServiceID=cloud-vm45.cloud.cnaf.infn.it/storage,GLUE2GroupID=resource,o=glue
    dn: GLUE2StorageShareCapacityID=cloud-vm45.cloud.cnaf.infn.it/storage/share/dteam/capacity/online,GLUE2ShareID=cloud-vm45.cloud.cnaf.infn.it/storage/share/dteam,GLUE2ServiceID=cloud-vm45.cloud.cnaf.infn.it/storage,GLUE2GroupID=resource,o=glue
    dn: GLUE2ShareID=cloud-vm45.cloud.cnaf.infn.it/storage/share/testerseuemieu,GLUE2ServiceID=cloud-vm45.cloud.cnaf.infn.it/storage,GLUE2GroupID=resource,o=glue
    dn: GLUE2StorageShareCapacityID=cloud-vm45.cloud.cnaf.infn.it/storage/share/testerseuemieu/capacity/online,GLUE2ShareID=cloud-vm45.cloud.cnaf.infn.it/storage/share/testerseuemieu,GLUE2ServiceID=cloud-vm45.cloud.cnaf.infn.it/storage,GLUE2GroupID=resource,o=glue

Example of the output generated when StoRM service is down:

    service storm-backend-server stop

    Stopping storm-backend-server                              [  OK  ]

    /usr/libexec/storm-info-provider get-update-ldif -g glue2 2>/dev/null

    dn: GLUE2EndpointID=cloud-vm45.cloud.cnaf.infn.it/storage/endpoint/SRM,GLUE2ServiceID=cloud-vm45.cloud.cnaf.infn.it/storage,GLUE2GroupID=resource,o=glue
    GLUE2EndpointServingState: closed
    dn: GLUE2EndpointID=cloud-vm45.cloud.cnaf.infn.it/storage/endpoint/HTTP,GLUE2ServiceID=cloud-vm45.cloud.cnaf.infn.it/storage,GLUE2GroupID=resource,o=glue
    GLUE2EndpointServingState: closed
    dn: GLUE2EndpointID=cloud-vm45.cloud.cnaf.infn.it/storage/endpoint/HTTPS,GLUE2ServiceID=cloud-vm45.cloud.cnaf.infn.it/storage,GLUE2GroupID=resource,o=glue
    GLUE2EndpointServingState: closed


[info-provider-177]: {{site.baseurl}}/release-notes/storm-dynamic-info-provider/1.7.7/
[info-provider-182]: {{site.baseurl}}/release-notes/storm-dynamic-info-provider/1.7.7/
