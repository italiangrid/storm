### Moving from classicSE to StoRM

Since StoRM does not use a database to store the location of data into the storage system, moving from a classicSE to StoRM is really easy. There are not migration scripts or special procedures: it only requires to install StoRM on the desired host and all your data will be automatically available in GRID through the SRM interface.

#### Instructions

1. Please have a look to the [StoRM requirements]({{ site.baseurl }}/documentation/{{ site.storm_latest_version }}/sysadmin-guide.html#installprereq).
1. Follow the StoRM [installation]({{ site.baseurl }}/documentation/{{ site.storm_latest_version }}/sysadmin-guide.html#installationguide) and [configuration]({{ site.baseurl }}/documentation/{{ site.storm_latest_version }}/sysadmin-guide.html#configuration) guides.
1. After launching YAIM, the StoRM services will be up.

#### Information System

The YAIM installation takes care of installing and configuring all things related to the information system. Once YAIM end, your SE machine will be published as an **SRM 2.2 service**.

#### Catalog

Please follow the instructions [here](https://twiki.cern.ch/twiki/bin/view/LCG/ChangeSeName).

#### Testing the system

The new StoRM installation can be tested by using one of the following SRM clients:

* lcg-utils
* SAM test
* download and use our SRM v2.2 Command Line Client: [clientSRM]({{ site.baseurl }}/documentation/clientSRM-guide.html)

#### Troubleshooting

For any problem please refer to **storm-support@cnaf.infn.it**.
