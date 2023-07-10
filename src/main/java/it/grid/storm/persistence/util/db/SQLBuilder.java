/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
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
