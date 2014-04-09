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

package it.grid.storm.catalogs;

import it.grid.storm.config.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for VolatileAndJiTCatalog: it has been specifically designed for
 * MySQL.
 * 
 * @author EGRID ICTP
 * @version 1.0 (based on old PinnedFilesDAO)
 * @date November, 2006
 */
public class VolatileAndJiTDAO  extends ConnectionPoolManagerStrategy{

	private static final Logger log = LoggerFactory
			.getLogger(VolatileAndJiTDAO.class);

	private final String driver = Configuration.getInstance().getDBDriver(); // String
																				// with
																				// the
																				// name
																				// of
																				// the
																				// class
																				// for
																				// the
																				// DB
																				// driver
	private final String url = Configuration.getInstance().getDBURL(); // String
																		// referring
																		// to
																		// the
																		// URL
																		// of
																		// the
																		// DB
	private final String password = Configuration.getInstance().getDBPassword(); // String
																					// with
																					// the
																					// password
																					// for
																					// the
																					// DB
	private final String name = Configuration.getInstance().getDBUserName(); // String
																				// with
																				// the
																				// name
																				// for
																				// the
																				// DB
	private Connection con = null; // Connection to DB

	private static final VolatileAndJiTDAO dao = new VolatileAndJiTDAO(); // only
																			// instance
																			// of
																			// DAO

	

	private VolatileAndJiTDAO() {
		
	}

	/**
	 * Method that returns the only instance of VolatileAndJiTDAO.
	 */
	public static VolatileAndJiTDAO getInstance() {

		return dao;
	}

