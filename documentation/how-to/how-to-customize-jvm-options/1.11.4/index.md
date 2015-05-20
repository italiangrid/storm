---
layout: default
title: StoRM Storage Resource Manager - How to customize JVM options
version: 1.11.4
---

#### [Back to How-To]({{site.baseurl}}/documentation/examples/)

## Customizing JVM options

StoRM Backend service can be launched with customized options for the JVM.
There are a lot of command line options that can be specified when launching the service, as you can read from [Java HotSpot VM Options][jvmoptions].
StoRM administrators can tune JVM options for StoRM Backend server by changing the value of ```STORM_BE_JVM_OPTS``` variable contained into ```/etc/sysconfig/storm-backend-server``` file.
In the examples below, we will show how to:

1. increase/decrease java heap size limits
2. enable Garbage Collector log monitoring

### 1. Setting Java heap size limits

The Java heap (the "heap") is the part of the memory where blocks of memory are allocated to objects and freed during garbage collection.
The ```-Xms``` and ```-Xmx``` command line options could be used to customize the heap size, as your needed.

- ```-Xms``` option sets the initial and minimum Java heap size. Its value cannot exceed the value set for ```-Xmx```.
- ```-Xmx``` option sets the maximum size. ```-Xmx``` does not limit the total amount of memory that the JVM can use.

By default, StoRM Backend ```-Xms``` and ```-Xmx``` values are:

```bash
STORM_BE_JVM_OPTS='-server -Xms512m -Xmx512m'
```

Note: StoRM Backend is started in ```-server``` mode by default.

If you do not add a unit, you will get the exact value you state; for example, 64 will be interpreted as 64 bytes, not 64 megabytes or 64 kilobytes.
The parallel collector will throw an ```OutOfMemoryError``` if too much time is being spent in garbage collection: if more than 98% of the total time is spent in garbage collection and less than 2% of the heap is recovered, an ```OutOfMemoryError``` will be thrown. This feature is designed to prevent applications from running for an extended period of time while making little or no progress because the heap is too small. Increase ```-Xmx``` value to prevein ```OutOfMemoryError``` exceptions. For example:

```bash
STORM_BE_JVM_OPTS='-server -Xms512m -Xmx1024m'
```

To obtain the best performance, administrators should set ```-Xms``` to the same size as the maximum heap size.
Once configured, restart the Backend service.

### 2. Enable JVM Garbage Collector monitoring log.

If you want to monitor the behaviour of your Java Virtual Machine Garbage Collector, you can use several command line options. See [Diagnosing a Garbage Collection problem][debugging] section or the [Debugging Options][debuggingoptions] to get further information.
If you want to understand well how the JVM Garbage Collection works, see [Java SE 6 HotSpot Virtual Machine Garbage Collection Tuning][jse6_vm_gc_tuning].

For example, we can enable and redirect its logging messages to a file by using the ```-Xloggc``` option as follow:

```bash
-Xloggc:/var/log/storm/gc.log
```

To increase the log verbosity add also:

```bash
-XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps
```

You should now have the ```STORM_BE_JVM_OPTS``` variable initialized as follow:

```bash
STORM_BE_JVM_OPTS='-server -Xms512m -Xmx512m -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:/var/log/storm/gc.log'
```

Once configured, restart the Backend service.

File ```/var/log/storm/gc.log``` contains rows as follow:

	2014-04-10T11:23:56.176+0200: 732.257: [GC [PSYoungGen: 118096K->39648K(114816K)] 425882K->373402K(464384K), 0.0742970 secs] [Times: user=0.22 sys=0.07, real=0.08 secs] 
	2014-04-10T11:23:56.250+0200: 732.332: [Full GC [PSYoungGen: 39648K->0K(114816K)] [ParOldGen: 333754K->81604K(349568K)] 373402K->81604K(464384K) [PSPermGen: 25622K->25622K(25664K)], 0.2782990 secs] [Times: user=0.94 sys=0.01, real=0.28 secs] 


##### Minor GC values meaning:

_GC [PSYoungGen: **A**K->**B**K(**C**K)] **D**K->**E**K(**F**K), 0.0218070 secs] [Times: user=0.03 sys=0.01, real=0.02 secs]_

- A: occupancy of the young generation before the gc
- B: occupancy of the young generation after the garbage collection (i.e. size of one of the survivor space occupancy).
- C: size (not the occupancy) of the young generation space (i.e. total size of eden and survivor spaces)
- D: total heap utilization before garbage collection
- E: total heap utilization after garbage collection
- F: heap size (total size of young and old generation spaces)

Finally the time it took to do the GC.

##### Full GC values meaning:

Full GC collects objects in all the spaces. It first starts from the young generation, cleans dead object and moves live object in the old generation.
For this reason the occupancy of the young generation after a full gc is always zero. 

_Full GC [PSYoungGen: **A**K->0K(**C**K)] [ParOldGen: **D**K->**E**K(**F**K)] **J**K->**L**K(**M**K) [PSPermGen: 26539K->26489K(53312K)], 0.4307910 secs] [Times: user=0.15 sys=1.08, real=0.43 secs]_

- A: Occupancy of the young gen before gc
- C: Size of the young generation space
- D: Occupancy of the old generation before gc
- E: Occupancy of the old generation after gc
- F: Size of the old generation space
- J: Occupancy of the whole heap before gc
- L: Occupancy of the whole heap after gc
- M: Size of the heap

Similarly for the PermGen space.

To better understand Garbage Collector logging semantic and sintax, and to get further information, see [Diagnosing a Garbage Collection problem][debugging].

[jse6_vm_gc_tuning]: http://www.oracle.com/technetwork/java/javase/gc-tuning-6-140523.html
[jvmoptions]: http://www.oracle.com/technetwork/java/javase/tech/vmoptions-jsp-140102.html
[debuggingoptions]: http://www.oracle.com/technetwork/java/javase/tech/vmoptions-jsp-140102.html#DebuggingOptions
[debugging]: http://www.oracle.com/technetwork/java/example-141412.html