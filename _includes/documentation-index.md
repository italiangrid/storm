{% if site.storm_latest_version != page.version %}
<p style="position: absolute; top: 50px; right: 150px; color: #2f82ff; font-style: italic; background-color: #cbe4ff; border: 2px solid #9ec3ff; width: 500px; padding: 1px 10px;">
This is not the documentation of the latest version. Go to the latest version <a href="{{ site.baseurl }}/documentation/{{ site.storm_latest_version }}/index.html">here</a>
</p>
{% endif %}

<img src="{{ site.baseurl }}/assets/images/documentation.png" alt="documentation" width="100" style="float: left; padding-right: 26px; margin-left: 2px; margin-top: 24px; background-color: white;"/>

## StoRM Documentation
_Here you can find all the StoRM guides, useful to learn what is StoRM service and how to install and configure it on your system. Follow the user guides to take the most of StoRM's functionalities and see FAQ and Troubleshooting sections if something is wrong._

<br/>

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

This is the documentation for: <b>{{ page.release_title }}</b>
<br/>
Is not this version you was looking for? Choose another version: {% include versions.md %}

