package it.grid.storm.persistence.impl.mysql;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import it.grid.storm.persistence.dao.AbstractDAO;
import it.grid.storm.persistence.dao.VolatileAndJiTDAO;
import it.grid.storm.persistence.model.JiTData;
import it.grid.storm.persistence.pool.impl.StormDbConnectionPool;

/**
 * DAO class for VolatileAndJiTCatalog: it has been specifically designed for MySQL.
 * 
 * @author EGRID ICTP
 * @version 1.0 (based on old PinnedFilesDAO)
 * @date November, 2006
 */
public class VolatileAndJiTDAOMySql extends AbstractDAO implements VolatileAndJiTDAO {

  private static final Logger log = LoggerFactory.getLogger(VolatileAndJiTDAOMySql.class);

  private static VolatileAndJiTDAO instance;

  public static synchronized VolatileAndJiTDAO getInstance() {
    if (instance == null) {
      instance = new VolatileAndJiTDAOMySql();
    }
    return instance;
  }

  private VolatileAndJiTDAOMySql() {
    super(StormDbConnectionPool.getInstance());
  }

  /**
   * Method that inserts a new entry in the JiT table of the DB, consisting of the specified
   * filename, the local user uid, the local user gid, the acl, the start time as expressed by UNIX
   * epoch (seconds since 00:00:00 1 1 1970) and the number of seconds the jit must last.
   * 
   * In the DB, the start time gets translated into DATE:TIME in order to make it more readable.
   * pinLifetime remains in seconds.
   */
  public void addJiT(String filename, int uid, int gid, int acl, long start, long pinLifetime) {

    String sql =
        "INSERT INTO jit(file,uid,gid,acl,start,pinLifetime) VALUES(?,?,?,?,FROM_UNIXTIME(?),?)";

    Connection con = null;
    PreparedStatement stmt = null;
    try {
      con = getConnection();
      stmt = con.prepareStatement(sql);
      stmt.setString(1, filename);
      stmt.setInt(2, uid);
      stmt.setInt(3, gid);
      stmt.setInt(4, acl);
      stmt.setLong(5, start);
      stmt.setLong(6, pinLifetime);
      log.debug("VolatileAndJiTDAO. addJiT: {}", stmt);
      stmt.execute();
    } catch (SQLException e) {
      log.error("VolatileAndJiTDAO! Error in addJiT: {}", e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeStatement(stmt);
      closeConnection(con);
    }
  }

  /**
   * Method that inserts a new entry in the Volatile table of the DB, consisting of the specified
   * filename, the start time as expressed by UNIX epoch (seconds since 00:00:00 1 1 1970), and the
   * number of seconds the file must be kept for.
   * 
   * In the DB, the start time gets translated into DATE:TIME in order to make it more readable.
   * pinLifetime remains in seconds.
   */
  public void addVolatile(String filename, long start, long fileLifetime) {

    String sql = "INSERT INTO volatile(file,start,fileLifetime) VALUES(?,FROM_UNIXTIME(?),?)";

    Connection con = null;
    PreparedStatement stmt = null;
    try {
      con = getConnection();
      stmt = con.prepareStatement(sql);
      stmt.setString(1, filename);
      stmt.setLong(2, start);
      stmt.setLong(3, fileLifetime);
      log.debug("VolatileAndJiTDAO. addVolatile: {}", stmt);
      stmt.execute();
    } catch (SQLException e) {
      log.error("VolatileAndJiTDAO! Error in addVolatile: {}", e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeStatement(stmt);
      closeConnection(con);
    }
  }

  /**
   * Checks whether the given file exists in the volatile table or not.
   * 
   * @param filename
   * @return <code>true</code> if there is antry for the given file in the volatilte table,
   *         <code>false</code> otherwise.
   */
  public boolean exists(String filename) {

    String sql = "SELECT ID FROM volatile WHERE file=? LIMIT 1";

    Connection con = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    boolean result;

    try {
      con = getConnection();
      stmt = con.prepareStatement(sql);
      stmt.setString(1, filename);
      log.debug("VolatileAndJiTDAO - existsOnVolatile - {}", stmt);
      rs = stmt.executeQuery();

      if (rs.next()) {
        result = true;
      } else {
        result = false;
      }
    } catch (SQLException e) {
      log.error("VolatileAndJiTDAO! Error in existsOnVolatile: {}", e.getMessage(), e);
      e.printStackTrace();
      result = false;
    } finally {
      closeResultSet(rs);
      closeStatement(stmt);
      closeConnection(con);
    }
    return result;
  }

  /**
   * Method that updates an existing entry in the JiT table of the DB, consisting of the specified
   * filename, the uid and gid of the local user, the acl, the start time as expressed by UNIX epoch
   * (seconds since 00:00:00 1 1 1970), and the number of seconds the jit must last.
   * 
   * In the DB, the start time gets translated into DATE:TIME in order to make it more readable.
   * pinLifetime remains in seconds.
   * 
   * This method _forces_ the update regardless of the fact that the new expiry lasts less than the
   * current one! This method is intended to be used by expireJiT.
   * 
   * Only start and pinLifetime get updated, while filename, uid, gid and acl, are used as criteria
   * to select records.
   */
  public void forceUpdateJiT(String filename, int uid, int acl, long start, long pinLifetime) {

    String sql = "UPDATE jit " + "SET start=FROM_UNIXTIME(?), pinLifetime=? "
        + "WHERE file=? AND uid=? AND acl=?";

    Connection con = null;
    PreparedStatement stmt = null;
    try {
      con = getConnection();
      stmt = con.prepareStatement(sql);
      stmt.setLong(1, start);
      stmt.setLong(2, pinLifetime);
      stmt.setString(3, filename);
      stmt.setInt(4, uid);
      stmt.setInt(5, acl);
      log.debug("VolatileAndJiTDAO. forceUpdateJiT: {}", stmt);
      int n = stmt.executeUpdate();
      log.debug("VolatileAndJiTDAO. {} jit entries forced updated.", n);
    } catch (SQLException e) {
      log.error("VolatileAndJiTDAO! Error in forceUpdateJiT: {}", e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeStatement(stmt);
      closeConnection(con);
    }
  }

  /**
   * Method that returns the number of entries in the catalogue, matching the given filename, uid
   * and acl.
   * 
   * Notice that in general there should be either one or none, and more should be taken as
   * indication of catalogue corruption.
   * 
   * -1 is returned if there are problems with the DB.
   */
  public int numberJiT(String filename, int uid, int acl) {

    String sql = "SELECT COUNT(ID) FROM jit WHERE file=? AND uid=? AND acl=?";

    Connection con = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    int n = -1;

    try {
      con = getConnection();
      stmt = con.prepareStatement(sql);
      stmt.setString(1, filename);
      stmt.setInt(2, uid);
      stmt.setInt(3, acl);
      log.debug("VolatileAndJiTDAO. numberJiT: {}", stmt);
      rs = stmt.executeQuery();

      if (rs.next()) {
        n = rs.getInt(1);
      } else {
        log.error("VolatileAndJiTDAO! Unexpected situation in numberJiT: " + "result set empty!");
      }
    } catch (SQLException e) {
      log.error("VolatileAndJiTDAO! Error in numberJiT: {}", e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeResultSet(rs);
      closeStatement(stmt);
      closeConnection(con);
    }
    return n;
  }

  /**
   * Method that returns the number of Volatile entries in the catalogue, for the given filename.
   * 
   * Notice that in general there should be either one or none, and more should be taken as
   * indication of catalogue corruption.
   * 
   * -1 is returned if there are problems with the DB.
   */
  public int numberVolatile(String filename) {

    String sql = "SELECT COUNT(ID) FROM volatile WHERE file=?";

    Connection con = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    int n = -1;

    try {
      con = getConnection();
      stmt = con.prepareStatement(sql);
      stmt.setString(1, filename);
      log.debug("VolatileAndJiTDAO. numberVolatile: {}", stmt);
      rs = stmt.executeQuery();
      if (rs.next()) {
        n = rs.getInt(1);
      } else {
        log.error(
            "VolatileAndJiTDAO! Unexpected situation in numberVolatile: " + "result set empty!");
      }
    } catch (SQLException e) {
      log.error("VolatileAndJiTDAO! Error in numberVolatile: {}", e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeResultSet(rs);
      closeStatement(stmt);
      closeConnection(con);
    }
    return n;
  }

  /**
   * Method that removes all entries in the JiT table of the DB, that match the specified filename.
   * So this action takes place _regardless_ of the user that set up the ACL!
   */
  public void removeAllJiTsOn(String filename) {

    String sql = "DELETE FROM jit WHERE file=?";

    Connection con = null;
    PreparedStatement stmt = null;
    try {
      con = getConnection();
      stmt = con.prepareStatement(sql);
      stmt.setString(1, filename);
      log.debug("VolatileAndJiTDAO. removeJiT: {}", stmt);
      int n = stmt.executeUpdate();
      log.debug("VolatileAndJiTDAO. removeJiT: {} entries removed", n);
    } catch (SQLException e) {
      log.error("VolatileAndJiTDAO! Error in removeJiT: {}", e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeStatement(stmt);
      closeConnection(con);
    }
  }

  /**
   * Method used to remove all expired entries, both of pinned files and of jit ACLs. Also, when
   * removing volatile entries, any jit entry that refers to those expired volatiles will also be
   * removed.
   * 
   * The method requires a long representing the time measured as UNIX EPOCH upon which to base the
   * purging: entries are evaluated expired when compared to this date.
   * 
   * The method returns an array of two Collections; Collection[0] contains expired volatile entries
   * String PFNs, while Collection[1] contains JiTDataTO objects. Collection[1] also contains those
   * entries that may not have expired yet, but since the respective Volatile is being removed they
   * too must be removed automatically.
   * 
   * WARNING! If any error occurs it gets logged, and an array of two empty Collection is returned.
   * This operation is treated as a Transaction by the DB, so a Roll Back should return everything
   * to its original state!
   */
  public List<Object> removeExpired(long time) {

    List<Object> output = Lists.newArrayList(Lists.newArrayList(), Lists.newArrayList());

    String vol = "SELECT ID,file FROM volatile WHERE (UNIX_TIMESTAMP(start)+fileLifetime<?)";
    String jit = "SELECT ID,file,acl,uid,gid FROM jit WHERE (UNIX_TIMESTAMP(start)+pinLifetime<?)";
    String delvol = "DELETE FROM volatile WHERE ID IN ";
    String deljit = "DELETE FROM jit WHERE ID IN ";

    Connection con = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try {

      con = getConnection();
      stmt = con.prepareStatement(vol);
      stmt.setLong(1, time);
      log.debug("VolatileAndJiTDAO. removeExpired: {}", stmt);
      rs = stmt.executeQuery();
      Collection<String> volat = Lists.newArrayList();
      Collection<Long> volatid = Lists.newArrayList();
      while (rs.next()) {
        volatid.add(Long.valueOf(rs.getLong("ID")));
        volat.add(rs.getString("file"));
      }
      int nvolat = volatid.size();
      closeResultSet(rs);
      closeStatement(stmt);

      // get list of jits
      if (nvolat > 0) {
        // there are expired volatile entries: adjust jit selection to include
        // those SURLs too!
        jit = jit + " OR file IN " + makeFileString(volat);
      }
      stmt = con.prepareStatement(jit);
      stmt.setLong(1, time);
      log.debug("VolatileAndJiTDAO. removeExpired: {}", stmt);
      rs = stmt.executeQuery();

      Collection<JiTData> track = Lists.newArrayList();
      Collection<Long> trackid = Lists.newArrayList();

      while (rs.next()) {
        trackid.add(Long.valueOf(rs.getLong("ID")));
        JiTData aux =
            new JiTData(rs.getString("file"), rs.getInt("acl"), rs.getInt("uid"), rs.getInt("gid"));
        track.add(aux);
      }
      int njit = trackid.size();
      closeResultSet(rs);
      closeStatement(stmt);

      // remove entries
      Collection<String> volcol = Lists.newArrayList();
      Collection<JiTData> jitcol = Lists.newArrayList();
      try {
        con.setAutoCommit(false); // begin transaction!
        // delete volatile
        int deletedvol = 0;
        if (nvolat > 0) {
          delvol = delvol + makeIDString(volatid);
          stmt = con.prepareStatement(delvol);
          log.debug("VolatileAndJiTDAO. removeExpired: {}", stmt);
          deletedvol = stmt.executeUpdate();
          closeStatement(stmt);
        }
        // delete jits
        int deletedjit = 0;
        if (njit > 0) {
          deljit = deljit + makeIDString(trackid);
          stmt = con.prepareStatement(deljit);
          log.debug("VolatileAndJiTDAO. removeExpired: {}", stmt);
          deletedjit = stmt.executeUpdate();
          closeStatement(stmt);
        }
        con.commit();
        con.setAutoCommit(true); // end transaction!
        log.debug("VolatileAndJiTDAO. Removed {} volatile catalogue entries "
            + "and {} jit catalogue entries.", deletedvol, deletedjit);
        volcol = volat;
        jitcol = track;
      } catch (SQLException e) {
        log.error("VolatileAndJiTDAO! Unable to complete removeExpired... " + "rolling back! {}",
            e.getMessage(), e);
        con.rollback();
        closeStatement(stmt);
      }

      // return collections
      return Lists.newArrayList(volcol, jitcol);
    } catch (SQLException e) {
      log.error("VolatileAndJiTDAO! Unable to complete removeExpired! {}", e.getMessage(), e);
      // in case of any failure return an array of two empty Collection
      return output;
    } finally {
      closeResultSet(rs);
      closeStatement(stmt);
      closeConnection(con);
    }
  }

  /**
   * Method that updates an existing entry in the JiT table of the DB, consisting of the specified
   * filename, the uid and gid of the local user, the acl, the start time as expressed by UNIX epoch
   * (seconds since 00:00:00 1 1 1970), and the number of seconds the jit must last.
   * 
   * In the DB, the start time gets translated into DATE:TIME in order to make it more readable.
   * pinLifetime remains in seconds.
   * 
   * Entries get updated only if the new expiry calculated by adding start and pinLifetime, is
   * larger than the existing one.
   * 
   * Only start and pinLifetime get updated, while filename, uid, gid and acl, are used as criteria
   * to select records.
   */
  public void updateJiT(String filename, int uid, int acl, long start, long pinLifetime) {

    String sql = "UPDATE jit SET start=FROM_UNIXTIME(?), pinLifetime=? "
        + "WHERE file=? AND uid=? AND acl=? AND (UNIX_TIMESTAMP(start)+pinLifetime<?)";

    Connection con = null;
    PreparedStatement stmt = null;
    try {
      con = getConnection();
      stmt = con.prepareStatement(sql);
      stmt.setLong(1, start);
      stmt.setLong(2, pinLifetime);
      stmt.setString(3, filename);
      stmt.setInt(4, uid);
      stmt.setInt(5, acl);
      stmt.setLong(6, start + pinLifetime);
      log.debug("VolatileAndJiTDAO. updateJiT: {}", stmt);
      int n = stmt.executeUpdate();
      log.debug("VolatileAndJiTDAO. {} jit entries updated.", n);
    } catch (SQLException e) {
      log.error("VolatileAndJiTDAO! Error in updateJiT: {}", e.getMessage(), e);
    } finally {
      closeStatement(stmt);
      closeConnection(con);
    }
  }

  /**
   * Method that updates an existing entry in the Volatile table of the DB, consisting of the
   * specified filename, the start time as expressed by UNIX epoch (seconds since 00:00:00 1 1
   * 1970), and the number of seconds the file must be kept for.
   * 
   * In the DB, the start time gets translated into DATE:TIME in order to make it more readable.
   * pinLifetime remains in seconds.
   * 
   * Entries get updated only if the new expiry calculated by adding start and fileLifetime, is
   * larger than the existing one.
   */
  public void updateVolatile(String filename, long start, long fileLifetime) {

    String sql = "UPDATE volatile SET file=?, start=FROM_UNIXTIME(?), fileLifetime=? "
        + "WHERE file=? AND (UNIX_TIMESTAMP(start)+fileLifetime<?)";

    Connection con = null;
    PreparedStatement stmt = null;
    try {
      con = getConnection();
      stmt = con.prepareStatement(sql);
      stmt.setString(1, filename);
      stmt.setLong(2, start);
      stmt.setLong(3, fileLifetime);
      stmt.setString(4, filename);
      stmt.setLong(5, start + fileLifetime);
      log.debug("VolatileAndJiTDAO. updateVolatile: {}", stmt);
      int n = stmt.executeUpdate();
      log.debug("VolatileAndJiTDAO. {} volatile entries updated.", n);
    } catch (SQLException e) {
      log.error("VolatileAndJiTDAO! Error in updateVolatile: {}", e.getMessage(), e);
    } finally {
      closeStatement(stmt);
      closeConnection(con);
    }
  }

  public void updateVolatile(String fileName, long fileStart) {

    String sql = "UPDATE volatile SET start=FROM_UNIXTIME(?) WHERE file=?";

    Connection con = null;
    PreparedStatement stmt = null;
    try {
      con = getConnection();
      stmt = con.prepareStatement(sql);
      stmt.setLong(1, fileStart);
      stmt.setString(2, fileName);
      log.debug("VolatileAndJiTDAO. updateVolatile: {}", stmt);
      int n = stmt.executeUpdate();
      log.debug("VolatileAndJiTDAO. {} volatile entries updated.", n);
    } catch (SQLException e) {
      log.error("VolatileAndJiTDAO! Error in updateVolatile: {}", e.getMessage(), e);
    } finally {
      closeStatement(stmt);
      closeConnection(con);
    }
  }

  /**
   * Method used to find out the starting time and lifetime, expressed as long, of a Volatile file
   * identified by the String filename.
   * 
   * The two long are returned inside a List: the first one is the start time expressed in Unix
   * epoch; the second long is the lifetime expressed in seconds.
   * 
   * In case no entry is found or there are errors, an empty List is returned and proper error
   * messages get logged.
   */
  public List<Long> volatileInfoOn(String filename) {

    String sql = "SELECT UNIX_TIMESTAMP(start), fileLifetime FROM volatile WHERE file=?";

    Connection con = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    List<Long> aux = Lists.newArrayList();

    try {
      con = getConnection();
      stmt = con.prepareStatement(sql);
      stmt.setString(1, filename);
      log.debug("VolatileAndJiTDAO - infoOnVolatile - {}", stmt);
      rs = stmt.executeQuery();
      if (rs.next()) {
        aux.add(rs.getLong("UNIX_TIMESTAMP(start)"));
        aux.add(rs.getLong("fileLifetime"));
      } else {
        log.debug("VolatileAndJiTDAO! infoOnVolatile did not find {}", filename);
      }
    } catch (SQLException e) {
      log.error("VolatileAndJiTDAO! Error in infoOnVolatile: {}", e.getMessage(), e);
    } finally {
      closeResultSet(rs);
      closeStatement(stmt);
      closeConnection(con);
    }
    return aux;
  }

  /**
   * Method that returns a String containing all Files.
   */
  private String makeFileString(Collection<String> files) {

    StringBuilder sb = new StringBuilder("(");
    for (Iterator<String> i = files.iterator(); i.hasNext();) {
      sb.append("'");
      sb.append(i.next());
      sb.append("'");
      if (i.hasNext()) {
        sb.append(",");
      }
    }
    sb.append(")");
    return sb.toString();
  }

  /**
   * Method that returns a String containing all IDs.
   */
  private String makeIDString(Collection<Long> rowids) {

    StringBuilder sb = new StringBuilder("(");
    for (Iterator<Long> i = rowids.iterator(); i.hasNext();) {
      sb.append(String.valueOf(i.next()));
      if (i.hasNext()) {
        sb.append(",");
      }
    }
    sb.append(")");
    return sb.toString();
  }
}
