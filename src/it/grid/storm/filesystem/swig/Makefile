#!/usr/bin/make -f
#
# Makefile for building the StoRM filesystem interface code
# on SLC3.0.x and LCG-2.4.0.
#
#
# Copyright (c) 2005, 2006 Riccardo Murri <riccardo.murri@ictp.it>
# for the EGRID/INFN joint project StoRM.
#
# You may copy, modify and distribute this code under the terms of
# the StoRM licence (see file LICENSE.txt).
#
# $Id: Makefile,v 1.42 2007/04/13 14:08:42 aforti Exp $
#

top_srcdir=.
stagedir=.
distdir=.
globalprefix=storm
backendprefix=backend
package=storm-backend
version=1.5.4
PREFIX=/opt/storm/backend

libacl_devel_prefix=${libacl_devel_location}
libattr_devel_prefix=${libattr_devel_location}
gpfs_prefix=${gpfs_base_location}
xfsprogs_devel_prefix=${xfsprogs_devel_location}
jdk_prefix=${jdk_location}
swig_prefix=${swig_location}
architecture=${arch}

-include Makefile.inc

default: all

# ====================================================================
#
# CUSTOMIZATION
#
# Change these values to fit your environment.
#
# ====================================================================

# Comma-separated list of filesystems to compile support code into
# the StoRM backend.
#
# Currently legal values are:
#  gpfs
#  xfs
#  posix  (ignored; generic POSIX support is *always* compiled in)
#
#FILESYSTEMS:=gpfs,xfs,posix
FILESYSTEMS ?= posix,gpfs,xfs

# Whether to compile debugging code
#
# Legal values:
#   1  --> compile with debugging flags
#   0  --> *no* debugging code, no assertions
#
DEBUG ?= 1

# the Java package for SWIG-generated wrapper classes.
JAVA_PACKAGE ?= it.grid.storm.filesystem.swig

# where the "jni.h" and "jni_md.h" include files are located
JAVA_INCLUDES ?= \
	-I$(JAVA_HOME)/include \
	-I$(JAVA_HOME)/include/linux

# additional libraries libposixapi_interface depends on
POSIXAPI_INCLUDES ?= -I$(libacl_devel_prefix)/usr/include -I$(libattr_devel_prefix)/usr/include -I$(libacl_devel_prefix)/include -I$(libattr_devel_prefix)/include
# where acl lib file is located
POSIXAPI_LIBS ?= -L$(libacl_devel_prefix)/usr/lib$(architecture) -L$(libacl_devel_prefix)/lib$(architecture) -lacl

# where gpfs library and includes are located
GPFSAPI_INCLUDES ?= -I$(gpfs_prefix)/include
# where libraries libposixapi_interface depends on
GPFSAPI_LIBS ?= $(POSIXAPI_LIBS) -L$(gpfs_prefix)/lib$(architecture) -lgpfs

# where  the "xfs/libxfs.h" include file is located
XFSAPI_INCLUDES ?= -I$(xfsprogs_devel_prefix)/usr/include -I$(xfsprogs_devel_prefix)/include
# additional libraries libposixapi_interface depends on
XFSAPI_LIBS ?= -L$(xfsprogs_devel_prefix)/usr/lib$(architecture) -L$(xfsprogs_devel_prefix)/lib$(architecture) -lhandle -lxfs

# the SWIG interface generator invocation
SWIG_BIN ?= $(swig_prefix)/bin/swig
# ====================================================================
#
# MAKEFILE MAGIC
#
# ====================================================================

# non-portable GNU-only features we depend on
#defines += -DHAVE_HASH_MAP
defines += -DHAVE_STDIO_FILEBUF
defines += -fPIC


# largefile support
#defines += -D_LARGEFILE_SOURCE
+defines += -D_LARGEFILE_SOURCE -D_FILE_OFFSET_BITS=64


# compile GPFS code?
ifeq (gpfs, $(findstring gpfs,$(FILESYSTEMS)))
BUILD_GPFS:=1
endif


# compile XFS code?
ifeq (xfs, $(findstring xfs,$(FILESYSTEMS)))
BUILD_XFS:=1
endif


# compile debugging and assertion code?
#
ifeq (1,$(DEBUG))
defines += -DDEBUG
override CXXFLAGS += -Wall
override CXXFLAGS += -g
else
defines += -DNDEBUG  # ignore assert()
endif


# add needed defines to compilation flags
override CXXFLAGS += $(defines)
override CPPFLAGS += $(defines)



# allow DESTDIR to be empty (builds in current dir)
# or set to some directory name (in this case, remove
# trailing slashes).
#
ifdef DESTDIR
# remove final slashes from the expansion of DESTDIR and append
# the modified content to the expansion of variable ``destdir``.
# (this is needed as 'make' matches target names as strings,
# not as filenames, so 'foo/bar' and 'foo//bar' are different targets.)
destdir := $(shell echo $(DESTDIR) | tr -s '/' | sed -e 's:/$$::')/
else
destdir :=
endif


