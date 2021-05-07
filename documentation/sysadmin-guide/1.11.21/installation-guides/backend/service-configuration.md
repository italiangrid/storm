## Service configuration

The Backend needs to be configured for two main aspects:

- _service information_: this section contains all the parameter regarding the StoRM service details. It relies on the **storm.properties** configuration file.
- _storage information_: this section contains all the information regarding Storage Area and other storage details. It relies on the **namespace.xml** file.

Both _storm.properties_ and _namespace.xml_ configuration file location is:

    /etc/storm/backend-server

The _storm.properties_ configuration file contains a list of key-value pairs that represent all the information needed to configure the StoRM Backend service.  When the BackEnd starts, it writes into the log file the whole set of parameters read from the configuration file.

The _namespace.xml_ configuration file contains the storage area info like what is needed to perform the **mapping functionality**, the **storage area capabilities**, which are the **access and transfer protocols** supported, etc..

{% include_relative service-information.md %}
{% include_relative storage-information.md %}
