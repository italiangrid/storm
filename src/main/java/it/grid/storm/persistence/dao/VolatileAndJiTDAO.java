/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.dao;

import java.util.List;

/**
 * DAO class for VolatileAndJiTCatalog: it has been specifically designed for MySQL.
 *
 */

public interface VolatileAndJiTDAO {

  public void addJiT(String filename, int uid, int gid, int acl, long start, long pinLifetime);

  public void addVolatile(String filename, long start, long fileLifetime);

  public boolean exists(String filename);

  public void forceUpdateJiT(String filename, int uid, int acl, long start, long pinLifetime);

  public int numberJiT(String filename, int uid, int acl);

  public int numberVolatile(String filename);

  public void removeAllJiTsOn(String filename);

  public List<Object> removeExpired(long time);

  public void updateJiT(String filename, int uid, int acl, long start, long pinLifetime);

  public void updateVolatile(String filename, long start, long fileLifetime);

  public void updateVolatile(String fileName, long fileStart);

  public List<Long> volatileInfoOn(String filename);

}
