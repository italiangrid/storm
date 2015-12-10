---
layout: howto
title: StoRM Storage Resource Manager - How-to upgrade StoRM from EMI2 version to EMI3
---

### How-to upgrade StoRM from EMI2 version to EMI3

In order to upgrade your current version of StoRM from EMI1 or EMI2 to EMI3 you
need to install the EMI3 repos.

Depending on your platform, download and install the right EMI release package,
as described in the [Repository settings][repo-settings] section.

Then execute:

```bash
$ yum clean all
$ yum -y update
```

{% assign label_caption="Important" %}
{% include open_note.liquid %}
> If you are upgrading a StoRM installation that runs on top of GPFS, be sure to install the `storm-native-libs-gpfs` package after the update has completed, issuing the command ```yum install storm-native-libs-gpfs```

If you are also upgrading the StoRM GridHTTPs server component, after the
installation you can remove tomcat because it's no more used by EMI3 GridHTTPs.
Of course, you can do this if you are not using tomcat for other purposes:

```bash
$ yum remove tomcat5
```

[repo-settings]: {{site.baseurl}}/documentation/sysadmin-guide/{{site.sysadmin_guide_version}}/index.html#repository-settings
