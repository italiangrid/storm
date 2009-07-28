package it.grid.storm.persistence.impl.mysql;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.persistence.PersistenceDirector;
import it.grid.storm.persistence.dao.AbstractDAO;
import it.grid.storm.persistence.dao.StorageSpaceDAO;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.StorageSpaceTO;
import it.grid.storm.persistence.util.helper.StorageSpaceSQLHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * find = con.prepareStatement("SELECT storm_get_filereq.rowid, storm_req.r_token, storm_get_filereq.from_surl, storm_get_filereq.lifetime, storm_get_filereq.s_token, storm_get_filereq.flags, storm_req.protocol, storm_get_filereq.actual_size, storm_get_filereq.status, storm_get_filereq.errstring, storm_get_filereq.pfn FROM storm_get_filereq, storm_req WHERE storm_get_filereq.r_token=storm_req.r_token AND storm_get_filereq.r_token=?"
 * );
 **/

public class StorageSpaceDAOMySql extends AbstractDAO implements StorageSpaceDAO {

    private static final Logger log = LoggerFactory.getLogger(StorageSpaceDAOMySql.class);

    private static String INSERT_NEW;
    private static String FIND_BY_OWNER;
    private static String REMOVE;

    private StorageSpaceSQLHelper helper;

    /**
     * CONSTRUCTOR
     */
    public StorageSpaceDAOMySql() {
        helper = new StorageSpaceSQLHelper(PersistenceDirector.getDataBase().getDbmsVendor());
    }

    /**
     * addStorageSpace
     * 
     * @param ss StorageSpace
     * @throws DataAccessException
     * @todo Implement this it.grid.storm.catalog.StorageSpaceDAO method
     */
    public void addStorageSpace(StorageSpaceTO ss) throws DataAccessException {
        String query = helper.insertQuery(ss);
        log.debug("INSERT query = " + query);
        Connection conn = getConnection();
        int res = -1;
        Statement stat = getStatement(conn);
        try {
            res = stat.executeUpdate(query);
            log.debug("UPDATE result = " + res);
            if (res == -1) {
                log.error("db error : " + query);
            }
        } catch (SQLException ex) {
            log.error("Error while executing INSERT query", ex);
            throw new DataAccessException("Error while executing INSERT query", ex);
        }
        releaseConnection(null, stat, conn);
    }

    /**
     * getStorageSpaceById
     * 
     * @param ssId Long
     * @return StorageSpace
     * @throws DataAccessException
     * @todo Implement this it.grid.storm.catalog.StorageSpaceDAO method
     */
    public StorageSpaceTO getStorageSpaceById(Long ssId) throws DataAccessException {
        return null;
    }

    /**
     * Returns a Collection of StorageSpaceTO owned by 'user' and with the specified alias
     * ('spaceAlias'). 'spaceAlias' can be NULL or empty and in these cases a Collection of all the
     * StorageSpaceTO owned by 'user' is returned.
     * 
     * @param owner VomsGridUser.
     * @param spaceAlias String.
     * @return Collection of StorageSpaceTO.
     * @throws DataAccessException
     * @todo Implement this it.grid.storm.catalog.StorageSpaceDAO method
     */
    public Collection getStorageSpaceByOwner(GridUserInterface owner, String spaceAlias)
            throws DataAccessException {
        StorageSpaceTO ssTO = null;
        Collection result = new LinkedList();
        String query = helper.selectBySpaceAliasQuery(owner, spaceAlias);
        log.debug("DB query = " + query);
        Connection conn = getConnection();
        ResultSet res = null;
        Statement stat = getStatement(conn);
        try {
            res = stat.executeQuery(query);
            log.debug("query result = " + res);
            if (res == null) {
                log.error("db error : " + query);
                throw new DataAccessException("No Storage Space found!");
            } else {
                // Fetch each row from the result set
                while (res.next()) {
                    ssTO = helper.makeStorageSpaceTO(res);
                    result.add(ssTO);
                }
            }
        } catch (SQLException e) {
            log.error("Error while executing DB query", e);
            throw new DataAccessException("Error while executing DB query", e);
        }
        releaseConnection(res, stat, conn);

        return result;
    }

