package it.grid.storm.catalogs;

import it.grid.storm.config.Configuration;
import it.grid.storm.ea.StormEA;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for BoLChunkCatalog. This DAO is specifically designed to connect to a MySQL DB. The raw data
 * found in those tables is pre-treated in order to turn it into the Object Model of StoRM. See Method
 * comments for further info.
 * 
 * BEWARE! DAO Adjusts for extra fields in the DB that are not present in the object model.
 * 
 * @author CNAF
 * @version 1.0
 * @date Aug 2009
 */
public class BoLChunkDAO {

    private static final Logger log = LoggerFactory.getLogger(BoLChunkDAO.class);

    /** String with the name of the class for the DB driver */
    private final String driver = Configuration.getInstance().getDBDriver();
    /** String referring to the URL of the DB */
    private final String url = Configuration.getInstance().getDBURL();
    /** String with the password for the DB */
    private final String password = Configuration.getInstance().getDBPassword();
    /** String with the name for the DB */
    private final String name = Configuration.getInstance().getDBUserName();
    /** Connection to DB - WARNING!!! It is kept open all the time! */
    private Connection con = null;
    private final static BoLChunkDAO dao = new BoLChunkDAO();

    /** timer thread that will run a taask to alert when reconnecting is necessary! */
    private Timer clock = null;
    /** timer task that will update the boolean signalling that a reconnection is neede! */
    private TimerTask clockTask = null;
    /** milliseconds that must pass before reconnecting to DB */
    private long period = Configuration.getInstance().getDBReconnectPeriod() * 1000;
    /** initial delay in millseconds before starting timer */
    private long delay = Configuration.getInstance().getDBReconnectDelay() * 1000;
    /** boolean that tells whether reconnection is needed because of MySQL bug! */
    private boolean reconnect = false;

