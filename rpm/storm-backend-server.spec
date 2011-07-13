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

%define _generalprefix storm
%define _modulename backend-server
%define _servicename %{_generalprefix}-%{_modulename}

%define default_user root

Name: %{_servicename}
Version: %{version}
Release: %{age}.%{distribution}
Summary: The StoRM BackEnd component.
License:  Apache License
Url: http://storm.forge.cnaf.infn.it
Vendor: INFN - CNAF (2009)
Group: Applications/File
Packager: Luca Magnoni <storm-support@lists.infn.it> 
AutoReqProv:    yes

BuildRoot: %{_tmppath}/%{name}
Source: %{name}-%{version}.tar.gz
Summary: The StoRM BackEnd server.

#Added to make the mock build work
#Commented to avoid breaking ETICS build
#BuildRequires: ant
#BuildRequires: swig
#BuildRequires: xfsprogs-devel
#BuildRequires: libattr-devel
#BuildRequires: libacl-devel
#BuildRequires: java-devel
#BuildRequires: lcmaps-interface
#BuildRequires: lcmaps-without-gsi
#BuildRequires: lcmaps-plugins-basic
#BuildRequires: lcmaps-plugins-voms

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
Requires: java-devel
Requires: nc

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
ant -Dswig.location="%{swig}" -Djdk.location="%{jdk}" -Dlcmaps.location="%{lcmaps}" -Dlcmaps-without-gsi.location="%{lcmaps_without_gsi}" -Dlcmaps-plugins-basic.location="%{lcmaps_plugins_basic}" -Dlcmaps-plugins-voms.location="%{lcmaps_plugins_voms}" -Dlibacl-devel.location=%{libacl_devel} -Dlibattr-devel.location="%{libattr_devel}" -Dxfsprogs-devel.location="%{xfsprogs_devel}" -Dversion="%{version}" -Dage="%{age}" build

%pre
TMP_FOLDER=%{_sysconfdir}/%{_generalprefix}/tmp/storm_permissions_mantainance
rm -rf ${TMP_FOLDER}

if [ -d %{_sysconfdir}/%{_generalprefix} ] ; then
  STORM_CONF_OWNNER_USER=`stat -c %U %{_sysconfdir}/%{_generalprefix}`
  STORM_CONF_OWNNER_GROUP=`stat -c %G %{_sysconfdir}/%{_generalprefix}`
  if [ ! "x${STORM_CONF_OWNNER_USER}" = "x%{default_user}" ] ; then
    echo "preserving %{_sysconfdir}/%{_generalprefix} folder owner ownership"
    umask 077 && mkdir -p ${TMP_FOLDER}
    echo "export STORM_CONF_OWNNER_USER=${STORM_CONF_OWNNER_USER}" >> ${TMP_FOLDER}/export_vars
    echo "unset STORM_CONF_OWNNER_USER" >> ${TMP_FOLDER}/unset_vars
  fi
  if [ ! "x${STORM_CONF_OWNNER_GROUP}" = "x%{default_user}" ] ; then
    echo "preserving %{_sysconfdir}/%{_generalprefix} folder group ownership"
    echo "export STORM_CONF_OWNNER_GROUP=${STORM_CONF_OWNNER_GROUP}" >> ${TMP_FOLDER}/export_vars
    echo "unset STORM_CONF_OWNNER_GROUP" >> ${TMP_FOLDER}/unset_vars
  fi
fi
if [ -d %{_varlog}/%{_generalprefix} ] ; then
  STORM_LOG_OWNNER_USER=`stat -c %U %{_varlog}/%{_generalprefix}`
  STORM_LOG_OWNNER_GROUP=`stat -c %G %{_varlog}/%{_generalprefix}`
  if [ ! "x${STORM_LOG_OWNNER_USER}" = "x%{default_user}" ] ; then
    echo "preserving %{_varlog}/%{_generalprefix} folder owner ownership"
    umask 077 && mkdir -p ${TMP_FOLDER}
    echo "export STORM_LOG_OWNNER_USER=${STORM_LOG_OWNNER_USER}" >> ${TMP_FOLDER}/export_vars
    echo "unset STORM_LOG_OWNNER_USER" >> ${TMP_FOLDER}/unset_vars
  fi
  if [ ! "x${STORM_LOG_OWNNER_GROUP}" = "x%{default_user}" ] ; then
    echo "preserving %{_varlog}/%{_generalprefix} folder group ownership"
    echo "export STORM_LOG_OWNNER_GROUP=${STORM_LOG_OWNNER_GROUP}" >> ${TMP_FOLDER}/export_vars
    echo "unset STORM_LOG_OWNNER_GROUP" >> ${TMP_FOLDER}/unset_vars
  fi