    /**
     * Returns a Collection of StorageSpaceTO owned by 'VO'.
     * 
     * @param voname Vo.
     * @return Collection of StorageSpaceTO.
     * @throws DataAccessException
     * @todo Implement this it.grid.storm.catalog.StorageSpaceDAO method
     */
    public Collection getStorageSpaceBySpaceType(String stype) throws DataAccessException {
        StorageSpaceTO ssTO = null;
        Collection result = new LinkedList();
        String query = helper.selectBySpaceType(stype);
        log.debug("DB query = " + query);
        Connection conn = getConnection();
        ResultSet res = null;
        Statement stat = getStatement(conn);
        try {
            res = stat.executeQuery(query);
            log.debug("query result = " + res);
            if (res == null) {
                log.error("db error : " + query);
                throw new DataAccessException("No Storage Space found!");
            } else {
                // Fetch each row from the result set
                while (res.next()) {
                    ssTO = helper.makeStorageSpaceTO(res);
                    result.add(ssTO);
                }
            }
        } catch (SQLException e) {
            log.error("Error while executing DB query", e);
            throw new DataAccessException("Error while executing DB query", e);
        }
        releaseConnection(res, stat, conn);

        return result;
    }

    /**
     * Returns a Collection of StorageSpaceTO with the specified alias ('spaceAlias'). 'spaceAlias'
     * can not be be NULL or empty.
     * 
     * @param spaceAlias String.
     * @return Collection of StorageSpaceTO.
     * @throws DataAccessException
     * @todo Implement this it.grid.storm.catalog.StorageSpaceDAO method
     */
    public Collection getStorageSpaceByAliasOnly(String spaceAlias) throws DataAccessException {
        StorageSpaceTO ssTO = null;
        Collection result = new LinkedList();
        String query = helper.selectBySpaceAliasOnlyQuery(spaceAlias);
        log.debug("DB query = " + query);
        Connection conn = getConnection();
        ResultSet res = null;
        Statement stat = getStatement(conn);
        try {
            res = stat.executeQuery(query);
            log.debug("query result = " + res);
            if (res == null) {
                log.error("db error : " + query);
                throw new DataAccessException("No Storage Space found!");
            } else {
                // Fetch each row from the result set
                while (res.next()) {
                    ssTO = helper.makeStorageSpaceTO(res);
                    result.add(ssTO);
                }
            }
        } catch (SQLException e) {
            log.error("Error while executing DB query", e);
            throw new DataAccessException("Error while executing DB query", e);
        }
        releaseConnection(res, stat, conn);

        return result;
    }

    /**
     * getStorageSpaceByToken
     * 
     * @param token TSpaceToken
     * @return StorageSpace
     * @throws DataAccessException
     * @todo Implement this it.grid.storm.catalog.StorageSpaceDAO method
     */
    public StorageSpaceTO getStorageSpaceByToken(String token) throws DataAccessException {

        StorageSpaceTO ssTO = null;
        String query = helper.selectByTokenQuery(token);
        log.debug("SELECT query = " + query);
        Connection conn = getConnection();
        ResultSet res = null;
        Statement stat = getStatement(conn);
        try {
            res = stat.executeQuery(query);
            log.debug("SELECT result = " + res);
            if (res == null) {
                log.error("db error : " + query);
                throw new DataAccessException("Storage Space with token = '" + token + "' not found!");
            } else {
                /**
                 * @todo: Check size of result set. Only one storage space can hold the token.
                 **/

                // Fetch each row from the result set
                while (res.next()) {
                    ssTO = helper.makeStorageSpaceTO(res);
                }
            }
        } catch (SQLException ex) {
            log.error("Error while executing INSERT query", ex);
            throw new DataAccessException("Error while executing INSERT query", ex);
        }
        releaseConnection(res, stat, conn);

        return ssTO;
    }

    /**
     * removeStorageSpace
     * 
     * @param ss StorageSpace
     * @throws DataAccessException
     * @todo Implement this it.grid.storm.catalog.StorageSpaceDAO method
     */
    public void removeStorageSpace(GridUserInterface user, String spaceToken) throws DataAccessException {
        String query = helper.removeByTokenQuery(user, spaceToken);
        log.debug("query = " + query);
        Connection conn = getConnection();
        int res = -1;
        Statement stat = getStatement(conn);
        try {
            res = stat.executeUpdate(query);
            log.debug("Number of rows removed: " + res);
            if (res == -1) {
                log.error("db error : " + query);
                throw new DataAccessException("Storage Space with token = '" + spaceToken + "' not found!");
            }
        } catch (SQLException ex) {
            log.error("Error while executing DELETE query", ex);
            throw new DataAccessException("Error while executing DELETE query", ex);
        }
        releaseConnection(null, stat, conn);
    }

