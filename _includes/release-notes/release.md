{% assign rdata=site.data.releases[page.release].info %}

{% if rdata != null %}

## StoRM v. {{ rdata.version }}

Released on **{{rdata.date}}**.

### Description

{{ rdata.description }}

### Released components

<table>
	<thead>
		<tr>
			<th align=left>Component</th>
			<th align=left>Version</th>
		</tr>
	</thead>
	<tbody>
	{% for cname in rdata.components %}
    {% assign component = site.data.releases[page.release][cname] %}
		<tr>
			<td align=left>{{component.name}}</td>
			<td align=left>
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
{% for rfc in rfcs %}
  <li>[<a href="{{ site.issue_base_url }}{{ rfc.id }}">{{ rfc.id }}</a>] - {{ rfc.title }}</li>
{% endfor %}
</ul>

{% endif %}

{% if features.size > 0 %}

### Improvements

{% assign features = (features | sort: 'id') %}

<ul>
{% for rfc in features %}
  <li>[<a href="{{ site.issue_base_url }}{{ rfc.id }}">{{ rfc.id }}</a>] - {{ rfc.title }}</li>
{% endfor %}
</ul>

{% endif %}

### Installation and configuration

Packages can be obtained from our repositories (see the instructions in the [download section]({{site.baseurl}}/download.html)) and will soon be available on the EMI-3 repository.

{{ rdata.installation | replace: '!SITE_URL!', site.baseurl }}

You can find more information about upgrade, clean installation and configuration of StoRM services in the [System Administration Guide]({{site.baseurl}}/documentation/sysadmin-guide/).

{% else %}

No release found.

{% endif %}
