### UMD Repositories <a name="umdrepos">&nbsp;</a>

StoRM depends on UMD repositories.

Install UMD pgp key:

```
rpm --import http://repository.egi.eu/sw/production/umd/UMD-RPM-PGP-KEY
```

Install latest UMD-4 repository:

<div role="tabpanel">
  <ul class="nav nav-tabs" role="tablist">
    <li class="active"><a href="#umd_rhel7" role="tab" data-toggle="tab">CentOS 7</a></li>
    <li><a href="#umd_rhel6" role="tab" data-toggle="tab">CentOS 6</a></li>
  </ul>

  <div class="tab-content">
    <div class="tab-pane active" id="umd_rhel7">
        <pre><code class="language-bash" data-lang="bash">yum localinstall http://repository.egi.eu/sw/production/umd/4/centos7/x86_64/updates/umd-release-4.1.3-1.el7.centos.noarch.rpm</code></pre>
    </div>
    <div class="tab-pane" id="umd_rhel6">
        <pre><code class="language-bash" data-lang="bash">yum localinstall http://repository.egi.eu/sw/production/umd/4/sl6/x86_64/updates/umd-release-4.1.3-1.el6.noarch.rpm</code></pre>
    </div>
  </div>
</div>

More information about UMD installation can be found [here][UMD-instructions].

[UMD-instructions]: http://repository.egi.eu/category/umd_releases/distribution/umd-4/
