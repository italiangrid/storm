---
layout: default
title: StoRM Documentation - StoRM WebDAV user guide
---

# StoRM WebDAV User Guide

## Table of contents

* [The WebDAV protocol](#theprotocol)
* [StoRM WebDAV](#stormwebdav)
  * [Installation and configuration](#installandconf)
  * [Endpoints](#davendpoints)
  * [Authentication and authorization](#authandauth)
  * [Notes](#notes)
* [Usage](#usage)
  * [GET](#get)
  * [PUT](#put)
  * [MKCOL](#mkcol)
  * [DELETE](#delete)
  * [OPTIONS](#options)
  * [PROPFIND](#propfind)
  * [COPY](#copy)
  * [MOVE](#move)
* [Appendix A - Firefox RESTClient plugin](#usingrestclient)

# The WebDAV protocol <a name="theprotocol">&nbsp;</a>

<img src="{{ site.baseurl }}/assets/images/webdav-logo.jpg" alt="webdav-logo" width="100" style="float: right; margin-right: 10px; margin-left: 30px; margin-top: -5px;"/>

Web Distributed Authoring and Versioning (WebDAV) protocol consists of a set of methods, headers, and content-types ancillary to HTTP/1.1 for the management of resource properties, creation and management of resource collections, URL namespace manipulation, and resource locking. The purpose of this protocol is to present a Web content as a writable medium in addition to be a readable one. [WebDAV on Wikipedia](http://en.wikipedia.org/wiki/WebDAV) and the [WebDAV website](http://www.webdav.org/) provide information on this protocol.

In a few words, the WebDAV protocol mainly abstracts concepts such as resource properties, collections of resources, locks in general, and write locks specifically. These abstractions are manipulated by the WebDAV-specific HTTP methods and the extra HTTP headers used with WebDAV methods. The WebDAV added methods include:

* PROPFIND - used to retrieve properties, stored as XML, from a web resource. It is also overloaded to allow one to retrieve the collection structure (a.k.a. directory hierarchy) of a remote system.
* PROPPATCH - used to change and delete multiple properties on a resource in a single atomic act.
* MKCOL - used to create collections (a.k.a. a directory).
* COPY - used to copy a resource from one URI to another.
* MOVE - used to move a resource from one URI to another.
* LOCK - used to put a lock on a resource. WebDAV supports both shared and exclusive locks.
* UNLOCK - used to remove a lock from a resource.

While the status codes provided by HTTP/1.1 are sufficient to describe most error conditions encountered by WebDAV methods, there are some errors that do not fall neatly into the existing categories, so the WebDAV specification defines some extra status codes. Since some WebDAV methods may operate over many resources, the Multi-Status response has been introduced to return status information for multiple resources.
WebDAV uses XML for property names and some values, and also uses XML to marshal complicated requests and responses.

# StoRM WebDAV <a name="stormwebdav">&nbsp;</a>

From [StoRM v.1.11.7][storm-1-11-7] release, the StoRM service that provides valid WebDAV endpoints for each managed storage area is *StoRM WebDAV*.

*StoRM WebDAV* replaces the *StoRM gridhttps service*. All sites installing StoRM and providing HTTP and WebDAV endpoints should upgrade to the StoRM WebDAV service for improved performance and stability of the service as soon as possible.

**Important**: The StoRM WebDAV service is released and supported only on SL/CENTOS 6.

## Installation and configuration <a name="installandconf">&nbsp;</a>

See the [System Administration Guide][webdavconf] to learn how to install and configure the service. 

## Endpoints <a name="davendpoints">&nbsp;</a>

For each Storage Area, both/either a plain HTTP and/or a HTTP over SSL endpoint can be enabled. The default ports are **8085** (HTTP) and **8443** (HTTPS). 
All the following URLs are valid endpoints for a storage area:

    http://example.infn.it:8085/storage_area_accesspoint
    https://example.infn.it:8443/storage_area_accesspoint
    
To fully support the old *StoRM GridHTTPs* webdav endpoints, used until StoRM v1.11.6, all the URLs with *webdav* context path are accepted by *StoRM WebDAV*:
    
    http://example.infn.it:8085/webdav/storage_area_accesspoint
    https://example.infn.it:8443/webdav/storage_area_accesspoint

## Authentication and authorization <a name="authandauth">&nbsp;</a>

Users authentication within *StoRM WebDAV* is made through a valid VOMS proxy. All the users that provide a valid x509 VOMS proxy are authorized to access all the content of the storage area in read/write mode.

The most common way to authenticate and be authorized to read/write data into a Storage Area is by providing the right VOMS credentials through a valid VOMS Proxy. Otherwise, through the definition of a VOMS map file, a Storage Area can be configure to accept the list of VO members as obtained by running the `voms-admin list-users` command.
When VOMS mapfiles are enabled, users can authenticate to the StoRM webdav
service using the certificate in their browser and be granted VOMS attributes
if their subject is listed in one of the supported VOMS mapfile (see more details [here][vomapfiles]).

Read permissions of the content of a storage area can also be extendend to anonymous user (it's disabled by default).

## Notes <a name="notes">&nbsp;</a>

Both the old ```storm-gridhttps-server``` and the new ```storm-webdav``` components implements WebDAV protocol by using [*Milton*](http://milton.io/) open source java library.

![milton]({{ site.baseurl }}/assets/images/milton.png)

# Usage <a name="usage">&nbsp;</a>

The most common WebDAV clients are:

* browsers
* command-line tools like cURLs and davix
* a third-party GUI

Currently, users are used to connect to a WebDAV endpoint providing a valid username and password or as anonymous users (if supported). But, as seen in the [Authentication and authorization](#) paragraph, in our case the most common use case is providing a valid VOMS proxy. The VOMS proxies are supported only by command-line tools. Browsers can be used to navigate into the storage area content in the some cases:

* if VO users access through their x509 certificate is enabled (HTTPS endpoint)
* if anonymous read-only access is enabled (HTTP endpoint)

The use of a third party client (in read only mode) can happen only if anonymous read is enabled.

You can also develop a client on your own, for example by using the <a href="http://jackrabbit.apache.org/">Apache Jackrabbit API</a>.

The following paragraphs will give an example for each WebDAV/HTTP method by using cURLS and DAVIX client command line tools. cURL is a command line tool for transferring data with URL syntax (see [cURL website](http://curl.haxx.se/)).

All the requests have been done:

* against our WebDAV test endpoint `https://omii006-vm03.cnaf.infn.it:9443/`
* using `test0.p12` of our `igi-test-ca` credentials:

```bash
$ yum install igi-test-ca
$ cp /usr/share/igi-test-ca/test0.p12 $HOME
$ chmod 600 test0.p12
```

* after the creation of a valid VOMS proxy for `test.vo` VO

```bash
$ cd $HOME
$ voms-proxy-init --voms test.vo --cert test0.p12
```

## GET <a name="get">&nbsp;</a>

GET is defined as "*retrieve whatever information (in the form of an entity) is identified by the Request-URI*" (see [RFC2616](http://tools.ietf.org/html/rfc2616.txt)). GET applied to a file retrieves file's content. GET, when applied to a collection, returns an HTML resource that is a human-readable view of the contents of the collection.

#### Get all the content of the file `test.txt` stored into `test.vo` storage area

DAVIX example:

```bash
$ echo "Hello world" > test.txt
$ davix-get -P Grid --debug https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt
< HTTP/1.1 200 OK
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Strict-Transport-Security: max-age=31536000 ; includeSubDomains
< X-Frame-Options: DENY
< Set-Cookie: JSESSIONID=y37fa1hvmhiojd6w5xgyzzst;Path=/;Secure
< Expires: Thu, 01 Jan 1970 00:00:00 GMT
< Content-Type: text/plain
< Last-Modified: Fri, 13 Mar 2015 13:22:22 GMT
< Content-Length: 12
< Accept-Ranges: bytes
<
Hello world

```

CURL example:

```bash
$ curl https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt \
    --cert $X509_USER_PROXY \
    --capath /etc/grid-security/certificates \
    --cacert /usr/share/igi-test-ca/test0.cert.pem
```

#### Get multiple ranges of the file `test.txt` stored into `test.vo` storage area

DAVIX example:

```bash
$ echo "Hello world" > test.txt
$ davix-get -P Grid https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt \
    -H "Range: 0-1,3-4,8-10"

--jetty1913974379i77mn8om
Content-Type: text/plain
Content-Range: bytes 0-1/12

He
--jetty1913974379i77mn8om
Content-Type: text/plain
Content-Range: bytes 3-4/12

lo
--jetty1913974379i77mn8om
Content-Type: text/plain
Content-Range: bytes 8-10/12

rld
--jetty1913974379i77mn8om--

```

CURL example:

```bash
$ curl https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt \
    --cert $X509_USER_PROXY \
    --capath /etc/grid-security/certificates \
    --cacert /usr/share/igi-test-ca/test0.cert.pem \
    -H "Range: 0-1,3-4,8-10"
```

## PUT <a name="put">&nbsp;</a>

The PUT method requests that the enclosed entity be stored under the supplied Request-URI. If the Request-URI refers to an already existing resource, the enclosed entity is considered as a modified version of the one residing on the origin server. If the Request-URI does not point to an existing resource, server creates the resource with that URI.

#### Upload local file `test.txt` to `test.vo` storage area

DAVIX example:

```bash
$ echo "Hello world" > test.txt
$ davix-put -P Grid --debug text.txt https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt

> PUT /test.vo/test.txt HTTP/1.1
> User-Agent: libdavix/0.4.0 neon/0.0.29
> Keep-Alive: 
> Connection: TE, Keep-Alive
> TE: trailers
> Host: omii006-vm03.cnaf.infn.it:9443
> Content-Length: 12
> Expect: 100-continue
> 
< HTTP/1.1 100 Continue
< HTTP/1.1 201 Created
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Strict-Transport-Security: max-age=31536000 ; includeSubDomains
< X-Frame-Options: DENY
< Set-Cookie: JSESSIONID=abwhzs5fhrwyf3jn2fb9u1uf;Path=/;Secure
< Expires: Thu, 01 Jan 1970 00:00:00 GMT
< Date: Fri, 13 Mar 2015 13:22:22 GMT
< Accept-Ranges: bytes
< ETag: "/storage/test.vo/test.txt_323800060"
< Transfer-Encoding: chunked
< 

$ davix-get -P Grid https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt
Hello world

```

CURL example:

```bash
$ curl -T test.txt https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt \
    --cert $X509_USER_PROXY \
    --capath /etc/grid-security/certificates \
    --cacert /usr/share/igi-test-ca/test0.cert.pem
```


## HEAD <a name="head">&nbsp;</a>

Acts like HTTP/1.1, so HEAD is a GET without a response message body.

#### HEAD file `test.txt` stored into `test.vo` storage area, to check if exists


```bash
$ echo "Hello world" > test.txt
$ davix-http -P Grid --debug -X HEAD https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt

< HTTP/1.1 200 OK
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Strict-Transport-Security: max-age=31536000 ; includeSubDomains
< X-Frame-Options: DENY
< Set-Cookie: JSESSIONID=1g5r0pqndsonh3kvlcz6nqhrc;Path=/;Secure
< Expires: Thu, 01 Jan 1970 00:00:00 GMT
< Content-Type: text/plain
< Last-Modified: Fri, 13 Mar 2015 13:22:22 GMT
< Content-Length: 12
< Accept-Ranges: bytes
< 
```

CURL example:

```bash
$ curl -X HEAD https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt \
    --cert $X509_USER_PROXY \
    --capath /etc/grid-security/certificates \
    --cacert /usr/share/igi-test-ca/test0.cert.pem
```


## MKCOL <a name="mkcol">&nbsp;</a>

MKCOL creates a new collection resource at the location specified by the Request-UI.

#### Create remote directory `testDir` into `test.vo` storage area

DAVIX example:

```bash
$ davix-mkdir -P Grid --debug https://omii006-vm03.cnaf.infn.it:9443/test.vo/testDir
< HTTP/1.1 201 Created
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Strict-Transport-Security: max-age=31536000 ; includeSubDomains
< X-Frame-Options: DENY
< Set-Cookie: JSESSIONID=1vaxmyqwph6bx17glea63khsfy;Path=/;Secure
< Expires: Thu, 01 Jan 1970 00:00:00 GMT
< Transfer-Encoding: chunked
< 
```

CURL example:

```bash
curl -X MKCOL https://omii006-vm03.cnaf.infn.it:9443/test.vo/testDir \
    --cert $X509_USER_PROXY \
    --capath /etc/grid-security/certificates \
    --cacert /usr/share/igi-test-ca/test0.cert.pem
```


## DELETE <a name="delete">&nbsp;</a>

DELETE remove the resource identified by the Request-URI. If the resource is a collection, every resource contained is recursively removed.

#### Delete remote file `test.txt` from `test.vo` storage area

DAVIX example:

```bash
$ echo "Hello world" > test.txt
$ davix-put -P Grid text.txt https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt
$ davix-rm -P Grid --debug https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt
< HTTP/1.1 204 No Content
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Strict-Transport-Security: max-age=31536000 ; includeSubDomains
< X-Frame-Options: DENY
< Set-Cookie: JSESSIONID=1xd4oo2aao53i1abg7b8no4nae;Path=/;Secure
< Expires: Thu, 01 Jan 1970 00:00:00 GMT
< Date: Fri, 13 Mar 2015 13:55:07 GMT
< Accept-Ranges: bytes
< ETag: "/storage/test.vo/test.txt_0"
< 
```

CURL example:

```bash
curl -X DELETE https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt \
    --cert $X509_USER_PROXY \
    --capath /etc/grid-security/certificates \
    --cacert /usr/share/igi-test-ca/test0.cert.pem
```

## OPTIONS <a name="options">&nbsp;</a>

OPTIONS in our case returns `"DAV: 1"` header cause LOCK is disabled.

```bash
$ davix-http -P Grid -X OPTIONS --debug https://omii006-vm03.cnaf.infn.it:9443/test.vo

> OPTIONS /test.vo HTTP/1.1
> User-Agent: libdavix/0.4.0 neon/0.0.29
> Keep-Alive: 
> Connection: TE, Keep-Alive
> TE: trailers
> Host: omii006-vm03.cnaf.infn.it:9443
> 
< HTTP/1.1 200 OK
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Strict-Transport-Security: max-age=31536000 ; includeSubDomains
< X-Frame-Options: DENY
< Set-Cookie: JSESSIONID=bnf5rmr8f2mo12aca4ka26g6i;Path=/;Secure
< Expires: Thu, 01 Jan 1970 00:00:00 GMT
< DAV: 1
< MS-Author-Via: DAV
< Date: Fri, 13 Mar 2015 13:16:47 GMT
< Accept-Ranges: bytes
< ETag: "/storage/test.vo_256368916"
< Allow: COPY, DELETE, MKCOL, PROPFIND, PROPPATCH, OPTIONS, MOVE, PUT
< Content-Length: 0
<
```

## PROPFIND <a name="propfind">&nbsp;</a>

The PROPFIND operation retrieves, in XML format, the properties defined on the resource identified by the Request-URI. 

Clients must submit a `Depth` header with a value of `"0"`, `"1"`, or `"infinity"` (default is `"Depth: infinity"`).

Clients may submit through the body of the request a 'propfind' XML element. It's used to describe what information is being requested: 

* a particular property value (by using the 'prop' element)

```xml
<?xml version="1.0" encoding="utf-8" ?> 
  <D:propfind xmlns:D="DAV:"> 
    <D:prop xmlns:R="http://ns.example.com/boxschema/"> 
      <R:author/> 
      <R:title/> 
    </D:prop> 
  </D:propfind>
```

In this example, the propfind XML element specifies the name of two properties whose values are being requested.

* all property values (by using the 'allprop' element);

```xml
<?xml version="1.0" encoding="utf-8" ?>
  <D:propfind xmlns:D="DAV:">
    <D:allprop/>
  </D:propfind>
```

In this example, the request should return the name and value of all the properties defined by WebDAV specification plus the user defined properties.

* the list of names of all the properties defined on the resource (by using the 'propname' element).

```xml
<?xml version="1.0" encoding="utf-8" ?> 
  <propfind xmlns="DAV:"> 
    <propname/> 
  </propfind> 
```


## COPY <a name="copy">&nbsp;</a>

COPY method duplicates the resource identified by the Request-URI to the resource identified by the `Destination` header URI. `Destination` header MUST be present.

If destination resource already exists the `Destination` header value is evaluated. Available values are `T` (true, default) and `F` (false).

#### Copy remote `test.vo` file's `test.txt` to `test2.txt`

DAVIX example:

```bash
$ echo "Hello world" > test.txt
$ davix-put -P Grid text.txt https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt
$ davix-cp -P Grid --debug https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt https://omii006-vm03.cnaf.infn.it:9443/test.vo/test2.txt
> COPY /test.vo/test.txt HTTP/1.1
> User-Agent: libdavix/0.4.0 neon/0.0.29
> Keep-Alive: 
> Connection: TE, Keep-Alive
> TE: trailers
> Host: omii006-vm03.cnaf.infn.it:9443
> Destination: https://omii006-vm03.cnaf.infn.it:9443/test.vo/test2.txt
> X-Number-Of-Streams: 1
>
< HTTP/1.1 201 Created
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Strict-Transport-Security: max-age=31536000 ; includeSubDomains
< X-Frame-Options: DENY
< Set-Cookie: JSESSIONID=dnyk3ni98vyj1bwe56n50fpif;Path=/;Secure
< Expires: Thu, 01 Jan 1970 00:00:00 GMT
< Date: Fri, 13 Mar 2015 13:58:29 GMT
< Accept-Ranges: bytes
< ETag: "/storage/test.vo/test.txt_325854060"
< Transfer-Encoding: chunked
```

CURL example:

```bash
$ curl -X COPY https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt --cert $X509_USER_PROXY --capath /etc/grid-security/certificates --cacert /usr/share/igi-test-ca/apostrofe.cert.pem -H "Destination: https://omii006-vm03.cnaf.infn.it:9443/test.vo/test3.txt"
```

#### Copy remote `test.vo` file's `test.txt` to `test2.txt` that already exists with `Overwrite: F`

DAVIX example:

```bash
$ echo "Hello world" > test.txt
$ davix-put -P Grid text.txt https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt
$ davix-put -P Grid text.txt https://omii006-vm03.cnaf.infn.it:9443/test.vo/test2.txt
$ davix-cp -P Grid --debug -H "Overwrite: F" https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt https://omii006-vm03.cnaf.infn.it:9443/test.vo/test2.txt
> COPY /test.vo/test2.txt HTTP/1.1
> User-Agent: libdavix/0.4.0 neon/0.0.29
> Keep-Alive: 
> Connection: TE, Keep-Alive
> TE: trailers
> Host: omii006-vm03.cnaf.infn.it:9443
> Destination: https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt
> X-Number-Of-Streams: 1
> Overwrite:  F
> 
< HTTP/1.1 412 Precondition Failed
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Strict-Transport-Security: max-age=31536000 ; includeSubDomains
< X-Frame-Options: DENY
< Set-Cookie: JSESSIONID=19frfsmgug5gws8osgfda769q;Path=/;Secure
< Expires: Thu, 01 Jan 1970 00:00:00 GMT
< Transfer-Encoding: chunked
< 
```

CURL example:

```bash
$ curl -X COPY https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt --cert $X509_USER_PROXY --capath /etc/grid-security/certificates --cacert /usr/share/igi-test-ca/apostrofe.cert.pem -H "Destination: https://omii006-vm03.cnaf.infn.it:9443/test.vo/test3.txt" -H "Overwrite: F"
```


## MOVE <a name="move">&nbsp;</a>

MOVE operation is the logical equivalent of a COPY followed by a DELETE of the  Request-URI. All these actions must be performed as a unique operation. As already seen for COPY method, also for every MOVE the `Destination` header MUST be present.

#### Move remote `test.vo` file's `test.txt` to `test2.txt`

DAVIX example:

```bash
$ echo "Hello world" > test.txt
$ davix-put -P Grid text.txt https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt
$ davix-mv -P Grid --debug https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt https://omii006-vm03.cnaf.infn.it:9443/test.vo/test2.txt
> MOVE /test.vo/test.txt HTTP/1.1
> User-Agent: libdavix/0.4.0 neon/0.0.29
> Keep-Alive: 
> Connection: TE, Keep-Alive
> TE: trailers
> Host: omii006-vm03.cnaf.infn.it:9443
> Destination: https://omii006-vm03.cnaf.infn.it:9443/test.vo/test2.txt
> 
< HTTP/1.1 201 Created
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Strict-Transport-Security: max-age=31536000 ; includeSubDomains
< X-Frame-Options: DENY
< Set-Cookie: JSESSIONID=fcyai4anga5a73pcoyltszrl;Path=/;Secure
< Expires: Thu, 01 Jan 1970 00:00:00 GMT
< Date: Mon, 16 Mar 2015 09:28:40 GMT
< Accept-Ranges: bytes
< ETag: "/storage/test.vo/test.txt_0"
< Transfer-Encoding: chunked
<
```

CURL example:

```bash
$ curl -X MOVE https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt --cert $X509_USER_PROXY --capath /etc/grid-security/certificates --cacert /usr/share/igi-test-ca/test0.cert.pem -H "Destination: https://omii006-vm03.cnaf.infn.it:9443/test.vo/test2.txt"
```

#### Move remote `test.vo` file's `test.txt` to `test2.txt` that already exists

```bash
$ echo "Hello world" > test.txt
$ davix-put -P Grid text.txt https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt
$ davix-put -P Grid text.txt https://omii006-vm03.cnaf.infn.it:9443/test.vo/test2.txt
$ davix-mv -P Grid --debug https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt https://omii006-vm03.cnaf.infn.it:9443/test.vo/test2.txt
> MOVE /test.vo/test.txt HTTP/1.1
> User-Agent: libdavix/0.4.0 neon/0.0.29
> Keep-Alive: 
> Connection: TE, Keep-Alive
> TE: trailers
> Host: omii006-vm03.cnaf.infn.it:9443
> Destination: https://omii006-vm03.cnaf.infn.it:9443/test.vo/test2.txt
> 
< HTTP/1.1 412 Precondition Failed
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Strict-Transport-Security: max-age=31536000 ; includeSubDomains
< X-Frame-Options: DENY
< Set-Cookie: JSESSIONID=14e5nxdetfgxdnlbod8oc8gle;Path=/;Secure
< Expires: Thu, 01 Jan 1970 00:00:00 GMT
< Transfer-Encoding: chunked
< 
```

CURL example:

```bash
$ curl -X MOVE https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt --cert $X509_USER_PROXY --capath /etc/grid-security/certificates --cacert /usr/share/igi-test-ca/test0.cert.pem -H "Destination: https://omii006-vm03.cnaf.infn.it:9443/test.vo/test2.txt" -H "Overwrite: F"
```


# Appendix A - Firefox RESTClient plugin <a name="usingrestclient">&nbsp;</a>

There's a useful Firefox plugin, named <a href="https://addons.mozilla.org/it/firefox/addon/restclient/">RESTClient</a>, that can be used as a debugger for RESTful web services. RESTClient supports all HTTP methods <a href="http://www.w3.org/Protocols/rfc2616/rfc2616.html">RFC2616</a> (HTTP/1.1) and <a href="http://www.webdav.org/specs/rfc2518.html">RFC2518</a> (WebDAV). You can construct custom HTTP request (custom method with resources URI and HTTP request Body) to directly test requests against a server.

![RESTClient home screenshot]({{ site.baseurl }}/assets/images/restclient.png "RESTClient home screenshot")



[storm-1-11-7]: {{ site.baseurl }}/release-notes/StoRM-v1.11.7.html
[vomapfiles]: {{ site.baseurl }}/documentation/sysadmin-guide/{{site.versions.docs.sysadmin_guide.current}}/#dav_advconf
[webdavconf]: {{ site.baseurl }}/documentation/sysadmin-guide/{{site.versions.sysadmin_guide}}/#webdavconf
