package it.grid.storm.config.model;

import static it.grid.storm.config.ConfigurationDefaults.EXTRA_SLASHES_FOR_FILE_TURL;
import static it.grid.storm.config.ConfigurationDefaults.EXTRA_SLASHES_FOR_GSIFTP_TURL;
import static it.grid.storm.config.ConfigurationDefaults.EXTRA_SLASHES_FOR_RFIO_TURL;
import static it.grid.storm.config.ConfigurationDefaults.EXTRA_SLASHES_FOR_ROOT_TURL;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ExtraslashesSettings {

  public String file;
  public String rfio;
  public String root;
  public String gsiftp;
  
  public ExtraslashesSettings() {
    file = EXTRA_SLASHES_FOR_FILE_TURL;
    rfio = EXTRA_SLASHES_FOR_RFIO_TURL;
    root = EXTRA_SLASHES_FOR_ROOT_TURL;
    gsiftp = EXTRA_SLASHES_FOR_GSIFTP_TURL;
  }
  
  public void log(Logger log, String prefix) {
    log.info("{}.file: {}", prefix, file);
    log.info("{}.rfio: {}", prefix, rfio);
    log.info("{}.root: {}", prefix, root);
    log.info("{}.gsiftp: {}", prefix, gsiftp);
  }
}
