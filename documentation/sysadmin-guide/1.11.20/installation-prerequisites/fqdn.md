### FQDN Hostname <a name="fqdnhostname">&nbsp;</a>

Hostname must be a *Fully Qualified Domain Name* (FQDN).

To check if your hostname is a FQDN, run:

```
hostname -f
```

The command must return the host FQDN.

If you need to correct it and you are using bind or NIS for host lookups, you can change the FQDN and the DNS domain name, which is part of the FQDN, in the /etc/hosts file.

```
# Do not remove the following line, or various programs
# that require network functionality will fail.
127.0.0.1       MYHOSTNAME.MYDOMAIN MYHOSTNAME localhost.localdomain localhost
::1             localhost6.localdomain6 localhost6
```

Set your own MYHOSTNAME and MYDOMAIN and restart the network service:

```
service network restart
```