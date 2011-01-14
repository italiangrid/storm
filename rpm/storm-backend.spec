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

Name: storm-backend-server
Version: %{version}
Release: %{age}.%{distribution}
Summary: The StoRM BackEnd component.
License:  Apache License, Version 2.0. See included file LICENSE.txt
Url: http://storm.forge.cnaf.infn.it
Vendor: INFN - CNAF (2009)
Group: Application/Generic
Packager: Luca Magnoni <storm-support@lists.infn.it> 
Prefix: /opt/storm/backend
BuildRoot: %{_tmppath}/%{name}
Source: %{name}.tar.gz
Summary: The StoRM BackEnd server.
Obsoletes: storm-backend-jars
# Requires: redhat-lsb
Requires: nc
Requires: glite-security-lcmaps = 1.4.7-1.%{distributionrpm}
Requires: glite-security-lcmaps-plugins-basic = 1.3.10-2.%{distributionrpm}
Requires: glite-security-lcmaps-plugins-voms = 1.3.7-%{lpvage}.%{distributionrpm}
Requires: glite-security-lcmaps-without-gsi = %{lwgversion}-%{lwgage}.%{distributionrpm}
Requires: vdt_globus_sdk

### Package Description

%description
This package contains the StoRM BackEnd server.

StoRM provides an SRM interface to any POSIX
filesystem with direct file access ("file:" transport protocol), but
can take advantage of special features of high performance parallel and cluster file systems, as 
GPFS from IBM and Lustre from SUN.

The StoRM BackEnd is split into two binary packages:
'storm-backend-server', which contains the actual server binaries and libraries

%prep
%setup -q -n %{name}

%build
ant -Dswig.location="%{swig}" -Djdk.location="%{jdk}" -Dlcmaps.location="%{lcmaps}" -Dlcmaps-without-gsi.location="%{lcmaps_without_gsi}" -Dlcmaps-plugins-basic.location="%{lcmaps_plugins_basic}" -Dlcmaps-plugins-voms.location="%{lcmaps_plugins_voms}" -Dlibacl-devel.location=%{libacl_devel} -Dlibattr-devel.location="%{libattr_devel}" -Dxfsprogs-devel.location="%{xfsprogs_devel}" -Dversion="%{version}" -Dage="%{age}" build

%install
rm -rf $RPM_BUILD_ROOT
ant -Dswig.location="%{swig}" -Djdk.location="%{jdk}" -Dlcmaps.location="%{lcmaps}" -Dlcmaps-without-gsi.location="%{lcmaps_without_gsi}" -Dlcmaps-plugins-basic.location="%{lcmaps_plugins_basic}" -Dlcmaps-plugins-voms.location="%{lcmaps_plugins_voms}" -Dlibacl-devel.location=%{libacl_devel} -Dlibattr-devel.location="%{libattr_devel}" -Dxfsprogs-devel.location="%{xfsprogs_devel}" -Dversion="%{version}" -Dage="%{age}" -Dprefix="$RPM_BUILD_ROOT%{prefix}" install

%post
#during an install, the value of the argument passed in is 1
#during an unupgrade, the value of the argument passed in is 2
if [ "$1" = "1" ] ; then
echo "The StoRM BackEnd server is installed but NOT configured yet.
You need to use yaim to configure the server.
"
echo 'create ln for /etc/init.d/storm-backend'
ln -sf %{prefix}/etc/init.d/storm-backend /etc/init.d/storm-backend
echo 'create ln for /etc/cron.d/storm-backend.cron'
ln -sf %{prefix}/etc/cron.d/storm-backend.cron /etc/cron.d/storm-backend.cron
fi
if [ "$1" = "2" ] ; then
echo "The StoRM BackEnd server has been upgraded but NOT configured yet.
You need to use yaim to configure the server.
"
if [ -s "/etc/init.d/storm-backend" ] ; then
echo 'stop service storm-backend'
/etc/init.d/storm-backend stop &> /dev/null || :
fi
fi;

%preun
#during an upgrade, the value of the argument passed in is 1
#during an uninstall, the value of the argument passed in is 0
if [ "$1" = "0" ] ; then
if [ -s "/etc/init.d/storm-backend" ] ; then
echo 'stop service storm-backend and remove ln /etc/init.d/storm-backend'
/etc/init.d/storm-backend stop &> /dev/null || :
rm -f /etc/init.d/storm-backend
fi
if [ -s "/etc/cron.d/storm-backend.cron" ] ; then
echo 'remove ln /etc/cron.d/storm-backend.cron'
rm -f /etc/cron.d/storm-backend.cron
fi
fi;
if [ "$1" = "1" ] ; then
if [ -s "/etc/init.d/storm-backend" ] ; then
echo 'stop service storm-backend'
/etc/init.d/storm-backend stop &> /dev/null || :
fi
fi;

%postun
#during an upgrade, the value of the argument passed in is 1
#during an uninstall, the value of the argument passed in is 0
if [ "$1" = "1" ] ; then
if [ -s "/etc/init.d/storm-backend" ] ; then
echo 'remove old file'
rm -f /etc/init.d/storm-backend
rm -f /etc/init.d/storm-backend.*
fi
if [ -s "/etc/cron.d/storm-backend.cron" ] ; then
echo 'remove old file'
rm -f /etc/cron.d/storm-backend.cron
rm -f /etc/cron.d/storm-backend.cron.*
fi
echo 'create ln for /etc/init.d/storm-backend'
ln -sf %{prefix}/etc/init.d/storm-backend /etc/init.d/storm-backend
echo 'create ln for /etc/cron.d/storm-backend.cron'
ln -sf %{prefix}/etc/cron.d/storm-backend.cron /etc/cron.d/storm-backend.cron
fi;
if [ "$1" = "0" ] ; then
if [ -s "/etc/init.d/storm-backend" ] ; then
echo 'remove old file'
rm -f /etc/init.d/storm-backend
fi
if [ -s "/etc/cron.d/storm-backend.cron" ] ; then
echo 'remove old file'
rm -f /etc/cron.d/storm-backend.cron
fi
fi;

