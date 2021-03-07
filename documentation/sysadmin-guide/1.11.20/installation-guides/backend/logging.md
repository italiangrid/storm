## Logging

The Backend log files provide information on the execution process of all SRM requests. All the Backend log files are placed in the _/var/log/storm_ directory. Backend logging is based on *logback* framework. Logback provides a way to set the level of verbosity depending on the use case. The level supported are FATAL, ERROR, INFO, WARN, DEBUG.

The file

    /etc/storm/backend-server/logging.xml

contains the following information:

```xml
<logger name="it.grid.storm" additivity="false">
    <level value="INFO" />
    <appender-ref ref="PROCESS" />
</logger>
```

the *value* can be set to the desired log level. Please be careful, because logging operations can impact on system performance (even 30% slower with DEBUG in the worst case). The suggest logging level for production endpoint is INFO. In case the log level is modified, the Backend has not to be restarted to read the new value.

StoRM Backend log files are the followings:

* _storm-backend.log_, the main log file with each single request and errors are logged;
* _heartbeat.log_, an aggregated log that shows the number of synch and asynch requests occoured from startup and on last minute;
* _storm-backend-metrics.log_, a finer grained monitoring of incoming synchronous requests, contains metrics for individual types of synchronous requests.   

### The storm-backend.log file

The main Backend service log file is:

- _storm-backend.log_. All the information about the SRM execution process, error or warning are logged here depending on the log level. At startup time, the BE logs here all the storm.properties value, this can be useful to check value effectively used by the system. After that, the BE logs the result of the namespace initialization, reporting errors or misconfiguration. At the INFO level, the BE logs for each SRM operation at least who have request the operation (DN and FQANs), on which files (SURLs) and the operation result. At DEBUG level, much more information are printed regarding the status of many StoRM internal component, depending on the SRM request type. DEBUG level has to be used carefully only for troubleshooting operation. If ERROR or FATAL level are used, the only event logged in the file are due to error condition.

### The heartbeat.log file

StoRM provides a bookkeeping framework that elaborates informations on SRM requests processed by the system to provide user-friendly aggregated data that can be used to get a quick view on system health.

- _heartbeat.log_. This useful file contains information on the SRM requests process by the system from its startup, adding new information at each beat. The beat time interval can be configured, by default is 60 seconds. At each beat, the heartbeat component logs an entry.

A _heartbeat.log_ entry example:

```bash
    [#.....71 lifetime=1:10.01]
        Heap Free:59123488 SYNCH [500] ASynch [PTG:2450 PTP:3422]
        Last:( [#PTG=10 OK=10 M.Dur.=150] [#PTP=5 OK=5 M.Dur.=300] )
```

|   Log     |   Meaning     |
|:----------|:--------------|
| `#......71`            | Log entry number
| `lifetime=1:10.01`     | Lifetime from last startup, hh:mm:ss
| `Heap Free:59123488`   | BE Process free heap size in Bytes
| `SYNCH [500]`          | Number of Synchronous SRM requests executed in the last beat
| `ASynch [PTG:2450 PTP:3422]` | Number of _srmPrepareToGet_ and _srmPrepareToPut_ requests executed from start-up.
| `Last:( [#PTG=10 OK=10 M.Dur.=150]` | Number of _srmPrepareToGet_ executed in the last beat, with the number of request terminated with success (OK=10) and average time in millisecond (M.Dur.=150)
| `[#PTP=5 OK=5 M.Dur.=300]` | Number of srmPrepareToPut executed in the last beat, with number of request terminated with success and average time in milliseconds.

This log information can be really useful to gain a global view on the overall system status. A tail on this file is the first thing to do if you want to check the health of your StoRM installation. From here you can understand if the system is receiving SRM requests or if the system is overloaded by SRM request or if PtG and PtP are running without problem or if the interaction with the filesystem is exceptionally low (in case the M.Dur. is much more than usual).

### The storm-backend-metrics.log file

A finer grained monitoring of incoming synchronous requests is provided by this log file. It contains metrics for individual types of synchronous requests.

A _storm-backend-metrics.log_ entry example:

```bash
16:57:03.109 - synch.ls [(m1_count=286, count=21136) (max=123.98375399999999, min=4.299131, mean=9.130859862802883, p95=20.736006, p99=48.147704999999995) (m1_rate=4.469984951030006, mean_rate=0.07548032009470132)] duration_units=milliseconds, rate_units=events/second
```

|   Log                    |   Meaning           |
|:-------------------------|:--------------------|
| `synch.ls`               | Type of operation.
| `m1_count=286`           | Number of operation of the last minute.
| `count=21136`            | Number of operations from last startup.
| `max=123.98375399999999` | Maximum duration of last bunch.
| `min=4.299131`           | Minimum duration of last bunch.
| `mean=9.130859862802883` | Duration average of last bunch
| `p95=20.736006`          | The 95% of last bunch operations lasted less then 20.73ms
| `p99=48.147704999999995` | The 99% of last bunch operations lasted less then 48.14ms

Here is the list of current logged operations:

|   Operation          |   Description       |
|:---------------------|:--------------------|
| `synch`              | Synch operations summary
| `synch.af`           | Synch srmAbortFiles operations
| `synch.ar`           | Synch srmAbortRequest operations
| `synch.efl`          | Synch srmExtendFileLifetime operations
| `synch.gsm`          | Synch srmGetSpaceMetadata operations
| `synch.gst`          | Synch srmGetSpaceToken operations
| `synch.ls`           | Synch srmLs operations
| `synch.mkdir`        | Synch srmMkDir operations
| `synch.mv`           | Synch srmMv operations
| `synch.pd`           | Synch srmPd operations
| `synch.ping`         | Synch srmPing operations
| `synch.rf`           | Synch srmRf operations
| `synch.rm`           | Synch srmRm operations
| `synch.rmDir`        | Synch srmRmDir operations
| `fs.aclOp`           | Acl set/unset on filesystem operations
| `fs.fileAttributeOp` | File attribute set/unset on filesystem operations
| `ea`                 | Extended attributes operations

