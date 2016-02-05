---
layout: default
title: StoRM Documentation Examples
---

# How-To

A list of useful configuration hints.

{% for doc in site.data.howto  %}

#### {{forloop.index}}. [{{doc.title}}]({{site.baseurl}}{{doc.relative-link}})

{{doc.description}}

{% endfor %}
