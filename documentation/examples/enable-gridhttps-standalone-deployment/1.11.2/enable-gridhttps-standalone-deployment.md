---
layout: default
title: StoRM Storage Resource Manager - Enable StoRM GridHTTPs on a standalone deployment
version: 1.11.2
---

#### [StoRM Examples]({{site.baseurl}}/documentation/examples)

### Enable StoRM GridHTTPs on a standalone deployment

_**Components installed**_: <span class="label label-important">StoRM Backend</span> <span class="label label-info">StoRM Frontend</span> <span class="label">StoRM GridFTP</span> <span class="label label-success">StoRM GridHTTPs</span>

If you have a standalone deployment of StoRM (see [this example][example]), and you need to add a WebDAV interface or use the HTTP/HTTPS protocol file transfer functionality, then you have to install the StoRM GridHTTPs component:

```bash
yum install emi-storm-gridhttps-mp
```

Then you have to add some variables to your YAIM configuration file, for example **/etc/storm/siteinfo/storm.def**:

```bash
STORM_GRIDHTTPS_ENABLED=true
STORM_GRIDHTTPS_PLUGIN_CLASSNAME="it.grid.storm.https.GhttpsHTTPSPluginInterface"
STORM_GRIDHTTPS_SERVER_USER_UID=`id -u gridhttps`
STORM_GRIDHTTPS_SERVER_GROUP_UID=`id -g gridhttps`
STORM_INFO_HTTP_SUPPORT=true
STORM_INFO_HTTPS_SUPPORT=true
```

It's a better idea to create the **gridhttps** user before running YAIM (see [system users section]({{site.baseurl}}/documentation/sysadmin-guide/1.11.2/#systemusers) on the [system administrator guide]({{site.baseurl}}/documentation/sysadmin-guide/1.11.2)):

```bash
useradd gridhttps -M -G storm
```

 Otherwise you could have to run it twice.

Run YAIM specyfing also the StoRM GridHTTPs profile:

```bash
/opt/glite/yaim/bin/yaim -c -s /etc/storm/siteinfo/storm.def -n se_storm_backend -n se_storm_frontend -n se_storm_gridftp -n se_storm_gridhttps
```

[example]: {{site.baseurl}}/documentation/examples/basic-storm-standalone-configuration/1.11.2/basic-storm-standalone-configuration.html
