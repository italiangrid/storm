# 
# RPM spec file for the StoRM BackEnd server.
#
# Copyright (c) 2008 Magnoni Luca <luca.magnoni@cnaf.infn.it>.
# 
# You may copy, modify and distribute this file under the same
# terms as the StoRM BackEnd itself.
#
#

%define debug_package %{nil}  # no debug package (not useful with Java)

### Package Naming 

Name: storm-backend
Version: 1.5.0
Release: rc13.sl4
Summary: The StoRM BackEnd component.
License:  Apache License, Version 2.0. See included file LICENSE.txt
Url: http://storm.forge.cnaf.infn.it
Vendor: INFN - CNAF (2009)
Group: Application/Generic
Packager: Luca Magnoni <luca.magnoni@cnaf.infn.it> 
Prefix: /opt/storm/backend
BuildRoot: %{_tmppath}/%{name}-%{version}
Source: %{name}-%{version}.tar.gz

### Package Description

%description
This package contains the StoRM BackEnd server.

StoRM provides an SRM interface to any POSIX
filesystem with direct file access ("file:" transport protocol), but
can take advantage of special features of high performance parallel and cluster file systems, as 
GPFS from IBM and Lustre from SUN.

The StoRM BackEnd is split into two binary packages:
'storm-backend-server', which contains the actual server binaries, and
'storm-backend-jars' which contains the 3rd party .jar libraries the
server depends upon.

###### SubPackage server

%package server

Summary: The StoRM BackEnd server.
Group: Application/Generic

###### Package Dependency

Requires: storm-backend-jars >= 1.5.0
# this causes a conflict error between postfix and torque-client packages. 
# Requires: redhat-lsb
Requires: nc
# Not really necessary...
Requires: glite-security-lcmaps-plugins-basic
Requires: glite-security-lcmaps-plugins-voms
Requires: vdt_globus_sdk


%description server

This package contains the StoRM BackEnd server.

StoRM provides an SRM interface to any POSIX
filesystem with direct file access ("file:" transport protocol), but
can take advantage of special features of high performance parallel and cluster file systems, as 
GPFS from IBM and Lustre from SUN.

This server depends upon some 3rd party .jar files, which are packed
separately in the ``storm-backend-jars`` package.




%files server
%defattr(-,root,root)

%{prefix}/doc/LICENSE.txt
%{prefix}/doc/INSTALL.txt
%config(noreplace) %{prefix}/etc/namespace-1.5.0.xsd
%config(noreplace) %{prefix}/etc/logrotate.d/storm-backend.logrotate
%config(noreplace) %{prefix}/etc/db/storm_mysql_update_from_1.0.0_to_1.3.2000.sql
%config(noreplace) %{prefix}/etc/db/storm_mysql_tbl.sql
%config(noreplace) %{prefix}/etc/db/storm_mysql_grant.sql
%config(noreplace) %{prefix}/etc/db/storm_mysql_update_from_1.3.2000_to_1.4.0.sql
%config(noreplace) %{prefix}/etc/db/storm_mysql_update_from_1.4.0_to_1.5.0.sql
%config(noreplace) %{prefix}/etc/logging.xml
%config(noreplace) %{prefix}/etc/sysconfig/storm-backend
%config(noreplace) %{prefix}/etc/lcmaps.db
%config(noreplace) %{prefix}/etc/storm.properties.template
%config(noreplace) %{prefix}/etc/path-authz.db
%config(noreplace) %{prefix}/etc/namespace.xml
%{prefix}/lib/storm-backend/libgpfsapi_interface.so
%{prefix}/lib/storm-backend/storm-backend.jar
%{prefix}/lib/storm-backend/liblcmaps_interface.so
%{prefix}/lib/storm-backend/libxfsapi_interface.so
%{prefix}/lib/storm-backend/libposixapi_interface.so
%{prefix}/lib/storm-backend/libjdim.so
%{prefix}/lib/storm-backend/libstorm_cutil.so
%config(noreplace) /etc/cron.d/storm-backend.cron

%defattr(755,root,root)

%config(noreplace) %{prefix}/sbin/storm-backend-configure
%config(noreplace) %{prefix}/etc/db/storm_database_config.sh
%config /etc/init.d/storm-backend

# empty directories; nonetheless include in RPM
%defattr(-,root,root)
%dir %{prefix}/var/log
%dir %{prefix}/var/tmp
%dir %{prefix}/lib/storm-backend/jar

%post server
echo "The StoRM BackEnd server is installed but NOT configured yet.
You need to launch the storm-backend-configure script, or to use 
yaim to configure the server.
"

%preun server
if /etc/init.d/storm-backend status &> /dev/null ; then
  echo "storm-backend is running"
  /etc/init.d/storm-backend stop &> /dev/null
else
  echo "storm-backend is not running"
fi

%postun server
rm -f /etc/cron.d/storm-backend.cron
# glite-info-dynamic-storm is a file created by YAIM
rm -f /etc/cron.d/glite-info-dynamic-storm

%package jars

Summary: The StoRM BackEnd server (3rd party libraries).
Group: Application/Generic
Prefix: /opt/storm/backend
Requires: storm-backend-server >= 1.4.0
Obsoletes: storm-backend-jars

%description jars
This package contains the 3rd party .jar libraries needed to run the
StoRM BackEnd server.


%files jars
%defattr(-,root,root)

