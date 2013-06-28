{% for item in page.release_notes %}
* [{{ item.name }}]({{ site.baseurl }}/release-notes/{{ item.name }}.html)
{% endfor %}