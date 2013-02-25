StoRM backend service
===============================

StoRM BackEnd provides a set of facilities and interfaces that allows to
build Grid Storage Element upon any POSIX filesystem granting direct file access, 
but taking advantage of special features of high performance parallel and
cluster file systems, as GPFS from IBM and Lustre from SUN.

## Build from source

### Supported platforms
* Scientific Linux 5 on x86_64 architecture
* Scientific Linux 6 on x86_64 architecture

### Repositories

Some of the packages needed to build storm-client are in the [EPEL](http://fedoraproject.org/wiki/EPEL) 
repository. To enable it run

```bash
yum install epel-release
```

Some of the packages needed to build storm-client are in the [EMI](http://www.eu-emi.eu/) 
repository. Get the latest production emi-release package from the 
[EMI repository website](http://emisoft.web.cern.ch/emisoft/index.html) and install


```bash
yum localinstall emi-release-2.0.0-1.sl5.noarch.rpm
```

### Building

You need git to get the code, and ant (libtool, automake, autoconf) and gcc and gcc-c++ to build it.

The other dependcies are

* swig
* libacl-devel
* lcmaps-without-gsi-devel
* lcmaps-interface
* rpm-build

Build command:
```bash
ant -Dversion=1.11.0 -Dage=43 -Dplatform.acronym=sl5 build-rpm
```

## Hacking StoRM

If you want contribute fork the repository and submit a 
[pull request](https://help.github.com/articles/using-pull-requests). 

A work in progress developer guide can be found in the [README](src/it/grid/storm/README.md) of the source code directories.

## Contact info

If you have problems, questions, ideas or suggestions, please contact us at
the following URLs

* GGUS (official support channel): http://www.ggus.eu

