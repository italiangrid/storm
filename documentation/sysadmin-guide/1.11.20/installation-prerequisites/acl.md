### ACL support <a name="acl">&nbsp;</a>

StoRM uses the ACLs on files and directories to implement the security model.
In so doing, StoRM uses the native access to the file system. Therefore in order to ensure a proper running, ACLs need to be enabled on the underlying file-system (sometimes they are enabled by default) and work properly.

If the _getfacl_ and _setfacl_ commands are not available on your host you have to install _acl_ package:

```shell
yum install acl
```

To check if all works properly, try to set an acl to a test file as follow:

```shell
touch test
setfacl -m u:storm:rw test
```

**Note**: _storm_ user **must** exist.

```shell
getfacl test
```

Should return the following values:

```
# file: test
# owner: root
# group: root
user::rw-
user:storm:rw-
group::r--
mask::rw-
other::r--
```

To enable ACLs (if needed), you must add the acl property to the relevant file system in your `/etc/fstab` file.
For example:

```
/dev/hda3     /storage      ext3     defaults, acl     1 2
```

Then you need to remount the affected partitions as follows:

```shell
mount -o remount /storage
```

This is valid for different file system types (i.e., ext3, xfs, gpfs and others).