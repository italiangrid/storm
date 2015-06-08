---
layout: howto
title: StoRM How-To - Example of basic StoRM all-in-one deployment configuration
---

# Quick all-in-one deployment

The simplest example of a StoRM deployment can be done by installing all the components on a single host.

Assuming that:

- the host satisfies the [StoRM Installation Prerequisites][INSTALL-PREREQ]
- the StoRM repositories have been installed (see [repositories section][REPOSETTINGS]).

## Install the services packages

Install all the components as follows:

```bash
yum install emi-storm-backend-mp \
    emi-storm-frontend-mp \
    emi-storm-globus-gridftp-mp \
    storm-webdav
```

## Configure the services with YAIM

Create a single site configuration file `/etc/storm/siteinfo/storm.def` as follows:

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

# Enable Root Transfer Protocol 
STORM_INFO_ROOT_SUPPORT=true

# Enable HTTP Transfer Protocol
STORM_INFO_HTTP_SUPPORT=true

# Enable HTTPS Transfer Protocol
STORM_INFO_HTTPS_SUPPORT=true

```

Run YAIM specifying the proper components profiles:

```bash
/opt/glite/yaim/bin/yaim \
    -c -s /etc/storm/siteinfo/storm.def \
    -n se_storm_backend \
    -n se_storm_frontend \
    -n se_storm_gridftp \
    -n se_storm_webdav
```

### Check services statuses

Check StoRM services statuses:

```bash
service storm-backend-server status
service storm-frontend-server status
service storm-globus-gridftp status
service storm-webdav status
```

[INSTALL-PREREQ]: {{ site.baseurl }}/documentation/sysadmin-guide/1.11.7/index.html#installprereq
[REPOSETTINGS]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.7/index.html#reposettings


[sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/{{ site.sysadmin_guide_version }}

[enable-example]: {{site.baseurl}}/documentation/examples/enable-gridhttps-standalone-deployment/1.11.2/enable-gridhttps-standalone-deployment.html