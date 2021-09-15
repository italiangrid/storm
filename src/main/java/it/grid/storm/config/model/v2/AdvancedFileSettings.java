package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.DEFAULT_FILE_STORAGE_TYPE;
import static it.grid.storm.config.ConfigurationDefaults.DEFAULT_OVERWRITE_MODE;
import static it.grid.storm.config.ConfigurationDefaults.FILE_DEFAULT_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.FILE_LIFETIME_DEFAULT;

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

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("AdvancedFileSettings [defaultSize=");
    builder.append(defaultSize);
    builder.append(", defaultLifetime=");
    builder.append(defaultLifetime);
    builder.append(", defaultOverwrite=");
    builder.append(defaultOverwrite);
    builder.append(", defaultStoragetype=");
    builder.append(defaultStoragetype);
    builder.append("]");
    return builder.toString();
  }
  
}
