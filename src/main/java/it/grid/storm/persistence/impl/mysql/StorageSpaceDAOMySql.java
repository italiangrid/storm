/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.impl.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.persistence.dao.AbstractDAO;
import it.grid.storm.persistence.dao.StorageSpaceDAO;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.StorageSpaceTO;
import it.grid.storm.persistence.pool.impl.StormBeIsamConnectionPool;
import it.grid.storm.persistence.util.helper.StorageSpaceSQLHelper;

public class StorageSpaceDAOMySql extends AbstractDAO implements StorageSpaceDAO {

  private static final Logger log = LoggerFactory.getLogger(StorageSpaceDAOMySql.class);

  private static StorageSpaceDAO instance;

  public static synchronized StorageSpaceDAO getInstance() {
    if (instance == null) {
      instance = new StorageSpaceDAOMySql();
    }
    return instance;
  }

  private StorageSpaceSQLHelper helper;

  private StorageSpaceDAOMySql() {
    super(StormBeIsamConnectionPool.getInstance());
    helper = new StorageSpaceSQLHelper();
  }

  /**
   * addStorageSpace
   * 
   * @param ss StorageSpace
   */
  public void addStorageSpace(StorageSpaceTO ss) {

    Connection con = null;
    PreparedStatement ps = null;
    int res = 0;

    try {

      con = getConnection();
      ps = helper.insertQuery(con, ss);

      log.debug("INSERT query = {}", ps);
      res = ps.executeUpdate();
      log.debug("INSERT result = {}", res);

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeStatement(ps);
      closeConnection(con);
    }

    if (res <= 0) {
      log.error("No rows inserted for Storage Space: {}", ss.toString());
    }
  }

  /**
   * getStorageSpaceById
   * 
   * @param ssId Long
   * @return StorageSpace
   * @throws DataAccessException
   */
  public StorageSpaceTO getStorageSpaceById(Long ssId) throws DataAccessException {

    throw new DataAccessException("getStorageSpaceById: Unimplemented method!");
  }

  public Collection<StorageSpaceTO> findAll() throws DataAccessException {

    throw new DataAccessException("findAll: Unimplemented method!");
  }

