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

package it.grid.storm.persistence.util.helper;

import it.grid.storm.persistence.model.StorageSpaceTO;
import it.grid.storm.persistence.util.db.InsertBuilder;
import it.grid.storm.persistence.util.db.SQLHelper;
import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import it.grid.storm.common.types.VO;

import it.grid.storm.griduser.GridUserInterface;

public class StorageSpaceSQLHelper extends SQLHelper {

	private final static String TABLE_NAME = "storage_space";
	private final static HashMap<String, String> COLS = new HashMap<String, String>();

	private static final String[] COLUMN_NAMES = { "SS_ID", "USERDN", "VOGROUP",
		"ALIAS", "SPACE_TOKEN", "CREATED", "TOTAL_SIZE", "GUAR_SIZE", "FREE_SIZE",
		"SPACE_FILE", "STORAGE_INFO", "LIFETIME", "SPACE_TYPE", "USED_SIZE",
		"BUSY_SIZE", "UNAVAILABLE_SIZE", "AVAILABLE_SIZE", "RESERVED_SIZE",
		"UPDATE_TIME" };

	static {
		COLS.put("storageSpaceId", "SS_ID");
		COLS.put("ownerName", "USERDN");
		COLS.put("ownerVO", "VOGROUP");
		COLS.put("alias", "ALIAS");
		COLS.put("token", "SPACE_TOKEN");
		COLS.put("created", "CREATED");
		COLS.put("spaceFile", "SPACE_FILE");
		COLS.put("storaqeInfo", "STORAGE_INFO");
		COLS.put("lifeTime", "LIFETIME");
		COLS.put("spaceType", "SPACE_TYPE");
		COLS.put("total_size", "TOTAL_SIZE");
		COLS.put("guar_size", "GUAR_SIZE");
		COLS.put("free_size", "FREE_SIZE");
		COLS.put("used_size", "USED_SIZE");
		COLS.put("busy_size", "BUSY_SIZE");
		COLS.put("unavailable_size", "UNAVAILABLE_SIZE");
		COLS.put("available_size", "AVAILABLE_SIZE");
		COLS.put("reserved_size", "RESERVED_SIZE");
		COLS.put("update_time", "UPDATE_TIME");
	}

	private InsertBuilder builder;

	/**
	 * CONSTRUCTOR
	 */
	public StorageSpaceSQLHelper(String dbmsVendor) {

		super(dbmsVendor);
	}

	/**
	 * 
	 * @return String[]
	 */
	public String[] getColumnNames() {

		return COLUMN_NAMES;
	}

