### Extended Attribute support <a name="attr">&nbsp;</a>

StoRM uses the Extended Attributes (EA) on files to store some metadata related to the file (e.g. the checksum value); therefore in order to ensure a proper running, the EA support needs to be enabled on the underlying file system and work properly.

If the _getfattr_ and _setfattrl_ commands are not available on your host, install `attr` package:

```
yum install attr
```

To check if all properly works, try to set an extendend attribute to a test file:

```
touch testfile
setfattr -n user.testea -v test testfile
getfattr -d testfile
```

It should return:

```
# file: testfile
user.testea="test"
```

To enable EA (if needed) you must add the `user_xattr` property to the relevant file systems in your `/etc/fstab` file.
For example:

```
/dev/hda3     /storage     ext3     defaults,acl,user_xattr     1 2
```

Then you need to remount the affected partitions as follows:

```
mount -o remount /storage
```