%files
%defattr(-,root,root)
%{prefix}/doc/CREDITS
%{prefix}/doc/RELEASE-NOTES.txt
%{prefix}/doc/LICENSE.txt
%config(noreplace) %{prefix}/etc/namespace-1.5.0.xsd
%config(noreplace) %{prefix}/etc/logrotate.d/storm-backend.logrotate
%config(noreplace) %{prefix}/etc/db/storm_mysql_grant.sql
%config(noreplace) %{prefix}/etc/db/storm_mysql_tbl.sql
%config(noreplace) %{prefix}/etc/logging.xml
%config(noreplace) %{prefix}/etc/sysconfig/storm-backend
%config(noreplace) %{prefix}/etc/lcmaps.db
%config(noreplace) %{prefix}/etc/storm.properties.template
%config(noreplace) %{prefix}/etc/path-authz.db
%config(noreplace) %{prefix}/etc/welcome.txt
%config(noreplace) %{prefix}/etc/namespace.xml
%{prefix}/lib/native/%{platform}/libgpfsapi_interface.so
%{prefix}/lib/native/%{platform}/libposixapi_interface.so
%{prefix}/lib/native/%{platform}/libxfsapi_interface.so
%{prefix}/lib/native/%{platform}/libstorm_cutil.so
%{prefix}/storm-backend.jar
%defattr(755,root,root)
%config(noreplace) %{prefix}/etc/db/storm_database_config.sh
%attr(755,root,root) %{prefix}/etc/cron.d/storm-backend.cron
%attr(755,root,root) %{prefix}/etc/init.d/storm-backend
# empty directories; nonetheless include in RPM
%defattr(-,root,root)
%dir %{prefix}/var/log
%dir %{prefix}/var/tmp
%defattr(-,root,root)
%{prefix}/lib/activation.jar
%{prefix}/lib/asm-3.1.jar
%{prefix}/lib/axis.jar
%{prefix}/lib/axis-schema.jar
%{prefix}/lib/cog-axis.jar
%{prefix}/lib/cog-jglobus.jar
%{prefix}/lib/cog-jobmanager.jar
%{prefix}/lib/cog-url.jar
%{prefix}/lib/commons-collections-3.1.jar
%{prefix}/lib/commons-configuration-1.4.jar
%{prefix}/lib/commons-logging-1.1.jar
%{prefix}/lib/commons-dbcp-1.2.1.jar
%{prefix}/lib/commons-cli-1.0.jar
%{prefix}/lib/commons-codec-1.3.jar
%{prefix}/lib/commons-validator-1.3.0.jar
%{prefix}/lib/commons-beanutils.jar
%{prefix}/lib/commons-lang-2.3.jar
%{prefix}/lib/commons-digester-1.7.jar
%{prefix}/lib/commons-pool-1.2.jar
%{prefix}/lib/commons-discovery-0.2.jar
%{prefix}/lib/cryptix-asn1.jar
%{prefix}/lib/cryptix32.jar
%{prefix}/lib/dom.jar
%{prefix}/lib/grizzly-webserver-1.9.15b.jar
%{prefix}/lib/jce-jdk13-131.jar
%{prefix}/lib/jgss.jar
%{prefix}/lib/jaxrpc.jar
%{prefix}/lib/jakarta-oro-2.0.8.jar
%{prefix}/lib/jersey-core-1.1.0-ea.jar
%{prefix}/lib/jersey-server-1.1.0-ea.jar
%{prefix}/lib/jna.jar
%{prefix}/lib/jsr311-api-1.1.jar
%{prefix}/lib/json-20080701.jar
%{prefix}/lib/log4j-1.2.13.jar
%{prefix}/lib/logback-classic-0.9.15.jar
%{prefix}/lib/logback-core-0.9.15.jar
%{prefix}/lib/mail.jar
%{prefix}/lib/puretls.jar
%{prefix}/lib/saaj.jar
%{prefix}/lib/srm22client.jar
%{prefix}/lib/slf4j-api-1.5.6.jar
%{prefix}/lib/ws-commons-util-1.0.1.jar
%{prefix}/lib/wsdl4j-1.5.1.jar
%{prefix}/lib/xmlrpc-server-3.0.jar
%{prefix}/lib/xml-apis.jar
%{prefix}/lib/xmlrpc-common-3.0.jar
%{prefix}/lib/xalan.jar
%{prefix}/lib/xercesImpl.jar
%{prefix}/doc/apache2.LICENSE
%{prefix}/doc/puretls.LICENSE
%{prefix}/doc/junit.LICENSE
%{prefix}/doc/bouncycastle.LICENSE
%{prefix}/doc/commons-logging.LICENSE
%{prefix}/doc/cog.LICENSE
%{prefix}/doc/cryptix.LICENSE
%{prefix}/doc/ACKNOWLEDGEMENTS.txt

%clean
rm -rf $RPM_BUILD_ROOT

### Package ChangeLog

%changelog
* Tue Nov 15 2010 <Elisabetta Ronchieri> <elisabetta.ronchieri@cnaf.infn.it>
- version %{version}-%{release}
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


