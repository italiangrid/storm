### Service information

#### Service Parameters

|   Property Name                         |   Description     |
|:----------------------------------------|:------------------|
| `storm.service.SURL.endpoint`           | List of comma separated strings identifying the StoRM Frontend endpoint(s). This is used by StoRM to understand if a SURL is local. E.g. *srm://storm.cnaf.infn.it:8444/srm/managerv2*. <br/> If you want to accept SURL with the ip address instead of the FQDN hostname you have to add the proper endpoint (E.g. IPv4: *srm://192.168.100.12:8444/srm/managerv2* or IPv6: *srm://[2001:0db8::1428:57ab]:8444/srm/managerv2*. Default value: **srm://`storm.service.FE-public.hostname`:8444/srm/managerv2**
| `storm.service.port`                    | SRM service port. Default: **8444**
| `storm.service.SURL.default-ports`      | List of comma separated valid SURL port numbers. Default: **8444**
| `storm.service.FE-public.hostname`      | StoRM Frontend hostname in case of a single Frontend StoRM deployment, StoRM Frontends DNS alias in case of a multiple Frontends StoRM deployment.
| `storm.service.FE-list.hostnames`       | Comma separated list os Frontend(s) hostname(s). Default: **localhost**
| `storm.service.FE-list.IPs`             | Comma separated list os Frontend(s) IP(s). E.g. *131.154.5.127, 131.154.5.128*. Default: **127.0.0.1**
| `pinLifetime.default`                   | Default *PinLifetime* in seconds used for pinning files in case of *srmPrepareToPut* or *srmPrepareToGet* operation without any pinLifetime specified. Default: **259200**
| `pinLifetime.maximum`                   | Maximum *PinLifetime* allowed in seconds.<br/>Default: **1814400**
| `fileLifetime.default`                  | Default *FileLifetime* in seconds used for VOLATILE file in case of SRM request without *FileLifetime* parameter specified. Default: **3600**
| `extraslashes.gsiftp`                   | Add extra slashes after the "authority" part of a TURL for gsiftp protocol.
| `extraslashes.rfio`                     | Add extra slashes after the "authority" part of a TURL for rfio protocol.
| `extraslashes.root`                     | Add extra slashes after the "authority" part of a TURL for root protocol.
| `extraslashes.file`                     | Add extra slashes after the "authority" part of a TURL for file protocol.
| `synchcall.directoryManager.maxLsEntry` | Maximum number of entries returned by an *srmLs* call. Since in case of recursive *srmLs* results can be in order of million, this prevent a server overload. Default: **500**
| `directory.automatic-creation`          | Flag to enable automatic missing directory creation upon *srmPrepareToPut* requests.<br/>Default: **false**
| `directory.writeperm`                   | Flag to enable directory write permission setting upon *srmMkDir* requests on created directories. Default: **false**
| `default.overwrite`                     | Default file overwrite mode to use upon *srmPrepareToPut* requests. Default: **A**. Possible values are: N, A, D. Please note that N stands for *Never*, A stands for *Always* and D stands for *When files differs*.
| `default.storagetype`                   | Default File Storage Type to be used for *srmPrepareToPut* requests in case is not provided in the request. Default: **V**. Possible values are: V, P, D. Please note that V stands for *Volatile*, P stands for *Permanent* and D stands for *Durable*.

#### Garbage collector <a name="requestsgarbagecollector">&nbsp;</a>

The requests garbage collector process cleans database from the expired asynchronous SRM requests. The value of `expired.request.time` defines how many seconds are necessary to a request, after its submission, to be considered expired. An appropriate tuning is needed in case of high throughput of SRM requests required for long time.

|   Property Name            |   Description     |
|:---------------------------|:------------------|
| `purging`                  |  Enable the request garbage collector. Default: **true**.
| `purge.interval`           |  Time interval in seconds between successive purging run. Default: **600**.
| `purge.size`               |  Number of requests picked up for cleaning from the requests garbage collector at each run. This value is use also by Tape Recall Garbage Collector. Default: **800**
| `purge.delay`              |  Initial delay before starting the requests garbage collection process, in seconds. Default: **10**
| `expired.request.time`     |  Time in seconds to consider a request expired after its submission. Default: **604800** seconds (1 week). From **StoRM 1.11.13** it is used also to identify how much time is needed to consider a completed recall task as cleanable.
| `expired.inprogress.time`  |  Time in seconds to consider an in-progress ptp request as expired. Default: **2592000** seconds (1 month)

**Expired PUT requests Garbage Collector**

This agent:

* transits ongoing srmPtP to SRM_FILE_LIFETIME_EXPIRED if the request pin-lifetime is expired (see `pinLifetime.default` variable into Service Information section).
* transits to SRM_FAILURE the srmPtP that after `expired.inprogress.time` seconds are still in SRM_REQUEST_INPROGRESS.

The agent runs each `transit.interval` seconds and updates all the expired requests.

|   Property Name    |   Description     |
|:-------------------|:------------------|
| `transit.interval` |   Time interval in seconds between successive agent run. Default: **3000**.
| `transit.delay`    |   Initial delay before starting the agent process, in seconds. Default: **60**

**Reserved Space Garbage Collector**

|   Property Name   |   Description     |
|:------------------|:------------------|
|   `gc.pinnedfiles.cleaning.delay`     |   Initial delay before starting the reserved space, JIT ACLs and pinned files garbage collection process, in seconds. Default: **10**
|   `gc.pinnedfiles.cleaning.interval`  |   Time interval in seconds between successive purging run. Default: **300**

#### Synchronous call

|   Property Name   |   Description     |
|:------------------|:------------------|
|   `synchcall.xmlrpc.unsecureServerPort`   |   Port to listen on for incoming XML-RPC connections from Frontends(s). Default: **8080**
|   `synchcall.xmlrpc.maxthread`            |   Number of threads managing XML-RPC connection from Frontends(s). A well sized value for this parameter have to be at least equal to the sum of the number of working threads in all FrontEend(s). Default: **100**
|   `synchcall.xmlrpc.max_queue_size`       |   Max number of accepted and queued XML-RPC connection from Frontends(s). Default: **1000**
|   `synchcall.xmlrpc.security.enabled`     |   Whether the backend will require a token to be present for accepting XML-RPC requests. Default: true
|   `synchcall.xmlrpc.security.token`       |   The token that the backend will require to be present for accepting XML-RPC requests. Mandatory if synchcall.xmlrpc.token.enabled is true

#### REST interface parameters

|   Property Name   |   Description     |
|:------------------|:------------------|
|   `storm.rest.services.port`  |   REST services port. Default: **9998**
|   `storm.rest.services.maxthreads`  |   REST services max active requests. Default: **100**
|   `storm.rest.services.max_queue_size`  |   REST services max queue size of accepted requests. Default: **1000**

#### Database connection parameters

|   Property Name   |   Description         |
|:------------------|:----------------------|
|   `storm.service.request-db.host`        |   Host for StoRM database. Default: **localhost**
|   `storm.service.request-db.username`    |   Username for database connection. Default: **storm**
|   `storm.service.request-db.passwd`      |   Password for database connection
|   `storm.service.request-db.properties`  |   Database connection URL properties. Default: **serverTimezone=UTC&autoReconnect=true**
|   `asynch.db.ReconnectPeriod`            |   Database connection refresh time intervall in seconds. Default: **18000**
|   `asynch.db.DelayPeriod`                |   Database connection refresh initial delay in seconds. Default: **30**
|   `persistence.internal-db.connection-pool.maxActive`  |   Database connection pool max active connections. Default: **10**
|   `persistence.internal-db.connection-pool.maxWait`    |   Database connection pool max wait time to provide a connection. Default: **50**

#### SRM Requests Picker

|   Property Name   |   Description     |
|:------------------|:------------------|
|   `asynch.PickingInitialDelay`    |   Initial delay before starting to pick requests from the DB, in seconds. Default: **1**
|   `asynch.PickingTimeInterval`    |   Polling interval in seconds to pick up new SRM requests. Default: **2**
|   `asynch.PickingMaxBatchSize`    |   Maximum number of requests picked up at each polling time. Default: **100**

#### Worker threads

