---
layout: toc
title: StoRM Storage Resource Manager - How-to publish storage site report
---

# How-to publish storage site report

- [Configure a storage area](#configure)
- [Periodically reload report](#reload-report)
    - [Refresh script](#script)
    - [Cron-job](#cronjob)
- [Get JSON report through HTTP](#json)
 
The following is an example of how make the storage site report in JSON readble from all VOs' authenticated users through a WebDAV endpoint.

## Configure a storage area <a name="configure">&nbsp;</a>

The aim is allowing all the users to access - through a WebDAV endpoint - the JSON report.

In the following example, a dedicated storage area has been added and configured to be readable-only by all the users.

Add `info` storage area to the storage area list:

```
STORM_STORAGEAREA_LIST = "${STORM_STORAGEAREA_LIST} info"
```

Set mandatory online size value (e.g. 1GB):

```
STORM_INFO_ONLINE_SIZE = 1
```

Then, allow info storage area to be accessed from all the users of one (or more) VOs:

```
STORM_INFO_VONAME = "test.vo test.vo.2"
```

Allow authenticated users ot access through WebDAV the content of `info`:

```
STORM_INFO_AUTHENTICATED_HTTP_READ = true
```

Deny write access to `/info` path to all users through path-authz.db file:

```
$ vim /etc/storm/backend-server/path-authz.db
...
#--------+----------------------+---------------+----------
# user   |            Path          |   Permission  |   ACE
# class  |                      |   mask        |   Type
#--------+----------------------+---------------+----------
  @ALL@     /info                    WFDMN            deny
  @ALL@     /                        WRFDLMN          permit
```

## Periodically reload report <a name="reload-report">&nbsp;</a>

### Refresh script <a name="script">&nbsp;</a>

Write a simple bash script which runs info-provider, gets the updated json-report and replaces the old one.

```
#!/bin/bash
set -x

TMP_REPORT_PATH="/tmp/site-report-$(date +%s).json"
TARGET_REPORT_PATH="/storage/info/report.json"
if [ $# -gt 0 ]; then
    TARGET_REPORT_PATH=$1
fi

# refresh report
/usr/libexec/storm-info-provider get-report-json -o $TMP_REPORT_PATH

# copy report to storage area

cp $TMP_REPORT_PATH $TARGET_REPORT_PATH
chown storm:storm $TARGET_REPORT_PATH
```

Save it into `/root/update-report.sh`


### Cron-job<a name="cronjob">&nbsp;</a>

Create a cron-job that runs `update-report.sh` script hourly.

Create `/etc/cron.d/update-site-report` as follow:

```
0 * * * *      root    /bin/bash /root/update-report.sh
```

At XX:00 the `update-report.sh` script will be executed.

## Get JSON report through HTTP <a name="json">&nbsp;</a>

Acting as `test0` user of test.vo VO, we can access storage area with a simple http get.

We need `test0` user's certificate `test0.cert.pem` and his unencrypted key `test0.ukey.pem`.


```
$ curl https://omii006-vm03.cnaf.infn.it:8443/info/report.json \
    --cert ./test0.cert.pem \
    --key ./test0.ukey.pem \
    --capath /etc/grid-security/certificates
```

Output:

```
{
    "capabilities": [
        "data.management.transfer", 
        "data.management.storage"
    ], 
    "endpoints": [
        {
            "assignedshares": [
                "all"
            ], 
            "capabilities": [
                "data.management.transfer", 
                "data.management.storage"
            ], 
            "endpointurl": "httpg://omii006-vm03.cnaf.infn.it:8444/srm/managerv2", 
            "interfacetype": "srm", 
            "interfaceversion": "2.2", 
            "name": "INFO-FS_srm", 
            "qualitylevel": "pre-production"
        }, 
        {
            "assignedshares": [
                "all"
            ], 
            "capabilities": [
                "data.management.transfer", 
                "data.management.storage"
            ], 
            "endpointurl": "http://omii006-vm03.cnaf.infn.it:8085/webdav", 
            "interfacetype": "DAV", 
            "interfaceversion": "1.1", 
            "name": "INFO-FS_http", 
            "qualitylevel": "pre-production"
        }, 
        {
            "assignedshares": [
                "all"
            ], 
            "capabilities": [
                "data.management.transfer", 
                "data.management.storage"
            ], 
            "endpointurl": "https://omii006-vm03.cnaf.infn.it:8443/webdav", 
            "interfacetype": "DAV", 
            "interfaceversion": "1.1", 
            "name": "INFO-FS_https", 
            "qualitylevel": "pre-production"
        }
    ], 
    "implementation": "storm", 
    "implementationversion": "1.11.13", 
    "latestupdate": 1518169441, 
    "name": "storm-testbed", 
    "qualitylevel": "pre-production", 
    "shares": [ 
        {
            "accesslatency": "online", 
            "assignedendpoints": [
                "all"
            ], 
            "name": "TESTVO-FS", 
            "path": [
                "/test.vo"
            ], 
            "retentionpolicy": "replica", 
            "servingstate": "open", 
            "timestamp": 1518169441, 
            "totalsize": 12000000000, 
            "usedsize": 54648129, 
            "vos": [
                "test.vo"
            ]
        }, 
        {
            "accesslatency": "online", 
            "assignedendpoints": [
                "all"
            ], 
            "name": "INFO-FS", 
            "path": [
                "/info"
            ], 
            "retentionpolicy": "replica", 
            "servingstate": "open", 
            "timestamp": 1518169441, 
            "totalsize": 1000000000, 
            "usedsize": 4096, 
            "vos": [
                "test.vo test.vo.2"
            ]
        },
        ...
    ]
}
```