# 
# RPM spec file for the StoRM BackEnd server.
#
# Copyright (c) 2008 INFN 
# AUTHORS: Magnoni Luca, Ronchieri Elisabetta
# 
# You may copy, modify and distribute this file under the same
# terms as the StoRM BackEnd itself.
#
#

%define debug_package %{nil}  # no debug package (not useful with Java)

### Package Naming 

%define _modulename backend-server

%define prefixname storm
%define longname   storm-backend-server
%define default_user root

%define version @version@
%define age @age@
%define os @os@
%define swig @swig@
%define jdk @jdk@
%define lcmaps @lcmaps@
%define lcmaps_without_gsi @lcmaps_without_gsi@
%define lcmaps_plugins_basic @lcmaps_plugins_basic@
%define lcmaps_plugins_voms @lcmaps_plugins_voms@
%define libacl_devel @libacl_devel@
%define libattr_devel @libattr_devel@

Name:    %{longname}
Version: %{version}
Release: %{age}.%{os}
Summary: The StoRM BackEnd component.
License:  Apache License
Url: http://storm.forge.cnaf.infn.it
Vendor: EMI
Group: Applications/File
Packager: Elisabetta Ronchieri <storm-support@lists.infn.it> 
AutoReqProv:    yes
BuildRoot: %{_tmppath}/%{name}
Source:    %{name}-%{version}.tar.gz
Summary:   The StoRM BackEnd server.

#Added to make the mock build work
BuildRequires: ant
BuildRequires: swig
BuildRequires: e2fsprogs-devel
BuildRequires: libattr-devel
BuildRequires: libacl-devel
BuildRequires: java-devel
BuildRequires: lcmaps-interface
BuildRequires: lcmaps-without-gsi
BuildRequires: lcmaps-plugins-basic
BuildRequires: lcmaps-plugins-voms

Requires(post):   chkconfig
Requires(preun):  chkconfig
Requires(preun):  initscripts
Requires(postun): initscripts

Requires: mysql
Requires: mysql-server
Requires: lcmaps
Requires: lcmaps-interface
Requires: lcmaps-without-gsi
Requires: lcmaps-plugins-basic
Requires: lcmaps-plugins-voms
Requires: java
Requires: nc
Requires: xml-commons-apis
Requires: mysql-connector-java

%define _datadir               /usr/share
%define _sysconfdir            /etc
%define _libdir                /usr/%{_lib}
%define _javadir               %{_datadir}/java
%define _varlog                /var/log

### Package Description

%description
This package contains the StoRM BackEnd server.

StoRM provides an SRM interface to any POSIX filesystem with direct file
access ("file:" transport protocol), but can take advantage of special
features of high performance parallel and cluster file systems, as GPFS from 
IBM and Lustre from SUN.

%prep
%setup -q -n %{name}

%build
ant -Dswig.location="%{swig}" -Djdk.location="%{jdk}" -Dlcmaps.location="%{lcmaps}" -Dlcmaps-without-gsi.location="%{lcmaps_without_gsi}" -Dlcmaps-plugins-basic.location="%{lcmaps_plugins_basic}" -Dlcmaps-plugins-voms.location="%{lcmaps_plugins_voms}" -Dlibacl-devel.location=%{libacl_devel} -Dlibattr-devel.location="%{libattr_devel}" -Dversion="%{version}" -Dage="%{age}" -Dplatform.acronym="%{os}" build

%pre
TMP_FOLDER=%{_sysconfdir}/%{prefixname}/tmp/storm_permissions_mantainance
rm -rf ${TMP_FOLDER}

