package it.grid.storm.persistence.util.db;

public abstract class SQLBuilder {

  public SQLBuilder() {
    super();
  }

  public abstract String getCommand();

  public abstract String getTable();

  public abstract String getWhat();

  public abstract String getCriteria();


}


