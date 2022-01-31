package it.grid.storm.rest.info.endpoint.model;

import java.util.Map;

import com.google.common.collect.Maps;

import it.grid.storm.config.model.v2.QualityLevel;
import it.grid.storm.rest.info.storageareas.model.SAInfo;

public class EndpointInfo {

  private String siteName;
  private QualityLevel qualityLevel;
  private String version;
  private Map<String, SAInfo> storageAreas;

  public EndpointInfo() {
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

}
