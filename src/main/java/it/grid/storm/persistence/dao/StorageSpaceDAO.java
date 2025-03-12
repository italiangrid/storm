/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.dao;

import java.util.Collection;
import java.util.Date;

import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.StorageSpaceTO;

import it.grid.storm.griduser.GridUserInterface;

/**
 * 
 * Storage Space Data Access Object (DAO)
 * 
 */
public interface StorageSpaceDAO {

  public StorageSpaceTO getStorageSpaceById(Long ssId) throws DataAccessException;

  public Collection<StorageSpaceTO> getStorageSpaceByOwner(GridUserInterface owner,
      String spaceAlias) throws DataAccessException;

  public Collection<StorageSpaceTO> getStorageSpaceBySpaceType(String stype)
      throws DataAccessException;

  public Collection<StorageSpaceTO> getStorageSpaceByAliasOnly(String spaceAlias)
      throws DataAccessException;

  public StorageSpaceTO getStorageSpaceByToken(String token) throws DataAccessException;

  public Collection<StorageSpaceTO> findAll() throws DataAccessException;

  public void addStorageSpace(StorageSpaceTO ss) throws DataAccessException;

  public void removeStorageSpace(GridUserInterface user, String spaceToken)
      throws DataAccessException;

  public void removeStorageSpace(String spaceToken) throws DataAccessException;

  public void updateStorageSpace(StorageSpaceTO ss) throws DataAccessException;

  public void updateStorageSpaceFreeSpace(StorageSpaceTO ss) throws DataAccessException;

  public void updateAllStorageSpace(StorageSpaceTO ss) throws DataAccessException;

  public Collection<StorageSpaceTO> getExpired(long currentTimeInSecond) throws DataAccessException;

  public Collection<StorageSpaceTO> getStorageSpaceByUnavailableUsedSpace(long unavailableSizeValue)
      throws DataAccessException;

  public Collection<StorageSpaceTO> getStorageSpaceByPreviousLastUpdate(Date lastUpdateTimestamp)
      throws DataAccessException;

  public int increaseUsedSpace(String spaceToken, long usedSpaceToAdd) throws DataAccessException;

  public int decreaseUsedSpace(String spaceToken, long usedSpaceToRemove)
      throws DataAccessException;

}
