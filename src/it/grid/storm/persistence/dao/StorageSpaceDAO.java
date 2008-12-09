package it.grid.storm.persistence.dao;



import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.StorageSpaceTO;
import java.util.*;

import it.grid.storm.griduser.GridUserInterface;
//import it.grid.storm.griduser.VomsGridUser;


/**
 *
 * Storage Space Data Access Object (DAO)
 *
 * <a href="http://java.sun.com/blueprints/corej2eepatterns/Patterns/DataAccessObject.html">
 * DAO pattern</a>
 *
 *
 */
public interface StorageSpaceDAO {

  public StorageSpaceTO getStorageSpaceById(Long ssId) throws DataAccessException;

  public Collection getStorageSpaceByOwner(GridUserInterface owner, String spaceAlias) throws DataAccessException;

  public Collection getStorageSpaceByAliasOnly(String spaceAlias) throws DataAccessException;

  public StorageSpaceTO getStorageSpaceByToken(String token) throws DataAccessException;

  public Collection findAll() throws DataAccessException;

  public void addStorageSpace(StorageSpaceTO ss) throws DataAccessException;

  public void removeStorageSpace(GridUserInterface user, String spaceToken) throws DataAccessException;

  public void removeStorageSpace(String spaceToken) throws DataAccessException;

  public void updateStorageSpace(StorageSpaceTO ss) throws DataAccessException;

  public void updateAllStorageSpace(StorageSpaceTO ss) throws DataAccessException;

  public Collection getExpired(long currentTimeInSecond) throws DataAccessException ;

}
