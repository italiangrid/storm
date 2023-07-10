/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence;

import it.grid.storm.persistence.exceptions.PersistenceException;
import java.sql.Connection;

public interface DataSourceConnectionFactory {

  public Connection borrowConnection() throws PersistenceException;

  public void giveBackConnection(Connection con) throws PersistenceException;
}
