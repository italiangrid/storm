package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.AUTOMATIC_DIRECTORY_CREATION;
import static it.grid.storm.config.ConfigurationDefaults.ENABLE_WRITE_PERM_ON_DIRECTORY;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AdvancedDirectorySettings {

  private boolean enableAutomaticCreation;
  private boolean enableWritepermOnCreation;

  public AdvancedDirectorySettings() {
    enableAutomaticCreation = AUTOMATIC_DIRECTORY_CREATION;
    enableWritepermOnCreation = ENABLE_WRITE_PERM_ON_DIRECTORY;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("AdvancedDirectorySettings [enableAutomaticCreation=");
    builder.append(enableAutomaticCreation);
    builder.append(", enableWritepermOnCreation=");
    builder.append(enableWritepermOnCreation);
    builder.append("]");
    return builder.toString();
  }

  public boolean isEnableAutomaticCreation() {
    return enableAutomaticCreation;
  }

  public void setEnableAutomaticCreation(boolean enableAutomaticCreation) {
    this.enableAutomaticCreation = enableAutomaticCreation;
  }

  public boolean isEnableWritepermOnCreation() {
    return enableWritepermOnCreation;
  }

  public void setEnableWritepermOnCreation(boolean enableWritepermOnCreation) {
    this.enableWritepermOnCreation = enableWritepermOnCreation;
  }

}
