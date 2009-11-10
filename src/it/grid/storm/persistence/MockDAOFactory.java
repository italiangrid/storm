package it.grid.storm.persistence;

import it.grid.storm.persistence.dao.CopyChunkDAO;
import it.grid.storm.persistence.dao.PermissionDAO;
import it.grid.storm.persistence.dao.PtGChunkDAO;
import it.grid.storm.persistence.dao.PtPChunkDAO;
import it.grid.storm.persistence.dao.RequestSummaryDAO;
import it.grid.storm.persistence.dao.StorageAreaDAO;
import it.grid.storm.persistence.dao.StorageSpaceDAO;
import it.grid.storm.persistence.dao.TapeRecallDAO;
import it.grid.storm.persistence.exceptions.DataAccessException;

public class MockDAOFactory implements DAOFactory {
    private final String factoryName = "MOCK DAO Factory";

    public MockDAOFactory() {
    }

    /**
     * getCopyChunkDAO
     * 
     * @return CopyChunkDAO
     * @throws DataAccessException
     * @todo Implement this it.grid.storm.persistence.DAOFactory method
     */
    public CopyChunkDAO getCopyChunkDAO() throws DataAccessException {
        return null;
    }

    /**
     * getPermissionDAO
     * 
     * @return PermissionDAO
     * @throws DataAccessException
     * @todo Implement this it.grid.storm.persistence.DAOFactory method
     */
    public PermissionDAO getPermissionDAO() throws DataAccessException {
        return null;
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

    /**
     * Returns an implementation of StorageSpaceCatalog, specific to a particular datastore.
     * 
     * @throws DataAccessException
     * @return StorageSpaceDAO
     * @todo Implement this it.grid.storm.persistence.DAOFactory method
     */
    public StorageSpaceDAO getStorageSpaceDAO() throws DataAccessException {
        return null;
    }

    /**
     * Returns an implementation of TapeRecallCatalog, specific to a particular datastore.
     * 
     * @throws DataAccessException
     * @return TapeReallDAO
     * @todo Implement this it.grid.storm.persistence.DAOFactory method
     */
    public TapeRecallDAO getTapeRecallDAO() throws DataAccessException {
        return null;
    }

    @Override
    public String toString() {
        return factoryName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see it.grid.storm.persistence.DAOFactory#getTapeRecallDAO(boolean)
     */
    public TapeRecallDAO getTapeRecallDAO(boolean test) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
}
