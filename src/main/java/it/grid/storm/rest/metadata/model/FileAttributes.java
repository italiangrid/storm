/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.rest.metadata.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class FileAttributes {

  private final Boolean pinned;
  private final Boolean migrated;
  private final Boolean premigrated;
  private final String checksum;
  private final Long tsmRecD;
  private final Integer tsmRecR;
  private final String tsmRecT;

  /**
   * Constructor with parameters.
   * 
   * @param pinned Is file pinned.
   * @param migrated Is file migrated to tape.
   * @param premigrated Is file only on disk and needs to be migrated to tape.
   * @param checksum The Adler32 checksum value
   * @param tsmRecD The recall date
   * @param tsmRecR The recall number or max retry
   * @param tsmRecT The recall tasks queued
   */
  @JsonCreator
  public FileAttributes(@JsonProperty("pinned") Boolean pinned,
      @JsonProperty("migrated") Boolean migrated, @JsonProperty("premigrated") Boolean premigrated,
      @JsonProperty("checksum") String checksum, @JsonProperty("TSMRecD") Long tsmRecD,
      @JsonProperty("TSMRecR") Integer tsmRecR, @JsonProperty("TSMRecT") String tsmRecT) {

    this.pinned = pinned;
    this.migrated = migrated;
    this.premigrated = premigrated;
    this.checksum = checksum;
    this.tsmRecD = tsmRecD;
    this.tsmRecR = tsmRecR;
    this.tsmRecT = tsmRecT;
  }

  public Boolean getPinned() {
    return pinned;
  }

  public Boolean getMigrated() {
    return migrated;
  }

  public Boolean getPremigrated() {
    return premigrated;
  }

  public String getChecksum() {
    return checksum;
  }

  public Long getTsmRecD() {
    return tsmRecD;
  }

  public Integer getTsmRecR() {
    return tsmRecR;
  }

  public String getTsmRecT() {
    return tsmRecT;
  }

  @Override
  public String toString() {
    return "FileAttributes [pinned=" + pinned + ", migrated=" + migrated + ", premigrated="
        + premigrated + ", checksum=" + checksum + ", tsmRecD=" + tsmRecD + ", tsmRecR=" + tsmRecR
        + ", tsmRecT=" + tsmRecT + "]";
  }

	/**
   * Constructor through the builder.
   * 
   * @param builder The @FileAttributes.Builder instance.
   */
  public FileAttributes(Builder builder) {

    this.pinned = builder.pinned;
    this.migrated = builder.migrated;
    this.premigrated = builder.premigrated;
    this.checksum = builder.checksum;
    this.tsmRecD = builder.tsmRecD;
    this.tsmRecR = builder.tsmRecR;
    this.tsmRecT = builder.tsmRecT;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private Boolean pinned;
    private Boolean migrated;
    private Boolean premigrated;
    private String checksum;
    private Long tsmRecD;
    private Integer tsmRecR;
    private String tsmRecT;

    public Builder pinned(Boolean pinned) {
      this.pinned = pinned;
      return this;
    }

    public Builder migrated(Boolean migrated) {
      this.migrated = migrated;
      return this;
    }

    public Builder premigrated(Boolean premigrated) {
      this.premigrated = premigrated;
      return this;
    }

    public Builder checksum(String checksum) {
      this.checksum = checksum;
      return this;
    }

    public Builder tsmRecD(Long tsmRecD) {
      this.tsmRecD = tsmRecD;
      return this;
    }

    public Builder tsmRecR(Integer tsmRecR) {
      this.tsmRecR = tsmRecR;
      return this;
    }

    public Builder tsmRecT(String tsmRecT) {
      this.tsmRecT = tsmRecT;
      return this;
    }

    public FileAttributes build() {
      return new FileAttributes(this);
    }
  }
}
