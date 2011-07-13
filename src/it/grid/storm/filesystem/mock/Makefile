#
# MOCK Library for GPFS
#

top_srcdir=.
stagedir=.
distdir=.
globalprefix=storm
backendprefix=backend
package=storm-backend-server
version=1.7.0

architecture=${arch}

-include Makefile.inc

default: all

DEBUG ?= 1

# non-portable GNU-only features we depend on
defines += -D_FILE_OFFSET_BITS=64
defines += -fPIC

# compile debugging and assertion code?
#
ifeq (1,$(DEBUG))
defines += -DDEBUG
override CFLAGS += -Wall
override CFLAGS += -g -O
else
defines += -DNDEBUG  # ignore assert()
endif


# add needed defines to compilation flags
override CFLAGS += $(defines)
# add current directory to include gpfs.h
override CFLAGS += -I.

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

libgpfs := $(destdir)libgpfs.so

lib: $(libgpfs)

all: lib

#CC=gcc

# files
libgpfs_srcs := \
        libgpfs.c

libgpfs_objs := $(libgpfs_srcs:.c=.o)

$(libgpfs): $(libgpfs_objs)
	$(LINK.c) -shared -o $(libgpfs) $(libgpfs_objs)

clean:
	rm -f *~ *.o *.so