	/**
	 * Method that inserts a new entry in the JiT table of the DB, consisting of
	 * the specified filename, the local user uid, the local user gid, the acl,
	 * the start time as expressed by UNIX epoch (seconds since 00:00:00 1 1
	 * 1970) and the number of seconds the jit must last.
	 * 
	 * In the DB, the start time gets translated into DATE:TIME in order to make
	 * it more readable. pinLifetime remains in seconds.
	 */
	public void addJiT(String filename, int uid, int gid, int acl, long start,
			long pinLifetime) {

		con = setUpConnection();
		
		String sql = "INSERT INTO jit(file,uid,gid,acl,start,pinLifetime) VALUES(?,?,?,?,FROM_UNIXTIME(?),?)";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			logWarnings(con.getWarnings());
			stmt.setString(1, filename);
			logWarnings(stmt.getWarnings());
			stmt.setInt(2, uid);
			logWarnings(stmt.getWarnings());
			stmt.setInt(3, gid);
			logWarnings(stmt.getWarnings());
			stmt.setInt(4, acl);
			logWarnings(stmt.getWarnings());
			stmt.setLong(5, start);
			logWarnings(stmt.getWarnings());
			stmt.setLong(6, pinLifetime);
			logWarnings(stmt.getWarnings());
			log.debug("VolatileAndJiTDAO. addJiT: {}", stmt.toString());
			stmt.execute();
		} catch (SQLException e) {
			log.error("VolatileAndJiTDAO! Error in addJiT: {}", e.getMessage(),
					e);
		} finally {
			close(stmt);
			takeDownConnection(con);
		}
	}

	/**
	 * Method that inserts a new entry in the Volatile table of the DB,
	 * consisting of the specified filename, the start time as expressed by UNIX
	 * epoch (seconds since 00:00:00 1 1 1970), and the number of seconds the
	 * file must be kept for.
	 * 
	 * In the DB, the start time gets translated into DATE:TIME in order to make
	 * it more readable. pinLifetime remains in seconds.
	 */
	public void addVolatile(String filename, long start, long fileLifetime) {

		con = setUpConnection();
		
		String sql = "INSERT INTO volatile(file,start,fileLifetime) VALUES(?,FROM_UNIXTIME(?),?)";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			logWarnings(con.getWarnings());
			stmt.setString(1, filename);
			logWarnings(stmt.getWarnings());
			stmt.setLong(2, start);
			logWarnings(stmt.getWarnings());
			stmt.setLong(3, fileLifetime);
			logWarnings(stmt.getWarnings());
			log.debug("VolatileAndJiTDAO. addVolatile: {}", stmt.toString());
			stmt.execute();
		} catch (SQLException e) {
			log.error("VolatileAndJiTDAO! Error in addVolatile: {}",
					e.getMessage(), e);
		} finally {
			close(stmt);
			takeDownConnection(con);
		}
	}

	/**
	 * Checks whether the given file exists in the volatile table or not.
	 * 
	 * @param filename
	 * @return <code>true</code> if there is antry for the given file in the
	 *         volatilte table, <code>false</code> otherwise.
	 */
	public boolean exists(String filename) {

		con = setUpConnection();
		
		String sql = "SELECT ID FROM volatile WHERE file=? LIMIT 1";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean result;

		try {
			stmt = con.prepareStatement(sql);
			logWarnings(con.getWarnings());

			stmt.setString(1, filename);
			logWarnings(stmt.getWarnings());

			log.debug("VolatileAndJiTDAO - existsOnVolatile - {}",
					stmt.toString());

			rs = stmt.executeQuery();
			logWarnings(stmt.getWarnings());

			if (rs.next()) {
				result = true;
			} else {
				result = false;
			}
		} catch (SQLException e) {
			log.error("VolatileAndJiTDAO! Error in existsOnVolatile: {}",
					e.getMessage(), e);
			result = false;
		} finally {
			close(rs);
			close(stmt);
			takeDownConnection(con);
		}
		return result;
	}

	/**
	 * Method that updates an existing entry in the JiT table of the DB,
	 * consisting of the specified filename, the uid and gid of the local user,
	 * the acl, the start time as expressed by UNIX epoch (seconds since
	 * 00:00:00 1 1 1970), and the number of seconds the jit must last.
	 * 
	 * In the DB, the start time gets translated into DATE:TIME in order to make
	 * it more readable. pinLifetime remains in seconds.
	 * 
	 * This method _forces_ the update regardless of the fact that the new
	 * expiry lasts less than the current one! This method is intended to be
	 * used by expireJiT.
	 * 
	 * Only start and pinLifetime get updated, while filename, uid, gid and acl,
	 * are used as criteria to select records.
	 */
	public void forceUpdateJiT(String filename, int uid, int acl, long start,
			long pinLifetime) {

		con = setUpConnection();
		
		String sql = "UPDATE jit "
				+ "SET start=FROM_UNIXTIME(?), pinLifetime=? "
				+ "WHERE file=? AND uid=? AND acl=?";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			logWarnings(con.getWarnings());
			stmt.setLong(1, start);
			logWarnings(stmt.getWarnings());
			stmt.setLong(2, pinLifetime);
			logWarnings(stmt.getWarnings());
			stmt.setString(3, filename);
			logWarnings(stmt.getWarnings());
			stmt.setInt(4, uid);
			logWarnings(stmt.getWarnings());
			stmt.setInt(5, acl);
			logWarnings(stmt.getWarnings());
			log.debug("VolatileAndJiTDAO. forceUpdateJiT: {}", stmt.toString());
			int n = stmt.executeUpdate();
			log.debug("VolatileAndJiTDAO. {} jit entries forced updated.", n);
		} catch (SQLException e) {
			log.error("VolatileAndJiTDAO! Error in forceUpdateJiT: {}",
					e.getMessage(), e);
		} finally {
			close(stmt);
			takeDownConnection(con);
		}
	}

	/**
	 * Method that returns the number of entries in the catalogue, matching the
	 * given filename, uid and acl.
	 * 
	 * Notice that in general there should be either one or none, and more
	 * should be taken as indication of catalogue corruption.
	 * 
	 * -1 is returned if there are problems with the DB.
	 */
	public int numberJiT(String filename, int uid, int acl) {

		con = setUpConnection();
		
		String sql = "SELECT COUNT(ID) FROM jit WHERE file=? AND uid=? AND acl=?";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.prepareStatement(sql);
			logWarnings(con.getWarnings());
			stmt.setString(1, filename);
			logWarnings(stmt.getWarnings());
			stmt.setInt(2, uid);
			logWarnings(stmt.getWarnings());
			stmt.setInt(3, acl);
			logWarnings(stmt.getWarnings());
			log.debug("VolatileAndJiTDAO. numberJiT: {}", stmt.toString());
			rs = stmt.executeQuery();
			logWarnings(stmt.getWarnings());
			int n = -1;
			if (rs.next()) {
				n = rs.getInt(1);
			} else {
				log.error("VolatileAndJiTDAO! Unexpected situation in numberJiT: "
						+ "result set empty!");
			}
			close(rs);
			close(stmt);
			takeDownConnection(con);
			return n;
		} catch (SQLException e) {
			log.error("VolatileAndJiTDAO! Error in numberJiT: {}",
					e.getMessage(), e);
			close(rs);
			close(stmt);
			return -1;
		}
	}

	/**
	 * Method that returns the number of Volatile entries in the catalogue, for
	 * the given filename.
	 * 
	 * Notice that in general there should be either one or none, and more
	 * should be taken as indication of catalogue corruption.
	 * 
	 * -1 is returned if there are problems with the DB.
	 */
	public int numberVolatile(String filename) {

		con = setUpConnection();
		
		String sql = "SELECT COUNT(ID) FROM volatile WHERE file=?";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.prepareStatement(sql);
			logWarnings(con.getWarnings());
			stmt.setString(1, filename);
			logWarnings(stmt.getWarnings());
			log.debug("VolatileAndJiTDAO. numberVolatile: {}", stmt.toString());
			rs = stmt.executeQuery();
			logWarnings(stmt.getWarnings());
			int n = -1;
			if (rs.next()) {
				n = rs.getInt(1);
			} else {
				log.error("VolatileAndJiTDAO! Unexpected situation in numberVolatile: "
						+ "result set empty!");
			}
			close(rs);
			close(stmt);
			takeDownConnection(con);
			return n;
		} catch (SQLException e) {
			log.error("VolatileAndJiTDAO! Error in numberVolatile: {}",
					e.getMessage(), e);
			close(rs);
			close(stmt);
			return -1;
		}
	}

	/**
	 * Method that removes all entries in the JiT table of the DB, that match
	 * the specified filename. So this action takes place _regardless_ of the
	 * user that set up the ACL!
	 */
	public void removeAllJiTsOn(String filename) {

		con = setUpConnection();
		
		String sql = "DELETE FROM jit WHERE file=?";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			logWarnings(con.getWarnings());
			stmt.setString(1, filename);
			logWarnings(stmt.getWarnings());
			log.debug("VolatileAndJiTDAO. removeJiT: {}", stmt.toString());
			int n = stmt.executeUpdate();
			log.debug("VolatileAndJiTDAO. removeJiT: {} entries removed", n);
		} catch (SQLException e) {
			log.error("VolatileAndJiTDAO! Error in removeJiT: {}",
					e.getMessage(), e);
		} finally {
			close(stmt);
			takeDownConnection(con);
		}
	}

	/**
	 * Method used to remove all expired entries, both of pinned files and of
	 * jit ACLs. Also, when removing volatile entries, any jit entry that refers
	 * to those expired volatiles will also be removed.
	 * 
	 * The method requires a long representing the time measured as UNIX EPOCH
	 * upon which to base the purging: entries are evaluated expired when
	 * compared to this date.
	 * 
	 * The method returns an array of two Collections; Collection[0] contains
	 * expired volatile entries String PFNs, while Collection[1] contains
	 * JiTDataTO objects. Collection[1] also contains those entries that may not
	 * have expired yet, but since the respective Volatile is being removed they
	 * too must be removed automatically.
	 * 
	 * WARNING! If any error occurs it gets logged, and an array of two empty
	 * Collection is returned. This operation is treated as a Transcation by the
	 * DB, so a Roll Back should return everything to its original state!
	 */
	public Collection[] removeExpired(long time) {

		con = setUpConnection();

		String vol = "SELECT ID,file FROM volatile WHERE (UNIX_TIMESTAMP(start)+fileLifetime<?)";
		String jit = "SELECT ID,file,acl,uid,gid FROM jit WHERE (UNIX_TIMESTAMP(start)+pinLifetime<?)";
		String delvol = "DELETE FROM volatile WHERE ID IN ";
		String deljit = "DELETE FROM jit WHERE ID IN ";

		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			// get list of expired volatile
			stmt = con.prepareStatement(vol);
			logWarnings(con.getWarnings());
			stmt.setLong(1, time);
			logWarnings(stmt.getWarnings());
			log.debug("VolatileAndJiTDAO. removeExpired: {}", stmt.toString());
			rs = stmt.executeQuery();
			logWarnings(stmt.getWarnings());
			Collection volat = new ArrayList();
			Collection volatid = new ArrayList();
			while (rs.next()) {
				volatid.add(new Long(rs.getLong("ID")));
				volat.add(rs.getString("file"));
			}
			int nvolat = volatid.size();
			close(rs);
			close(stmt);
			// get list of jits
			if (nvolat > 0) {
				// there are expired volatile entries: adjust jit selection to
				// include
				// those SURLs too!
				jit = jit + " OR file IN " + makeFileString(volat);
			}
			stmt = con.prepareStatement(jit);
			logWarnings(con.getWarnings());
			stmt.setLong(1, time);
			logWarnings(stmt.getWarnings());
			log.debug("VolatileAndJiTDAO. removeExpired: {}", stmt.toString());
			rs = stmt.executeQuery();
			logWarnings(stmt.getWarnings());
			Collection track = new ArrayList();
			Collection trackid = new ArrayList();
			JiTData aux = null;
			while (rs.next()) {
				trackid.add(new Long(rs.getLong("ID")));
				aux = new JiTData(rs.getString("file"), rs.getInt("acl"),
						rs.getInt("uid"), rs.getInt("gid"));
				track.add(aux);
			}
			int njit = trackid.size();
			close(rs);
			close(stmt);

			// remove entries
			Collection volcol = new ArrayList();
			Collection jitcol = new ArrayList();
			try {
				con.setAutoCommit(false); // begin transaction!
				logWarnings(con.getWarnings());
				// delete volatile
				int deletedvol = 0;
				if (nvolat > 0) {
					delvol = delvol + makeIDString(volatid);
					stmt = con.prepareStatement(delvol);
					logWarnings(con.getWarnings());
					log.debug("VolatileAndJiTDAO. removeExpired: {}",
							stmt.toString());
					deletedvol = stmt.executeUpdate();
					logWarnings(stmt.getWarnings());
					close(stmt);
				}
				// delete jits
				int deletedjit = 0;
				if (njit > 0) {
					deljit = deljit + makeIDString(trackid);
					stmt = con.prepareStatement(deljit);
					logWarnings(con.getWarnings());
					log.debug("VolatileAndJiTDAO. removeExpired: {}",
							stmt.toString());
					deletedjit = stmt.executeUpdate();
					logWarnings(stmt.getWarnings());
					close(stmt);
				}
				con.commit();
				logWarnings(con.getWarnings());
				con.setAutoCommit(true); // end transaction!
				logWarnings(con.getWarnings());
				log.debug(
						"VolatileAndJiTDAO. Removed {} volatile catalogue entries "
								+ "and {} jit catalogue entries.", deletedvol,
						deletedjit);
				volcol = volat;
				jitcol = track;
			} catch (SQLException e) {
				log.error(
						"VolatileAndJiTDAO! Unable to complete removeExpired... "
								+ "rolling back! {}", e.getMessage(), e);
				rollback(con);
				close(stmt);
			}

			// return collections
			return new Collection[] { volcol, jitcol };
		} catch (SQLException e) {
			close(rs);
			close(stmt);
			log.error(
					"VolatileAndJiTDAO! Unable to complete removeExpired! {}",
					e.getMessage(), e);
			return new Collection[] { new ArrayList(), new ArrayList() }; // in
																			// case
																			// of
																			// any
																			// failure
																			// return
																			// an
																			// array
																			// of
																			// two
																			// empty
																			// Collection
		}
		finally{
			takeDownConnection(con);
		}
	}

	/**
	 * Method that removes all entries in the Volatile table of the DB, that
	 * match the specified filename.
	 */
	public void removeVolatile(String filename) {

		con = setUpConnection();
		
		String sql = "DELETE FROM volatile WHERE file=?";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			logWarnings(con.getWarnings());
			stmt.setString(1, filename);
			logWarnings(stmt.getWarnings());
			log.debug("VolatileAndJiTDAO. removeVolatile: {}", stmt.toString());
			int n = stmt.executeUpdate();
			log.debug("VolatileAndJiTDAO. removeVolatile: {} entries removed.",
					n);
		} catch (SQLException e) {
			log.error("VolatileAndJiTDAO! Error in removeVolatile: {}",
					e.getMessage(), e);
		} finally {
			close(stmt);
			takeDownConnection(con);
		}
	}

	/**
	 * Method that updates an existing entry in the JiT table of the DB,
	 * consisting of the specified filename, the uid and gid of the local user,
	 * the acl, the start time as expressed by UNIX epoch (seconds since
	 * 00:00:00 1 1 1970), and the number of seconds the jit must last.
	 * 
	 * In the DB, the start time gets translated into DATE:TIME in order to make
	 * it more readable. pinLifetime remains in seconds.
	 * 
	 * Entries get updated only if the new expiry calculated by adding start and
	 * pinLifetime, is larger than the existing one.
	 * 
	 * Only start and pinLifetime get updated, while filename, uid, gid and acl,
	 * are used as criteria to select records.
	 */
	public void updateJiT(String filename, int uid, int acl, long start,
			long pinLifetime) {

		con = setUpConnection();
		
		String sql = "UPDATE jit "
				+ "SET start=FROM_UNIXTIME(?), pinLifetime=? "
				+ "WHERE file=? AND uid=? AND acl=? AND (UNIX_TIMESTAMP(start)+pinLifetime<?)";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			logWarnings(con.getWarnings());
			stmt.setLong(1, start);
			logWarnings(stmt.getWarnings());
			stmt.setLong(2, pinLifetime);
			logWarnings(stmt.getWarnings());
			stmt.setString(3, filename);
			logWarnings(stmt.getWarnings());
			stmt.setInt(4, uid);
			logWarnings(stmt.getWarnings());
			stmt.setInt(5, acl);
			logWarnings(stmt.getWarnings());
			stmt.setLong(6, start + pinLifetime);
			logWarnings(stmt.getWarnings());
			log.debug("VolatileAndJiTDAO. updateJiT: {}", stmt.toString());
			int n = stmt.executeUpdate();
			log.debug("VolatileAndJiTDAO. {} jit entries updated.", n);
		} catch (SQLException e) {
			log.error("VolatileAndJiTDAO! Error in updateJiT: {}",
					e.getMessage(), e);
		} finally {
			close(stmt);
			takeDownConnection(con);
		}
	}

	/**
	 * Method that updates an existing entry in the Volatile table of the DB,
	 * consisting of the specified filename, the start time as expressed by UNIX
	 * epoch (seconds since 00:00:00 1 1 1970), and the number of seconds the
	 * file must be kept for.
	 * 
	 * In the DB, the start time gets translated into DATE:TIME in order to make
	 * it more readable. pinLifetime remains in seconds.
	 * 
	 * Entries get updated only if the new expiry calculated by adding start and
	 * fileLifetime, is larger than the existing one.
	 */
	public void updateVolatile(String filename, long start, long fileLifetime) {

		con = setUpConnection();
		
		String sql = "UPDATE volatile "
				+ "SET file=?, start=FROM_UNIXTIME(?), fileLifetime=? "
				+ "WHERE file=? AND (UNIX_TIMESTAMP(start)+fileLifetime<?)";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			logWarnings(con.getWarnings());
			stmt.setString(1, filename);
			logWarnings(stmt.getWarnings());
			stmt.setLong(2, start);
			logWarnings(stmt.getWarnings());
			stmt.setLong(3, fileLifetime);
			logWarnings(stmt.getWarnings());
			stmt.setString(4, filename);
			logWarnings(stmt.getWarnings());
			stmt.setLong(5, start + fileLifetime);
			logWarnings(stmt.getWarnings());
			log.debug("VolatileAndJiTDAO. updateVolatile: {}", stmt.toString());
			int n = stmt.executeUpdate();
			log.debug("VolatileAndJiTDAO. {} volatile entries updated.", n);
		} catch (SQLException e) {
			log.error("VolatileAndJiTDAO! Error in updateVolatile: {}",
					e.getMessage(), e);
		} finally {
			close(stmt);
			takeDownConnection(con);
		}
	}

	public void updateVolatile(String fileName, long fileStart) {

		con = setUpConnection();
		
		String sql = "UPDATE volatile " + "SET start=FROM_UNIXTIME(?) "
				+ "WHERE file=?";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			logWarnings(con.getWarnings());
			stmt.setLong(1, fileStart);
			logWarnings(stmt.getWarnings());
			stmt.setString(2, fileName);
			logWarnings(stmt.getWarnings());
			log.debug("VolatileAndJiTDAO. updateVolatile: {}", stmt.toString());
			int n = stmt.executeUpdate();
			log.debug("VolatileAndJiTDAO. {} volatile entries updated.", n);
		} catch (SQLException e) {
			log.error("VolatileAndJiTDAO! Error in updateVolatile: {}",
					e.getMessage(), e);
		} finally {
			close(stmt);
			takeDownConnection(con);
		}
	}

	/**
	 * Method used to find out the starting time and lifetime, expressed as
	 * long, of a Volatile file identified by the String filename.
	 * 
	 * The two long are returned inside a List: the first one is the start time
	 * expressed in Unix epoch; the second long is the lifetime expressed in
	 * seconds.
	 * 
	 * In case no entry is found or there are errors, an empty List is returned
	 * and proper error messagges get logged.
	 */
	public List<Long> volatileInfoOn(String filename) {

		con = setUpConnection();
		
		String sql = "SELECT UNIX_TIMESTAMP(start), fileLifetime FROM volatile WHERE file=?";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Long> aux = new ArrayList<Long>();
		try {
			stmt = con.prepareStatement(sql);
			logWarnings(con.getWarnings());
			stmt.setString(1, filename);
			logWarnings(stmt.getWarnings());
			log.debug("VolatileAndJiTDAO - infoOnVolatile - {}",
					stmt.toString());
			rs = stmt.executeQuery();
			logWarnings(stmt.getWarnings());
			if (rs.next()) {
				aux.add(new Long(rs.getLong("UNIX_TIMESTAMP(start)")));
				aux.add(new Long(rs.getLong("fileLifetime")));
			} else {
				log.debug("VolatileAndJiTDAO! infoOnVolatile did not find {}",
						filename);
			}
		} catch (SQLException e) {
			log.error("VolatileAndJiTDAO! Error in infoOnVolatile: {}",
					e.getMessage(), e);
		} finally {
			close(rs);
			close(stmt);
			takeDownConnection(con);
		}
		return aux;
	}

	

	/**
	 * Auxiliary method that closes a ResultSet and handles all possible
	 * exceptions.
	 */
	private void close(ResultSet rset) {

		if (rset != null) {
			try {
				rset.close();
			} catch (Exception e) {
				log.error(
						"VolatileAndJiTDAO! Unable to close ResultSet - Error: {}",
						e.getMessage(), e);
			}
		}
	}

	/**
	 * Auxiliary method that closes a Statement and handles all possible
	 * exceptions.
	 */
	private void close(Statement stmt) {

		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				log.error(
						"VolatileAndJiTDAO! Unable to close Statement {} - Error: {}",
						stmt.toString(), e.getMessage(), e);
			}
		}
	}

	/**
	 * Auxiliary method used to log warnings.
	 */
	private void logWarnings(SQLWarning warning) {

		if (warning != null) {
			log.debug("VolatileAndJiTDAO: {}", warning.toString());
			while ((warning = warning.getNextWarning()) != null) {
				log.debug("VolatileAndJiTDAO: {}", warning.toString());
			}
		}
	}

	/**
	 * Method that returns a String containing all Files.
	 */
	private String makeFileString(Collection files) {

		StringBuffer sb = new StringBuffer("(");
		for (Iterator i = files.iterator(); i.hasNext();) {
			sb.append("'");
			sb.append((String) i.next());
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
	private String makeIDString(Collection rowids) {

		StringBuffer sb = new StringBuffer("(");
		for (Iterator i = rowids.iterator(); i.hasNext();) {
			sb.append(i.next());
			if (i.hasNext()) {
				sb.append(",");
			}
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Auxiliary method used to roll back a transaction and handles all possible
	 * exceptions.
	 */
	protected void rollback(Connection con) {

		if (con != null) {
			try {
				con.rollback();
				logWarnings(con.getWarnings());
				log.error("VolatileAndJiTDAO! Roll back successful!");
			} catch (SQLException e3) {
				log.error("VolatileAndJiTDAO! Roll back failed! {}",
						e3.getMessage(), e3);
			}
		}
	}

	
}
