package it.grid.storm.rest.metadata.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class VirtualFsMetadata {

  private final String name;
  private final String root;

  @JsonCreator
  private VirtualFsMetadata(@JsonProperty("name") String name, @JsonProperty("root") String root) {
    this.name = name;
    this.root = root;
  }

  private VirtualFsMetadata(Builder builder) {
    this.name = builder.name;
    this.root = builder.root;
  }

  public String getName() {
    return name;
  }

  public String getRoot() {
    return root;
  }

  @Override
  public String toString() {
    return "VirtualFsMetadata [name=" + name + ", root=" + root + "]";
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String name;
    private String root;

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder root(String root) {
      this.root = root;
      return this;
    }

    public VirtualFsMetadata build() {
      return new VirtualFsMetadata(this);
    }
  }
}
