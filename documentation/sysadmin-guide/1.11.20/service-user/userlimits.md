### User file limits <a name="userlimits">&nbsp;</a>

It's recommended to raise the number of open files for *storm* user.
Put these settings in */etc/security/limits.conf* or in a file contained in the
*/etc/security/limits.d* directory (recommended):

```
storm hard nofile 65535
storm soft nofile 65535
```

Edit the total amount of opened files as your needed.