    private BoLChunkDAO() {

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
     * Method that returns the only instance of the BoLChunkDAO.
     */
    public static BoLChunkDAO getInstance() {
        return dao;
    }

    /**
     * Method used to add a new record to the DB: the supplied BoLChunkDataTO gets its primaryKey changed to
     * the one assigned by the DB.
     * 
     * The supplied BoLChunkData is used to fill in only the DB table where file specific info gets recorded:
     * it does _not_ add a new request! So if spurious data is supplied, it will just stay there because of a
     * lack of a parent request!
     */
    public synchronized void addChild(BoLChunkDataTO to) {
        checkConnection();
        String str = null;
        PreparedStatement id = null; // statement to find out the ID associated to the request token
        PreparedStatement addDirOption = null; // statement to add the TDirOption
        PreparedStatement addBoL = null; // statement to add the request_BoL info
        PreparedStatement addChild = null; // statement to the status_BoL info
        ResultSet rsid = null; // result set containing the ID of the request.
        ResultSet rsdo = null; // result set containing the generated ID of the TDirOption insertion
        ResultSet rsg = null; // result set containing the generated ID of the request_BoL insertion
        ResultSet rs = null; // result set containing the ID generated after the status_BoL
        // insertion
        try {

            // WARNING!!!! We are forced to run a query to get the ID of the request, which should
            // NOT be so
            // because the corresponding request object should have been changed with the extra
            // field! However, it is not possible
            // at the moment to perform such change because of strict deadline and the change could
            // wreak havoc
            // the code. So we are forced to make this query!!!

            // begin transaction
            con.setAutoCommit(false);
            logWarnings(con.getWarnings());

            // find ID of request corresponding to given RequestToken
            str = "SELECT r.ID FROM request_queue r WHERE r.r_token=?";
            id = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            id.setString(1, to.getRequestToken());
            logWarnings(id.getWarnings());
            log.debug("BoL CHUNK DAO: addChild; " + id.toString());
            rsid = id.executeQuery();
            logWarnings(id.getWarnings());
            int request_id = extractID(rsid); // ID of request in request_process!

            // fill in TDirOption
            str = "INSERT INTO request_DirOption (isSourceADirectory,allLevelRecursive,numOfLevels) VALUES (?,?,?)";
            addDirOption = con.prepareStatement(str, Statement.RETURN_GENERATED_KEYS);
            logWarnings(con.getWarnings());
            addDirOption.setBoolean(1, to.getDirOption());
            logWarnings(addDirOption.getWarnings());
            addDirOption.setBoolean(2, to.getAllLevelRecursive());
            logWarnings(addDirOption.getWarnings());
            addDirOption.setInt(3, to.getNumLevel());
            logWarnings(addDirOption.getWarnings());
            log.debug("BoL CHUNK DAO: addChild; " + addDirOption.toString());
            addDirOption.execute();
            logWarnings(addDirOption.getWarnings());
            rsdo = addDirOption.getGeneratedKeys();
            int do_id = extractID(rsdo);

            // fill in request_BoL... sourceSURL and TDirOption!
            str = "INSERT INTO request_BoL (request_DirOptionID,request_queueID,sourceSURL) VALUES (?,?,?)";
            addBoL = con.prepareStatement(str, Statement.RETURN_GENERATED_KEYS);
            logWarnings(con.getWarnings());
            addBoL.setInt(1, do_id);
            logWarnings(addBoL.getWarnings());
            addBoL.setInt(2, request_id);
            logWarnings(addBoL.getWarnings());
            addBoL.setString(3, to.getFromSURL());
            logWarnings(addBoL.getWarnings());
            log.debug("BoL CHUNK DAO: addChild; " + addBoL.toString());
            addBoL.execute();
            logWarnings(addBoL.getWarnings());
            rsg = addBoL.getGeneratedKeys();
            int g_id = extractID(rsg);

            // fill in status_BoL...
            str = "INSERT INTO status_BoL (request_BoLID,statusCode,explanation) VALUES (?,?,?)";
            addChild = con.prepareStatement(str, Statement.RETURN_GENERATED_KEYS);
            logWarnings(con.getWarnings());
            addChild.setInt(1, g_id);
            logWarnings(addChild.getWarnings());
            addChild.setInt(2, to.getStatus());
            logWarnings(addChild.getWarnings());
            addChild.setString(3, to.getErrString());
            logWarnings(addChild.getWarnings());
            log.debug("BoL CHUNK DAO: addChild; " + addChild.toString());
            addChild.execute();
            logWarnings(addChild.getWarnings());
            rs = addChild.getGeneratedKeys();
            int s_id = extractID(rs);

            // end transaction!
            con.commit();
            logWarnings(con.getWarnings());
            con.setAutoCommit(true);
            logWarnings(con.getWarnings());

            // update primary key reading the generated key
            to.setPrimaryKey(s_id);
        } catch (SQLException e) {
            log.error("BoL CHUNK DAO: unable to complete addChild! BoLChunkDataTO: " + to
                    + "; exception received:" + e);
            rollback(con);
        } catch (Exception e) {
            log.error("BoL CHUNK DAO: unable to complete addChild! BoLChunkDataTO: " + to
                    + "; exception received:" + e);
            rollback(con);
        } finally {
            close(rsid);
            close(id);
            close(rsdo);
            close(addDirOption);
            close(rsg);
            close(addBoL);
            close(rs);
            close(addChild);
        }
    }

    /**
     * Method used to add a new record to the DB: the supplied BoLChunkDataTO gets its primaryKey changed to
     * the one assigned by the DB. The client_dn must also be supplied as a String.
     * 
     * The supplied BoLChunkData is used to fill in all the DB tables where file specific info gets recorded:
     * it _adds_ a new request!
     */
    public synchronized void addNew(BoLChunkDataTO to, String client_dn) {
        checkConnection();
        String str = null;
        ResultSet rs_new = null; // result set containing the ID of the inserted new request
        ResultSet rs_do = null; // result set containing the ID of the inserted TDirOption
        ResultSet rs_g = null; // result set containing the ID of the inserted request_BoL
        ResultSet rs_s = null; // result set containing the ID of the inserted request_Status
        PreparedStatement addNew = null; // insert new request into process_request
        PreparedStatement addProtocols = null; // insert protocols for request.
        PreparedStatement addDirOption = null; // insert TDirOption for request
        PreparedStatement addBoL = null; // insert request_BoL for request
        PreparedStatement addChild = null;
        try {
            // begin transaction
            con.setAutoCommit(false);
            logWarnings(con.getWarnings());

            // add to request_queue...
            str = "INSERT INTO request_queue (config_RequestTypeID,client_dn,pinLifetime,status,errstring,r_token,nbreqfiles,timeStamp,deferredStartTime) VALUES (?,?,?,?,?,?,?,?,?)";
            addNew = con.prepareStatement(str, Statement.RETURN_GENERATED_KEYS);
            logWarnings(con.getWarnings());
            addNew.setString(1, RequestTypeConverter.getInstance().toDB(TRequestType.BRING_ON_LINE)); // request
            // type
            // set
            // to
            // prepare
            // to
            // get!
            logWarnings(addNew.getWarnings());
            addNew.setString(2, client_dn);
            logWarnings(addNew.getWarnings());
            addNew.setInt(3, to.getLifeTime());
            logWarnings(addNew.getWarnings());
            addNew.setInt(4, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_REQUEST_INPROGRESS));
            logWarnings(addNew.getWarnings());
            addNew.setString(5, "New BoL Request resulting from srmCopy invocation.");
            logWarnings(addNew.getWarnings());
            addNew.setString(6, to.getRequestToken());
            logWarnings(addNew.getWarnings());
            addNew.setInt(7, 1); // number of requested files set to 1!
            logWarnings(addNew.getWarnings());
            addNew.setTimestamp(8, new Timestamp(new Date().getTime()));
            logWarnings(addNew.getWarnings());
            addNew.setInt(9, to.getDeferredStartTime());
            logWarnings(addNew.getWarnings());
            log.debug("BoL CHUNK DAO: addNew; " + addNew.toString());
            addNew.execute();
            logWarnings(addNew.getWarnings());
            rs_new = addNew.getGeneratedKeys();
            int id_new = extractID(rs_new);

            // add protocols...
            str = "INSERT INTO request_TransferProtocols (request_queueID,config_ProtocolsID) VALUES (?,?)";
            addProtocols = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            for (Iterator<String> i = to.getProtocolList().iterator(); i.hasNext();) {
                addProtocols.setInt(1, id_new);
                logWarnings(addProtocols.getWarnings());
                addProtocols.setString(2, (String) i.next());
                logWarnings(addProtocols.getWarnings());
                log.debug("BoL CHUNK DAO: addNew; " + addProtocols.toString());
                addProtocols.execute();
                logWarnings(addProtocols.getWarnings());
            }

            // addChild...

            // first fill in TDirOption
            str = "INSERT INTO request_DirOption (isSourceADirectory,allLevelRecursive,numOfLevels) VALUES (?,?,?)";
            addDirOption = con.prepareStatement(str, Statement.RETURN_GENERATED_KEYS);
            logWarnings(con.getWarnings());
            addDirOption.setBoolean(1, to.getDirOption());
            logWarnings(addDirOption.getWarnings());
            addDirOption.setBoolean(2, to.getAllLevelRecursive());
            logWarnings(addDirOption.getWarnings());
            addDirOption.setInt(3, to.getNumLevel());
            logWarnings(addDirOption.getWarnings());
            log.debug("BoL CHUNK DAO: addNew; " + addDirOption.toString());
            addDirOption.execute();
            logWarnings(addDirOption.getWarnings());
            rs_do = addDirOption.getGeneratedKeys();
            int id_do = extractID(rs_do);

            // second fill in request_BoL... sourceSURL and TDirOption!
            str = "INSERT INTO request_BoL (request_DirOptionID,request_queueID,sourceSURL) VALUES (?,?,?)";
            addBoL = con.prepareStatement(str, Statement.RETURN_GENERATED_KEYS);
            logWarnings(con.getWarnings());
            addBoL.setInt(1, id_do);
            logWarnings(addBoL.getWarnings());
            addBoL.setInt(2, id_new);
            logWarnings(addBoL.getWarnings());
            addBoL.setString(3, to.getFromSURL());
            logWarnings(addBoL.getWarnings());
            log.debug("BoL CHUNK DAO: addNew; " + addBoL.toString());
            addBoL.execute();
            logWarnings(addBoL.getWarnings());
            rs_g = addBoL.getGeneratedKeys();
            int id_g = extractID(rs_g);

            // third fill in status_BoL...
            str = "INSERT INTO status_BoL (request_BoLID,statusCode,explanation) VALUES (?,?,?)";
            addChild = con.prepareStatement(str, Statement.RETURN_GENERATED_KEYS);
            logWarnings(con.getWarnings());
            addChild.setInt(1, id_g);
            logWarnings(addChild.getWarnings());
            addChild.setInt(2, to.getStatus());
            logWarnings(addChild.getWarnings());
            addChild.setString(3, to.getErrString());
            logWarnings(addChild.getWarnings());
            log.debug("BoL CHUNK DAO: addNew; " + addChild.toString());
            addChild.execute();
            logWarnings(addChild.getWarnings());
            rs_s = addChild.getGeneratedKeys();
            int id_s = extractID(rs_s);

            // end transaction!
            con.commit();
            logWarnings(con.getWarnings());
            con.setAutoCommit(true);
            logWarnings(con.getWarnings());

            // update primary key reading the generated key
            to.setPrimaryKey(id_s);
        } catch (SQLException e) {
            log.error("BoL CHUNK DAO: Rolling back! Unable to complete addNew! BoLChunkDataTO: " + to
                    + "; exception received:" + e);
            rollback(con);
        } catch (Exception e) {
            log.error("BoL CHUNK DAO: unable to complete addNew! BoLChunkDataTO: " + to
                    + "; exception received:" + e);
            rollback(con);
        } finally {
            close(rs_new);
            close(rs_do);
            close(rs_g);
            close(rs_s);
            close(addNew);
            close(addProtocols);
            close(addDirOption);
            close(addBoL);
            close(addChild);
        }
    }

