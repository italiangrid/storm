### StoRM Repositories <a name="stormrepos">&nbsp;</a>

The latest certified StoRM packages can be found in the StoRM production repository:

- [Browse SL7 packages][stable-storm-sl7-repoview]
- [Browse SL6 packages][stable-storm-sl6-repoview]

Note that you should also have UMD repositories installed (as detailed above) for your setup to work as expected.

To install the StoRM production repository files, run the following commands (as root):

<div role="tabpanel">
  <ul class="nav nav-tabs" role="tablist">
    <li class="active"><a href="#storm_rhel7" role="tab" data-toggle="tab">CentOS 7</a></li>
    <li><a href="#storm_rhel6" role="tab" data-toggle="tab">CentOS 6</a></li>
  </ul>

  <div class="tab-content">
    <div class="tab-pane active" id="storm_rhel7">
        <pre><code class="language-bash" data-lang="bash">yum-config-manager --add-repo https://repo.cloud.cnaf.infn.it/repository/storm/storm-stable-centos7.repo</code></pre>
    </div>
    <div class="tab-pane" id="storm_rhel6">
        <pre><code class="language-bash" data-lang="bash">yum-config-manager --add-repo https://repo.cloud.cnaf.infn.it/repository/storm/storm-stable-centos6.repo</code></pre>
    </div>
  </div>
</div>

[stable-storm-sl6-repoview]: https://repo.cloud.cnaf.infn.it/service/rest/repository/browse/storm-rpm-stable/centos6/
[stable-storm-sl7-repoview]: https://repo.cloud.cnaf.infn.it/service/rest/repository/browse/storm-rpm-stable/centos7/