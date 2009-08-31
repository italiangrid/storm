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
 * DAO class for PtGChunkCatalog. This DAO is specifically designed to connect
 * to a MySQL DB. The raw data found in those tables is pre-treated in order to
 * turn it into the Object Model of StoRM. See Method comments for further info.
 *
 * BEWARE! DAO Adjusts for extra fields in the DB that are not present in the
 * object model.
 *
 * @author  EGRID ICTP
 * @version 3.0
 * @date    June 2005
 */
public class PtGChunkDAO {

    private static final Logger log = LoggerFactory.getLogger(PtGChunkDAO.class);

    private final String driver=Configuration.getInstance().getDBDriver();//String with the name of the class for the DB driver
    private final String url=Configuration.getInstance().getDBURL(); //String referring to the URL of the DB
    private final String password=Configuration.getInstance().getDBPassword(); //String with the password for the DB
    private final String name=Configuration.getInstance().getDBUserName();     //String with the name for the DB
    private Connection con=null; //Connection to DB - WARNING!!! It is kept open all the time!
    private final static PtGChunkDAO dao = new PtGChunkDAO(); //DAO!

    private Timer clock = null; //timer thread that will run a taask to alert when reconnecting is necessary!
    private TimerTask clockTask = null; //timer task that will update the boolean signalling that a reconnection is neede!
    private long period = Configuration.getInstance().getDBReconnectPeriod() * 1000;//milliseconds that must pass before reconnecting to DB
    private long delay = Configuration.getInstance().getDBReconnectDelay() * 1000;//initial delay in millseconds before starting timer
    private boolean reconnect = false; //boolean that tells whether reconnection is needed because of MySQL bug!

    private PtGChunkDAO() {
        setUpConnection();
        clock = new Timer();
        clockTask = new TimerTask() {
            @Override
            public void run() {
                reconnect = true;
            }
        }; //clock task
        clock.scheduleAtFixedRate(clockTask,delay,period);
    }

    /**
     * Method that returns the only instance of the PtGChunkDAO.
     */
    public static PtGChunkDAO getInstance() {
        return dao;
    }





