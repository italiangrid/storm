---
layout: default
title: StoRM v. 1.11.2
version: 1.11.2
description: This StoRM release provides the bug fixes for the StoRM Backend and StoRM native-libs.
rfcs:
    - id: STOR-259
      type: bug
      title: StoRM native libs call to change_group_ownership now correctly forwards exceptions to the parent java process
    - id: STOR-250
      type: bug
      title: StoRM GPFS get_fileset_quota_info now doesn't leak more file descriptors
    - id: STOR-235
      type: issue
      title: YAIM StoRM does not provide a way to configure the XML-RPC service port
    - id: STOR-257
      type: issue
      title: Unable to change STORM_USER via yaim setup of StoRM
    - id: STOR-103
      type: issue
      title: StoRM publishes a wrong GLUE2EndpointServingState in one of the two GLUE2Endpoint
---

# StoRM v. {{ page.version }}

{{ page.description }}

### Bug fixes 

{% assign filter="bug" %}
{% include list-rfcs.md %}

### Enhancements

None.

### Other news

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [Documentation]({{ site.baseurl }}/documentation/1.11.2/index.html) section.

### Known issues

{% assign filter="issue" %}
{% include list-rfcs.md %}

All of these issues will be fixed soon with next release.