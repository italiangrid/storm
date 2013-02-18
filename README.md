The StoRM BackEnd Service
===============================

StoRM BackEnd provides a set of facilities and interfaces that allows to
build Grid Storage Element upon any POSIX filesystem granting direct file access, 
but taking advantage of special features of high performance parallel and
cluster file systems, as GPFS from IBM and Lustre from SUN.

Supported platforms
Scientific Linux 5 on x86_64 architecture
Scientific Linux 6 on x86_64 architecture

### Building
Required packages:

* epel
* git
* ant
* gcc
* swig
* gcc-c++
* libacl-devel
* lcmaps-without-gsi-devel
* lcmaps-interface
* rpm-build

Build command:
```bash
ant -Dswig.location=/usr -Djdk.location=/usr/lib/jvm/java -Dlcmaps.location=/usr -Dlcmaps-without-gsi.location=/usr -Dlcmaps-plugins-basic.location=/usr -Dlcmaps-plugins-voms.location=/usr -Dlibacl-devel.location=/usr -Dlibattr-devel.location=/usr -Dversion=1.11.0 -Dage=43 -Dplatform.acronym=sl5 build-rpm
```
# Contact info

If you have problems, questions, ideas or suggestions, please contact us at
the following URLs

* GGUS (official support channel): http://www.ggus.eu

