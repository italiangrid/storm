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
NAME := lcmaps_interface


# where to put binary output files
DESTDIR ?= .


JAVA_PACKAGE ?= it.grid.storm.griduser.swig
JAVA_INCLUDES ?= \
	-I$(JAVA_HOME)/include \
	-I$(JAVA_HOME)/include/linux


LCMAPS_INCLUDES ?= -I /opt/glite/include/glite/security/lcmaps_without_gsi
LCMAPS_LIBS ?= /opt/glite/lib/liblcmaps_return_poolindex_without_gsi.so \
    /opt/glite/lib/liblcmaps_without_gsi.so \
	/opt/glite/lib/modules/liblcmaps_dummy_bad.so \
	/opt/glite/lib/modules/liblcmaps_dummy_good.so \
	/opt/glite/lib/modules/liblcmaps_localaccount.so \
	/opt/glite/lib/modules/liblcmaps_poolaccount.so \
	/opt/glite/lib/modules/liblcmaps_posix_enf.so \
	/opt/glite/lib/modules/liblcmaps_voms_localaccount.so \
	/opt/glite/lib/modules/liblcmaps_voms_localgroup.so \
	/opt/glite/lib/modules/liblcmaps_voms_poolaccount.so \
	/opt/glite/lib/modules/liblcmaps_voms_poolgroup.so \
	/opt/glite/lib/modules/liblcmaps_voms.so

# the swig binary
#SWIG ?= swig


### NO CUSTOMIZATION BELOW THIS LINE!!

override CXXFLAGS += -DDEBUG -D__USE_BSD -Wall -O2
override INCLUDES += $(JAVA_INCLUDES) $(LCMAPS_INCLUDES)
override LIBS     += $(LCMAPS_LIBS)

#SWIG ?= $(GLITE_LOCATION)/externals/swig-1.3.21/bin/swig \
#		-I$(GLITE_LOCATION)/externals/swig-1.3.21/lib/swig1.3 \
#		-I$(GLITE_LOCATION)/externals/swig-1.3.21/lib/swig1.3/java
#SWIG ?= /opt/glite/externals/swig-1.3.31/bin/swig \
#		-I/opt/glite/externals/swig-1.3.31/share/swig/1.3.31/ \
#		-I/opt/glite/externals/swig-1.3.31/share/swig/1.3.31/java

ifdef GLITE_LOCATION
SWIG ?= /opt/glite/externals/swig-1.3.31/bin/swig \
		-I/opt/glite/externals/swig-1.3.31/share/swig/1.3.31/ \
		-I/opt/glite/externals/swig-1.3.31/share/swig/1.3.31/java

else
SWIG ?= swig
endif


swig_interface := lcmaps_interface.i
h := lcmaps_interface.h
srcs := lcmaps_interface.cpp lcmaps_interface_wrap.cxx

swig_output := lcmaps_interface_wrap.cxx \
				lcmaps_interface.java \
				lcmaps_interfaceJNI.java \
				localuser_info.java



all: swig $(DESTDIR)/liblcmaps_interface.so

$(DESTDIR)/liblcmaps_interface.so: $(srcs) $(h)
	$(CXX) -shared -o $(DESTDIR)/liblcmaps_interface.so \
		$(CXXFLAGS) $(INCLUDES) $(srcs) $(LIBS)

.PHONY: swig
swig: $(swig_output)
$(swig_output): $(swig_interface)
	$(SWIG) -c++ -java -package $(JAVA_PACKAGE) $(swig_interface)


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
