package it.grid.storm.catalogs;

import it.grid.storm.config.Configuration;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TStatusCode;

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
 * DAO class for PtPChunkCatalog. This DAO is specifically designed to connect to a MySQL DB. The raw data found in
 * those tables is pre-treated in order to turn it into the Object Model of StoRM. See Method comments for further info.
 * BEWARE! DAO Adjusts for extra fields in the DB that are not present in the object model.
 * 
 * @author EGRID ICTP
 * @version 2.0
 * @date June 2005
 */
public class PtPChunkDAO {

    private static final Logger log = LoggerFactory.getLogger(PtPChunkDAO.class);
    private final String driver = Configuration.getInstance().getDBDriver();// String with the name of the class for the
                                                                            // DB driver
    private final String url = Configuration.getInstance().getDBURL(); // String referring to the URL of the DB
    private final String password = Configuration.getInstance().getDBPassword(); // String with the password for the DB
    private final String name = Configuration.getInstance().getDBUserName(); // String with the name for the DB
    private Connection con = null; // Connection to DB - WARNING!!! It is kept open all the time!

    private static final PtPChunkDAO dao = new PtPChunkDAO(); // DAO!

    private Timer clock = null; // timer thread that will run a task to alert when reconnecting is necessary!
    private TimerTask clockTask = null; // timer task that will update the boolean signalling that a reconnection is
                                        // neede!
    private long period = Configuration.getInstance().getDBReconnectPeriod() * 1000;// milliseconds that must pass
                                                                                    // before reconnecting to DB
    private long delay = Configuration.getInstance().getDBReconnectDelay() * 1000;// initial delay in millseconds before
                                                                                  // starting timer
    private boolean reconnect = false; // boolean that tells whether reconnection is needed because of MySQL bug!

    private PtPChunkDAO() {
        setUpConnection();
        clock = new Timer();
        clockTask = new TimerTask() {
            @Override
            public void run() {
                reconnect = true;
            }
        }; // clock task
        clock.scheduleAtFixedRate(clockTask, delay, period);
    }

    /**
     * Method that returns the only instance of the PtPChunkDAO.
     */
    public static PtPChunkDAO getInstance() {
        return dao;
    }