fi

%install
rm -rf $RPM_BUILD_ROOT
ant -Dswig.location="%{swig}" -Djdk.location="%{jdk}" -Dlcmaps.location="%{lcmaps}" -Dlcmaps-without-gsi.location="%{lcmaps_without_gsi}" -Dlcmaps-plugins-basic.location="%{lcmaps_plugins_basic}" -Dlcmaps-plugins-voms.location="%{lcmaps_plugins_voms}" -Dlibacl-devel.location=%{libacl_devel} -Dlibattr-devel.location="%{libattr_devel}" -Dxfsprogs-devel.location="%{xfsprogs_devel}" -Dversion="%{version}" -Dage="%{age}" -Dprefix="$RPM_BUILD_ROOT" install
echo $RPM_BUILD_ROOT
mkdir -p $RPM_BUILD_ROOT%{_sysconfdir}/init.d
mkdir -p $RPM_BUILD_ROOT%{_sysconfdir}/cron.d
mkdir -p $RPM_BUILD_ROOT%{_sysconfdir}/logrotate.d
mkdir -p $RPM_BUILD_ROOT%{_sysconfdir}/sysconfig
mkdir -p $RPM_BUILD_ROOT%{_varlog}/%{_generalprefix}

%post
#during an install, the value of the argument passed in is 1
#during an unupgrade, the value of the argument passed in is 2
if [ "$1" = "1" ] ; then
  echo 'add service to chkconfig'
  /sbin/chkconfig --add %{_servicename}
fi;
if [ "$1" = "2" ] ; then
  echo "The StoRM FrontEnd server has been upgraded but NOT configured yet.
You need to use yaim to configure the server.
"
  echo 'stop service'
  /sbin/service %{_servicename} stop >/dev/null 2>&1 || :
fi;

TMP_FOLDER=%{_sysconfdir}/%{_generalprefix}/tmp/storm_permissions_mantainance

if [ -d %{_sysconfdir}/%{_generalprefix} ] ; then
  if [ -d ${TMP_FOLDER} ] ; then
    echo "restoring %{_sysconfdir}/%{_generalprefix} and %{_varlog}/%{_generalprefix} folder ownership"
    if [ -f ${TMP_FOLDER}/export_vars -a -f ${TMP_FOLDER}/unset_vars ] ; then
      source ${TMP_FOLDER}/export_vars
      DONE="false"
      if [ ! -z ${STORM_CONF_OWNNER_USER} ] ; then
        echo "Setting %{_sysconfdir}/%{_generalprefix} user ownership to ${STORM_CONF_OWNNER_USER}"
        chown ${STORM_CONF_OWNNER_USER} %{_sysconfdir}/%{_generalprefix}
        DONE="true"
      fi
      if [ ! -z ${STORM_CONF_OWNNER_GROUP} ] ; then
        echo "Setting %{_sysconfdir}/%{_generalprefix} group ownership to ${STORM_CONF_OWNNER_GROUP}"
        chgrp ${STORM_CONF_OWNNER_GROUP} %{_sysconfdir}/%{_generalprefix}
        DONE="true"
      fi
      if [ ! -z ${STORM_LOG_OWNNER_USER} ] ; then
        echo "Setting %{_varlog}/%{_generalprefix} user ownership to ${STORM_LOG_OWNNER_USER}"
        chown ${STORM_LOG_OWNNER_USER} %{_varlog}/%{_generalprefix}
        DONE="true"
      fi
      if [ ! -z ${STORM_LOG_OWNNER_GROUP} ] ; then
        echo "Setting %{_varlog}/%{_generalprefix} group ownership to ${STORM_LOG_OWNNER_GROUP}"
        chgrp ${STORM_LOG_OWNNER_GROUP} %{_varlog}/%{_generalprefix}
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
  /sbin/service %{_servicename} stop >/dev/null 2>&1 || :
  echo 'del service from chkconfig'
  /sbin/chkconfig --del %{_servicename}
  
  #remove files from folders belonging to the rpm
  echo 'remove generated file'
  rm -f %{_sysconfdir}/%{_generalprefix}/%{_modulename}/namespace.xml.*
  rm -f %{_sysconfdir}/%{_generalprefix}/%{_modulename}/storm.properties