if [ -d %{_sysconfdir}/%{prefixname} ] ; then
  STORM_CONF_OWNNER_USER=`stat -c %U %{_sysconfdir}/%{prefixname}`
  STORM_CONF_OWNNER_GROUP=`stat -c %G %{_sysconfdir}/%{prefixname}`
  if [ ! "x${STORM_CONF_OWNNER_USER}" = "x%{default_user}" ] ; then
    echo "preserving %{_sysconfdir}/%{prefixname} folder owner ownership"
    umask 077 && mkdir -p ${TMP_FOLDER}
    echo "export STORM_CONF_OWNNER_USER=${STORM_CONF_OWNNER_USER}" >> ${TMP_FOLDER}/export_vars
    echo "unset STORM_CONF_OWNNER_USER" >> ${TMP_FOLDER}/unset_vars
  fi
  if [ ! "x${STORM_CONF_OWNNER_GROUP}" = "x%{default_user}" ] ; then
    echo "preserving %{_sysconfdir}/%{prefixname} folder group ownership"
    echo "export STORM_CONF_OWNNER_GROUP=${STORM_CONF_OWNNER_GROUP}" >> ${TMP_FOLDER}/export_vars
    echo "unset STORM_CONF_OWNNER_GROUP" >> ${TMP_FOLDER}/unset_vars
  fi
fi
if [ -d %{_varlog}/%{prefixname} ] ; then
  STORM_LOG_OWNNER_USER=`stat -c %U %{_varlog}/%{prefixname}`
  STORM_LOG_OWNNER_GROUP=`stat -c %G %{_varlog}/%{prefixname}`
  if [ ! "x${STORM_LOG_OWNNER_USER}" = "x%{default_user}" ] ; then
    echo "preserving %{_varlog}/%{prefixname} folder owner ownership"
    umask 077 && mkdir -p ${TMP_FOLDER}
    echo "export STORM_LOG_OWNNER_USER=${STORM_LOG_OWNNER_USER}" >> ${TMP_FOLDER}/export_vars
    echo "unset STORM_LOG_OWNNER_USER" >> ${TMP_FOLDER}/unset_vars
  fi
  if [ ! "x${STORM_LOG_OWNNER_GROUP}" = "x%{default_user}" ] ; then
    echo "preserving %{_varlog}/%{prefixname} folder group ownership"
    echo "export STORM_LOG_OWNNER_GROUP=${STORM_LOG_OWNNER_GROUP}" >> ${TMP_FOLDER}/export_vars
    echo "unset STORM_LOG_OWNNER_GROUP" >> ${TMP_FOLDER}/unset_vars
  fi
fi

%install
rm -rf $RPM_BUILD_ROOT
ant -Dswig.location="%{swig}" -Djdk.location="%{jdk}" -Dlcmaps.location="%{lcmaps}" -Dlcmaps-without-gsi.location="%{lcmaps_without_gsi}" -Dlcmaps-plugins-basic.location="%{lcmaps_plugins_basic}" -Dlcmaps-plugins-voms.location="%{lcmaps_plugins_voms}" -Dlibacl-devel.location=%{libacl_devel} -Dlibattr-devel.location="%{libattr_devel}" -Dversion="%{version}" -Dage="%{age}" -Dplatform.acronym="%{os}" -Dprefix="$RPM_BUILD_ROOT" install
echo $RPM_BUILD_ROOT
mkdir -p $RPM_BUILD_ROOT%{_sysconfdir}/init.d
mkdir -p $RPM_BUILD_ROOT%{_sysconfdir}/cron.d
mkdir -p $RPM_BUILD_ROOT%{_sysconfdir}/logrotate.d
mkdir -p $RPM_BUILD_ROOT%{_sysconfdir}/sysconfig
mkdir -p $RPM_BUILD_ROOT%{_varlog}/%{prefixname}

%post
#during an install, the value of the argument passed in is 1
#during an upgrade, the value of the argument passed in is 2
if [ "$1" = "1" ] ; then
  echo 'add service to chkconfig'
  /sbin/chkconfig --add %{longname}

  # add symbolic link
  echo 'add symbolic link'
  /bin/ln -sf /usr/share/java/mysql-connector-java-5.1.12.jar %{_javadir}/%{longname}/mysql-connector-java-5.1.12.jar
fi;
if [ "$1" = "2" ] ; then
  echo "The StoRM BackEnd server has been upgraded but NOT configured yet.
