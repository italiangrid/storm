package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.LS_DEFAULT_ALL_LEVEL_RECURSIVE;
import static it.grid.storm.config.ConfigurationDefaults.LS_DEFAULT_NUM_OF_LEVELS;
import static it.grid.storm.config.ConfigurationDefaults.LS_DEFAULT_OFFSET;
import static it.grid.storm.config.ConfigurationDefaults.LS_MAX_NUMBER_OF_ENTRY;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SynchLsSettings {

  public int maxEntries;
  public boolean defaultAllLevelRecursive;
  public short defaultNumLevels;
  public short defaultOffset;

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

}
