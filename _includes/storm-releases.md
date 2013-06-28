{% if show_release == "current" %}
  {% for item in site.storm_released_versions %}
    {% if item.version == site.storm_latest_version %}
The current release is **StoRM v.{{ item.version }}**.

Release notes:
      {% for note in item.notes %}
* [{{ note.name }}]({{ site.baseurl }}/release-notes/{{ note.name }}.html)
      {% endfor %}
    {% endif %}
  {% endfor %}
See [documentation]({{ site.baseurl }}/documentation/{{ site.storm_latest_version}}/index.html) for further info.

{% else if show_release == "previous" %}

  {% for item in site.storm_released_versions %}
    {% if item.version != site.storm_latest_version %}
* **StoRM v. {{ item.version }}** - _{{ item.date }}_ - {{ item.description }} - Release notes: {% for note in item.notes %}[{{ note.name }}]({{ site.baseurl }}/release-notes/{{ note.name }}.html){% endfor %} - [Documentation]({{ site.baseurl }}/documentation/{{ item.version }}/index.html) 
    {% endif %}
  {% endfor %}

{% else %}

  {% for item in site.storm_released_versions %}
* **StoRM v. {{ item.version }}** - _{{ item.date }}_ - {{ item.description }} - Release notes:
      {% for note in item.notes %}
[{{ note.name }}]({{ site.baseurl }}/release-notes/{{ note.name }}.html)
      {% endfor %}
[Documentation]({{ site.baseurl }}/documentation/{{ item.version }}/index.html) 
  {% endfor %}

{% endif %}