DESTDIR?=.
BUILDDIR?=.
GPFS_HOME?=/usr/lpp/mmfs

GPFS_CFLAGS=-I$(GPFS_HOME)/include
GPFS_LDFLAGS=-L$(GPFS_HOME)/lib
GPFS_LDADD=-l gpfs

CC = gcc

CFLAGS=-O -D_FILE_OFFSET_BITS=64 -c -fPIC
CFLAGS += $(GPFS_CFLAGS)

.PHONY: all clean lib

all: lib 

clean:
	rm -f *.o lib*

storm_libcutil: storm_libcutil.c
	$(CC) -o $(BUILDDIR)/storm_libcutil.o $(CFLAGS) storm_libcutil.c

lib: storm_libcutil
	$(CC) -shared -Wall -o $(DESTDIR)/libstorm_cutil.so $(BUILDDIR)/storm_libcutil.o $(GPFS_LDFLAGS) $(GPFS_LDADD)

