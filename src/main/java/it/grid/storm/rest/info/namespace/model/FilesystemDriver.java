package it.grid.storm.rest.info.namespace.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FilesystemDriver {

  POSIX_FS("it.grid.storm.filesystem.swig.posixfs"),
  GPSF("it.grid.storm.filesystem.swig.gpfs"),
  GPFS23("it.grid.storm.filesystem.swig.gpfs23"),
  XFS("it.grid.storm.filesystem.swig.xfs"),
  TEST("it.grid.storm.filesystem.swig.test");

  private String value;

  private FilesystemDriver(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
