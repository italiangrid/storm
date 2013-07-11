---
layout: default
title: StoRM Documentation - toRM GridHTTPs Server's WebDAV interface
---

## StoRM GridHTTPs Server's WebDAV interface

### Table of contents

* [Introduction](#introduction)
* [What is WebDAV?](#whatwebdav)
* [SRM operations via WebDAV](#mappingdavsrm)
* [Service installation and configuration](#installconf)
* [Using WebDAV](#usingwebdav)
  * [Access data via browser](#usingbrowsers)
  * [cURLs](#usingcurls)
  * [Firefox RESTClient plugin](#usingrestclient)
  * [Cyberduck](#cyberduck)

### Introduction <a name="introduction">&nbsp;</a>

The StoRM GridHTTPs is the component responsible for providing both HTTP(s) file transfer capabilities to a StoRM endpoint and a WebDAV interface. The brand-new WebDAV interface provided, conceals the details of the SRM protocol and allows users to mount remote Grid storage as a volume on their own desktops. It represents a single entry point to the storage data both for file management and transferring by providing different authentication models (from typical grid x.509 proxies and standard x.509 certificates to anonymous http read access), maintaining at the same time full compliance with present Grid standards.

### What is WebDAV? <a name="whatwebdav">&nbsp;</a>

<img src="{{ site.baseurl }}/assets/images/webdav-logo.jpg" alt="webdav-logo" width="100" style="float: right; margin-right: 10px; margin-left: 30px; margin-top: -5px;"/>

Web Distributed Authoring and Versioning (WebDAV) protocol consists of a set of methods, headers, and content-types ancillary to HTTP/1.1 for the management of resource properties, creation and management of resource collections, URL namespace manipulation, and resource locking. The purpose of this protocol is to present a Web content as a writable medium in addition to be a readable one. [WebDAV on Wikipedia](http://en.wikipedia.org/wiki/WebDAV) and the [WebDAV website](http://www.webdav.org/) provide information on this protocol.

In a few words, the WebDAV protocol mainly abstracts concepts such as resource properties, collections of resources, locks in general, and write locks specifically. These abstractions are manipulated by the WebDAV-specific HTTP methods and the extra HTTP headers used with WebDAV methods. The WebDAV added methods include:

* PROPFIND — used to retrieve properties, stored as XML, from a web resource. It is also overloaded to allow one to retrieve the collection structure (a.k.a. directory hierarchy) of a remote system.
* PROPPATCH — used to change and delete multiple properties on a resource in a single atomic act.
* MKCOL — used to create collections (a.k.a. a directory).
* COPY — used to copy a resource from one URI to another.
* MOVE — used to move a resource from one URI to another.
* LOCK — used to put a lock on a resource. WebDAV supports both shared and exclusive locks.
* UNLOCK — used to remove a lock from a resource.

While the status codes provided by HTTP/1.1 are sufficient to describe most error conditions encountered by WebDAV methods, there are some errors that do not fall neatly into the existing categories, so the WebDAV specification defines some extra status codes. Since some WebDAV methods may operate over many resources, the Multi-Status response has been introduced to return status information for multiple resources.
WebDAV uses XML for property names and some values, and also uses XML to marshal complicated requests and responses.

#### SRM operations via WebDAV <a name="mappingdavsrm">&nbsp;</a>

Starting from EMI3 version, StoRM GridHTTPs server exposes a WebDAV interface to allow users to access Storage-Areas data via browser or by mounting it from a remote host.


<img src="{{ site.baseurl }}/assets/images/milton.png" alt="milton-logo" width="180" style="float: left; margin-right: 20px; margin-left: 0px; margin-top: 9px;"/>
GridHTTPs' WebDAV implementation is based on [*Milton*](http://milton.io/) open source java library that acts as an API and HTTP protocol handler for adding the WebDAV support to web applications. *Milton* is not a full server in itself. It is able to expose any existing data source (e.g. CMS, hibernate pojos, etc) through a WebDAV interface.

As seen in the chapter before, through a WebDAV interface we are allowed to manipulate resources and collections of them. So it is simple to understand that a WebDAV resource for StoRM GridHTTPs WebDAV implementation will be a file, while WebDAV collections will be directories of a file-system. Every WebDAV method needs to be mapped to one or more SRM operations that have to be transparent to the final users.
StoRM GridHTTPs maps the HTTP/WebDAV methods with the SRM operations as shown by the following table:

| HTTP/WebDAV Method | Description | SRM Operation | Status codes |
|:------------------:|:------------|:--------------|:-------------|
|**GET**	|GET is defined as "*retrieve whatever information (in the form of an entity) is identified by the Request-URI*" (see [RFC2616](http://tools.ietf.org/html/rfc2616.txt)). GET applied to a file retrieves file's content. GET, when applied to a collection, returns an HTML resource that is a human-readable view of the contents of the collection. |If resource is a file then a ***srmPtg*** and a ***srmRf*** are performed before and after the *file transfer* from server. If resource is a directory then a ***srmLs*** is performed to retrieve its content.| Success when status code is **OK 200**.
|**PUT**	|The PUT method requests that the enclosed entity be stored under the supplied Request-URI. If the Request-URI refers to an already existing resource, the enclosed entity is considered as a modified version of the one residing on the origin server. If the Request-URI does not point to an existing resource, server creates the resource with that URI.|Resource has to be a file so a ***srmPtp*** and a ***srmPd*** are performed before and after the *file transfer* from client.|Success when status code is **Created 201** or **No Content 204** (already existing resource). If status code is **Conflict 409** one or more intermediate collections doesn't exist and you can't perform a PUT.
| **HEAD**	| Acts like HTTP/1.1, so HEAD is a GET without a response message body.	| none	| Success when status code is **OK 200**
| **OPTIONS**| Returns "DAV: 1" header.	| Performs an additional ***srmPing***	| Success when status code is **OK 200**
| **MKCOL**	| MKCOL creates a new collection resource at the location specified by the Request-URI.| ***srmMkdir*** | Success when status code is **Created 201**. **Conflict 409** means that one or more intermediate collections doesn't exist. **Method Not Allowed 415** means that collection already exists.
| **DELETE**| Delete the resource identified by the Request-URI.| ***srmRm*** or ***srmRmdir*** (with ```-r``` recursive option in case directory is not empty)| Success when status code is **No Content 204**
| **COPY**	| The COPY method creates a duplication of the source resource identified by the Request-URI, in the destination resource identified by the URI in the Destination header. The Destination header MUST be present.| The COPY of a file has been implemented with a PUT of the file read from request-URI to the request's destination URI. The COPY of a directory has been implemented as a recoursive serie of MKCOL/PUT.| Success when status code is **Created 201** or **No Content 204** (already existing destination). **Conflict 409** means that one or more intermediate collections doesn't exist. **Forbidden 403** is a retrived if source and destination URI are the same.
| **MOVE**	| The MOVE operation is the logical equivalent of a COPY followed by a delete of the source. All these actions has to be performed in a single operation. The Destination header MUST be present on all MOVE methods.| ***srmMv***| Success when status code is **Created 201** or **No Content 204** (already existing destination). **Conflict 409** means that one or more intermediate collections doesn't exist. **Forbidden 403** is a retrived if source and destination URI are the same.
| **PROPFIND**| The PROPFIND operation retrieves, in XML format, the properties defined on the resource identified by the Request-URL. Clients must submit a *Depth* header with a value of "0", "1", or "infinity" (default is "Depth: infinity"). Clients may submit a 'propfind' XML element in the body of the request method describing what information is being requested: a particular property values, by naming the properties desired within the 'prop' element, all property values including additional by using the 'allprop' element (e.g. checksum tyoe and value), the list of names of all the properties defined on the resource by using the 'propname' element.| ***srmLs*** with ```-l``` (detailed) option| Success when status is ***Multi-Status 207***.
| **POST** 	 	| - | *not allowed* | - 
| **TRACE** 	| - | *not allowed* | - 
| **CONNECT** 	| - | *not allowed* | - 
| **LOCK** 		| - | *not allowed* | - 
| **UNLOCK** 	| - | *not allowed* | - 
| **PROPPATCH** | - | *not allowed* | - 

### Service installation and configuration <a name="installconf">&nbsp;</a>

The WebDAV interface is provided by StoRM GridHTTPs component. 
Therefore, if you want to install a WebDAV access point to your data you have to install StoRM GridHTTPs metapackage RPM (do not forget to satisfy all the [pre-requisites shown in the sys-admin guide][prereq] before):

	  [~]# yum install emi-storm-gridhttps-mp

To configure storm-gridhttps-server you need to fill the requested YAIM variables as described in the [basic][ghttp_basic_conf] and [advanced][ghttp_advanced_conf] StoRM GridHTTPs sys-admin configuration guides. 
A good explanation of the required YAIM variable is available in:

	/opt/glite/yaim/examples/siteinfo/services/se_storm_gridhttps

The service uses (by default) ports 8443 and 8085, so open them on your firewall.

The service needs to be installed on a machine on which storm file system is mounted. If you need, you can install the StoRM GridHTTPs on differents hosts (that share the same data, e.g. hosts are GPFS clients) and use them as a pool (see [StoRM BackEnd configuration on sys-admin guide][be_conf]).
To start the service:

	  [~]# service storm-gridhttps-server start


### Using WebDAV <a name="usingwebdav">&nbsp;</a>

The StoRM GridHTTPs WebDAV server listens on two ports, one for the unencrypted HTTP connections and another for the SSL encrypted HTTP requests. Their default values are:

* HTTP: **8085**
* HTTP over SSL: **8443**

User can access storage area data by using a browser, by using cURLs or several third-party WebDAV clients. They also can develop a client on their own, for example by using the <a href="http://jackrabbit.apache.org/">Apache Jackrabbit API</a>.

#### Access data via browser <a name="usingbrowsers">&nbsp;</a>

<img src="{{ site.baseurl }}/assets/images/browser-logos.jpg" alt="brower-logos" width="200" style="float: right; margin-right: 50px;"/>

Users can use browsers to easily read data of storage areas that are:

- HTTP readable (see the storage area configuration described in the [StoRM BackEnd configuration on sys-admin guide][be_conf])
- not HTTP readable, not associated to a particular VO, eventually access-filtered through users' DN.

Using a browser, users can navigate through the storage areas' directories and download/open files. 

#### cURLs <a name="usingcurls">&nbsp;</a>

The best way to use the WebDAV service is using cURL command. cURL is a command line tool for transferring data with URL syntax (see [cURL website](http://curl.haxx.se/)). With cURLs we can do anonymous requests or provide our x509 credentials: personal certificate, plain Grid proxy, VOMS proxy. The following examples suppose that user has his/her personal certificate (*usercert.pem*) and key (*userkey.pem*) in $HOME/.globus directory, and his/her proxy in $X509\_USER\_PROXY.

##### Anonymous cURLs

Assuming that:

- *ghttps.hostname* is the hostname where your WebDAV endpoint is deployed
- *free* is the name of a R/W from anonymous Storage Area

and knowing that unencrypted connections have 8085 as default port, the following table show various cURLs, one for each HTTP/WebDAV method.

| Method | cURL | Notes |
|-------------------:|:----|:------|
| **GET** | <pre style="width: 500px"><code>curl -v -X GET http://ghttps.hostname:8085/free</code></pre><pre><code>curl -v http://ghttps.hostname:8085/free</code></pre> | In case you are getting a file, you can specify a *range* header to get a part of it. The method GET can be omitted because by default a cURL is an HTTP GET.
| **PUT** | To create the destination resource specifying its content via HTTP body: <pre><code>curl -v -X PUT http://ghttps.hostname:8085/free/filename.txt --data-ascii "file content"</code></pre> To create the destination resource by uploading a local existent file:<pre><code>curl -v -T /local/path/to/filename.txt http://ghttps.hostname:8085/free/filename.txt</code></pre> | By default, a PUT request overwrites the destination resource if exists, so there is an implicit ```'Overwrite: T'``` header. If you want to be sure that destination resource won't be overwritten, you have to add: ```--header 'Overwrite: F'```
| **MKCOL** | <pre><code>curl -v -X MKCOL http://ghttps.hostname:8085/free/newdirectory</code></pre> | -
| **DELETE** | <pre><code>curl -v -X DELETE http://ghttps.hostname:8085/free/existent_resource</code></pre> | If *existent\_resource* is a not empty directory, DELETE works recoursively deleting all the resources contained.
| **OPTIONS** | <pre><code>curl -v -X OPTIONS http://ghttps.hostname:8085/</code></pre>| There's no need to specify any storage area or resource with OPTIONS cURLs, the response is the same.
| **HEAD** | <pre><code>curl -v -X HEAD http://ghttps.hostname:8085/</code></pre>| There's no need to specify any storage area or resource with HEAD cURLs, the response is the same: a GET on the same URL without body.
| **PROPFIND** | <pre><code>curl -v -X PROPFIND http://ghttps.hostname:8085/free/path/to/resource</code></pre>| The *Depth* header with a value of "0", "1", or "infinity" (default is "Depth: infinity") is used to enable/disable recursion. HTTP request body is used to retrieve specific and/or more detailed information. It must be in XML format so it's necessary to add a `--header "Content-Type: text/xml"` header. Then, the body content is specified through the *data-ascii* option, that, first of all, contains the XML header: `--data-ascii "<?xml version='1.0' encoding='utf-8'?>..."`<br/>To obtain the list of names of all the resource properties complete it with:<br/>`<propfind xmlns='DAV:'><propname/></propfind>`<br/>To obtain the value of a single property complete it with:<br/>`<propfind xmlns='DAV:'><prop>property-name</prop></propfind>`<br/>To obtain all the property values complete it with:<br/>`<propfind xmlns='DAV:'><allprop/></propfind>`
| **COPY** | <pre><code>curl -X COPY http://ghttps.hostname:8085/free/existent_resource --header "Destination: http://ghttps.hostname:8085/free/unexistent_resource"</code></pre> | The *Destination* header must be present. The COPY method on a collection without a *Depth* header must act as if a *Depth: infinity* header was included. *Depth* header can be 0 or *infinity*. A COPY with ```--header "Depth: infinity"``` copies all its internal member resources, recursively through all levels of the collection hierarchy. A COPY with ```--header "Depth: 0"``` only instructs that the collection and its properties but not resources identified by its internal member URIs, are to be copied. If destination resource exists, the copy has success only if user specifies ```--header "Overwrite: T"```.
| **MOVE** | <pre><code>curl -X MOVE http://ghttps.hostname:8085/free/existent\_resource --header "Destination: http://ghttps.hostname:8085/free/unexistent_resource"</code></pre>| The *Destination* header must be present. The MOVE method act like a COPY and a DELETE of the source. If the destination resource exists, the move has success only if user specifies ```--header "Overwrite: T"```.

##### Using x509 credentials

If you need to present an x509 certificate to be authorized, you can add to your cURL command the following options:

	--cert path/to/usercert.pem --key path/to/userkey.pem --capath /path/to/the/trustdir

For example, assuming that:

* **A** is a storage area readable and writable only with an x509 certificate that has *O="INFN"*
* $HOME/.globus/usercert.pem and $HOME/.globus/userkey.pem are user's certificate and private key
* $HOME/.globus/usercert.pem has *O="INFN"*
* the trust directory with CA informations is in */etc/grid-security/certificates*

and knowing that encrypted connections has 8443 as default port, we can perform a cURL like this:

	curl --verbose -X GET https://gridhttps.hostname:8443/A/ --cert $HOME/.globus/usercert.pem --key $HOME/.globus/userkey.pem --capath /etc/grid-security/certificates

and retrieve the list of file/directories in the root directory of the storage area **A**.

##### Using proxy credentials

If you need to present an x509 proxy - plain or with VOMS extensions - to be authorized, you can add to your cURL command the following options:

	--cert path/to/yourproxy --capath /path/to/the/trustdir

For example, assuming that:

* **B** is a storage area readable and writable only with an x509 VOMS proxy for dteam VO
* $X509\_USER\_PROXY contains the path to the user's VOMS proxy
* the trust directory with CA informations is in */etc/grid-security/certificates*

and knowing that encrypted connections has 8443 as default port, we can perform a cURL like this:

	curl --verbose -X GET https://gridhttps.hostname:8443/B/ --cert $X509_USER_PROXY --capath /etc/grid-security/certificates

and retrieve the list of file/directories in the root directory of the storage area **B**.

#### Firefox RESTClient plugin <a name="usingrestclient">&nbsp;</a>

There's a useful Firefox plugin, named <a href="https://addons.mozilla.org/it/firefox/addon/restclient/">RESTClient</a>, that can be used as a debugger for RESTful web services. RESTClient supports all HTTP methods <a href="http://www.w3.org/Protocols/rfc2616/rfc2616.html">RFC2616</a> (HTTP/1.1) and <a href="http://www.webdav.org/specs/rfc2518.html">RFC2518</a> (WebDAV). You can construct custom HTTP request (custom method with resources URI and HTTP request Body) to directly test requests against a server.

![RESTClient home screenshot]({{ site.baseurl }}/assets/images/restclient.png "RESTClient home screenshot")

#### Cyberduck <a name="usingcyberduck">&nbsp;</a>

To connect to HTTP readable storage area you can use several clients. One of this is <a href="http://cyberduck.ch/">Cyberduck</a>. Cyberduck is an open source FTP and SFTP, WebDAV, Cloud Files, Google Docs, and Amazon S3 client for Mac OS X and Windows (as of version 4.0) licensed under the GPL. To configure it, add a new connection and insert:

* *server*: the FQDN of the gridhttps host
* *port*: the unencrypted HTTP port, default 8085
* select *anonymous login*
* specify /storage-area-name as *remote path*

This configuration is the same for lots of WebDAV clients, alternatives to Cyberduck.

<img src="{{ site.baseurl }}/assets/images/cyberduck.png" alt="cyberduck" style="margin: 10px auto;"/>



[ghttp_basic_conf]: {{ site.baseurl }}/documentation/sysadmin-guide/{{ site.storm_latest_version }}/#ghttpconf
[ghttp_advanced_conf]: {{ site.baseurl }}/documentation/sysadmin-guide/{{ site.storm_latest_version }}/#ghttp_advconf
[be_conf]: {{ site.baseurl }}/documentation/sysadmin-guide/{{ site.storm_latest_version }}/#beconf
[prereq]: {{ site.baseurl }}/documentation/sysadmin-guide/{{ site.storm_latest_version }}/#installprereq
