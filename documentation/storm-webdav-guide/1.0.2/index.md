---
layout: toc
title: StoRM Documentation - StoRM WebDAV user guide
---

# StoRM WebDAV User Guide

* [The WebDAV protocol](#theprotocol)
* [StoRM WebDAV](#stormwebdav)
  * [Installation and configuration](#installandconf)
  * [Endpoints](#davendpoints)
  * [Authentication and authorization](#authandauth)
  * [Notes](#notes)
* [Examples](#examples)
  * [Download file](#ex1)
  * [Download multiple file ranges](#ex2)
  * [Upload file](#ex3)
  * [Check if resource exists](#ex4)
  * [Create directory](#ex5)
  * [Delete file](#ex6)
  * [Copy/duplicate file](#ex7)
  * [Move/rename file](#ex8)
  * [List directory](#ex9)

## The WebDAV protocol <a name="theprotocol">&nbsp;</a>

<img src="{{ site.baseurl }}/assets/images/webdav-logo.jpg" alt="webdav-logo" width="100" style="float: left; margin-right: 30px; margin-left: 0px; margin-top: 10px;"/>

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

## StoRM WebDAV <a name="stormwebdav">&nbsp;</a>

From [StoRM v.1.11.7][storm-1-11-7] release, the StoRM service that provides valid WebDAV endpoints for each managed storage area is *StoRM WebDAV*.

*StoRM WebDAV* replaces the *StoRM gridhttps service*. All sites installing StoRM and providing HTTP and WebDAV endpoints should upgrade to the StoRM WebDAV service for improved performance and stability of the service as soon as possible.

**Important**: The StoRM WebDAV service is released and supported only on SL/CENTOS 6.

### Installation and configuration <a name="installandconf">&nbsp;</a>

See the [System Administration Guide][webdavconf] to learn how to install and configure the service. 

### Endpoints <a name="davendpoints">&nbsp;</a>

For each Storage Area, both/either a plain HTTP and/or a HTTP over SSL endpoint can be enabled. The default ports are **8085** (HTTP) and **8443** (HTTPS). 
All the following URLs are valid endpoints for a storage area:

    http://example.infn.it:8085/storage_area_accesspoint
    https://example.infn.it:8443/storage_area_accesspoint
    
To fully support the old *StoRM GridHTTPs* webdav endpoints, used until StoRM v1.11.6, all the URLs with *webdav* context path are accepted by *StoRM WebDAV*:
    
    http://example.infn.it:8085/webdav/storage_area_accesspoint
    https://example.infn.it:8443/webdav/storage_area_accesspoint

### Authentication and authorization <a name="authandauth">&nbsp;</a>

Users authentication within *StoRM WebDAV* is made through a valid VOMS proxy. All the users that provide a valid x509 VOMS proxy are authorized to access all the content of the storage area in read/write mode.

The most common way to authenticate and be authorized to read/write data into a Storage Area is by providing the right VOMS credentials through a valid VOMS Proxy. Otherwise, through the definition of a VOMS map file, a Storage Area can be configure to accept the list of VO members as obtained by running the `voms-admin list-users` command.
When VOMS mapfiles are enabled, users can authenticate to the StoRM webdav
service using the certificate in their browser and be granted VOMS attributes
if their subject is listed in one of the supported VOMS mapfile. For each supported VO, a file having the same name as the VO is put in the voms-mapfiles directory (`/etc/storm/storm-webdav/vo-mapfiles.d`). 

Example: to generate a VOMS mapfile for the cms VO, run the following command

```bash
  voms-admin --host voms.cern.ch --vo cms list-users > cms
```

See more details [here][vomapfiles]. Read permissions of the content of a storage area can also be extendend to anonymous user (it's disabled by default).

### Notes <a name="notes">&nbsp;</a>

Both the old ```storm-gridhttps-server``` and the new ```storm-webdav``` components implements WebDAV protocol by using [*Milton*](http://milton.io/) open source java library.

![milton]({{ site.baseurl }}/assets/images/milton.png)

## Examples <a name="examples">&nbsp;</a>

The most common WebDAV clients are:

* browsers
* command-line tools like cURLs and davix
* a third-party GUI

Currently, users are used to connect to a WebDAV endpoint providing a valid username and password or as anonymous users (if supported). But, as seen in the [Authentication and authorization](#authandauth) paragraph, in our case the most common use case is providing a valid VOMS proxy. The VOMS proxies are supported only by command-line tools. Browsers can be used to navigate into the storage area content in the some cases:

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


<br/>
### Download file <a name="ex1">&nbsp;</a>

Having the remote file:

**/test.vo/test.txt**

```bash
Hello world
```

use:

{% assign example=site.data.davexamples.downloadfile %}
{% include tab-template.liquid %}

<br/>
### Download multiple file ranges <a name="ex2">&nbsp;</a>

Having the remote file:

**/test.vo/test.txt**

```bash
Hello world
```

use:

{% assign example=site.data.davexamples.downloadfileranges %}
{% include tab-template.liquid %}

<br/>
### Upload file <a name="ex3">&nbsp;</a>

{% assign example=site.data.davexamples.uploadfile %}
{% include tab-template.liquid %}

<br/>
### Check if resource exists <a name="ex4">&nbsp;</a>

To check if a resource exists without download any data in case of a file, the HTTP HEAD method is used. HEAD acts like HTTP/1.1, so HEAD is a GET without a response message body.

{% assign example=site.data.davexamples.existsfile %}
{% include tab-template.liquid %}

<br/>
### Create directory <a name="ex5">&nbsp;</a>

{% assign example=site.data.davexamples.createdir %}
{% include tab-template.liquid %}

<br/>
### Delete file <a name="ex6">&nbsp;</a>

{% assign example=site.data.davexamples.deletefile %}
{% include tab-template.liquid %}

<br/>
### Copy/duplicate file <a name="ex7">&nbsp;</a>

{% assign example=site.data.davexamples.copyfile %}
{% include tab-template.liquid %}

<br/> 
### Move/rename file <a name="ex8">&nbsp;</a>

{% assign example=site.data.davexamples.movefile %}
{% include tab-template.liquid %}

<br/>

### List directory <a name="ex9">&nbsp;</a>

There are two ways to get the list of the resources into a remote directory: 

- the first is through an HTTP GET which returns a human readable version of all the content of the directory;
- the second is through a WebDAV PROPFIND which returns a structured XML body.

The PROPFIND operation retrieves, in XML format, the properties defined on the resource identified by the Request-URI. Clients must submit a `Depth` header with a value of `"0"`, `"1"`, or `"infinity"` (default is `"Depth: infinity"`).

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

To list all the content of a remote directory we can use the 'allprop' XML body, with depth equal to 1:

{% assign example=site.data.davexamples.propfinddir %}
{% include tab-template.liquid %}

To list all the properties of a remote file we can use the same 'allprop' XML body:

{% assign example=site.data.davexamples.propfindfile %}
{% include tab-template.liquid %}

[storm-1-11-7]: {{ site.baseurl }}/release-notes/StoRM-v1.11.7.html
[webdavconf]: {{ site.baseurl }}/documentation/sysadmin-guide/1.11.8/#webdavconf
[vomapfiles]: https://github.com/italiangrid/storm-webdav/blob/master/etc/storm-webdav/vo-mapfiles.d/README.md