	/**
	 * INSERT NEW ROW into TABLE
	 * 
	 * @param ssTO
	 *          StorageSpaceTO
	 * @return String
	 */
	public String insertQuery(StorageSpaceTO ssTO) {

		builder = new InsertBuilder();
		builder.setTable(TABLE_NAME);
		if (ssTO != null) {
			if (ssTO.getOwnerName() != null) {
				builder.addColumnAndData((String) COLS.get("ownerName"),
					format(ssTO.getOwnerName()));
			}
			builder.addColumnAndData(COLS.get("ownerVO"),
				format(getVOName(ssTO.getVoName())));
			if (ssTO.getAlias() != null) {
				builder.addColumnAndData((String) COLS.get("alias"),
					format(ssTO.getAlias()));
			}
			if (ssTO.getSpaceToken() != null) {
				builder.addColumnAndData((String) COLS.get("token"),
					format(ssTO.getSpaceToken()));
			}
			if (ssTO.getCreated() != null) {
				builder.addColumnAndData((String) COLS.get("created"),
					format(ssTO.getCreated()));
			}
			if (ssTO.getSpaceFile() != null) {
				builder.addColumnAndData((String) COLS.get("spaceFile"),
					format(ssTO.getSpaceFile()));
			}
			if (ssTO.getStorageInfo() != null) {
				builder.addColumnAndData((String) COLS.get("storaqeInfo"),
					format(ssTO.getStorageInfo()));
			}
			if (ssTO.getLifetime() != -1) {
				builder.addColumnAndData((String) COLS.get("lifeTime"),
					format(ssTO.getLifetime()));
			}
			if (ssTO.getSpaceType() != null) {
				builder.addColumnAndData((String) COLS.get("spaceType"),
					format(ssTO.getSpaceType()));
			}
			if ((ssTO.getTotalSize() != 0) || (ssTO.getTotalSize() != -1)) {
				builder.addColumnAndData((String) COLS.get("total_size"),
					format(ssTO.getTotalSize()));
			}
			if ((ssTO.getGuaranteedSize() != 0) || (ssTO.getGuaranteedSize() != -1)) {
				builder.addColumnAndData((String) COLS.get("guar_size"),
					format(ssTO.getGuaranteedSize()));
			}
			if ((ssTO.getFreeSize() != 0) || (ssTO.getFreeSize() != -1)) {
				builder.addColumnAndData((String) COLS.get("free_size"),
					format(ssTO.getFreeSize()));
			}
			if ((ssTO.getUsedSize() != 0) || (ssTO.getUsedSize() != -1)) {
				builder.addColumnAndData((String) COLS.get("used_size"),
					format(ssTO.getUsedSize()));
			}
			if ((ssTO.getBusySize() != 0) || (ssTO.getBusySize() != -1)) {
				builder.addColumnAndData((String) COLS.get("busy_size"),
					format(ssTO.getBusySize()));
			}
			if ((ssTO.getUnavailableSize() != 0) || (ssTO.getUnavailableSize() != -1)) {
				builder.addColumnAndData((String) COLS.get("unavailable_size"),
					format(ssTO.getUnavailableSize()));
			}

			if ((ssTO.getAvailableSize() != 0) || (ssTO.getAvailableSize() != -1)) {
				builder.addColumnAndData((String) COLS.get("available_size"),
					format(ssTO.getAvailableSize()));
			}
			if ((ssTO.getReservedSize() != 0) || (ssTO.getReservedSize() != -1)) {
				builder.addColumnAndData((String) COLS.get("reserved_size"),
					format(ssTO.getReservedSize()));
			}
			if (ssTO.getUpdateTime() != null) {
				builder.addColumnAndData((String) COLS.get("update_time"),
					format(ssTO.getUpdateTime()));
			}
		}

		String sql = buildSQL(builder);
		return sql;
	}

