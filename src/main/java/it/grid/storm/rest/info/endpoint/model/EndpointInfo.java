package it.grid.storm.rest.info.endpoint.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import it.grid.storm.config.model.v2.SrmEndpoint;
import it.grid.storm.namespace.model.Authority;
import it.grid.storm.config.ConfigurationDefaults;
import it.grid.storm.config.model.v2.QualityLevel;
import it.grid.storm.rest.info.storageareas.model.SAInfo;

public class EndpointInfo {

  private String siteName;
  private QualityLevel qualityLevel;
  private String version;
  private Set<String> vos;
  private List<SrmEndpoint> srmEndpoints;
  private Set<Authority> gridftpEndpoints;
  private Set<Authority> davEndpoints;
  private Set<Authority> xrootEndpoints;
  private Map<String, SAInfo> storageAreas;

  public EndpointInfo() {
    siteName = ConfigurationDefaults.DEFAULT_SITENAME;
    qualityLevel = ConfigurationDefaults.DEFAULT_QUALITY_LEVEL;
    version = "unknown";
    vos = Sets.newHashSet();
    srmEndpoints = Lists.newArrayList();
    gridftpEndpoints = Sets.newHashSet();
    davEndpoints = Sets.newHashSet();
    xrootEndpoints = Sets.newHashSet();
    storageAreas = Maps.newHashMap();
  }

  public String getSiteName() {
    return siteName;
  }

  public void setSiteName(String siteName) {
    this.siteName = siteName;
  }

  public QualityLevel getQualityLevel() {
    return qualityLevel;
  }

  public void setQualityLevel(QualityLevel qualityLevel) {
    this.qualityLevel = qualityLevel;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public Map<String, SAInfo> getStorageAreas() {
    return storageAreas;
  }

  public void setStorageAreas(Map<String, SAInfo> storageAreas) {
    this.storageAreas.clear();
    this.storageAreas.putAll(storageAreas);
  }

  public List<SrmEndpoint> getSrmEndpoints() {
    return srmEndpoints;
  }

  public void setSrmEndpoints(List<SrmEndpoint> srmEndpoints) {
    this.srmEndpoints.clear();
    this.srmEndpoints.addAll(srmEndpoints);
  }

  public Set<Authority> getGridftpEndpoints() {
    return gridftpEndpoints;
  }

  public void setGridftpEndpoints(Set<Authority> gridftpEndpoints) {
    this.gridftpEndpoints.clear();
    this.gridftpEndpoints.addAll(gridftpEndpoints);
  }

  public Set<Authority> getDavEndpoints() {
    return davEndpoints;
  }

  public void setDavEndpoints(Set<Authority> davEndpoints) {
    this.davEndpoints.clear();
    this.davEndpoints.addAll(davEndpoints);
  }

  public Set<Authority> getXrootEndpoints() {
    return xrootEndpoints;
  }

  public void setXrootEndpoints(Set<Authority> xrootEndpoints) {
    this.xrootEndpoints.clear();
    this.xrootEndpoints.addAll(xrootEndpoints);
  }

  public Set<String> getVos() {
    return vos;
  }

  public void setVos(Set<String> vos) {
    this.vos.clear();
    this.vos.addAll(vos);
  }

}
