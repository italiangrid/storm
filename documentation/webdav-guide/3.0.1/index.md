---
layout: toc
title: StoRM Documentation - StoRM GridHTTPs Server provides a WebDAV interface
version: 3.0.1
redirect_from:
  - /documentation/webdav-guide/
---

# StoRM WebDAV interface

version: {{ page.version }}

<div class="alert alert-error">
<h4 style="margin-bottom: 0px;">READ THIS</h4>
<div id="note300" style="margin-top: 20px;">
From StoRM GridHTTPs server version <b>3.x</b> the WebDAV service is provided at:
<pre><code>http(s)://[gridhttps.hostname]:[port]/webdav/[storage-area]/</code></pre>
instead of:
<pre><code>http(s)://[gridhttps.hostname]:[port]/[storage-area]/</code></pre>
</div>
</div>

**Table of contents**

* [Introduction](#introduction)
* [What is WebDAV?](#what-is-webdav)
* [SRM operations via WebDAV](#srm-operations-via-webdav)
* [Installation and configuration](#installation-and-configuration)
* [Using WebDAV](#using-webdav)
  * [Access data via browser](#access-data-via-browser)
  * [cURLs](#curls)
  * [Firefox RESTClient plugin](#firefox-restclient-plugin)
  * [Cyberduck](#cyberduck)

## Introduction

Each Storage Area that supports HTTP or HTTPS transfer protocols can be accessed through the WebDAV interface provided by the `storm-gridhttps-server` component. This WebDAV interface conceals the details of the SRM protocol and allows users to mount remote Grid storage areas as a volume, directly on their own desktop.

To access the Storage Area's data users have to provide the right credentials. For example, if the Storage Area *A* is owned by the VO *X*, user has to provide a valid VOMS proxy. If the Storage Area *B* is owned by the VO *Y* but permits a  read-only access to anonymous (see [examples section][anonymous-read-example]), user has to provide a valid VOMS proxy only if he wants to write data. And so on.

See the [examples section][examples-section] to other storage area configuration examples.

## What is WebDAV?

Web Distributed Authoring and Versioning (WebDAV) protocol consists of a set of methods, headers, and content-types ancillary to HTTP/1.1 for the management of resource properties, creation and management of resource collections, URL namespace manipulation, and resource locking. The purpose of this protocol is to present a Web content as a writable medium in addition to be a readable one. [WebDAV on Wikipedia](http://en.wikipedia.org/wiki/WebDAV) and the [WebDAV website](http://www.webdav.org/) provide information on this protocol.

In a few words, the WebDAV protocol mainly abstracts concepts such as resource properties, collections of resources, locks in general, and write locks specifically. These abstractions are manipulated by the WebDAV-specific HTTP methods and the extra HTTP headers used with WebDAV methods. The WebDAV added methods include:

* PROPFIND - used to retrieve properties, stored as XML, from a web resource. It is also overloaded to allow one to retrieve the collection structure (a.k.a. directory hierarchy) of a remote system.
* PROPPATCH - used to change and delete multiple properties on a resource in a single atomic act.
* MKCOL - used to create collections (a.k.a. a directory).
* COPY - used to copy a resource from one URI to another.
* MOVE - used to move a resource from one URI to another.
* LOCK - used to put a lock on a resource. WebDAV supports both shared and exclusive locks.
* UNLOCK - used to remove a lock from a resource.

While the status codes provided by HTTP/1.1 are sufficient to describe most error conditions encountered by WebDAV methods, there are some errors that do not fall neatly into the existing categories, so the WebDAV specification defines some extra status codes. Since some WebDAV methods may operate over many resources, the Multi-Status response has been introduced to return status information for multiple resources. WebDAV uses XML for property names and some values, and also uses XML to marshal complicated requests and responses.

### SRM operations via WebDAV

Starting from EMI3 version, the `storm-gridhttps-server` component exposes a WebDAV interface to allow users to access Storage-Areas data via browser or by mounting it from a remote host.

GridHTTPs' WebDAV implementation is based on [*Milton*](http://milton.io/) open source java library that acts as an API and HTTP protocol handler for adding the WebDAV support to web applications. *Milton* is not a full server in itself. It is able to expose any existing data source (e.g. CMS, hibernate pojos, etc) through a WebDAV interface.

As seen in the chapter before, through a WebDAV interface we are allowed to manipulate resources and collections of them. So it is simple to understand that a WebDAV resource for StoRM GridHTTPs WebDAV implementation will be a file, while WebDAV collections will be directories of a file-system. Every WebDAV method needs to be mapped to one or more SRM operations that have to be transparent to the final users.

StoRM GridHTTPs maps the HTTP/WebDAV methods with the SRM operations as shown by the following table:

<table>
	<thead>
		<th style="width: 3%; text-align: center;">Method</th>
		<th style="width: 40%">Description</th>
		<th style="width: 25%">SRM Operation</th>
		<th style="width: 32%">Main exit codes</th>
	</thead>
	<tbody>
		<tr>
			<td style="text-align: center;">
				<b>GET</b>
			</td>
			<td>
				GET is defined as "<i>retrieve whatever information (in the form of an entity) is identified by the Request-URI</i>"
				(see <a href="http://tools.ietf.org/html/rfc2616.txt">RFC2616</a>).
				GET applied to a file retrieves file's content.
				GET, when applied to a collection, returns an HTML resource that is a human-readable view of the contents of the collection.
			</td>
			<td>
				GET <b>directory</b>: <span class="label label-info">srmLs</span> <br/>
				GET <b>file</b>:<br/>
				&nbsp; 1. <span class="label label-info">srmPrepareToGet</span> <br/>
				&nbsp; 2. <i>read-file</i> from disk</li> <br/>
				&nbsp; 3. <span class="label label-info">srmReleaseFile</span> <br/>
			</td>
			<td>
				<span class="label label-success">200 OK</span> <br/>
				<span class="label label-important">404 Not Found</span> <br/>
				<span class="label label-important">409 Conflict</span> <br/>
				when file is in a <span class="label label-info">SRM_FILE_BUSY</span> state
			</td>
		</tr>
		<tr>
			<td style="text-align: center;">
				<b>PUT</b>
			</td>
			<td>
				The PUT method requests that the enclosed entity be stored under the supplied Request-URI.
				If the Request-URI refers to an already existing resource, the enclosed entity is considered as a modified version
				of the one residing on the origin server.
				If the Request-URI does not point to an existing resource, server creates the resource with that URI.
			</td>
			<td>
				Resource can't be a collection. <br/>
				PUT <b>file</b>: <br/>
				&nbsp; 1. <span class="label label-info">srmPrepareToPut</span> <br/>
				&nbsp; 2. <i>write-file</i> on disk <br/>
				&nbsp; 3. <span class="label label-info">srmPutDone</span>.
			</td>
			<td>
				<span class="label label-success">201 Created</span> file created <br/>
				<span class="label label-success">204 No Content</span> file overwrited <br/>
				<span class="label label-important">409 Conflict</span> one or more intermediate collections doesn't exist <br/>
				<span class="label label-important">405 Method Not Allowed</span> resource exists but it's a collection
			</td>
		</tr>
		<tr>
			<td style="text-align: center;">
				<b>HEAD</b>
			</td>
			<td>
				Acts like HTTP/1.1, so HEAD is a GET without a response message body
			</td>
			<td>
				<i>none</i>
			</td>
			<td>
				<span class="label label-success">200 OK</span> <br/>
				<span class="label label-important">404 Not Found</span>
			</td>
		</tr>
		<tr>
			<td style="text-align: center;">
				<b>OPTIONS</b>
			</td>
			<td>
				Returns "DAV: 1" header
			</td>
			<td>
				<i>none</i>
			</td>
			<td>
				<span class="label label-success">200 OK</span> <br/>
				<span class="label label-important">404 Not Found</span>
			</td>
		</tr>
		<tr>
			<td style="text-align: center;">
				<b>MKCOL</b>
			</td>
			<td>
				MKCOL creates a new collection resource at the location specified by the Request-UI.
			</td>
			<td>
				<span class="label label-info">srmMkdir</span>
			</td>
			<td>
				<span class="label label-success">201 Created</span> directory created <br/>
				<span class="label label-important">409 Conflict</span> means that one or more intermediate collections doesn't exist <br/>
				<span class="label label-important">415 Method Not Allowed</span> means that collection already exists
			</td>
		</tr>
		<tr>
			<td style="text-align: center;">
				<b>DELETE</b>
			</td>
			<td>
				Delete the resource identified by the Request-URI. If the resource is a collection, deletes every resource contained recursively.
			</td>
			<td>
				DELETE <b>file</b>: <span class="label label-info">srmRm</span> <br/>
				DELETE <b>directory</b>: <span class="label label-info">srmRmdir</span> with <span class="label">-r</span> recursive option
			</td>
			<td>
				<span class="label label-success">204 No Content</span> resource deleted <br/>
				<span class="label label-important">404 Not Found</span> resource doesn't exist
			</td>
		</tr>
		<tr>
			<td style="text-align: center;">
				<b>COPY</b>
			</td>
			<td>
				The COPY method creates a duplication of the source resource identified by the Request-URI,
				in the destination resource identified by the URI in the Destination header.
				The Destination header MUST be present.
			</td>
			<td>
				Actually the StoRM <span class="label label-info">srmCopy</span> is deprecated, so the COPY of a file becomes ar PUT of the file read from request-URI to the request's destination URI.
				The COPY of a directory is a recursive series of MKCOL/PUT.
			</td>
			<td>
				<span class="label label-success">201 Created</span> </br>
				<span class="label label-success">204 No Content</span> destination resource already exists <br/>
				<span class="label label-important">409 Conflict</span> means that one or more intermediate collections doesn't exist. <br/>
				<span class="label label-warning">403 Forbidden</span> is a retrieved if source and destination URI are the same.
				<span class="label label-important">412 Precondition-Failed</span> means that Destination URL is equal to source URL. <br/>
			</td>
		</tr>
		<tr>
			<td style="text-align: center;">
				<b>MOVE</b>
			</td>
			<td>
				The MOVE operation is the logical equivalent of a COPY followed by a delete of the source.
				All these actions has to be performed in a single operation.
				The Destination header MUST be present on all MOVE methods.
			</td>
			<td>
				<span class="label label-info">srmMv</span>
			</td>
			<td>
				<span class="label label-success">201 Created</span> or <br/>
				<span class="label label-success">204 No Content</span> if destination resource already exists <br/>
				<span class="label label-important">409 Conflict</span> means that one or more intermediate collections doesn't exist <br/>
				<span class="label label-warning">403 Forbidden</span> is retrieved if source and destination URI are the same.
				<span class="label label-important">412 Precondition-Failed</span> means that Destination URL is equal to source URL. <br/>
			</td>
		</tr>
		<tr>
			<td style="text-align: center;">
				<b>PROPFIND</b>
			</td>
			<td>
				The PROPFIND operation retrieves, in XML format, the properties defined on the resource identified by the Request-URL.
				Clients must submit a <i>Depth</i> header with a value of "0", "1", or "infinity" (default is "Depth: infinity").
				Clients may submit a 'propfind' XML element in the body of the request method describing what information
				is being requested: a particular property values, by naming the properties desired within the 'prop' element,
				all property values including additional by using the 'allprop' element (e.g. checksum type and value),
				the list of names of all the properties defined on the resource by using the 'propname' element.
			</td>
			<td>
				<span class="label label-info">srmLs</span> with <span class="label">-l</span> (detailed) option
			</td>
			<td>
				<span class="label label-success">207 Multi-Status</span>
			</td>
		</tr>
		<tr>
			<td style="text-align: center;"><b>POST</b></td><td>-</td><td><i>not allowed</i></td><td>-</td>
		</tr>
		<tr>
			<td style="text-align: center;"><b>TRACE</b></td><td>-</td><td><i>not allowed</i></td><td>-</td>
		</tr>
		<tr>
			<td style="text-align: center;"><b>CONNECT</b></td><td>-</td><td><i>not allowed</i></td><td>-</td>
		</tr>
		<tr>
			<td style="text-align: center;"><b>LOCK</b></td><td>-</td><td><i>not allowed</i></td><td>-</td>
		</tr>
		<tr>
			<td style="text-align: center;"><b>UNLOCK</b></td><td>-</td><td><i>not allowed</i></td><td>-</td>
		</tr>
		<tr>
			<td style="text-align: center;"><b>PROPPATCH</b></td><td>-</td><td><i>not allowed</i></td><td>-</td>
		</tr>
	</tbody>
</table>

For each method, a <span class="label label-warning">403 Forbidden</span> can be obtained if user doesn't provide the necessary credentials.

## Installation and configuration

The WebDAV interface is provided by StoRM GridHTTPs component. Therefore, if you want to install a WebDAV access point to your data you have to install StoRM GridHTTPs metapackage RPM (do not forget to satisfy all the [pre-requisites shown in the sys-admin guide][prereq] before):

	  yum install emi-storm-gridhttps-mp

To configure storm-gridhttps-server you need to fill the requested YAIM variables as described in the [basic][ghttp_basic_conf] and [advanced][ghttp_advanced_conf] StoRM GridHTTPs sys-admin configuration guides. A good explanation of the required YAIM variable is available in:

	 /opt/glite/yaim/examples/siteinfo/services/se_storm_gridhttps

The service uses (by default) ports 8443 and 8085, so open them on your firewall.

The service needs to be installed on a machine on which storm file system is mounted. If you need, you can install the StoRM GridHTTPs on differents hosts (that share the same data, e.g. hosts are GPFS clients) and use them as a pool (see [StoRM BackEnd configuration on sys-admin guide][be_conf]).
To start the service:

	  service storm-gridhttps-server start

## Using WebDAV

The StoRM GridHTTPs WebDAV server listens on two ports, one for the unencrypted HTTP connections and another for the SSL encrypted HTTP requests. Their default values are:

* HTTP: **8085**
* HTTP over SSL: **8443**

To access storage areas' data, users can use:

* a browser (if the storage area can be accessed by anonymous or via a valid personal certificate)
* cURLs (mandatory if you need to provide a valid x509 proxy credential)
* a third-party WebDAV client (Cyberduck, Firefox RestClient plugin, ...)

You can also develop a client on your own, for example by using the [Apache Jackrabbit API](http://jackrabbit.apache.org/).

### Access data via browser

Users can use browsers to easily read data of storage areas that are:

- HTTP readable (see the storage area configuration described in the [StoRM BackEnd configuration on sys-admin guide][be_conf])
- not HTTP readable, not associated to a particular VO, eventually access-filtered through users' DN.

Using a browser, users can navigate through the storage areas' directories and download/open files.

### CURLs

The best way to use the WebDAV service is using `curl` command. `curl` is a command line tool for transferring data with URL syntax (see [CURL website](http://curl.haxx.se/)). With `curl` we can do anonymous requests or provide our x509 credentials: personal certificate, plain Grid proxy, VOMS proxy. The following examples suppose that user has his/her personal certificate (*usercert.pem*) and key (*userkey.pem*) in `$HOME`/.globus directory, and his/her proxy in `$X509_USER_PROXY`.

#### Anonymous CURLs

Assuming that:

- *ghttps.hostname* is the hostname where your WebDAV service is available
- *free* is the name of a R/W from anonymous Storage Area

and knowing that unencrypted connections have 8085 as default port, the following table show various `curl`s, one for each HTTP/WebDAV method.

* **GET**

```
curl -v -X GET http://ghttps.hostname:8085/webdav/free
```

or

```
curl -v http://ghttps.hostname:8085/webdav/free
```

In case you are getting a file, you can specify a *range* header to get a part of it. The method GET can be omitted because by default a `curl` is an HTTP GET.

* **PUT**

To create the destination resource specifying its content via HTTP body:

```
curl -v -X PUT http://ghttps.hostname:8085/webdav/free/filename.txt --data-ascii "file content"
```

To create the destination resource by uploading a local existent file:

```
curl -v -T /local/path/to/filename.txt http://ghttps.hostname:8085/free/filename.txt
```

By default, a PUT request overwrites the destination resource if exists, so there is an implicit `'Overwrite: T'` header. If you want to be sure that destination resource won't overwritten, you have to add: `--header 'Overwrite: F'`

* **MKCOL**

```
curl -v -X MKCOL http://ghttps.hostname:8085/webdav/free/newdirectory
```

* **DELETE**

```
curl -v -X DELETE http://ghttps.hostname:8085/webdav/free/existent_resource
```

If *existent_resource* is a not empty directory, DELETE removes all the resources contained.

* **OPTIONS**

```
curl -v -X OPTIONS http://ghttps.hostname:8085/webdav/
```

There's no need to specify any storage area or resource with OPTIONS `curl`, the response is the same.

* **HEAD**

```
curl -v --head http://ghttps.hostname:8085/webdav/
```

There's no need to specify any storage area or resource with HEAD `curl`, the response is the same: a GET on the same URL without body.

* **PROPFIND**

```
curl -v -X PROPFIND http://ghttps.hostname:8085/webdav/free/path/to/resource
```

The *Depth* header with a value of "0", "1", or "infinity" (default is "Depth: infinity") is used to enable/disable recursion. HTTP request body is used to retrieve specific and/or more detailed information. It must be in XML format so it's necessary to add a `--header "Content-Type: text/xml"` header. Then, the body content is specified through the *data-ascii* option, that, first of all, contains the XML header:

`--data-ascii "<?xml version='1.0' encoding='utf-8'?>..."`

To obtain the list of names of all the resource properties complete it with:

```xml
<propfind xmlns='DAV:'><propname/></propfind>
```

To obtain the value of a single property complete it with:

```xml
<propfind xmlns='DAV:'><prop>property-name</prop></propfind>
```

To obtain all the property values complete it with:

```xml
<propfind xmlns='DAV:'><allprop/></propfind>
```

* **COPY**

```
curl -X COPY http://ghttps.hostname:8085/webdav/free/existent_resource --header "Destination: http://ghttps.hostname:8085/webdav/free/unexistent_resource"
```

The *Destination* header must be present. The COPY method on a collection without a *Depth* header must act as if a *Depth: infinity* header was included. *Depth* header can be 0 or *infinity*. A COPY with `--header "Depth: infinity"` copies all its internal member resources, recursively through all levels of the collection hierarchy. A COPY with `--header "Depth: 0"` only instructs that the collection and its properties but not resources identified by its internal member URIs, are to be copied. If destination resource exists, the copy has success only if user specifies `--header "Overwrite: T"`.

* **MOVE**

```
curl -X MOVE http://ghttps.hostname:8085/webdav/free/existent_resource --header "Destination: http://ghttps.hostname:8085/webdav/free/unexistent_resource"
```

The *Destination* header must be present. The MOVE method act like a COPY and a DELETE of the source. If the destination resource exists, the move has success only if user specifies `--header "Overwrite: T"`.

#### Using x509 credentials

If you need to present an x509 certificate to be authorized, you can add to your `curl` command the following options:

```
--cert path/to/usercert.pem --key path/to/userkey.pem --capath /path/to/the/trustdir
```

For example, assuming that:

* **A** is a storage area readable and writable only with an x509 certificate that has *O="INFN"*
* `$HOME`/.globus/usercert.pem and `$HOME`/.globus/userkey.pem are user's certificate and private key
* `$HOME`/.globus/usercert.pem has *O="INFN"*
* the trust directory with CA informations is in */etc/grid-security/certificates*

and knowing that encrypted connections has 8443 as default port, we can perform a `curl` like this:

```
curl --verbose -X GET https://gridhttps.hostname:8443/webdav/A/ --cert $HOME/.globus/usercert.pem --key $HOME/.globus/userkey.pem --capath /etc/grid-security/certificates
```

and retrieve the list of file/directories in the root directory of the storage area **A**.

#### Using proxy credentials

If you need to present an x509 proxy - plain or with VOMS extensions - to be authorized, you can add to your `curl` command the following options:

```
--cert path/to/yourproxy --capath /path/to/the/trustdir
```

For example, assuming that:

* **B** is a storage area readable and writable only with an x509 VOMS proxy for dteam VO
* `$X509_USER_PROXY` contains the path to the user's VOMS proxy
* the trust directory with CA informations is in */etc/grid-security/certificates*

and knowing that encrypted connections has 8443 as default port, we can perform a `curl` like this:

```
curl --verbose -X GET https://gridhttps.hostname:8443/webdav/B/ --cert $X509_USER_PROXY --capath /etc/grid-security/certificates
```

and retrieve the list of file/directories in the root directory of the storage area **B**.

### Firefox RESTClient plugin

There's a useful Firefox plugin, named [RESTClient](https://addons.mozilla.org/it/firefox/addon/restclient/), that can be used as a debugger for RESTful web services. RESTClient supports all HTTP methods [RFC2616](http://www.w3.org/Protocols/rfc2616/rfc2616.html) (HTTP/1.1) and [RFC2518](http://www.webdav.org/specs/rfc2518.html) (WebDAV). You can construct custom HTTP request (custom method with resources URI and HTTP request Body) to directly test requests against a server.

![RESTClient home screenshot]({{site.baseurl}}/assets/images/restclient.png "RESTClient home screenshot")

### Cyberduck

To connect to HTTP readable storage area you can use several clients. One of this is [Cyberduck](http://cyberduck.ch/). Cyberduck is an open source FTP and SFTP, WebDAV, Cloud Files, Google Docs, and Amazon S3 client for Mac OS X and Windows (as of version 4.0) licensed under the GPL. To configure it, add a new connection and insert:

* *server*: the FQDN of the gridhttps host
* *port*: the unencrypted HTTP port, default 8085
* select *anonymous login*
* specify /storage-area-name as *remote path*

This configuration is the same for lots of WebDAV clients, alternatives to Cyberduck.

<img src="{{site.baseurl}}/assets/images/cyberduck.png" alt="cyberduck" style="margin: 10px auto;"/>


[examples-section]: {{site.baseurl}}/documentation/examples/
[anonymous-read-example]: {{site.baseurl}}/documentation/examples/how-to/storage-area-configuration-examples/1.11.2/#sa-anonymous-r
[ghttp_basic_conf]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.6/#ghttpconf
[ghttp_advanced_conf]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.6/#ghttp_advconf
[be_conf]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.6/#beconf
[prereq]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.6/#installprereq
