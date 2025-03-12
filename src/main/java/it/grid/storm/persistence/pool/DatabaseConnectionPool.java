/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.pool;

public interface DatabaseConnectionPool {

  public int getMaxTotal();

  public int getInitialSize();

  public int getMinIdle();

  public long getMaxConnLifetimeMillis();

  public boolean getTestOnBorrow();

  public boolean getTestWhileIdle();

}