{% for item in site.storm_released_versions %}
 {% if item.version != page.version %}
  {% if item.documentation == "true" %} 
- [{{ item.version }} ]({{ page.rootdir }}/documentation/{{ item.version }}/index.html)
  {% endif %}
 {% endif %}
{% endfor %}