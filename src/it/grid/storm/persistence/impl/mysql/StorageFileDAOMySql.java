package it.grid.storm.persistence.impl.mysql;


import java.util.Collection;
import it.grid.storm.common.types.PFN;
import it.grid.storm.persistence.dao.StorageFileDAO;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.exceptions.InfrastructureException;
import it.grid.storm.persistence.model.ResourceRuleData;
import it.grid.storm.persistence.model.StorageFileTO;
import it.grid.storm.srm.types.TSURL;


public class StorageFileDAOMySql implements StorageFileDAO {
  /**
   * addStorageFile
   *
   * @param stori StoRI
   * @throws DataAccessException
   * @todo Implement this it.grid.storm.catalog.StorageFileDAO method
   */
  public void addStorageFile(StorageFileTO storageFile) throws DataAccessException
  {
  }

  /**
   * getStorageFileById
   *
   * @param ssId Long
   * @return StorageFileData
   * @throws DataAccessException
   * @todo Implement this it.grid.storm.catalog.StorageFileDAO method
   */
  public StorageFileTO getStorageFileById(Long ssId) throws DataAccessException
  {
    return null;
  }

  /**
   * getStorageFileByPFN
   *
   * @param pfn PFN
   * @return StorageFileData
   * @throws DataAccessException
   * @todo Implement this it.grid.storm.catalog.StorageFileDAO method
   */
  public StorageFileTO getStorageFileByPFN(PFN pfn) throws DataAccessException
  {
    return null;
  }

  /**
   * getStorageFileBySURL
   *
   * @param surl TSURL
   * @return StorageFileData
   * @throws DataAccessException
   * @todo Implement this it.grid.storm.catalog.StorageFileDAO method
   */
  public StorageFileTO getStorageFileBySURL(TSURL surl) throws DataAccessException
  {
    return null;
  }

  /**
   * getStorageFilesByResourceRule
   *
   * @param rr ResourceRule
   * @return Collection
   * @throws DataAccessException
   * @todo Implement this it.grid.storm.catalog.StorageFileDAO method
   */
  public Collection getStorageFilesByResourceRule(ResourceRuleData rr) throws DataAccessException
  {
    return null;
  }

  /**
   * removeStorageFile
   *
   * @param storageFile StorageFileData
   * @throws DataAccessException
   * @todo Implement this it.grid.storm.catalog.StorageFileDAO method
   */
  public void removeStorageFile(StorageFileTO storageFile) throws DataAccessException
  {
  }

  public void makePersistent(StorageFileTO storageFileData) throws InfrastructureException
  {

  }

  public void makeTransient(StorageFileTO storageFileData) throws InfrastructureException
  {

  }
}
