---
layout: service-guide
title: StoRM Backend installation and configuration guide
navigation:
  - link: documentation/sysadmin-guide/1.11.21/index.html
    label: Index
  - link: documentation/sysadmin-guide/1.11.21/installation-guides/index.html
    label: Installation and Configuration guides
  - link: documentation/sysadmin-guide/1.11.21/installation-guides/backend/index.html
    label: StoRM Backend
---

# StoRM Backend installation and configuration guide

## Introduction

The StoRM Backend service is the core of the StoRM services. It executes all SRM functionalities and takes care of file and space metadata management. It also enforces authorization permissions on files and interacts with external Grid services.

{% include_relative install-service.md %}
{% include_relative service-configuration.md %}
{% include_relative configure-with-yaim.md %}
{% include_relative configure-with-puppet.md %}
{% include_relative logging.md %}






