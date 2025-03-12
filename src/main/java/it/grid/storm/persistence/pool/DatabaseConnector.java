/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */

package it.grid.storm.persistence.pool;

public interface DatabaseConnector {

  public String getDbName();

  public String getDriverName();

  public String getDbUsername();

  public String getDbPassword();

  public String getDbURL();

}
