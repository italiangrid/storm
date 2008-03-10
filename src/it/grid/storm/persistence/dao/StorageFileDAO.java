package it.grid.storm.persistence.dao;


import java.util.Collection;
import it.grid.storm.common.types.PFN;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.ResourceRuleData;
import it.grid.storm.persistence.model.StorageFileTO;
import it.grid.storm.srm.types.TSURL;


/**
 *
 * Logical File Data Access Object (DAO)
 *
 * <a href="http://java.sun.com/blueprints/corej2eepatterns/Patterns/DataAccessObject.html">
 * DAO pattern</a>
 *
 *
 */
public interface StorageFileDAO {
  public StorageFileTO getStorageFileById(Long ssId) throws DataAccessException;

  public void addStorageFile(StorageFileTO sfFileData) throws DataAccessException;

  public StorageFileTO getStorageFileByPFN(PFN pfn) throws DataAccessException;

  public StorageFileTO getStorageFileBySURL(TSURL surl) throws DataAccessException;

  public void removeStorageFile(StorageFileTO sfFileData) throws DataAccessException;

  public Collection getStorageFilesByResourceRule(ResourceRuleData rr) throws DataAccessException;
  // public void makePersistent(StorageFileData storageFileData) throws InfrastructureException;

//  public void makeTransient(StorageFileData storageFileData) throws InfrastructureException;


}
