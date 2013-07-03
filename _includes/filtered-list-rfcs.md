<ul>
{% for release in site.storm_released_versions %}
	{% if version_filter == "all" or release.version == version_filter %}
		{% for rfc in release.rfcs %}
			{% if type_filter == "all" or rfc.type == type_filter %}
				{% if component_filter == "all" or rfc.component contains component_filter %}
    <li><a href="{{ site.issue_base_url }}{{ rfc.id }}">{{ rfc.id }}</a> : {{ rfc.title }}</li>			
				{% endif %}
			{% endif %}
		{% endfor %}
	{% endif %}
{% endfor %}
</ul>