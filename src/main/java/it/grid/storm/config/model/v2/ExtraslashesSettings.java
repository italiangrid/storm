package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.EXTRA_SLASHES_FOR_FILE_TURL;
import static it.grid.storm.config.ConfigurationDefaults.EXTRA_SLASHES_FOR_GSIFTP_TURL;
import static it.grid.storm.config.ConfigurationDefaults.EXTRA_SLASHES_FOR_RFIO_TURL;
import static it.grid.storm.config.ConfigurationDefaults.EXTRA_SLASHES_FOR_ROOT_TURL;

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

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ExtraslashesSettings [file=");
    builder.append(file);
    builder.append(", rfio=");
    builder.append(rfio);
    builder.append(", root=");
    builder.append(root);
    builder.append(", gsiftp=");
    builder.append(gsiftp);
    builder.append("]");
    return builder.toString();
  }

}