fi;
if [ "$1" = "1" ] ; then
  echo "The StoRM FrontEnd server has been upgraded but NOT configured yet.
You need to use yaim to configure the server.
"
  echo 'stop service'
  /sbin/service %{_servicename} stop >/dev/null 2>&1 || :
fi;

%postun
#during an upgrade, the value of the argument passed in is 1
#during an uninstall, the value of the argument passed in is 0
if [ "$1" = "0" ] ; then
  #remove files from folders not belonging to the rpm
  echo 'remove old file'
  rm -f %{_sysconfdir}/init.d/%{_servicename}.*
  rm -f %{_sysconfdir}/cron.d/%{_servicename}.cron.*
fi;

if [ "$1" = "1" ] ; then
  echo "The StoRM FrontEnd server has been upgraded but NOT configured yet.
You need to use yaim to configure the server.
"
  echo 'stop service'
  /sbin/service %{_servicename} stop >/dev/null 2>&1 || :
fi;

# %_prefix                /usr
# %_datadir               %{_prefix}/share
# %_sysconfdir            %{_prefix}/etc
# %_lib                   lib
# %_exec_prefix           %{_prefix}
# %_libdir                %{_exec_prefix}/%{_lib}
# %_javadir               %{_datadir}/java
# %_var                   /var
# %_tmppath               %{_var}/tmp

%files
%defattr(-,%{default_user},%{default_user})

%dir %{_datadir}/doc/%{name}-%{version} 
%{_datadir}/doc/%{name}-%{version}/CREDITS
%{_datadir}/doc/%{name}-%{version}/RELEASE-NOTES.txt
%{_datadir}/doc/%{name}-%{version}/LICENSE.txt
%{_datadir}/doc/%{name}-%{version}/ACKNOWLEDGEMENTS.txt


# %{_libdir}/%{_servicename}/native/%{platform}/libgpfs.so
%dir %{_libdir}/%{_servicename}
%dir %{_libdir}/%{_servicename}/native
%dir %{_libdir}/%{_servicename}/native/%{platform}
%{_libdir}/%{_servicename}/native/%{platform}/libposixapi_interface.so
%{_libdir}/%{_servicename}/native/%{platform}/libgpfsapi_interface.so
%{_libdir}/%{_servicename}/native/%{platform}/libxfsapi_interface.so
%{_libdir}/%{_servicename}/native/%{platform}/libstorm_cutil.so

%dir %{_javadir}/%{_servicename}
%{_javadir}/%{_servicename}/storm-backend-server.jar

%dir %attr(755,%{default_user},%{default_user}) %{_sysconfdir}/%{_generalprefix}
%attr(755,%{default_user},%{default_user}) %{_sysconfdir}/%{_generalprefix}/%{_modulename}/db/storm_database_config.sh
%config(noreplace) %attr(644,%{default_user},%{default_user}) %{_sysconfdir}/logrotate.d/%{_servicename}
%config(noreplace) %attr(644,%{default_user},%{default_user}) %{_sysconfdir}/cron.d/%{_servicename}.cron
%config(noreplace) %attr(644,%{default_user},%{default_user}) %{_sysconfdir}/sysconfig/%{_servicename}
%attr(755,%{default_user},%{default_user}) %{_sysconfdir}/init.d/%{_servicename}


%{_sysconfdir}/%{_generalprefix}/%{_modulename}/db/storm_be_ISAM_mysql_update_from_1.0.0_to_1.1.0.sql
%{_sysconfdir}/%{_generalprefix}/%{_modulename}/db/storm_mysql_grant.sql
%{_sysconfdir}/%{_generalprefix}/%{_modulename}/db/storm_mysql_tbl.sql
%{_sysconfdir}/%{_generalprefix}/%{_modulename}/lcmaps.db
%{_sysconfdir}/%{_generalprefix}/%{_modulename}/logging.xml
%{_sysconfdir}/%{_generalprefix}/%{_modulename}/namespace-1.5.0.xsd
%{_sysconfdir}/%{_generalprefix}/%{_modulename}/namespace.xml
%{_sysconfdir}/%{_generalprefix}/%{_modulename}/path-authz.db
%{_sysconfdir}/%{_generalprefix}/%{_modulename}/storm.properties.template
%{_sysconfdir}/%{_generalprefix}/%{_modulename}/welcome.txt


