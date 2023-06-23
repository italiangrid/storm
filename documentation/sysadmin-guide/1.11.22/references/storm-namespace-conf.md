---
layout: service-guide
title: StoRM Backend namespace XML full reference
---

# StoRM Backend namespace XML full reference

Information about storage managed by StoRM is stored in a configuration file named `namespace.xml` located at `/etc/storm/backend-server/` on StoRM Backend host.
One of the information stored into this file is what is needed to perform the **mapping functionality**.

## The _mapping functionality_

The *mapping functionality* is the process of retrieving or building the transport URL (TURL) of a file addressed by a Site URL (SURL) together with grid user credential.
The following picture shows the different schema of SURL and TURL.

{% assign image_src="surl-turl-schema.png" %}
{% assign image_width="100%" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 3" %}
{% assign label_description="Site URL and Transfer URL schema." %}
{% include documentation/label.html %}

A couple of quick concepts from SRM:

- The SURL is the logical identifier for a local data entity
- Data access and data transfer are made through the TURLs
- The TURL identify a physical location of a replica
- SRM services retrieve the TURL from a namespace database (like DPNS component in DPM) or build it through other mechanisms (like StoRM)

In StoRM, the mapping functionality is provided by the namespace component (NS).

- The Namespace component works without a database.
- The Namespace component is based on an XML configuration.
- It relies on the physical storage structure.

The basic features of the namespace component are:

- The configuration is modular and structured (representation is based on XML)
- An efficient structure of namespace configuration lives in memory.
- No access to disk or database is performed
- The loading and the parsing of the configuration file occurs:
    * at start-up of the back-end service
    * when configuration file is modified


StoRM is different from the other solution, where typically, for every SRM request a query to the data base have to be done in order to establish the physical location of file and build the correct transfer URL.
The namespace functions relies on two kind of parameters for mapping operations derived from the SRM requests, that are:

- the grid user credential (a subject or a service acting on behalf of the subject)
- the SURL

The Fig.4 shows the main concepts of Namespace Component:

- *NS-Filesystem*: is the representation of a Storage Area
- *Mapping rule*: represents the basic rule for the mapping functionalities
- *Approachable rule*: represents the coarse grain access control to the Storage Area.

{% assign image_src="namespace-structure.png" %}
{% assign image_width="80%" %}
{% include documentation/image.html %}
{% assign label_title="Fig. 4" %}
{% assign label_description="Namespace structure." %}
{% include documentation/label.html %}

This is and example of the FS element:

```xml
    <filesystem name="dteam-FS" fs_type="ext3">
        <space-token-description>DTEAM_TOKEN</space-token-description>
        <root>/storage/dteam</root>
        <filesystem-driver>it.grid.storm.filesystem.swig.posixfs</filesystem-driver>
        <spacesystem-driver>it.grid.storm.filesystem.MockSpaceSystem</spacesystem-driver>
        <storage-area-authz>
            <fixed>permit-all</fixed>
        </storage-area-authz>
        <properties>
            <RetentionPolicy>replica</RetentionPolicy>
            <AccessLatency>online</AccessLatency>
            <ExpirationMode>neverExpire</ExpirationMode>
            <TotalOnlineSize unit="GB" limited-size="true">291</TotalOnlineSize>
            <TotalNearlineSize unit="GB">0</TotalNearlineSize>
        </properties>
        <capabilities>
            <aclMode>AoT</aclMode>
            <default-acl>
                <acl-entry>
                    <groupName>lhcb</groupName>
                    <permissions>RW</permissions>
                </acl-entry>
            </default-acl>
            <trans-prot>
                <prot name="file">
                    <schema>file</schema>
                </prot>
                <prot name="gsiftp">
                    <id>0</id>
                    <schema>gsiftp</schema>
                    <host>gsiftp-dteam-01.cnaf.infn.it</host>
                    <port>2811</port>
                </prot>
                <prot name="gsiftp">
                    <id>1</id>
                    <schema>gsiftp</schema>
                    <host>gsiftp-dteam-02.cnaf.infn.it</host>
                    <port>2811</port>
                </prot>
                <prot name="rfio">
                    <schema>rfio</schema>
                    <host>rfio-dteam.cnaf.infn.it</host>
                    <port>5001</port>
                </prot>
                <prot name="root">
                    <schema>root</schema>
                    <host>root-dteam.cnaf.infn.it</host>
                    <port>1094</port>
                </prot>
            </trans-prot>
            <pool>
                <balance-strategy>round-robin</balance-strategy>
                <members>
                    <member member-id="0"></member>
                    <member member-id="1"></member>
                </members>
            </pool>
        </capabilities>
        <defaults-values>
            <space lifetime="86400" type="volatile" guarsize="291" totalsize="291"/>
            <file lifetime="3600" type="volatile"/>
        </defaults-values>
    </filesystem>
```

Attributes meaning:

- ```<filesystem name="dteam-FS" fs_type="ext3">``` : The name is the element identifier. It identifies this Storage Area in the namespace domains. The *fs\_type* is the type of the filesystem the Storage Area is built on. Possible values are: *ext3*, *gpfs*. Please note that *ext3* stands for all generic POSIX filesystem (*ext3*, *Lustre*, etc.)
- ```<space-token-description>DTEAM_TOKEN</space-token-description>``` : Storage Area space token description.
- ```<root>/storage/dteam</root>``` : Physical root directory of the Storage Area on the file system.
- ```<filesystem-driver>it.grid.storm.filesystem.swig.posixfs</filesystem-driver>``` : Driver loaded by the Backend for filesystem interaction. This driver is used mainly to set up ACLs on space and files.
- ```<spacesystem-driver>it.grid.storm.filesystem.MockSpaceSystem</spacesystem-driver>``` Driver loaded by the Backend for filesystem interaction. This is driver is used to manage space allocation. (E.g. on GPFS it uses the _gpfs_prealloc()_ call).

## Storage Area properties

```xml
    <properties>
        <RetentionPolicy>replica</RetentionPolicy>
        <AccessLatency>online</AccessLatency>
        <ExpirationMode>neverExpire</ExpirationMode>
        <TotalOnlineSize unit="GB" limited-size="true">291</TotalOnlineSize>
        <TotalNearlineSize unit="GB">0</TotalNearlineSize>
    </properties>
```

in details:

- ```<RetentionPolicy>replica</RetentionPolicy>``` : Retention Policy of the Storage Area. Possible values are: *replica*, *custodial*.
- ```<AccessLatency>online</AccessLatency>``` : Access Latency of the Storage Area. Possible values: *online*, *nearline*.
- ```<ExpirationMode>neverExpire</ExpirationMode>``` : Expiration Mode of the Storage Area. **Deprecated**.
- ```<TotalOnlineSize unit="GB" limited-size="true">291</TotalOnlineSize>``` Total on-line size of the Storage Area in GigaBytes. In case the attribute *limited-size*="true", StoRM enforce this limit at SRM level. When the space used for the Storage Area is at least equal to the size specified, every further SRM request to write files will fail with SRM_NO_FREE_SPACE error code.
- ```<TotalNearlineSize unit="GB">0</TotalNearlineSize>``` : Total near-line size of the Storage Area. This only means in case the Storage Area is in some way attached to a MSS storage system (such as TSM with GPFS).

## Storage area capabilities

```xml
    <aclMode>AoT</aclMode>
```

This is the ACL enforcing approach. Possible values are: *AoT*, *JiT*. In case of *AheadOfTime*(**AoT**) approach StoRM sets up a physical ACL on file and directories for the local group (*gid*) in which the user is mapped. (The mapping is done querying the LCMAPS service con the BE machine passing both user DN and FQANs). The group ACL remains for the whole lifetime of the file. In case of *JustInTime*(**JiT**) approach StoRM sets up and ACL for the local user (*uid*) the user is mapped. The ACL remains in place only for the lifetime of the SRM request, then StoRM removes it. (This is to avoid to grant access to pool account uid in case of reallocation on different users.)

```xml
    <default-acl>
        <acl-entry>
            <groupName>lhcb</groupName>
            <permissions>RW</permissions>
        </acl-entry>
    </default-acl>
```

This is the Default ACL list. A list of ACL entry (that specify a local user (*uid*) or group id (*gid*) and a permission (R,W,RW). This ACL are automatically by StoRM at each read or write request. Useful for use cases where experiment want to allow local access to file on group different than the one that made the SRM request operation.

## Access and Transfer protocol supported

The ```file``` protocol:

```xml
    <prot name="file">
        <schema>file</schema>
    </prot>
```

The **file** protocol means the capability to perform local access on file and directory. If user performs an SRM request (*srmPtG* or *srmPtP*) specifying the file protocol, and it is supported by the selected Storage Area, StoRM return a TURL structured as:

    file:///atlas/atlasmcdisk/filename

This TURL can be used through GFAL or other SRM clients to perform a direct access on the file.

```xml
    <prot name="gsiftp">
        <id>0</id>
        <schema>gsiftp</schema>
        <host>gridftp-dteam.cnaf.infn.it</host>
        <port>2811</port>
    </prot>
```

The ```gsiftp``` protocol:

The **gsiftp** protocol means the GridFTP transfer system from Globus widely adopted in many Grid environments. This capability element contains all the information about the GridFTP server to use with this Storage Area. Site administrator can decide to have different server (or pools of server) for different Storage Areas. The *id* is the server identifier to be used when defining a pool. The *schema* have to be gsiftp. *host* is the hostname of the server (or the DNS alias used to aggregate more than one server). The *port* is the GridFTP server port, typically 2811. If user performs an SRM request (*srmPtG* or *srmPtP*) specifying the *gsiftp* protocol, and it is supported by the selected Storage Area, StoRM return a TURL structured as:

    gsiftp://gridftp-dteam.cnaf.infn.it:2811/atlas/atlasmcdisk/filename.

The ```rfio``` protocol:

```xml
    <prot name="rfio">
        <schema>rfio</schema>
        <host>rfio-dteam.cnaf.infn.it</host>
        <port>5001</port>
    </prot>
```

This capability element contains all the information about the **rfio** server to use with this Storage Area. Like for GridFTP, site administrator can decide to have different server (or pools of server) for different Storage Areas. The *id* is the server identifier. The *schema* have to be rfio. *host* is the hostname of the server (or the DNS alias used to aggregate more than one server). The *port* is the rfio server port, typically 2811. If user performs an SRM request (*srmPtG* or *srmPtP*) specifying the rfio protocol, and it is supported by the selected Storage Area, StoRM return a TURL structured as:

    rfio://rfio-dteam.cnaf.infn.it:5001/atlas/atlasmcdisk/filename.

The ```root``` protocol:

```xml
    <prot name="root">
        <schema>root</schema>
        <host>root-dteam.cnaf.infn.it</host>
        <port>1094</port>
    </prot>
```

This capability element contains all the information about the **root** server to use with this Storage Area. Like for other protocols, site administrator can decide to have different server (or pools of server) for different Storage Areas. The *id* is the server identifier. The *schema* have to be root. *host* is the hostname of the server (or the DNS alias used to aggregate more than one server). The *port* is the root server port, typically 1094. If user performs an SRM request (*srmPtG* or *srmPtP*) specifying the root protocol, and it is supported by the selected Storage Area, StoRM return a TURL structured as:

    root://root-dteam.cnaf.infn.it:1094/atlas/atlasmcdisk/filename.

## Pool of protocol servers

```xml
    <pool>
        <balance-strategy>round-robin</balance-strategy>
        <members>
            <member member-id="0"></member>
            <member member-id="1"></member>
        </members>
    </pool>
```

Here is defined a *pool of protocol servers*. Within the pool element pool *members* are declared identified by their *id*, the list of members have to be homogenious with respect to their schema. This id is the server identifier specified in the *prot* element. The *balance-strategy* represent the load balancing strategy with which the pool has to be managed. Possible values are: *round-robin*, *smart-rr*, *random* and *weight*.
<br/>
**NOTE**: Protocol server pooling is currently available only for gsiftp servers.
<br/>
Load balancing strategies details:

* *round-robin* At each TURL construction request the strategy returns the next server following the round-robin approach: a circular list with an index starting from the head and incrementd at each request.

* *smart-rr* An enhanced version of *round-robin*. The status of pool members is monitored and maintained in a cache. Cache entries has a validity life time that is refreshed when expired. If the member chosen by *round-robin* is marked as not responsive another iteration of *round-robin* is performed.

* *random* At each TURL construction request the strategy returns a random member of the pool.

* *weight* An enhanced version of *round-robin*. When a server is chosen the list index will not be moved forward (and the server will be choosen again in next request) for as many times as specified in its *weight*.

**NOTE**: The weight has to be specified in a *weight* element inside the member element:

```xml
    <pool>
        <balance-strategy>WEIGHT</balance-strategy>
        <members>
            <member member-id="0">
                <weight>5</weight>
            </member>
            <member member-id="1">
                <weight>1</weight>
            </member>
        </members>
    </pool>
```

#### Storage space information default values

```xml
    <defaults-values>
        <space lifetime="86400" type="volatile" guarsize="291" totalsize="291"/>
        <file lifetime="3600" type="volatile"/>
    </defaults-values>
```

## Mapping rules

A **mapping rule** define how a certain NS-Filesystem, that correspond to a Storage Area in SRM meaning of terms, is exposed in Grid:

```xml
    <mapping-rules>
        <map-rule name="dteam-maprule">
            <stfn-root>/dteam</stfn-root>
            <mapped-fs>dteam-FS</mapped-fs>
        </map-rule>
    </mapping-rules>
```

The ```<stfn-root>``` is the path used to build SURL referring to that Storage Area. The mapping rule above define that the NS-Filesystem named *dteam-FS* has to be mapped in the */dteam* SURL path. Following the NS-Filesystem element defined in the previous section, the SURL:

    srm://storm-fe.cr.cnaf.infn.it:8444/dteam/testfile

following the root expressed in the *dteam-FS* NF-Filesystem element, is mapped in the physical root path on the file system:

    /storage/dteam

This approach works similar to an alias, from the SURL *stfn-root* path to the NS-Filesystem root.

## Approachable rules

**Approachable rules** defines which users (or which class of users) can approach a certain Storage Area, always expressed as NS-Filesystem element. If a user can approach a Storage Area, he can use it for all SRM operations. If a user is not allowed to approach a Storage Area, and he try to specify it in any SRM request, he will receive an SRM\_INVALID\_PATH. In practics, if a user cannot approach a Storage Area, for him that specific path does not exists at all.
Here is an example of approachable rule for the *dteam-FS* element:

```xml
    <approachable-rules>
        <app-rule name="dteam-rule">
            <subjects>
                <dn>*</dn>
                <vo-name>dteam</vo-name>
            </subjects>
            <approachable-fs>dteam-FS</approachable-fs>
            <space-rel-path>/</space-rel-path>
        </app-rule>
    </approachable-rules>
```

- `<dn>*</dn>` means that everybody can access the storage Area. Here you can define regular expression on DN fields to define more complex approachable rules.

- `<vo-name>dteam</vo-name>` means that only users belonging to the VO dteam will be allowed to access the Storage Area. This entry can be a list of comma separeted VO-name.

