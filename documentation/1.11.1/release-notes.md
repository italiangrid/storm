---
layout: default
title: StoRM v. 1.11.1
rootdir: ../..
assetsdir: ../../assets
---

# StoRM v. 1.11.1

This StoRM release provides the bug fixes and improvements for the StoRM services.

### Bug fixes 

* [STOR-172][STOR-172] : StoRM now correctly publishes information about storage area sizes in the information system.
* [STOR-148][STOR-148] , [STOR-10][STOR-10] : StoRM now leverages quota limits information gathered from the underlying GPFS filesystem to compute a storage area size.
* [STOR-130][STOR-130] : StoRM GridHTTPs server is now correctly registered to start at system boot.
* [STOR-117][STOR-117] : Duplicate prepare-to-get calls on a SURL are now correctly handled.
* [STOR-113][STOR-113] : The StoRM YAIM module does not try to configure permissions on existing configured storage areas. It is assumed (and documented) 
that the correct permissions are set by the system administrator before running YAIM.
* [STOR-109][STOR-109] : The Java JDK dependency has been fixed so that all StoRM packages explicitly requires OpenJDK. 



### Enhancements

* [STOR-230][STOR-230]: The StoRM WebDAV propfind implementation performance has been improved.
  

### Other news

* The StoRM web site is now hosted on [Github](http://italiangrid.github.io/storm).

* All the StoRM code has been migrated to Github. StoRM repositories can be found
[here](https://github.com/italiangrid).

* The StorRM PT now uses [JIRA](https://issues.infn.it:8443/browse/STOR) for issue, development and release progress tracking.


[STOR-172]: https://issues.infn.it:8443/browse/STOR-172
[STOR-148]: https://issues.infn.it:8443/browse/STOR-148
[STOR-10]: https://issues.infn.it:8443/browse/STOR-10
[STOR-130]: https://issues.infn.it:8443/browse/STOR-130
[STOR-117]: https://issues.infn.it:8443/browse/STOR-117
[STOR-113]: https://issues.infn.it:8443/browse/STOR-113
[STOR-109]: https://issues.infn.it:8443/browse/STOR-109
[STOR-230]: https://issues.infn.it:8443/browse/STOR-230

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [Documentation]({{ page.rootdir }}/documentation/1.11.1/index.html) section.

### Known issues

None at the moment