    /**
     * Auxiliary method that sets up the conenction to the DB, as well as the prepared
     * statement.
     */
    private void setUpConnection() {
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url,name,password);
            if (con==null) {
                log.error("PTG CHUNK DAO! Exception in setUpConnection! DriverManager could not create connection!");
            } else {
                logWarnings(con.getWarnings());
            }
        } catch (ClassNotFoundException e) {
            log.error("PTG CHUNK DAO! Exception in setUpConnection! "+e);
        } catch (SQLException e) {
            log.error("PTG CHUNK DAO! Exception in setUpConenction! "+e);
        }
    }

    /**
     * Auxiliary method that tales down a conenctin to the DB.
     */
    private void takeDownConnection() {
        try {
            con.close();
        } catch (SQLException e) {
            log.error("PTG CHUNK DAO! Exception in takeDownConnection method: "+e);
        }
    }

    /**
     * Auxiliary method that checks if time for resetting the connection has
     * come, and eventually takes it down and up back again.
     */
    private void checkConnection() {
        if (reconnect) {
            log.debug("PTG CHUNK DAO! Reconnecting to DB! ");
            takeDownConnection();
            setUpConnection();
            reconnect = false;
        }
    }







    /**
     * Method that queries the MySQL DB to find all entries matching the supplied
     * TRequestToken. The Collection contains the corresponding PtGChunkDataTO
     * objects.
     *
     * An initial simple query establishes the list of protocols associated with
     * the request. A second complex query establishes all chunks associated with
     * the request, by properly joining request_queue, request_Get, status_Get
     * and request_DirOption. The considered fields are:
     *
     * (1) From status_Get: the ID field which becomes the TOs primary key, and
     * statusCode.
     *
     * (2) From request_Get: sourceSURL
     *
     * (3) From request_queue: pinLifetime
     *
     * (4) From request_DirOption: isSourceADirectory, alLevelRecursive,
     *                             numOfLevels
     *
     * In case of any error, a log gets written and an empty collection is
     * returned. No exception is thrown.
     *
     * NOTE! Chunks in SRM_ABORTED status are NOT returned!
     */
    public Collection find(TRequestToken requestToken) {
        checkConnection();
        String strToken = requestToken.toString();
        String str = null;
        PreparedStatement find = null;
        ResultSet rs = null;
        try {
            //get protocols for the request
            //str = "SELECT tp.config_ProtocolsID "+
            //    "FROM request_TransferProtocols tp "+
            //    "WHERE tp.request_queueID IN "+
            //   "(SELECT r.ID FROM request_queue r WHERE r.r_token=?)";

            str = "SELECT tp.config_ProtocolsID "+
            "FROM request_TransferProtocols tp JOIN request_queue r ON tp.request_queueID=r.ID "+
            "WHERE r.r_token=?";

            find = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            List protocols = new ArrayList();
            find.setString(1,strToken);
            logWarnings(find.getWarnings());
            log.debug("PTG CHUNK DAO: find method; "+find.toString());
            rs = find.executeQuery();
            logWarnings(find.getWarnings());
            while (rs.next()) {
                protocols.add(rs.getString("tp.config_ProtocolsID"));
            }
            close(rs);
            close(find);

            //get chunks of the request
            str = "SELECT s.ID, s.statusCode, r.pinLifetime, g.sourceSURL, d.isSourceADirectory, d.allLevelRecursive, d.numOfLevels "+
            "FROM request_queue r JOIN (request_Get g, status_Get s) "+
            "ON (g.request_queueID=r.ID AND s.request_GetID=g.ID) "+
            "LEFT JOIN request_DirOption d ON g.request_DirOptionID=d.ID "+
            "WHERE r.r_token=? AND s.statusCode<>?";
            find = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            List list = new ArrayList();
            find.setString(1,strToken);
            logWarnings(find.getWarnings());
            find.setInt(2,StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_ABORTED) );
            logWarnings(find.getWarnings());
            log.debug("PTG CHUNK DAO: find method; "+find.toString());
            rs = find.executeQuery();
            logWarnings(find.getWarnings());
            PtGChunkDataTO aux = null;
            while (rs.next()) {
                aux = new PtGChunkDataTO();
                aux.setPrimaryKey(rs.getLong("s.ID"));
                aux.setStatus(rs.getInt("s.statusCode"));
                aux.setRequestToken(strToken);
                aux.setFromSURL(rs.getString("g.sourceSURL"));
                aux.setLifeTime(rs.getInt("r.pinLifetime"));
                aux.setDirOption(rs.getBoolean("d.isSourceADirectory"));
                aux.setAllLevelRecursive(rs.getBoolean("d.allLevelRecursive"));
                aux.setNumLevel(rs.getInt("d.numOfLevels"));
                aux.setProtocolList(protocols);
                list.add(aux);
            }
            close(rs);
            close(find);
            return list;
        } catch (SQLException e) {
            log.error("PTG CHUNK DAO: "+e);
            close(rs);
            close(find);
            return new ArrayList(); //return empty Collection!
        }
    }



    /**
     * Method that returns a Collection of ReducedPtGChunkDataTO associated to
     * the given TRequestToken expressed as String.
     */
    public Collection findReduced(String reqtoken) {
        checkConnection();
        PreparedStatement find = null;
        ResultSet rs = null;
        try {
            //get reduced chunks
            String str = "SELECT s.ID, s.statusCode, g.sourceSURL "+
            "FROM request_queue r JOIN (request_Get g, status_Get s) "+
            "ON (g.request_queueID=r.ID AND s.request_GetID=g.ID) "+
            "WHERE r.r_token=?";
            find = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            List list = new ArrayList();
            find.setString(1,reqtoken);
            logWarnings(find.getWarnings());
            log.debug("PtG CHUNK DAO! findReduced with request token; "+find.toString());
            rs = find.executeQuery();
            logWarnings(find.getWarnings());
            ReducedPtGChunkDataTO aux = null;
            while (rs.next()) {
                aux = new ReducedPtGChunkDataTO();
                aux.setPrimaryKey(rs.getLong("s.ID"));
                aux.setFromSURL(rs.getString("g.sourceSURL"));
                aux.setStatus(rs.getInt("s.statusCode"));
                list.add(aux);
            }
            close(rs);
            close(find);
            return list;
        } catch (SQLException e) {
            log.error("PTG CHUNK DAO: "+e);
            close(rs);
            close(find);
            return new ArrayList(); //return empty Collection!
        }
    }

    /**
     * Method that returns a Collection of ReducedPtGChunkDataTO associated to
     * the given griduser, and whose SURLs are contained in the supplied array
     * of Strings.
     */
    public Collection findReduced(String griduser, String[] surls) {
        checkConnection();
        PreparedStatement find = null;
        ResultSet rs = null;
        try {
            //get reduced chunks
            String str = "SELECT s.ID, s.statusCode, g.sourceSURL "+
            "FROM request_queue r JOIN (request_Get g, status_Get s) "+
            "ON (g.request_queueID=r.ID AND s.request_GetID=g.ID) "+
            "WHERE r.client_dn=? AND g.sourceSURL IN "+makeSurlString(surls);
            find = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            List list = new ArrayList();
            find.setString(1,griduser);
            logWarnings(find.getWarnings());
            log.debug("PtG CHUNK DAO! findReduced with griduser+surlarray; "+find.toString());
            rs = find.executeQuery();
            logWarnings(find.getWarnings());
            ReducedPtGChunkDataTO aux = null;
            while (rs.next()) {
                aux = new ReducedPtGChunkDataTO();
                aux.setPrimaryKey(rs.getLong("s.ID"));
                aux.setFromSURL(rs.getString("g.sourceSURL"));
                aux.setStatus(rs.getInt("s.statusCode"));
                list.add(aux);
            }
            close(rs);
            close(find);
            return list;
        } catch (SQLException e) {
            log.error("PTG CHUNK DAO: "+e);
            close(rs);
            close(find);
            return new ArrayList(); //return empty Collection!
        }
    }

    private String makeSurlString(String[] surls) {
        StringBuffer sb = new StringBuffer("(");
        int n = surls.length;
        for (int i = 0; i<n; i++ ) {
            sb.append("'"); sb.append(surls[i]); sb.append("'");
            if (i<(n-1)) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }



    /**
     * Method used to save the changes made to a retrieved PtPChunkDataTO,
     * back into the MySQL DB.
     *
     * Only the fileSize, transferURL, statusCode and explanation, of status_Get
     * table are written to the DB. Likewise for the request pinLifetime.
     *
     * In case of any error, an error messagge gets logged but no exception is
     * thrown.
     */
    public void update(PtGChunkDataTO to) {
        checkConnection();
        PreparedStatement updateFileReq = null;
        try {
            //ready updateFileReq...
            updateFileReq = con.prepareStatement("UPDATE request_queue r JOIN (status_Get s, request_Get g) ON (r.ID=g.request_queueID AND s.request_GetID=g.ID) SET s.fileSize=?, s.transferURL=?, s.statusCode=?, s.explanation=?, r.pinLifetime=? WHERE s.ID=?");
            logWarnings(con.getWarnings());
            updateFileReq.setLong(1,to.fileSize());
            logWarnings(updateFileReq.getWarnings());
            updateFileReq.setString(2,to.turl());
            logWarnings(updateFileReq.getWarnings());
            updateFileReq.setInt(3,to.status());
            logWarnings(updateFileReq.getWarnings());
            updateFileReq.setString(4,to.errString());
            logWarnings(updateFileReq.getWarnings());
            updateFileReq.setInt(5,to.lifeTime());
            logWarnings(updateFileReq.getWarnings());
            updateFileReq.setLong(6,to.primaryKey());
            logWarnings(updateFileReq.getWarnings());
            //execute update
            log.debug("PTG CHUNK DAO: update method; "+updateFileReq.toString());
            updateFileReq.executeUpdate();
            logWarnings(updateFileReq.getWarnings());
        } catch (SQLException e) {
            log.error("PtG CHUNK DAO: Unable to complete update! "+e);
        } finally {
            close(updateFileReq);
        }
    }



    /**
     * TODO
     * WARNING! THIS IS A WORK IN PROGRESS!!!
     * 
     * Method used to refresh the PtPChunkDataTO information from
     * the MySQL DB.
     *
     * In this first version, only the statusCode and the TURL are reloaded from the DB.
     * TODO The next version must contains all the information related to the Chunk!
     *
     * In case of any error, an error messagge gets logged but no exception is
     * thrown.
     */

    public PtGChunkDataTO refresh(long primary_key) {

        checkConnection();
        String str = null;
        PreparedStatement find = null;
        ResultSet rs = null;

        try {
            //get chunks of the request
            str = "SELECT  s.statusCode, s.transferURL "+
            "FROM status_Get s "+
            "WHERE s.ID=?";
            find = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            find.setLong(1,primary_key);

            logWarnings(find.getWarnings());
            log.debug("PTG CHUNK DAO: refresh status method; "+find.toString());

            rs = find.executeQuery();

            logWarnings(find.getWarnings());
            PtGChunkDataTO aux = null;
            //The result shoul be un
            //TODO REMOVE THIS WHILE
            while (rs.next()) {
                aux = new PtGChunkDataTO();
                //aux.setPrimaryKey(rs.getLong("s.ID"));
                aux.setStatus(rs.getInt("s.statusCode"));
                aux.setTurl(rs.getString("s.transferURL"));
                //aux.setRequestToken(strToken);
                //aux.setFromSURL(rs.getString("g.sourceSURL"));
                //aux.setLifeTime(rs.getInt("r.pinLifetime"));
                //aux.setDirOption(rs.getBoolean("d.isSourceADirectory"));
                //aux.setAllLevelRecursive(rs.getBoolean("d.allLevelRecursive"));
                //aux.setNumLevel(rs.getInt("d.numOfLevels"));
                //aux.setProtocolList(protocols);
            }
            close(rs);
            close(find);
            return aux;
        } catch (SQLException e) {
            log.error("PTG CHUNK DAO: "+e);
            close(rs);
            close(find);
            return null; //return null TransferObject!
        }
    }









    /**
     * Method used to add a new record to the DB: the supplied PtGChunkDataTO
     * gets its primaryKey changed to the one assigned by the DB.
     *
     * The supplied PtGChunkData is used to fill in only the DB table where file
     * specific info gets recorded: it does _not_ add a new request! So if
     * spurious data is supplied, it will just stay there because of a lack of
     * a parent request!
     */
    public void addChild(PtGChunkDataTO to) {
        checkConnection();
        String str = null;
        PreparedStatement id = null; //statement to find out the ID associated to the request token
        PreparedStatement addDirOption = null; //statement to add the TDirOption
        PreparedStatement addGet = null; //statement to add the request_Get info
        PreparedStatement addChild = null; //statement to the status_Get info
        ResultSet rsid = null; //result set containing the ID of the request.
        ResultSet rsdo = null; //result set containing the generated ID of the TDirOption insertion
        ResultSet rsg = null; //result set containing the generated ID of the request_Get insertion
        ResultSet rs = null; //result set containing the ID generated after the status_Get insertion
        try {

            //WARNING!!!! We are forced to run a query to get the ID of the request, which should NOT be so
            //because the corresponding request object should have been changed with the extra field! However, it is not possible
            //at the moment to perform such chage because of strict deadline and the change could wreak havoc
            //the code. So we are forced to make this query!!!

            //begin transaction
            con.setAutoCommit(false);
            logWarnings(con.getWarnings());

            //find ID of request corresponding to given RequestToken
            str = "SELECT r.ID FROM request_queue r WHERE r.r_token=?";
            id = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            id.setString(1,to.requestToken());
            logWarnings(id.getWarnings());
            log.debug("PTG CHUNK DAO: addChild; "+id.toString());
            rsid = id.executeQuery();
            logWarnings(id.getWarnings());
            int request_id = extractID(rsid); //ID of request in request_process!

            //fill in TDirOption
            str = "INSERT INTO request_DirOption (isSourceADirectory,allLevelRecursive,numOfLevels) VALUES (?,?,?)";
            addDirOption = con.prepareStatement(str,Statement.RETURN_GENERATED_KEYS);
            logWarnings(con.getWarnings());
            addDirOption.setBoolean(1,to.dirOption());
            logWarnings(addDirOption.getWarnings());
            addDirOption.setBoolean(2,to.allLevelRecursive());
            logWarnings(addDirOption.getWarnings());
            addDirOption.setInt(3,to.numLevel());
            logWarnings(addDirOption.getWarnings());
            log.debug("PTG CHUNK DAO: addChild; "+addDirOption.toString());
            addDirOption.execute();
            logWarnings(addDirOption.getWarnings());
            rsdo = addDirOption.getGeneratedKeys();
            int do_id = extractID(rsdo);

            //fill in request_Get... sourceSURL and TDirOption!
            str = "INSERT INTO request_Get (request_DirOptionID,request_queueID,sourceSURL) VALUES (?,?,?)";
            addGet = con.prepareStatement(str,Statement.RETURN_GENERATED_KEYS);
            logWarnings(con.getWarnings());
            addGet.setInt(1,do_id);
            logWarnings(addGet.getWarnings());
            addGet.setInt(2,request_id);
            logWarnings(addGet.getWarnings());
            addGet.setString(3,to.fromSURL());
            logWarnings(addGet.getWarnings());
            log.debug("PTG CHUNK DAO: addChild; "+addGet.toString());
            addGet.execute();
            logWarnings(addGet.getWarnings());
            rsg = addGet.getGeneratedKeys();
            int g_id = extractID(rsg);

            //fill in status_Get...
            str = "INSERT INTO status_Get (request_GetID,statusCode,explanation) VALUES (?,?,?)";
            addChild = con.prepareStatement(str,Statement.RETURN_GENERATED_KEYS);
            logWarnings(con.getWarnings());
            addChild.setInt(1,g_id);
            logWarnings(addChild.getWarnings());
            addChild.setInt(2,to.status());
            logWarnings(addChild.getWarnings());
            addChild.setString(3,to.errString());
            logWarnings(addChild.getWarnings());
            log.debug("PTG CHUNK DAO: addChild; "+addChild.toString());
            addChild.execute();
            logWarnings(addChild.getWarnings());
            rs = addChild.getGeneratedKeys();
            int s_id = extractID(rs);

            //end transaction!
            con.commit();
            logWarnings(con.getWarnings());
            con.setAutoCommit(true);
            logWarnings(con.getWarnings());

            //update primary key reading the generated key
            to.setPrimaryKey(s_id);
        } catch (SQLException e) {
            log.error("PTG CHUNK DAO: unable to complete addChild! PtGChunkDataTO: "+to+"; exception received:"+e);
            rollback(con);
        } catch (Exception e) {
            log.error("PTG CHUNK DAO: unable to complete addChild! PtGChunkDataTO: "+to+"; exception received:"+e);
            rollback(con);
        }finally {
            close(rsid);
            close(id);
            close(rsdo);
            close(addDirOption);
            close(rsg);
            close(addGet);
            close(rs);
            close(addChild);
        }
    }



    /**
     * Method used to add a new record to the DB: the supplied PtGChunkDataTO
     * gets its primaryKey changed to the one assigned by the DB. The client_dn
     * must also be supplied as a String.
     *
     * The supplied PtGChunkData is used to fill in all the DB tables where file
     * specific info gets recorded: it _adds_ a new request!
     */
    public void addNew(PtGChunkDataTO to, String client_dn) {
        checkConnection();
        String str = null;
        ResultSet rs_new = null; //result set containing the ID of the inserted  new request
        ResultSet rs_do = null; //result set containing the ID of the inserted TDirOption
        ResultSet rs_g = null; //result set containing the ID of the inserted request_Get
        ResultSet rs_s = null; //result set containing the ID of the inserted request_Status
        PreparedStatement addNew = null; //insert new request into process_request
        PreparedStatement addProtocols = null; //insert protocols for request.
        PreparedStatement addDirOption = null; //insert TDirOption for request
        PreparedStatement addGet = null; //insert request_Get for request
        PreparedStatement addChild = null;
        try {
            //begin transaction
            con.setAutoCommit(false);
            logWarnings(con.getWarnings());

            //add to request_queue...
            str = "INSERT INTO request_queue (config_RequestTypeID,client_dn,pinLifetime,status,errstring,r_token,nbreqfiles,timeStamp) VALUES (?,?,?,?,?,?,?,?)";
            addNew = con.prepareStatement(str,Statement.RETURN_GENERATED_KEYS);
            logWarnings(con.getWarnings());
            addNew.setString(1,RequestTypeConverter.getInstance().toDB(TRequestType.PREPARE_TO_GET)); //request type set to prepare to get!
            logWarnings(addNew.getWarnings());
            addNew.setString(2,client_dn);
            logWarnings(addNew.getWarnings());
            addNew.setInt(3,to.lifeTime());
            logWarnings(addNew.getWarnings());
            addNew.setInt(4,StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_REQUEST_INPROGRESS));
            logWarnings(addNew.getWarnings());
            addNew.setString(5,"New PtG Request resulting from srmCopy invocation.");
            logWarnings(addNew.getWarnings());
            addNew.setString(6,to.requestToken());
            logWarnings(addNew.getWarnings());
            addNew.setInt(7,1); //number of requested files set to 1!
            logWarnings(addNew.getWarnings());
            addNew.setTimestamp(8,new Timestamp(new Date().getTime()));
            logWarnings(addNew.getWarnings());
            log.debug("PTG CHUNK DAO: addNew; "+addNew.toString());
            addNew.execute();
            logWarnings(addNew.getWarnings());
            rs_new = addNew.getGeneratedKeys();
            int id_new = extractID(rs_new);

            //add protocols...
            str = "INSERT INTO request_TransferProtocols (request_queueID,config_ProtocolsID) VALUES (?,?)";
            addProtocols = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            for (Iterator i = to.protocolList().iterator(); i.hasNext(); ) {
                addProtocols.setInt(1,id_new);
                logWarnings(addProtocols.getWarnings());
                addProtocols.setString(2,(String)i.next());
                logWarnings(addProtocols.getWarnings());
                log.debug("PTG CHUNK DAO: addNew; "+addProtocols.toString());
                addProtocols.execute();
                logWarnings(addProtocols.getWarnings());
            }

            //addChild...

            //first fill in TDirOption
            str = "INSERT INTO request_DirOption (isSourceADirectory,allLevelRecursive,numOfLevels) VALUES (?,?,?)";
            addDirOption = con.prepareStatement(str,Statement.RETURN_GENERATED_KEYS);
            logWarnings(con.getWarnings());
            addDirOption.setBoolean(1,to.dirOption());
            logWarnings(addDirOption.getWarnings());
            addDirOption.setBoolean(2,to.allLevelRecursive());
            logWarnings(addDirOption.getWarnings());
            addDirOption.setInt(3,to.numLevel());
            logWarnings(addDirOption.getWarnings());
            log.debug("PTG CHUNK DAO: addNew; "+addDirOption.toString());
            addDirOption.execute();
            logWarnings(addDirOption.getWarnings());
            rs_do = addDirOption.getGeneratedKeys();
            int id_do = extractID(rs_do);

            //second fill in request_Get... sourceSURL and TDirOption!
            str = "INSERT INTO request_Get (request_DirOptionID,request_queueID,sourceSURL) VALUES (?,?,?)";
            addGet = con.prepareStatement(str,Statement.RETURN_GENERATED_KEYS);
            logWarnings(con.getWarnings());
            addGet.setInt(1,id_do);
            logWarnings(addGet.getWarnings());
            addGet.setInt(2,id_new);
            logWarnings(addGet.getWarnings());
            addGet.setString(3,to.fromSURL());
            logWarnings(addGet.getWarnings());
            log.debug("PTG CHUNK DAO: addNew; "+addGet.toString());
            addGet.execute();
            logWarnings(addGet.getWarnings());
            rs_g = addGet.getGeneratedKeys();
            int id_g = extractID(rs_g);

            //third fill in status_Get...
            str = "INSERT INTO status_Get (request_GetID,statusCode,explanation) VALUES (?,?,?)";
            addChild = con.prepareStatement(str,Statement.RETURN_GENERATED_KEYS);
            logWarnings(con.getWarnings());
            addChild.setInt(1,id_g);
            logWarnings(addChild.getWarnings());
            addChild.setInt(2,to.status());
            logWarnings(addChild.getWarnings());
            addChild.setString(3,to.errString());
            logWarnings(addChild.getWarnings());
            log.debug("PTG CHUNK DAO: addNew; "+addChild.toString());
            addChild.execute();
            logWarnings(addChild.getWarnings());
            rs_s = addChild.getGeneratedKeys();
            int id_s = extractID(rs_s);

            //end transaction!
            con.commit();
            logWarnings(con.getWarnings());
            con.setAutoCommit(true);
            logWarnings(con.getWarnings());

            //update primary key reading the generated key
            to.setPrimaryKey(id_s);
        } catch (SQLException e) {
            log.error("PTG CHUNK DAO: Rolling back! Unable to complete addNew! PtGChunkDataTO: "+to+"; exception received:"+e);
            rollback(con);
        } catch (Exception e) {
            log.error("PTG CHUNK DAO: unable to complete addNew! PtGChunkDataTO: "+to+"; exception received:"+e);
            rollback(con);
        } finally {
            close(rs_new);
            close(rs_do);
            close(rs_g);
            close(rs_s);
            close(addNew);
            close(addProtocols);
            close(addDirOption);
            close(addGet);
            close(addChild);
        }
    }

    /**
     * Private method that returns the generated ID: it throws an exception in
     * case of any problem!
     */
    private int extractID(ResultSet rs) throws Exception {
        if (rs==null) {
            throw new Exception("PTG CHUNK DAO! Null ResultSet!");
        }
        if (rs.next()) {
            return rs.getInt(1);
        } else {
            log.error("PTG CHUNK DAO! It was not possible to establish the assigned autoincrement primary key!");
            throw new Exception("PTG CHUNK DAO! It was not possible to establish the assigned autoincrement primary key!");
        }
    }


    /**
     * Method used in extraordinary situations to signal that data retrieved from
     * the DB was malformed and could not be translated into the StoRM object model.
     *
     * This method attempts to change the status of the request to SRM_FAILURE and
     * record it in the DB.
     *
     * This operation could potentially fail because the source of the malformed
     * problems could be a problematic DB; indeed, initially only log messagges
     * where recorded.
     *
     * Yet it soon became clear that the source of malformed data were the clients
     * and/or FE recording info in the DB. In these circumstances the client would see
     * its request as being in the SRM_IN_PROGRESS state for ever. Hence the pressing
     * need to inform it of the encountered problems.
     */
    public void signalMalformedPtGChunk(PtGChunkDataTO auxTO) {
        checkConnection();
        String signalSQL = "UPDATE status_Get s "+
        "SET s.statusCode="+ StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FAILURE) +", s.explanation=? "+
        "WHERE s.ID="+auxTO.primaryKey();
        PreparedStatement signal = null;
        try {
            signal = con.prepareStatement(signalSQL);
            logWarnings(con.getWarnings());
            signal.setString(1,"Request is malformed!"); //Prepared statement spares DB-specific String notation!
            logWarnings(signal.getWarnings());
            log.debug("PTG CHUNK DAO: signalMalformed; "+signal.toString());
            signal.executeUpdate();
            logWarnings(signal.getWarnings());
        } catch (SQLException e) {
            log.error("PtGChunkDAO! Unable to signal in DB that the request was malformed! Request: "+auxTO.toString()+"; Exception: "+e.toString());
        } finally {
            close(signal);
        }
    }

    /**
     * Method that returns the number of Get requests on the given SURL, that
     * are in SRM_FILE_PINNED state.
     *
     * This method is intended to be used by PtGChunkCatalog in the
     * isSRM_FILE_PINNED method invocation.
     *
     * In case of any error, 0 is returned.
     */
    public int numberInSRM_FILE_PINNED(String surl) {
        checkConnection();
        String str = "SELECT COUNT(s.ID) "+
        "FROM status_Get s JOIN request_Get r "+
        "ON (s.request_GetID=r.ID) "+
        "WHERE r.sourceSURL=? AND s.statusCode=?";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            stmt.setString(1,surl); //Prepared statement spares DB-specific String notation!
            logWarnings(stmt.getWarnings());
            stmt.setInt(2,StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_PINNED));
            log.debug("PtG CHUNK DAO - numberInSRM_FILE_PINNED method: "+stmt.toString());
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
            log.error("PtG CHUNK DAO! Unable to determine numberInSRM_FILE_PINNED! Returning 0! "+e);
            close(rs);
            close(stmt);
            return 0;
        }
    }



    /**
     * Method that updates all chunks in SRM_FILE_PINNED state, into SRM_RELEASED.
     * An array of long representing the primary key of each chunk is required:
     * only they get the status changed provided their current status is
     * SRM_FILE_PINNED.
     *
     * This method is used during srmReleaseFiles
     *
     * In case of any error nothing happens and no exception is thrown, but
     * proper messagges get logged.
     */
    public void transitSRM_FILE_PINNEDtoSRM_RELEASED(long[] ids) {
        checkConnection();
        String str = "UPDATE "+
        "status_Get s JOIN (request_Get rg, request_queue r) ON s.request_GetID=rg.ID AND rg.request_queueID=r.ID "+
        "SET s.statusCode=? "+
        "WHERE s.statusCode=? AND s.ID IN " +
        makeWhereString(ids);
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            stmt.setInt(1,StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_RELEASED));
            logWarnings(stmt.getWarnings());
            stmt.setInt(2,StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_PINNED));
            logWarnings(stmt.getWarnings());
            log.debug("PtG CHUNK DAO - transitSRM_FILE_PINNEDtoSRM_RELEASED: "+stmt.toString());
            int count = stmt.executeUpdate();
            logWarnings(stmt.getWarnings());
            if (count==0) {
                log.debug("PtG CHUNK DAO! No chunk of PtG request was transited from SRM_FILE_PINNED to SRM_RELEASED.");
            } else {
                log.info("PtG CHUNK DAO! "+count+" chunks of PtG requests were transited from SRM_FILE_PINNED to SRM_RELEASED.");
            }
        } catch (SQLException e) {
            log.error("PtG CHUNK DAO! Unable to transit chunks from SRM_FILE_PINNED to SRM_RELEASED! "+e);
        } finally {
            close(stmt);
        }
    }



    public void transitSRM_FILE_PINNEDtoSRM_RELEASED(long[] ids, TRequestToken token) {
        if(token == null) {
            transitSRM_FILE_PINNEDtoSRM_RELEASED(ids);
        } else {
            /*
             * If a request token as been specified, only the related Get requests have to be
             * released.
             * This is done adding the r.r_token="..." clause in the where subquery.
             * 
             */
            checkConnection();
            String str = "UPDATE "
                    + "status_Get s JOIN (request_Get rg, request_queue r) ON s.request_GetID=rg.ID AND rg.request_queueID=r.ID "
                    + "SET s.statusCode=? " + "WHERE s.statusCode=? AND r.r_token='" + token.toString()
                    + "' AND s.ID IN " + makeWhereString(ids);
            PreparedStatement stmt = null;
            try {
                stmt = con.prepareStatement(str);
                logWarnings(con.getWarnings());
                stmt.setInt(1,StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_RELEASED));
                logWarnings(stmt.getWarnings());
                stmt.setInt(2,StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_PINNED));
                logWarnings(stmt.getWarnings());
                log.debug("PtG CHUNK DAO - transitSRM_FILE_PINNEDtoSRM_RELEASED: "+stmt.toString());
                int count = stmt.executeUpdate();
                logWarnings(stmt.getWarnings());
                if (count==0) {
                    log.debug("PtG CHUNK DAO! No chunk of PtG request was transited from SRM_FILE_PINNED to SRM_RELEASED.");
                } else {
                    log.info("PtG CHUNK DAO! "+count+" chunks of PtG requests were transited from SRM_FILE_PINNED to SRM_RELEASED.");
                }
            } catch (SQLException e) {
                log.error("PtG CHUNK DAO! Unable to transit chunks from SRM_FILE_PINNED to SRM_RELEASED! "+e);
            } finally {
                close(stmt);
            }
        }

    }



    /**
     * Method that returns a String containing all IDs.
     */
    private String makeWhereString(long[] rowids) {
        StringBuffer sb = new StringBuffer("(");
        int n = rowids.length;
        for (int i = 0; i<n; i++ ) {
            sb.append(rowids[i]);
            if (i<(n-1)) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }




    /**
     * Method that transits chunks in SRM_FILE_PINNED to SRM_ABORTED, for the
     * given SURL: the overall request status of the requests containing that chunk,
     * is not changed! The TURL is set to null.
     *
     * Beware, that the chunks may be part of requests that have finished, or that
     * still have not finished because other chunks are still being processed.
     */
    public void transitSRM_FILE_PINNEDtoSRM_ABORTED(String surl, String explanation) {
        checkConnection();
        String str = "UPDATE "+
        "status_Get s JOIN (request_Get rg, request_queue r) ON s.request_GetID=rg.ID AND rg.request_queueID=r.ID "+
        "SET s.statusCode=?, s.explanation=?, s.transferURL=NULL "+
        "WHERE s.statusCode=? AND rg.targetSURL=?";
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            stmt.setInt(1,StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_ABORTED));
            logWarnings(stmt.getWarnings());
            stmt.setString(2,explanation);
            logWarnings(stmt.getWarnings());
            stmt.setInt(3,StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_PINNED));
            logWarnings(stmt.getWarnings());
            stmt.setString(4,surl);
            logWarnings(stmt.getWarnings());
            log.debug("PtG CHUNK DAO - transitSRM_FILE_PINNEDtoSRM_ABORTED: "+stmt.toString());
            int count = stmt.executeUpdate();
            logWarnings(stmt.getWarnings());
            log.debug("PtG CHUNK DAO! "+count+" chunks were transited from SRM_FILE_PINNED to SRM_ABORTED.");
        } catch (SQLException e) {
            log.error("PtG CHUNK DAO! Unable to transitSRM_FILE_PINNEDtoSRM_ABORTED! "+e);
        } finally {
            close(stmt);
        }
    }

    /**
     * Method that updates all expired requests in SRM_FILE_PINNED state, into SRM_RELEASED.
     * 
     * This is needed when the client forgets to invoke srmReleaseFiles().
     */
    public void transitExpiredSRM_FILE_PINNED() {
        
        // TODO: put a limit on the queries.....
        
        boolean failure = false;
        checkConnection();
        List<String> expiredSurlList = new LinkedList<String>();
        
        String str = "SELECT sourceSURL FROM "
                + "request_Get rg JOIN (status_Get s, request_queue r) ON s.request_GetID=rg.ID AND rg.request_queueID=r.ID "
                + "WHERE s.statusCode=" + StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_PINNED)
                + " AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(r.timeStamp) >= r.pinLifetime ";

        Statement statement = null;
        try {

            statement = con.createStatement();

            statement.executeUpdate("START TRANSACTION");

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
            failure = true;
        } finally {
            close(statement);
        }

        if (failure) {
            commit(con);
            return;
        }
    
    
        str = "UPDATE "
                + "status_Get s JOIN (request_Get rg, request_queue r) ON s.request_GetID=rg.ID AND rg.request_queueID=r.ID "
                + "SET s.statusCode=? "
                + "WHERE s.statusCode=? AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(r.timeStamp) >= r.pinLifetime ";
        
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            
            stmt.setInt(1, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_RELEASED));
            logWarnings(stmt.getWarnings());
            stmt.setInt(2, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_PINNED));
            logWarnings(stmt.getWarnings());
            
            log.debug("PtG CHUNK DAO - transitExpiredSRM_FILE_PINNED method: " + stmt.toString());
            
            int count = stmt.executeUpdate();
            logWarnings(stmt.getWarnings());
            
            if (count == 0) {
                log.debug("PtGChunkDAO! No chunk of PtG request was transited from SRM_FILE_PINNED to SRM_RELEASED.");
                failure = true;
            } else {
                log.info("PtGChunkDAO! " + count
                        + " chunks of PtG requests were transited from SRM_FILE_PINNED to SRM_RELEASED.");
            }
        } catch (SQLException e) {
            log.error("PtGChunkDAO! Unable to transit expired SRM_FILE_PINNED chunks of PtG requests, to SRM_RELEASED! "
                    + e);
        } finally {
            close(stmt);
        }
        
        if (failure) {
            commit(con);
            return;
        }
        
        Set<String> pinnedSurlList = new HashSet<String>();
        try {

            statement = con.createStatement();

            // SURLs pinned by PtGs
            str = "SELECT sourceSURL FROM "
                    + "request_Get rg JOIN (status_Get s, request_queue r) ON s.request_GetID=rg.ID AND rg.request_queueID=r.ID "
                    + "WHERE s.statusCode="
                    + StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_PINNED)
                    + " AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(r.timeStamp) < r.pinLifetime ";

            ResultSet res = statement.executeQuery(str);
            logWarnings(statement.getWarnings());

            while (res.next()) {
                pinnedSurlList.add(res.getString("sourceSURL"));
            }

            // SURLs pinned by BoLs
            str = "SELECT sourceSURL FROM "
                    + "request_BoL rb JOIN (status_BoL s, request_queue r) ON s.request_BoLID=rb.ID AND rb.request_queueID=r.ID "
                    + "WHERE s.statusCode="
                    + StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_PINNED)
                    + " AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(r.timeStamp) < r.pinLifetime ";

            res = statement.executeQuery(str);
            logWarnings(statement.getWarnings());

            while (res.next()) {
                pinnedSurlList.add(res.getString("sourceSURL"));
            }
            
            statement.executeUpdate("COMMIT");
            logWarnings(statement.getWarnings());

        } catch (SQLException e) {
            log.error("BoLChunkDAO! SQLException." + e);
        } finally {
            close(statement);
        }

        for (String surl : expiredSurlList) {
            if (!pinnedSurlList.contains(surl)) {

                StoRI stori;

                try {

                    stori = NamespaceDirector.getNamespace().resolveStoRIbySURL(TSURL.makeFromString(surl));

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



    /**
     * Auxiliary method used to roll back a failed transaction
     */
    private void rollback(Connection con) {
        if (con!=null) {
            try {
                con.rollback();
                log.error("PTG CHUNK DAO: roll back successful!");
            } catch (SQLException e2) {
                log.error("PTG CHUNK DAO: roll back failed! "+e2);
            }
        }
    }

    /**
     * Auxiliary method used to close a Statement
     */
    private void close(Statement stmt) {
        if (stmt!=null) {
            try {
                stmt.close();
            } catch (Exception e) {
                log.error("PTG CHUNK DAO! Unable to close Statement "+stmt.toString()+" - Exception: "+e);
            }
        }
    }
    
    private void commit(Connection con) {
        try {
            Statement s = con.createStatement();
            
            s.executeUpdate("COMMIT");
        } catch (SQLException e) {
            log.error( "BoL, SQL EXception", e);
        }
    }

    /**
     * Auxiliary method used to close a ResultSet
     */
    private void close(ResultSet rset) {
        if (rset!=null) {
            try {
                rset.close();
            } catch (Exception e) {
                log.error("PTG CHUNK DAO! Unable to close ResultSet! Exception: "+e);
            }
        }
    }

    /**
     * Auxiliary private method that logs all SQL warnings.
     */
    private void logWarnings(SQLWarning w) {
        if (w!=null) {
            log.debug("PTG CHUNK DAO: "+w.toString());
            while ((w=w.getNextWarning())!=null) {
                log.debug("PTG CHUNK DAO: "+w.toString());
            }
        }
    }
}
