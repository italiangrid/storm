package it.grid.storm.rest.metadata.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

@JsonInclude(Include.NON_EMPTY)
public class StoriMetadata {

  public enum ResourceType {
    FILE, FOLDER
  }

  public enum ResourceStatus {
    ONLINE, NEARLINE
  }

  private String absolutePath;
  private VirtualFsMetadata filesystem;
  private ResourceType type;
  private ResourceStatus status;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
  private Date lastModified;

  private List<String> children;

  private FileAttributes attributes;

  /**
   * Constructor with params.
   * 
   * @param absolutePath The absolute path of the resource
   * @param type The resource type: FILE or FOLDER
   * @param status The status of the resource in order of latency: ONLINE or NEARLINE
   * @param filesystem The info about the file-system
   * @param attributes In case of FILE, the extended file attributes
   * @param lastModified The @Date of last update
   * @param children In case of FOLDER, the list of children
   */
  @JsonCreator
  public StoriMetadata(@JsonProperty("absolutePath") String absolutePath,
      @JsonProperty("type") ResourceType type, @JsonProperty("status") ResourceStatus status,
      @JsonProperty("filesystem") VirtualFsMetadata filesystem,
      @JsonProperty("attributes") FileAttributes attributes,
      @JsonProperty("lastModified") Date lastModified,
      @JsonProperty("children") List<String> children) {
    this.absolutePath = absolutePath;
    this.type = type;
    this.status = status;
    this.filesystem = filesystem;
    this.attributes = attributes;
    this.lastModified = lastModified;
    this.children = children;
  }

  /**
   * Constructor through the builder.
   * 
   * @param builder The @StoriMetadata.Builder instance.
   */
  private StoriMetadata(Builder builder) {
    this.absolutePath = builder.absolutePath;
    this.type = builder.type;
    this.status = builder.status;
    this.filesystem = builder.filesystem;
    this.attributes = builder.attributes;
    this.lastModified = builder.lastModified;
    this.children = builder.children;
  }

  public String getAbsolutePath() {
    return absolutePath;
  }

  public ResourceType getType() {
    return type;
  }

  public ResourceStatus getStatus() {
    return status;
  }

  public VirtualFsMetadata getFilesystem() {
    return filesystem;
  }

  public FileAttributes getAttributes() {
    return attributes;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public List<String> getChildren() {
    return children;
  }

  @Override
  public String toString() {
    return "StoriMetadata [absolutePath=" + absolutePath + ", filesystem=" + filesystem + ", type="
        + type + ", status=" + status + ", lastModified=" + lastModified + ", children=" + children
        + ", attributes=" + attributes + "]";
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private Date lastModified;
    private String absolutePath;
    private ResourceType type;
    private ResourceStatus status;
    private VirtualFsMetadata filesystem;
    private FileAttributes attributes;
    private List<String> children;

    public Builder absolutePath(String absolutePath) {
      this.absolutePath = absolutePath;
      return this;
    }

    public Builder type(ResourceType type) {
      this.type = type;
      return this;
    }

    public Builder status(ResourceStatus status) {
      this.status = status;
      return this;
    }

    public Builder filesystem(VirtualFsMetadata filesystem) {
      this.filesystem = filesystem;
      return this;
    }

    public Builder attributes(FileAttributes attributes) {
      this.attributes = attributes;
      return this;
    }

    public Builder lastModified(Date lastModified) {
      this.lastModified = lastModified;
      return this;
    }

    public Builder children(List<String> children) {
      this.children = children;
      return this;
    }

    public StoriMetadata build() {
      return new StoriMetadata(this);
    }
  }
}
