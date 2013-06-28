---
layout: default
title: StoRM v. 1.11.1 release notes
version: 1.11.1
description: This StoRM release provides the bug fixes and improvements for the StoRM services.
rfcs:
    - id: STOR-172
      type: bug
      title: StoRM now correctly publishes information about storage area sizes in the information system.
    - id: STOR-148
      type: bug
      title: StoRM now leverages quota limits information gathered from the underlying GPFS filesystem to compute a storage area size
    - id: STOR-10
      type: bug
      title: StoRM now gets quota information directly from GPFS filesystem
    - id: STOR-130
      type: bug
      title: StoRM GridHTTPs server is now correctly registered to start at system boot.
    - id: STOR-117
      type: bug
      title: Duplicate prepare-to-get calls on a SURL are now correctly handled
    - id: STOR-113
      type: bug
      title: The StoRM YAIM module does not try to configure permissions on existing configured storage areas. It is assumed (and documented) that the correct permissions are set by the system administrator before running YAIM.
    - id: STOR-109
      type: bug
      title: The Java JDK dependency has been fixed so that all StoRM packages explicitly requires OpenJDK.
    - id: STOR-230
      type: feature
      title: The StoRM WebDAV PROPFIND implementation performance has been improved.
---

# StoRM v. {{ page.version }}

{{ page.description }}

### Bug fixes 

{% assign filter="bug" %}
{% include list-rfcs.md %}

### Enhancements

{% assign filter="feature" %}
{% include list-rfcs.md %}

### Other news

* The StoRM web site is now hosted on [Github]({{ site.website_base_url }}).

* All the StoRM code has been migrated to Github. StoRM repositories can be found
[here]({{ site.repo_base_url }}).

* The StorRM PT now uses [JIRA]({{ site.issue_base_url }}/STOR) for issue, development and release progress tracking.

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [Documentation]({{ site.baseurl }}/documentation/1.11.1/index.html) section.

### Known issues

None at the moment

