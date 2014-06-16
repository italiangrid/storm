package it.grid.storm.catalogs.surl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SURLStatusDAO {

	public static final Logger log = LoggerFactory.getLogger(SURLStatusDAO.class);

	public SURLStatusDAO() {

	}

	public Connection getConnection() {

		return null;
	}

	private void surlSanityChecks(String surl) {

		if (surl == null)
			throw new IllegalArgumentException("surl must be non-null.");

		if (surl.isEmpty())
			throw new IllegalArgumentException("surl must be non-empty.");

	}

	private void closeStatetement(Statement stat) {

		if (stat != null) {
			try {
				stat.close();
			} catch (SQLException e) {
				log.error("Error closing statement: {}.", e.getMessage(), e);
			}
		}
	}

	private void closeResultSet(ResultSet rs) {

		if (rs != null) {

			try {
				rs.close();
			} catch (SQLException e) {
				log.error("Error closing result set: {}", e.getMessage(), e);
			}
		}
	}

	public boolean surlHasOngoingPtGs(String surl) {
		surlSanityChecks(surl);
		
		ResultSet rs = null;
		PreparedStatement stat = null;
		Connection con = getConnection();
		
		try{
			
			// We basically check whether there are active requests
			// that have the SURL in SRM_FILE_PINNED status
			String query = "SELECT rq.ID, rg.ID, sg.statusCode"
				+"FROM request_queue rq JOIN (request_Get rg, status_Get sg) " 
				+"ON (rg.request_queueID = rq.ID AND sg.ID = rg.ID)"
				+"WHERE ( rg.sourceSURL = ? and sg.statusCode = 22)";
			
			stat = con.prepareStatement(query);
			stat.setString(1, surl);
			
			rs = stat.executeQuery();
			return rs.next();
		} catch(SQLException e){
			log.error("surlHasOngoingPtGs: SQL error {}", e.getMessage(), e);
			throw new RuntimeException("surlHasOngoingPtGs: SQL error",e);
		} finally{
			closeStatetement(stat);
			closeResultSet(rs);
		}
	}
	
	public boolean surlHasOngoingPtPs(String surl, String ptpRequestToken) {

		surlSanityChecks(surl);

		ResultSet rs = null;
		PreparedStatement stat = null;
		Connection con = getConnection();

		try {

			// We basically check whether there are active requests
			// that have the SURL in SRM_SPACE_AVAILABLE status
			String query = "SELECT rq.ID, rp.ID, sp.statusCode "
				+ "FROM request_queue rq JOIN (request_Put rp, status_Put sp) "
				+ "ON (rp.request_queueID=rq.ID AND sp.request_PutID=rp.ID) "
				+ "WHERE ( rp.targetSURL = ? and sp.statusCode=24 )";

			if (ptpRequestToken != null) {
				query += " AND rq.r_token != ?";
			}

			stat = con.prepareStatement(query);
			stat.setString(1, surl);

			if (ptpRequestToken != null) {
				stat.setString(2, ptpRequestToken);
			}

			rs = stat.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			log.error("surlHasOngoingPtPs: SQL error {}", e.getMessage(), e);
			throw new RuntimeException("surlHasOngoingPtPs: SQL error", e);
		} finally {
			closeStatetement(stat);
			closeResultSet(rs);
		}

	}
}
