package it.grid.storm.rest.info.namespace.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Filesystem {

  private String name;
  private FilesystemType fsType;
  private String spaceTokenDescription;
  private StorageClass storageClass;
  private String root;
  private FilesystemDriver filesystemDriver;
  private SpacesystemDriver spacesystemDriver;
  private Object storageAreaAuthz;
  private Properties properties;
  private Capabilities capabilities;

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("fsType")
  public FilesystemType getFsType() {
    return fsType;
  }

  @JsonProperty("fs_type")
  public void setFsType(FilesystemType fsType) {
    this.fsType = fsType;
  }

  @JsonProperty("spaceToken")
  public String getSpaceTokenDescription() {
    return spaceTokenDescription;
  }

  @JsonProperty("space-token-description")
  public void setSpaceTokenDescription(String spaceTokenDescription) {
    this.spaceTokenDescription = spaceTokenDescription;
  }

  @JsonProperty("storageClass")
  public StorageClass getStorageClass() {
    return storageClass;
  }

  @JsonProperty("storage-class")
  public void setStorageClass(StorageClass storageClass) {
    this.storageClass = storageClass;
  }

  @JsonProperty("rootPath")
  public String getRoot() {
    return root;
  }

  @JsonProperty("root")
  public void setRoot(String root) {
    this.root = root;
  }

  @JsonProperty("filesystemDriver")
  public FilesystemDriver getFilesystemDriver() {
    return filesystemDriver;
  }

  @JsonProperty("filesystem-driver")
  public void setFilesystemDriver(FilesystemDriver filesystemDriver) {
    this.filesystemDriver = filesystemDriver;
  }

  @JsonProperty("spacesystemDriver")
  public SpacesystemDriver getSpacesystemDriver() {
    return spacesystemDriver;
  }

  @JsonProperty("spacesystem-driver")
  public void setSpacesystemDriver(SpacesystemDriver spacesystemDriver) {
    this.spacesystemDriver = spacesystemDriver;
  }

  @JsonProperty("storageAreaAuthz")
  public Object getStorageAreaAuthz() {
    return storageAreaAuthz;
  }

  @JsonProperty("storage-area-authz")
  public void setStorageAreaAuthz(Object storageAreaAuthz) {
    this.storageAreaAuthz = storageAreaAuthz;
  }

  @JsonProperty("properties")
  public Properties getProperties() {
    return properties;
  }

  @JsonProperty("properties")
  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  @JsonProperty("capabilities")
  public Capabilities getCapabilities() {
    return capabilities;
  }

  @JsonProperty("capabilities")
  public void setCapabilities(Capabilities capabilities) {
    this.capabilities = capabilities;
  }


}
