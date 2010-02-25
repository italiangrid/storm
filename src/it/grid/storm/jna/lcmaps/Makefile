DESTDIR?=.
BUILDDIR?=.

GLITE_HOME?=/opt/glite
GLITE_SECURITY_INCLUDE?=$(GLITE_HOME)/include/glite/security
# Variable ARCH is set by ant (ant -Darch=64 to compile 64 bit)
GLITE_LIB?=$(GLITE_HOME)/lib$(ARCH)


LCMAPS_CFLAGS=-I$(GLITE_SECURITY_INCLUDE)/lcmaps -I$(GLITE_SECURITY_INCLUDE)/lcmaps_without_gsi
LCMAPS_LDFLAGS=-L$(GLITE_LIB)
LCMAPS_LDADD=-llcmaps -llcmaps_return_poolindex_without_gsi

CXX=g++

CXXFLAGS=-O -c -fPIC
CXXFLAGS += $(LCMAPS_CFLAGS)

.PHONY: all clean lib

all: lib

clean:
	rm -f *.o lib*

storm_lcmaps: storm_lcmaps.c
	$(CXX) -o $(BUILDDIR)/storm_lcmaps.o $(CXXFLAGS) storm_lcmaps.h storm_lcmaps.c

lib: storm_lcmaps
	$(CXX) -shared -Wall -o $(DESTDIR)/libstorm_lcmaps.so $(BUILDDIR)/storm_lcmaps.o $(LCMAPS_LDFLAGS) $(LCMAPS_LDADD)

