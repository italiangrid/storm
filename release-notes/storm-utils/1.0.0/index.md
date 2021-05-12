---
layout: default
title: "StoRM Utils v. 10.0 release notes"
release_date: "12.05.2021"

---

## StoRM Utils v. 1.0.0

Released on **{{ page.release_date }}** with [StoRM v. 1.11.21][release-notes].
<span style="float: right; margin-left: 8px;" class="label label-info">centos7</span>

### Description

This first version of storm-utils contains two scripts:

* `storm-get-space-aliases`
* `storm-update-used-space`

#### Usages

The command `storm-get-space-aliases` retrieves the list of storage areas' space aliases of your StoRM instance.
Usage:

```
storm-get-space-aliases.sh [-u <db-username>] [-p <db-password>]
```

Example:

```
$ sh storm-get-space-tokens.sh -u storm -p storm
DTEAM_TOKEN
TAPE_TOKEN
TESTVO_TOKEN
```

This command doesn't change any information stored on database.

The command `storm-update-used-space` is used to **update** the used space information related to a specific space-alias.
Usage:

```
storm-update-used-space.sh [-u <db-username>] [-p <db-password>] [-a <spacetoken-alias>] [-s <used-space-size>]
```

The `used-space-size` must be expressed in **bytes**.

Example:

```bash
$ sh storm-update-used-space.sh -u storm -p storm -a TESTVO_TOKEN -s 52080
Getting space info for 'TESTVO_TOKEN' ...
  TOTAL_SIZE=4000000000, FREE_SIZE=3999957920, USED_SIZE=42080
Setting new free size as 3999947920 and new used space as 52080 ...
  Update query exited with 0
```


### Installation and configuration

You can install this scripts as follow:

```
yum install storm-utils
```

In case of a clean installation please read the [System Administrator Guide][storm-sysadmin-guide].

Read more at:
* the [Quick deploy on CentOS7][quickdeploy] guide;
* [StoRM Puppet module forge page][stormpuppetmodule];
* [StoRM Puppet module generated documentation][stormpuppetmoduledoc] forge page.

[stormpuppetmodule]: https://forge.puppet.com/cnafsd/storm
[stormpuppetmoduledoc]: https://italiangrid.github.io/storm-puppet-module/
[quickdeploy]: {{site.baseurl}}/documentation/documentation/sysadmin-guide/1.11.21/quick-deployments/centos7/index.html

[release-notes]: {{site.baseurl}}/release-notes/StoRM-v1.11.21.html
[upgrading]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.21/upgrading
[storm-sysadmin-guide]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.21