	/**
	 * Create a StorageSpace Transfer Object coming from Result Set
	 * 
	 * @param res
	 *          ResultSet
	 * @return StorageSpaceTO
	 */
	public StorageSpaceTO makeStorageSpaceTO(ResultSet res) {

		StorageSpaceTO ssTO = new StorageSpaceTO();

		try {
			ssTO.setStorageSpaceId(new Long(res.getLong("SS_ID")));

			ssTO.setOwnerName(res.getString("USERDN"));
			ssTO.setVoName(res.getString("VOGROUP"));
			ssTO.setAlias(res.getString("ALIAS"));
			ssTO.setSpaceToken(res.getString("SPACE_TOKEN"));

			java.sql.Timestamp createdTimeStamp = res.getTimestamp("CREATED");
			Date creationDate = new Date(createdTimeStamp.getTime());
			ssTO.setCreated(creationDate);

			ssTO.setSpaceFile(res.getString("SPACE_FILE"));
			ssTO.setStorageInfo(res.getString("STORAGE_INFO"));
			long tempLong = res.getLong("LIFETIME");
			if (!res.wasNull()) {
				ssTO.setLifetime(tempLong);
			}

			ssTO.setSpaceType(res.getString("SPACE_TYPE"));

			// Sizes
			tempLong = res.getLong("TOTAL_SIZE");
			if (!res.wasNull()) {
				ssTO.setTotalSize(tempLong);
			}
			tempLong = res.getLong("GUAR_SIZE");
			if (!res.wasNull()) {
				ssTO.setGuaranteedSize(tempLong);
			}
			tempLong = res.getLong("RESERVED_SIZE");
			if (!res.wasNull()) {
				ssTO.setReservedSize(tempLong);
			}
			tempLong = res.getLong("FREE_SIZE");
			if (!res.wasNull()) {
				ssTO.setFreeSize(tempLong);
			}
			tempLong = res.getLong("AVAILABLE_SIZE");
			if (!res.wasNull()) {
				ssTO.setAvailableSize(tempLong);
			}
			tempLong = res.getLong("USED_SIZE");
			if (!res.wasNull()) {
				ssTO.setUsedSize(tempLong);
			}
			tempLong = res.getLong("BUSY_SIZE");
			if (!res.wasNull()) {
				ssTO.setBusySize(tempLong);
			}
			tempLong = res.getLong("UNAVAILABLE_SIZE");
			if (!res.wasNull()) {
				ssTO.setUnavailableSize(tempLong);
			}

			// Last Update
			java.sql.Timestamp updatedTimeStamp = res.getTimestamp("UPDATE_TIME");
			Date updateDate = new Date(updatedTimeStamp.getTime());
			ssTO.setUpdateTime(updateDate);

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return ssTO;
	}

	// ************ HELPER Method *************** //

	/**
	 * @param vo
	 * @return
	 */
	private String getVOName(String vo) {

		String voStr = VO.makeNoVo().getValue();
		if (vo != null && !vo.trim().equals("")) {
			voStr = vo.trim();
		}
		return voStr;
	}

	/**
	 * 
	 * 
	 * @param token
	 *          String
	 * @return String
	 */
	public String selectByTokenQuery(String token) {

		return "SELECT * FROM `storage_space` where space_token='" + token + "'";
	}

	/**
	 * Returns the SQL string for selecting all columns from the table
	 * 'storage_space' in the 'storm_be_ISAM' database matching 'user' and
	 * 'spaceAlias'. 'spaceAlias' can be NULL or empty.
	 * 
	 * @param user
	 *          VomsGridUser.
	 * @param spaceAlias
	 *          String.
	 * @return String.
	 */
	public String selectBySpaceAliasQuery(GridUserInterface user,
		String spaceAlias) {

		String dn = user.getDn();

		if ((spaceAlias == null) || (spaceAlias.length() == 0))
			return "SELECT * FROM `storage_space` where userdn='" + dn + "'";

		return "SELECT * FROM `storage_space` where userdn='" + dn
			+ "' AND alias='" + spaceAlias + "'";
	}

	/**
	 * Returns the SQL string for selecting all columns from the table
	 * 'storage_space' in the 'storm_be_ISAM' database matching 'user' and
	 * 'spaceAlias'. 'spaceAlias' can be NULL or empty.
	 * 
	 * @param user
	 *          VomsGridUser.
	 * @param spaceAlias
	 *          String.
	 * @return String.
	 */
	public String selectBySpaceAliasOnlyQuery(String spaceAlias) {

		/*
		 * This is to distinguish a client reseve space with a VOSpaceArea both with
		 * the same token. Only the one made by the namespace process contains a
		 * fake dn
		 */

		return "SELECT * FROM `storage_space` where alias='" + spaceAlias + "'";
	}

	/**
	 * Returns the SQL string for selecting all columns from the table
	 * 'storage_space' in the 'storm_be_ISAM' database matching 'voname'.
	 * 
	 * @param voname
	 *          string
	 * @return String.
	 */
	public String selectBySpaceType(String voname) {

		/*
		 * This is to distinguish a client reseve space with a VOSpaceArea both with
		 * the same token. Only the one made by the namespace process contains a
		 * fake dn
		 */

		return "SELECT * FROM `storage_space` where SPACE_TYPE='" + voname + "'";
	}

	/**
	 * This method return the SQL query to evaluate all expired space reservation
	 * requests.
	 * 
	 * @param time
	 *          Current time (in second) to compare to the reservationTime +
	 *          lifetime
	 * @return String SQL query
	 */
	public String selectExpiredQuery(long currentTimeInSecond) {

		return "SELECT * FROM `storage_space` where  lifetime is not null and (UNIX_TIMESTAMP(created)+lifetime< "
			+ currentTimeInSecond + ")";
	}

	/**
	 * @param size
	 * @return
	 */
	public String selectByUnavailableUsedSpaceSizeQuery(long unavailableSizeValue) {

		return "SELECT * FROM `storage_space` where " + COLS.get("used_size")
			+ " IS NULL or " + COLS.get("used_size") + "=" + unavailableSizeValue;
	}

	/**
	 * @param lastUpdateTimestamp
	 * @return
	 */
	public String selectByPreviousOrNullLastUpdateQuery(long lastUpdateTimestamp) {

		return "SELECT * FROM `storage_space` where " + COLS.get("update_time")
			+ " IS NULL or UNIX_TIMESTAMP(" + COLS.get("update_time") + ") < "
			+ lastUpdateTimestamp;
	}

	/**
	 * Returns the SQL query for removing a row from the table 'storage_space' in
	 * the 'storm_be_ISAM' database matching 'userDN' and 'spaceToken'.
	 * 
	 * @param user
	 * @param spaceToken
	 * @return
	 */
	public String removeByTokenQuery(GridUserInterface user, String spaceToken) {

		return "DELETE FROM `storage_space` WHERE ((USERDN='" + user.getDn()
			+ "') AND (SPACE_TOKEN='" + spaceToken + "'))";
	}

	/**
	 * Returns the SQL query for removing a row from the table 'storage_space' in
	 * the 'storm_be_ISAM' database matching 'spaceToken'.
	 * 
	 * @param spaceToken
	 * @return
	 */
	public String removeByTokenQuery(String spaceToken) {

		return "DELETE FROM `storage_space` WHERE (SPACE_TOKEN='" + spaceToken
			+ "')";
	}

	/**
	 * Provides a query that updates all row fields accordingly to the provided
	 * StorageSpaceTO
	 * 
	 * @param ssTO
	 * @return
	 * @throws IllegalArgumentException
	 */
	public String updateByAliasAndTokenQuery(StorageSpaceTO ssTO)
		throws IllegalArgumentException {

		if (ssTO == null) {
			throw new IllegalArgumentException();
		}
		String query = "UPDATE `storage_space` SET";
		if (ssTO.getOwnerName() != null) {
			query += " `" + COLS.get("ownerName") + "` = "
				+ format(ssTO.getOwnerName()) + " ,";
		}
		query += " `" + COLS.get("ownerVO") + "` = "
			+ format(getVOName(ssTO.getVoName())) + " ,";
		if (ssTO.getCreated() != null) {
			query += " `" + COLS.get("created") + "` = " + format(ssTO.getCreated())
				+ " ,";
		}
		if (ssTO.getSpaceFile() != null) {
			query += " `" + COLS.get("spaceFile") + "` = "
				+ format(ssTO.getSpaceFile()) + " ,";
		}
		if (ssTO.getStorageInfo() != null) {
			query += " `" + COLS.get("storaqeInfo") + "` = "
				+ format(ssTO.getStorageInfo()) + " ,";
		}
		if (ssTO.getLifetime() != -1) {
			query += " `" + COLS.get("lifeTime") + "` = "
				+ format(ssTO.getLifetime()) + " ,";
		}
		if (ssTO.getSpaceType() != null) {
			query += " `" + COLS.get("spaceType") + "` = "
				+ format(ssTO.getSpaceType()) + " ,";
		}
		if ((ssTO.getTotalSize() != 0) || (ssTO.getTotalSize() != -1)) {
			query += " `" + COLS.get("total_size") + "` = "
				+ format(ssTO.getTotalSize()) + " ,";
		}
		if ((ssTO.getGuaranteedSize() != 0) || (ssTO.getGuaranteedSize() != -1)) {
			query += " `" + COLS.get("guar_size") + "` = "
				+ format(ssTO.getGuaranteedSize()) + " ,";
		}
		if ((ssTO.getFreeSize() != 0) || (ssTO.getFreeSize() != -1)) {
			query += " `" + COLS.get("free_size") + "` = "
				+ format(ssTO.getFreeSize()) + " ,";
		}
		if ((ssTO.getUsedSize() != 0) || (ssTO.getUsedSize() != -1)) {
			query += " `" + COLS.get("used_size") + "` = "
				+ format(ssTO.getUsedSize()) + " ,";
		}
		if ((ssTO.getBusySize() != 0) || (ssTO.getBusySize() != -1)) {
			query += " `" + COLS.get("busy_size") + "` = "
				+ format(ssTO.getBusySize()) + " ,";
		}
		if ((ssTO.getUnavailableSize() != 0) || (ssTO.getUnavailableSize() != -1)) {
			query += " `" + COLS.get("unavailable_size") + "` = "
				+ format(ssTO.getUnavailableSize()) + " ,";
		}
		if ((ssTO.getAvailableSize() != 0) || (ssTO.getAvailableSize() != -1)) {
			query += " `" + COLS.get("available_size") + "` = "
				+ format(ssTO.getAvailableSize()) + " ,";
		}
		if ((ssTO.getReservedSize() != 0) || (ssTO.getReservedSize() != -1)) {
			query += " `" + COLS.get("reserved_size") + "` = "
				+ format(ssTO.getReservedSize()) + " ,";
		}
		if (ssTO.getUpdateTime() != null) {
			query += " `" + COLS.get("update_time") + "` = "
				+ format(ssTO.getUpdateTime()) + "";
		}
		if (query.charAt(query.length() - 1) == ',') {
			query = query.substring(0, query.length() - 1);
		}
		query += " where `" + COLS.get("alias") + "` = " + format(ssTO.getAlias())
			+ " and `" + COLS.get("token") + "` = " + format(ssTO.getSpaceToken());
		return query;
	}

	/**
	 * Provides a query that updates all row fields accordingly to the provided
	 * StorageSpaceTO and using SpaceToken as key
	 * 
	 * @param ssTO
	 * @return
	 * @throws IllegalArgumentException
	 */
	public String updateByTokenQuery(StorageSpaceTO ssTO)
		throws IllegalArgumentException {

		if (ssTO == null) {
			throw new IllegalArgumentException();
		}
		String query = "UPDATE `storage_space` SET";
		if (ssTO.getOwnerName() != null) {
			query += " `" + COLS.get("ownerName") + "` = "
				+ format(ssTO.getOwnerName()) + " ,";
		}
		query += " `" + COLS.get("ownerVO") + "` = "
			+ format(getVOName(ssTO.getVoName())) + " ,";
		if (ssTO.getCreated() != null) {
			query += " `" + COLS.get("created") + "` = " + format(ssTO.getCreated())
				+ " ,";
		}
		if (ssTO.getAlias() != null) {
			query += " `" + COLS.get("alias") + "` = " + format(ssTO.getAlias())
				+ " ,";
		}
		if (ssTO.getSpaceFile() != null) {
			query += " `" + COLS.get("spaceFile") + "` = "
				+ format(ssTO.getSpaceFile()) + " ,";
		}
		if (ssTO.getStorageInfo() != null) {
			query += " `" + COLS.get("storaqeInfo") + "` = "
				+ format(ssTO.getStorageInfo()) + " ,";
		}
		if (ssTO.getLifetime() != -1) {
			query += " `" + COLS.get("lifeTime") + "` = "
				+ format(ssTO.getLifetime()) + " ,";
		}
		if (ssTO.getSpaceType() != null) {
			query += " `" + COLS.get("spaceType") + "` = "
				+ format(ssTO.getSpaceType()) + " ,";
		}
		if ((ssTO.getTotalSize() != 0) || (ssTO.getTotalSize() != -1)) {
			query += " `" + COLS.get("total_size") + "` = "
				+ format(ssTO.getTotalSize()) + " ,";
		}
		if ((ssTO.getGuaranteedSize() != 0) || (ssTO.getGuaranteedSize() != -1)) {
			query += " `" + COLS.get("guar_size") + "` = "
				+ format(ssTO.getGuaranteedSize()) + " ,";
		}
		if ((ssTO.getFreeSize() != 0) || (ssTO.getFreeSize() != -1)) {
			query += " `" + COLS.get("free_size") + "` = "
				+ format(ssTO.getFreeSize()) + " ,";
		}
		if ((ssTO.getUsedSize() != 0) || (ssTO.getUsedSize() != -1)) {
			query += " `" + COLS.get("used_size") + "` = "
				+ format(ssTO.getUsedSize()) + " ,";
		}
		if ((ssTO.getBusySize() != 0) || (ssTO.getBusySize() != -1)) {
			query += " `" + COLS.get("busy_size") + "` = "
				+ format(ssTO.getBusySize()) + " ,";
		}
		if ((ssTO.getUnavailableSize() != 0) || (ssTO.getUnavailableSize() != -1)) {
			query += " `" + COLS.get("unavailable_size") + "` = "
				+ format(ssTO.getUnavailableSize()) + " ,";
		}
		if ((ssTO.getAvailableSize() != 0) || (ssTO.getAvailableSize() != -1)) {
			query += " `" + COLS.get("available_size") + "` = "
				+ format(ssTO.getAvailableSize()) + " ,";
		}
		if ((ssTO.getReservedSize() != 0) || (ssTO.getReservedSize() != -1)) {
			query += " `" + COLS.get("reserved_size") + "` = "
				+ format(ssTO.getReservedSize()) + " ,";
		}
		if (ssTO.getUpdateTime() != null) {
			query += " `" + COLS.get("update_time") + "` = "
				+ format(ssTO.getUpdateTime()) + "";
		}
		if (query.charAt(query.length() - 1) == ',') {
			query = query.substring(0, query.length() - 1);
		}
		query += " where `" + COLS.get("token") + "` = "
			+ format(ssTO.getSpaceToken());
		return query;
	}

	/**
	 * 
	 * @param token
	 *          String
	 * @param freeSpace
	 *          long
	 * @return String
	 */
	public String updateFreeSpaceByTokenQuery(String token, long freeSpace,
		Date updateTimestamp) {

		return "UPDATE `storage_space` SET `free_size`=" + freeSpace + " , "
			+ "`UPDATE_TIME`=" + format(updateTimestamp) + " WHERE space_token='"
			+ token + "'";
	}

	/**
	 * 
	 * @param token
	 *          String
	 * @param freeSpace
	 *          long
	 * @return String
	 */
	public String updateSpaceSizesByTokenQuery(String token, long freeSpace,
		long availableSpace, long usedSpace, long busySpace, long unavailableSpace,
		Date updateTimestamp)

	{

		String query = "UPDATE `storage_space` SET ";
		query += "`free_size`=" + freeSpace;
		query += "`available_size`=" + availableSpace;
		query += "`used_size`=" + usedSpace;
		query += "`busy_size`=" + busySpace;
		query += "`unavailable_size`=" + unavailableSpace;
		query += "`UPDATE_TIME`=" + format(updateTimestamp);
		query += " WHERE space_token='" + token + "'";

		return query;

	}

}
