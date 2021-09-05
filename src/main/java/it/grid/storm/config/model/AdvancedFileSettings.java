package it.grid.storm.config.model;

import static it.grid.storm.config.ConfigurationDefaults.DEFAULT_FILE_STORAGE_TYPE;
import static it.grid.storm.config.ConfigurationDefaults.DEFAULT_OVERWRITE_MODE;
import static it.grid.storm.config.ConfigurationDefaults.FILE_DEFAULT_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.FILE_LIFETIME_DEFAULT;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AdvancedFileSettings {
  
  public long defaultSize;
  public long defaultLifetime;
  public String defaultOverwrite;
  public String defaultStoragetype;

  public AdvancedFileSettings() {
    defaultSize = FILE_DEFAULT_SIZE;
    defaultLifetime = FILE_LIFETIME_DEFAULT;
    defaultOverwrite = DEFAULT_OVERWRITE_MODE;
    defaultStoragetype = DEFAULT_FILE_STORAGE_TYPE;
  }
  
  public void log(Logger log, String prefix) {
    log.info("{}.default_size: {}", prefix, defaultSize);
    log.info("{}.default_lifetime: {}", prefix, defaultLifetime);
    log.info("{}.default_overwrite: {}", prefix, defaultOverwrite);
    log.info("{}.default_storagetype: {}", prefix, defaultStoragetype);
  }
}
