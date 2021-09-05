package it.grid.storm.config.model;

import static it.grid.storm.config.ConfigurationDefaults.LS_DEFAULT_ALL_LEVEL_RECURSIVE;
import static it.grid.storm.config.ConfigurationDefaults.LS_DEFAULT_NUM_OF_LEVELS;
import static it.grid.storm.config.ConfigurationDefaults.LS_DEFAULT_OFFSET;
import static it.grid.storm.config.ConfigurationDefaults.LS_MAX_NUMBER_OF_ENTRY;

import org.slf4j.Logger;

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

  public void log(Logger log, String prefix) {
    log.info("{}.max_entries: {}", prefix, maxEntries);
    log.info("{}.default_all_level_recursive: {}", prefix, defaultAllLevelRecursive);
    log.info("{}.default_num_levels: {}", prefix, defaultNumLevels);
    log.info("{}.default_offset: {}", prefix, defaultOffset);
  }
}
