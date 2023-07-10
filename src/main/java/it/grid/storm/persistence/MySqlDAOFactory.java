/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence;

import it.grid.storm.persistence.dao.PtGChunkDAO;
import it.grid.storm.persistence.dao.PtPChunkDAO;
import it.grid.storm.persistence.dao.RequestSummaryDAO;
import it.grid.storm.persistence.dao.StorageAreaDAO;
import it.grid.storm.persistence.dao.StorageSpaceDAO;
import it.grid.storm.persistence.dao.TapeRecallDAO;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.impl.mysql.StorageSpaceDAOMySql;
import it.grid.storm.persistence.impl.mysql.TapeRecallDAOMySql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySqlDAOFactory implements DAOFactory {

  public static final String factoryName = "JDBC - MySQL DAO Factory";

  private static final Logger log = LoggerFactory.getLogger(MySqlDAOFactory.class);

  private static MySqlDAOFactory factory = new MySqlDAOFactory();

  /** */
  private MySqlDAOFactory() {
    log.info("DAO factory: {}", MySqlDAOFactory.factoryName);
  }

  public static MySqlDAOFactory getInstance() {

    return MySqlDAOFactory.factory;
  }

  /**
   * Returns an implementation of StorageSpaceCatalog, specific to a particular datastore.
   *
   * @throws DataAccessException
   * @return StorageSpaceDAO
   * @todo Implement this it.grid.storm.persistence.DAOFactory method
   */
  public StorageSpaceDAO getStorageSpaceDAO() throws DataAccessException {

    return new StorageSpaceDAOMySql();
  }

  /**
   * Returns an implementation of TapeRecallCatalog, specific to a particular datastore.
   *
   * @throws DataAccessException
   * @return TapeReallDAO
   * @todo Implement this it.grid.storm.persistence.DAOFactory method
   */
  public TapeRecallDAO getTapeRecallDAO() {

    return new TapeRecallDAOMySql();
  }

  /** @return String */
  @Override
  public String toString() {

    return MySqlDAOFactory.factoryName;
  }

  /**
   * getPtGChunkDAO
   *
   * @return PtGChunkDAO
   * @throws DataAccessException
   * @todo Implement this it.grid.storm.persistence.DAOFactory method
   */
  public PtGChunkDAO getPtGChunkDAO() throws DataAccessException {

    return null;
  }

  /**
   * getPtPChunkDAO
   *
   * @return PtPChunkDAO
   * @throws DataAccessException
   * @todo Implement this it.grid.storm.persistence.DAOFactory method
   */
  public PtPChunkDAO getPtPChunkDAO() throws DataAccessException {

    return null;
  }

  /**
   * getRequestSummaryDAO
   *
   * @return RequestSummaryDAO
   * @throws DataAccessException
   * @todo Implement this it.grid.storm.persistence.DAOFactory method
   */
  public RequestSummaryDAO getRequestSummaryDAO() throws DataAccessException {

    return null;
  }

  /**
   * getStorageAreaDAO
   *
   * @return StorageAreaDAO
   * @throws DataAccessException
   * @todo Implement this it.grid.storm.persistence.DAOFactory method
   */
  public StorageAreaDAO getStorageAreaDAO() throws DataAccessException {

    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see it.grid.storm.persistence.DAOFactory#getTapeRecallDAO(boolean)
   */
  public TapeRecallDAO getTapeRecallDAO(boolean test) throws DataAccessException {

    return new TapeRecallDAOMySql();
  }
}
