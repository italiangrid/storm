/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
import java.util.Date;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * find = con.prepareStatement("SELECT storm_get_filereq.rowid, storm_req.r_token, storm_get_filereq.from_surl, storm_get_filereq.lifetime, storm_get_filereq.s_token, storm_get_filereq.flags, storm_req.protocol, storm_get_filereq.actual_size, storm_get_filereq.status, storm_get_filereq.errstring, storm_get_filereq.pfn FROM storm_get_filereq, storm_req WHERE storm_get_filereq.r_token=storm_req.r_token AND storm_get_filereq.r_token=?"
 * );
 **/

public class StorageSpaceDAOMySql extends AbstractDAO implements StorageSpaceDAO {

    private static final Logger log = LoggerFactory.getLogger(StorageSpaceDAOMySql.class);

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
     */
	public void addStorageSpace(StorageSpaceTO ss) throws DataAccessException {

		String query = helper.insertQuery(ss);
		log.debug("INSERT query = " + query);
		Connection conn = getConnection();
		Statement stat = null;
		try
		{
			stat = getStatement(conn);
			int res = stat.executeUpdate(query);
			log.debug("INSERT result = " + res);
			if(res <= 0)
			{
				log.error("No row inserted for statement : " + query);
				throw new DataAccessException("No rows inserted for Storage Space");	
			}
		} catch(SQLException ex)
		{
			log.error("Error while executing INSERT query", ex);
			throw new DataAccessException("Error while executing INSERT query", ex);
		}
		finally
		{
			releaseConnection(null, stat, conn);	
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
     * @throws DataAccessException
     */
    public Collection<StorageSpaceTO> getStorageSpaceByOwner(GridUserInterface owner, String spaceAlias)
            throws DataAccessException
    {

        StorageSpaceTO ssTO = null;
        Collection<StorageSpaceTO> result = new LinkedList<StorageSpaceTO>();
        String query = helper.selectBySpaceAliasQuery(owner, spaceAlias);
        log.debug("DB query = " + query);

        Connection conn = getConnection();
        Statement stat = null;
        ResultSet res = null;
        try
        {
            stat = getStatement(conn);
            res = stat.executeQuery(query);
            log.debug("query result = " + res);
            if (res.first() == false)
            {
                log.info("No rows found for query : " + query);
            }
            else
            {
                // Fetch each row from the result set
                do
                {
                    ssTO = helper.makeStorageSpaceTO(res);
                    result.add(ssTO);
                } while (res.next());
            }
        }
        catch (SQLException e)
        {
            log.error("Error while executing DB query", e);
            throw new DataAccessException("Error while executing DB query", e);
        }
        finally
        {
            releaseConnection(res, stat, conn);
        }
        return result;
    }

    /**
     * Returns a Collection of StorageSpaceTO owned by 'VO'.
     * 
     * @param voname Vo.
     * @return Collection of StorageSpaceTO.
     * @throws DataAccessException
     */
    public Collection<StorageSpaceTO> getStorageSpaceBySpaceType(String stype) throws DataAccessException
    {
        StorageSpaceTO ssTO = null;
        Collection<StorageSpaceTO> result = new LinkedList<StorageSpaceTO>();
        
        String query = helper.selectBySpaceType(stype);
        log.debug("DB query = " + query);
        
        Connection conn = getConnection();
        ResultSet res = null;
        Statement stat = null;
        try
        {
            stat = getStatement(conn);
            res = stat.executeQuery(query);
            log.debug("query result = " + res);
            if (res.first() == false)
            {
                log.info("No rows found for query : " + query);
            }
            else
            {
                // Fetch each row from the result set
                do
                {
                    ssTO = helper.makeStorageSpaceTO(res);
                    result.add(ssTO);
                } while (res.next());
            }
        }
        catch (SQLException e)
        {
            log.error("Error while executing DB query", e);
            throw new DataAccessException("Error while executing DB query", e);
        }
        finally
        {
            releaseConnection(res, stat, conn);
        }
        return result;
    }

    /**
     * Returns a Collection of StorageSpaceTO with the specified alias ('spaceAlias'). 'spaceAlias'
     * can not be be NULL or empty.
     * 
     * @param spaceAlias String.
     * @return Collection of StorageSpaceTO.
     * @throws DataAccessException
     */
    public Collection<StorageSpaceTO> getStorageSpaceByAliasOnly(String spaceAlias) throws DataAccessException
    {
        StorageSpaceTO ssTO = null;
        Collection<StorageSpaceTO> result = new LinkedList<StorageSpaceTO>();
        
        String query = helper.selectBySpaceAliasOnlyQuery(spaceAlias);
        log.debug("DB query = " + query);
        
        Connection conn = getConnection();
        ResultSet res = null;
        Statement stat = null;
        try
        {
            stat = getStatement(conn);
            res = stat.executeQuery(query);
            log.debug("query result = " + res);
            if (res.first() == false)
            {
                log.info("No rows found for query : " + query);
            }
            else
            {
                // Fetch each row from the result set
                do
                {
                    ssTO = helper.makeStorageSpaceTO(res);
                    result.add(ssTO);
                } while (res.next());
            }
        }
        catch (SQLException e)
        {
            log.error("Error while executing DB query", e);
            throw new DataAccessException("Error while executing DB query", e);
        }
        finally
        {
            releaseConnection(res, stat, conn);
        }
        return result;
    }

    /**
     * getStorageSpaceByToken
     * 
     * @param token TSpaceToken
     * @return StorageSpace
     * @throws DataAccessException
     */
    public StorageSpaceTO getStorageSpaceByToken(String token) throws DataAccessException
    {
        StorageSpaceTO ssTO = null;
        
        String query = helper.selectByTokenQuery(token);
        log.debug("SELECT query = " + query);
        
        Connection conn = getConnection();
        ResultSet res = null;
        Statement stat = null;
        try
        {
            stat = getStatement(conn);
            res = stat.executeQuery(query);
            log.debug("SELECT result = " + res);
            if (res.first() == false)
            {
                log.info("No rows found for query : " + query);
                throw new DataAccessException("No storage space found for token" + token);
            }
            else
            {
                //take the first
                ssTO = helper.makeStorageSpaceTO(res);
            }
        }
        catch (SQLException ex)
        {
            log.error("Error while executing SELECT query", ex);
            throw new DataAccessException("Error while executing INSERT query", ex);
        }
        finally
        {
            releaseConnection(res, stat, conn);
        }
        return ssTO;
    }

    @Override
    public Collection<StorageSpaceTO> getStorageSpaceByUnavailableUsedSpace(long unavailableSizeValue) throws DataAccessException
    {
        StorageSpaceTO ssTO = null;
        Collection<StorageSpaceTO> result = new LinkedList<StorageSpaceTO>();
        
        String query = helper.selectByUnavailableUsedSpaceSizeQuery(unavailableSizeValue);
        log.debug("SELECT query = " + query);
        
        Connection conn = getConnection();
        ResultSet res = null;
        Statement stat = null;
        try
        {
            stat = getStatement(conn);
            res = stat.executeQuery(query);
            log.debug("SELECT result = " + res);
            if (res.first() == false)
            {
                log.info("No rows found for query : " + query);
            }
            else
            {
                // Fetch each row from the result set
                do
                {
                    ssTO = helper.makeStorageSpaceTO(res);
                    result.add(ssTO);
                } while (res.next());
            }
        }
        catch (SQLException ex)
        {
            log.error("Error while executing SELECT query", ex);
            throw new DataAccessException("Error while executing INSERT query", ex);
        }
        finally
        {
            releaseConnection(res, stat, conn);
        }
        return result;
    }

    @Override
    public Collection<StorageSpaceTO> getStorageSpaceByPreviousLastUpdate(Date lastUpdateTimestamp) throws DataAccessException
    {
        StorageSpaceTO ssTO = null;
        Collection<StorageSpaceTO> result = new LinkedList<StorageSpaceTO>();
        
        String query = helper.selectByPreviousOrNullLastUpdateQuery(lastUpdateTimestamp.getTime());
        log.debug("SELECT query = " + query);
        
        Connection conn = getConnection();
        ResultSet res = null;
        Statement stat = null;
        try
        {
            stat = getStatement(conn);
            res = stat.executeQuery(query);
            log.debug("SELECT result = " + res);
            if (res.first() == false)
            {
                log.info("No rows found for query : " + query);
            }
            else
            {
                // Fetch each row from the result set
                do
                {
                    ssTO = helper.makeStorageSpaceTO(res);
                    result.add(ssTO);
                } while (res.next());
            }
        }
        catch (SQLException ex)
        {
            log.error("Error while executing SELECT query", ex);
            throw new DataAccessException("Error while executing INSERT query", ex);
        }
        finally
        {
            releaseConnection(res, stat, conn);
        }
        return result;
    }
    
    /**
     * removeStorageSpace
     * 
     * @param ss StorageSpace
     * @throws DataAccessException
     */
    public void removeStorageSpace(GridUserInterface user, String spaceToken) throws DataAccessException
    {
        String query = helper.removeByTokenQuery(user, spaceToken);
        log.debug("query = " + query);

        Connection conn = getConnection();
        Statement stat = null;
        try
        {
            stat = getStatement(conn);
            int res = stat.executeUpdate(query);
            log.debug("Number of rows removed: " + res);
            if (res <= 0)
            {
                log.error("Unable to remove Storage Space with token = '" + spaceToken + "' for user '"
                        + user.getDn() + "' not found!");
                throw new DataAccessException("Storage Space with token = '" + spaceToken + "' for user '"
                        + user.getDn() + "' not found!");
            }
        }
        catch (SQLException ex)
        {
            log.error("Error while executing DELETE query", ex);
            throw new DataAccessException("Error while executing DELETE query", ex);
        }
        finally
        {
            releaseConnection(null, stat, conn);
        }
    }

    /**
     * removeStorageSpace only by spaceToken
     * 
     * @param ss StorageSpace
     * @throws DataAccessException
     */
    public void removeStorageSpace(String spaceToken) throws DataAccessException
    {
        String query = helper.removeByTokenQuery(spaceToken);
        log.debug("query = " + query);
        Connection conn = getConnection();
        Statement stat = null;
        try
        {
            stat = getStatement(conn);
            int res = stat.executeUpdate(query);
            log.debug("Number of rows removed: " + res);
            if (res <= 0)
            {
                log.error("Unable to remove Storage Space with token = '" + spaceToken + "' not found!");
                throw new DataAccessException("Storage Space with token = '" + spaceToken + "' not found!");
            }
        }
        catch (SQLException ex)
        {
            log.error("Error while executing DELETE query", ex);
            throw new DataAccessException("Error while executing DELETE query", ex);
        }
        finally
        {
            releaseConnection(null, stat, conn);
        }
    }

    /**
     * 
     * @param ssTO StorageSpaceTO
     * @throws DataAccessException
     */
    public void updateStorageSpace(StorageSpaceTO ssTO) throws DataAccessException
    {
    	
        String query = helper.updateByAliasAndTokenQuery(ssTO);
        
        log.debug("UPDATE query = " + query);
        
        Connection conn = getConnection();
        Statement stat = null;
        try
        {
            stat = getStatement(conn);
            int res = stat.executeUpdate(query);
            log.debug("UPDATE row count = " + res);
            if (res != 1)
            {
                if(res < 1)
                {
                    log.error("No storage space rows updated by query : " + query);
                }
                else
                {
                    log.warn("More than a single storage space rows updated by query : " + query + " updated " + res + " rows");
                }
            }
        }
        catch (SQLException ex)
        {
            log.error("Error while executing UPDATE query", ex);
            throw new DataAccessException("Error while executing UPDATE query", ex);
        }
        finally
        {
            releaseConnection(null, stat, conn);
        }
    }

    
    /**
     * 
     * @param ssTO StorageSpaceTO
     * @throws DataAccessException
     */
    public void updateStorageSpaceFreeSpace(StorageSpaceTO ssTO) throws DataAccessException
    {
        
        long freeSpace = ssTO.getFreeSize();
        String query = helper.updateFreeSpaceByTokenQuery(ssTO.getSpaceToken(), freeSpace, ssTO.getUpdateTime());
        log.debug("UPDATE query = " + query);
              
        Connection conn = getConnection();
        Statement stat = null;
        try
        {
            stat = getStatement(conn);
            int res = stat.executeUpdate(query);
            log.debug("UPDATE row count = " + res);
            if (res <= 0)
            {
                log.error("No storage space rows updated by query : " + query);
            }
        }
        catch (SQLException ex)
        {
            log.error("Error while executing UPDATE query", ex);
            throw new DataAccessException("Error while executing UPDATE query", ex);
        }
        finally
        {
            releaseConnection(null, stat, conn);
        }
    }
    
    /**
     * 
     * @param ssTO StorageSpaceTO
     * @throws DataAccessException
     */
    public void updateAllStorageSpace(StorageSpaceTO ssTO) throws DataAccessException
    {
        /**
         * @todo: Update all changeable column! not only FreeSpace.
         */
//        String query = helper.updateAllByTokenQuery(ss.getSpaceToken(),
//                                                    ss.getAlias(),
//                                                    ss.getGuaranteedSize(),
//                                                    ss.getSpaceFile(), ss.getUpdateTime());
        String query = helper.updateByTokenQuery(ssTO);
        log.debug("UPDATE query = " + query);

        Connection conn = getConnection();
        Statement stat = null;
        try
        {
            stat = getStatement(conn);
            int res = stat.executeUpdate(query);
            log.debug("UPDATE row count = " + res);
            if (res != 1)
            {
                if(res < 1)
                {
                    log.error("No storage space rows updated by query : " + query);
                }
                else
                {
                    log.warn("More than a single storage space rows updated by query : " + query + " updated " + res + " rows");
                }
            }
        }
        catch (SQLException ex)
        {
            log.error("Error while executing UPDATE query", ex);
            throw new DataAccessException("Error while executing UPDATE query", ex);
        }
        finally
        {
            releaseConnection(null, stat, conn);
        }
    }

    /**
     * Method used to retrieve the set of StorageTO for expired space.
     * 
     * @param long timeInSecond
     * @return Collection of transfer object
     */
    public Collection<StorageSpaceTO> getExpired(long currentTimeInSecond) throws DataAccessException
    {
        StorageSpaceTO ssTO = null;
        Collection<StorageSpaceTO> result = new LinkedList<StorageSpaceTO>();
        
        String query = helper.selectExpiredQuery(currentTimeInSecond);
        log.debug("DB query = " + query);
        
        Connection conn = getConnection();
        ResultSet res = null;
        Statement stat = null;
        try
        {
            stat = getStatement(conn);
            res = stat.executeQuery(query);
            log.debug("query result = " + res);
            if (res.first() == false)
            {
                log.info("No rows found for query : " + query);
                throw new DataAccessException("No storage space expired found at time " + currentTimeInSecond);
            }
            else
            {
                // Fetch each row from the result set
                do
                {
                    ssTO = helper.makeStorageSpaceTO(res);
                    result.add(ssTO);
                } while (res.next());
            }
        }
        catch (SQLException e)
        {
            log.error("Error while executing DB query", e);
            throw new DataAccessException("Error while executing DB query", e);
        }
        finally
        {
            releaseConnection(res, stat, conn);
        }
        return result;
    }
}
