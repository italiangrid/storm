package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.DEFAULT_QUALITY_LEVEL;
import static it.grid.storm.config.ConfigurationDefaults.DEFAULT_SITENAME;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Site {

  private String name;
  private QualityLevel qualityLevel;
  
  public Site() {

    setName(DEFAULT_SITENAME);
    setQualityLevel(DEFAULT_QUALITY_LEVEL);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public QualityLevel getQualityLevel() {
    return qualityLevel;
  }

  public void setQualityLevel(QualityLevel qualityLevel) {
    this.qualityLevel = qualityLevel;
  }

  
}
