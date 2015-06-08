---
layout: howto
title: StoRM Storage Resource Manager - Example of how to avoid a specific user to access a storage-area using Argus
version: 1.11.4
---

### Example of how to avoid a specific user to access a storage-area using Argus authorization system.

_**Components installed**_: <span class="label label-important">StoRM Backend</span> <span class="label label-info">StoRM Frontend</span> <span class="label">StoRM GridFTP</span> <span class="label label-warning">Argus</span>

#### Prerequisites:

You need:

- basic StoRM services installated (see this [example][standalone])
- an Argus authorization system installed (see [Argus twiki][argustwiki])

#### StoRM configuration

In order to create and manage a list of banned users, StoRM can be configured to use ARGUS authorization system.
The Argus authorization service allows administrators to define policies that answer questions like _Can user X perform action Y on resource Z at this time?_ See [Argus twiki](https://twiki.cern.ch/twiki/bin/view/EGEE/AuthZIntro) to get more information about this framework.
StoRM doesn't integrate all the services provided by Argus. It allows only to define a list of banned users. 

Supposing you have installed StoRM on a single host (as seen in this [example][standalone]), modify your *storm.def* YAIM site configuration file to integrate ARGUS service. It's necessary to instruct the Frontend to communicate with Argus PEP server.
First of all we need to tell YAIM that you want to use ARGUS:

	USE_ARGUS = yes

and specify the complete service endpoint of Argus PEP server:

	ARGUS_PEPD_ENDPOINTS = "https://<ARGUS-service-hostname>:8154/authz"

Then you have to choose an entity identifier for StoRM service (it's a string that represents the entity-id that you'll use for each StoRM's Argus policy):

	STORM_PEPC_RESOURCEID = "storm"

and enable Frontend's user blacklisting with:

	STORM_FE_USER_BLACKLISTING = true

The StoRM configuration is completed. But before running YAIM configuration it's necessary to define at least a policy for every VO that your StoRM instance supports. To do this, we have to add some valid policies. We can write a file in [Simplified Policy Language][SPLguide] and then import it or we can use the [pap-admin CLI][pap_admin_CLI] directly.
For example, if your StoRM instance supports ```testers.eu-emi.eu``` and ```dteam``` VOs, you have to write the following policies:

```bash
resource "storm" {
    obligation "http://glite.org/xacml/obligation/local-environment-map" {}
    action ".*" {
        rule permit { vo="testers.eu-emi.eu" }
    }
}
resource "storm" {
    obligation "http://glite.org/xacml/obligation/local-environment-map" {}
    action ".*" {
        rule permit { vo="dteam" }
    }
}
```
See [Simplified Policy Language guide][SPLguide] to learn how to write valid Argus policies.
If you have a storage area not owned by a VO but readable and writable with a particular x509 certificate (see [this example][X509_SA_conf_example]), you can add an ARGUS policy as follow:

```bash
resource "storm" {
    action ".*" {
        rule permit { subject-issuer="CN=Test CA,O=IGI,C=IT" }
    }
}
```
Finally, if you want to ban a particular user you can add a policy as follow:

```bash
resource ".*" {
    action ".*" {
        rule deny { subject="CN=Enrico Vianello, L=CNAF, OU=Personal Certificate, O=INFN, C=IT" }
    }
}
```

Save all these policies on a file and import it via pap-admin command ```add-policies-from-file```:

```bash
pap-admin add-policies-from-file <filepath>
```

This command append the contained policies so, if you want to replace the existing policies, you have to do a:

```bash
pap-admin remove-all-policies
```

before, and then import your file.
Clear the cache and reload the policies to finish:

```bash
service argus-pepd clearcache
service argus-pdp reloadpolicy
```

You can add your policies also by using only the pap-admin CLI. In our case you can launch:

```bash
pap-admin-rap
pap-admin add-policy --resource "storm" --action ".*" --obligation "http://glite.org/xacml/obligation/local-environment-map" permit vo="testers.eu-emi.eu"
pap-admin add-policy --resource "storm" --action ".*" permit subject-issuer="CN=Test CA,O=IGI,C=IT"
pap-admin ban subject "CN=Enrico Vianello, L=CNAF, OU=Personal Certificate, O=INFN, C=IT"
service argus-pepd clearcache
service argus-pdp reloadpolicy
```

Trying to do a srmLs with the banned certificate we get:

```bash
$ clientSRM ls -e omii005-vm01.cnaf.infn.it:8444 -s srm://omii005-vm01.cnaf.infn.it:8444/testers.eu-emi.eu/tmp.vKKnG12525
============================================================
Sending Ls request to: omii005-vm01.cnaf.infn.it:8444
Before execute:
Afer execute:
Request Status Code 3
Poll Flag 0
============================================================
Request status:
  statusCode="SRM_AUTHORIZATION_FAILURE"(3)
  explanation="Request authorization error: user is blacklisted."
============================================================
SRM Response:
============================================================
```


[standalone]: {{site.baseurl}}/documentation/examples/basic-storm-standalone-configuration/1.11.2/basic-storm-standalone-configuration.html
[argustwiki]: https://twiki.cern.ch/twiki/bin/view/EGEE/AuthorizationFramework