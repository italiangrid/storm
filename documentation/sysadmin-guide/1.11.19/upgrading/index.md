---
layout: service-guide
title: StoRM System Administration Guide - Upgrade to StoRM 1.11.19
navigation:
  - link: documentation/sysadmin-guide/1.11.19/index.html
    label: Index
  - link: documentation/sysadmin-guide/1.11.19/upgrading/index.html
    label: Upgrading
---

## Upgrade to StoRM 1.11.19 <a name="upgrading">&nbsp;</a>

In case you're updating from **StoRM v1.11.18**, the services that needs to be updated are:

* _storm-backend-server_
* _storm-webdav_

You can directly update the involved packages and restart services:

<div role="tabpanel">
  <ul class="nav nav-tabs" role="tablist">
    <li class="active"><a href="#update_rhel7" role="tab" data-toggle="tab">CentOS 7</a></li>
    <li><a href="#update_rhel6" role="tab" data-toggle="tab">CentOS 6</a></li>
  </ul>

  <div class="tab-content">
    <div class="tab-pane active" id="update_rhel7">
        <pre><code class="language-bash" data-lang="bash">yum install storm-backend-server storm-webdav
systemctl restart storm-backend-server storm-webdav</code></pre>
    </div>
    <div class="tab-pane" id="update_rhel6">
        <pre><code class="language-bash" data-lang="bash">yum install storm-backend-server storm-webdav
service storm-backend-server restart
service storm-webdav restart</code></pre>
    </div>
  </div>
</div>

Split this command properly if you have a distributed deployment.

If you are upgrading from **StoRM v1.11.17** (or earlier versions) on CentOS 6 please follow
[these instructions][upgrade-17] before.

[upgrade-17]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.18/#upgrading