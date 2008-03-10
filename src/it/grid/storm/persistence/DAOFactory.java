/*
* (c)2004 INFN / ICTP-eGrid
* This file can be distributed and/or modified under the terms of
* the INFN Software License. For a copy of the licence please visit
* http://www.cnaf.infn.it/license.html
*
*/

package it.grid.storm.persistence;


import it.grid.storm.persistence.dao.*;
import it.grid.storm.persistence.exceptions.DataAccessException;


/**
 * Returns an implementation of all <XXX>Catalog interfaces.
 *
 * @author Riccardo Zappi - riccardo.zappi AT cnaf.infn.it
 * @version $Id: DAOFactory.java,v 1.3 2005/10/22 15:09:40 rzappi Exp $
 */
public interface DAOFactory {

  /**
   * Returns an implementation of StorageSpaceCatalog, specific to a particular
   * datastore.
   *
   * @throws DataAccessException
   * @return StorageSpaceDAO
   */
  public StorageSpaceDAO getStorageSpaceDAO() throws DataAccessException;

  public StorageFileDAO getStorageFileDAO() throws DataAccessException;

  public PermissionDAO getPermissionDAO() throws DataAccessException;

  public CopyChunkDAO getCopyChunkDAO() throws DataAccessException;

  public PtGChunkDAO getPtGChunkDAO() throws DataAccessException;

  public PtPChunkDAO getPtPChunkDAO() throws DataAccessException;

  public StorageAreaDAO getStorageAreaDAO() throws DataAccessException;

  public RequestSummaryDAO getRequestSummaryDAO() throws DataAccessException;

  /**
   * public PendingRequestDAO getPendingRequestDAO() throws DataAccessException;
  **/
}
