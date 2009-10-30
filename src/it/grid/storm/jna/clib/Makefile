
ifndef DESTDIR
DESTDIR=.
endif

ifndef BUILDDIR
BUILDDIR=.
endif

CC = gcc
CFLAGS = -O

all: lib

clean:
	rm -f *.o lib*

#storm_dmutil: storm_dmutil.c
#	$(CC) -o $(BUILDDIR)/storm_dmutil.o $(CFLAGS) -c -fPIC  -ldmapi storm_dmutil.c

storm_libcutil: storm_libcutil.c
	$(CC) -D_FILE_OFFSET_BITS=64 -o $(BUILDDIR)/storm_libcutil.o $(CFLAGS) -c -fPIC storm_libcutil.c

#lib: storm_dmutil storm_libcutil
#	gcc -shared -Wall -o $(DESTDIR)/libstorm_cutil.so -ldmapi $(BUILDDIR)/storm_dmutil.o $(BUILDDIR)/storm_libcutil.o

lib: storm_libcutil
	gcc -shared -Wall -o $(DESTDIR)/libstorm_cutil.so $(BUILDDIR)/storm_libcutil.o -l gpfs

