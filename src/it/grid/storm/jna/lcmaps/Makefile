top_srcdir=.
stagedir=.
distdir=.
globalprefix=storm
backendprefix=backend
package=storm-backend
version=1.5.4
PREFIX=/opt/storm/backend

#lcmaps_location=${lcmaps_location}
#lcmaps_without_gsi_location=${lcmaps_without_gsi_location}
#lcmaps_plugins_basic_location=${lcmaps_plugins_basic_location}
#lcmaps_plugins_voms_location=${lcmaps_plugins_voms_location}
arch=${arch}

-include Makefile.inc

DESTDIR?=.
BUILDDIR?=./lib$(arch)

LCMAPS_INCLUDES ?= -I${lcmaps_location}/include/glite/security/lcmaps -I${lcmaps_without_gsi_location}/include/glite/security/lcmaps_without_gsi
LCMAPS_LIBS ?= \
 ${lcmaps_without_gsi_location}/lib$(arch)/liblcmaps_return_poolindex_without_gsi.so \
 ${lcmaps_location}/lib$(arch)/liblcmaps.so

CXX=g++

CXXFLAGS=-O -fPIC
CXXFLAGS += $(LCMAPS_INCLUDES)

.PHONY: all clean lib

all: lib

clean:
	rm -f *.o lib*

storm_lcmaps: storm_lcmaps.c storm_lcmaps.h
	$(CXX) -c storm_lcmaps.c $(CXXFLAGS) -o $(BUILDDIR)/storm_lcmaps.o

lib: storm_lcmaps
	$(CXX) -shared -Wall -o $(DESTDIR)/libstorm_lcmaps.so $(BUILDDIR)/storm_lcmaps.o $(LCMAPS_LDFLAGS) $(LCMAPS_LIBS)

