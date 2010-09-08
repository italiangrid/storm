#!/usr/bin/make -f
#
# Makefile for building the StoRM LCMAPS C code
# interfaces on SLC3.0.x and LCG-2.4.0.
#
#
# Copyright (c) 2005,2006 Riccardo Murri <riccardo.murri@ictp.it>
# for the EGRID/INFN joint project StoRM.
#
# You may copy, modify and distribute this code under the same terms as
# StoRM itself; see file LICENSE.txt
#
# $Id: Makefile,v 1.3 2007/05/16 09:50:04 lmagnoni Exp $
#

top_srcdir=.
stagedir=.
distdir=.
globalprefix=storm
backendprefix=backend
package=storm-backend
version=1.5.4
PREFIX=/opt/storm/backend

lcmaps_prefix=${lcmaps_location}
lcmaps_without_gsi_prefix=${lcmaps_without_gsi_location}
lcmaps_plugins_basic_prefix=${lcmaps_plugins_basic_location}
lcmaps_plugins_voms_prefix=${lcmaps_plugins_voms_location}

jdk_prefix=${jdk_location}
swig_prefix=${swig_location}
architecture=${arch}

-include Makefile.inc

NAME := lcmaps_interface

# where to put binary output files
DESTDIR ?= .

JAVA_PACKAGE ?= it.grid.storm.griduser.swig
JAVA_INCLUDES ?= \
	-I$(JAVA_HOME)/include \
	-I$(JAVA_HOME)/include/linux

LCMAPS_INCLUDES ?= -I$(lcmaps_without_gsi_prefix)/include/glite/security/lcmaps_without_gsi
LCMAPS_LIBS ?= \
 $(lcmaps_without_gsi_prefix)/lib$(architecture)/liblcmaps_return_poolindex_without_gsi.so \
 $(lcmaps_without_gsi_prefix)/lib$(architecture)/liblcmaps_without_gsi.so \
 $(lcmaps_plugins_basic_prefix)/lib$(architecture)/modules/liblcmaps_dummy_bad.so \
 $(lcmaps_plugins_basic_prefix)/lib$(architecture)/modules/liblcmaps_dummy_good.so \
 $(lcmaps_plugins_basic_prefix)/lib$(architecture)/modules/liblcmaps_localaccount.so \
 $(lcmaps_plugins_basic_prefix)/lib$(architecture)/modules/liblcmaps_poolaccount.so \
 $(lcmaps_plugins_basic_prefix)/lib$(architecture)/modules/liblcmaps_posix_enf.so \
 $(lcmaps_plugins_voms_prefix)/lib$(architecture)/modules/liblcmaps_voms_localaccount.so \
 $(lcmaps_plugins_voms_prefix)/lib$(architecture)/modules/liblcmaps_voms_localgroup.so \
 $(lcmaps_plugins_voms_prefix)/lib$(architecture)/modules/liblcmaps_voms_poolaccount.so \
 $(lcmaps_plugins_voms_prefix)/lib$(architecture)/modules/liblcmaps_voms_poolgroup.so \
 $(lcmaps_plugins_voms_prefix)/lib$(architecture)/modules/liblcmaps_voms.so

LIB_INCLUDES ?= -I$(libacl_devel_prefix)/usr/include -I$(libattr_devel_prefix)/usr/include -I$(libacl_devel_prefix)/include -I$(libattr_devel_prefix)/include

# the swig binary
SWIG_BIN ?= $(swig_prefix)/bin/swig


### NO CUSTOMIZATION BELOW THIS LINE!!

override CXXFLAGS += -DDEBUG -D__USE_BSD -Wall -O2
override INCLUDES += $(JAVA_INCLUDES) $(LIB_INCLUDES) $(LCMAPS_INCLUDES)
override LIBS     += $(LCMAPS_LIBS)

swig_interface := lcmaps_interface.i
h := lcmaps_interface.h
srcs := lcmaps_interface.cpp lcmaps_interface_wrap.cxx

swig_output := lcmaps_interface_wrap.cxx \
				lcmaps_interface.java \
				lcmaps_interfaceJNI.java \
				localuser_info.java



all: swig libso
#$(DESTDIR)/liblcmaps_interface.so

libso: $(srcs) $(h)
	$(CXX) -shared -fPIC -o $(DESTDIR)/liblcmaps_interface.so \
		$(CXXFLAGS) $(INCLUDES) $(srcs) $(LIBS)

.PHONY: swig
swig: $(swig_output)
$(swig_output): $(swig_interface)
	$(SWIG_BIN) -c++ -java -package $(JAVA_PACKAGE) $(swig_interface)


# FIXME: tests need a more complex incantation...
.PHONY: lcmaps_interface_test
lcmaps_interface_test: TestLcmapsInterface.class
	java -Djava.library.path=. TestLcmapsInterface
TestLcmapsInterface.class: TestLcmapsInterface.java
	javac -source 1.4 TestLcmapsInterface.java


.PHONY: clean swigclean extraclean
clean: 
	rm -f *~ $(objs) *.class
swigclean:
	rm -f $(swig_output)
extraclean: clean swigclean