# empty directories; nonetheless include in RPM
%dir %{_varlog}/%{_generalprefix}
#%dir %{_vartmp}/%{name}

%{_javadir}/%{_servicename}/activation.jar
%{_javadir}/%{_servicename}/asm-3.1.jar
%{_javadir}/%{_servicename}/axis.jar
%{_javadir}/%{_servicename}/axis-schema.jar
%{_javadir}/%{_servicename}/cog-axis.jar
%{_javadir}/%{_servicename}/cog-jglobus.jar
%{_javadir}/%{_servicename}/cog-jobmanager.jar
%{_javadir}/%{_servicename}/cog-url.jar
%{_javadir}/%{_servicename}/commons-collections-3.1.jar
%{_javadir}/%{_servicename}/commons-configuration-1.4.jar
%{_javadir}/%{_servicename}/commons-logging-1.1.jar
%{_javadir}/%{_servicename}/commons-dbcp-1.2.1.jar
%{_javadir}/%{_servicename}/commons-cli-1.0.jar
%{_javadir}/%{_servicename}/commons-codec-1.3.jar
%{_javadir}/%{_servicename}/commons-validator-1.3.0.jar
%{_javadir}/%{_servicename}/commons-beanutils.jar
%{_javadir}/%{_servicename}/commons-lang-2.3.jar
%{_javadir}/%{_servicename}/commons-io-2.0.1.jar
%{_javadir}/%{_servicename}/commons-digester-1.7.jar
%{_javadir}/%{_servicename}/commons-pool-1.2.jar
%{_javadir}/%{_servicename}/commons-discovery-0.2.jar
%{_javadir}/%{_servicename}/cryptix-asn1.jar
%{_javadir}/%{_servicename}/cryptix32.jar
%{_javadir}/%{_servicename}/dom.jar
%{_javadir}/%{_servicename}/grizzly-webserver-1.9.15b.jar
%{_javadir}/%{_servicename}/jce-jdk13-131.jar
%{_javadir}/%{_servicename}/jgss.jar
%{_javadir}/%{_servicename}/jaxrpc.jar
%{_javadir}/%{_servicename}/jakarta-oro-2.0.8.jar
%{_javadir}/%{_servicename}/jersey-core-1.1.0-ea.jar
%{_javadir}/%{_servicename}/jersey-server-1.1.0-ea.jar
%{_javadir}/%{_servicename}/jettison-1.1.jar
%{_javadir}/%{_servicename}/jna.jar
%{_javadir}/%{_servicename}/jsr311-api-1.1.jar
%{_javadir}/%{_servicename}/json-20080701.jar
%{_javadir}/%{_servicename}/log4j-1.2.13.jar
%{_javadir}/%{_servicename}/logback-classic-0.9.15.jar
%{_javadir}/%{_servicename}/logback-core-0.9.15.jar
%{_javadir}/%{_servicename}/mail.jar
%{_javadir}/%{_servicename}/puretls.jar
%{_javadir}/%{_servicename}/saaj.jar
%{_javadir}/%{_servicename}/srm22client.jar
%{_javadir}/%{_servicename}/slf4j-api-1.5.6.jar
%{_javadir}/%{_servicename}/stax-api-1.0-2.jar
%{_javadir}/%{_servicename}/ws-commons-util-1.0.1.jar
%{_javadir}/%{_servicename}/wsdl4j-1.5.1.jar
%{_javadir}/%{_servicename}/xmlrpc-server-3.0.jar
%{_javadir}/%{_servicename}/xml-apis.jar
%{_javadir}/%{_servicename}/xmlrpc-common-3.0.jar
%{_javadir}/%{_servicename}/xalan.jar
%{_javadir}/%{_servicename}/xercesImpl.jar

%dir %{_javadir}/%{_servicename}/doc
%{_javadir}/%{_servicename}/doc/puretls.LICENSE
%{_javadir}/%{_servicename}/doc/junit.LICENSE
%{_javadir}/%{_servicename}/doc/bouncycastle.LICENSE
%{_javadir}/%{_servicename}/doc/commons-logging.LICENSE
%{_javadir}/%{_servicename}/doc/cog.LICENSE
%{_javadir}/%{_servicename}/doc/cryptix.LICENSE


%clean
rm -rf $RPM_BUILD_ROOT

### Package ChangeLog

%changelog
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


