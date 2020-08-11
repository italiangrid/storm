---
layout: toc
title: StoRM Storage Resource Manager - Basic StoRM standalone installation and configuration on CentOS 7 with Puppet
---

# StoRM quick deploy on CentOS 7 with Puppet

The simplest example of a StoRM deployment can be done by installing all the components on a single host.

Assuming that:

- the host satisfies the [StoRM Installation Prerequisites][INSTALL-PREREQ]
- the StoRM repositories have been installed (see [repositories section][REPOSETTINGS]).

we will deploy all the services after preparing the node:

* [Prepare node](#node)
* [Install services](#services)

## Prepare node <a name="node">&nbsp;</a>

This part of the guide explains how to brief prepare a node to install all the StoRM services.
We are aware that there are a lot of ways to do these things and each site administratos knows what is better for his site.
This guide is ispired from how we test the clean deployment of our services.

### Host certificate and key

Be sure you have your host certificate and key stored at `/etc/grid-security`. Check also if your host certificate is not expired.

```
$ ls /etc/grid-security/hostcert.pem
$ ls /etc/grid-security/hostkey.pem
$ openssl x509 -in /etc/grid-security/hostcert.pem -noout -text
```

### Install Puppet

Install Puppet as follow:

```bash
rpm -Uvh https://yum.puppetlabs.com/puppet5/el/7/x86_64/puppet5-release-5.0.0-6.el7.noarch.rpm
yum install -y puppet
```

### Install Puppet modules

Install the needed puppet modules:

```bash
# ntp service
puppet module install puppetlabs-ntp
# fetch-crl and all ca certificates
puppet module install puppet-fetchcrl
# voms
puppet module install lcgdm-voms
# bdii
puppet module install cnafsd-bdii
# storm services and utils
puppet module install cnafsd-storm
```

The following puppet modules are not published on Puppet forge and are used for our test purposes:

```bash
# Install mwdevel puppet modules
git clone git://github.com/cnaf/ci-puppet-modules.git /ci-puppet-modules
cd /ci-puppet-modules/modules

cd mwdevel_umd_repo
puppet module build
puppet module install ./pkg/mwdevel-mwdevel_umd_repo-0.1.0.tar.gz
cd ..

cd mwdevel_test_vos2
puppet module build
puppet module install ./pkg/mwdevel-mwdevel_test_vos2-0.1.0.tar.gz
cd ..

cd mwdevel_test_ca
puppet module build
puppet module install ./pkg/mwdevel-mwdevel_test_ca-0.1.0.tar.gz
cd ..
```

### Setup node

Apply this **setup.pp**:

```puppet
include ntp
include fetchcrl

# install and configure dteam vo
include voms::dteam

# add storm and edguser users and groups
include storm::users

# create a pool account of 20 users for dteam vo 
class { 'storm::gridmap':
  gridmap_data => [{
    'vo' => 'dteam',
    'group' => 'dteam',
    'gid' => 7100,
    'pool_name' => 'dteam',
    'pool_size' => 20,
    'pool_base_uid' => 7100,
  }],
}

# storage root directory for all the storage areas
class { 'storm::storage':
  root_directories => [
    '/storage',
    '/storage/dteam',
  ],
}

# install bdii
class { 'bdii':
  firewall => false,
}

Class['storm::users']
-> Class['storm::storage']
-> Class['storm::gridmap']
```

```bash
puppet apply setup.pp
```

Example of output:

```bash
# puppet apply setup.pp 
Warning: The function 'hiera' is deprecated in favor of using 'lookup'. See https://puppet.com/docs/puppet/5.5/deprecated_language.html\n   (file & line not available)
Notice: Compiled catalog for backend-test.example.org in environment production in 1.34 seconds
Notice: /Stage[main]/Mwdevel_test_ca/File[test-ca.repo]/ensure: defined content as '{md5}8d369ad41819fe3a8eb4818e42fd4251'
Notice: /Stage[main]/Mwdevel_test_ca/Package[igi-test-ca]/ensure: created
Notice: /Stage[main]/Mwdevel_test_ca/Package[igi-test-ca-2]/ensure: created
Notice: /Stage[main]/Mwdevel_test_ca/Package[igi-test-ca-256]/ensure: created
Notice: /Stage[main]/Ntp::Config/File[/etc/ntp.conf]/content: content changed '{md5}94156d9b9af7106b7bded3fc4df4caff' to '{md5}41d80b5b9ca1c85d5cea2e40e9424c32'
Notice: /Stage[main]/Ntp::Config/File[/etc/ntp/step-tickers]/content: content changed '{md5}9b77b3b3eb41daf0b9abb8ed01c5499b' to '{md5}413c531d0533c4dba18b9acf7a29ad5d'
Notice: /Stage[main]/Ntp::Service/Service[ntp]: Triggered 'refresh' from 1 event
Notice: /Stage[main]/Fetchcrl::Install/Package[fetch-crl]/ensure: created
Notice: /Stage[main]/Fetchcrl::Install/File[/etc/pki/rpm-gpg/GPG-KEY-EUGridPMA-RPM-3]/ensure: defined content as '{md5}3cef52496793a9ae3f8de1b319e319fb'
Notice: /Stage[main]/Fetchcrl::Install/Yumrepo[carepo]/ensure: created
Notice: /Stage[main]/Fetchcrl::Install/Package[ca-policy-egi-core]/ensure: created
Notice: /Stage[main]/Fetchcrl::Config/File[/etc/fetch-crl.conf]/content: content changed '{md5}4bcf7c70f1801dd20939d874b5af2850' to '{md5}3c4bd382bb94fca04f1adcb47aa66051'
Notice: /Stage[main]/Fetchcrl::Service/Service[fetch-crl-cron]/ensure: ensure changed 'stopped' to 'running'
Notice: /Stage[main]/Bdii::Install/Package[bdii]/ensure: created
Notice: /Stage[main]/Bdii::Config/File[/etc/bdii/bdii-slapd.conf]/content: content changed '{md5}d683317b714e79a024473eafce1b0017' to '{md5}9c674c9e7dc0b54bcb1e5370c3e60001'
Notice: /Stage[main]/Bdii::Config/File[/etc/bdii/bdii-slapd.conf]/owner: owner changed 'ldap' to 'root'
Notice: /Stage[main]/Bdii::Config/File[/etc/bdii/bdii-slapd.conf]/group: group changed 'ldap' to 'root'
Notice: /Stage[main]/Bdii::Config/File[/etc/bdii/bdii-slapd.conf]/mode: mode changed '0640' to '0644'
Notice: /Stage[main]/Bdii::Config/File[/etc/bdii/gip]/ensure: created
Notice: /Stage[main]/Bdii::Config/File[/var/lib/bdii/db]/ensure: created
Notice: /Stage[main]/Bdii::Config/File[/etc/sysconfig/bdii]/content: content changed '{md5}0a97d89b46ea542c4edded3236d6348b' to '{md5}9f436d193ff9eb2c54f5672e1b8bca7e'
Notice: /Stage[main]/Bdii::Service/Service[bdii]/ensure: ensure changed 'stopped' to 'running'
Notice: /Stage[main]/Voms::Install/Package[voms-clients-cpp]/ensure: created
Notice: /Stage[main]/Voms::Install/File[/etc/vomses]/ensure: created
Notice: /Stage[main]/Voms::Dteam/Voms::Client[dteam]/File[/etc/grid-security/vomsdir/dteam]/ensure: created
Notice: /Stage[main]/Voms::Dteam/Voms::Client[dteam]/File[/etc/grid-security/vomsdir/dteam/voms2.hellasgrid.gr.lsc]/ensure: defined content as '{md5}22b78c95c2a52243a5a1d2f9a5c436ed'
Notice: /Stage[main]/Voms::Dteam/Voms::Client[dteam]/File[/etc/vomses/dteam-voms2.hellasgrid.gr]/ensure: defined content as '{md5}aa6fbde9d227cd1a5a9846beeeeaf886'
Notice: /Stage[main]/Storm::Users/Accounts::User[edguser]/Group[edguser]/ensure: created
Notice: /Stage[main]/Storm::Users/Accounts::User[storm]/Group[storm]/ensure: created
Notice: /Stage[main]/Storm::Users/Accounts::User[edguser]/User[edguser]/ensure: created
Notice: /Stage[main]/Storm::Users/Accounts::User[storm]/User[storm]/ensure: created
Notice: /Stage[main]/Storm::Users/Accounts::User[edguser]/Accounts::Home_dir[/home/edguser]/File[/home/edguser/.vim]/ensure: created
Notice: /Stage[main]/Storm::Users/Accounts::User[edguser]/Accounts::Key_management[edguser_key_management]/File[/home/edguser/.ssh]/ensure: created
Notice: /Stage[main]/Storm::Users/Accounts::User[edguser]/Accounts::Key_management[edguser_key_management]/File[/home/edguser/.ssh/authorized_keys]/ensure: created
Notice: /Stage[main]/Storm::Users/Accounts::User[storm]/Accounts::Home_dir[/home/storm]/File[/home/storm/.vim]/ensure: created
Notice: /Stage[main]/Storm::Users/Accounts::User[storm]/Accounts::Key_management[storm_key_management]/File[/home/storm/.ssh]/ensure: created
Notice: /Stage[main]/Storm::Users/Accounts::User[storm]/Accounts::Key_management[storm_key_management]/File[/home/storm/.ssh/authorized_keys]/ensure: created
Notice: /Stage[main]/Storm::Storage/Storm::Rootdir[/storage]/File[/storage]/ensure: created
Notice: /Stage[main]/Storm::Storage/Storm::Rootdir[/storage/dteam]/File[/storage/dteam]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/File[/etc/grid-security/gridmapdir]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/Group[dteam]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/User[dteam001]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/File[/etc/grid-security/gridmapdir/dteam001]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/User[dteam002]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/File[/etc/grid-security/gridmapdir/dteam002]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/User[dteam003]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/File[/etc/grid-security/gridmapdir/dteam003]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/User[dteam004]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/File[/etc/grid-security/gridmapdir/dteam004]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/User[dteam005]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/File[/etc/grid-security/gridmapdir/dteam005]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/User[dteam006]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/File[/etc/grid-security/gridmapdir/dteam006]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/User[dteam007]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/File[/etc/grid-security/gridmapdir/dteam007]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/User[dteam008]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/File[/etc/grid-security/gridmapdir/dteam008]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/User[dteam009]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/File[/etc/grid-security/gridmapdir/dteam009]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/User[dteam010]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/File[/etc/grid-security/gridmapdir/dteam010]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/User[dteam011]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/File[/etc/grid-security/gridmapdir/dteam011]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/User[dteam012]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/File[/etc/grid-security/gridmapdir/dteam012]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/User[dteam013]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/File[/etc/grid-security/gridmapdir/dteam013]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/User[dteam014]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/File[/etc/grid-security/gridmapdir/dteam014]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/User[dteam015]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/File[/etc/grid-security/gridmapdir/dteam015]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/User[dteam016]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/File[/etc/grid-security/gridmapdir/dteam016]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/User[dteam017]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/File[/etc/grid-security/gridmapdir/dteam017]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/User[dteam018]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/File[/etc/grid-security/gridmapdir/dteam018]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/User[dteam019]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/File[/etc/grid-security/gridmapdir/dteam019]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/User[dteam020]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/File[/etc/grid-security/gridmapdir/dteam020]/ensure: created
Notice: /Stage[main]/Storm::Gridmap/File[/etc/grid-security/grid-mapfile]/ensure: defined content as '{md5}a8f73514ad4afa17b73d9b6946a7130b'
Notice: /Stage[main]/Storm::Gridmap/File[/etc/grid-security/groupmapfile]/ensure: defined content as '{md5}e803b65038d6e66d9d7eb7fc4daccbad'
```

## Install services <a name="services">&nbsp;</a>

### Example of manifest.pp

The following configuration install and configure StoRM Backend, Frontend, GridFTP and WebDAV services on the same host.
One storage area for dteam VO is defined for Backend and WebDAV services. 

Example of **manifest.pp**:

```puppet
$host='backend-test.example.org'

Class['storm::backend']
-> Class['storm::frontend']
-> Class['storm::gridftp']
-> Class['storm::webdav']

class { 'storm::backend':
  hostname              => $host,
  mysql_server_install  => true,
  transfer_protocols    => ['file', 'gsiftp', 'webdav'],
  xmlrpc_security_token => 'NS4kYAZuR65XJCq',
  db_username           => 'storm',
  db_password           => 'storm',
  service_du_enabled    => true,
  srm_pool_members      => [
    {
      'hostname' => $host,
    }
  ],
  gsiftp_pool_members   => [
    {
      'hostname' => $host,
    },
  ],
  webdav_pool_members   => [
    {
      'hostname' => $host,
    },
  ],
  storage_areas         => [
    {
      'name'          => 'test.vo',
      'root_path'     => '/storage/dteam',
      'access_points' => ['/dteam'],
      'vos'           => ['dteam'],
      'online_size'   => 40,
    },
  ],
}

class { 'storm::frontend':
  be_xmlrpc_host  => $host,
  be_xmlrpc_token => 'NS4kYAZuR65XJCq',
  db_user         => 'storm',
  db_passwd       => 'storm',
}

class { 'storm::gridftp':
  redirect_lcmaps_log => true,
  llgt_log_file       => '/var/log/storm/storm-gridftp-lcmaps.log',
}

class { 'storm::webdav':
  storage_areas => [
    {
      'name'                       => 'dteam',
      'root_path'                  => '/storage/dteam',
      'access_points'              => ['/dteam'],
      'vos'                        => ['dteam'],
    },
  ],
  hostnames     => [$host],
}
```

Then apply it as follow:

```bash
puppet apply manifest.pp
```

Example of output:

```bash
# puppet apply assets/manifest.pp 
Notice: Scope(Class[Storm::Backend::Configdb]): Computed short hostname for backend-test.example.org => backend-test
Notice: Compiled catalog for backend-test.example.org in environment production in 1.43 seconds
Notice: /Stage[main]/Storm::Backend::Configdb/File[/tmp/storm_db.sql]/ensure: defined content as '{md5}74350c8c70cbb7603ce1994403aaa744'
Notice: /Stage[main]/Storm::Backend::Configdb/File[/tmp/storm_be_ISAM.sql]/ensure: defined content as '{md5}01dc823d02511b1fcf2b6409de6d32b0'
Notice: /Stage[main]/Storm::Backend::Configdb/Mysql::Db[storm_db]/Mysql_database[storm_db]/ensure: created
Notice: /Stage[main]/Storm::Backend::Configdb/Mysql::Db[storm_db]/Mysql_user[storm@backend-test.example.org]/ensure: created
Notice: /Stage[main]/Storm::Backend::Configdb/Mysql::Db[storm_db]/Mysql_grant[storm@backend-test.example.org/storm_db.*]/ensure: created
Notice: /Stage[main]/Storm::Backend::Configdb/Mysql::Db[storm_db]/Exec[storm_db-import]: Triggered 'refresh' from 1 event
Notice: /Stage[main]/Storm::Backend::Configdb/Mysql::Db[storm_be_ISAM]/Mysql_database[storm_be_ISAM]/ensure: created
Notice: /Stage[main]/Storm::Backend::Configdb/Mysql::Db[storm_be_ISAM]/Mysql_grant[storm@backend-test.example.org/storm_be_ISAM.*]/ensure: created
Notice: /Stage[main]/Storm::Backend::Configdb/Mysql::Db[storm_be_ISAM]/Exec[storm_be_ISAM-import]: Triggered 'refresh' from 1 event
Notice: /Stage[main]/Storm::Backend::Configdb/Mysql_user[storm@backend-test]/ensure: created
Notice: /Stage[main]/Storm::Backend::Configdb/Mysql_grant[storm@backend-test/storm_db.*]/ensure: created
Notice: /Stage[main]/Storm::Backend::Configdb/Mysql_grant[storm@backend-test/storm_be_ISAM.*]/ensure: created
Notice: /Stage[main]/Storm::Backend::Configdb/Mysql_user[storm@%]/ensure: created
Notice: /Stage[main]/Storm::Backend::Configdb/Mysql_grant[storm@%/storm_db.*]/ensure: created
Notice: /Stage[main]/Storm::Backend::Configdb/Mysql_grant[storm@%/storm_be_ISAM.*]/ensure: created
Notice: /Stage[main]/Storm::Backend::Configdb/Mysql_user[storm@localhost]/ensure: created
Notice: /Stage[main]/Storm::Backend::Configdb/Mysql_grant[storm@localhost/storm_db.*]/ensure: created
Notice: /Stage[main]/Storm::Backend::Configdb/Mysql_grant[storm@localhost/storm_be_ISAM.*]/ensure: created
Notice: /Stage[main]/Storm::Backend::Config/File[/etc/grid-security/storm]/ensure: created
Notice: /Stage[main]/Storm::Backend::Config/File[/etc/grid-security/storm/hostcert.pem]/ensure: defined content as '{md5}e458cb58a8afeb57d545ee75545a0598'
Notice: /Stage[main]/Storm::Backend::Config/File[/etc/grid-security/storm/hostkey.pem]/ensure: defined content as '{md5}c31376f89899440b5252b67dbb7ad6d1'
Notice: /Stage[main]/Storm::Backend::Config/File[/var/log/storm]/mode: mode changed '0750' to '0755'
Notice: /Stage[main]/Storm::Backend::Config/File[/etc/storm/backend-server/namespace.xml]/content: content changed '{md5}0933e7490b788eeff7eb553d724bcf27' to '{md5}477769b3e1b6ce36408213c043a1c391'
Notice: /Stage[main]/Storm::Backend::Config/File[/etc/storm/backend-server/namespace.xml]/owner: owner changed 'storm' to 'root'
Notice: /Stage[main]/Storm::Backend::Config/File[/etc/storm/backend-server/storm.properties]/ensure: defined content as '{md5}5b1da937855a94429fb45c4ae6cec565'
Notice: /Stage[main]/Storm::Backend::Config/File[/etc/systemd/system/storm-backend-server.service.d]/mode: mode changed '0644' to '0755'
Notice: /Stage[main]/Storm::Backend::Config/File[/etc/systemd/system/storm-backend-server.service.d/storm-backend-server.conf]/content: content changed '{md5}100f4d2ed237601e8c64abf7930f8126' to '{md5}c7d47b580d7c3be3e031e448e3a11fb2'
Notice: /Stage[main]/Storm::Backend::Config/File[/etc/storm/info-provider/storm-yaim-variables.conf]/ensure: defined content as '{md5}2ee64eeedb33a24caf525c1f9fc824fc'
Notice: /Stage[main]/Storm::Backend::Service/Exec[backend-daemon-reload]: Triggered 'refresh' from 1 event
Notice: /Stage[main]/Storm::Backend::Service/Service[storm-backend-server]/ensure: ensure changed 'stopped' to 'running'
Notice: /Stage[main]/Storm::Backend::Service/Exec[configure-info-provider]: Triggered 'refresh' from 1 event
Notice: /Stage[main]/Storm::Frontend::Install/Package[storm-frontend-mp]/ensure: created
Notice: /Stage[main]/Storm::Frontend::Config/File[/etc/storm/frontend-server/storm-frontend-server.conf]/ensure: defined content as '{md5}65511d50bed9b91150e191fbd57e2bb4'
Notice: /Stage[main]/Storm::Frontend::Config/File[/etc/sysconfig/storm-frontend-server]/content: content changed '{md5}e90b99ca14b8a589bf23221cd6c0b42c' to '{md5}93cd40cf56e743a2a0653dac99b38130'
Notice: /Stage[main]/Storm::Frontend::Service/Service[storm-frontend-server]/ensure: ensure changed 'stopped' to 'running'
Notice: /Stage[main]/Storm::Gridftp::Install/Package[storm-globus-gridftp-mp]/ensure: created
Notice: /Stage[main]/Storm::Gridftp::Config/File[/etc/gridftp.conf]/content: content changed '{md5}c19c90a28ce0ed8dbdb84566a2509e93' to '{md5}96b5c0b9241e49e06bd48db5baf39f7f'
Notice: /Stage[main]/Storm::Gridftp::Config/File[/etc/sysconfig/storm-globus-gridftp]/ensure: defined content as '{md5}2d5d2d48d7c103853a57e04dfe850b35'
Notice: /Stage[main]/Storm::Gridftp::Config/File[/etc/grid-security/gsi-authz.conf]/ensure: defined content as '{md5}c47e2fe29c3fa835c1d18e2bb093fd1e'
Notice: /Stage[main]/Storm::Gridftp::Config/File[/etc/lcmaps/lcmaps.db]/ensure: defined content as '{md5}2967cf5a0f856ff30f7eef5899865b1e'
Notice: /Stage[main]/Storm::Gridftp::Config/File[/etc/lcas/lcas.db]/ensure: defined content as '{md5}56a3d6bf5fade122fa7cea55af1f7f4c'
Notice: /Stage[main]/Storm::Gridftp::Config/File[/etc/lcas/ban_users.db]/ensure: defined content as '{md5}d41d8cd98f00b204e9800998ecf8427e'
Notice: /Stage[main]/Storm::Gridftp::Service/Service[storm-globus-gridftp]/ensure: ensure changed 'stopped' to 'running'
Notice: /Stage[main]/Storm::Webdav::Install/Package[storm-webdav]/ensure: created
Notice: /Stage[main]/Storm::Webdav::Config/File[/etc/grid-security/storm-webdav]/ensure: created
Notice: /Stage[main]/Storm::Webdav::Config/File[/etc/grid-security/storm-webdav/hostcert.pem]/ensure: defined content as '{md5}e458cb58a8afeb57d545ee75545a0598'
Notice: /Stage[main]/Storm::Webdav::Config/File[/etc/grid-security/storm-webdav/hostkey.pem]/ensure: defined content as '{md5}c31376f89899440b5252b67dbb7ad6d1'
Notice: /Stage[main]/Storm::Webdav::Config/File[/etc/storm/webdav/sa.d/test.vo.properties]/ensure: defined content as '{md5}79f5efb54690d79d076e282219b7f783'
Notice: /Stage[main]/Storm::Webdav::Config/File[/etc/storm/webdav/sa.d/test.vo.2.properties]/ensure: defined content as '{md5}d6c2b615f3d8d0e7971024ef9907a050'
Notice: /Stage[main]/Storm::Webdav::Config/File[/etc/storm/webdav/sa.d/igi.properties]/ensure: defined content as '{md5}e5ff4ba0eb4d9a86a43124aceb44919f'
Notice: /Stage[main]/Storm::Webdav::Config/File[/etc/storm/webdav/sa.d/noauth.properties]/ensure: defined content as '{md5}5cc550ecbaefb883bf0d1d44683646df'
Notice: /Stage[main]/Storm::Webdav::Config/File[/etc/storm/webdav/sa.d/test.vo.bis.properties]/ensure: defined content as '{md5}b5fe791ea145a7f167dfe0669769206b'
Notice: /Stage[main]/Storm::Webdav::Config/File[/etc/storm/webdav/sa.d/nested.properties]/ensure: defined content as '{md5}ec968082b974a7093b04ee55e4c261bc'
Notice: /Stage[main]/Storm::Webdav::Config/File[/etc/storm/webdav/sa.d/tape.properties]/ensure: defined content as '{md5}9f564ae0f8f8d331af76c285614aa185'
Notice: /Stage[main]/Storm::Webdav::Config/File[/etc/storm/webdav/config/application.yml]/content: content changed '{md5}cd87331a14cc29120ff529b2ed1aab4f' to '{md5}5ee25c9495b6f50e69e975fd3f192ff6'
Notice: /Stage[main]/Storm::Webdav::Config/File[/etc/storm/webdav/config/application.yml]/group: group changed 'root' to 'storm'
Notice: /Stage[main]/Storm::Webdav::Config/File[/etc/systemd/system/storm-webdav.service.d]/mode: mode changed '0644' to '0755'
Notice: /Stage[main]/Storm::Webdav::Config/File[/etc/systemd/system/storm-webdav.service.d/filelimit.conf]/content: content changed '{md5}984c3bb6fb1ff9be3373a4dd1431c853' to '{md5}29ef14ebc16c0d0fc319815c4d2377ad'
Notice: /Stage[main]/Storm::Webdav::Config/File[/etc/systemd/system/storm-webdav.service.d/storm-webdav.conf]/content: content changed '{md5}49300c17653939763f8ff8139d1eff2a' to '{md5}0890cd072bf52b545065f1e6f8aec2d3'
Notice: /Stage[main]/Storm::Webdav::Service/Exec[webdav-daemon-reload]: Triggered 'refresh' from 2 events
Notice: /Stage[main]/Storm::Webdav::Service/Service[storm-webdav]/ensure: ensure changed 'stopped' to 'running'
Notice: Applied catalog in 178.85 seconds
```

Read [Puppet Configuration][puppetconf] section into the System Administrator guide to see all the configuration values for each Puppet class.

### Enable GPFS native libs on StoRM Backend

If you're running StoRM Backend on GPFS file system and you need to install the GPFS native libs, enable the installation through the Puppet module as follows:

```puppet
class { 'storm::backend':
  ...
  install_native_libs_gpfs => true,
  ...
}
```

### Custom MySQL server configuration

The installation of MySQL server on StoRM Backend class is disabled by default. The assumption is that a site administrator prefers to install and tune database as its needed.

StoRM Backend Puppet module class can enable the installation of a MySQL server on the same host by adding:

```puppet
class { 'storm::backend':
  ...
  mysql_server_install => true,
  mysql_server_root_password => 'supersecretpassword',
  mysql_server_override_options => {
    'mysqld'      => {
      'bind-address'    => '127.0.0.1',
      'log-error'       => '/var/log/mysqld.log',
      'max_connections' => 2048,
    },
    'mysqld_safe' => {
      'log-error' => '/var/log/mysqld.log',
    },
  },
  ...
}
```

The value of `mysql_server_override_options` showed in this example is the default value so you don't need to specify it if these values are enough for you. If you need to customize mode your options, please read the documentation of [puppetlabs/mysql](https://forge.puppet.com/puppetlabs/mysql) module.



[INSTALL-PREREQ]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.18/index.html#installprereq
[REPOSETTINGS]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.18/index.html#reposettings

[puppetconf]: {{site.baseurl}}/documentation/sysadmin-guide/1.11.18/index.html#puppetconfiguration