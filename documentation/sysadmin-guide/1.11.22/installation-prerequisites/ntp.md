### NTP service <a name="ntpservice">&nbsp;</a>

NTP service must be installed.

<div role="tabpanel">
  <ul class="nav nav-tabs" role="tablist">
    <li class="active"><a href="#ntp_rhel7" role="tab" data-toggle="tab">CentOS 7</a></li>
    <li><a href="#ntp_rhel6" role="tab" data-toggle="tab">CentOS 6</a></li>
    <li><a href="#ntp_puppet" role="tab" data-toggle="tab">Puppet</a></li>
  </ul>

  <div class="tab-content">
    <div class="tab-pane active" id="ntp_rhel7">
        <pre><code class="language-bash" data-lang="bash">yum install ntp
systemctl enable ntpd
systemctl start ntpd</code></pre>
    </div>
    <div class="tab-pane" id="ntp_rhel6">
        <pre><code class="language-bash" data-lang="bash">yum install ntp
chkconfig ntpd on
service ntpd start</code></pre>
    </div>
    <div class="tab-pane" id="ntp_puppet">
        <p>You can also use a Puppet module to install and configure NTP service. Install the NTP Puppet module:</p>
        <pre><code class="language-bash" data-lang="bash">puppet module install puppetlabs-ntp</code></pre>
        <p>And apply the following manifest.pp:</p>
        <pre><code class="language-bash" data-lang="puppet">include ntp</code></pre>
    </div>
  </div>
</div>
