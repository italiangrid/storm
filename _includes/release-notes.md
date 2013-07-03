{% assign has_bug=false %}
{% assign has_feature=false %}
{% assign has_issues=false %}
{% assign selected=false %}
{% for release in site.storm_released_versions %}
 {% if release.version == page.release %}
  {% assign selected=release %}
  {% for rfc in release.rfcs %}
   {% if component_filter == "all" or rfc.component contains component_filter %}
    {% if rfc.type == 'bug' %}{% assign has_bug=true %}{% endif %}
    {% if rfc.type == 'issue' %}{% assign has_issue=true %}{% endif %}
    {% if rfc.type == 'feature' %}{% assign has_feature=true %}{% endif %}
   {% endif %}
  {% endfor %}
  {% for comp in release.components %}
   {% if comp.name == component_filter %}
    {% assign component_version=comp.version %}
   {% endif %}
  {% endfor %}
 {% endif %}
{% endfor %}

{% for item in site.storm_components %}
  {% if item.package == component_filter %}
    {% assign component_detail=item %}
  {% endif %}
{% endfor %}

{% if component_filter == "all" %}
# {{ selected.title }}

_Released on **{{ selected.date }}**_

{{ selected.description }}

### Released packages

{% include release-notes/components-list.html %}
{% else %}
# {{ component_detail.name }} v. {{ component_version }}

_Released on **{{ selected.date }}**_  with [{{ selected.title }}]({{ site.baseurl }}/release-notes/{{ selected.version }}.html)

{{ selected.description }}
{% endif %}

### Bug fixes 

{% assign version_filter=selected.version %}
{% assign type_filter="bug" %}
{% if has_bug %}
{% include filtered-list-rfcs.md %}
{% else %}
None.
{% endif %}

### Enhancements

{% assign version_filter=selected.version %}
{% assign type_filter="feature" %}
{% if has_feature %}
{% include filtered-list-rfcs.md %}
{% else %}
None.
{% endif %}

### Installation and configuration

You can find information about upgrade, clean installation and configuration of StoRM services in the [Documentation]({{ site.baseurl }}/documentation/{{ page.release }}/index.html) section.

### Known issues

{% assign version_filter=selected.version %}
{% assign type_filter="issue" %}
{% if has_issue %}
{% include filtered-list-rfcs.md %}
All of these issues will be fixed soon with next release.
{% else %}
None.
{% endif %}