
{% assign rdata=site.data.releases[page.release].info %}
{% assign cdata=site.data.releases[page.release][page.package] %}

{% if cdata != null %}

## {{ cdata.name }} v. {{ cdata.version }}

Released on **{{rdata.date}}** with [StoRM v. {{rdata.version}}]({{site.baseurl}}{{site.releasenotes_path}}/StoRM-v{{rdata.version}}.html).

### Description

{{ cdata.description }}

{% if cdata.rfcs.size > 0 %}

### Bug fixes

<ul>
{% for rfc in cdata.rfcs %}
  <li>[<a href="{{ site.issue_base_url }}{{ rfc.id }}">{{ rfc.id }}</a>] - {{ rfc.title }}</li>
{% endfor %}
</ul>

{% endif %}

{% if cdata.features.size > 0 %}

### Improvements

<ul>
{% for rfc in cdata.features %}
  <li>[<a href="{{ site.issue_base_url }}{{ rfc.id }}">{{ rfc.id }}</a>] - {{ rfc.title }}</li>
{% endfor %}
</ul>

{% endif %}

### Installation and configuration

{{ cdata.installation | replace: '!SITE_URL!', site.baseurl }}

You can find more information about upgrade, clean installation and configuration of
StoRM services in the [System Administration Guide]({{site.baseurl}}/documentation/sysadmin-guide/).

{% else %}

No component found.

{% endif %}
