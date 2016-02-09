---
layout: default
title: StoRM GridHTTPs configuration guide
---

# StoRM GridHTTPs configuration guide

{% assign label_caption="Important" %}
{% include open_note.liquid %}
>From [StoRM v1.11.7][storm-1117] the GridHTTPs server is deprecated. Install and configure the storm-webdav service instead.

The EMI3 GridHTTPs is the component responsible to provide:

- HTTP(s) file-transfer capabilities: it's possible to GET/PUT data via HTTP protocol but this is authorized only if a valid SRM prepare-to-get or SRM prepare-to-put has been successfully done before;
- a WebDAV interface to the StoRM endpoint that conceals the details of the SRM protocol and allows users to mount remote Grid storage as a volume on their own desktops;
- a mapping-service used by Backend that convert a real file path to a valid file-transfer URL.

## YAIM configuration variables

All the GridHTTPs specific variables can be found in the following file:

```bash
  /opt/glite/yaim/examples/siteinfo/services/se_storm_gridhttps
```

Please copy it in your CONFDIR/services directory and edit as your needs.
You have to set at least these mandatory variables:

- STORM\_BACKEND\_HOST

and check the other variables to evaluate if you like the default set or if you want to change those settings.
Table 1 summaries YAIM variables for StoRM GridHTTPs component.

|   Var. Name                           |   Description |
|:--------------------------------------|:--------------|
|STORM\_BACKEND\_HOST                   |Host name of the StoRM Backend server. **Mandatory**.
|STORM\_BACKEND\_REST\_SERVICES\_PORT   |StoRM Backend server REST port. Optional variable. Default value: **9998**
|STORM\_BE\_XMLRPC\_PORT                |StoRM Backend server XMLRPC port. Optional variable. Default value: **8080**
|STORM\_FRONTEND\_PORT                  |StoRM Frontend server SRM port. Optional variable. Default value: **8444**
|STORM\_GRIDHTTPS\_CERT\_DIR            |Host certificate folder for SSL connector. Optional variable. <br/>Default value: **/etc/grid-security/STORM\_GRIDHTTPS\_USER**
|STORM\_GRIDHTTPS\_HTTP\_ENABLED        |Flag that enables/disables http connections. Optional variable. Available values: true, false. <br/>Default value: **true**
|STORM\_GRIDHTTPS\_HTTP\_PORT           |StoRM GridHTTPs http port. Optional variable. <br/>Default value: **8085**
|STORM\_GRIDHTTPS\_HTTPS\_PORT          |StoRM GridHTTPs https port Optional variable. <br/>Default value: **8443**
|STORM\_GRIDHTTPS\_USER                 |StoRM GridHTTPs service user. Optional variable. <br/>Default value: **gridhttps**
|STORM\_SRM\_ENDPOINT                   |StoRM SRM EndPoint. Optional variable. <br/>Default value: **STORM\_BACKEND\_HOST:<br/>STORM\_FRONTEND\_PORT**
|STORM\_USER                            |StoRM Backend service user. Optional variable. <br/>Default value: **storm**
|X509\_CERT\_DIR                        |The location of certificates truststore. Optional variable. <br/>Default value: **/etc/grid-security/certificates**
|X509\_HOST\_CERT                       |Host certificate location. <br/>Default value: **/etc/grid-security/hostcert.pem**
|X509\_HOST\_KEY                        |Host certificate key location. Optional variable. <br/>Default value: **/etc/grid-security/hostkey.pem**
|CANL\_UPDATE\_INTERVAL                 |Canl truststore update time interval expressed in milliseconds. Optional variable. Default value: **600000** (1 minute)

{% assign label_title="Table 1" %}
{% assign label_id="Table1" %}
{% assign label_description="Specific StoRM GridHTTPs Variables." %}
{% include documentation/label.html %}

##Advanced service configuration

The GridHTTPs component relies on a single configuration file that contains all the configurable parameters. This file is:

    /etc/storm/gridhttps-server/server.ini

containing a list of:

    key = value

pairs that can be used to configure the GridHTTPs server.
In case a parameter is modified, **the GridHTTPs service has to be restarted** in order to read the new value.

###server.ini

EMI3 StoRM GridHTTPs server no longer needs Tomcat, cause it is now a web component residing in an embedded Jetty server. About Jetty server and its connectors configuration you can manage the following variables:

