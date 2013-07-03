---
layout: default
title: StoRM Storage Resource Manager
---

# StoRM releases

StoRM packages can be obtained from the EMI repository or from the StoRM product team package repository.

### Repository configuration 

#### EMI 3 

You can find [general EMI 3 installation instructions](https://twiki.cern.ch/twiki/bin/view/EMI/GenericInstallationConfigurationEMI3) on the EMI site, but it basically boils down to installing the EMI repository

	rpm --import http://emisoft.web.cern.ch/emisoft/dist/EMI/3/RPM-GPG-KEY-emi
	wget http://emisoft.web.cern.ch/emisoft/dist/EMI/3/sl5/x86_64/base/emi-release-3.0.0-2.el5.noarch.rpm
	yum localinstall -y emi-release-3.0.0-2.el5.noarch.rpm

Follow the [system administration guide]({{ site.baseurl }}/documentation/{{ site.storm_latest_version}}/sysadmin-guide.html) for detailed installation instructions.

#### StoRM

Note that the StoRM PT repositories only provide the latest version of the certified StoRM packages.
You still need to install EMI3 repositories (as detailed above) for installations to work as expected.

To install the repository files, run the following commands (as root):

    (SL5) # wget http://italiangrid.github.io/storm/repo/storm_sl5.repo -O /etc/yum.repos.d/storm_sl5.repo
    (SL6) # wget http://italiangrid.github.io/storm/repo/storm_sl6.repo -O /etc/yum.repos.d/storm_sl6.repo

## Current release
{% for release in site.storm_released_versions %}
  {% if release.version == site.storm_latest_version %}
    {% assign current=release %}
  {% endif %}
{% endfor %}

The current release is [**{{ current.title }}**]({{ site.baseurl }}/release-notes/{{ current.version }}.html)

{% include download/component-table.html %}

Last release RFCs:

{% assign version_filter=site.storm_latest_version %}
{% assign type_filter="bug" %}
{% assign component_filter="all" %}
{% include filtered-list-rfcs.md %}

## Previous releases

{% for item in site.storm_released_versions %}
  {% if item.version != site.storm_latest_version %}
* **{{ item.title }}** - _{{ item.date }}_ - {{ item.description }} - [Release notes]({{ site.baseurl }}/release-notes/{{ item.version }}.html) - [Documentation]({{ site.baseurl }}/documentation/{{ item.version }}/index.html) 
  {% endif %}
{% endfor %}

##### _Older releases_:

{% for item in site.storm_old_versions %}
* **StoRM v. {{ item.version }}** - _{{ item.date }}_ - {{ item.description }} - [Release notes]({{ item.notes }}) 
{% endfor %}

## Testing versions

We are going provide a repository for testing versions, i.e versions for which the development has finished and can be passed to early adopters for the staged roll-out.

## Development versions

Development versions are built regularly on our [continuous integration infrastructure](http://radiohead.cnaf.infn.it:9999/view/STORM/). 

Artifacts for the last commit can be found on our yum repos for [SL5](http://radiohead.cnaf.infn.it:9999/view/STORM/job/storm-repo_SL5/lastSuccessfulBuild/artifact/storm.repo) or [SL6](http://radiohead.cnaf.infn.it:9999/view/STORM/job/storm-repo_SL6/lastSuccessfulBuild/artifact/storm.repo).

Install the development repositories like

	wget http://radiohead.cnaf.infn.it:9999/view/STORM/job/storm-repo_SL5/lastSuccessfulBuild/artifact/storm.repo -O /etc/yum.repos.d/storm.repo
	
<br/>

[storm-emi3-v1.11.1]: http://www.eu-emi.eu/releases/emi-3-monte-bianco/updates/-/asset_publisher/5Na8/content/update-5-03-06-2013-v-3-3-0-1#STORM_v_1_11_1



