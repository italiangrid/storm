## Configure the service with Puppet

The [StoRM puppet module][storm-puppet] can be used to configure the service on **CentOS 7 platform**. 

The module contains the `storm::backend` class that installs the metapackage _storm-backend-mp_ and allows site administrator to configure _storm-backend-server_ service.

> **Prerequisites**: A MySQL or MariaDB server with StoRM databases must exist. Databases can be empty. If you want to use this module to install MySQL client and server and init databases, please read about [StoRM database utility class](#stormdatabaseclass).

The Backend class installs:

- _storm-backend-mp_
- _storm-dynamic-info-provider_

Then, the Backend class configures _storm-backend-server_ service by managing the following files:

- /etc/storm/backend-server/storm.properties
- /etc/storm/backend-server/namespace.xml
- /etc/systemd/system/storm-backend-server.service.d/storm-backend-server.conf
- /etc/systemd/system/storm-backend-server.service.d/filelimit.conf

and deploys StoRM databases. In addiction, this class configures and run StoRM Info Provider by managing the following file:

- /etc/storm/info-provider/storm-yaim-variables.conf

The whole list of StoRM Backend class parameters can be found [here](https://italiangrid.github.io/storm-puppet-module/puppet_classes/storm_3A_3Abackend.html).

Example of StoRM Backend configuration:

```puppet
class { 'storm::backend':
  hostname              => 'backend.test.example',
  frontend_public_host  => 'frontend.test.example',
  transfer_protocols    => ['file', 'gsiftp', 'webdav'],
  xmlrpc_security_token => 'NS4kYAZuR65XJCq',
  service_du_enabled    => true,
  srm_pool_members      => [
    {
      'hostname' => 'frontend.test.example',
    }
  ],
  gsiftp_pool_members   => [
    {
      'hostname' => 'gridftp.test.example',
    },
  ],
  webdav_pool_members   => [
    {
      'hostname' => 'webdav.test.example',
    },
  ],
  storage_areas         => [
    {
      'name'          => 'dteam-disk',
      'root_path'     => '/storage/disk',
      'access_points' => ['/disk'],
      'vos'           => ['dteam'],
      'online_size'   => 40,
    },
    {
      'name'          => 'dteam-tape',
      'root_path'     => '/storage/tape',
      'access_points' => ['/tape'],
      'vos'           => ['dteam'],
      'online_size'   => 40,
      'nearline_size' => 80,
      'fs_type'       => 'gpfs',
      'storage_class' => 'T1D0',
    },
  ],
}
```

Starting from Puppet module v2.0.0, the management of Storage Site Report has been improved.
Site administrators can add script and cron described in the [how-to](http://italiangrid.github.io/storm/documentation/how-to/how-to-publish-json-report/) using a defined type `storm::backend::storage_site_report`.
For example:

```puppet
storm::backend::storage_site_report { 'storage-site-report':
  report_path => '/storage/info/report.json', # the internal storage area path
  minute      => '*/20', # set cron's minute
}
```

### StoRM database class <a name="stormdatabaseclass">&nbsp;</a>

The StoRM database utility class installs _mariadb_ server and releated rpms and configures _mysql_ service by managing the following files:

- /etc/my.cnf.d/server.cnf;
- /etc/systemd/system/mariadb.service.d/limits.conf.

The whole list of StoRM Database class parameters can be found [here](https://italiangrid.github.io/storm-puppet-module/puppet_classes/storm_3A_3Adb.html).

Examples of StoRM Database usage:

```puppet
class { 'storm::db':
  root_password => 'supersupersecretword',
  storm_password => 'supersecretword',
}
```

[storm-puppet]: https://forge.puppet.com/cnafsd/storm