|   Var. name           |   Description             |
|:----------------------|:--------------------------|
|```http.enabled```     |Flag to enable anonymous webdav and file-transfer connections. Available values: true, false. Default value: **true**
|```http.port```        |Gridhttps http port for anonymous webdav and file-transfer connections. Default value: **8085**
|```https.port```       |Gridhttps https port for secure webdav and file-transfer connections. Default value: **8443**
|```mapper.servlet.port```  |Mapping-service http port.<br/>Default value: **8086**
|```max.active.threads```   |Maximum number of active threads for server's requests.<br/>Default value: **150**
|```max.queued.threads```   |Maximum number of queued threads for server's requests.<br/>Default value: **300**
|```x509.host-certificate```    |x509 host certificate for SSL connector.<br/>Default value: **/etc/grid-security/gridhttps/hostcert.pem**
|```x509.host-key```    |x509 host key for SSL connector.<br/>Default value: **/etc/grid-security/gridhttps/hostkey.pem**
|```x509.truststore.directory```    |Truststore location.<br/>Default value: **/etc/grid-security/certificates**
|```x509.truststore.refresh-interval``` |Canl truststore update time interval expressed in milliseconds.<br/>Default value: **600000** (1 minute)

<br/>
GridHTTPs' log file is configurable:

|   Var. name           |   Description             |
|:----------------------|:--------------------------|
|```log.configuration-file```   |GridHTTPs logging configuration file.<br/>Default value: **/etc/storm/gridhttps-server/logback.xml**

<br/>
GridHTTPs interacts with StoRM Backend to configure itself in bootstrap phase, to check user's authorization access to resources, to perform SRM operation, to set checksum value on a file, etc. So it needs to know information about BE location and ports:

|   Var. name           |   Description             |
|:----------------------|:--------------------------|
|```backend.hostname```     |StoRM Backend server full hostname. <br/>**Mandatory**
|```backend.authorization-service.port```   |StoRM Backend server REST port.<br/>Default value: **9998**
|```backend.srm-service.port``` |StoRM Backend server XMLRPC port.<br/>Default value: **8080**
|```backend.xmlrpc.token``` |Token used for communicating with the backend service. Mandatory, has no default

<br/>
GridHTTPs works with SURLs so it needs to know a valid SRM endpoint:

|   Var. name           |   Description             |
|:----------------------|:--------------------------|
|```srm.endpoint```         |StoRM SRM EndPoint.<br/>Default value: **$STORM\_BACKEND\_HOSTNAME:8444**

<br/>
GridHTTPs manage file transfers and file creation. So it computes checksum during transfers. The computed checksum type is *adler32*. This capability can be disabled.

|   Var. name           |   Description             |
|:----------------------|:--------------------------|
|```compute-checksum```     |If compute-checksum is true, for every file created a valid adler32 checksum value is computed. Available values: true, false.<br/>Default value: **true**

##GridHTTPs Logging

GridHTTPs' log files are located in */var/log/storm/* directory. They are the followings:

- **storm-gridhttps-server.log** For managed requests
This is the main log file of StoRM GridHTTPs. All the information about the WebDAV, HTTP file-transfer and mapping requests, error or warning are logged here depending on the log level. At the INFO level, the GridHTTPs logs, for each operation, who have request the operation (DN and FQANs if not anonymous), on which file(s) (SURLs) and the operation result. At DEBUG level, much more information are printed regarding the status of many StoRM internal component, depending on the request type. DEBUG level has to be used carefully only for troubleshooting operation. If ERROR or FATAL level are used, the only event logged in the file are due to error condition.

- **storm-backend.stdout**
This file contains the standard out of the GridHTTPs process. Usually it does not contains any useful information.

- **storm-backend.stderr**
This file contains the event logged as ERROR or FATAL conditions. This event logs are presents both in the *storm-gridhttps-server.log* file and here.

The logging level of these files can be specified editing the configuration file **logback.xml** located in */etc/storm/gridhttps-server/* directory modifying the *level* value of:

```xml
  <logger name="it.grid.storm" level="INFO">
    <appender-ref ref="PROCESS" />
  </logger>
```

The supported logging levels are: FATAL, ERROR, WARN, INFO, DEBUG and TRACE.

The suggest logging level for production endpoint is INFO.
In case the log level is modified, there's no need to restart GridHTTPs service.

[storm-1117]: {{site.baseurl}}/release-notes/StoRM-v1.11.7.html
