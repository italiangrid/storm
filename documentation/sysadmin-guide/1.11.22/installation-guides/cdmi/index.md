---
layout: service-guide
title: CDMI StoRM installation and configuration guide
navigation:
  - link: documentation/sysadmin-guide/1.11.22/index.html
    label: Index
  - link: documentation/sysadmin-guide/1.11.22/installation-guides/index.html
    label: Installation and Configuration guides
  - link: documentation/sysadmin-guide/1.11.22/installation-guides/cdmi/index.html
    label: CDMI StoRM
---

# CDMI StoRM installation and configuration guide

CDMI StoRM is a plugin for the [INDIGO DataCloud CDMI server][indigo-cdmi-server] in order to support StoRM
as storage back-end and allow users to negotiate the Quality of Service of stored data through CDMI.

In short, through the CDMI server users/admins can:

- read the status of a resource (access latency, number of replicas, size, etc..);
- schedule, if allowed, a change of its related Quality of Service.

Currently, in StoRM use case, changing the QoS of a resource means recalling a file from tape.

{% assign image_src="cdmi-storm.png" %}
{% assign image_width="30%" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 5" %}
{% assign label_description="CDMI StoRM architecture overview." %}
{% include documentation/label.html %}

Refer to the [CDMI server user guide][indigo-cdmi-server-user-guide] for all the details about INDIGO DataCloud QoS management.

## Installation and Configuration <a name="cdmistorminstall">&nbsp;</a>

You can found CDMI StoRM details about installation and configuration [here][indigo-cdmi-deployment-guide].


[indigo-cdmi-spi]: https://github.com/indigo-dc/cdmi-spi
[indigo-cdmi-server]: https://github.com/indigo-dc/cdmi
[indigo-cdmi-server-user-guide]: https://indigo-dc.gitbooks.io/cdmi-qos/content/doc/api_walkthrough.html
[indigo-cdmi-deployment-guide]: https://github.com/italiangrid/cdmi-storm/blob/master/doc/admin.md