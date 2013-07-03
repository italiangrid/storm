{% for release in site.storm_released_versions %}
  {% if release.version == page.version %}
    {% assign selected=release %}
  {% endif %}
{% endfor %}

# StoRM Documentation
_Here you can find all the StoRM guides, useful to learn what is StoRM service and how to install and configure it on your system. Follow the user guides to take the most of StoRM's functionalities and see FAQ and Troubleshooting sections if something is wrong._

Updated to release: [**{{ selected.title }}**]({{ site.baseurl }}/release-notes/{{ selected.version }}.html)
<hr/>

#### [System administrator guide](sysadmin-guide.html)
- A guide to install and configure StoRM components on a single or distributed scenario.

#### [WebDAV service user guide](../webdav-guide.html)
- Explains the StoRM WebDAV interface, how to install, configure and use it.

#### [StoRM clientSRM user guide](../clientSRM-guide.html)
- Explains how to use the StoRM SRM client.

#### [Frequently Asked Questions](../faq.html)
- Questions from the StoRM user community.

#### [Troubleshooting](../troubleshooting.html)
- Common issues: analysis and solution.
	
#### [Cookbook](../cookbook.html)
- Useful recipes for StoRM advanced configuration.

#### [Conferences and tutorials](../tutorials-conferences-presentations.html)
- Conferences, tutorials, presentations and papers.

<hr/>

Is not this version you was looking for? Choose another version:
{% for item in site.storm_released_versions %}
 {% if item.version != selected.version %}
* **[{{ item.title }}]({{ page.rootdir }}/documentation/{{ item.version }}/index.html)**
 {% endif %}
{% endfor %}

<br/>