<ul>
{% for rfc in page.rfcs %}
  {% if page.filter == "all" or rfc.type == filter %}
    <li><a href="{{ site.issue_base_url }}{{ rfc.id }}">{{ rfc.id }}</a> : {{ rfc.title }}</li>
  {% endif %}
{% endfor %}
</ul>