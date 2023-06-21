### Storage Area's permissions <a name="sapermissions">&nbsp;</a>

All the Storage Areas managed by StoRM needs to be owned by *storm* user.
This means that, for example, the storage-area *test* root directory permissions 
must be:

    drwxr-x---+  2 storm storm

The site administrator has to take care of it. To set the correct permissions
on a storage area, you can launch the following commands
(assuming that storm runs as user *storm*, which is the default):

    chown -RL storm:storm <sa-root-directory>
    chmod -R 750 <sa-root-directory>

Site administrator must also make traversable by other users the parent
directories of each storage-area root directory (that's usually the same
directory for all the storage-areas):

    chmod o+x <sa-root-directory-parent>