%{prefix}/lib/storm-backend/jar/commons-codec-1.3.jar
%{prefix}/lib/storm-backend/jar/saaj.jar
%{prefix}/lib/storm-backend/jar/xmlrpc-server-3.0.jar
%{prefix}/lib/storm-backend/jar/dom.jar
%{prefix}/lib/storm-backend/jar/activation.jar
%{prefix}/lib/storm-backend/jar/wsdl4j-1.5.1.jar
%{prefix}/lib/storm-backend/jar/cog-jglobus.jar
%{prefix}/lib/storm-backend/jar/backport-util-concurrent.jar
%{prefix}/lib/storm-backend/jar/commons-collections-3.1.jar
%{prefix}/lib/storm-backend/jar/commons-configuration-1.4.jar
%{prefix}/lib/storm-backend/jar/commons-logging-1.1.jar
%{prefix}/lib/storm-backend/jar/commons-dbcp-1.2.1.jar
%{prefix}/lib/storm-backend/jar/commons-cli-1.0.jar
%{prefix}/lib/storm-backend/jar/FMC.jar
%{prefix}/lib/storm-backend/jar/dim-18.r2.jar
%{prefix}/lib/storm-backend/jar/xml-apis.jar
%{prefix}/lib/storm-backend/jar/xmlrpc-common-3.0.jar
%{prefix}/lib/storm-backend/jar/mail.jar
%{prefix}/lib/storm-backend/jar/cryptix-asn1.jar
%{prefix}/lib/storm-backend/jar/axis.jar
%{prefix}/lib/storm-backend/jar/axis-schema.jar
%{prefix}/lib/storm-backend/jar/cryptix32.jar
%{prefix}/lib/storm-backend/jar/ws-commons-util-1.0.1.jar
%{prefix}/lib/storm-backend/jar/cog-url.jar
%{prefix}/lib/storm-backend/jar/xalan.jar
%{prefix}/lib/storm-backend/jar/commons-validator-1.3.0.jar
%{prefix}/lib/storm-backend/jar/cog-axis.jar
%{prefix}/lib/storm-backend/jar/commons-beanutils.jar
%{prefix}/lib/storm-backend/jar/jce-jdk13-131.jar
%{prefix}/lib/storm-backend/jar/xercesImpl.jar
%{prefix}/lib/storm-backend/jar/srm22client.jar
%{prefix}/lib/storm-backend/jar/commons-lang-2.3.jar
%{prefix}/lib/storm-backend/jar/puretls.jar
%{prefix}/lib/storm-backend/jar/commons-digester-1.7.jar
%{prefix}/lib/storm-backend/jar/logback-classic-0.9.15.jar
%{prefix}/lib/storm-backend/jar/logback-core-0.9.15.jar
%{prefix}/lib/storm-backend/jar/slf4j-api-1.5.6.jar
%{prefix}/lib/storm-backend/jar/jgss.jar
%{prefix}/lib/storm-backend/jar/commons-pool-1.2.jar
%{prefix}/lib/storm-backend/jar/jaxrpc.jar
%{prefix}/lib/storm-backend/jar/commons-discovery-0.2.jar
%{prefix}/lib/storm-backend/jar/cog-jobmanager.jar
%{prefix}/lib/storm-backend/jar/jakarta-oro-2.0.8.jar
%{prefix}/lib/storm-backend/jar/asm-3.1.jar
%{prefix}/lib/storm-backend/jar/grizzly-webserver-1.9.15b.jar
%{prefix}/lib/storm-backend/jar/jersey-core-1.1.0-ea.jar
%{prefix}/lib/storm-backend/jar/jersey-server-1.1.0-ea.jar
%{prefix}/lib/storm-backend/jar/jna.jar
%{prefix}/lib/storm-backend/jar/jsr311-api-1.1.jar
%{prefix}/lib/storm-backend/jar/json-20080701.jar

%{prefix}/doc/apache2.LICENSE
%{prefix}/doc/puretls.LICENSE
%{prefix}/doc/junit.LICENSE
%{prefix}/doc/bouncycastle.LICENSE
%{prefix}/doc/commons-logging.LICENSE
%{prefix}/doc/cog.LICENSE
%{prefix}/doc/cryptix.LICENSE
%{prefix}/doc/ACKNOWLEDGEMENTS.txt
#%{prefix}/doc/RESTful-RecallTable.txt
#%{prefix}/doc/RecallTable-RESTful-URIDesign.pdf
#%{prefix}/doc/codetemplates-1.0..xml
#%{prefix}/doc/request-to-garbage.qbquery
#%{prefix}/doc/space-token-acls-03062008.pdf
#%{prefix}/doc/storm-code-formatter-2.0.xml

#%files jars -f ../filelist.jars.%{name}
#%defattr(-,root,root)

%prep
%setup -q


%build
ant -Dversion="%{version}" -Darch="%{?arch_bit}" clean build

%install
rm -rf $RPM_BUILD_ROOT
ant -Dversion="%{version}" -Dprefix="$RPM_BUILD_ROOT%{prefix}" install

mkdir -p $RPM_BUILD_ROOT/etc/cron.d
mv -f $RPM_BUILD_ROOT/opt/storm/backend/etc/logrotate.d/storm-backend.cron $RPM_BUILD_ROOT/etc/cron.d/
mkdir -p $RPM_BUILD_ROOT/etc/init.d
mv -f $RPM_BUILD_ROOT/opt/storm/backend/etc/init.d/storm-backend $RPM_BUILD_ROOT/etc/init.d/

if [ -h /etc/init.d/storm-backend ]; then
    rm -f /etc/init.d/storm-backend
fi

%clean
rm -rf $RPM_BUILD_ROOT
rm -f "$RPM_BUILD_DIR/filelist.server.%{name}"
rm -f "$RPM_BUILD_DIR/filelist.jars.%{name}"


### Package ChangeLog

%changelog
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


