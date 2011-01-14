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

package it.grid.storm.persistence.util.helper;

import it.grid.storm.persistence.model.StorageSpaceTO;
import it.grid.storm.persistence.util.db.InsertBuilder;
import it.grid.storm.persistence.util.db.SQLHelper;
import java.util.HashMap;
import it.grid.storm.persistence.util.db.DataBaseStrategy;
import it.grid.storm.persistence.PersistenceDirector;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;

import it.grid.storm.srm.types.TSpaceType;
import it.grid.storm.common.types.VO;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.*;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.griduser.VomsGridUser;

public class StorageSpaceSQLHelper extends SQLHelper{

  private final static String TABLE_NAME="storage_space";
  private final static HashMap COLS = new HashMap();

  private static final String[] COLUMN_NAMES = {
      "SS_ID",
      "USERDN",
      "VOGROUP",
      "ALIAS",
      "SPACE_TOKEN",
      "CREATED",
      "TOTAL_SIZE",
      "GUAR_SIZE",
      "FREE_SIZE",
      "SPACE_FILE",
      "STORAGE_INFO",
      "LIFETIME",
      "SPACE_TYPE"
};


  static {
    COLS.put("storageSpaceId", "SS_ID");
    COLS.put("ownerName", "USERDN");
    COLS.put("ownerVO", "VOGROUP");
    COLS.put("alias", "ALIAS");
    COLS.put("token", "SPACE_TOKEN");
    COLS.put("created", "CREATED");
    COLS.put("total_size", "TOTAL_SIZE");
    COLS.put("guar_size", "GUAR_SIZE");
    COLS.put("unused_size", "FREE_SIZE");
    COLS.put("spaceFile", "SPACE_FILE");
    COLS.put("storaqeInfo", "STORAGE_INFO");
    COLS.put("lifeTime", "LIFETIME");
    COLS.put("spaceType", "SPACE_TYPE");
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
   * @param ssTO StorageSpaceTO
   * @return String
   */
  public String insertQuery(StorageSpaceTO ssTO) {

    builder = new InsertBuilder();
    builder.setTable(TABLE_NAME);
    if (ssTO != null) {
      if (ssTO.getOwnerName() != null) {
        builder.addColumnAndData((String)COLS.get("ownerName"),
                                 format(ssTO.getOwnerName()));
      }
      builder.addColumnAndData((String)COLS.get("ownerVO"),format(getVOName(ssTO.getOwner())));
      if (ssTO.getAlias() != null) {
        builder.addColumnAndData((String)COLS.get("alias"),
                                format(ssTO.getAlias()));
      }
      if (ssTO.getSpaceToken() != null) {
        builder.addColumnAndData((String)COLS.get("token"),
                                format(ssTO.getSpaceToken()));
      }
      if (ssTO.getCreated() != null) {
        builder.addColumnAndData((String)COLS.get("created"),
                                format(ssTO.getCreated()));
      }
      if (ssTO.getTotalSize() != -1) {
        builder.addColumnAndData((String)COLS.get("total_size"),
                                format(ssTO.getTotalSize()));
      }
      if (ssTO.getGuaranteedSize() != -1) {
        builder.addColumnAndData((String)COLS.get("guar_size"),
                                format(ssTO.getGuaranteedSize()));
      }
      if (ssTO.getUnusedSize() != -1) {
        builder.addColumnAndData((String)COLS.get("unused_size"),
                                format(ssTO.getUnusedSize()));
      }
      if (ssTO.getSpaceFile() != null) {
        builder.addColumnAndData((String)COLS.get("spaceFile"),
                                format(ssTO.getSpaceFile()));
      }
      if (ssTO.getStorageInfo() != null) {
        builder.addColumnAndData((String)COLS.get("storaqeInfo"),
                                format(ssTO.getStorageInfo()));
      }
      if (ssTO.getLifetime() != -1) {
        builder.addColumnAndData((String)COLS.get("lifeTime"),
                                format(ssTO.getLifetime()));
      }
      if (ssTO.getSpaceType()!= null) {
        builder.addColumnAndData((String)COLS.get("spaceType"),
                                format(ssTO.getSpaceType()));
      }
    }
    String sql = buildSQL(builder);
    return sql;
  }

  // ************ HELPER Method *************** //
  private String getVOName(GridUserInterface maker) {
      String voStr = VO.makeNoVo().getValue();
      if (maker!=null) {
          if (maker instanceof VomsGridUser) {
              voStr = ( (VomsGridUser) maker).getVO().getValue();
          }
      }
      return voStr;
  }

  /**
   *
   *
   * @param token String
   * @return String
   */
  public String selectByTokenQuery(String token)
  {
    //SELECT * FROM `storage_space` where space_token="6D4E8533-0B01-F1F2-7F00-0001B8273F51";
    return "SELECT * FROM `storage_space` where space_token='"+token+"'";
  }

  /**
   * Returns the SQL string for selecting all columns from the table 'storage_space' in the
   * 'storm_be_ISAM' database matching 'user' and 'spaceAlias'. 'spaceAlias' can be NULL or empty.
   * @param user VomsGridUser.
   * @param spaceAlias String.
   * @return String.
   */
  public String selectBySpaceAliasQuery(GridUserInterface user, String spaceAlias)
  {
    String dn = user.getDn();

    if ((spaceAlias==null) || (spaceAlias.length()==0))
      return "SELECT * FROM `storage_space` where userdn='" + dn + "'";

    return "SELECT * FROM `storage_space` where userdn='" + dn + "' AND alias='" + spaceAlias + "'";
  }


  /**
   * Returns the SQL string for selecting all columns from the table 'storage_space' in the
   * 'storm_be_ISAM' database matching 'user' and 'spaceAlias'. 'spaceAlias' can be NULL or empty.
   * @param user VomsGridUser.
   * @param spaceAlias String.
   * @return String.
   */
  public String selectBySpaceAliasOnlyQuery(String spaceAlias)
  {
      /*
       * This is to distinguish  a client reseve space with  a VOSpaceArea both with the same token.
       * Only the one made by the namespace process contains a fake dn
       */

       return "SELECT * FROM `storage_space` where alias='" + spaceAlias + "'";
  }


  /**
   * Returns the SQL string for selecting all columns from the table 'storage_space' in the
   * 'storm_be_ISAM' database matching 'voname'.
   * @param voname string
   * @return String.
   */
  public String selectBySpaceType(String voname)
  {
      /*
       * This is to distinguish  a client reseve space with  a VOSpaceArea both with the same token.
       * Only the one made by the namespace process contains a fake dn
       */

       return "SELECT * FROM `storage_space` where SPACE_TYPE='" + voname + "'";
  }





  /**
   * This metod return the SQL query to evaluate all expired space reservation requests.
   * @param time Current time (in second) to compare to the reservationTime + lifetime
   * @return String SQL query
   */
  public String selectExpiredQuery(long currentTimeInSecond)  {
	  return "SELECT * FROM `storage_space` where (UNIX_TIMESTAMP(created)+lifetime< "+currentTimeInSecond+")";
  }



  /**
   * Returns the SQL query for removing a row from the table 'storage_space' in the 'storm_be_ISAM' database
   * matching 'userDN' and 'spaceToken'.
   * @param user
   * @param spaceToken
   * @return
   */
  public String removeByTokenQuery(GridUserInterface user, String spaceToken) {
      return "DELETE FROM `storage_space` WHERE ((USERDN='" + user.getDn()+ "') AND (SPACE_TOKEN='" + spaceToken  + "'))";
  }

  /**
   * Returns the SQL query for removing a row from the table 'storage_space' in the 'storm_be_ISAM' database
   * matching 'spaceToken'.
   * @param spaceToken
   * @return
   */
  public String removeByTokenQuery(String spaceToken) {
      return "DELETE FROM `storage_space` WHERE (SPACE_TOKEN='" + spaceToken  + "')";
  }

  /**
   *
   * @param token String
   * @param freeSpace long
   * @return String
   */
  public String updateFreeSpaceByTokenQuery(String token, long freeSpace)
  {
    //UPDATE `storm_be_isam`.`storage_space` SET `free_size`=1123 WHERE `SS_ID`=1;
    return "UPDATE `storage_space` SET `free_size`="+freeSpace+" WHERE space_token='"+token+"'";
  }

  /**
  *
  * @param token String
  * @param freeSpace long
  * @return String
  */
 public String updateAllByTokenQuery(String token, String alias, long size, String filename)
 {
	/**
	 * @todo all size are the same in this version!!!
	 */
   //UPDATE `storm_be_isam`.`storage_space` SET `free_size`=1123 WHERE `SS_ID`=1;
   return "UPDATE `storage_space` SET `alias`='"+alias+"', `total_size`='"+size+"', `guar_size`='"+size+"', `free_size`='"+size+"', `space_file`='"+filename+"' WHERE space_token='"+token+"'";
 }

  /**
   *
   * @param res ResultSet
   * @return StorageSpaceTO
   */
  public StorageSpaceTO makeStorageSpaceTO(ResultSet res)
  {
    StorageSpaceTO ssTO = new StorageSpaceTO();
    GridUserInterface guser = null;
    String dn;

    try {
      ssTO.setStorageSpaceId(new Long(res.getLong("SS_ID")));

      dn = res.getString("USERDN");
      guser = GridUserManager.makeGridUser(dn);
      //guser = VomsGridUser.make(DN);

      ssTO.setOwner(guser);
      ssTO.setOwnerName(dn);
      ssTO.setVoName(res.getString("VOGROUP"));
      ssTO.setAlias(res.getString("ALIAS"));
      ssTO.setSpaceToken(res.getString("SPACE_TOKEN"));
      // The date of creation is stored as a string in the db, it must be converted in Date format.
      String strCreationDate = res.getString("CREATED");
      Date creationDate = new Date();
      SimpleDateFormat creationDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      creationDate = creationDateFormat.parse(strCreationDate, new ParsePosition(0));
      ssTO.setCreated(creationDate);

      ssTO.setTotalSize(res.getLong("TOTAL_SIZE"));
      ssTO.setGuaranteedSize(res.getLong("GUAR_SIZE"));
      ssTO.setUnusedSize(res.getLong("FREE_SIZE"));
      ssTO.setSpaceFile(res.getString("SPACE_FILE"));
      ssTO.setStorageInfo("STORAGE_INFO");
      ssTO.setLifetime(res.getLong("LIFETIME"));
      ssTO.setSpaceType(res.getString("SPACE_TYPE"));
    }
    catch (SQLException ex) {
      ex.printStackTrace();
    }

    return ssTO;
  }


  /**
   * Main - only for test
   *
   * @param args String[]
   */
  public static void main(String[] args)
  {

    DataBaseStrategy db = PersistenceDirector.getDataBase();
    System.out.println("Connection string ="+ db.getConnectionString());

    StorageSpaceSQLHelper helper = new StorageSpaceSQLHelper("mysql");


    StorageSpaceTO ssTO = new StorageSpaceTO();
    ssTO.setAlias("Alias");
    ssTO.setCreated(new java.util.Date(System.currentTimeMillis()));
    ssTO.setGuaranteedSize(1000000);
    VO vo = VO.make("VOCiccio");
    GridUserInterface gu = null;
    gu = GridUserManager.makeStoRMGridUser();
    //gu = VomsGridUsermake("Ciccio");

    ssTO.setOwner(gu);
    ssTO.setOwnerName("Ciccio");
    ssTO.setSpaceFile("spaceFile");
    ssTO.setSpaceToken("spacetoken001");
    ssTO.setSpaceType(TSpaceType.VOLATILE.toString());

    String query = helper.insertQuery(ssTO);
    System.out.println("Insert Query = "+query);
  }



}
