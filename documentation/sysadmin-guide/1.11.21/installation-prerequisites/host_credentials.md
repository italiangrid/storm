### Host credentials <a name="x509host">&nbsp;</a>

Hosts participating to the StoRM-SE which run services such as StoRM Frontend, StoRM Backend, StoRM WebDAV or StoRM Globus GridFTP must be configured with X.509 certificates signed by a trusted Certification Authority (CA).

Usually, the **hostcert.pem** and **hostkey.pem** certificate and private key are located in the `/etc/grid-security` directory. They must have permission 0644 and 0400 respectively:

```bash
ls -l /etc/grid-security/hostkey.pem
-r-------- 1 root root 887 Mar  1 17:08 /etc/grid-security/hostkey.pem

ls -l /etc/grid-security/hostcert.pem
-rw-r--r-- 1 root root 1440 Mar  1 17:08 /etc/grid-security/hostcert.pem
```

Check if your certificate is expired as follow:

```bash
openssl x509 -checkend 0 -in /etc/grid-security/hostcert.pem
```

To change permissions, if necessary:

```bash
chmod 0400 /etc/grid-security/hostkey.pem
chmod 0644 /etc/grid-security/hostcert.pem
```