    /**
     * Method that queries the MySQL DB to find all entries matching the supplied TRequestToken. The
     * Collection contains the corresponding BoLChunkDataTO objects.
     * 
     * An initial simple query establishes the list of protocols associated with the request. A second complex
     * query establishes all chunks associated with the request, by properly joining request_queue,
     * request_BoL, status_BoL and request_DirOption. The considered fields are:
     * 
     * (1) From status_BoL: the ID field which becomes the TOs primary key, and statusCode.
     * 
     * (2) From request_BoL: sourceSURL
     * 
     * (3) From request_queue: pinLifetime
     * 
     * (4) From request_DirOption: isSourceADirectory, alLevelRecursive, numOfLevels
     * 
     * In case of any error, a log gets written and an empty collection is returned. No exception is thrown.
     * 
     * NOTE! Chunks in SRM_ABORTED status are NOT returned!
     */
    public synchronized Collection<BoLChunkDataTO> find(TRequestToken requestToken) {
        checkConnection();
        String strToken = requestToken.toString();
        String str = null;
        PreparedStatement find = null;
        ResultSet rs = null;
        try {

            str = "SELECT tp.config_ProtocolsID "
                    + "FROM request_TransferProtocols tp JOIN request_queue r ON tp.request_queueID=r.ID "
                    + "WHERE r.r_token=?";

            find = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            List<String> protocols = new ArrayList<String>();
            find.setString(1, strToken);
            logWarnings(find.getWarnings());
            log.debug("BoL CHUNK DAO: find method; " + find.toString());
            rs = find.executeQuery();
            logWarnings(find.getWarnings());
            while (rs.next()) {
                protocols.add(rs.getString("tp.config_ProtocolsID"));
            }
            close(rs);
            close(find);

            // get chunks of the request
            str = "SELECT s.ID, s.statusCode, r.pinLifetime, g.sourceSURL, d.isSourceADirectory, d.allLevelRecursive, d.numOfLevels, r.deferredStartTime "
                    + "FROM request_queue r JOIN (request_BoL g, status_BoL s) "
                    + "ON (g.request_queueID=r.ID AND s.request_BoLID=g.ID) "
                    + "LEFT JOIN request_DirOption d ON g.request_DirOptionID=d.ID "
                    + "WHERE r.r_token=? AND s.statusCode<>?";
            find = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            List<BoLChunkDataTO> list = new ArrayList<BoLChunkDataTO>();

            find.setString(1, strToken);
            logWarnings(find.getWarnings());
            find.setInt(2, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_ABORTED));
            logWarnings(find.getWarnings());

            log.debug("BoL CHUNK DAO: find method; " + find.toString());
            rs = find.executeQuery();
            logWarnings(find.getWarnings());
            while (rs.next()) {
                BoLChunkDataTO aux = new BoLChunkDataTO();
                aux.setPrimaryKey(rs.getLong("s.ID"));
                aux.setStatus(rs.getInt("s.statusCode"));
                aux.setRequestToken(strToken);
                aux.setFromSURL(rs.getString("g.sourceSURL"));
                aux.setLifeTime(rs.getInt("r.pinLifetime"));
                aux.setDirOption(rs.getBoolean("d.isSourceADirectory"));
                aux.setAllLevelRecursive(rs.getBoolean("d.allLevelRecursive"));
                aux.setNumLevel(rs.getInt("d.numOfLevels"));
                aux.setDeferredStartTime(rs.getInt("r.deferredStartTime"));
                aux.setProtocolList(protocols);
                list.add(aux);
            }
            close(rs);
            close(find);
            return list;
        } catch (SQLException e) {
            log.error("BoL CHUNK DAO: " + e);
            close(rs);
            close(find);
            return new ArrayList<BoLChunkDataTO>(); // return empty Collection!
        }
    }

    /**
     * Method that returns a Collection of ReducedBoLChunkDataTO associated to the given TRequestToken
     * expressed as String.
     */
    public synchronized Collection<ReducedBoLChunkDataTO> findReduced(String reqtoken) {
        checkConnection();
        PreparedStatement find = null;
        ResultSet rs = null;
        try {
            // get reduced chunks
            String str = "SELECT s.ID, s.statusCode, g.sourceSURL "
                    + "FROM request_queue r JOIN (request_BoL g, status_BoL s) "
                    + "ON (g.request_queueID=r.ID AND s.request_BoLID=g.ID) " + "WHERE r.r_token=?";
            find = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            List<ReducedBoLChunkDataTO> list = new ArrayList<ReducedBoLChunkDataTO>();
            find.setString(1, reqtoken);
            logWarnings(find.getWarnings());
            log.debug("BoL CHUNK DAO! findReduced with request token; " + find.toString());
            rs = find.executeQuery();
            logWarnings(find.getWarnings());
            while (rs.next()) {
                ReducedBoLChunkDataTO aux = new ReducedBoLChunkDataTO();
                aux.setPrimaryKey(rs.getLong("s.ID"));
                aux.setFromSURL(rs.getString("g.sourceSURL"));
                aux.setStatus(rs.getInt("s.statusCode"));
                list.add(aux);
            }
            close(rs);
            close(find);
            return list;
        } catch (SQLException e) {
            log.error("BoL CHUNK DAO: " + e);
            close(rs);
            close(find);
            return new ArrayList<ReducedBoLChunkDataTO>(); // return empty Collection!
        }
    }

    /**
     * Method that returns a Collection of ReducedBoLChunkDataTO associated to the given griduser, and whose
     * SURLs are contained in the supplied array of Strings.
     */
    public synchronized Collection<ReducedBoLChunkDataTO> findReduced(String griduser, String[] surls) {
        checkConnection();
        PreparedStatement find = null;
        ResultSet rs = null;
        try {
            // get reduced chunks
            String str = "SELECT s.ID, s.statusCode, g.sourceSURL "
                    + "FROM request_queue r JOIN (request_BoL g, status_BoL s) "
                    + "ON (g.request_queueID=r.ID AND s.request_BoLID=g.ID) "
                    + "WHERE r.client_dn=? AND g.sourceSURL IN " + makeSurlString(surls);
            find = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            List<ReducedBoLChunkDataTO> list = new ArrayList<ReducedBoLChunkDataTO>();
            find.setString(1, griduser);
            logWarnings(find.getWarnings());
            log.debug("BoL CHUNK DAO! findReduced with griduser+surlarray; " + find.toString());
            rs = find.executeQuery();
            logWarnings(find.getWarnings());
            while (rs.next()) {
                ReducedBoLChunkDataTO aux = new ReducedBoLChunkDataTO();
                aux.setPrimaryKey(rs.getLong("s.ID"));
                aux.setFromSURL(rs.getString("g.sourceSURL"));
                aux.setStatus(rs.getInt("s.statusCode"));
                list.add(aux);
            }
            close(rs);
            close(find);
            return list;
        } catch (SQLException e) {
            log.error("BoL CHUNK DAO: " + e);
            close(rs);
            close(find);
            return new ArrayList<ReducedBoLChunkDataTO>(); // return empty Collection!
        }
    }

    /**
     * Method that returns the number of BoL requests on the given SURL, that are in SRM_FILE_PINNED state.
     * 
     * This method is intended to be used by BoLChunkCatalog in the isSRM_FILE_PINNED method invocation.
     * 
     * In case of any error, 0 is returned.
     */
    public synchronized int numberInSRM_FILE_PINNED(String surl) {
        checkConnection();
        String str = "SELECT COUNT(s.ID) " + "FROM status_BoL s JOIN request_BoL r "
                + "ON (s.request_BoLID=r.ID) " + "WHERE r.sourceSURL=? AND s.statusCode=?";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            stmt.setString(1, surl); // Prepared statement spares DB-specific String notation!
            logWarnings(stmt.getWarnings());
            stmt.setInt(2, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_PINNED));
            log.debug("BoL CHUNK DAO - numberInSRM_FILE_PINNED method: " + stmt.toString());
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
            log.error("BoL CHUNK DAO! Unable to determine numberInSRM_FILE_PINNED! Returning 0! " + e);
            close(rs);
            close(stmt);
            return 0;
        }
    }

    /**
     * TODO WARNING! THIS IS A WORK IN PROGRESS!!!
     * 
     * Method used to refresh the BoLChunkDataTO information from the MySQL DB.
     * 
     * In this first version, only the statusCode is reloaded from the DB. TODO The next version must contains
     * all the information related to the Chunk!
     * 
     * In case of any error, an error message gets logged but no exception is thrown.
     */

    public synchronized BoLChunkDataTO refresh(long primary_key) {

        checkConnection();
        String str = null;
        PreparedStatement find = null;
        ResultSet rs = null;

        try {
            // get chunks of the request
            str = "SELECT  s.statusCode " + "FROM status_BoL s " + "WHERE s.ID=?";
            find = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            find.setLong(1, primary_key);

            logWarnings(find.getWarnings());
            log.debug("BoL CHUNK DAO: refresh status method; " + find.toString());

            rs = find.executeQuery();

            logWarnings(find.getWarnings());
            BoLChunkDataTO aux = null;
            // The result shoul be un
            // TODO REMOVE THIS WHILE
            while (rs.next()) {
                aux = new BoLChunkDataTO();
                // aux.setPrimaryKey(rs.getLong("s.ID"));
                aux.setStatus(rs.getInt("s.statusCode"));
                // aux.setRequestToken(strToken);
                // aux.setFromSURL(rs.getString("g.sourceSURL"));
                // aux.setLifeTime(rs.getInt("r.pinLifetime"));
                // aux.setDirOption(rs.getBoolean("d.isSourceADirectory"));
                // aux.setAllLevelRecursive(rs.getBoolean("d.allLevelRecursive"));
                // aux.setNumLevel(rs.getInt("d.numOfLevels"));
                // aux.setProtocolList(protocols);
            }
            close(rs);
            close(find);
            return aux;
        } catch (SQLException e) {
            log.error("BoL CHUNK DAO: " + e);
            close(rs);
            close(find);
            return null; // return null TransferObject!
        }
    }

    /**
     * Method used in extraordinary situations to signal that data retrieved from the DB was malformed and
     * could not be translated into the StoRM object model.
     * 
     * This method attempts to change the status of the request to SRM_FAILURE and record it in the DB.
     * 
     * This operation could potentially fail because the source of the malformed problems could be a
     * problematic DB; indeed, initially only log messages where recorded.
     * 
     * Yet it soon became clear that the source of malformed data were the clients and/or FE recording info in
     * the DB. In these circumstances the client would see its request as being in the SRM_IN_PROGRESS state
     * for ever. Hence the pressing need to inform it of the encountered problems.
     */
    public synchronized void signalMalformedBoLChunk(BoLChunkDataTO auxTO) {
        checkConnection();
        String signalSQL = "UPDATE status_BoL s " + "SET s.statusCode="
                + StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FAILURE) + ", s.explanation=? "
                + "WHERE s.ID=" + auxTO.getPrimaryKey();
        PreparedStatement signal = null;
        try {
            signal = con.prepareStatement(signalSQL);
            logWarnings(con.getWarnings());
            signal.setString(1, "Request is malformed!"); // Prepared statement spares DB-specific
            // String notation!
            logWarnings(signal.getWarnings());
            log.debug("BoL CHUNK DAO: signalMalformed; " + signal.toString());
            signal.executeUpdate();
            logWarnings(signal.getWarnings());
        } catch (SQLException e) {
            log.error("BoLChunkDAO! Unable to signal in DB that the request was malformed! Request: "
                    + auxTO.toString() + "; Exception: " + e.toString());
        } finally {
            close(signal);
        }
    }

    /**
     * Method that updates all expired requests in SRM_FILE_PINNED state, into SRM_RELEASED.
     * 
     * This is needed when the client forgets to invoke srmReleaseFiles().
     */
    public synchronized void transitExpiredSRM_FILE_PINNED() {

        // TODO: put a limit on the queries.....

        checkConnection();
        List<String> expiredSurlList = new LinkedList<String>();
        String str;

        Statement statement = null;
        try {

            // start transaction
            con.setAutoCommit(false);

            statement = con.createStatement();

            str = "SELECT sourceSURL FROM "
                    + "request_BoL rb JOIN (status_BoL s, request_queue r) ON s.request_BoLID=rb.ID AND rb.request_queueID=r.ID "
                    + "WHERE s.statusCode="
                    + StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_PINNED)
                    + " AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(r.timeStamp) >= r.pinLifetime ";

            ResultSet res = statement.executeQuery(str);
            logWarnings(statement.getWarnings());

            while (res.next()) {
                expiredSurlList.add(res.getString("sourceSURL"));
            }

            if (expiredSurlList.isEmpty()) {
                log.debug("BoLChunkDAO! No chunk of BoL request was transited from SRM_FILE_PINNED to SRM_RELEASED.");
                return;
            }

        } catch (SQLException e) {
            log.error("BoLChunkDAO! SQLException." + e);
            rollback(con);
            return;
        } finally {
            close(statement);
        }

        PreparedStatement preparedStatement = null;
        try {

            str = "UPDATE "
                    + "status_BoL s JOIN (request_BoL rg, request_queue r) ON s.request_BoLID=rg.ID AND rg.request_queueID=r.ID "
                    + "SET s.statusCode=? "
                    + "WHERE s.statusCode=? AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(r.timeStamp) >= r.pinLifetime ";
            preparedStatement = con.prepareStatement(str);
            logWarnings(con.getWarnings());

            preparedStatement.setInt(1, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_RELEASED));
            logWarnings(preparedStatement.getWarnings());
            preparedStatement.setInt(2, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_PINNED));
            logWarnings(preparedStatement.getWarnings());

            log.debug("BoL CHUNK DAO - transitExpiredSRM_FILE_PINNED method: " + preparedStatement.toString());

            int count = preparedStatement.executeUpdate();
            logWarnings(preparedStatement.getWarnings());

            if (count == 0) {
                log.debug("BoLChunkDAO! No chunk of BoL request was transited from SRM_FILE_PINNED to SRM_RELEASED.");
            } else {
                log.info("BoLChunkDAO! " + count
                        + " chunks of BoL requests were transited from SRM_FILE_PINNED to SRM_RELEASED.");
            }
        } catch (SQLException e) {
            log.error("BoLChunkDAO! Unable to transit expired SRM_FILE_PINNED chunks of BoL requests, to SRM_RELEASED! "
                    + e);
            rollback(con);
            return;
        } finally {
            close(preparedStatement);
        }

        Set<String> pinnedSurlList = new HashSet<String>();
        try {

            statement = con.createStatement();

            str = "SELECT sourceSURL FROM "
                    + "request_BoL rb JOIN (status_BoL s, request_queue r) ON s.request_BoLID=rb.ID AND rb.request_queueID=r.ID "
                    + "WHERE s.statusCode="
                    + StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_PINNED)
                    + " AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(r.timeStamp) < r.pinLifetime ";

            ResultSet res = statement.executeQuery(str);
            logWarnings(statement.getWarnings());

            while (res.next()) {
                pinnedSurlList.add(res.getString("sourceSURL"));
            }

            str = "SELECT sourceSURL FROM "
                    + "request_Get rg JOIN (status_Get s, request_queue r) ON s.request_GetID=rg.ID AND rg.request_queueID=r.ID "
                    + "WHERE s.statusCode="
                    + StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_PINNED)
                    + " AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(r.timeStamp) < r.pinLifetime ";

            res = statement.executeQuery(str);
            logWarnings(statement.getWarnings());

            while (res.next()) {
                pinnedSurlList.add(res.getString("sourceSURL"));
            }

        } catch (SQLException e) {
            log.error("BoLChunkDAO! SQLException." + e);
            rollback(con);
        } finally {
            close(statement);
        }

        commit(con);

        if (Configuration.getInstance().getTapeEnabled()) {
            for (String surl : expiredSurlList) {
                if (!pinnedSurlList.contains(surl)) {

                    StoRI stori;

                    try {

                        stori = NamespaceDirector.getNamespace()
                                                 .resolveStoRIbySURL(TSURL.makeFromString(surl));

                    } catch (NamespaceException e) {
                        log.error("Cannot remove EA \"pinned\" because cannot get StoRI from SURL: " + surl);
                        continue;
                    } catch (InvalidTSURLAttributesException e) {
                        log.error("Invalid SURL, cannot release the pin (Extended Attribute): " + surl);
                        continue;
                    }

                    if (Configuration.getInstance().getTapeEnabled()) {
                        StormEA.removePinned(stori.getAbsolutePath());
                    }
                }
            }
        }
    }

    /**
     * Method that transits chunks in SRM_FILE_PINNED to SRM_ABORTED, for the given SURL: the overall request
     * status of the requests containing that chunk, is not changed! The TURL is set to null.
     * 
     * Beware, that the chunks may be part of requests that have finished, or that still have not finished
     * because other chunks are still being processed.
     */
    public synchronized void transitSRM_FILE_PINNEDtoSRM_ABORTED(String surl, String explanation) {
        checkConnection();
        String str = "UPDATE "
                + "status_BoL s JOIN (request_BoL rg, request_queue r) ON s.request_BoLID=rg.ID AND rg.request_queueID=r.ID "
                + "SET s.statusCode=?, s.explanation=?, s.transferURL=NULL "
                + "WHERE s.statusCode=? AND rg.targetSURL=?";
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            stmt.setInt(1, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_ABORTED));
            logWarnings(stmt.getWarnings());
            stmt.setString(2, explanation);
            logWarnings(stmt.getWarnings());
            stmt.setInt(3, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_PINNED));
            logWarnings(stmt.getWarnings());
            stmt.setString(4, surl);
            logWarnings(stmt.getWarnings());
            log.debug("BoL CHUNK DAO - transitSRM_FILE_PINNEDtoSRM_ABORTED: " + stmt.toString());
            int count = stmt.executeUpdate();
            logWarnings(stmt.getWarnings());
            log.debug("BoL CHUNK DAO! " + count
                    + " chunks were transited from SRM_FILE_PINNED to SRM_ABORTED.");
        } catch (SQLException e) {
            log.error("BoL CHUNK DAO! Unable to transitSRM_FILE_PINNEDtoSRM_ABORTED! " + e);
        } finally {
            close(stmt);
        }
    }

    /**
     * Method that updates all chunks in SRM_FILE_PINNED state, into SRM_RELEASED. An array of long
     * representing the primary key of each chunk is required: only they get the status changed provided their
     * current status is SRM_FILE_PINNED.
     * 
     * This method is used during srmReleaseFiles
     * 
     * In case of any error nothing happens and no exception is thrown, but proper messages get logged.
     */
    public synchronized void transitSRM_FILE_PINNEDtoSRM_RELEASED(long[] ids) {
        checkConnection();
        String str = "UPDATE "
                + "status_BoL s JOIN (request_BoL rg, request_queue r) ON s.request_BoLID=rg.ID AND rg.request_queueID=r.ID "
                + "SET s.statusCode=? " + "WHERE s.statusCode=? AND s.ID IN " + makeWhereString(ids);
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            stmt.setInt(1, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_RELEASED));
            logWarnings(stmt.getWarnings());
            stmt.setInt(2, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_PINNED));
            logWarnings(stmt.getWarnings());
            log.debug("BoL CHUNK DAO - transitSRM_FILE_PINNEDtoSRM_RELEASED: " + stmt.toString());
            int count = stmt.executeUpdate();
            logWarnings(stmt.getWarnings());
            if (count == 0) {
                log.debug("BoL CHUNK DAO! No chunk of BoL request was transited from SRM_FILE_PINNED to SRM_RELEASED.");
            } else {
                log.info("BoL CHUNK DAO! " + count
                        + " chunks of BoL requests were transited from SRM_FILE_PINNED to SRM_RELEASED.");
            }
        } catch (SQLException e) {
            log.error("BoL CHUNK DAO! Unable to transit chunks from SRM_FILE_PINNED to SRM_RELEASED! " + e);
        } finally {
            close(stmt);
        }
    }

    public synchronized void transitSRM_FILE_PINNEDtoSRM_RELEASED(long[] ids, TRequestToken token) {
        if (token == null) {
            transitSRM_FILE_PINNEDtoSRM_RELEASED(ids);
        } else {
            /*
             * If a request token has been specified, only the related BoL requests have to be released. This
             * is done adding the r.r_token="..." clause in the where subquery.
             */
            checkConnection();
            String str = "UPDATE "
                    + "status_BoL s JOIN (request_BoL rg, request_queue r) ON s.request_BoLID=rg.ID AND rg.request_queueID=r.ID "
                    + "SET s.statusCode=? " + "WHERE s.statusCode=? AND r.r_token='" + token.toString()
                    + "' AND s.ID IN " + makeWhereString(ids);
            PreparedStatement stmt = null;
            try {
                stmt = con.prepareStatement(str);
                logWarnings(con.getWarnings());
                stmt.setInt(1, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_RELEASED));
                logWarnings(stmt.getWarnings());
                stmt.setInt(2, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_PINNED));
                logWarnings(stmt.getWarnings());
                log.debug("BoL CHUNK DAO - transitSRM_FILE_PINNEDtoSRM_RELEASED: " + stmt.toString());
                int count = stmt.executeUpdate();
                logWarnings(stmt.getWarnings());
                if (count == 0) {
                    log.debug("BoL CHUNK DAO! No chunk of BoL request was transited from SRM_FILE_PINNED to SRM_RELEASED.");
                } else {
                    log.info("BoL CHUNK DAO! " + count
                            + " chunks of BoL requests were transited from SRM_FILE_PINNED to SRM_RELEASED.");
                }
            } catch (SQLException e) {
                log.error("BoL CHUNK DAO! Unable to transit chunks from SRM_FILE_PINNED to SRM_RELEASED! "
                        + e);
            } finally {
                close(stmt);
            }
        }

    }

    /**
     * Method used to save the changes made to a retrieved BoLChunkDataTO, back into the MySQL DB.
     * 
     * Only the fileSize, statusCode and explanation, of status_BoL table are written to the DB. Likewise for
     * the request pinLifetime.
     * 
     * In case of any error, an error message gets logged but no exception is thrown.
     */
    public synchronized void update(BoLChunkDataTO to) {
        checkConnection();
        PreparedStatement updateFileReq = null;
        try {
            // ready updateFileReq...
            updateFileReq = con.prepareStatement("UPDATE request_queue r JOIN (status_BoL s, request_BoL g) ON (r.ID=g.request_queueID AND s.request_BoLID=g.ID) SET s.fileSize=?, s.statusCode=?, s.explanation=?, r.pinLifetime=? WHERE s.ID=?");
            logWarnings(con.getWarnings());
            updateFileReq.setLong(1, to.getFileSize());
            logWarnings(updateFileReq.getWarnings());
            updateFileReq.setInt(2, to.getStatus());
            logWarnings(updateFileReq.getWarnings());
            updateFileReq.setString(3, to.getErrString());
            logWarnings(updateFileReq.getWarnings());
            updateFileReq.setInt(4, to.getLifeTime());
            logWarnings(updateFileReq.getWarnings());
            updateFileReq.setLong(5, to.getPrimaryKey());
            logWarnings(updateFileReq.getWarnings());
            // execute update
            log.debug("BoL CHUNK DAO: update method; " + updateFileReq.toString());
            updateFileReq.executeUpdate();
            logWarnings(updateFileReq.getWarnings());
        } catch (SQLException e) {
            log.error("BoL CHUNK DAO: Unable to complete update! " + e);
        } finally {
            close(updateFileReq);
        }
    }

    /**
     * Auxiliary method that checks if time for resetting the connection has come, and eventually takes it
     * down and up back again.
     */
    private void checkConnection() {
        if (reconnect) {
            log.debug("BoL CHUNK DAO! Reconnecting to DB! ");
            takeDownConnection();
            setUpConnection();
            reconnect = false;
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
                log.error("BoL CHUNK DAO! Unable to close ResultSet! Exception: " + e);
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
                log.error("BoL CHUNK DAO! Unable to close Statement " + stmt.toString() + " - Exception: "
                        + e);
            }
        }
    }

    private void commit(Connection con) {
        try {

            con.commit();
            con.setAutoCommit(true);

        } catch (SQLException e) {
            log.error("BoL, SQL EXception", e);
        }
    }

    /**
     * Private method that returns the generated ID: it throws an exception in case of any problem!
     */
    private int extractID(ResultSet rs) throws Exception {
        if (rs == null) {
            throw new Exception("BoL CHUNK DAO! Null ResultSet!");
        }
        if (rs.next()) {
            return rs.getInt(1);
        } else {
            log.error("BoL CHUNK DAO! It was not possible to establish the assigned autoincrement primary key!");
            throw new Exception("BoL CHUNK DAO! It was not possible to establish the assigned autoincrement primary key!");
        }
    }

    /**
     * Auxiliary private method that logs all SQL warnings.
     */
    private void logWarnings(SQLWarning w) {
        if (w != null) {
            log.debug("BoL CHUNK DAO: " + w.toString());
            while ((w = w.getNextWarning()) != null) {
                log.debug("BoL CHUNK DAO: " + w.toString());
            }
        }
    }

    private String makeSurlString(String[] surls) {
        StringBuffer sb = new StringBuffer("(");
        int n = surls.length;
        for (int i = 0; i < n; i++) {
            sb.append("'");
            sb.append(surls[i]);
            sb.append("'");
            if (i < (n - 1)) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
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
     * Auxiliary method used to roll back a failed transaction
     */
    private void rollback(Connection con) {
        if (con != null) {
            try {
                con.rollback();
                log.error("BoL CHUNK DAO: roll back successful!");
            } catch (SQLException e2) {
                log.error("BoL CHUNK DAO: roll back failed! " + e2);
            }
        }
    }

    /**
     * Auxiliary method that sets up the connection to the DB, as well as the prepared statement.
     */
    private void setUpConnection() {
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, name, password);
            if (con == null) {
                log.error("BoL CHUNK DAO! Exception in setUpConnection! DriverManager could not create connection!");
            } else {
                logWarnings(con.getWarnings());
            }
        } catch (ClassNotFoundException e) {
            log.error("BoL CHUNK DAO! Exception in setUpConnection! " + e);
        } catch (SQLException e) {
            log.error("BoL CHUNK DAO! Exception in setUpConenction! " + e);
        }
    }

    /**
     * Auxiliary method that tales down a connection to the DB.
     */
    private void takeDownConnection() {
        try {
            con.close();
        } catch (SQLException e) {
            log.error("BoL CHUNK DAO! Exception in takeDownConnection method: " + e);
        }
    }
}
