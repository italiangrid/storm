
ifndef DESTDIR
DESTDIR=.
endif

ifndef BUILDDIR
BUILDDIR=.
endif

CC = gcc
CFLAGS = -O
LIB = -ldmapi

all: lib

clean:
	rm -f *.o lib*

storm_dmutil: storm_dmutil.c
	$(CC) -o $(BUILDDIR)/storm_dmutil.o $(CFLAGS) -c -fPIC $(LIB) storm_dmutil.c

lib: storm_dmutil
	gcc -shared -Wall -o $(DESTDIR)/libstorm_dmutil.so $(LIB) $(BUILDDIR)/storm_dmutil.o

