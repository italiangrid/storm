package it.grid.storm.catalogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLWarning;

public class ChunkDAO {

  private static final Logger log = LoggerFactory.getLogger(ChunkDAO.class);

  protected ChunkDAO() {}

  public static void printWarnings(SQLWarning warning) {

    if (warning != null) {
      log.warn("---Warning---");

      while (warning != null) {
        log.warn("Message: {}", warning.getMessage());
        log.warn("SQLState: {}", warning.getSQLState());
        log.warn("Vendor error code: {}", warning.getErrorCode());
        warning = warning.getNextWarning();
      }
    }
  }

  public static String buildInClauseForArray(int size) {
    StringBuilder b = new StringBuilder();
    for (int i=1; i<=size; i++) {
      b.append('?');
      if (i<size) {
        b.append(',');
      }
    }
    return b.toString();
  }
}
