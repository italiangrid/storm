package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.SECURITY_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.SECURITY_TOKEN;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SecuritySettings {

  public boolean enabled;
  public String token;

  public SecuritySettings() {
    enabled = SECURITY_ENABLED;
    token = SECURITY_TOKEN;
  }
}