    public Collection findAll() throws DataAccessException {
        return null;
    }

    /**
     * removeStorageSpace only by spaceToken
     * 
     * @param ss StorageSpace
     * @throws DataAccessException
     * @todo Implement this it.grid.storm.catalog.StorageSpaceDAO method
     */
    public void removeStorageSpace(String spaceToken) throws DataAccessException {
        String query = helper.removeByTokenQuery(spaceToken);
        log.debug("query = " + query);
        Connection conn = getConnection();
        int res = -1;
        Statement stat = getStatement(conn);
        try {
            res = stat.executeUpdate(query);
            log.debug("Number of rows removed: " + res);
            if (res == -1) {
                log.error("db error : " + query);
                throw new DataAccessException("Storage Space with token = '" + spaceToken + "' not found!");
            }
        } catch (SQLException ex) {
            log.error("Error while executing DELETE query", ex);
            throw new DataAccessException("Error while executing DELETE query", ex);
        }
        releaseConnection(null, stat, conn);
    }

    /**
     * 
     * @param ss StorageSpaceTO
     * @throws DataAccessException
     */
    public void updateStorageSpace(StorageSpaceTO ss) throws DataAccessException {
        long freeSpace = (ss.getUnusedSize());
        /**
         * @todo: Update all changeable column! not only FreeSpace.
         */
        String query = helper.updateFreeSpaceByTokenQuery(ss.getSpaceToken(), freeSpace);
        log.debug("UPDATE query = " + query);
        Connection conn = getConnection();
        int res = -1;
        Statement stat = getStatement(conn);
        try {
            res = stat.executeUpdate(query);
            log.debug("UPDATE result = " + res);
            if (res == -1) {
                log.error("db error : " + query);
            }
        } catch (SQLException ex) {
            log.error("Error while executing INSERT query", ex);
            throw new DataAccessException("Error while executing UPDATE query", ex);
        }
        releaseConnection(null, stat, conn);
    }

    /**
     * 
     * @param ss StorageSpaceTO
     * @throws DataAccessException
     */
    public void updateAllStorageSpace(StorageSpaceTO ss) throws DataAccessException {
        long freeSpace = (ss.getUnusedSize());
        /**
         * @todo: Update all changeable column! not only FreeSpace.
         */
        String query = helper.updateAllByTokenQuery(ss.getSpaceToken(),
                                                    ss.getAlias(),
                                                    ss.getGuaranteedSize(),
                                                    ss.getSpaceFile());
        log.debug("UPDATE query = " + query);
        Connection conn = getConnection();
        int res = -1;
        Statement stat = getStatement(conn);
        try {
            res = stat.executeUpdate(query);
            log.debug("UPDATE result = " + res);
            if (res == -1) {
                log.error("db error : " + query);
            }
        } catch (SQLException ex) {
            log.error("Error while executing INSERT query", ex);
            throw new DataAccessException("Error while executing UPDATE query", ex);
        }
        releaseConnection(null, stat, conn);
    }

    /**
     * Method used to retrieve the set of StorageTO for expired space.
     * 
     * @param long timeInSecond
     * @return Collection of transfer object
     */
    public Collection getExpired(long currentTimeInSecond) throws DataAccessException {
        StorageSpaceTO ssTO = null;
        Collection result = new LinkedList();
        String query = helper.selectExpiredQuery(currentTimeInSecond);
        log.debug("DB query = " + query);
        Connection conn = getConnection();
        ResultSet res = null;
        Statement stat = getStatement(conn);
        try {
            res = stat.executeQuery(query);
            log.debug("query result = " + res);
            if (res == null) {
                log.error("db error : " + query);
                throw new DataAccessException("No Storage Space found!");
            } else {
                // Fetch each row from the result set
                while (res.next()) {
                    ssTO = helper.makeStorageSpaceTO(res);
                    result.add(ssTO);
                }
            }
        } catch (SQLException e) {
            log.error("Error while executing DB query", e);
            throw new DataAccessException("Error while executing DB query", e);
        }
        releaseConnection(res, stat, conn);

        return result;
    }
}
