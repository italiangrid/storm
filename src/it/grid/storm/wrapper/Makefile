#	<StoRM: Disk Resource Management Middleware.>
#
#	Copyright (C) 2004 Luca Magnoni <luca.magnoni@cnaf.infn.it>,
#	                   Riccardo Murri <riccardo.murri@ictp.it>
#	
#
# $Id: Makefile,v 1.12 2005/12/05 09:47:42 lmagnoni Exp $


# Comma-separated list of filesystems to compile support code into
# the StoRM backend.
#
# Currently legal values are: 
#  gpfs  
#  lustre
#  posix  (generic POSIX support is *always* compiled in)
#
#FILESYSTEMS:=gpfs,posix
FILESYSTEMS:=posix


# Whether to compile debugging code 
#
# Legal values:
#   1  --> compile with debugging flags
#   0  --> *no* debugging code 
#
DEBUG:=1


# where are the JNI include files on your system?
JNI_INCLUDES := -I $(JAVA_HOME)/include -I $(JAVA_HOME)/include/linux


# where is GPFS API lib on this system? 
# (ignore if not compiling GPFS support)
LIBGPFS:=/usr/lib/libgpfs.so


# where to put produced .o and .so files
BUILDDIR:=./lib


# where to install the libraries (currently ignored)
DESTDIR:=/opt/storm/lib/storm-be



### NO CUSTOMIZATION NEEDED BELOW THIS LINE!

OBJ_TARGETS :=  \
	StoRM_PosixWrapper.o \
	StoRM_Ex.o

LIB_TARGETS := \
	libspacenativelib.so\
	libcompnativelib.so\
	libaclnativelib.so\
	libremoveaclnativelib.so\
	libtruncatenativelib.so\
	libgetaclnativelib.so\
	libstatnativelib.so

# for JNI code shared library
LIB_OBJ =   $(BUILDDIR)/StoRM_PosixWrapper.o \
	           $(BUILDDIR)/StoRM_Ex.o


# compile GPFS code?
ifeq (gpfs, $(findstring gpfs,$(FILESYSTEMS)))
DEFINE += -DGPFS
OBJ_TARGETS += StoRM_GPFSWrapper.o
LIB_OBJ += $(BUILDDIR)/StoRM_GPFSWrapper.o 
LIBS += $(LIBGPFS)
endif

# compile Lustre code?
ifeq (gpfs, $(findstring lustre,$(FILESYSTEMS)))
DEFINE += -DLUSTRE
OBJ_TARGETS += StoRM_LustreWrapper.o
LIB_OBJ += $(BUILDDIR)/StoRM_LustreWrapper.o
endif

# enable debugging code?
ifeq (1,$(DEBUG))
DEFINE += -DDEBUG_ON 
CFLAGS += -g -Wall 
endif

# set parameters for the compiler
CFLAGS += $(DEFINE) -Wno-deprecated

# don't check for existence of this files
.PHONY: all alllib exec


# main makefile targets
#
build_obj_targets:=$(addprefix $(BUILDDIR)/,$(OBJ_TARGETS))
build_lib_targets:=$(addprefix $(BUILDDIR)/,$(LIB_TARGETS))

all: 	$(build_obj_targets)

alllib : $(build_lib_targets)

exec : all ./Proto_StoRM

clean :
	rm -f Proto_Storm
	rm -f $(build_obj_targets) $(build_lib_targets)


# OBJS
#
$(BUILDDIR)/StoRM_Ex.o : src/StoRM_Ex.cpp h/StoRM_Ex.h h/StoRM_SRMspec.h
	$(CC)  $(CFLAGS) -c src/StoRM_Ex.cpp -o $(BUILDDIR)/StoRM_Ex.o

$(BUILDDIR)/StoRM_GPFSWrapper.o : src/StoRM_GPFSWrapper.cpp h/StoRM_GPFSWrapper.h h/StoRM_WrapperInterface.h	#h/common.h 
	$(CC)  $(CFLAGS) -c src/StoRM_GPFSWrapper.cpp -o $(BUILDDIR)/StoRM_GPFSWrapper.o 

