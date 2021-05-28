package it.grid.storm.catalogs;

public class ChunkDAOUtils {

  protected ChunkDAOUtils() {}

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
