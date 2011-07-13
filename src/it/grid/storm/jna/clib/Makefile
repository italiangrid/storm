top_srcdir=.
stagedir=.
distdir=.
globalprefix=storm
backendprefix=backend
package=storm-backend
version=1.7.0

gpfs_mock_prefix=../../filesystem/mock
architecture=${arch}

-include Makefile.inc

DESTDIR?=.
BUILDDIR?=./lib$(architecture)

# where Mock gpfs library and includes are located
# gpfs_mock_location

# where the "gpfs.h" include file is located
GPFSAPI_INCLUDES ?= -I$(gpfs_mock_prefix)

# where libraries libposixapi_interface depends on
GPFSAPI_LIBS ?= -lgpfs -L$(DESTDIR)

CC = gcc

CFLAGS=-O -D_FILE_OFFSET_BITS=64 -c -fPIC
CFLAGS+=$(GPFSAPI_INCLUDES)

.PHONY: all clean lib

all: lib 

clean:
	rm -f *.o lib*

storm_libcutil: storm_libcutil.c
	$(CC) -o $(BUILDDIR)/storm_libcutil.o $(CFLAGS) storm_libcutil.c

lib: storm_libcutil
	$(CC) -shared -Wall -o $(DESTDIR)/libstorm_cutil.so $(BUILDDIR)/storm_libcutil.o  $(GPFSAPI_LIBS)

