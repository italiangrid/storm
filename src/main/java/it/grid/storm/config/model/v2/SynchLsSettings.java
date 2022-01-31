package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.LS_DEFAULT_ALL_LEVEL_RECURSIVE;
import static it.grid.storm.config.ConfigurationDefaults.LS_DEFAULT_NUM_OF_LEVELS;
import static it.grid.storm.config.ConfigurationDefaults.LS_DEFAULT_OFFSET;
import static it.grid.storm.config.ConfigurationDefaults.LS_MAX_NUMBER_OF_ENTRY;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SynchLsSettings {

  private int maxEntries;
  private boolean defaultAllLevelRecursive;
  private short defaultNumLevels;
  private short defaultOffset;

  public SynchLsSettings() {
    maxEntries = LS_MAX_NUMBER_OF_ENTRY;
    defaultAllLevelRecursive = LS_DEFAULT_ALL_LEVEL_RECURSIVE;
    defaultNumLevels = LS_DEFAULT_NUM_OF_LEVELS;
    defaultOffset = LS_DEFAULT_OFFSET;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("SynchLsSettings [maxEntries=");
    builder.append(maxEntries);
    builder.append(", defaultAllLevelRecursive=");
    builder.append(defaultAllLevelRecursive);
    builder.append(", defaultNumLevels=");
    builder.append(defaultNumLevels);
    builder.append(", defaultOffset=");
    builder.append(defaultOffset);
    builder.append("]");
    return builder.toString();
  }

  public int getMaxEntries() {
    return maxEntries;
  }

  public void setMaxEntries(int maxEntries) {
    this.maxEntries = maxEntries;
  }

  public boolean isDefaultAllLevelRecursive() {
    return defaultAllLevelRecursive;
  }

  public void setDefaultAllLevelRecursive(boolean defaultAllLevelRecursive) {
    this.defaultAllLevelRecursive = defaultAllLevelRecursive;
  }

  public short getDefaultNumLevels() {
    return defaultNumLevels;
  }

  public void setDefaultNumLevels(short defaultNumLevels) {
    this.defaultNumLevels = defaultNumLevels;
  }

  public short getDefaultOffset() {
    return defaultOffset;
  }

  public void setDefaultOffset(short defaultOffset) {
    this.defaultOffset = defaultOffset;
  }

}
