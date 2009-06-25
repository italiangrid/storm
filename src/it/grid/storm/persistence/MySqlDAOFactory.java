package it.grid.storm.persistence;


import it.grid.storm.config.Configuration;
import it.grid.storm.persistence.dao.CopyChunkDAO;
import it.grid.storm.persistence.dao.PermissionDAO;
import it.grid.storm.persistence.dao.PtGChunkDAO;
import it.grid.storm.persistence.dao.PtPChunkDAO;
import it.grid.storm.persistence.dao.RequestSummaryDAO;
import it.grid.storm.persistence.dao.StorageAreaDAO;
import it.grid.storm.persistence.dao.StorageFileDAO;
import it.grid.storm.persistence.dao.StorageSpaceDAO;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.exceptions.PersistenceException;
import it.grid.storm.persistence.impl.mysql.StorageFileDAOMySql;
import it.grid.storm.persistence.impl.mysql.StorageSpaceDAOMySql;
import it.grid.storm.persistence.util.db.DBConnection;
import it.grid.storm.persistence.util.db.DBConnectionPool;
import it.grid.storm.persistence.util.db.DataBaseStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySqlDAOFactory implements DAOFactory {

    public static final String factoryName = "JDBC - MySQL DAO Factory";

    private static final Logger log = LoggerFactory.getLogger(MySqlDAOFactory.class);
    private static final DataBaseStrategy datasource = DataBaseStrategy.MYSQL;
    private static DataSourceConnectionFactory connFactory = null;
    private static MySqlDAOFactory factory = new MySqlDAOFactory();

    static
    {
        initializeDataSource();
    }

    /**
     *
     */
    private MySqlDAOFactory()
    {
        super();
        log.info("Choose "+factoryName);
    }

    public static MySqlDAOFactory getInstance()
    {
        return factory;
    }

    private static void initializeDataSource()
    {
        Configuration config = Configuration.getInstance();
        datasource.setDbUrl(config.getBE_PersistenceDBMSUrl());
        datasource.setDbName(config.getBE_PersistenceDBName());
        datasource.setDbUsr(config.getBE_PersistenceDBUserName());
        datasource.setDbPwd(config.getBE_PersistenceDBPassword());

        boolean pool = config.getBE_PersistencePoolDB();
        if (pool) {
            int maxActive = config.getBE_PersistencePoolDB_MaxActive();
            int maxWait = config.getBE_PersistencePoolDB_MaxWait();
            try {
                DBConnectionPool.initPool(datasource, maxActive, maxWait);
            }
            catch (PersistenceException ex) {
                log.error("Exception while setting up Pool", ex);
            }
            connFactory = DBConnectionPool.getPoolInstance();
        }
        else {
            try {
                connFactory = new DBConnection(datasource);
            }
            catch (PersistenceException ex1) {
                log.error("Exception while setting up DB connection", ex1);
            }
        }


    }

    /**
     * getStorageFileDAO
     *
     * @return StorageFileDAO
     * @throws DataAccessException
     * @todo Implement this it.grid.storm.persistence.DAOFactory method
     */
    public StorageFileDAO getStorageFileDAO() throws DataAccessException
    {
        return new StorageFileDAOMySql();
    }

    /**
     * Returns an implementation of StorageSpaceCatalog, specific to a particular
     * datastore.
     *
     * @throws DataAccessException
     * @return StorageSpaceDAO
     * @todo Implement this it.grid.storm.persistence.DAOFactory method
     */
    public StorageSpaceDAO getStorageSpaceDAO() throws DataAccessException
    {
        return new StorageSpaceDAOMySql();
    }

    /**
     *
     * @return String
     */
    @Override
    public String toString()
    {
        return factoryName;
    }

    /**
     * NOT IMPLEMENTED!
     */

    /**
     * getCopyChunkDAO
     *
     * @return CopyChunkDAO
     * @throws DataAccessException
     * @todo Implement this it.grid.storm.persistence.DAOFactory method
     */
    public CopyChunkDAO getCopyChunkDAO() throws DataAccessException
    {
        return null;
    }

    /**
     * getPermissionDAO
     *
     * @return PermissionDAO
     * @throws DataAccessException
     * @todo Implement this it.grid.storm.persistence.DAOFactory method
     */
    public PermissionDAO getPermissionDAO() throws DataAccessException
    {
        return null;
    }

    /**
     * getPtGChunkDAO
     *
     * @return PtGChunkDAO
     * @throws DataAccessException
     * @todo Implement this it.grid.storm.persistence.DAOFactory method
     */
    public PtGChunkDAO getPtGChunkDAO() throws DataAccessException
    {
        return null;
    }

    /**
     * getPtPChunkDAO
     *
     * @return PtPChunkDAO
     * @throws DataAccessException
     * @todo Implement this it.grid.storm.persistence.DAOFactory method
     */
    public PtPChunkDAO getPtPChunkDAO() throws DataAccessException
    {
        return null;
    }

    /**
     * getRequestSummaryDAO
     *
     * @return RequestSummaryDAO
     * @throws DataAccessException
     * @todo Implement this it.grid.storm.persistence.DAOFactory method
     */
    public RequestSummaryDAO getRequestSummaryDAO() throws DataAccessException
    {
        return null;
    }

    /**
     * getStorageAreaDAO
     *
     * @return StorageAreaDAO
     * @throws DataAccessException
     * @todo Implement this it.grid.storm.persistence.DAOFactory method
     */
    public StorageAreaDAO getStorageAreaDAO() throws DataAccessException
    {
        return null;
    }


}
