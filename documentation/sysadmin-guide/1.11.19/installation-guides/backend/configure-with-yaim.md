## Configure the service with YAIM

StoRM Backend can be configured with YAIM tool on **CentOS 6 platform**.

Read more about YAIM tool [here][yaim-configuration-tool] and what are the [general YAIM variables][general-yaim-variables] for a StoRM deployment.

StoRM Backend specific YAIM variables can be found in the following file:

```
/opt/glite/yaim/exaples/siteinfo/services/se_storm_backend
```

You have to set at least these variables in your `site-info.def`:

- `STORM_BACKEND_HOST`
- `STORM_DEFAULT_ROOT`
- `STORM_DB_PWD`

and check the other variables to evaluate if you like the default set or if you want to change those settings.

The following table summaries YAIM variables for StoRM Backend component.

|   Var. Name                       |   Description |
|:----------------------------------|:--------------|
|`STORM_ACLMODE`                    |ACL enforcing mechanism (default value for all Storage Areas). Note: you may change the settings for each SA acting on `STORM_[SA]_ACLMODE` variable. Available values: aot, jit (use aot for WLCG experiments).<br/>Optional variable. Default value: **aot**
|`STORM_ANONYMOUS_HTTP_READ`        |Storage Area anonymous read access via HTTP. Note: you may change the settings for each SA acting on `STORM_[SA]_ANONYMOUS_HTTP_READ` variable.<br/>Optional variable. Available values: true, false. Default value: **false**
|`STORM_AUTH`                       |Authorization mechanism (default value for all Storage Areas). Note: you may change the settings for each SA acting on `STORM_[SA]_AUTH` variable Available values: permit-all, deny-all, FILENAME.<br/>Optional variable. Default value: **permit-all**
|`STORM_BACKEND_HOST`               |Host name of the StoRM Backend server. **Mandatory**.
|`STORM_BACKEND_REST_SERVICES_PORT` |StoRM backend server rest port. Optional variable. Default value: **9998**
|`STORM_BE_XMLRPC_TOKEN`            |Token used y Frontend in communication to the StoRM Backend
|`STORM_CERT_DIR`                   |Host certificate directory for StoRM Backend service.<br/>Optional variable. Default value: **/etc/grid-security/STORM_USER**
|`STORM_DEFAULT_ROOT`               |Default directory for Storage Areas. **Mandatory**.
|`STORM_DB_HOST`                    |Host for database connection.<br/>Optional variable. Default value: **localhost**
|`STORM_DB_PWD`                     |Password for database connection. **Mandatory**.
|`STORM_DB_USER`                    |User for database connection.<br/>Optional variable. Default value: **storm**
|`STORM_FRONTEND_HOST_LIST`         |StoRM Frontend service host list: SRM endpoints can be more than one virtual host different from `STORM_BACKEND_HOST` (i.e. dynamic DNS for multiple StoRM Frontends).<br/>Mandatory variable. Default value: **STORM_BACKEND_HOST**
|`STORM_FRONTEND_PATH`              |StoRM Frontend service path.<br/>Optional variable. Default value: **/srm/managerv2**
|`STORM_FRONTEND_PORT`              |StoRM Frontend service port. Optional variable. Default value: **8444**
|`STORM_FRONTEND_PUBLIC_HOST`       |StoRM Frontend service public host. It's used by StoRM Info Provider to publish the SRM endpoint into the Resource BDII.<br/>Mandatory variable. Default value: **STORM_BACKEND_HOST**
|`STORM_FSTYPE`                     |File System Type (default value for all Storage Areas). Note: you may change the settings for each `SA` acting on `STORM_[SA]_FSTYPE` variable.<br/>Optional variable. Available values: posixfs, gpfs and test. Default value: **posixfs**
|`STORM_GRIDFTP_POOL_LIST`          |GridFTP servers pool list (default value for all Storage Areas). Note: you may change the settings for each `SA` acting on `STORM_[SA]_GRIDFTP_POOL_LIST` variable.<br/>ATTENTION: this variable define a list of pair values space-separated: host weight, e.g.: `STORM_GRIDFTP_POOL_LIST="host1 weight1, host2 weight2, host3 weight3"` Weight has 0-100 range; if not specified, weight will be 100.<br/>Mandatory variable. Default value: **STORM_BACKEND_HOST**
|`STORM_GRIDFTP_POOL_STRATEGY`      |Load balancing strategy for GridFTP server pool (default value for all Storage Areas). Note: you may change the settings for each `SA` acting on `STORM_[SA]_GRIDFTP_POOL_STRATEGY` variable.<br/>Optional variable. Available values: round-robin, smart-rr, random, weight. Default value: **round-robin**
|`STORM_INFO_FILE_SUPPORT`          |If set to false, the following variables prevent the corresponding protocol to be published by the StoRM gip.<br/>Optional variable. Available values: true, false. Default value: **true**
|`STORM_INFO_GRIDFTP_SUPPORT`       |If set to false, the following variables prevent the corresponding protocol to be published by the StoRM gip.<br/>Optional variable. Available values: true, false. Default value: **true**
|`STORM_INFO_RFIO_SUPPORT`          |If set to false, the following variables prevent the corresponding protocol to be published by the StoRM gip. <br/>Optional variable. Available values: true, false. Default value: **false**
|`STORM_INFO_ROOT_SUPPORT`          |If set to false, the following variables prevent the corresponding protocol to be published by the StoRM gip.<br/>Optional variable. Available values: true, false. Default value: **false**
|`STORM_INFO_HTTP_SUPPORT`          |If set to false, the following variables prevent the corresponding protocol to be published by the StoRM gip.<br/>Optional variable. Available values: true, false. Default value: **false**
|`STORM_INFO_HTTPS_SUPPORT`         |If set to false, the following variables prevent the corresponding protocol to be published by the StoRM gip.<br/>Optional variable. Available values: true, false. Default value: **false**
|`STORM_NAMESPACE_OVERWRITE`        |This parameter tells YAIM to overwrite namespace.xml configuration file. Optional variable. Available values: true, false. Default value: **true**
|`STORM_RFIO_HOST`                  |Rfio server (default value for all Storage Areas). Note: you may change the settings for each `SA` acting on `STORM_[SA]_RFIO_HOST` variable.<br/>Optional variable. Default value: **STORM_BACKEND_HOST**
|`STORM_ROOT_HOST`                  |Root server (default value for all Storage Areas). Note: you may change the settings for each `SA` acting on `STORM_[SA]_ROOT_HOST` variable.<br/>Optional variable. Default value: **STORM_BACKEND_HOST**
|`STORM_SERVICE_SURL_DEF_PORTS`     |Comma-separated list of managed SURL's default ports used to check SURL validity.<br/>Optional variable. Default value: **8444**
|`STORM_SIZE_LIMIT`                 |Limit Maximum available space on the Storage Area (default value for all Storage Areas).<br/>Note: you may change the settings for each `SA` acting on `STORM_[SA]_SIZE_LIMIT` variable. Optional variable. Available values: true, false. Default value: **true**
|`STORM_STORAGEAREA_LIST`           |List of supported Storage Areas. Usually at least one Storage Area for each VO specified in VOS should be created.<br/>Optional variable. Default value: **VOS**
|`STORM_STORAGECLASS`               |Storage Class type (default value for all Storage Areas). Note: you may change the settings for each `SA` acting on `STORM_[SA]_STORAGECLASS` variable. <br/>Optional variable. Available values: T0D1, T1D0, T1D1. No default value.
|`STORM_SURL_ENDPOINT_LIST`         |This is a comma separated list of the SRM endpoints managed by the Backend. A SURL is accepted only if this list contains the endpoint specified. It's an optional variable with default value: **srm://STORM_FRONTEND_PUBLIC_HOST:STORM_FRONTEND_PORT/STORM_FRONTEND_PATH**. So, if you want to accept requests with incoming SURLs that has the ip address instead of the FQDN hostname, add the full srm endpoint to this list.
|`STORM_ENDPOINT_QUALITY_LEVEL`     |Endpoint maturity level to be published by the StoRM gip. Optional variable. Default value: **2**
|`STORM_ENDPOINT_SERVING_STATE`     |Endpoint serving state to be published by the StoRM gip. Optional variable. Default value: **4**
|`STORM_ENDPOINT_CAPABILITY`        |Capability according to OGSA to be published by the StoRM gip. Optional variable. Default value: **data.management.storage**
| `STORM_WEBDAV_POOL_LIST`          | Publish the WebDAV endpoints listed by this variable. Default value: **https://STORM_BACKEND_HOST:8443,http://STORM_BACKEND_HOST:8085** |

