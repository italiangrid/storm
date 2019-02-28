---
layout: howto
title: StoRM Storage Resource Manager - How-to kill StoRM backend process
---

## How-to force kill StoRM backend process

If the restart command:

```
service storm-backend-server restart
```

keeps failing for some reason, and backend process is stuck, you can force the
kill of `storm-backend-server` process as follow:

```shell
# get the process-ids:
pslist=$( ps -ef | grep java | grep storm-backend-server | awk '{print $2}' | tr '\n' ' ' | sed -e s/\ $// )
# kill all:
kill -9 $pslist
```

Then you can re-start it:

```shell
/sbin/service storm-backend-server start
```
