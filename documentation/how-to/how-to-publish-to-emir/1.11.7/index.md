---
layout: default
title: StoRM How-To - StoRM EMIR Configuration
---

#### [Back to How-To]({{site.baseurl}}/documentation/examples)

# StoRM EMIR Configuration

You can use EMIR-SERP to publish StoRM information to EMIR. EMIR-SERP uses the information already available in the resource resource BDII (aka ERIS) and publish it to an EMIR DSR endpoint.


First check that the resource bdii is up and running. Executing:

	ldapsearch -x -h localhost -p 2170 -b 'GLUE2GroupID=resource,o=glue' objectCLass=GLUE2Service
	
it should return two services for each vo installed on the machine. If this is not the case, there is some problem with your installation.
Then install serp:

	sudo yum install emir-serp

and edit the configuration file */etc/emi/emir-serp/emir-serp.ini*, providing the url for the EMIR DSR and the url for the resource bdii

	...
	url = http://emitbdsr1.cern.ch:9126
	...
	[servicesFromResourceBDII]
	resource_bdii_url = ldap://localhost:2170/GLUE2GroupID=resource,o=glue
	...
	
You can change the update interval

	# Period of registration/update messages
	# Mandatory configuration parameter
	# Value is given in hours
	period = 1
	
and the time of registration entry validity:

	# Time of registration entry validity
	# Mandatory configuration parameter
	# Value is given in hours
	validity = 2

You might want to set the logging level to debug the first time you start the service:

	verbosity = debug

Start the service:

	sudo service emir-serp start

Verify the publication by inspecting [this page](http://emitbdsr1.cern.ch:9126/services) searching for an entity with "Name" attribute equal to StoRM YAIM variable "SITE\_NAME". It is recommended to set back the logging level to error and restart the service. Stopping emir-serp will cause the entry to be deleted.