|   Property Name   |   Description     |
|:------------------|:------------------|
|   `scheduler.crusher.workerCorePoolSize`          |   Crusher Scheduler worker pool base size. Default: **10**
|   `scheduler.crusher.workerMaxPoolSize`           |   Crusher Schedule worker pool max size. Default: **50**
|   `scheduler.crusher.queueSize`                   |   Request queue maximum size.<br/>Default: **2000**
|   `scheduler.chunksched.ptg.workerCorePoolSize`   |   *PrepareToGet* worker pool base size. Default: **50**
|   `scheduler.chunksched.ptg.workerMaxPoolSize`    |   *PrepareToGet* worker pool max size. Default: **200**
|   `scheduler.chunksched.ptg.queueSize`            |   *PrepareToGet* request queue maximum size. Default: **2000**
|   `scheduler.chunksched.ptp.workerCorePoolSize`   |   *PrepareToPut* worker pool base size. Default: **50**
|   `scheduler.chunksched.ptp.workerMaxPoolSize`    |   *PrepareToPut* worker pool max size. Default: **200**
|   `scheduler.chunksched.ptp.queueSize`            |   *PrepareToPut* request queue maximum size. Default: **1000**
|   `scheduler.chunksched.bol.workerCorePoolSize`   |   *BringOnline* worker pool base size. Default: **50**
|   `scheduler.chunksched.bol.workerMaxPoolSize`    |   *BringOnline* Worker pool max size. Default: **200**
|   `scheduler.chunksched.bol.queueSize`            |   *BringOnline* request queue maximum size. Default: **2000**
|   `scheduler.chunksched.copy.workerCorePoolSize`  |   *Copy* worker pool base size. Default: **10**
|   `scheduler.chunksched.copy.workerMaxPoolSize`   |   *Copy* worker pool max size. Default: **50**
|   `scheduler.chunksched.copy.queueSize`           |   *Copy* request queue maximum size. Default: **500**

#### Protocol balancing

|   Property Name   |   Description     |
|:------------------|:------------------|
|   `gridftp-pool.status-check.timeout` |   Time in milliseconds after which the status of a GridFTP has to be verified. Default: **20000** (20 secs)

#### Tape recall

|   Property Name   |   Description     |
|:------------------|:------------------|
|   `tape.recalltable.service.param.retry-value`   |   Default: **retry-value**
|   `tape.recalltable.service.param.status`        |   Default: **status**
|   `tape.recalltable.service.param.takeover`      |   Default: **first**

#### Disk Usage Service

The Disk Usage Service has been introduced within StoRM v1.11.18 and allows administrators to enable periodic du calls on the storage area root directory in order to compute the used space size.
By default the service is disabled. Set `storm.service.du.enabled` in yoir storm.properties file to enable it.

|   Property Name   |   Description     |
|:------------------|:------------------|
|   `storm.service.du.enabled`  |   Flag to enable disk usage service. Default: **false**
|   `storm.service.du.delay`    |   The initial delay before the service is started (seconds). Default: **60**
|   `storm.service.du.interval` |   The interval in seconds between successive run. Default: **360**.

Example of output from internal log with an enabled du service with 0 delay and a week as interval:

```
04:26:50.920 - INFO [main] - Starting DiskUsage Service (delay: 0s, period: 604800s)
04:26:50.943 - INFO [main] - DiskUsage Service started.
...
04:26:50.961 - INFO [pool-6-thread-1] - DiskUsageTask for NESTED_TOKEN on /storage/nested started ...
04:26:51.364 - INFO [pool-6-thread-1] - DiskUsageTask for NESTED_TOKEN successfully ended in 0s with used-size = 4096 bytes
04:26:51.365 - INFO [pool-6-thread-1] - DiskUsageTask for IGI_TOKEN on /storage/igi started ...
04:26:51.455 - INFO [pool-6-thread-1] - DiskUsageTask for IGI_TOKEN successfully ended in 0s with used-size = 4096 bytes
04:26:51.458 - INFO [pool-6-thread-1] - DiskUsageTask for TAPE_TOKEN on /storage/tape started ...
04:26:51.624 - INFO [pool-6-thread-1] - DiskUsageTask for TAPE_TOKEN successfully ended in 0s with used-size = 8286 bytes
04:26:51.625 - INFO [pool-6-thread-1] - DiskUsageTask for TESTVOBIS_TOKEN on /storage/test.vo.bis started ...
04:26:51.784 - INFO [pool-6-thread-1] - DiskUsageTask for TESTVOBIS_TOKEN successfully ended in 0s with used-size = 4096 bytes
04:26:51.784 - INFO [pool-6-thread-1] - DiskUsageTask for NOAUTH_TOKEN on /storage/noauth started ...
04:26:51.857 - INFO [pool-6-thread-1] - DiskUsageTask for NOAUTH_TOKEN successfully ended in 0s with used-size = 4096 bytes
04:26:51.858 - INFO [pool-6-thread-1] - DiskUsageTask for TESTVO2_TOKEN on /storage/test.vo.2 started ...
04:26:51.993 - INFO [pool-6-thread-1] - DiskUsageTask for TESTVO2_TOKEN successfully ended in 0s with used-size = 4096 bytes
04:26:51.994 - INFO [pool-6-thread-1] - DiskUsageTask for TESTVO_TOKEN on /storage/test.vo started ...
04:26:52.100 - INFO [pool-6-thread-1] - DiskUsageTask for TESTVO_TOKEN successfully ended in 0s with used-size = 4108 bytes
```