You need to use yaim to configure the server.
"
  if [ ! -e %{_javadir}/%{longname}/mysql-connector-java-5.1.12.jar ] ; then
    echo 'add symbolic link'
    /bin/ln -sf /usr/share/java/mysql-connector-java-5.1.12.jar %{_javadir}/%{longname}/mysql-connector-java-5.1.12.jar
  fi
    
  echo 'stop service'
  /sbin/service %{longname} stop >/dev/null 2>&1 || :
fi;

TMP_FOLDER=%{_sysconfdir}/%{prefixname}/tmp/storm_permissions_mantainance

if [ -d %{_sysconfdir}/%{prefixname} ] ; then
  if [ -d ${TMP_FOLDER} ] ; then
    echo "restoring %{_sysconfdir}/%{prefixname} and %{_varlog}/%{prefixname} folder ownership"
    if [ -f ${TMP_FOLDER}/export_vars -a -f ${TMP_FOLDER}/unset_vars ] ; then
      source ${TMP_FOLDER}/export_vars
      DONE="false"
      if [ ! -z ${STORM_CONF_OWNNER_USER} ] ; then
        echo "Setting %{_sysconfdir}/%{prefixname} user ownership to ${STORM_CONF_OWNNER_USER}"
        chown ${STORM_CONF_OWNNER_USER} %{_sysconfdir}/%{prefixname}
        DONE="true"
      fi
      if [ ! -z ${STORM_CONF_OWNNER_GROUP} ] ; then
        echo "Setting %{_sysconfdir}/%{prefixname} group ownership to ${STORM_CONF_OWNNER_GROUP}"
        chgrp ${STORM_CONF_OWNNER_GROUP} %{_sysconfdir}/%{prefixname}
        DONE="true"
      fi
      if [ ! -z ${STORM_LOG_OWNNER_USER} ] ; then
        echo "Setting %{_varlog}/%{prefixname} user ownership to ${STORM_LOG_OWNNER_USER}"
        chown ${STORM_LOG_OWNNER_USER} %{_varlog}/%{prefixname}
        DONE="true"
      fi
      if [ ! -z ${STORM_LOG_OWNNER_GROUP} ] ; then
        echo "Setting %{_varlog}/%{prefixname} group ownership to ${STORM_LOG_OWNNER_GROUP}"
        chgrp ${STORM_LOG_OWNNER_GROUP} %{_varlog}/%{prefixname}
        DONE="true"
      fi
      if [ ! "x$DONE" = "xtrue" ] ; then
        echo "Error. No STORM_CONF_OWNNER_GROUP or STORM_CONF_OWNNER_USER variable available"
        exit 1
      fi
      source ${TMP_FOLDER}/unset_vars
      DONE=""
    else
      echo "Error. No env variable files available in ${TMP_FOLDER}"
      exit 1
    fi
    rm -rf ${TMP_FOLDER}
  fi
fi


%preun
#during an upgrade, the value of the argument passed in is 1
#during an uninstall, the value of the argument passed in is 0
if [ "$1" = "0" ] ; then
  echo 'stop service'
  /sbin/service %{longname} stop >/dev/null 2>&1 || :
  echo 'del service from chkconfig'
  /sbin/chkconfig --del %{longname}
  
  #remove files from folders belonging to the rpm
  echo 'remove generated file'
  rm -f %{_sysconfdir}/%{prefixname}/%{_modulename}/namespace.xml.*
  rm -f %{_sysconfdir}/%{prefixname}/%{_modulename}/storm.properties

  #remove symbolic link
  echo 'remove symbolic link'
  rm -f %{_javadir}/%{longname}/mysql-connector-java-5.1.12.jar
fi;
if [ "$1" = "1" ] ; then
  echo "The StoRM BackEnd server has been upgraded but NOT configured yet.
You need to use yaim to configure the server.
"
  if [ ! -e %{_javadir}/%{longname}/mysql-connector-java-5.1.12.jar ] ; then
    echo 'add symbolic link'
    /bin/ln -sf /usr/share/java/mysql-connector-java-5.1.12.jar %{_javadir}/%{longname}/mysql-connector-java-5.1.12.jar
  fi

  echo 'stop service'
  /sbin/service %{longname} stop >/dev/null 2>&1 || :
fi;

