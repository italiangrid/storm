# 
# RPM spec file for the StoRM dynamic information provider plugin
#
# Copyright (c) 2008 Magnoni Luca <luca.magnoni@cnaf.infn.it>.
# 
# You may copy, modify and distribute this file under the same
# terms as the StoRM itself.
#
#


### Package Naming 

Name: glite-info-dynamic-storm
Version: 1.5.0
Release: 02.sl4
Summary: The StoRM dynamic information provider plugin.
Copyright:  Apache License, Version 2.0. 
Url: http://storm.forge.cnaf.infn.it
Vendor: INFN - CNAF (2009)
Group: Application/Generic
Packager: Luca Magnoni <luca.magnoni@cnaf.infn.it> 
Prefix: /opt/glite/libexec

#BuildRoot: %{_tmppath}/%{name}
BuildRoot: %{_topdir}/BUILD/glite-info-dynamic-storm-1.5.0
#BuildRoot: %{_tmppath}/info-provider

Source: %{name}-%{version}.tar.gz

%description
This package contains the StoRM dynamic information provider plugin.
This plugin provides dynamic information on space usage and other parameters published by the StoRM Storage Element.

%files
%defattr(755,root,root)
%{prefix}/glite-info-dynamic-storm

%prep
%setup -n glite-info-dynamic-storm-1.5.0