  /**
   * Returns a Collection of StorageSpaceTO owned by 'user' and with the specified alias
   * ('spaceAlias'). 'spaceAlias' can be NULL or empty and in these cases a Collection of all the
   * StorageSpaceTO owned by 'user' is returned.
   * 
   * @param owner VomsGridUser.
   * @param spaceAlias String.
   * @return Collection of StorageSpaceTO.
   */
  public Collection<StorageSpaceTO> getStorageSpaceByOwner(GridUserInterface owner,
      String spaceAlias) {

    StorageSpaceTO ssTO = null;
    Collection<StorageSpaceTO> result = new LinkedList<StorageSpaceTO>();

    Connection con = null;
    ResultSet res = null;
    PreparedStatement ps = null;

    try {
      con = getConnection();
      ps = helper.selectBySpaceAliasQuery(con, owner, spaceAlias);

      log.debug("DB query = {}", ps);
      res = ps.executeQuery();
      log.debug("query result = {}", res);

      if (res.first()) {
        do {
          ssTO = helper.makeStorageSpaceTO(res);
          result.add(ssTO);
        } while (res.next());
      } else {
        log.debug("No rows found for query : {}", ps);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeResultSet(res);
      closeStatement(ps);
      closeConnection(con);
    }
    return result;
  }

  /**
   * Returns a Collection of StorageSpaceTO owned by 'VO'.
   * 
   * @param stype.
   * @return Collection of StorageSpaceTO.
   */

  public Collection<StorageSpaceTO> getStorageSpaceBySpaceType(String stype) {

    StorageSpaceTO ssTO = null;
    Collection<StorageSpaceTO> result = new LinkedList<StorageSpaceTO>();

    Connection con = null;
    ResultSet res = null;
    PreparedStatement ps = null;

    try {
      con = getConnection();
      ps = helper.selectBySpaceType(con, stype);

      log.debug("DB query = {}", ps);
      res = ps.executeQuery();
      log.debug("query result = {}", res);

      if (res.first()) {
        do {
          ssTO = helper.makeStorageSpaceTO(res);
          result.add(ssTO);
        } while (res.next());
      } else {
        log.info("No rows found for query : {}", ps);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeResultSet(res);
      closeStatement(ps);
      closeConnection(con);
    }
    return result;
  }

  /**
   * Returns a Collection of StorageSpaceTO with the specified alias ('spaceAlias'). 'spaceAlias'
   * can not be be NULL or empty.
   * 
   * @param spaceAlias String.
   * @return Collection of StorageSpaceTO.
   */
  public Collection<StorageSpaceTO> getStorageSpaceByAliasOnly(String spaceAlias) {

    StorageSpaceTO ssTO = null;
    Collection<StorageSpaceTO> result = new LinkedList<StorageSpaceTO>();

    Connection con = null;
    ResultSet res = null;
    PreparedStatement ps = null;

    try {
      con = getConnection();
      ps = helper.selectBySpaceAliasOnlyQuery(con, spaceAlias);

      log.debug("DB query = {}", ps);
      res = ps.executeQuery();
      log.debug("query result = {}", res);

      if (res.first()) {
        do {
          ssTO = helper.makeStorageSpaceTO(res);
          result.add(ssTO);
        } while (res.next());
      } else {
        log.info("No rows found for query : {}", ps);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeResultSet(res);
      closeStatement(ps);
      closeConnection(con);
    }
    return result;
  }

  /**
   * getStorageSpaceByToken
   * 
   * @param token TSpaceToken
   * @return StorageSpace , null if not row found on that token
   */
  public StorageSpaceTO getStorageSpaceByToken(String token) {

    StorageSpaceTO ssTO = null;

    Connection con = null;
    ResultSet res = null;
    PreparedStatement ps = null;

    try {
      con = getConnection();
      ps = helper.selectByTokenQuery(con, token);

      log.debug("SELECT query = {}", ps);
      res = ps.executeQuery();
      log.debug("SELECT result = {}", res);

      if (res.first()) {
        ssTO = helper.makeStorageSpaceTO(res);
      } else {
        log.info("No rows found for query : {}", ps);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeResultSet(res);
      closeStatement(ps);
      closeConnection(con);
    }
    return ssTO;
  }

  @Override
  public Collection<StorageSpaceTO> getStorageSpaceByUnavailableUsedSpace(
      long unavailableSizeValue) {

    StorageSpaceTO ssTO = null;
    Collection<StorageSpaceTO> result = new LinkedList<StorageSpaceTO>();

    Connection con = null;
    ResultSet res = null;
    PreparedStatement ps = null;

    try {
      con = getConnection();
      ps = helper.selectByUnavailableUsedSpaceSizeQuery(con, unavailableSizeValue);

      log.debug("SELECT query = {}", ps);
      res = ps.executeQuery();
      log.debug("SELECT result = {}", res);

      if (res.first()) {
        do {
          ssTO = helper.makeStorageSpaceTO(res);
          result.add(ssTO);
        } while (res.next());
      } else {
        log.debug("No rows found for query : {}", ps);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeResultSet(res);
      closeStatement(ps);
      closeConnection(con);
    }
    return result;
  }

  @Override
  public Collection<StorageSpaceTO> getStorageSpaceByPreviousLastUpdate(Date lastUpdateTimestamp) {

    StorageSpaceTO ssTO = null;
    Collection<StorageSpaceTO> result = new LinkedList<StorageSpaceTO>();

    Connection con = null;
    ResultSet res = null;
    PreparedStatement ps = null;

    try {
      con = getConnection();
      ps = helper.selectByPreviousOrNullLastUpdateQuery(con, lastUpdateTimestamp.getTime());

      log.debug("SELECT query = {}", ps);
      res = ps.executeQuery();
      log.debug("SELECT result = {}", res);

      if (res.first()) {
        do {
          ssTO = helper.makeStorageSpaceTO(res);
          result.add(ssTO);
        } while (res.next());
      } else {
        log.info("No rows found for query : {}", ps);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeResultSet(res);
      closeStatement(ps);
      closeConnection(con);
    }
    return result;
  }

  /**
   * removeStorageSpace
   * 
   * @param ss StorageSpace
   */
  public void removeStorageSpace(GridUserInterface user, String spaceToken) {

    Connection con = null;
    PreparedStatement ps = null;
    int res = 0;

    try {
      con = getConnection();
      ps = helper.removeByTokenQuery(con, user, spaceToken);
      log.debug("query = {}", ps);

      res = ps.executeUpdate();
      log.debug("Number of rows removed: {}", res);

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeStatement(ps);
      closeConnection(con);
    }
  }

  /**
   * removeStorageSpace only by spaceToken
   * 
   * @param ss StorageSpace
   * @throws DataAccessException
   */
  public void removeStorageSpace(String spaceToken) throws DataAccessException {

    Connection con = null;
    PreparedStatement ps = null;
    int res = 0;

    try {
      con = getConnection();
      ps = helper.removeByTokenQuery(con, spaceToken);

      log.debug("query = {}", ps);
      res = ps.executeUpdate();
      log.debug("Number of rows removed: {}", res);

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeStatement(ps);
      closeConnection(con);
    }
  }

  /**
   * 
   * @param ssTO StorageSpaceTO
   */
  public void updateStorageSpace(StorageSpaceTO ssTO) {

    Connection con = null;
    PreparedStatement ps = null;
    int res = 0;

    try {
      con = getConnection();
      ps = helper.updateByAliasAndTokenQuery(con, ssTO);

      log.debug("UPDATE query = {}", ps);
      res = ps.executeUpdate();
      log.debug("UPDATE row count = {}", res);

      if (res == 0) {
        log.warn("No storage space rows updated by query : {}", ps);
      }
      if (res > 1) {
        log.warn(
            "More than a single storage space rows updated by " + "query : {}. updated {} rows.",
            ps, res);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeStatement(ps);
      closeConnection(con);
    }

  }

  /**
   * 
   * @param ssTO StorageSpaceTO
   * @throws DataAccessException
   */
  public void updateStorageSpaceFreeSpace(StorageSpaceTO ssTO) throws DataAccessException {

    long freeSpace = ssTO.getFreeSize();

    Connection con = null;
    PreparedStatement ps = null;
    int res = 0;

    try {

      con = getConnection();
      ps = helper.updateFreeSpaceByTokenQuery(con, ssTO.getSpaceToken(), freeSpace, new Date());

      log.debug("UPDATE query = {}", ps);
      res = ps.executeUpdate();
      log.debug("UPDATE row count = {}", res);

      if (res <= 0) {
        log.warn("No storage space rows updated by query : {}", ps);
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new DataAccessException("Error while executing UPDATE query", e);
    } finally {
      closeStatement(ps);
      closeConnection(con);
    }
  }

  /**
   * 
   * @param ssTO StorageSpaceTO
   */
  public void updateAllStorageSpace(StorageSpaceTO ssTO) {

    Connection con = null;
    PreparedStatement ps = null;
    int res = 0;

    try {
      con = getConnection();
      ps = helper.updateByTokenQuery(con, ssTO);

      log.debug("UPDATE query = {}", ps);
      res = ps.executeUpdate();
      log.debug("UPDATE row count = {}", res);

      if (res == 0) {
        log.warn("No storage space rows updated by query {}", ps);
      }
      if (res > 1) {
        log.warn(
            "More than a single storage space rows updated " + "by query : {}. updated {} rows", ps,
            res);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeStatement(ps);
      closeConnection(con);
    }
  }

  /**
   * Method used to retrieve the set of StorageTO for expired space.
   * 
   * @param long timeInSecond
   * @return Collection of transfer object
   */
  public Collection<StorageSpaceTO> getExpired(long currentTimeInSecond) {

    StorageSpaceTO ssTO = null;
    Collection<StorageSpaceTO> result = new LinkedList<StorageSpaceTO>();

    Connection con = null;
    ResultSet res = null;
    PreparedStatement ps = null;

    try {

      con = getConnection();
      ps = helper.selectExpiredQuery(con, currentTimeInSecond);

      log.debug("DB query = {}", ps);
      res = ps.executeQuery();
      log.debug("query result = {}", res);

      if (res.first()) {
        do {
          ssTO = helper.makeStorageSpaceTO(res);
          result.add(ssTO);
        } while (res.next());
      } else {
        log.debug("No rows found for query : {}", ps);
        log.debug("No storage space expired found at time " + currentTimeInSecond);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeResultSet(res);
      closeStatement(ps);
      closeConnection(con);
    }

    return result;
  }

  @Override
  public int increaseUsedSpace(String spaceToken, long usedSpaceToAdd) throws DataAccessException {

    Connection con = null;
    ResultSet res = null;
    PreparedStatement ps = null;
    int n = 0;

    try {

      con = getConnection();
      ps = helper.increaseUsedSpaceByTokenQuery(con, spaceToken, usedSpaceToAdd);

      log.debug("DB query = {}", ps);
      n = ps.executeUpdate();
      log.debug("query result = {}", n);

      if (n == 0) {
        log.debug("No storage space updated!");
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeResultSet(res);
      closeStatement(ps);
      closeConnection(con);
    }

    return n;
  }

  @Override
  public int decreaseUsedSpace(String spaceToken, long usedSpaceToRemove)
      throws DataAccessException {

    Connection con = null;
    ResultSet res = null;
    PreparedStatement ps = null;
    int n = 0;

    try {

      con = getConnection();
      ps = helper.decreaseUsedSpaceByTokenQuery(con, spaceToken, usedSpaceToRemove);

      log.debug("DB query = {}", ps);
      n = ps.executeUpdate();
      log.debug("query result = {}", n);

      if (n == 0) {
        log.debug("No storage space updated!");
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeResultSet(res);
      closeStatement(ps);
      closeConnection(con);
    }

    return n;
  }
}