%postun
#during an upgrade, the value of the argument passed in is 1
#during an uninstall, the value of the argument passed in is 0
if [ "$1" = "0" ] ; then
  #remove files from folders not belonging to the rpm
  echo 'remove old file'
  rm -f %{_sysconfdir}/init.d/%{longname}.*
  rm -f %{_sysconfdir}/cron.d/%{longname}.cron.*

  #remove symbolic link
  echo 'remove symbolic link'
  rm -f %{_javadir}/%{longname}/mysql-connector-java-5.1.12.jar
fi;

if [ "$1" = "1" ] ; then
  echo "The StoRM BackEnd server has been upgraded but NOT configured yet.
You need to use yaim to configure the server.
"
  if [ ! -e %{_javadir}/%{longname}/mysql-connector-java-5.1.12.jar ] ; then
    echo 'add symbolic link'
    /bin/ln -sf /usr/share/java/mysql-connector-java-5.1.12.jar %{_javadir}/%{longname}/mysql-connector-java-5.1.12.jar
  fi

  echo 'stop service'
  /sbin/service %{longname} stop >/dev/null 2>&1 || :
fi;

%files
%defattr(-,%{default_user},%{default_user})

%dir %{_datadir}/doc/%{name}-%{version} 
%{_datadir}/doc/%{name}-%{version}/CREDITS
%{_datadir}/doc/%{name}-%{version}/RELEASE-NOTES.txt
%{_datadir}/doc/%{name}-%{version}/LICENSE.txt
%{_datadir}/doc/%{name}-%{version}/ACKNOWLEDGEMENTS.txt

%dir %{_libdir}/%{longname}
%{_libdir}/%{longname}/libposixapi_interface.so
%{_libdir}/%{longname}/libgpfsapi_interface.so
%{_libdir}/%{longname}/libstorm_cutil.so
%{_libdir}/%{longname}/libstorm_lcmaps.so

%dir %{_javadir}/%{longname}
%{_javadir}/%{longname}/storm-backend-server.jar

%dir %attr(755,%{default_user},%{default_user}) %{_sysconfdir}/%{prefixname}
%attr(755,%{default_user},%{default_user}) %{_sysconfdir}/%{prefixname}/%{_modulename}/db/storm_database_config.sh
%config(noreplace) %attr(644,%{default_user},%{default_user}) %{_sysconfdir}/logrotate.d/%{longname}
%config(noreplace) %attr(644,%{default_user},%{default_user}) %{_sysconfdir}/cron.d/%{longname}.cron
%config(noreplace) %attr(644,%{default_user},%{default_user}) %{_sysconfdir}/sysconfig/%{longname}
%attr(755,%{default_user},%{default_user}) %{_sysconfdir}/init.d/%{longname}

%{_sysconfdir}/%{prefixname}/%{_modulename}/db/storm_be_ISAM_mysql_update_from_1.0.0_to_1.1.0.sql
%{_sysconfdir}/%{prefixname}/%{_modulename}/db/storm_mysql_grant.sql
%{_sysconfdir}/%{prefixname}/%{_modulename}/db/storm_mysql_tbl.sql
%{_sysconfdir}/%{prefixname}/%{_modulename}/lcmaps.db
%config(noreplace) %{_sysconfdir}/%{prefixname}/%{_modulename}/logging.xml
%{_sysconfdir}/%{prefixname}/%{_modulename}/namespace-1.5.0.xsd
%config(noreplace) %{_sysconfdir}/%{prefixname}/%{_modulename}/namespace.xml
%{_sysconfdir}/%{prefixname}/%{_modulename}/path-authz.db
%{_sysconfdir}/%{prefixname}/%{_modulename}/storm.properties.template
%{_sysconfdir}/%{prefixname}/%{_modulename}/used-space.ini.template
%{_sysconfdir}/%{prefixname}/%{_modulename}/welcome.txt

# empty directories; nonetheless include in RPM
%dir %{_varlog}/%{prefixname}

