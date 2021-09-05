package it.grid.storm.config.model;

import static it.grid.storm.config.ConfigurationDefaults.SECURITY_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.SECURITY_TOKEN;

import org.slf4j.Logger;

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

  public void log(Logger log, String prefix) {
    log.info("{}.enabled: {}", prefix, enabled);
    log.info("{}.token: {}", prefix, token);
  }
}
