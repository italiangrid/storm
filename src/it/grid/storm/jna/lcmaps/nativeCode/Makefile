top_srcdir=.
stagedir=.
distdir=.
globalprefix=storm
backendprefix=backend-server
package=storm-backend-server
version=1.7.1

architecture=${arch}
lcmaps_without_gsi_location=${lcmaps-without-gsi.location}
DESTDIR?=.
BUILDDIR?=./lib$(architecture)

LCMAPS_INCLUDES ?= -I$(lcmaps_without_gsi_location)/include/ 
#\
#-I/usr/include/lcmaps/lcmaps_return_poolindex_without_gsi.h

#LCMAPS_LIBS ?= \
# ${lcmaps_without_gsi_location}/lib$(arch)/liblcmaps_return_poolindex_without_gsi.so 
#\
#${lcmaps_location}/lib$(arch)/liblcmaps.so

#LCMAPS_LIBS= \
   $(LCMAPS_RETURN_POOLINDEX_WITHOUT_GSI_LIBS)
#LCMAPS_LIBS=$(lcmaps_without_gsi_location)/lib$(arch)/liblcmaps_return_poolindex_without_gsi.so

#LCMAPS_RETURN_POOLINDEX_WITHOUT_GSI_LIBS:
#	echo "check library"; 
#	if test -e "$(lcmaps_without_gsi_location)/lib$(arch)/liblcmaps_return_poolindex_without_gsi.so"; then \
#	  echo "$(lcmaps_without_gsi_location)/lib$(arch)/liblcmaps_return_poolindex_without_gsi.so"; \
#	else \
#	  echo "/usr/lib$(arch)/liblcmaps_return_poolindex_without_gsi.so"; \
#	fi;

GCC=gcc

GCCFLAGS=-g -O -fPIC
GCCFLAGS += $(LCMAPS_INCLUDES)

LCMAPSFGLAGS=-DLCMAPS_RETURN_POOLINDEX_WITHOUT_GSI_C
.PHONY: all clean lib LCMAPSLIBS

all: lib

clean:
	rm -f $(DESTDIR)/libstorm_lcmaps.so $(BUILDDIR)/storm_lcmaps.o *.o *.so

storm_lcmaps: storm_lcmaps.c storm_lcmaps.h
	$(GCC) -c $(GCCFLAGS) $(LCMAPSFGLAGS) storm_lcmaps.c -o $(BUILDDIR)/storm_lcmaps.o

lib: storm_lcmaps 
	if test -f "$(lcmaps_without_gsi_location)/lib$(arch)/liblcmaps_return_poolindex_without_gsi.so" ; then \
	$(GCC) -shared -Wall -o $(DESTDIR)/libstorm_lcmaps.so $(BUILDDIR)/storm_lcmaps.o $(LCMAPS_LDFLAGS) $(lcmaps_without_gsi_location)/lib$(arch)/liblcmaps_return_poolindex_without_gsi.so ; \
	else \
	$(GCC) -shared -Wall -o $(DESTDIR)/libstorm_lcmaps.so $(BUILDDIR)/storm_lcmaps.o $(LCMAPS_LDFLAGS) /usr/lib$(arch)/liblcmaps_return_poolindex_without_gsi.so ; \
	fi
