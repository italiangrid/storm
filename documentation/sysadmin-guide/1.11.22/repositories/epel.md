### EPEL repositories <a name="epelrepos">&nbsp;</a>

StoRM depends on [EPEL][epel-repo] repositories.

Install them as follows:

<div role="tabpanel">
  <ul class="nav nav-tabs" role="tablist">
    <li class="active"><a href="#epel_rhel7" role="tab" data-toggle="tab">CentOS 7</a></li>
    <li><a href="#epel_rhel6" role="tab" data-toggle="tab">CentOS 6</a></li>
  </ul>

  <div class="tab-content">
    <div class="tab-pane active" id="epel_rhel7">
        <pre><code class="language-bash" data-lang="bash">yum localinstall https://dl.fedoraproject.org/pub/epel/7/x86_64/Packages/e/epel-release-7-12.noarch.rpm</code></pre>
    </div>
    <div class="tab-pane" id="epel_rhel6">
        <pre><code class="language-bash" data-lang="bash">yum localinstall https://dl.fedoraproject.org/pub/epel/6/x86_64/Packages/e/epel-release-6-8.noarch.rpm</code></pre>
    </div>
  </div>
</div>



[epel-repo]: https://fedoraproject.org/wiki/EPEL/it