%{_javadir}/%{longname}/activation.jar
%{_javadir}/%{longname}/asm-3.1.jar
%{_javadir}/%{longname}/axis.jar
%{_javadir}/%{longname}/axis-schema.jar
%{_javadir}/%{longname}/cog-axis.jar
%{_javadir}/%{longname}/cog-jglobus.jar
%{_javadir}/%{longname}/cog-jobmanager.jar
%{_javadir}/%{longname}/cog-url.jar
%{_javadir}/%{longname}/commons-collections-3.1.jar
%{_javadir}/%{longname}/commons-configuration-1.4.jar
%{_javadir}/%{longname}/commons-logging-1.1.jar
%{_javadir}/%{longname}/commons-dbcp-1.2.1.jar
%{_javadir}/%{longname}/commons-cli-1.0.jar
%{_javadir}/%{longname}/commons-codec-1.3.jar
%{_javadir}/%{longname}/commons-validator-1.3.0.jar
%{_javadir}/%{longname}/commons-beanutils.jar
%{_javadir}/%{longname}/commons-lang-2.3.jar
%{_javadir}/%{longname}/commons-io-2.0.1.jar
%{_javadir}/%{longname}/commons-digester-1.7.jar
%{_javadir}/%{longname}/commons-pool-1.2.jar
%{_javadir}/%{longname}/commons-discovery-0.2.jar
%{_javadir}/%{longname}/cryptix-asn1.jar
%{_javadir}/%{longname}/cryptix32.jar
%{_javadir}/%{longname}/dom.jar
%{_javadir}/%{longname}/grizzly-webserver-1.9.15b.jar
%{_javadir}/%{longname}/ini4j-0.5.2.jar
%{_javadir}/%{longname}/jce-jdk13-131.jar
%{_javadir}/%{longname}/jgss.jar
%{_javadir}/%{longname}/jaxrpc.jar
%{_javadir}/%{longname}/jakarta-oro-2.0.8.jar
%{_javadir}/%{longname}/jersey-core-1.1.0-ea.jar
%{_javadir}/%{longname}/jersey-server-1.1.0-ea.jar
%{_javadir}/%{longname}/jettison-1.1.jar
%{_javadir}/%{longname}/jna.jar
%{_javadir}/%{longname}/jsr311-api-1.1.jar
%{_javadir}/%{longname}/json-20080701.jar
%{_javadir}/%{longname}/log4j-1.2.13.jar
%{_javadir}/%{longname}/logback-classic-0.9.15.jar
%{_javadir}/%{longname}/logback-core-0.9.15.jar
%{_javadir}/%{longname}/mail.jar
%{_javadir}/%{longname}/puretls.jar
%{_javadir}/%{longname}/saaj.jar
%{_javadir}/%{longname}/srm22client.jar
%{_javadir}/%{longname}/slf4j-api-1.5.6.jar
%{_javadir}/%{longname}/stax-api-1.0-2.jar
%{_javadir}/%{longname}/ws-commons-util-1.0.1.jar
%{_javadir}/%{longname}/wsdl4j-1.5.1.jar
%{_javadir}/%{longname}/xmlrpc-server-3.0.jar
%{_javadir}/%{longname}/xml-apis.jar
%{_javadir}/%{longname}/xmlrpc-common-3.0.jar
%{_javadir}/%{longname}/xalan.jar
%{_javadir}/%{longname}/xercesImpl.jar

%dir %{_javadir}/%{longname}/doc
%{_javadir}/%{longname}/doc/puretls.LICENSE
%{_javadir}/%{longname}/doc/junit.LICENSE
%{_javadir}/%{longname}/doc/bouncycastle.LICENSE
%{_javadir}/%{longname}/doc/commons-logging.LICENSE
%{_javadir}/%{longname}/doc/cog.LICENSE
%{_javadir}/%{longname}/doc/cryptix.LICENSE


%clean
rm -rf $RPM_BUILD_ROOT

### Package ChangeLog

%changelog
* Fri Jun 24 2011 <Riccardo, Elisabetta and Michele> 1.7.2-2.sl5
- Added consistency on Space Usage model (free, busy and used space)
- Fixed a bug occurring during the startup (sanity-check)
- Disk Usage tasks are performed in background and silently during the startup
- Space usage are updated after putDone and Rm
- Added commons-io package dependency
- Fixed a bunch of bugs (see StoRM bug trucker and SVN logs for further details)