# make dependencies for later inclusion by this file
# (see the GNU make manual, section 4.14
# "Generating Prerequisites Automatically")
%.dep: %.cpp
	set -e; \
	$(CC) -MM $(CPPFLAGS) $(JAVA_INCLUDES) $< \
		| sed 's/\($*\)\.o[ :]*/\1.o $@ : /g' > $@; \
	[ -s $@ ] || rm -f $@
%_wrap.dep: %_wrap.cxx
	set -e; \
	$(CC) -MM $(CPPFLAGS) $(JAVA_INCLUDES) $< \
		| sed 's/\($*\)\.o[ :]*/\1.o $@ : /g' > $@; \
	[ -s $@ ] || rm -f $@


# these need to be defined early...
libposixapi_interface := $(destdir)libposixapi_interface.so
libgpfsapi_interface := $(destdir)libgpfsapi_interface.so
libxfsapi_interface := $(destdir)libxfsapi_interface.so


# ====================================================================
#
# MAIN TARGETS
#
# ====================================================================

# POSIX support is always a default target
libsoo:= $(libposixapi_interface)

# only build GPFS if BUILD_GPFS is true
ifeq (1,$(BUILD_GPFS))
libsoo += $(libgpfsapi_interface)
endif

# only build XFS if BUILD_XFS is true
ifeq (1,$(BUILD_XFS))
libsoo += $(libxfsapi_interface)
endif

libso: $(libsoo)

#all: $(all)
all: swig libso

swig: posixapi_swig gpfsapi_swig xfsapi_swig

clean:
	rm -f *~ *.o *.class *.dep *.so 
swigclean:
	rm -f $(posixapi_swig_output) $(gpfsapi_swig_output) $(xfsapi_swig_output)
extraclean: clean swigclean
	rm -f errno.java

# errno.java -- system error codes constants
errno.java:
	( \
	echo "package $(JAVA_PACKAGE);"; \
	echo "public class errno {"; \
	cpp -dM /usr/include/errno.h \
		| grep '#define E' \
	  	| sort -k 3rg \
		| sed -e 's/#define \(E[[:alnum:]]*\) \(.*\)$$/public final int \1 = \2;/'; \
	echo '}'; \
	) > errno.java

.PHONY: all swig clean swigclean extraclean libso


# ====================================================================
#
# POSIXAPI TARGETS
#
# ====================================================================

# files
posixapi_srcs := \
	posixfs.cpp \
	genericfs.cpp \
	fs_acl.cpp \
	posixfs_acl.cpp \
	fs_errors.cpp \
	util.cpp
posixapi_objs := $(posixapi_srcs:.cpp=.o)

posixapi_swig_output_cxx := \
	posixapi_interface_wrap.cxx

posixapi_swig_output_java := \
	posixapi_interface.java \
	posixapi_interfaceJNI.java \
	genericfs.java \
	posixfs.java \
	fs_acl.java \
	posixfs_acl.java \
	error.java \
	acl_not_supported.java \
	helper_command_error.java \
	invalid_path.java \
	permission_denied.java \
	system_error.java \
	wrong_filesystem_type.java

posixapi_swig_output := \
	$(posixapi_swig_output_cxx) \
	$(posixapi_swig_output_java)


# generate all SWIG files in one run
# (FIXME: individual file dependencies?)
posixapi_swig: $(posixapi_swig_output)
$(posixapi_swig_output): \
		posixapi_interface.i \
		pass_vector_by_value_java.i
	$(SWIG_BIN) -c++ -java -package $(JAVA_PACKAGE) -Wall posixapi_interface.i

# SWIG-generated C wrapper
posixapi_interface_wrap.o: posixapi_interface_wrap.cxx
	$(COMPILE.cpp) $(JAVA_INCLUDES) $(POSIXAPI_INCLUDES) $(OUTPUT_OPTION) $<

# automatic C wrapper files dependencies
-include $(posixapi_swig_output_cxx:.cxx=.dep)

# main target
$(libposixapi_interface): \
		$(posixapi_objs) \
		posixapi_interface_wrap.o
	$(LINK.cpp) -shared -o $(libposixapi_interface) \
		posixapi_interface_wrap.o \
		$(posixapi_objs) $(POSIXAPI_LIBS)

# use POSIX_INCLUDES for posixapi files
$(posixapi_objs): CXXFLAGS += $(POSIXAPI_INCLUDES)

# automatic source files dependencies
-include $(posixapi_srcs:.cpp=.dep)



# ====================================================================
#
# GPFSAPI TARGETS
#
# ====================================================================

# files
gpfsapi_srcs := \
	gpfs.cpp \
	gpfs23.cpp \
	genericfs.cpp \
	fs_acl.cpp \
	posixfs_acl.cpp \
	gpfs23_acl.cpp \
	gpfs31_acl.cpp \
	fs_errors.cpp \
	helper_command.cpp \
	path.cpp \
	string_to_number.cpp \
	users_and_groups.cpp \
	sigchld.cpp \
	util.cpp
gpfsapi_objs := $(gpfsapi_srcs:.cpp=.o)

gpfsapi_swig_output_cxx := \
	gpfsapi_interface_wrap.cxx

