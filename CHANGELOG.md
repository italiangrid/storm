# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

[Ensure Database connection pool tests connections on borrow and while idle][STOR-1333]

### Fixed

[If database is restarted while storm is running, it doesn't reconnect and needs a restart][STOR-482]

## 1.11.21 - 2021-05-12

### Fixed

[StoRM Backend service enters failed state when stopped][STOR-1395]
[Upgrading to StoRM v1.11.20 could break connections with MariaDB][STOR-1397]
[Ensure MariaDB is started before StoRM Backend on boot][STOR-1401]

## 1.11.20 - 2021-04-01

### Added

- [StoRM Backend and native libs should run with Java 11][STOR-1357]

## 1.11.19 - 2020-10-29

### Added

- [Include Jetty handler metrics reporting in storm-backend-metrics log][STOR-1251]

### Fixed

- [BoL and PtG requests statuses not updated after recall success][STOR-1260]
- [Uncaught RuntimeException raised when user.storm.pinned attribute is not found causes SRM_INTERNAL_ERROR during srmReleaseFiles][STOR-1267]

## 1.11.18 - 2020-08-07

### Added

- [Make Background DU configurable to run periodically in order to update used space info on db][STOR-932]
- [Fix useless verbosity in log][STOR-1036]
- [Include thread pool and jetty handler metrics reporting in storm-backend-metrics log][STOR-1174]
- [Add Date to Backend's metrics log][STOR-1198]
- [SystemD support for StoRM Backend][STOR-1089]

### Fixed

- [Log as ERROR only internal errors][STOR-892]
- [Understand what is the purpose of the recallBuckets map and whether it can be removed][STOR-1175]
- [SrmRm file does not exist should not be logged as ERROR][STOR-1176]
- [Include mysql-connector-java into maven dependencies][STOR-1216]

## 1.11.17 - 2019-12-17

### Fixed

- [Service storm-backend-server status returns 0 even if backend is not running][STOR-821]

## 1.11.16 - 2019-10-02

### Fixed

- [Improve error description when srmMkdir path contains non existing intermediate directories][STOR-1099]



[STOR-482]: https://issues.infn.it/jira/browse/STOR-482
[STOR-821]: https://issues.infn.it/jira/browse/STOR-821
[STOR-892]: https://issues.infn.it/jira/browse/STOR-892
[STOR-932]: https://issues.infn.it/jira/browse/STOR-932
[STOR-1036]: https://issues.infn.it/jira/browse/STOR-1036
[STOR-1089]: https://issues.infn.it/jira/browse/STOR-1089
[STOR-1099]: https://issues.infn.it/jira/browse/STOR-1099
[STOR-1174]: https://issues.infn.it/jira/browse/STOR-1174
[STOR-1175]: https://issues.infn.it/jira/browse/STOR-1175
[STOR-1176]: https://issues.infn.it/jira/browse/STOR-1176
[STOR-1198]: https://issues.infn.it/jira/browse/STOR-1198
[STOR-1216]: https://issues.infn.it/jira/browse/STOR-1216
[STOR-1251]: https://issues.infn.it/jira/browse/STOR-1251
[STOR-1260]: https://issues.infn.it/jira/browse/STOR-1260
[STOR-1267]: https://issues.infn.it/jira/browse/STOR-1267
[STOR-1333]: https://issues.infn.it/jira/browse/STOR-1333
[STOR-1357]: https://issues.infn.it/jira/browse/STOR-1357
[STOR-1395]: https://issues.infn.it/jira/browse/STOR-1395
[STOR-1397]: https://issues.infn.it/jira/browse/STOR-1397
[STOR-1401]: https://issues.infn.it/jira/browse/STOR-1401