    /**
     * Method that queries the MySQL DB to find all entries matching the supplied TRequestToken. The Collection contains
     * the corresponding PtPChunkDataTO objects. An initial simple query establishes the list of protocols associated
     * with the request. A second complex query establishes all chunks associated with the request, by properly joining
     * request_queue, request_Put and status_Put. The considered fields are: (1) From status_Put: the ID field which
     * becomes the TOs primary key, and statusCode. (2) From request_Put: targetSURL and expectedFileSize. (3) From
     * request_queue: pinLifetime, fileLifetime, config_FileStorageTypeID, s_token, config_OverwriteID. In case of any
     * error, a log gets written and an empty collection is returned. No exception is returned. NOTE! Chunks in
     * SRM_ABORTED status are NOT returned! This is imporant because this method is intended to be used by the Feeders
     * to fetch all chunks in the request, and aborted chunks should not be picked up for processing!
     */
    public Collection<PtPChunkDataTO> find(TRequestToken requestToken) {
        checkConnection();
        String strToken = requestToken.toString();
        String str = null;
        PreparedStatement find = null;
        ResultSet rs = null;
        try {
            // get protocols for the request
            // str = "SELECT tp.config_ProtocolsID "+
            // "FROM request_TransferProtocols tp "+
            // "WHERE tp.request_queueID IN "+
            // "(SELECT r.ID FROM request_queue r WHERE r.r_token=?)";

            str = "SELECT tp.config_ProtocolsID "
                    + "FROM request_TransferProtocols tp JOIN request_queue r ON tp.request_queueID=r.ID "
                    + "WHERE r.r_token=?";

            find = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            List<String> protocols = new ArrayList<String>();
            find.setString(1, strToken);
            logWarnings(find.getWarnings());
            log.debug("PtP CHUNK DAO - find method: " + find.toString());
            rs = find.executeQuery();
            logWarnings(find.getWarnings());
            while (rs.next()) {
                protocols.add(rs.getString("tp.config_ProtocolsID"));
            }
            close(rs);
            close(find);

            // get chunks of the request
            str = "SELECT s.ID, r.config_FileStorageTypeID, r.config_OverwriteID, r.pinLifetime, r.fileLifetime, r.s_token, p.targetSURL, p.expectedFileSize, s.statusCode "
                    + "FROM request_queue r JOIN (request_Put p, status_Put s) "
                    + "ON (p.request_queueID=r.ID AND s.request_PutID=p.ID) "
                    + "WHERE r.r_token=? AND s.statusCode<>?";
            find = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            List<PtPChunkDataTO> list = new ArrayList<PtPChunkDataTO>();
            find.setString(1, strToken);
            logWarnings(find.getWarnings());
            find.setInt(2, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_ABORTED));
            logWarnings(find.getWarnings());
            log.debug("PtP CHUNK DAO - find method: " + find.toString());
            rs = find.executeQuery();
            logWarnings(find.getWarnings());
            PtPChunkDataTO aux = null;
            while (rs.next()) {
                aux = new PtPChunkDataTO();
                aux.setPrimaryKey(rs.getLong("s.ID"));
                aux.setFileStorageType(rs.getString("r.config_FileStorageTypeID"));
                aux.setOverwriteOption(rs.getString("r.config_OverwriteID"));
                aux.setPinLifetime(rs.getInt("r.pinLifetime"));
                aux.setFileLifetime(rs.getInt("r.fileLifetime"));
                aux.setSpaceToken(rs.getString("r.s_token"));
                aux.setToSURL(rs.getString("p.targetSURL"));
                aux.setExpectedFileSize(rs.getLong("p.expectedFileSize"));
                aux.setProtocolList(protocols);
                aux.setRequestToken(strToken);
                aux.setStatus(rs.getInt("s.statusCode"));
                list.add(aux);
            }
            close(rs);
            close(find);
            return list;
        } catch (SQLException e) {
            log.error("PTP CHUNK DAO: " + e);
            close(rs);
            close(find);
        }
        return new ArrayList<PtPChunkDataTO>(); // return empty Collection!
    }

    /**
     * Method that returns a Collection of ReducedPtPChunkDataTO associated to the given TRequestToken expressed as
     * String.
     */
    public Collection findReduced(String reqtoken) {
        checkConnection();
        PreparedStatement find = null;
        ResultSet rs = null;
        try {
            // get reduced chunks
            String str = "SELECT r.fileLifetime, r.config_FileStorageTypeID, s.ID, s.statusCode, p.targetSURL "
                    + "FROM request_queue r JOIN (request_Put p, status_Put s) "
                    + "ON (p.request_queueID=r.ID AND s.request_PutID=p.ID) " + "WHERE r.r_token=?";
            find = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            List list = new ArrayList();
            find.setString(1, reqtoken);
            logWarnings(find.getWarnings());
            log.debug("PtP CHUNK DAO! findReduced with request token; " + find.toString());
            rs = find.executeQuery();
            logWarnings(find.getWarnings());
            ReducedPtPChunkDataTO aux = null;
            while (rs.next()) {
                aux = new ReducedPtPChunkDataTO();
                aux.setPrimaryKey(rs.getLong("s.ID"));
                aux.setToSURL(rs.getString("p.targetSURL"));
                aux.setStatus(rs.getInt("s.statusCode"));
                aux.setFileStorageType(rs.getString("r.config_FileStorageTypeID"));
                aux.setFileLifetime(rs.getInt("r.fileLifetime"));
                list.add(aux);
            }
            close(rs);
            close(find);
            return list;
        } catch (SQLException e) {
            log.error("PTP CHUNK DAO: " + e);
            close(rs);
            close(find);
            return new ArrayList(); // return empty Collection!
        }
    }

    /**
     * Method that returns a Collection of ReducedPtPChunkDataTO corresponding to the IDs supplied in the given List of
     * Long. If the List is null or empty, an empty collection is returned and error messagges get logged.
     */
    public Collection<ReducedPtPChunkDataTO> fetchReduced(List<Long> ids) {
        boolean ok = ids != null && !ids.isEmpty();
        if (ok) {
            checkConnection();
            PreparedStatement find = null;
            ResultSet rs = null;
            try {
                // get reduced chunks
                String str = "SELECT r.fileLifetime, r.config_FileStorageTypeID, s.ID, s.statusCode, p.targetSURL "
                        + "FROM request_queue r JOIN (request_Put p, status_Put s) "
                        + "ON (p.request_queueID=r.ID AND s.request_PutID=p.ID) "
                        + "WHERE s.ID IN "
                        + makeWhereString(ids);
                find = con.prepareStatement(str);
                logWarnings(con.getWarnings());
                List<ReducedPtPChunkDataTO> list = new ArrayList<ReducedPtPChunkDataTO>();
                log.debug("PtP CHUNK DAO! fetchReduced; " + find.toString());
                rs = find.executeQuery();
                logWarnings(find.getWarnings());
                ReducedPtPChunkDataTO aux = null;
                while (rs.next()) {
                    aux = new ReducedPtPChunkDataTO();
                    aux.setPrimaryKey(rs.getLong("s.ID"));
                    aux.setToSURL(rs.getString("p.targetSURL"));
                    aux.setStatus(rs.getInt("s.statusCode"));
                    aux.setFileStorageType(rs.getString("r.config_FileStorageTypeID"));
                    aux.setFileLifetime(rs.getInt("r.fileLifetime"));
                    list.add(aux);
                }
                close(rs);
                close(find);
                return list;
            } catch (SQLException e) {
                log.error("PTP CHUNK DAO: " + e);
                close(rs);
                close(find);
                return new ArrayList<ReducedPtPChunkDataTO>(); // return empty Collection!
            }
        } else {
            log.warn("ATTENTION in PtP CHUNK DAO! fetchReduced invoked with null or empty list of IDs!");
            return new ArrayList<ReducedPtPChunkDataTO>();
        }
    }

    /**
     * Method used to save the changes made to a retrieved PtPChunkDataTO, back into the MySQL DB. Only the transferURL,
     * statusCode and explanation, of status_Put table get written to the DB. Likewise for the pinLifetime and
     * fileLifetime of request_queue. In case of any error, an error messagge gets logged but no exception is thrown.
     */
    public void update(PtPChunkDataTO to) {
        checkConnection();
        PreparedStatement updateStatusPut = null;
        try {
            // prepare statement...
            updateStatusPut = con.prepareStatement("UPDATE request_queue r JOIN (status_Put s, request_Put p) ON (r.ID=p.request_queueID AND s.request_PutID=p.ID) SET s.transferURL=?, s.statusCode=?, s.explanation=?, r.pinLifetime=?, r.fileLifetime=?, r.config_FileStorageTypeID=?, r.config_OverwriteID=? WHERE s.ID=?");
            logWarnings(con.getWarnings());
            updateStatusPut.setString(1, to.transferURL());
            logWarnings(updateStatusPut.getWarnings());
            updateStatusPut.setInt(2, to.status());
            logWarnings(updateStatusPut.getWarnings());
            updateStatusPut.setString(3, to.errString());
            logWarnings(updateStatusPut.getWarnings());
            updateStatusPut.setInt(4, to.pinLifetime());
            logWarnings(updateStatusPut.getWarnings());
            updateStatusPut.setInt(5, to.fileLifetime());
            logWarnings(updateStatusPut.getWarnings());
            updateStatusPut.setString(6, to.fileStorageType());
            logWarnings(updateStatusPut.getWarnings());
            updateStatusPut.setString(7, to.overwriteOption());
            logWarnings(updateStatusPut.getWarnings());
            updateStatusPut.setLong(8, to.primaryKey());
            logWarnings(updateStatusPut.getWarnings());
            // run updateStatusPut...
            log.debug("PtP CHUNK DAO - update method: " + updateStatusPut.toString());
            updateStatusPut.executeUpdate();
            logWarnings(updateStatusPut.getWarnings());
        } catch (SQLException e) {
            log.error("PtP CHUNK DAO: Unable to complete update! " + e);
        } finally {
            close(updateStatusPut);
        }
    }

    /**
     * Method used to refresh the PtPChunkDataTO information from the MySQL DB. This method is intended to be used
     * during the srmAbortRequest/File operation. In case of any error, an error messagge gets logged but no exception
     * is thrown; a null PtPChunkDataTO is returned.
     */
    public PtPChunkDataTO refresh(long primary_key) {
        checkConnection();
        String prot = "SELECT tp.config_ProtocolsID " + "FROM request_TransferProtocols tp "
                + "WHERE tp.request_queueID IN "
                + "(SELECT r.ID FROM request_queue r JOIN (request_Put p, status_Put s) "
                + "ON (p.request_queueID=r.ID AND s.request_PutID=p.ID) " + "WHERE s.ID=?)";

        String refresh = "SELECT r.config_FileStorageTypeID, r.config_OverwriteID, r.pinLifetime, r.fileLifetime, r.s_token, r.r_token, p.targetSURL, p.expectedFileSize, s.ID, s.statusCode, s.transferURL "
                + "FROM request_queue r JOIN (request_Put p, status_Put s) "
                + "ON (p.request_queueID=r.ID AND s.request_PutID=p.ID) " + "WHERE s.ID=?";

        PreparedStatement stmt = null;
        ResultSet rs = null;
        PtPChunkDataTO aux = null;

        try {
            // get protocols for the request
            stmt = con.prepareStatement(prot);
            logWarnings(con.getWarnings());
            List protocols = new ArrayList();
            stmt.setLong(1, primary_key);
            logWarnings(stmt.getWarnings());
            log.debug("PtP CHUNK DAO - refresh method: " + stmt.toString());
            rs = stmt.executeQuery();
            logWarnings(stmt.getWarnings());
            while (rs.next()) {
                protocols.add(rs.getString("tp.config_ProtocolsID"));
            }
            close(rs);
            close(stmt);

            // get chunk of the request
            stmt = con.prepareStatement(refresh);
            logWarnings(con.getWarnings());
            stmt.setLong(1, primary_key);
            logWarnings(stmt.getWarnings());
            log.debug("PtP CHUNK DAO - refresh method: " + stmt.toString());
            rs = stmt.executeQuery();
            logWarnings(stmt.getWarnings());
            if (rs.next()) {
                aux = new PtPChunkDataTO();
                aux.setPrimaryKey(rs.getLong("s.ID"));
                aux.setFileStorageType(rs.getString("r.config_FileStorageTypeID"));
                aux.setOverwriteOption(rs.getString("r.config_OverwriteID"));
                aux.setPinLifetime(rs.getInt("r.pinLifetime"));
                aux.setFileLifetime(rs.getInt("r.fileLifetime"));
                aux.setSpaceToken(rs.getString("r.s_token"));
                aux.setRequestToken(rs.getString("r.r_token"));
                aux.setToSURL(rs.getString("p.targetSURL"));
                aux.setExpectedFileSize(rs.getLong("p.expectedFileSize"));
                aux.setProtocolList(protocols);
                aux.setStatus(rs.getInt("s.statusCode"));
                aux.setTransferURL(rs.getString("s.transferURL"));
                if (rs.next()) {
                    log.warn("ATTENTION in PtP CHUNK DAO! Possible DB corruption! refresh method invoked for specific chunk with id "
                            + primary_key + ", but found more than one such chunks!");
                }
            } else {
                log.warn("ATTENTION in PtP CHUNK DAO! Possible DB corruption! refresh method invoked for specific chunk with id "
                        + primary_key + ", but chunk NOT found in persistence!");
            }
            close(rs);
            close(stmt);
        } catch (SQLException e) {
            log.error("PtP CHUNK DAO! Unable to refresh chunk! " + e);
            aux = null;
        } finally {
            close(rs);
            close(stmt);
        }
        return aux;
    }

    /**
     * Method used in extraordinary situations to signal that data retrieved from the DB was malformed and could not be
     * translated into the StoRM object model. This method attempts to change the status of the chunk to SRM_FAILURE and
     * record it in the DB, in the status_Put table. This operation could potentially fail because the source of the
     * malformed problems could be a problematic DB; indeed, initially only log messagges were recorded. Yet it soon
     * became clear that the source of malformed data were actually the clients themselves and/or FE recording in the
     * DB. In these circumstances the client would find its request as being in the SRM_IN_PROGRESS state for ever.
     * Hence the pressing need to inform it of the encountered problems.
     */
    public void signalMalformedPtPChunk(PtPChunkDataTO auxTO) {
        checkConnection();
        String signalSQL = "UPDATE status_Put s " + "SET s.statusCode="
                + StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FAILURE) + ", s.explanation=? "
                + "WHERE s.ID=" + auxTO.primaryKey();
        PreparedStatement signal = null;
        try {
            signal = con.prepareStatement(signalSQL);
            logWarnings(con.getWarnings());
            signal.setString(1, "This chunk of the request is malformed!"); // NB: Prepared statement spares DB-specific
                                                                            // String notation!
            logWarnings(signal.getWarnings());
            log.debug("PtP CHUNK DAO - signalMalformedPtPChunk method: " + signal.toString());
            signal.executeUpdate();
            logWarnings(signal.getWarnings());
        } catch (SQLException e) {
            log.error("PtPChunkDAO! Unable to signal in DB that a chunk of the request was malformed! Request: "
                    + auxTO.toString() + "; Exception: " + e.toString());
        } finally {
            close(signal);
        }
    }

    /**
     * Method that returns the number of Put requests on the given SURL, that are in SRM_SPACE_AVAILABLE state. This
     * method is intended to be used by PtPChunkCatalog in the isSRM_SPACE_AVAILABLE method ivocation. In case of any
     * error, 0 is returned.
     */
    public int numberInSRM_SPACE_AVAILABLE(String surl) {
        checkConnection();
        String str = "SELECT COUNT(s.ID) " + "FROM status_Put s JOIN request_Put r "
                + "ON (s.request_PutID=r.ID) " + "WHERE RIGHT(r.targetSURL,?)=? AND s.statusCode=?";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            stmt.setInt(1, surl.length());
            stmt.setString(2, surl); // Prepared statement spares DB-specific String notation!
            logWarnings(stmt.getWarnings());
            stmt.setInt(3, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_SPACE_AVAILABLE));
            log.debug("PtP CHUNK DAO - numberInSRM_SPACE_AVAILABLE method: " + stmt.toString());
            rs = stmt.executeQuery();
            logWarnings(stmt.getWarnings());
            int aux = 0;
            if (rs.next()) {
                aux = rs.getInt(1);
            }
            close(rs);
            close(stmt);
            return aux;
        } catch (SQLException e) {
            log.error("PtPChunkDAO! Unable to determine numberInSRM_SPACE_AVAILABLE! Returning 0! " + e);
            close(rs);
            close(stmt);
            return 0;
        }
    }

    /**
     * Method that updates all expired requests in SRM_SPACE_AVAILABLE state, into SRM_FILE_LIFTIME_EXPIRED. It returns
     * a List containing the ID of the requests that were transited. This is needed to delete the corresponding physical
     * files. This is needed when the client forgets to invoke srmPutDone().
     */
    public List<Long> transitExpiredSRM_SPACE_AVAILABLE() {
        checkConnection();
        String idsstr = "SELECT s.ID FROM "
                + "status_Put s JOIN (request_Put rp, request_queue r) ON s.request_PutID=rp.ID AND rp.request_queueID=r.ID "
                + "WHERE s.statusCode=? AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(r.timeStamp) >= r.pinLifetime ";

        // String nonvolidsstr = "SELECT s.ID FROM "+
        // "status_Put s JOIN (request_Put rp, request_queue r) ON s.request_PutID=rp.ID AND rp.request_queueID=r.ID "+
        // "WHERE s.statusCode=? AND r.config_FileStorageTypeID<>? AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(r.timeStamp) >= r.pinLifetime ";

        String updateStatus = "UPDATE " + "status_Put s " + "SET s.statusCode=? " + "WHERE s.ID IN ";

        List<Long> ids = new ArrayList<Long>();
        // List nonvolids = new ArrayList();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {

            // get ids
            stmt = con.prepareStatement(idsstr);
            logWarnings(con.getWarnings());
            stmt.setInt(1, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_SPACE_AVAILABLE));
            logWarnings(stmt.getWarnings());
            // stmt.setString(2,FileStorageTypeConverter.getInstance().toDB(TFileStorageType.VOLATILE));
            // logWarnings(stmt.getWarnings());
            log.debug("PtP CHUNK DAO - transitExpiredSRM_SPACE_AVAILABLE: " + stmt.toString());
            rs = stmt.executeQuery();
            logWarnings(stmt.getWarnings());
            while (rs.next()) {
                ids.add(new Long(rs.getLong("s.ID")));
            }
            close(rs);
            close(stmt);

            // get non volatile ids
            // stmt = con.prepareStatement(nonvolidsstr);
            // logWarnings(con.getWarnings());
            // stmt.setInt(1,StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_SPACE_AVAILABLE));
            // logWarnings(stmt.getWarnings());
            // stmt.setString(2,FileStorageTypeConverter.getInstance().toDB(TFileStorageType.VOLATILE));
            // logWarnings(stmt.getWarnings());
            // log.debug("PtP CHUNK DAO - transitExpiredSRM_SPACE_AVAILABLE: "+stmt.toString());
            // rs = stmt.executeQuery();
            // logWarnings(stmt.getWarnings());
            // while (rs.next()) {
            // nonvolids.add(new Long(rs.getLong("s.ID")));
            // }
            // close(rs);
            // close(stmt);

            // start transaction
            // con.setAutoCommit(false);

            // update volatile ids
            if (!ids.isEmpty()) {
                String updateStatusNew = updateStatus + makeWhereString(ids);
                stmt = con.prepareStatement(updateStatusNew);
                logWarnings(con.getWarnings());
                stmt.setInt(1, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_LIFETIME_EXPIRED));
                logWarnings(stmt.getWarnings());
                log.debug("PtP CHUNK DAO - transitExpiredSRM_SPACE_AVAILABLE: " + stmt.toString());
                int count = stmt.executeUpdate();
                logWarnings(stmt.getWarnings());
                close(stmt);
                log.info("PtPChunkDAO! "
                        + count
                        + " chunks of PtP requests were transited from SRM_SPACE_AVAILABLE to SRM_FILE_LIFETIME_EXPIRED.");
            } else {
                log.debug("PtPChunkDAO! No chunk of PtP request was transited from SRM_SPACE_AVAILABLE to SRM_FILE_LIFETIME_EXPIRED.");
            }

            // update non volatile ids
            // if (!nonvolids.isEmpty()) {
            // String updateStatusNonVol = updateStatus + makeWhereString(nonvolids);
            // stmt = con.prepareStatement(updateStatusNonVol);
            // logWarnings(con.getWarnings());
            // stmt.setInt(1,StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_SUCCESS));
            // logWarnings(stmt.getWarnings());
            // log.debug("PtP CHUNK DAO - transitExpiredSRM_SPACE_AVAILABLE: "+stmt.toString());
            // int count = stmt.executeUpdate();
            // logWarnings(stmt.getWarnings());
            // close(stmt);
            // log.info("PtPChunkDAO! "+count+" non-volatile chunks of PtP requests were transited from SRM_SPACE_AVAILABLE to SRM_SUCCESS.");
            // } else {
            // log.debug("PtPChunkDAO! No non-volatile chunk of PtP request was transited from SRM_SPACE_AVAILABLE to SRM_SUCCESS.");
            // }

            // end transaction
            // con.commit();
            // logWarnings(con.getWarnings());
            // con.setAutoCommit(true);
            // logWarnings(con.getWarnings());

        } catch (SQLException e) {
            log.error("PtPChunkDAO! Unable to transit expired SRM_SPACE_AVAILABLE chunks of PtP requests, to SRM_FILE_LIFETIME_EXPIRED! "
                    + e);
            // rollback(con);
            ids = new ArrayList<Long>(); // make an empty list!
        } finally {
            close(rs);
            close(stmt);
        }
        return ids;
    }

    /**
     * Private method that returns a String of all IDs retrieved by the last SELECT.
     */
    private String makeWhereString(List<Long> rowids) {
        StringBuffer sb = new StringBuffer("(");
        for (Iterator<Long> i = rowids.iterator(); i.hasNext();) {
            sb.append(i.next());
            if (i.hasNext()) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Method that updates chunks in SRM_SPACE_AVAILABLE state, into SRM_SUCCESS. An array of long representing the
     * primary key of each chunk is required. This is needed when the client invokes srmPutDone() In case of any error
     * nothing happens and no exception is thrown, but proper messagges get logged.
     */
    public void transitSRM_SPACE_AVAILABLEtoSRM_SUCCESS(long[] ids) {
        checkConnection();

        String str = "UPDATE "
                + "status_Put s JOIN (request_Put rp, request_queue r) ON s.request_PutID=rp.ID AND rp.request_queueID=r.ID "
                + "SET s.statusCode=? " + "WHERE s.statusCode=? AND s.ID IN " + makeWhereString(ids);

        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(str);
            logWarnings(con.getWarnings());

            stmt.setInt(1, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_SUCCESS));
            logWarnings(stmt.getWarnings());

            stmt.setInt(2, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_SPACE_AVAILABLE));
            logWarnings(stmt.getWarnings());

            log.debug("PtP CHUNK DAO - transitSRM_SPACE_AVAILABLEtoSRM_SUCCESS: " + stmt.toString());

            int count = stmt.executeUpdate();
            logWarnings(stmt.getWarnings());

            if (count == 0) {
                log.debug("PtPChunkDAO! No chunk of PtP request was transited from SRM_SPACE_AVAILABLE to SRM_SUCCESS.");
            } else {
                log.info("PtPChunkDAO! " + count
                        + " chunks of PtP requests were transited from SRM_SPACE_AVAILABLE to SRM_SUCCESS.");
            }
        } catch (SQLException e) {
            log.error("PtPChunkDAO! Unable to transit chunks from SRM_SPACE_AVAILABLE to SRM_SUCCESS! " + e);
        } finally {
            close(stmt);
        }
    }

    /**
     * Method that returns a String containing all IDs.
     */
    private String makeWhereString(long[] rowids) {
        StringBuffer sb = new StringBuffer("(");
        int n = rowids.length;
        for (int i = 0; i < n; i++) {
            sb.append(rowids[i]);
            if (i < (n - 1)) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Method that transits chunks in SRM_SPACE_AVAILABLE to SRM_ABORTED, for the given SURL: the overall request status
     * of the requests containing that chunk, is not changed! The TURL is set to null. Beware, that the chunks may be
     * part of requests that have finished, or that still have not finished because other chunks are still being
     * processed.
     */
    public void transitSRM_SPACE_AVAILABLEtoSRM_ABORTED(String surl, String explanation) {
        checkConnection();
        String str = "UPDATE "
                + "status_Put s JOIN (request_Put rp, request_queue r) ON s.request_PutID=rp.ID AND rp.request_queueID=r.ID "
                + "SET s.statusCode=?, s.explanation=?, s.transferURL=NULL "
                + "WHERE s.statusCode=? AND rp.targetSURL=?";
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            stmt.setInt(1, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_ABORTED));
            logWarnings(stmt.getWarnings());
            stmt.setString(2, explanation);
            logWarnings(stmt.getWarnings());
            stmt.setInt(3, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_SPACE_AVAILABLE));
            logWarnings(stmt.getWarnings());
            stmt.setString(4, surl);
            logWarnings(stmt.getWarnings());
            log.debug("PtP CHUNK DAO - transitSRM_SPACE_AVAILABLEtoSRM_ABORTED: " + stmt.toString());
            int count = stmt.executeUpdate();
            logWarnings(stmt.getWarnings());
            log.debug("PtP CHUNK DAO! " + count
                    + " chunks were transited from SRM_SPACE_AVAILABLE to SRM_ABORTED.");
        } catch (SQLException e) {
            log.error("PtP CHUNK DAO! Unable to transitSRM_SPACE_AVAILABLEtoSRM_ABORTED! " + e);
        } finally {
            close(stmt);
        }
    }

    /**
     * Auxiliary method used to roll back a failed transaction
     */
    private void rollback(Connection con) {
        if (con != null) {
            try {
                con.rollback();
                log.error("PTP CHUNK DAO: roll back successful!");
            } catch (SQLException e) {
                log.error("PTP CHUNK DAO: roll back failed! " + e);
            }
        }
    }

    /**
     * Auxiliary method used to close a Statement
     */
    private void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception e) {
                log.error("PTP CHUNK DAO! Unable to close Statement " + stmt.toString() + " - Exception: "
                        + e);
            }
        }
    }

    /**
     * Auxiliary method used to close a ResultSet
     */
    private void close(ResultSet rset) {
        if (rset != null) {
            try {
                rset.close();
            } catch (Exception e) {
                log.error("PTP CHUNK DAO! Unable to close ResultSet! Exception: " + e);
            }
        }
    }

    /**
     * Auxiliary private method that logs all SQL warnings.
     */
    private void logWarnings(SQLWarning w) {
        if (w != null) {
            log.debug("PTP CHUNK DAO: " + w.toString());
            while ((w = w.getNextWarning()) != null) {
                log.debug("PTP CHUNK DAO: " + w.toString());
            }
        }
    }

    /**
     * Auxiliary method that sets up the conenction to the DB.
     */
    private void setUpConnection() {
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, name, password);
            if (con == null) {
                log.error("PTP CHUNK DAO! DriverManager returned a null connection!");
            } else {
                logWarnings(con.getWarnings());
            }
        } catch (ClassNotFoundException e) {
            log.error("PTP CHUNK DAO! Exception in setUpConenction! " + e);
        } catch (SQLException e) {
            log.error("PTP CHUNK DAO! Exception in setUpConnection! " + e);
        }
    }

    /**
     * Auxiliary method that takes down a conenctin to the DB.
     */
    private void takeDownConnection() {
        try {
            con.close();
        } catch (SQLException e) {
            log.error("PTP CHUNK DAO! Exception in takeDownConnection method - could not close connection! "
                    + e);
        }
    }

    /**
     * Auxiliary method that checks if time for resetting the connection has come, and eventually takes it down and up
     * back again.
     */
    private void checkConnection() {
        if (reconnect) {
            log.debug("PTP CHUNK DAO! Reconnecting to DB! ");
            takeDownConnection();
            setUpConnection();
            reconnect = false;
        }
    }
}
