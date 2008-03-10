# 
# RPM spec file for the StoRM BE server.
#
# Copyright (c) 2005 Riccardo Murri <riccardo.murri@ictp.it>.
# 
# You may copy, modify and distribute this file under the same
# terms as the StoRM backend itself.
#
# $Id: storm-backend.spec,v 1.53 2007/04/16 16:03:06 aforti Exp $
#

Name: storm-backend
Version: 1.3.19
Release: 02
Summary: The StoRM SRMv2 server (back-end daemon).

License: INFN-GRID like, see included file LICENSE.txt
URL: http://infnforge.cnaf.infn.it/projects/drm
Vendor: MIUR EGRID project / INFN FIRB Grid.It Project (2004)
Packager: Luca Magnoni <luca.magnoni@cnaf.infn.it> 

Group: Application/Generic
Source0: %{name}-%{version}.tar.gz

BuildRoot: %{_tmppath}/%{name}-%{version}

Prefix: /opt/storm


%changelog
* Wed 5 Mar 2008 <Magnoni Luca> <luca.magnoni@cnaf.infn.it>
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


%define debug_package %{nil}  # no debug package (not useful with Java)


%description
This package contains the back-end daemon for the StoRM
SRMv2 server.

The StoRM SRMv2 server provides an SRM interface to any POSIX
filesystem with direct file access ("file:" transport protocol), but
can take advantage of special features of the GPFS advanced cluster
filesystem for advance space reservation.

The StoRM backend is split into two binary packages:
'storm-backend-server', which contains the actual server binaries, and
'storm-backend-jars' which contains the 3rd party .jar libraries the
server depends upon.


%prep
%setup -q


%build
ant -Dversion="%{version}" clean build


%install
rm -rf $RPM_BUILD_ROOT
ant -Dversion="%{version}" \
    -Dprefix="$RPM_BUILD_ROOT%{prefix}" \
    install

mkdir -p $RPM_BUILD_ROOT/etc/cron.d
cp -f $RPM_BUILD_ROOT/opt/storm/etc/logrotate.d/storm-backend.cron $RPM_BUILD_ROOT/etc/cron.d/

# server has "./config" hard-coded path for config files
ln -s etc "$RPM_BUILD_ROOT/%{prefix}/config"

# i can't find out how to do this in ant w/out too much pain...
ln -sf ../etc/init.d/storm-backend \
  "$RPM_BUILD_ROOT/%{prefix}/bin/storm-backend"

# make file list from install dir contents
# (this is getting intricated; I hope the lists stabilizes soon)
cd "$RPM_BUILD_ROOT"
(
    find './%{prefix}/lib/storm-backend/jar' -name '*.jar';
    find './%{prefix}/doc' -name '*.LICENSE';
    find './%{prefix}/doc' -name 'ACKNOWLEDGEMENTS.txt'
)  | sed -e " \
   s|^\.|%attr(-,root,root) |; \
" > "$RPM_BUILD_DIR/filelist.jars.%{name}"
sed -e 's|^.*%{prefix}|%{prefix}|;' \
    < "$RPM_BUILD_DIR/filelist.jars.%{name}" \
    > "$RPM_BUILD_DIR/contents.jars.%{name}" 
find -type f -or -type l \
  | grep -v -f "$RPM_BUILD_DIR/contents.jars.%{name}" \
  | sed -e " \
   s|^\.|%attr(-,root,root) |; \
   /\/etc\//s|^|%config(noreplace) |; \
" > "$RPM_BUILD_DIR/filelist.server.%{name}"


%clean
rm -rf $RPM_BUILD_ROOT
rm -f "$RPM_BUILD_DIR/filelist.server.%{name}"
rm -f "$RPM_BUILD_DIR/filelist.jars.%{name}"

%package server
Summary: The StoRM SRMv2 server (back-end daemon).
Group: Application/Generic

Requires: storm-backend-jars
# this causes a conflict error between postfix and torque-client packages. 
# Requires: redhat-lsb
Requires: nc
# Not really necessary...
#Requires: gpfs.base
Requires: glite-security-lcmaps-interface-without-gsi
Requires: glite-security-lcmaps-plugins-basic
Requires: glite-security-lcmaps-plugins-voms
Requires: vdt_globus_sdk

Obsoletes: storm-backend


%description server
This package contains the back-end daemon for the StoRM
SRMv2 server.

The StoRM SRMv2 server provides an SRM interface to any POSIX
filesystem with direct file access ("file:" transport protocol), but
can take advantage of special features of the GPFS advanced cluster
filesystem for advance space reservation.

This server depends upon some 3rd party .jar files, which are packed
separately in the ``storm-backend-jars`` package.


%files server -f ../filelist.server.%{name}
#%{prefix}/etc/logrotate.d/storm-backend.logrotate
/etc/cron.d/storm-backend.cron
#%defattr(-,root,root)
# Temp solution... find another way to make the directories readable by the
# user running StoRM... i.e. the user may not be "storm"
%defattr(-,storm,storm)

# empty directories; nonetheless include in RPM
%dir /opt/storm/var/log
%dir /opt/storm/var/tmp
%dir /opt/storm/lib/storm-backend/jar

%post server
echo "The StoRM SRMv2 server is installed but NOT configured yet.
You need to launch the storm-backend-configure script, or to use 
yaim to configure the server.
"

%package jars
Summary: The StoRM SRMv2 server (3rd party libraries).
Group: Application/Generic

Prefix: /opt/storm

Requires: storm-backend-jars


%description jars
This package contains the 3rd party .jar libraries needed to run the
StoRM SRMv2 back-end daemon.


%files jars -f ../filelist.jars.%{name}
%defattr(-,root,root)

%postun server
rm -f /etc/cron.d/storm-backend.cron

