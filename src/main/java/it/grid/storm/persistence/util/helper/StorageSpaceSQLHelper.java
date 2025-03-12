/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.util.helper;

import it.grid.storm.common.types.VO;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.persistence.model.StorageSpaceTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;

public class StorageSpaceSQLHelper extends SQLHelper {

  private final static String TABLE_NAME = "storage_space";
  private final static HashMap<String, String> COLS = new HashMap<String, String>();

  private static final String[] COLUMN_NAMES =
      {"SS_ID", "USERDN", "VOGROUP", "ALIAS", "SPACE_TOKEN", "CREATED", "TOTAL_SIZE", "GUAR_SIZE",
          "FREE_SIZE", "SPACE_FILE", "STORAGE_INFO", "LIFETIME", "SPACE_TYPE", "USED_SIZE",
          "BUSY_SIZE", "UNAVAILABLE_SIZE", "AVAILABLE_SIZE", "RESERVED_SIZE", "UPDATE_TIME"};

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
   * @param ssTO StorageSpaceTO
   * @return String
   * @throws SQLException
   */

  public PreparedStatement insertQuery(Connection conn, StorageSpaceTO ssTO) throws SQLException {

    List<String> values = Lists.newLinkedList();

    StringBuilder fields = new StringBuilder("(");
    StringBuilder placeholders = new StringBuilder("(");

    if (ssTO != null) {
      if (ssTO.getOwnerName() != null) {
        fields.append(COLS.get("ownerName") + (","));
        placeholders.append("?,");
        values.add(format(ssTO.getOwnerName()));
      }

      fields.append(COLS.get("ownerVO") + (","));
      placeholders.append("?,");
      values.add(format(ssTO.getVoName()));

      if (ssTO.getAlias() != null) {
        fields.append(COLS.get("alias") + (","));
        placeholders.append("?,");
        values.add(format(ssTO.getAlias()));
      }
      if (ssTO.getSpaceToken() != null) {
        fields.append(COLS.get("token") + (","));
        placeholders.append("?,");
        values.add(format(ssTO.getSpaceToken()));
      }
      if (ssTO.getCreated() != null) {
        fields.append(COLS.get("created") + (","));
        placeholders.append("?,");
        values.add(format(ssTO.getCreated()));
      }
      if (ssTO.getSpaceFile() != null) {
        fields.append(COLS.get("spaceFile") + (","));
        placeholders.append("?,");
        values.add(format(ssTO.getSpaceFile()));
      }
      if (ssTO.getStorageInfo() != null) {
        fields.append(COLS.get("storaqeInfo") + (","));
        placeholders.append("?,");
        values.add(format(ssTO.getStorageInfo()));
      }
      if (ssTO.getLifetime() != -1) {
        fields.append(COLS.get("lifeTime") + (","));
        placeholders.append("?,");
        values.add(format(ssTO.getLifetime()));
      }
      if (ssTO.getSpaceType() != null) {
        fields.append(COLS.get("spaceType") + (","));
        placeholders.append("?,");
        values.add(format(ssTO.getSpaceType()));
      }
      if ((ssTO.getTotalSize() != 0) || (ssTO.getTotalSize() != -1)) {
        fields.append(COLS.get("total_size") + (","));
        placeholders.append("?,");
        values.add(format(ssTO.getTotalSize()));
      }
      if ((ssTO.getGuaranteedSize() != 0) || (ssTO.getGuaranteedSize() != -1)) {
        fields.append(COLS.get("guar_size") + (","));
        placeholders.append("?,");
        values.add(format(ssTO.getGuaranteedSize()));
      }
      if ((ssTO.getFreeSize() != 0) || (ssTO.getFreeSize() != -1)) {
        fields.append(COLS.get("free_size") + (","));
        placeholders.append("?,");
        values.add(format(ssTO.getFreeSize()));
      }
      if ((ssTO.getUsedSize() != 0) || (ssTO.getUsedSize() != -1)) {
        fields.append(COLS.get("used_size") + (","));
        placeholders.append("?,");
        values.add(format(ssTO.getUsedSize()));
      }
      if ((ssTO.getBusySize() != 0) || (ssTO.getBusySize() != -1)) {
        fields.append(COLS.get("busy_size") + (","));
        placeholders.append("?,");
        values.add(format(ssTO.getBusySize()));
      }
      if ((ssTO.getUnavailableSize() != 0) || (ssTO.getUnavailableSize() != -1)) {
        fields.append(COLS.get("unavailable_size") + (","));
        placeholders.append("?,");
        values.add(format(ssTO.getUnavailableSize()));
      }

      if ((ssTO.getAvailableSize() != 0) || (ssTO.getAvailableSize() != -1)) {
        fields.append(COLS.get("available_size") + (","));
        placeholders.append("?,");
        values.add(format(ssTO.getAvailableSize()));
      }
      if ((ssTO.getReservedSize() != 0) || (ssTO.getReservedSize() != -1)) {
        fields.append(COLS.get("reserved_size") + (","));
        placeholders.append("?,");
        values.add(format(ssTO.getReservedSize()));
      }
      if (ssTO.getUpdateTime() != null) {
        fields.append(COLS.get("update_time").concat(","));
        placeholders.append("?,");
        values.add(format(ssTO.getUpdateTime()));
      }
    }

    fields.deleteCharAt(fields.length() - 1);
    fields.append(")");
    placeholders.deleteCharAt(placeholders.length() - 1);
    placeholders.append(")");

    String str = "INSERT INTO " + TABLE_NAME + " " + fields.toString() + " VALUES "
        + placeholders.toString();
    PreparedStatement preparedStatement = conn.prepareStatement(str);

    int index = 1;
    for (String val : values) {
      preparedStatement.setString(index, val);
      index++;
    }

    return preparedStatement;
  }

