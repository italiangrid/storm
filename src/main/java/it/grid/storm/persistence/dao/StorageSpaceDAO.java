/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.persistence.dao;

import java.util.Collection;
import java.util.Date;

import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.StorageSpaceTO;

import it.grid.storm.griduser.GridUserInterface;

/**
 * 
 * Storage Space Data Access Object (DAO)
 * 
 * <a href=
 * "http://java.sun.com/blueprints/corej2eepatterns/Patterns/DataAccessObject.html"
 * > DAO pattern</a>
 * 
 * 
 */
public interface StorageSpaceDAO {

	public StorageSpaceTO getStorageSpaceById(Long ssId)
		throws DataAccessException;

	public Collection<StorageSpaceTO> getStorageSpaceByOwner(
		GridUserInterface owner, String spaceAlias) throws DataAccessException;

	/*public Collection<StorageSpaceTO> getStorageSpaceBySpaceType(String stype)
		throws DataAccessException;*/

	public Collection<StorageSpaceTO> getStorageSpaceByAliasOnly(String spaceAlias)
		throws DataAccessException;

	public StorageSpaceTO getStorageSpaceByToken(String token)
		throws DataAccessException;

	public Collection<StorageSpaceTO> findAll() throws DataAccessException;

	public void addStorageSpace(StorageSpaceTO ss) throws DataAccessException;

	public void removeStorageSpace(GridUserInterface user, String spaceToken)
		throws DataAccessException;

	public void removeStorageSpace(String spaceToken) throws DataAccessException;

	public void updateStorageSpace(StorageSpaceTO ss) throws DataAccessException;

	public void updateStorageSpaceFreeSpace(StorageSpaceTO ss)
		throws DataAccessException;

	public void updateAllStorageSpace(StorageSpaceTO ss)
		throws DataAccessException;

	public Collection<StorageSpaceTO> getExpired(long currentTimeInSecond)
		throws DataAccessException;

	public Collection<StorageSpaceTO> getStorageSpaceByUnavailableUsedSpace(
		long unavailableSizeValue) throws DataAccessException;

	/*public Collection<StorageSpaceTO> getStorageSpaceByPreviousLastUpdate(
		Date lastUpdateTimestamp) throws DataAccessException;*/

}