### Storage Area variables

Then, for each Storage Area listed in the `STORM_STORAGEAREA_LIST` variable, which is not the name of a valid VO,
you have to edit the `STORM_[SA]_VONAME` compulsory variable. `SA` has to be written in capital letters as in the other variables included in the **site-info.def** file, otherwise default values will be used.

> For the DNS-like names, that use special characters as '.' or '-' you have to remove the '.' and '-'.<br/>
> For example the `SA` value for the storage area "test.vo" must be TESTVO:

    STORM_TESTVO_VONAME=test.vo

For each storage area `SA` listed in `STORM_STORAGEAREA_LIST` you have to set at least these variables: `STORM_[SA]_ONLINE_SIZE`
You can edit the optional variables summarized in the following table:

|   Var. Name                         |   Description |
|:------------------------------------|:--------------|
|`STORM_[SA]_VONAME`                  |Name of the VO that will use the Storage Area. Use the complete name, e.g., "lights.infn.it" to specify that there is no VO associated to the storage area (it's readable and writable from everyone - less than other filters). This variable becomes **mandatory if the value of SA is not the name of a VO**.
|`STORM_[SA]_ANONYMOUS_HTTP_READ`     |Storage Area anonymous read access via HTTP.<br/>Optional variable. Available values: true, false. Default value: **false**
|`STORM_[SA]_ACCESSPOINT`             |List space-separated of paths exposed by the SRM into the SURL. Optional variable. Default value: `SA`
|`STORM_[SA]_ACLMODE`                 |See `STORM_ACLMODE` definition. Optional variable. Default value: **STORM_ACLMODE**
|`STORM_[SA]_AUTH`                    |See `STORM_AUTH` definition. Optional variable. Default value: **STORM_AUTH**
|`STORM_[SA]_DEFAULT_ACL_LIST`        |A list of ACL entries that specifies a set of local groups with corresponding permissions (R, W, RW) using the following syntax: groupname1:permission1 [groupname2:permission2] [...]
|`STORM_[SA]_DN_C_REGEX`              |Regular expression specifying the format of C (Country) field of DNs that will use the Storage Area. Optional variable.
|`STORM_[SA]_DN_O_REGEX`              |Regular expression specifying the format of O (Organization name) field of DNs that will use the Storage Area. Optional variable.
|`STORM_[SA]_DN_OU_REGEX`             |Regular expression specifying the format of OU (Organizational Unit) field of DNs that will use the Storage Area. Optional variable.
|`STORM_[SA]_DN_L_REGEX`              |Regular expression specifying the format of L (Locality) field of DNs that will use the Storage Area. Optional variable.
|`STORM_[SA]_DN_CN_REGEX`             |Regular expression specifying the format of CN (Common Name) field of DNs that will use the Storage Area. Optional variable.
|`STORM_[SA]_FILE_SUPPORT`            |Enable the corresponding protocol. Optional variable. Default value: **STORM_INFO_FILE_SUPPORT**
|`STORM_[SA]_GRIDFTP_SUPPORT`         |Enable the corresponding protocol. Optional variable. Default value: **STORM_INFO_GRIDFTP_SUPPORT**
|`STORM_[SA]_RFIO_SUPPORT`            |Enable the corresponding protocol. Optional variable. Default value: **STORM_INFO_RFIO_SUPPORT**
|`STORM_[SA]_ROOT_SUPPORT`            |Enable the corresponding protocol. Optional variable. Default value: **STORM_INFO_ROOT_SUPPORT**
|`STORM_[SA]_HTTP_SUPPORT`            |Enable the corresponding protocol. Optional variable. Default value: **STORM_INFO_HTTP_SUPPORT**
|`STORM_[SA]_HTTPS_SUPPORT`           |Enable the corresponding protocol. Optional variable. Default value: **STORM_INFO_HTTPS_SUPPORT**
|`STORM_[SA]_FSTYPE`                  |See `STORM_[SA]_FSTYPE` definition. Optional variable. Available values: posixfs, gpfs. Default value: **STORM_FSTYPE**
|`STORM_[SA]_GRIDFTP_POOL_LIST`       |See `STORM_GRIDFTP_POOL_LIST` definition. Optional variable. Default value: **STORM_GRIDFTP_POOL_LIST**
|`STORM_[SA]_GRIDFTP_POOL_STRATEGY`   |See `STORM_GRIDFTP_POOL_STRATEGY` definition. Optional variable. Default value: **STORM_GRIDFTP_POOL_STRATEGY**
|`STORM_[SA]_ONLINE_SIZE`             |Total size assigned to the Storage Area Expressed in GB. Must be an integer value. **Mandatory**.
|`STORM_[SA]_ORGS`                    |Comma separated list of the supported authorization servers used by StoRM WebDAV with bearer tokens. No default value.
|`STORM_[SA]_QUOTA`                   |Enables the quota management for the Storage Area and it works only on GPFS filesystem. Optional variable. Available values: true, false. Default value: **false**
|`STORM_[SA]_QUOTA_DEVICE`            |GPFS device on which the quota is enabled. It is mandatory if `STORM_[SA]_QUOTA` variable is set. No default value.
|`STORM_[SA]_QUOTA_USER`              |GPFS quota scope. Only one of the following three will be used (the first one with the highest priority in this order: USER, then GROUP, then FILESET). Optional variable. No default value.
|`STORM_[SA]_QUOTA_GROUP`             |GPFS quota scope. Only one of the following three will be used (the first one with the highest priority in this order: USER, then GROUP, then FILESET). Optional variable. No default value.
|`STORM_[SA]_QUOTA_FILESET`           |GPFS quota scope. Only one of the following three will be used (the first one with the highest priority in this order: USER, then GROUP, then FILESET). Optional variable. No default value.
|`STORM_[SA]_RFIO_HOST`               |See `STORM_RFIO_HOST` definition. Optional variable. Default value: **STORM_RFIO_HOST**
|`STORM_[SA]_ROOT`                    |Physical storage path for the VO. Optional variable. Default value: **STORM_DEFAULT_ROOT/`SA`**
|`STORM_[SA]_ROOT_HOST`               |See `STORM_ROOT_HOST` definition. Optional variable. Default value: **STORM_ROOT_HOST**
|`STORM_[SA]_SIZE_LIMIT`              |See `STORM_SIZE_LIMIT` definition. Default value: **STORM_SIZE_LIMIT**
|`STORM_[SA]_STORAGECLASS`            |See `STORM_STORAGECLASS` definition. Available values: `T0D1`, `T1D0`, `T1D1`, null. No default value.
|`STORM_[SA]_TOKEN`                   |Storage Area token, e.g: `LHCb_RAW`, `INFNGRID_DISK`. No default value.
|`STORM_[SA]_USED_ONLINE_SIZE`        |Storage space currently used in the Storage Area expressed in Bytes. Must be an integer value. Used by YAIM to populate used-space.ini file.

The most important (and mandatory) parameters of _storm.properties_ are configured by default trough YAIM with a standard installation of StoRM. All the other parameters are optionals and can be used to make advanced tuning of the Backend. To change/set a new value, or add a new parameter, just edit the _storm.properties_ file and restart the Backend daemon.

To configure the service with YAIM, run the following command:

```bash
/opt/glite/yaim/bin/yaim -c -s SITEINFO.def -n se_storm_backend
```


[yaim-configuration-tool]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.19/installation-guides/common/yaim-configuration-tool.html
[general-yaim-variables]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.19/installation-guides/common/general-yaim-variables.html