  /**
   * Create a StorageSpace Transfer Object coming from Result Set
   * 
   * @param res ResultSet
   * @return StorageSpaceTO
   */
  public StorageSpaceTO makeStorageSpaceTO(ResultSet res) {

    StorageSpaceTO ssTO = new StorageSpaceTO();

    try {
      ssTO.setStorageSpaceId(Long.valueOf(res.getLong("SS_ID")));

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
   * @param token String
   * @param conn
   * @return String
   * @throws SQLException
   */
  public PreparedStatement selectByTokenQuery(Connection conn, String token) throws SQLException {

    String str = null;
    PreparedStatement preparedStatement = null;

    str = "SELECT * FROM  storage_space where space_token=?";
    preparedStatement = conn.prepareStatement(str);
    preparedStatement.setString(1, token);

    return preparedStatement;
  }

  /**
   * Returns the SQL string for selecting all columns from the table 'storage_space' in the
   * 'storm_be_ISAM' database matching 'user' and 'spaceAlias'. 'spaceAlias' can be NULL or empty.
   * 
   * @param user VomsGridUser.
   * @param spaceAlias String.
   * @return String.
   * @throws SQLException
   */
  public PreparedStatement selectBySpaceAliasQuery(Connection conn, GridUserInterface user,
      String spaceAlias) throws SQLException {

    String str = null;
    PreparedStatement preparedStatement = null;

    String dn = user.getDn();

    if ((spaceAlias == null) || (spaceAlias.length() == 0)) {
      str = "SELECT * FROM storage_space where userdn=?";
      preparedStatement = conn.prepareStatement(str);
      preparedStatement.setString(1, dn);
    } else {
      str = "SELECT * FROM storage_space where userdn=? AND alias=?";
      preparedStatement = conn.prepareStatement(str);
      preparedStatement.setString(1, dn);
      preparedStatement.setString(2, spaceAlias);
    }
    return preparedStatement;
  }

  /**
   * Returns the SQL string for selecting all columns from the table 'storage_space' in the
   * 'storm_be_ISAM' database matching 'user' and 'spaceAlias'. 'spaceAlias' can be NULL or empty.
   * 
   * @param user VomsGridUser.
   * @param spaceAlias String.
   * @return String.
   * @throws SQLException
   */
  public PreparedStatement selectBySpaceAliasOnlyQuery(Connection conn, String spaceAlias)
      throws SQLException {

    /*
     * This is to distinguish a client reseve space with a VOSpaceArea both with the same token.
     * Only the one made by the namespace process contains a fake dn
     */

    String str = null;
    PreparedStatement preparedStatement = null;

    str = "SELECT * FROM storage_space where alias=?";
    preparedStatement = conn.prepareStatement(str);
    preparedStatement.setString(1, spaceAlias);

    return preparedStatement;
  }

  /**
   * Returns the SQL string for selecting all columns from the table 'storage_space' in the
   * 'storm_be_ISAM' database matching 'voname'.
   * 
   * @param voname string
   * @return String.
   * @throws SQLException
   */

  public PreparedStatement selectBySpaceType(Connection conn, String voname) throws SQLException {

    /*
     * This is to distinguish a client reseve space with a VOSpaceArea both with the same token.
     * Only the one made by the namespace process contains a fake dn
     */

    String str = null;
    PreparedStatement preparedStatement = null;

    str = "SELECT * FROM storage_space where SPACE_TYPE=?";
    preparedStatement = conn.prepareStatement(str);
    preparedStatement.setString(1, voname);

    return preparedStatement;
  }

  /**
   * This method return the SQL query to evaluate all expired space reservation requests.
   * 
   * @param time Current time (in second) to compare to the reservationTime + lifetime
   * @return String SQL query
   * @throws SQLException
   */
  public PreparedStatement selectExpiredQuery(Connection conn, long currentTimeInSecond)
      throws SQLException {

    String str = null;
    PreparedStatement preparedStatement = null;

    str =
        "SELECT * FROM storage_space where  lifetime is not null and (UNIX_TIMESTAMP(created)+lifetime< ?)";
    preparedStatement = conn.prepareStatement(str);
    preparedStatement.setLong(1, currentTimeInSecond);

    return preparedStatement;

  }

  /**
   * @param size
   * @return
   * @throws SQLException
   */
  public PreparedStatement selectByUnavailableUsedSpaceSizeQuery(Connection conn,
      long unavailableSizeValue) throws SQLException {

    String str = null;
    PreparedStatement preparedStatement = null;

    str = "SELECT * FROM storage_space where " + COLS.get("used_size") + " IS NULL or "
        + COLS.get("used_size") + "=?";

    preparedStatement = conn.prepareStatement(str);
    preparedStatement.setLong(1, unavailableSizeValue);

    return preparedStatement;
  }

  /**
   * @param lastUpdateTimestamp
   * @return
   * @throws SQLException
   */

  public PreparedStatement selectByPreviousOrNullLastUpdateQuery(Connection conn,
      long lastUpdateTimestamp) throws SQLException {

    String str = null;
    PreparedStatement preparedStatement = null;

    str = "SELECT * FROM storage_space where " + COLS.get("update_time")
        + " IS NULL or UNIX_TIMESTAMP(" + COLS.get("update_time") + ") < ?";

    preparedStatement = conn.prepareStatement(str);
    preparedStatement.setLong(1, lastUpdateTimestamp);

    return preparedStatement;

  }

  /**
   * Returns the SQL query for removing a row from the table 'storage_space' in the 'storm_be_ISAM'
   * database matching 'userDN' and 'spaceToken'.
   * 
   * @param user
   * @param spaceToken
   * @return
   * @throws SQLException
   */
  public PreparedStatement removeByTokenQuery(Connection conn, GridUserInterface user,
      String spaceToken) throws SQLException {

    String str = null;
    PreparedStatement preparedStatement = null;

    str = "DELETE FROM storage_space WHERE ((USERDN=?) AND (SPACE_TOKEN=?))";
    preparedStatement = conn.prepareStatement(str);

    preparedStatement.setString(1, user.getDn());
    preparedStatement.setString(2, spaceToken);

    return preparedStatement;
  }

  /**
   * Returns the SQL query for removing a row from the table 'storage_space' in the 'storm_be_ISAM'
   * database matching 'spaceToken'.
   * 
   * @param spaceToken
   * @return
   * @throws SQLException
   */
  public PreparedStatement removeByTokenQuery(Connection conn, String spaceToken)
      throws SQLException {

    String str = null;
    PreparedStatement preparedStatement = null;

    str = "DELETE FROM storage_space WHERE (SPACE_TOKEN=?)";
    preparedStatement = conn.prepareStatement(str);

    preparedStatement.setString(1, spaceToken);

    return preparedStatement;
  }

  /**
   * Provides a query that updates all row fields accordingly to the provided StorageSpaceTO
   * 
   * @param ssTO
   * @return
   * @throws IllegalArgumentException
   * @throws SQLException
   */
  public PreparedStatement updateByAliasAndTokenQuery(Connection conn, StorageSpaceTO ssTO)
      throws IllegalArgumentException, SQLException {

    List<String> values = new LinkedList<String>();

    if (ssTO == null) {
      throw new IllegalArgumentException();
    }
    String query = "UPDATE storage_space SET";
    if (ssTO.getOwnerName() != null) {
      query += " " + COLS.get("ownerName") + " = ?" + " ,";
      values.add(format(ssTO.getOwnerName()));
    }

    query += " " + COLS.get("ownerVO") + " = ?" + " ,";
    values.add(format(getVOName(ssTO.getVoName())));

    if (ssTO.getCreated() != null) {
      query += " " + COLS.get("created") + " = ?" + " ,";
      values.add(format(ssTO.getCreated()));
    }
    if (ssTO.getSpaceFile() != null) {
      query += " " + COLS.get("spaceFile") + " = ?" + " ,";
      values.add(format(ssTO.getSpaceFile()));
    }
    if (ssTO.getStorageInfo() != null) {
      query += " " + COLS.get("storaqeInfo") + " = ?" + " ,";
      values.add(format(ssTO.getStorageInfo()));
    }
    if (ssTO.getLifetime() != -1) {
      query += " " + COLS.get("lifeTime") + " = ?" + " ,";
      values.add(format(ssTO.getLifetime()));
    }
    if (ssTO.getSpaceType() != null) {
      query += " " + COLS.get("spaceType") + " = ?" + " ,";
      values.add(format(ssTO.getSpaceType()));
    }
    if ((ssTO.getTotalSize() != 0) || (ssTO.getTotalSize() != -1)) {
      query += " " + COLS.get("total_size") + " = ?" + " ,";
      values.add(format(ssTO.getTotalSize()));
    }
    if ((ssTO.getGuaranteedSize() != 0) || (ssTO.getGuaranteedSize() != -1)) {
      query += " " + COLS.get("guar_size") + " = ?" + " ,";
      values.add(format(ssTO.getGuaranteedSize()));
    }
    if ((ssTO.getFreeSize() != 0) || (ssTO.getFreeSize() != -1)) {
      query += " " + COLS.get("free_size") + " = ?" + " ,";
      values.add(format(ssTO.getFreeSize()));
    }
    if ((ssTO.getUsedSize() != 0) || (ssTO.getUsedSize() != -1)) {
      query += " " + COLS.get("used_size") + " = ?" + " ,";
      values.add(format(ssTO.getUsedSize()));
    }
    if ((ssTO.getBusySize() != 0) || (ssTO.getBusySize() != -1)) {
      query += " " + COLS.get("busy_size") + " = ?" + " ,";
      values.add(format(ssTO.getBusySize()));
    }
    if ((ssTO.getUnavailableSize() != 0) || (ssTO.getUnavailableSize() != -1)) {
      query += " " + COLS.get("unavailable_size") + " = ?" + " ,";
      values.add(format(ssTO.getUnavailableSize()));
    }
    if ((ssTO.getAvailableSize() != 0) || (ssTO.getAvailableSize() != -1)) {
      query += " " + COLS.get("available_size") + " = ?" + " ,";
      values.add(format(ssTO.getAvailableSize()));
    }
    if ((ssTO.getReservedSize() != 0) || (ssTO.getReservedSize() != -1)) {
      query += " " + COLS.get("reserved_size") + " = ?" + " ,";
      values.add(format(ssTO.getReservedSize()));
    }
    if (ssTO.getUpdateTime() != null) {
      query += " " + COLS.get("update_time") + " = ?" + " ,";
      values.add(format(ssTO.getUpdateTime()));
    }
    if (query.charAt(query.length() - 1) == ',') {
      query = query.substring(0, query.length() - 1);
    }
    query += " where " + COLS.get("alias") + " = ?" + " and " + COLS.get("token") + " = ?";

    values.add(format(ssTO.getAlias()));
    values.add(format(ssTO.getSpaceToken()));

    PreparedStatement preparedStatement = conn.prepareStatement(query);

    int index = 1;
    for (String val : values) {
      preparedStatement.setString(index, val);
      index++;
    }

    return preparedStatement;
  }

  /**
   * Provides a query that updates all row fields accordingly to the provided StorageSpaceTO and
   * using SpaceToken as key
   * 
   * @param ssTO
   * @return
   * @throws IllegalArgumentException
   * @throws SQLException
   */
  public PreparedStatement updateByTokenQuery(Connection conn, StorageSpaceTO ssTO)
      throws IllegalArgumentException, SQLException {

    List<String> values = new LinkedList<String>();

    if (ssTO == null) {
      throw new IllegalArgumentException();
    }
    String query = "UPDATE storage_space SET";
    if (ssTO.getOwnerName() != null) {
      query += " " + COLS.get("ownerName") + " = ?" + " ,";
      values.add(format(ssTO.getOwnerName()));
    }

    query += " " + COLS.get("ownerVO") + " = ?" + " ,";
    values.add((getVOName(ssTO.getVoName())));

    if (ssTO.getCreated() != null) {
      query += " " + COLS.get("created") + " = ?" + " ,";
      values.add(format(ssTO.getCreated()));
    }
    if (ssTO.getAlias() != null) {
      query += " " + COLS.get("alias") + " = ?" + " ,";
      values.add(format(ssTO.getAlias()));
    }
    if (ssTO.getSpaceFile() != null) {
      query += " " + COLS.get("spaceFile") + " = ?" + " ,";
      values.add(format(ssTO.getSpaceFile()));
    }
    if (ssTO.getStorageInfo() != null) {
      query += " " + COLS.get("storaqeInfo") + " = ?" + " ,";
      values.add(format(ssTO.getStorageInfo()));
    }
    if (ssTO.getLifetime() != -1) {
      query += " " + COLS.get("lifeTime") + " = ?" + " ,";
      values.add(format(ssTO.getLifetime()));
    }
    if (ssTO.getSpaceType() != null) {
      query += " " + COLS.get("spaceType") + " = ?" + " ,";
      values.add(format(ssTO.getSpaceType()));
    }
    if ((ssTO.getTotalSize() != 0) || (ssTO.getTotalSize() != -1)) {
      query += " " + COLS.get("total_size") + " = ?" + " ,";
      values.add(format(ssTO.getTotalSize()));
    }
    if ((ssTO.getGuaranteedSize() != 0) || (ssTO.getGuaranteedSize() != -1)) {
      query += " " + COLS.get("guar_size") + " = ?" + " ,";
      values.add(format(ssTO.getGuaranteedSize()));
    }
    if ((ssTO.getFreeSize() != 0) || (ssTO.getFreeSize() != -1)) {
      query += " " + COLS.get("free_size") + " = ?" + " ,";
      values.add(format(ssTO.getFreeSize()));
    }
    if ((ssTO.getUsedSize() != 0) || (ssTO.getUsedSize() != -1)) {
      query += " " + COLS.get("used_size") + " = ?" + " ,";
      values.add(format(ssTO.getUsedSize()));
    }
    if ((ssTO.getBusySize() != 0) || (ssTO.getBusySize() != -1)) {
      query += " " + COLS.get("busy_size") + " = ?" + " ,";
      values.add(format(ssTO.getBusySize()));
    }
    if ((ssTO.getUnavailableSize() != 0) || (ssTO.getUnavailableSize() != -1)) {
      query += " " + COLS.get("unavailable_size") + " = ?" + " ,";
      values.add(format(ssTO.getUnavailableSize()));
    }
    if ((ssTO.getAvailableSize() != 0) || (ssTO.getAvailableSize() != -1)) {
      query += " " + COLS.get("available_size") + " = ?" + " ,";
      values.add(format(ssTO.getAvailableSize()));
    }
    if ((ssTO.getReservedSize() != 0) || (ssTO.getReservedSize() != -1)) {
      query += " " + COLS.get("reserved_size") + " = ?" + " ,";
      values.add(format(ssTO.getReservedSize()));
    }
    if (ssTO.getUpdateTime() != null) {
      query += " " + COLS.get("update_time") + " = ?" + " ,";
      values.add(format(ssTO.getUpdateTime()));
    }
    if (query.charAt(query.length() - 1) == ',') {
      query = query.substring(0, query.length() - 1);
    }
    query += " where " + COLS.get("token") + " = ?";

    values.add(format(format(ssTO.getSpaceToken())));

    PreparedStatement preparedStatement = conn.prepareStatement(query);

    int index = 1;
    for (String val : values) {
      preparedStatement.setString(index, val);
      index++;
    }

    return preparedStatement;
  }

  /**
   * 
   * @param token String
   * @param freeSpace long
   * @return String
   * @throws SQLException
   */
  public PreparedStatement updateFreeSpaceByTokenQuery(Connection conn, String token,
      long freeSpace, Date updateTimestamp) throws SQLException {

    String str = null;
    PreparedStatement preparedStatement = null;

    str = "UPDATE storage_space SET free_size=?" + " , " + "UPDATE_TIME=?" + " WHERE space_token=?";

    preparedStatement = conn.prepareStatement(str);

    preparedStatement.setLong(1, freeSpace);
    preparedStatement.setString(2, format(updateTimestamp));
    preparedStatement.setString(3, token);

    return preparedStatement;
  }

  public PreparedStatement increaseUsedSpaceByTokenQuery(Connection conn, String token,
      long usedSpaceToAdd) throws SQLException {

    String str = null;
    PreparedStatement preparedStatement = null;

    str = "UPDATE storage_space " + " SET USED_SIZE = USED_SIZE + ?, BUSY_SIZE = BUSY_SIZE + ?, "
        + " FREE_SIZE = FREE_SIZE - ?, AVAILABLE_SIZE = AVAILABLE_SIZE - ?, "
        + " UPDATE_TIME = NOW() " + " WHERE space_token=? AND USED_SIZE + ? <= TOTAL_SIZE ";

    preparedStatement = conn.prepareStatement(str);

    preparedStatement.setLong(1, usedSpaceToAdd);
    preparedStatement.setLong(2, usedSpaceToAdd);
    preparedStatement.setLong(3, usedSpaceToAdd);
    preparedStatement.setLong(4, usedSpaceToAdd);
    preparedStatement.setString(5, token);
    preparedStatement.setLong(6, usedSpaceToAdd);

    return preparedStatement;

  }

  public PreparedStatement decreaseUsedSpaceByTokenQuery(Connection conn, String token,
      long usedSpaceToRemove) throws SQLException {

    String str = null;
    PreparedStatement preparedStatement = null;

    str = "UPDATE storage_space " + " SET USED_SIZE = USED_SIZE - ?, BUSY_SIZE = BUSY_SIZE - ?, "
        + " FREE_SIZE = FREE_SIZE + ?, AVAILABLE_SIZE = AVAILABLE_SIZE + ?, "
        + " UPDATE_TIME = NOW() " + " WHERE space_token=? AND USED_SIZE - ? >= 0 ";

    preparedStatement = conn.prepareStatement(str);

    preparedStatement.setLong(1, usedSpaceToRemove);
    preparedStatement.setLong(2, usedSpaceToRemove);
    preparedStatement.setLong(3, usedSpaceToRemove);
    preparedStatement.setLong(4, usedSpaceToRemove);
    preparedStatement.setString(5, token);
    preparedStatement.setLong(6, usedSpaceToRemove);

    return preparedStatement;

  }

}
