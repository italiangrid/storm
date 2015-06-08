---
layout: howto
title: StoRM Storage Resource Manager - Enable StoRM GridHTTPs on a standalone deployment
version: 1.11.2
---

### How-to configure LDAP Server to share users' accounts

<hr/>

#### Table of contents

* [Introduction](#intro)
* [OpenLDAP Server installation and configuration](#LDAPServerIC)
* [OpenLDAP Client installation and configuration](#LDAPClientIC)
* [Test client-server LDAP installation](#TestLDAP)

<hr/>

#### Introduction <a name="intro">&nbsp;</a>

This is a short tutorial that wants to describe how to install and configure a LDAP Server in order to share users' accounts whitin a local network. In particular, we will see how to install and configure a client/server OpenLDAP service on Scientific Linux hosts.

#### OpenLDAP Server installation and configuration <a name="LDAPServerIC">&nbsp;</a>

To install OpenLDAP service on server, as root user on a SL5 host, we have to install *openldap-servers* package:

```bash
$ yum install openldap-servers
```

Instead, as root user on a SL6 host we have to install both *openldap* and *openldap-servers* packages:

```bash
$ yum install openldap openldap-servers
```

OpenLDAP installs several files in /etc and other places. The *slapd* daemon's configuration file is slapd.conf and can be found in /etc/openldap. First of all make sure service is not running:

```bash
(SL5) $ service ldap stop
(SL6) $ service slapd stop
```

Then, edit **/etc/openldap/slapd.conf**. We have to edit 5 entries: *database*, *suffix*, *rootdn*, *rootpw* and *directory*. It should look something like:

	  database        bdb
	  suffix          "dc=example,dc=com"
	  rootdn          "cn=Manager,dc=example,dc=com"
	  # Cleartext passwords, especially for the rootdn, should be avoided. 
	  # See slappasswd(8) and slapd.conf(5) for details.
	  # Use of strong authentication encouraged.
	  # rootpw        secret
	  directory 	  /var/lib/ldap
	
For example, define ourselves as a company called storm.cnaf.infn.it. We can leave default value for *database* entry, *bdb*, that stands for Berkeley Database. The *suffix* entry is our main domain or organization. So, we can changed it to:

	  suffix          "dc=storm,dc=cnaf,dc=infn.it"

The dc stands for domainComponent. Next, the *rootdn*. This means root Distinguished Name. Every entry in an LDAP database has a distinguished name. The default is Manager. In our example we will use root instead of Manager, so according to the suffix we have to change it to:

	  rootdn          "cn=root,dc=storm,dc=cnaf,dc=infn.it"

Next the *password*. If we desire an encrypted password, we can launch slappasswd command, insert our password and replace *secret* with the outputted string. So it will look something like:

	  rootpw         {SSHA}ca6CWAHXogaQ2Cib9sxOYRwHRzyKoSXA

We can leave default value for *directory* entry: /var/lib/ldap.
Now we have to clean up previous LDAP content and configuration with the following command:

```bash
(SL5) $ rm -rf /var/lib/ldap/*
(SL6) $ rm -rf /etc/openldap/slapd.d/*
```

Then we have to create the DB_CONFIG file into /var/lib/ldap/ directory (or copy it from OpenLDAP example file /etc/openldap/DB_CONFIG.example).

**/etc/openldap/DB_CONFIG.example**:

	  # $OpenLDAP: pkg/ldap/servers/slapd/DB_CONFIG,v 1.3.2.4 2007/12/18 11:53:27 ghenry Exp $
  	  # Example DB_CONFIG file for use with slapd(8) BDB/HDB databases.
  	  #
  	  # See the Oracle Berkeley DB documentation
  	  #	<http://www.oracle.com/technology/documentation/berkeley-db/db/ref/env/db_config.html>
  	  # for detail description of DB_CONFIG syntax and semantics.
  	  #
	  # Hints can also be found in the OpenLDAP Software FAQ
  	  # <http://www.openldap.org/faq/index.cgi?file=2>
	  # in particular:
	  #   <http://www.openldap.org/faq/index.cgi?file=1075>
	  # Note: most DB_CONFIG settings will take effect only upon rebuilding
	  # the DB environment.
	  # one 0.25 GB cache
	  set_cachesize 0 268435456 1
	  # Data Directory
	  #set_data_dir db
	  # Transaction Log settings
	  set_lg_regionmax 262144
	  set_lg_bsize 2097152
	  #set_lg_dir logs
	  # Note: special DB_CONFIG flags are no longer needed for "quick"
	  # slapadd(8) or slapindex(8) access (see their -q option).
	
To initialize the LDAP database we have to create a pair of files, one for the organization and one for the root DN. For example, create storm.cnaf.infn.it.ldif as follow:

	  dn: dc=storm,dc=cnaf,dc=infn.it
	  objectClass: dcObject
	  objectClass: organization
	  dc: storm
	  o: StoRM
	
It contains the organization entry. Then create root.storm.cnaf.infn.it.ldif as follow:

	  dn: cn=root,dc=storm,dc=cnaf,dc=infn.it
	  objectClass: organizationalRole
	  cn: root

and this contains the root DN. Now we have to initialize DB files for content in /var/lib/ldap directory:

```bash
$ echo "" | slapadd -f /etc/openldap/slapd.conf
```

This is required, otherwise you will get this error:
	  
	  bdb_db_open: database "dc=example,dc=com": db_open(/var/lib/ldap/id2entry.bdb) failed:
	  No such file or directory (2).

Now, only if host is SL6, convert configuration file into dynamic configuration under /etc/openldap/slapd.d directory:
	
```bash  
$ slaptest -f /etc/openldap/slapd.conf -F /etc/openldap/slapd.d
```

For both SL5 and SL6, set permissions:

```bash
	  chown -R ldap:ldap /var/lib/ldap 
```

and, only on SL6, add:

```bash
	  chown -R ldap:ldap /etc/openldap/slapd.d
```

Now we can initialize LDAP DB with already defined initial content, by launching the following commands:

```bash
	  slapadd -l storm.cnaf.infn.it.ldif
	  slapadd -l root.storm.cnaf.infn.it.ldif
```

So we are ready to add to our LDAP database the necessary users and groups. In particular we need storm and gridhttps users and also relative groups. But, how can we organize our directory tree? In a UNIX file system, the top level is the root. Underneath the root you have numerous files and directories. As mentioned above, LDAP directories are set up in much the same manner. Into the directory's base are conventionally created containers that logically separate data. For historical reasons, most LDAP directories set these logical separations up as OU entries. OU stands for "Organizational Unit", which in X.500 was used to indicate the functional organization within a company. Current LDAP implementations have kept the ou= naming convention. In our case, we can define a pair of Organizationa Unit: People and Group as follow:

**People.storm.cnaf.infn.it.ldif**

	  dn: ou=People,dc=storm,dc=cnaf,dc=infn.it
  	  objectClass: organizationalUnit
	  objectClass: top
	  ou: People

**Group.storm.cnaf.infn.it.ldif**

	  dn: ou=Group,dc=storm,dc=cnaf,dc=infn.it
	  objectClass: organizationalUnit
	  objectClass: top
 	  ou: Group

Then, we can define storm and gridhttps users and groups, as follow: 

**storm.People.storm.cnaf.infn.it.ldif**:

	  dn: uid=storm,ou=People,dc=storm,dc=cnaf,dc=infn.it
	  objectClass: account
	  objectClass: posixAccount
	  objectClass: top
	  objectClass: shadowAccount
	  cn: storm
	  gidNumber: 494
	  homeDirectory: /home/storm
	  uid: storm
	  uidNumber: 495
	
**storm.Group.storm.cnaf.infn.it.ldif**:

	  dn: cn=storm,ou=Group,dc=storm,dc=cnaf,dc=infn.it
	  objectClass: posixGroup
	  objectClass: top
	  cn: storm
	  gidNumber: 494
	
**gridhttps.People.storm.cnaf.infn.it.ldif**:

	  dn: uid=gridhttps,ou=People,dc=storm,dc=cnaf,dc=infn.it
	  objectClass: account
	  objectClass: posixAccount
	  objectClass: top
  	  objectClass: shadowAccount
	  cn: gridhttps
	  gidNumber: 504
	  homeDirectory: /home/gridhttps
	  uid: gridhttps
	  uidNumber: 503

**gridhttps.Group.storm.ldif**:

	  dn: cn=gridhttps,ou=Group,dc=storm,dc=cnaf,dc=infn.it
	  objectClass: posixGroup
	  objectClass: top
	  cn: gridhttps
	  gidNumber: 504

Set the various GIDs and UIDs values as you want. Then we can add the users and groups defined to the LDAP server database, with the following commands:

	  slapadd -l storm.Group.storm.cnaf.infn.it.ldif
	  slapadd -l gridhttps.Group.storm.cnaf.infn.it.ldif
	  slapadd -l storm.People.storm.cnaf.infn.it.ldif
	  slapadd -l gridhttps.People.storm.cnaf.infn.it.ldif
	
Using a free program like Apache Directory Studio we can easily connect to the LDAP server and add other entries, export ldif configurations, etc. Important: as you can see from the files above, a user must contain account, posixAccount and shadowAccount objectClasses, a group instead must define only a posixGroup objectClass.

To start server:

```bash
(SL5) $ service ldap start
(SL6) $ service slapd start
```

#### OpenLDAP Client installation and configuration <a name="LDAPClientIC">&nbsp;</a>

To install OpenLDAP service on a client, as root user on a SL5 host, we have to install *openldap-clients* and *nss_ldap* packages:

```bash
$ yum install openldap-clients nss_ldap
```

Instead, as root user on a SL6 host we have to install both *openldap-clients* and *nss-pam-ldapd* packages:

```bash
$ yum install openldap-clients nss-pam-ldapd
```

Be sure that service *nscd* is stopped:

```bash
$ service nscd status
```

Modify **/etc/nsswitch.conf** by adding "*ldap*" to the following lines:
	     
	  passwd: files ldap
	  shadow: files ldap
	  group: files ldap

If SL5, modify both **/etc/ldap.conf** and **/etc/openldap/ldap.conf** by adding:
	
	  uri ldap://<ldap-server-hostname>
	  base dc=storm,dc=cnaf,dc=infn.it

If SL6 modify both **/etc/openldap/ldap.conf** and **/etc/nslcd.conf** by adding: 

	  uri ldap://<ldap-server-hostname>
	  base dc=storm,dc=cnaf,dc=infn.it

#### Test client-server LDAP installation <a name="TestLDAP">&nbsp;</a>

From a configured client we need to know UIDs and/or GIDs of server's LDAP users. That users has not to be defined as UNIX-users on clients. To query the LDAP server from one of the clients type, for example, you can list all the contents in db:

```bash
$ ldapsearch -x -b 'dc=storm,dc=cnaf,dc=infn.it'
```

you can search a particular uid or group:

```bash
$ ldapsearch -x "uid=storm"
$ ldapsearch -x "group=storm"
```

or you can get the UID or GID of a username: 

```bash
$ id -u storm
$ id -g storm
```

Verify that the obtained values are equals to the previous defined.