---
layout: default
title: "StoRM BackEnd v. 1.11.20 release notes"
release_date: "12.04.2021"
features:
  - id: STOR-1357
    title: StoRM Backend and native libs should run with Java 11
---

## StoRM Backend v. 1.11.20

Released on **{{ page.release_date }}** with [StoRM v. 1.11.20][release-notes].
<span style="float: right; margin-left: 8px;" class="label label-info">centos7</span>

### Description

This release requires and install Java 11.

### Enhancements

{% include list-features.liquid %}

### Installation and configuration

If you're upgrading, you can update and restart StoRM backend service as follow:

```
yum update storm-backend-server
```

The update will upgrade also the native libraries as a dependency. <br/>
After the successful upgrade, the service will be restarted and you should have both Java 1.8 and Java 11 installed, but
**Java 11 must be set as your default runtime**. None of the latest StoRM Java components still need Java 1.8 so it can be safely removed as follows:

```
yum remove java-1.8.0-openjdk java-1.8.0-openjdk-headless
```

You shouldn't see storm backend component within the involved dependencies. <br/>
If you cannot remove it, you can also set java 11 as default runtime JDK by running:

```
update-alternatives --config java
```

and select the proper Java 11 option. <br/>

Now, don't forget to restart service:

```
systemctl restart storm-backend-server
```

In case you have any kind of questions or problems please contact us.

In case of a clean installation please read the [System Administrator Guide][storm-sysadmin-guide].

<hr/>

### Known issue \[Updated on 30.04.2021\]

After the update from StoRM v1.11.19 to StoRM v1.11.20, if JVM and database are not on the same timezone, the Backend's communication with MariaDB could start failing with the following error:

```
Caused by: java.sql.SQLException: The server time zone value 'CEST' is unrecognized or represents more than one time zone. You must configure either the server or JDBC driver (via the serverTimezone configuration property) to use a more specifc time zone value if you want to utilize time zone support.
```

This bug is tracked at [STOR-1397](https://issues.infn.it/jira/browse/STOR-1397).

The possible solutions to avoid this problem are:
 * downgrade StoRM Backend to v1.11.19 (**recommended**)
 * apply a workaround within MariaDB
 * install StoRM Backend v1.11.21 beta

Read more [here][known-issue-post] 

<hr/>

Read more at:
* the [Quick deploy on CentOS7][quickdeploy] guide;
* [StoRM Puppet module forge page][stormpuppetmodule];
* [StoRM Puppet module generated documentation][stormpuppetmoduledoc] forge page.

[stormpuppetmodule]: https://forge.puppet.com/cnafsd/storm
[stormpuppetmoduledoc]: https://italiangrid.github.io/storm-puppet-module/
[quickdeploy]: {{site.baseurl}}/documentation/documentation/sysadmin-guide/1.11.20/quick-deployments/centos7/index.html

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.20.html
[upgrading]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.20/upgrading
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.20

[known-issue-post]: {{site.baseurl}}/2021/04/30/storm-v1.11.20-known-issue.html