* Fri Jun 24 2011 <Riccardo, Elisabetta and Michele>
- version 1.7.0-9
- Added consistency on Space Usage model (free, busy and used space)
- Fixed a bug occurring during the startup (sanity-check)
- Disk Usage tasks are performed in background and silently during the startup
- Space usage are updated after putDone and Rm
- Added commons-io package dependency
- Fixed a bunch of bugs (see StoRM bug trucker and SVN logs for further details)

* Fri May 27 2011 <Riccardo, Elisabetta and Michele>
- version 1.7.0-1
- renamed storm-backend in storm-backend-server
- removed package constraints
- updated database storm_be_ISAM to version 1.1.0
- created an upgrade script for storm_be_ISAM from version 1.0.0 to 1.1.0
- fixed a bug on suspended chunks completion
- renamed tape recall related classes to contain TapeRecall in their name
- removed some direct accesses to tape recall DAO
- added the groupTaskId field to tape_recall table to group different recall requests for the same file executed at the same time
- changed configuration files installation folder to /etc/storm/backend-server
- moved sysconfig/storm-backend-server in /etc/sysconfig folder
- created tar.bin ant target to produce a tar.gz of the output of the install ant target
- fixed a bug on storage are space management upon namespace.xml changes
- added state progress timestamps to tape recall requests
- added rest service to update storage space information

* Fri Feb 16 2011 <Michele Dibenedetto> <michele.dibenedetto@cnaf.infn.it>
- version 1.6.2-1
- removed extended attributes mount option sanity check
- added extended ACL sanity check
- fixed a bug on sanity check failure notification
- added distinction between critical and not critical checks

* Fri Feb 4 2011 <Michele Dibenedetto> <michele.dibenedetto@cnaf.infn.it>
- version 1.6.1-1
- fixed a bug on VomsGridUser initialization causing wrong lcmaps mappings

* Tue Nov 15 2010 <Elisabetta Ronchieri> <elisabetta.ronchieri@cnaf.infn.it>
- version 1.6.0-6
- fixed a bug on concurrent local groups handling
- added a mechanism to check if filesystem is GPFS
- solved a bug on libstorm_cutil.so library loading
- added checking of modules path existence in the start up script
- added a check on in on etc/namespace.xml to grant uniqueness of field space-token-description

* Tue Oct 14 2010 <Michele Dibenedetto> <michele.dibenedetto@cnaf.infn.it>
- version 1.6.0-5
- fixed a bug on recall_table database connection management
- fixed a bug in management of request coming from users providing certificates without VOMS extension
- avoided exception raising in case of status check of a tape recall request expired and cleaned by garbage collector 

* Tue Aug 24 2010 <Elisabetta Ronchieri> <elisabetta.ronchieri@cnaf.infn.it>
- version 1.5.4-6
- joined jar and server packages in one package
- removed storm-backend-configure
- removed sbin directory
- changed lib (jar and native) structure
- moved main jar (storm-backend.jar) into %{prefix}

* Fri May 21 2010 <Elisabetta Ronchieri> <elisabetta.ronchieri@cnaf.infn.it>
- version %{version}-%{release}
- changed the handling of init script and cron file
- introduced lynks

* Wed May 19 2010 <Elisabetta Ronchieri> <elisabetta.ronchieri@cnaf.infn.it>
- version %{version}-%{release}
- changed the handling of init script and cron file
- introduced lynks

* Tue Dec 22 2009 <Riccardo Zappi> <riccardo.zappi@cnaf.infn.it>
- version 1.5.0-rc4
- changed 'path-authz.db' from 'path-authz.db.template'
- changed 'namespace-1.5.0.xsd' from 'namespace.xsd'

* Tue Dec 22 2009 <Alberto Forti> <alberto.forti@cnaf.infn.it>
- version 1.5.0-rc1/rc2/rc3
 - ...  
  
* Thu May 14 2009 <Magnoni Luca> <luca.magnoni@cnaf.infn.it>
- final 1.4.0 release

