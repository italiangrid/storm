package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.SECURITY_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.SECURITY_TOKEN;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SecuritySettings {

  private boolean enabled;
  private String token;

  public SecuritySettings() {
    enabled = SECURITY_ENABLED;
    token = SECURITY_TOKEN;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

}
