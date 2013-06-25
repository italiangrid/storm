
### StoRM current release: [{{ site.storm_latest_version }}]({{ page.rootdir }}/documentation/{{ site.storm_latest_version}}/release-notes.html)

StoRM current release is [v. {{ site.storm_latest_version }}]({{ page.rootdir }}/documentation/{{ site.storm_latest_version}}/release-notes.html). 
You can read the release notes [here]({{ page.rootdir }}/documentation/{{ site.storm_latest_version}}/release-notes.html).
See [documentation]({{ page.rootdir }}/documentation/{{ site.storm_latest_version}}/index.html) for further info.

#### _Previous releases_:

{% for item in site.storm_released_versions %}
 {% if item.notes == "true" %}
  {% assign noteslink = {{ page.rootdir }}/documentation/{{ item.version }}/release-notes.html %}
 {% else %}
  {% assign noteslink = {{ item.notes }} %}
 {% endif %}
 {% if item.documentation == "true" %}
  {% assign doclink = {{ page.rootdir }}/documentation/{{ item.version }}/index.html %}
 {% endif %}
 {% if item.version != site.storm_latest_version %}
* **StoRM v. {{ item.version }}** - _{{ item.date }}_ - {{ item.description }} - 
[Release notes]({% if item.notes == "true" %}{{ page.rootdir }}/documentation/{{ item.version }}/release-notes.html{% else %}{{ item.notes }}{% endif %})
{% if item.documentation == "true" %} [Documentation]({{ page.rootdir }}/documentation/{{ item.version }}/index.html){% endif %}
 {% endif %}
{% endfor %}