package it.grid.storm.rest.info.namespace.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SpacesystemDriver {

  GPFS_SPACESYSTEM("it.grid.storm.filesystem.GPFSSpaceSystem"),
  MOCK_SPACESYSTEM("it.grid.storm.filesystem.MockSpaceSystem"),
  XFS_SPACESYSTEM("it.grid.storm.filesystem.XFSSpaceSystem");

  private String value;

  private SpacesystemDriver(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}

