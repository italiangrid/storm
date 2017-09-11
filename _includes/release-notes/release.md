{% assign rdata=site.data.releases[page.release].info %}

{% if rdata != null %}

## StoRM v. {{ rdata.version }}

Released on **{{rdata.date}}**.

### Description

{{ rdata.description | replace: '!SITE_URL!', site.baseurl }}

### Released components

<table>
	<thead>
		<tr>
			<th>Component</th>
			<th>Version</th>
		</tr>
	</thead>
	<tbody>
	{% for cname in rdata.components %}
    {% assign component = site.data.releases[page.release][cname] %}
		<tr>
			<td>{{component.name}}</td>
			<td>
				<a href="{{site.baseurl}}{{site.releasenotes_path}}/{{component.package}}/{{component.version}}/">{{component.version}}</a>
			</td>
		</tr>
	{% endfor %}
	</tbody>
</table>

{% assign rfcs = site.emptyArray %}
{% assign features = site.emptyArray %}

{% for cname in rdata.components %}
  {% assign component = site.data.releases[page.release][cname] %}
  {% for rfc in component.rfcs %}
    {% assign rfcs = rfcs | push: rfc %}
  {% endfor %}
  {% for rfc in component.features %}
    {% assign features = features | push: rfc %}
  {% endfor %}
{% endfor %}

{% if rfcs.size > 0 %}

### Bug fixes

{% assign rfcs = (rfcs | sort: 'id') %}

<ul>
{% assign lastId = null %}
{% for rfc in rfcs %}
  {% if rfc.id != lastId %}
    <li>[<a href="{{ site.issue_base_url }}{{ rfc.id }}">{{ rfc.id }}</a>] - {{ rfc.title }}</li>
  {% endif %}
  {% assign lastId = rfc.id %}
{% endfor %}
</ul>

{% endif %}

{% if features.size > 0 %}

### Improvements

{% assign features = (features | sort: 'id') %}

<ul>
{% assign lastId = null %}
{% for rfc in features %}
  {% if rfc.id != lastId %}
    <li>[<a href="{{ site.issue_base_url }}{{ rfc.id }}">{{ rfc.id }}</a>] - {{ rfc.title }}</li>
  {% endif %}
  {% assign lastId = rfc.id %}
{% endfor %}
</ul>

{% endif %}

### Installation and configuration

Packages can be obtained from our repositories (see the instructions in the [download section]({{site.baseurl}}/download.html)).

{{ rdata.installation | replace: '!SITE_URL!', site.baseurl }}

You can find more information about upgrade, clean installation and configuration of StoRM services in the [System Administration Guide]({{site.baseurl}}/documentation/sysadmin-guide/).

{% else %}

No release found.

{% endif %}
