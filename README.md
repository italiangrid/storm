StoRM backend service
===============================

StoRM BackEnd provides a set of facilities and interfaces that allows to
build Grid Storage Element upon any POSIX filesystem granting direct file access, 
but taking advantage of special features of high performance parallel and
cluster file systems, as GPFS from IBM and Lustre from SUN.

## Build from source

### Supported platforms
* Scientific Linux 5 on x86_64 architecture
* Scientific Linux 6 on x86_64 architecture

### Build instructions

You need git to get the code.

Build command:
```bash
mvn compile
```

## Hacking StoRM

If you want contribute fork the repository and submit a 
[pull request](https://help.github.com/articles/using-pull-requests). 

A work in progress developer guide can be found in the [README](src/it/grid/storm/README.md) of the source code directories.

## Contact info

If you have problems, questions, ideas or suggestions, please contact us at
the following URLs

* GGUS (official support channel): http://www.ggus.eu

