---
layout: default
title: "StoRM WebDAV v. 1.1.0 release notes"
release_date: "28.02.2019"
features:
  - id: STOR-1018
    title: Support for third-party copy in StoRM WebDAV service
---

## StoRM WebDAV v. 1.1.0

Released on **{{ page.release_date }}** as an update of [StoRM v. 1.11.15][release-notes].

### Description

This release introduces:

- support for third-party copy transfers implemented by extending the semantic
  of the WebDAV copy method;
- support for token-based authentication and authorization, by introducing an
  internal OAuth authorization server that can be used to issue tokens to
  client authenticated with VOMS credentials
- support for OpenID connect authentication and authorization on storage areas

More information can be found in the [StoRM WebDAV service installation and
configuration guide][dav-guide] and [here][tpc-guide].

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

Update the StoRM WebDAV package:

    yum update storm-webdav

#### Template configuration files update

StoRM WebDAV 1.1.0 introduces changes in the template configuration file:


    /etc/sysconfig/storm-webdav

and in the configuration files for the logging facilities:


    /etc/storm/webdav/logback.xml
    /etc/storm/webdav/logback-access.xml

The new files provided by the updated packages must be used, which will
show up as .rpmnew files (when there are local changes to the configuration),
i.e.:


    /etc/sysconfig/storm-webdav.rpmnew
    /etc/storm/webdav/logback.xml.rpmnew
    /etc/storm/webdav/logback-access.xml.rpmnew

We recommend that you backup your current configuration file:

  cp /etc/sysconfig/storm-webdav /etc/syconfig/storm-webav.bkp

And port the changes in such file to the new template:

  cp /etc/sysconfig/storm-webdav.rpmnew /etc/syconfig/storm-webav

#### Hostname configuration

Support for third-party transfers is implemented by supporting a COPY method
request where the Source or Destination header points to a remote resource.
In order to tell apart remote resources from local ones, StoRM webdav must be
configured accordingly.

This is done via the `STORM_WEBDAV_HOSTNAME_0`, `STORM_WEBDAV_HOSTNAME_1`, …,
environment variables in `/etc/sysconfig/storm-wedav`, which allow to define
for which hostnames (and aliases) the service is serving requests.

Example:

```
STORM_WEBDAV_HOSTNAME_0="storm.example"
STORM_WEBDAV_HOSTNAME_1="alias.for.storm.example"
```

#### OAuth authorization service configuration

To support delegation without proxy certificates, StoRM WebDAV introduces
token-based authorization via a local OAuth authorization server that can issue
authorization tokens to clients authenticated with VOMS proxies.

Instructions on how to configure properly the authorization server are given in
[this document][tpc-guide]. The default configuration should work out of the
box for non-replicated deployments, but be sure to

- change the secret used to sign the tokens, i.e. provide a sensible value
  (longer than 32 characters) for the `STORM_WEBDAV_AUTHZ_SERVER_SECRET`
  variable
- set `STORM_WEBDAV_REQUIRE_CLIENT_CERT=false` so that client certificate
  authentication is no longer required

in `/etc/sysconfig/storm-webdav`.

Once the above actions have been performed, you can restart the service with
the following command:

```
service storm-webdav restart
```

Check the the [StoRM WebDAV installation and configuration
guide][storm-webdav-guide] for detailed installation and configuration
information.

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.15.html
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15
[dav-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15/storm-webdav-guide.html
[tpc-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15/tpc.html
[tpc-technical]: https://twiki.cern.ch/twiki/bin/view/LCG/HttpTpcTechnical
[webdav-tpc-aliases]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.15#important2
