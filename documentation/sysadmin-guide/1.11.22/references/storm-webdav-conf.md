---
layout: service-guide
title: StoRM WebDAV configuration reference
---

# StoRM WebDAV configuration reference

The storm-webdav service configuration lives in the `/etc/systemd/system/storm-webdav.service.d/storm-webdav.conf` file. 
Typically the configuration works out of the box, but changes are required, for instance, to
enable third-party transfer support.

StoRM can also be configured using (one or more) YAML files.

You can find an empty YAML configuration file in
`/etc/storm/webdav/config/application.yml` together with a `README.md` file in
the same directory that provides configuration instructions.

That configuration file is used to override settings in [the configuration file
embedded](https://github.com/italiangrid/storm-webdav/blob/master/src/main/resources/application.yml) in the storm webdav jar package, 
that you can consult to see what are the default settings.

## Storage area configuration

Each storage area is configured by placing a properties file in the `/etc/storm/webdav/sa.d` directory.
StoRM WebDAV will look for configuration in all files ending with `.properties` in this directory. If no configuration files are found, the service will not start.

### Properties

| _Name_                     | _Description_                                                                                                                | _Required_ | _Example_                               | _Default_ |
| -------------------------- | ---------------------------------------------------------------------------------------------------------------------------- | ---------- | --------------------------------------- | --------- |
| `name`                     | The name of the storage area                                                                                                 | True       | `name=sa`                               | N/A       |
| `rootPath`                 | Root path for the storage area. Files will be served from this path.                                                         | True       | `rootPath=/storage/sa`                  | N/A       |
| `accessPoints`             | Comma separated list of storage area access points.                                                                          | True       | `accessPoints=/sa`                      | N/A       |
| `vos`                      | Comma-separated list of VOMS VOs trusted in this storage area                                                                | False      | `vos=atlas`                             | N/A       |
| `orgs`                     | Comma-separated list of OAuth/OpenID Connect token issuers trusted in this storage area                                      | False      | `orgs=https://wlcg.cloud.cnaf.infn.it/` | N/A       |
| `authenticatedReadEnabled` | Enables read access to users authenticated with an X.509 certificate issued by a trusted CA (users without VOMS credentials) | False      | `authenticatedReadEnabled=true`         | false     |
| `anonymousReadEnabled`     | Enables read access to anonymous users.                                                                                      | False      | `anonymousReadEnabled=false`            | false     |
| `voMapEnabled`             | Enables VO map files for this storage area.                                                                                  | False      | `voMapEnabled=false`                    | true      |
| `voMapGrantsWriteAccess`   | VO map files grants read-only access to storage area files. Use this flag to grant write access to VO map files users        | False      | `voMapGrantsWriteAccess=true`           | false     |
| `orgsGrantReadPermission`  | Enables read access to storage area files to users authenticated using OAuth/OIDC                                            | False      | `orgsGrantReadPermission=false`         | true      |
| `orgsGrantWritePermission` | Enables write access to storage area files to users authenticated using OAuth/OIDC                                           | False      | `orgsGrantWritePermission=false`        | false     |
| `wlcgScopeAuthzEnabled`    | Enables scope-based authorization following the rules imposed by the WLCG JWT profile                                        | False      | `wlcgScopeAuthzEnabled=true`            | false     |
| `fineGrainedAuthzEnabled`  | Enables fine-grained authorization engine for the storage area                                                               | False      | `fineGrainedAuthzEnabled=true`          | false     |

## Authorization configuration

Up to version 1.2.0, StoRM WebDAV supported only flat authorization on
storage areas. This means that the same access privileges would be granted
to all the members of trusted VOMS Virtual Organization, or OpenID
Connect/OAuth token issuer.

This authorization model is good enough in many scientific computing
scenarios, and is the main one in use in WLCG computing.

Starting with version 1.3.1, StoRM WebDAV introduces a new
authorization engine that allows for finer-grained auhtorization.

### Flat authorization configuration

#### Anonymous access

To grant anonymous read access on the storage area, set
`anonymousReadEnabled=true`.

#### X.509 authenticated access

To grant anonymous read access to users authenticated with an X.509
certificate issued by a trusted CA (i.e., users without VOMS
credentials), set `authenticatedReadEnabled=true`.

#### VOMS authorization

Flat VOMS authorization is configured by including the VO that
needs to be trusted in the `vos` property of the storage area
configuration.

#### OAuth/OIDC authorization

Flat OAuth/OIDC authorization is enabled by including the token issuer
in the `orgs` property of the storage area configuration. 

Note that the token issuer must also be included in the list of trusted
issuer for the whole service in the `/etc/storm/webdav/config/application.yml` file.

By default, only read access is granted to the storage area. To enable
write access, set `orgsGrantWritePermission=true`.

### WLCG JWT scope-based authorization

Starting with version 1.3.1, StoRM WebDAV introduces support for WLCG
JWT profile scope-based authorization.

For this to work, add the trusted token issuer to the trusted ones
(see above) and set `wlcgScopeAuthzEnabled=true`.

By default, adding a token issuer to the list of the trusted ones for a
storage area will grant read access to the storage area. If you want to
enforce WLCG scope-based authorization completely, set
`orgsGrantReadPermission=false`.

### Fine-grained authorization

Starting with version 1.3.1, StoRM WebDAV introduces a new authorization
engine which allows for finer-grained authorization. It's possible to
define access policies for individual files/folders in the storage area,
and targeting specific authenticated users or group of users.

More flexibility means also more configuration complexity.
To enable fine-grained authorization on a storage area, set
`fineGrainedAuthzEnabled=true`. The authorization engine will be
activated **together with other authorization mechanism described
above**.

#### Fine-grained authorization policies

Fine-grained authorization policies are defined using spring application
configuration YAML files, i.e., typically by editing the
`/etc/storm/webdav/config/application.yml` file.

An example set of policies is given below:

```yaml
storm:
  authz:
    policies:
    - sa: wlcg
      actions:
      - list
      - read
      effect: permit
      description: Grant read access to any user
      principals:
      - type: anyone
    - sa: wlcg
      actions:
      - all
      effect: permit
      description: Grant read/write access to WLCG VOMS vo users
      principals:
      - type: vo
        params:
          vo: wlcg
```

Two policies are defined, for the `wlcg` storage area.
These policies are flat, i.e. cover the whole storage area.
The first policy allows read access to anonymous users.
The second one allows read/write access to any user authenticated with
VOMS credentials from the `wlcg` VO.

The policy above is basically equivalent to these settings in the
storage area configuration.

```properties
anonymousReadEnabled=true
vos=wlcg
```

Using the fine-grained authorization engine is not convenient if the
access model for the storage area is flat.

Policies are evaluated in the order in which they are defined, and the
fist applicable one is used to render the authorization decision.

A policy contains the following directives:

-   `sa`: The nome of the storage area to which the policy applies.

-   `actions`: a list of actions that the policy is authorizing. Possible
    values:

    -   `list`: the ability to list directory contents
    -   `read`: the ability to read files
    -   `write`: the ability to write files and create directories
    -   `delete`: the ability to delete files/directories
    -   `all`: a catchall for all other actions

-   `paths`: the list of paths, within the storage area, the policy
    applies to. If omitted, the policy will apply to all paths.

    To match a directory and all its contents, use a wildcard like the
    following: `/some/dir/**`

-   `effect`:

    -   `permit`: defines a permit policy
    -   `deny`: defines a deny policy

-   `description`: A textual description of the policy. This is a required
    field, and is mainly useful to help debugging authorization problems.

-   `principals`: a list of principals that the policy will apply to.
    Simple principal types are defined by a string value, while complex
    principal types require parameters.

    For example, the  `anyone` principal is a simple principal type,
    while the `jwt-group` principal is a complex type that requires
    two parameters: the token issuer URI, and the  group name.

  | _Principal_              | _Type_  | _Description_                                                                                      | _Parameters_                                          |
  | ------------------------ | ------- | -------------------------------------------------------------------------------------------------- | ----------------------------------------------------- |
  | `anyone`                 | simple  | will match any user (authenticated or not)                                                         | N/A                                                   |
  | `any-authenticated-user` | simple  | will match any authenticated user                                                                  | N/A                                                   |
  | `anonymous`              | simple  | will match any anonymous user                                                                      | N/A                                                   |
  | `vo`                     | complex | will match authenticated users presenting a valid VOMS    credential for a given VO.               | `vo`: the name of the VO                              |
  | `vo-map`                 | complex | will match authenticated mapped to a VO through a vomap configuration                              | `vo`: the name of the VO                              |
  | `fqan`                   | complex | will match authenticated users presenting a valid VOMS    credential that contains the given FQAN. | `fqan`: the VOMS fqan                                 |
  | `jwt-group`              | complex | will match authenticated users with valid JWT token and belonging to the given group.              | `iss`: the token issuer, `group`: the group name      |
  | `jwt-scope`              | complex | will match authenticated users with valid JWT token bearing the requested scope.                   | `iss`: the token issuer, `scope`: the scope           |
  | `jwt-issuer`             | complex | will match authenticated users with valid JWT token issued by the required token issuer            | `iss`: the token issuer                               |
  | `jwt-subject`            | complex | will match authenticated users with valid JWT token and presenting the requested subject claim.    | `iss`: the token issuer, `sub`: the subject           |
  | `x509-subject`           | complex | will match authenticated users presenting a valid x.509 credential and a given subject             | `subject`: the certificate subject, in RFC2253 format |


#### Examples

```yaml
storm:
  authz:
    policies:
    - sa: example
      actions:
      - list
      - read
      effect: permit
      paths:
      - /read-only/**
      description: Grant read access to any authenticated user to /read-only data
      principals:
      - type: anyone
    - sa: example
      actions:
      - all
      effect: permit
      description: Grant read/write access to the whole sa to /example/admins group members
      principals:
      - type: jwt-group
        params:
            iss: https://iam.example
            group: /example/admins
      - type: fqan
        params:
          fqan: /example/admins
```

The policies above grant:

-   read-only access to anyone on the `/read-only` folder, in the `example` storage area
-   all access to the `example` storage area to users in the group
    `/example/admins`, authenticated with VOMS or OpenID Connect

## VO mapfiles

When VO map files are enabled, users can authenticate to the StoRM webdav
service using the certificate in their browser and be granted VOMS attributes
if their subject is listed in one of the supported VO mapfile. You can
configure whether users listed in VO map files will be granted read-only or
write permissions in the storage area configuration in the
`/etc/storm/webdav/sa.d` directory.

This mechanism is very similar to the traditional Gridmap file but is just used
to know whether a given user is registered as a member in a VOMS managed VO and
not to map his/her certificate subject to a local unix account.

### How to enable VO map files

VO map files support is disabled by default in StoRM WebDAV.

Set `STORM_WEBDAV_VO_MAP_FILES_ENABLE=true` in 

`/etc/systemd/system/storm-webdav.service.d/storm-webdav.conf` to enable VO map file support.

### VO map files format and location

A VO map file is a csv file listing a certificate subject, issuer and email for each line.
It can be easily generated for a given VO using the `voms-admin` command line utility.
VO map files by default live in the `/etc/storm/webdav/vo-mapfiles.d` directory.

For each VO, a file named:

`VONAME.vomap`

is put in the `/etc/storm/webdav/vo-mapfiles.d` directory. 

### VO Map file examples

The file `/etc/storm/webdav/vo-mapfiles.d/test.vomap` with the following content:

```bat
/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Andrea Ceccanti,/C=IT/O=INFN/CN=INFN CA,andrea.ceccanti@cnaf.infn.it
/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Enrico Vianello,/C=IT/O=INFN/CN=INFN CA,enrico.vianello@cnaf.infn.it
```

will grant the `test` VO membership to clients authenticated with the above subjects.

To generate a VO mapfile for the `cms` VO, you could run the following command

```bash
voms-admin --host voms.cern.ch --vo cms list-users > /etc/storm/webdav/vo-mapfiles.d/cms.vomap
```

## Custom application.yml configuration

The `application.yml` file can be used to override settings embedded in the
storm-webdav configuration.

It's mainly used to:

- configure the list of trusted OAuth token issuers
- configure the list of trusted OpenID Connect providers (since version 1.3.0)
- configure fine-grained authorization policies (since version 1.3.0)

### Trusted OAuth connect token issuers configuration

In order to enable an OAuth token issuer in the context of a storage area, such
issuer must be included in the list of trusted issuer specified in this file
**and** in the list of trusted organizations for the storage area in the storage area
properties in /etc/storm/webdav/sa.d.

#### OAuth authorization example configuration

To trust the `super-provider.example` OAuth/OpenID Connect provider for
the storage area `example`, such provider must be listed among the trusted
token issuers in `/etc/storm/webdav/config/application.yml:`

```yaml
oauth:
  issuers:
    - name: super-provider
      issuer: https://super-provider.example/
```

**And** in the storage area configuration `/etc/storm/sa.d/example.properties`:

```properties
name=example
rootPath=/storage/example
accessPoints=/example
vos=example
orgs=https://super-provider.example/
```

### OpenID connect authentication

Starting with version 1.3.0, StoRM WebDAV supports OpenID Connect
authentication on its storage area browser application (i.e., the thing you get
when you point your browser to a StoRM WebDAV instance and that you use to
browse the storage area contents).

To enable OpenID Connect authentication you need:

- a trusted OpenID Connect provider, e.g. INDIGO IAM for WLCG
- a client registered in such provider with the `authorization_code` grant type
  enabled; instructions on how to register a client are typically provided by
  the OpenID provider

OpenID Connect authentication must be explicitly enabled in
`/etc/storm-webdav/config/application.yml`, as follows:

```yaml
oauth:
  enable-oidc: true
```

StoRM WebDAV leverages the [OpenID Connect support provider by Spring security
libraries][spring-oidc-support]. Below an example is given for the WLCG IAM
OpenID Connect provider:

```yaml
spring:
  security:
    oauth2:
      client:
        provider:
          wlcg:
            issuer-uri: https://wlcg.cloud.cnaf.infn.it/
        registration:
          wlcg:
            provider: wlcg
            client-name: WLCG IAM
            client-id: PUT_YOUR_CLIENT_ID_HERE
            client-secret: PUT_YOUR_CLIENT_SECRET_HERE
            scope:
              - openid
              - profile
              - wlcg.groups
```

### Fine-grained authorization

Starting with version 1.3.0, StoRM WebDAV provides support for fine-grained authorization on storage areas.

Before 1.3.0, authorization was coarse-grained, i.e. read and write privileges on a storage area were granted to a VOMS VO or OAuth trusted issuer, and those privileges were applied to the whole storage area, i.e. it was not possible to have different authorization policies on folders or on specific paths, or to grant different privileges to specific groups or subjects.

Fine-grained authorization can be enabled on a storage area as follows:

```properties
name=example
rootPath=/storage/example
...
fineGrainedAuthzEnabled=true
```

Then authorization policies must be included in the `/etc/storm/webdav/config/application.yml` configuration. Example policies are given below:

```yaml
storm:
  authz:
    policies:
    - sa: example
      actions:
      - list
      - read
      effect: permit
      description: Grant read access to users in the /example/read group
      principals:
      - type: oauth-group
        params:
          iss: https://vo.example/
          group: /example/read
    - sa: example
      actions:
      - all
      effect: permit
      description: Grant write access to /example/write-area to /example/write users
      principals:
      - type: oauth-group
        params:
          iss: https://vo.example/
          group: /example/write
      paths:
       - /write-area/**
```

The first policy grants read access to the whole `example` storage area to members
of group `/example/read`, as asserted by the trusted OAuth token issuer `https://vo.example`.

The second policy grants all privileges to members of group `/example/write` to the path `/write-area`,
and all its subpaths, as defined in the context of the storage area, i.e. below any of the access points defined for that storage
area. So, assuming the the `example` storage area is configured to have a single access point, `/example`, the policy will
apply to `/example/write-area` and all its subpath.

#### Fine-grained authorization policies

A fine-grained authorization policy defines access rules to directory and files in the context of a storage area.
In detail, an access policy defines access rules that regulate which actions on which resources are allowed for a set of principals.
Actions, in this context, are read, write, list and delete operations on files and directories. Resources are files and directories. Principals are authenticated entities (i.e., users or agents).

Policies are defined in the context of a storage area, and are applied in order following a first-applicable principle.
This means that the first policy found in the list that matches an incoming request will be applied when rendering an authorization
decision.

A policy matches an incoming request when the resources, actions and principal in the policy match the target resource action
and principal in the request.

An example, annotated policy is given below:

```yaml
  policies:
    // The storage area that will apply to this policy
    - sa: example
      actions:
      // An array of actions regulated by this policy, possible values are
      // read, write, delete, list, all
      - list
      - read
      // The policy effect. Possible values are 'permit', 'deny'
      effect: permit
      // A policy description.
      description: Grant read access to selected vo users
      // List of paths that this policy will apply to.
      // Paths are specific to the storage area, i.e. are relative to the
      // storage area access points. 
      // Example:
      // In a storage area 'example', that as the '/example/' access point,
      // in order to match the '/example/read-only' path and its subpaths, the
      // path expression included in the policy would be '/read-only/**'
      // '**' matches any file or directory, at any level of nesting, of a given path
      // '*' matches any file or directory contained in a given path
      // The matching rules follow the Spring AntPath matcher conventions, see:
      // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/util/AntPathMatcher.html
      paths:
      - /read-only/**
      // The list of principals this policy will apply to. Below examples of principals 
      // are given
      principals:
      // Anymous principal, will match users not authenticated
      - type: anonymous 
      // Any authenticated user, will match users that have been authenticated
      // (with any supported mechanism, i.e. X.509, OAuth or OIDC)
      - type: any-authenticated-user
      // anyone, will match any principal
      - type: anyone
      // a VOMS fully qualified attribute name
      - type: fqan
        params:
          fqan: /test.vo/Role=admin
      // A VOMS vo
      - type: vo
        params:
          vo: dteam
      // An OAuth/OpenID Connect group, as asserted by a trusted OAuth/OpenID Connect token issuer
      - type: oauth-group
        params:
          iss: https://wlcg.cloud.cnaf.infn.it/
          group: /wlcg/xfers
      // An OAuth/OpenID Connect scope, as asserted by a trusted OAuth/OpenID Connect token issuer
      - type: oauth-scope
        params:
          iss: https://wlcg.cloud.cnaf.infn.it/
          scope: /storage.read:/
      // An OAuth/OpenID Connect subject, as asserted by a trusted OAuth/OpenID Connect token issuer
      - type: oidc-subject
        params:
          iss: https://wlcg.cloud.cnaf.infn.it/
          sub: a1b98335-9649-4fb0-961d-5a49ce108d49
```

## Threadpool sizes

The size of the thread pool used to serve incoming requests and
third-party-copy requests can be set with the following variables:

```yaml
storm:
  connector:
    max-connections: 300
    max-queue-size: 900
  tpc:
    max-connections: 200
    max-connections-per-route: 150
    progress-report-thread-pool-size: (# of cores of your machine)
```

## Conscrypt

Conscrypt improves TLS performance and can be enabled as follows:

```yaml
storm:
  tpc:
    use-conscrypt: true
  tls:
    use-conscrypt: true
    enable-http2: true
```

## Configuring support for third-party transfers

Starting with version 1.1.0, StoRM WebDAV supports third party copy transfers. 

Third party transfer support is enabled and active by default, but will work as
expected only when properly configured.

### Hostname configuration

Support for third-party transfers is implemented by supporting a COPY method
request where the Source or Destination header points to a remote resource.

In order to tell apart remote resources from local ones, StoRM webdav must be
configured accordingly.

This is done via the `STORM_WEBDAV_HOSTNAME_0`, `STORM_WEBDAV_HOSTNAME_1`, ...,
environment variables, which allow to define for which hostnames (and aliases)
the service is serving requests.

Example:

```bat
STORM_WEBDAV_HOSTNAME_0="ds-808.cr.cnaf.infn.it"
STORM_WEBDAV_HOSTNAME_1="gridhttps-storm-atlas.cr.cnaf.infn.it"
```

### OAuth authorization server configuration

The OAuth authorization server can be used by clients such as FTS to obtain an
OAuth access token that grants the same privileges as a VOMS credential. This
mechanism is currently used to implement a form of delegated authorization in
support of third-party transfers. 

The tokens issued by the StoRM WebDAV OAuth authorization servers will only be
accepted by the StoRM WebDAV service instances. In a replicated setup, just 
ensure that the OAuth server configuration is consistent across the replicas.

Below is an example of configuration:

```bat
# THe issuer linked to the issued tokens. This is typically the 'https://' followed
# by the storm webdav instance main alias.
STORM_WEBDAV_AUTHZ_SERVER_ISSUER="https://gridhttps-storm.atlas.cr.cnaf.infn.it"

# The secret used to sign the tokens. 
STORM_WEBDAV_AUTHZ_SERVER_SECRET="supersupersupersecret"
```

## Configuring support for externalized sessions 

Starting with version 1.4.0, StoRM WebDAV supports storing HTTP session 
information in an external [redis][redis] server.

This can be useful when deploying multiple replicas of the StoRM WebDAV
service.

Externalized session support can be enabled by adding the following
configuration to the `/etc/storm/webdav/config/application.yaml` file:

```yaml
spring:
  session:
    store-type: redis

  redis:
    host: redis.host.example
    port: 6379

```

For other redis connection configuration options, see the [Spring boot reference guide][spring-boot-reference].

## Memory

You should give a reasonable amount of memory to StoRM WebDAV to do its work.
The amount depends on the number of concurrent requests that the server needs
to handle.

A good starting point is giving the server 2G of heap memory, by setting the
following env variable:

```bat
STORM_WEBDAV_JVM_OPTS=-Xms2048m -Xmx2048m
```

In general, allowing for `256Mb + (# threads * 6Mb)` should give StoRM WebDAV
enough memory to do its work.

## Use `/dev/urandom` for random number generation

Using `/dev/random` can lead to the service being blocked if not enough entropy
is available in the system.

To avoid this scenario, use `/dev/urandom`, by setting the JVM options as
follows:

```bat
STORM_WEBDAV_JVM_OPTS=-Xms2048m -Xmx2048m -Djava.security.egd=file:/dev/./urandom
```



[spring-oidc-support]: https://docs.spring.io/spring-boot/docs/2.1.12.RELEASE/reference/html/boot-features-security.html#boot-features-security-oauth2-client
[redis]: https://redis.io/
[spring-boot-reference]: https://docs.spring.io/spring-boot/docs/2.2.9.RELEASE/reference/htmlsingle/#data-properties
[storm-puppet]: https://github.com/italiangrid/storm-puppet-module