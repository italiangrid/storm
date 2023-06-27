---
layout: service-guide
title: StoRM YAIM configuration - general YAIM variables
navigation:
  - link: documentation/sysadmin-guide/1.11.21/index.html
    label: Index
  - link: documentation/sysadmin-guide/1.11.21/installation-guides/index.html
    label: Installation Guides
  - link: documentation/sysadmin-guide/1.11.21/installation-guides/common/general-yaim-variables.html
    label: General YAIM variables
---

# General YAIM variables <a name="generalyaim">&nbsp;</a>

The following variables are used to configure the node where StoRM is installed. They had to be set because the YAIM profile of StoRM services expected them. They are used to configure BDII, NTP, VOs and pool accounts for example.

| Var. Name              | Description   | Mandatory |
|:-----------------------|:--------------|:---------:|
| SITE\_NAME             | It's the human-readable name of your site used to set the Glue-SiteName attribute.<br/>Example: `SITE_NAME="INFN EMI TESTBED"` | Yes
| BDII\_HOST             | BDII hostname. <br/>Example: `BDII_HOST="emitb-bdii-site.cern.ch"` | Yes
| NTP\_HOSTS\_IP         | Space separated list of the IP addresses of the NTP servers (preferably set a local ntp server and a public one, e.g. pool.ntp.org). If defined, /etc/ntp.conf will be overwritten during YAIM configuration. If not defined, the site administrator will be manage on his own the ntp service and its configuration. <br/>Example: `NTP_HOSTS_IP="131.154.1.103"` | No
| USERS\_CONF            | Path to the file containing the list of Linux users (pool accounts) to be created. This file must be created by the site administrator. It contains a plain list of the users and their IDs. An example of this configuration file is given in /opt/glite/yaim/examples/users.conf file. More details can be found in the User configuration section in the YAIM guide. | Yes
| GROUPS\_CONF           | Path to the file containing information on the map- ping between VOMS groups and roles to local groups. An example of this configuration file is given in /opt/glite/yaim/examples/groups.conf file. More details can be found in the Group configuration section in the YAIM guide. | Yes
| VOS                    | List of supported VOs. <br/>Example: `VOS="test.vo dteam"` | Yes
| MYSQL\_PASSWORD        | MySQL root password. <br/>Example: `MYSQL_PASSWORD="carpediem"` | Yes

Example of a `storm-users.conf` file that should be targeted from USERS\_CONF variable:

```
71001:dteam001:7100:dteam:dteam::
71002:dteam002:7100:dteam:dteam::
71003:dteam003:7100:dteam:dteam::
71004:dteam004:7100:dteam:dteam::
[...]
71999:dteam999:7100:dteam:dteam::
```

Example of a `storm-groups.conf` file that should be targeted from GROUPS\_CONF variable:

```
"/dteam"::::
```

Read more about YAIM into official [YAIM Guide](yaim-guide).

[yaim-guide]: https://twiki.cern.ch/twiki/bin/view/LCG/YaimGuide400
