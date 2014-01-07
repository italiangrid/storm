---
layout: default
title: StoRM Storage Resource Manager - Storage Area configuration examples
version: 1.11.2
---

#### [StoRM Examples]({{site.baseurl}}/documentation/examples/)

## Storage Area configuration examples

From StoRM 1.11.x versions new configurations have been introduced for Storage Areas. 
It's possible to configure a Storage Area readable and/or writable from anonymous users, not associate a particular VO, or set filters on the x509 subject's fields.
These are some configuration examples of a Storage Area:

* [1. Storage Area readable from anonymous](#sa-anonymous-r)
* [2. Storage Area readable and writable from anonymous](#sa-anonymous-rw)
* [3. Storage Area readable and writable with a valid x509 certificate](#sa-anonymous-rw-x509)

### 1. Storage Area readable from anonymous <a name="sa-anonymous-r">&nbsp;</a>

To define a storage area ```test-sa``` readable and writable from any anonymous user add/modify the following YAIM variables like follows:

```bash
# list of VOs
VOS="test-vo"

# list of enabled Storage Areas
STORAGE_AREA_LIST="test-sa"

# test-sa configuration
STORM_TESTSA_VONAME="test-vo"
STORM_TESTSA_ANONYMOUS_HTTP_READ=true
```

To test this configuration, if you have a StoRM GridHTTPs server enabled (see [Enable StoRM GridHTTPs on a standalone deployment][example2]),
and _file.txt_ exists and it's located in the root directory of the storage area, you can simply open a browser or do a _curl_ to the address:

```bash
http://<storm-gridhttps-server-hostname>:8085/test-sa/file.txt
```

and see the file content. The ```storm-gridhttps-server-hostname``` is the Backend hostname in case of a standalone deployment.

### 2. Storage Area readable and writable from anonymous <a name="sa-anonymous-rw">&nbsp;</a>

To define a storage area ```test-sa``` for the VO ```test-vo``` as:

- writable only from ```test-vo``` valid users
- readable from anononymous users

add/modify the following YAIM variables like follows:

```bash
# list of enabled Storage Areas
STORAGE_AREA_LIST="test-sa"

# test-sa configuration
STORM_TESTSA_VONAME="*"
```
The value * for STORM\_TESTSA\_VONAME is enough to allow read and write operations on ```test-sa``` from anonymous users.
To test this configuration, if you have a StoRM GridHTTPs server enabled (see [Enable StoRM GridHTTPs on a standalone deployment][example2]),
and _file.txt_ exists and it's located in the root directory of the storage area, you can simply open a browser or do a _curl_ to the address:

```bash
http://<storm-gridhttps-server-hostname>:8085/test-sa/file.txt
```

and see the file content. The ```storm-gridhttps-server-hostname``` is the Backend hostname in case of a standalone deployment. If you want to write/upload an existent local file _localfile.txt_ do:

```bash
curl -T localfile.txt http://<storm-gridhttps-server-hostname>:8085/test-sa/localfile.txt
```


### 3. Storage Area readable and writable with a valid x509 certificate <a name="sa-anonymous-rw-x509">&nbsp;</a>

To define a storage area ```test-sa``` as readable and writable only with a particular x509 certificate (or proxy) add/modify the following YAIM variables like follows:

```bash
# list of enabled Storage Areas
STORAGE_AREA_LIST="test-sa"

# test-sa configuration
STORM_TESTSA_VONAME="*"
STORM_TESTSA_DN_O_REGEX="IGI"
```

This is an example of how to set that ```test-sa``` is readable and writable only by user that provide a certificate with Organization "IGI".
You can use the variables:

- STORM\_{SA}\_DN\_C\_REGEX	
- STORM\_{SA}\_DN\_O\_REGEX	
- STORM\_{SA}\_DN\_OU\_REGEX
- STORM\_{SA}\_DN\_L\_REGEX
- STORM\_{SA}\_DN\_CN\_REGEX

to make your own DN filter.




[example2]: {{site.baseurl}}/documentation/examples/1.11.2/enable-gridhttps-standalone-deployment.html
