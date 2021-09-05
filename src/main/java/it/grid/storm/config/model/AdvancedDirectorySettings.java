package it.grid.storm.config.model;

import static it.grid.storm.config.ConfigurationDefaults.AUTOMATIC_DIRECTORY_CREATION;
import static it.grid.storm.config.ConfigurationDefaults.ENABLE_WRITE_PERM_ON_DIRECTORY;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AdvancedDirectorySettings {

  public boolean enableAutomaticCreation;
  public boolean enableWritepermOnCreation;

  public AdvancedDirectorySettings() {
    enableAutomaticCreation = AUTOMATIC_DIRECTORY_CREATION;
    enableWritepermOnCreation = ENABLE_WRITE_PERM_ON_DIRECTORY;
  }
  
  public void log(Logger log, String prefix) {
    log.info("{}.enable_automatic_creation: {}", prefix, enableAutomaticCreation);
    log.info("{}.enable_writeperm_on_creation: {}", prefix, enableWritepermOnCreation);
  }
}