$(BUILDDIR)/StoRM_LustreWrapper.o : src/StoRM_LustreWrapper.cpp h/StoRM_LustreWrapper.h h/StoRM_WrapperInterface.h 
	$(CC)  $(CFLAGS) -c src/StoRM_LustreWrapper.cpp -o $(BUILDDIR)/StoRM_LustreWrapper.o	
$(BUILDDIR)/StoRM_PosixWrapper.o : src/StoRM_PosixWrapper.cpp h/StoRM_PosixWrapper.h h/StoRM_WrapperInterface.h 
	$(CC)  $(CFLAGS) -c src/StoRM_PosixWrapper.cpp -o $(BUILDDIR)/StoRM_PosixWrapper.o


# LIBS
#
$(BUILDDIR)/libspacenativelib.so: \
		$(LIB_OBJ) \
		src/it_grid_storm_wrapper_SpaceReservationWrapper.cpp
	$(CC)  $(CFLAGS) -o $(BUILDDIR)/libspacenativelib.so -shared $(LIB_OBJ) $(LIBS) $(JNI_INCLUDES) src/it_grid_storm_wrapper_SpaceReservationWrapper.cpp -static 
$(BUILDDIR)/libcompnativelib.so: \
		$(LIB_OBJ) \
		src/it_grid_storm_wrapper_CompactSpaceWrapper.cpp
	$(CC)  $(CFLAGS) -o $(BUILDDIR)/libcompnativelib.so -shared $(LIB_OBJ) $(LIBS) $(JNI_INCLUDES) src/it_grid_storm_wrapper_CompactSpaceWrapper.cpp -static 

$(BUILDDIR)/libaclnativelib.so: \
		$(LIB_OBJ) \
		src/it_grid_storm_wrapper_AclWrapper.cpp
	$(CC)  $(CFLAGS) -o $(BUILDDIR)/libaclnativelib.so -shared $(LIB_OBJ) $(LIBS) $(JNI_INCLUDES) src/it_grid_storm_wrapper_AclWrapper.cpp -static

$(BUILDDIR)/libremoveaclnativelib.so: \
		$(LIB_OBJ) \
		src/it_grid_storm_wrapper_RemoveAclWrapper.cpp
	$(CC)  $(CFLAGS) -o $(BUILDDIR)/libremoveaclnativelib.so -shared $(LIB_OBJ) $(LIBS) $(JNI_INCLUDES) src/it_grid_storm_wrapper_RemoveAclWrapper.cpp -static 

$(BUILDDIR)/libtruncatenativelib.so: \
		$(LIB_OBJ) \
		src/it_grid_storm_wrapper_TruncateWrapper.cpp
	$(CC)  $(CFLAGS) -o $(BUILDDIR)/libtruncatenativelib.so -shared $(LIB_OBJ) $(LIBS) $(JNI_INCLUDES) src/it_grid_storm_wrapper_TruncateWrapper.cpp -static 

$(BUILDDIR)/libgetaclnativelib.so: \
		$(LIB_OBJ) \
		src/it_grid_storm_wrapper_GetAclWrapper.cpp
	$(CC)  $(CFLAGS) -o $(BUILDDIR)/libgetaclnativelib.so -shared $(LIB_OBJ) $(LIBS) $(JNI_INCLUDES) src/it_grid_storm_wrapper_GetAclWrapper.cpp -static 

$(BUILDDIR)/libstatnativelib.so: \
		$(LIB_OBJ) \
		src/it_grid_storm_wrapper_StatWrapper.cpp
	$(CC)  $(CFLAGS) -o $(BUILDDIR)/libstatnativelib.so -shared $(LIB_OBJ) $(LIBS) $(JNI_INCLUDES) src/it_grid_storm_wrapper_StatWrapper.cpp -static 