* Mon May 11 2009 <Magnoni Luca> <luca.magnoni@cnaf.infn.it>
- version 1.4.0-rc5
- Fix Non-VOMS proxy support

* Fri Feb 13 2009 <Magnoni Luca> <luca.magnoni@cnaf.infn.it>

- version 1.4.0-rc3 , 
- fix in the hearthbeat for synchcall.
- fix for GFAL/lcg-utils >1.1.11 for surl with incomplete SAPath

* Wed Jan 21 2009 <Magnoni Luca> <luca.magnoni@cnaf.infn.it>
- version 1.4.0-rc2
- Bug fixes on Storage Area creation and FQANs representation

* Mon Dec 15 2008 <Magnoni Luca> <luca.magnoni@cnaf.infn.it>
- version 1.4.0-rc1
- http://storm.forge.cnaf.infn.it/documentation/changelog

* Mon Apr 10 2008 <Magnoni Luca> <luca.magnoni@cnaf.infn.it>

- version 1.3.20

* Wed Mar 5 2008 <Magnoni Luca> <luca.magnoni@cnaf.infn.it>
- version 1.3.19-02
- New default ACL capability by Storage Area for the CMS use case.
- New Root protocol support.
- New srmGetSpaceMetaData bounded with GPFS fileset and quota.

* Wed Feb 6 2008 <Magnoni Luca> <luca.magnoni@cnaf.infn.it>
- version 1.3.19-01
- Fix supported protocol evaluation order
- Add heartbeath component
- Fix bug for StorageArea space token
- Add root file protocol support
* Wed Jan 30 2008 <Magnoni Luca> <luca.magnoni@cnaf.infn.it>
- version 1.3.19 
- Database cleaninig
- New Log at INFO level
- New srmGetSpaceMetaData
- T1D1 with GPFS 
* Mon Jun 19 2007 <Magnoni Luca> <luca.magnoni@cnaf.infn.it>
- version 1.3.18
- release 0 Add VO_SA Token in namespace.xml and app-rule.
* Mon Jun 19 2007 <Magnoni Luca> <luca.magnoni@cnaf.infn.it>
-version 1.3.16
- Fix in many open issues...

- version 1.1.3: New FileSystem included
- release 0
- release 1-3
- release 4 Added srmReleaseFiles
* Thu Mar 16 2006  Riccardo Murri <riccardo.murri@ictp.it>
- version 1.1.1: serialize calls to LCMAPS since it's not thread-safe
- release 0 new NaiveSRMClient
- release 1 fix in NaiveSRMClient
- release 2 
- release 3 GSIFTP transfer protocol in local request
- release 4 NaiveGridFTPClient modified
* Fri Mar 10 2006 <Magnoni Luca> <luca.magnoni@cnaf.infn.it>
- version 1.1.0: Directory Function included
- release 1: wrapper library fix 
- release 2: Lcmaps_interface fix
- release 3: Lcmaps_interface reentrant code fix

* Thu Dec 15 2005  Riccardo Murri <riccardo.murri@ictp.it>
- version 1.0.1: fix typo in initscript
- version 1.0.0: match released FE version
- removed VOMS API dependency

* Wed Dec  7 2005  Riccardo Murri <riccardo.murri@ictp.it>
- version 0.8.6: improved startup script
- release 7: clean build
- release 6: split BE code and 3rd party .jars in two separate
  packages for licensing issues.

* Wed Nov 30 2005  Riccardo Murri <riccardo.murri@ictp.it>
- version 0.8.4

* Tue Nov 29 2005  Riccardo Murri <riccardo.murri@ictp.it>
- added empty directories /opt/storm/var/{log,tmp}
- version 0.8.2: download required jars not included in the distribution
- version 0.8.3: gLite 1.4

* Thu Nov 24 2005  Riccardo Murri <riccardo.murri@ictp.it>
- added 'Prefix:' line for relocation
- added dependencies
- make file list from install dir contents

* Thu Nov 17 2005  Riccardo Murri <riccardo.murri@ictp.it>
- First attempt


