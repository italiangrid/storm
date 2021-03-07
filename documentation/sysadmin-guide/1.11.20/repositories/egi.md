### EGI Trust Anchors Repository <a name="egitrustrepo">&nbsp;</a>

Install *EGI Trust Anchors repository* by following [EGI instructions][egi-instructions].

In short:

    wget http://repository.egi.eu/sw/production/cas/1/current/repo-files/EGI-trustanchors.repo -O /etc/yum.repos.d/EGI-trustanchors.repo
    yum install ca-policy-egi-core

The *DAG repository* must be disabled. If needed, set to 0 the enabled property in your */etc/yum.repos.d/dag.repo* file.

[egi-instructions]: https://wiki.egi.eu/wiki/EGI_IGTF_Release#Using_YUM_package_management
