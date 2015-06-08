---
layout: howto
title: StoRM Storage Resource Manager - Example of basic YAIM standalone configuration
version: 1.11.2
---

### A basic YAIM configuration for a StoRM standalone deployment

_**Components installed**_: <span class="label label-important">StoRM Backend</span> <span class="label label-info">StoRM Frontend</span> <span class="label">StoRM GridFTP</span>

In a StoRM standalone deployment all the StoRM components are installed on the same host. The StoRM Backend, Frontend and GridFTP are the mandatory components in this case.
As explained in the [System Administrator Guide]({{site.baseurl}}/documentation/sysadmin-guide/{{page.version}}/), to install these components you have to install the StoRM or the EMI3 repositories before (see [repositories section](http://italiangrid.github.io/storm/documentation/sysadmin-guide/1.11.2/#reposettings)).
Then you can install the proper meta-packages with:

```bash
yum install emi-storm-backend-mp emi-storm-frontend-mp emi-storm-globus-gridftp-mp
```

To properly configure these components you have to write a configuration file with some mandatory variables.
For example, we can create the **/etc/storm/siteinfo/storm.def** file.
Open this file and, first of all, set the YAIM mandatory variables (see [General YAIM Variables section](http://italiangrid.github.io/storm/documentation/sysadmin-guide/1.11.2/#yaimvariables)):

```bash
SITE_NAME="sample-storm-deployment"
BDII_HOST="emitb-bdii-site.cern.ch"
NTP_HOSTS_IP="77.242.176.254"
USERS_CONF=/etc/storm/siteinfo/storm-users.conf
GROUPS_CONF=/etc/storm/siteinfo/storm-groups.conf
MYSQL_PASSWORD="storm"
VOS="dteam"
```

- ```SITE_NAME``` is the human-readable name of your site used to set the Glue-SiteName attribute. Set this value as you want.
- ```BDII_HOST``` is a valid BDII hostname.
- ```NTP_HOSTS_IP``` is a space separated list of the IP addresses of the NTP servers (preferably set a local ntp server and a public one, e.g. pool.ntp.org).
- ```USERS_CONF``` is the path to the file containing the list of Linux users (pool accounts) to be created. This file must be created by the site administrator and contains a plain list of the users and their IDs. An example of this configuration file is given in ```/opt/glite/yaim/examples/users.conf``` file.
- ```GROUPS_CONF``` is the path to the file containing information on the mapping between VOMS groups and roles to local groups. An example of this configuration file is given in ```/opt/glite/yaim/examples/groups.conf``` file.
- ```MYSQL_PASSWORD``` mysql root password.
- ```VOS``` is a space separated list of supported VOs.

Then it's necessary to define some other variables:

```bash
STORM_BACKEND_HOST=`hostname -f`
JAVA_LOCATION="/usr/java/latest"
STORM_DEFAULT_ROOT="/storage"
STORM_DB_PWD=storm
```

- ```STORM_BACKEND_HOST``` is the FQDN of Backend's host.
- ```JAVA_LOCATION``` is a valid path of the installes java libraries (in case of SL6 and SL5.X with X>=9 you probably need to set ```"/usr/lib/jvm/java"```).
- ```STORM_DEFAULT_ROOT``` is the default directory for Storage Areas.
- ```STORM_DB_PWD``` is a valid password for database connection.

Run YAIM specifying the proper components profiles:

```bash
/opt/glite/yaim/bin/yaim -c -s /etc/storm/siteinfo/storm.def -n se_storm_backend -n se_storm_frontend -n se_storm_gridftp
```

If you want to install and use alse the StoRM GridHTTPs read [Enable StoRM GridHTTPs on a standalone deployment][enable-example].

[enable-example]: {{site.baseurl}}/documentation/examples/enable-gridhttps-standalone-deployment/1.11.2/enable-gridhttps-standalone-deployment.html