gpfsapi_swig_output_java := \
	gpfsapi_interface.java \
	gpfsapi_interfaceJNI.java \
	gpfs.java \
	gpfs23.java \
	gpfs23_acl.java \
	gpfs31_acl.java

gpfsapi_swig_output := \
	$(gpfsapi_swig_output_cxx) \
	$(gpfsapi_swig_output_java)

#SWIG_LIB=$(swig_prefix)/share/swig/1.3.24

# generate all SWIG files in one run
# (FIXME: individual file dependencies?)
gpfsapi_swig: $(gpfsapi_swig_output)
$(gpfsapi_swig_output): \
		gpfsapi_interface.i \
		pass_vector_by_value_java.i
	$(SWIG_BIN) -c++ -java -package $(JAVA_PACKAGE) -Wall gpfsapi_interface.i

# SWIG-generated C wrapper
gpfsapi_interface_wrap.o: gpfsapi_interface_wrap.cxx
	$(COMPILE.cpp) $(GPFSAPI_INCLUDES) $(JAVA_INCLUDES) $(OUTPUT_OPTION) $<

# automatic C wrapper files dependencies
-include $(gpfsapi_swig_output_cxx:.cxx=.dep)

# main target
$(libgpfsapi_interface): \
		$(gpfsapi_objs) \
		gpfsapi_interface_wrap.o
	$(LINK.cpp) -shared -o $(libgpfsapi_interface) \
		gpfsapi_interface_wrap.o \
		$(gpfsapi_objs) $(GPFSAPI_LIBS)

# use GPFS_INCLUDES for gpfsapi files
$(gpfsapi_objs): CXXFLAGS += $(GPFSAPI_INCLUDES)

# automatic source files dependencies
-include $(gpfsapi_srcs:.cpp=.dep)


# ====================================================================
#
# XFSAPI TARGETS
#
# ====================================================================

# files
xfsapi_srcs := \
	xfs.cpp

xfsapi_objs := $(xfsapi_srcs:.cpp=.o)

xfsapi_swig_output_cxx := \
	xfsapi_interface_wrap.cxx

xfsapi_swig_output_java := \
	xfsapi_interface.java \
	xfsapi_interfaceJNI.java \
	xfs.java

xfsapi_swig_output := \
	$(xfsapi_swig_output_cxx) \
	$(xfsapi_swig_output_java)


# generate all SWIG files in one run
# (FIXME: individual file dependencies?)
xfsapi_swig: $(xfsapi_swig_output)
$(xfsapi_swig_output): \
		xfsapi_interface.i \
		pass_vector_by_value_java.i
	$(SWIG_BIN) -c++ -java -package $(JAVA_PACKAGE) -Wall xfsapi_interface.i

# SWIG-generated C wrapper
xfsapi_interface_wrap.o: xfsapi_interface_wrap.cxx
	$(COMPILE.cpp) $(JAVA_INCLUDES) $(OUTPUT_OPTION) $<

# automatic C wrapper files dependencies
-include $(xfsapi_swig_output_cxx:.cxx=.dep)

# main target
$(libxfsapi_interface): \
		$(xfsapi_objs) \
		xfsapi_interface_wrap.o \
		$(libposixapi_interface)
	$(LINK.cpp) -shared -o $(libxfsapi_interface) \
		xfsapi_interface_wrap.o \
		$(xfsapi_objs) $(XFSAPI_LIBS) \
		-L$(destdir) -lposixapi_interface

# use XFS_INCLUDES for xfsapi files
$(xfsapi_objs): CXXFLAGS += $(XFSAPI_INCLUDES)

# automatic source files dependencies
-include $(xfsapi_srcs:.cpp=.dep)


# ====================================================================
#
# UNIT TEST TARGETS
#
# ====================================================================

tests := test_posixfs_acl test_gpfs23_acl

tests: $(tests)

run-test_%: test_%
	./$<

run-all-tests: $(tests:test_%=run-test_%)

test_posixfs_acl: test_posixfs_acl.o $(libposixapi_interface)
	$(CXX) $(CXXFLAGS) -o test_posixfs_acl test_posixfs_acl.o \
		$(libposixapi_interface) $(POSIXAPI_LIBS) \
		-lboost_unit_test_framework

test_gpfs23_acl: test_gpfs23_acl.o $(libposixapi_interface) $(libgpfsapi_interface)
	$(CXX) $(CXXFLAGS) -o test_gpfs23_acl test_gpfs23_acl.o \
		$(libgpfsapi_interface) $(libposixapi_interface) \
		$(GPFSAPI_LIBS) $(POSIXAPI_LIBS) \
		-lboost_unit_test_framework

test_helper_command: test_helper_command.o helper_command.o fs_errors.o
	$(CXX) $(CXXFLAGS) -o test_helper_command \
	test_helper_command.o \
	helper_command.o \
	fs_errors.o \
	-lboost_unit_test_framework

test_split2: test_split2.o
	$(CXX) $(CXXFLAGS) -o test_split2 \
	test_split2.o \
	-lboost_unit_test_framework

test_path: test_path.o path.o
	$(CXX) $(CXXFLAGS) -o test_path \
	test_path.o path.o \
	-lboost_unit_test_framework
