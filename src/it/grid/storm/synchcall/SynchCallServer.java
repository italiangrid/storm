/**
 * This class represents the Synchronous Call xmlrpc Server .
 * This class hava a set of Handler that manage the FE call invoking the right
 * BackEnd manager.
 *
 * @author  Magnoni Luca
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.synchcall;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.common.TypeConverterFactoryImpl;
//import org.apache.xmlrpc.server.DynamicHandlerMapping;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;

import it.grid.storm.common.HostLookup;
import it.grid.storm.config.Configuration;
import it.grid.storm.srm.types.*;
import it.grid.storm.synchcall.directory.*;
import it.grid.storm.synchcall.discovery.DiscoveryManager;
import it.grid.storm.synchcall.discovery.DiscoveryManagerImpl;
import it.grid.storm.synchcall.discovery.PingConverter;
import it.grid.storm.synchcall.discovery.PingInputData;
import it.grid.storm.synchcall.discovery.PingOutputData;
import it.grid.storm.synchcall.space.*;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.dataTransfer.*;
import it.grid.storm.xmlrpc.converter.datatransfer.PutDoneConverter;
import it.grid.storm.health.BookKeeper;
import it.grid.storm.health.HealthDirector;
import it.grid.storm.health.OperationType;
import it.grid.storm.health.LogEvent;

public class SynchCallServer {
  /**
   * Logger
   */
  private static final Logger log = Logger.getLogger("synch_xmlrpc_server");
  private int _port; //Default port for xmlRpc Server
  private int _secure_port; //Default port for SecureXmlRpc Server
  private List fe_hostname_array; //FE machines hostname array. In case of distributed FE-BE installation
  private List fe_IP_array; //FE machines IPs array. Needed in case of distributed FE-BE installation with dynamic DNS.
  private Configuration config;

  public SynchCallServer() {
    config = Configuration.getInstance();
    //Default value
    _port = 8080;
    _secure_port = 8081;
  };

  //public Integer addTwo(int x, int y) {
  public int addTwo(int x, int y) {
    log.debug("AddTwo: Call received- int X:" + x + " int Y:" + y);
    Integer res = new Integer(x + y);

    log.debug("AddTwo:Result= " + res.intValue());

    return res.intValue();
  }

  public int vectorSize(Vector vect) {
    log.debug("Call received : Vector size = " + vect.size());
    Integer res = new Integer(vect.size());
    log.debug("Result= " + res.intValue());
    return res.intValue();
  }

  public Map ping(Map inputParam) {
    //****** LOGs SYNCH OPERATION SETTING VAR*********
     OperationType opType = OperationType.PNG;
    String dn = "synch";
    long startTime = System.currentTimeMillis();
    long duration = startTime;
    boolean successResult = true;
//******************


     log.debug("Ping: Call received : Structure size = " + inputParam.size());
    log.debug("Ping: Input Structure toString: " + inputParam.toString());

    PingConverter converter = new PingConverter();
    DiscoveryManager discoveryManager = new DiscoveryManagerImpl(DiscoveryManager.PING);
    PingInputData inputData;
    PingOutputData outputData;
    Map outputParam = new HashMap();

    inputData = converter.getPingInputData(inputParam);
    outputData = discoveryManager.ping(inputData);
    outputParam = converter.getOutputParameter(outputData);

    //****** LOGs SYNCH OPERATION *********
     successResult = true;
    duration = System.currentTimeMillis() - startTime;
    logExecution(opType, dn, startTime, duration, successResult);
//******************


     return outputParam;
  }

  public Map putDone(Map inputParam) {

    //****** LOGs SYNCH OPERATION SETTING VAR*********
     OperationType opType = OperationType.PD;
    String dn = "synch";
    long startTime = System.currentTimeMillis();
    long duration = startTime;
    boolean successResult = true;
//******************

     log.debug("putDone: Call received : Structure size = " + inputParam.size());
    log.debug("putDone: Input Structure toString: " + inputParam.toString());

    PutDoneConverter converter = new PutDoneConverter();
    DataTransferManager dataTransferManager = new DataTransferManagerImpl(DataTransferManager.PUTDONE);
    PutDoneInputData inputData;
    PutDoneOutputData outputData;
    Map outputParam = new HashMap();

    inputData = (PutDoneInputData) converter.convertToInputData(inputParam);
    outputData = dataTransferManager.putDone(inputData);
    outputParam = converter.convertFromOutputData((OutputData)outputData);

    //****** LOGs SYNCH OPERATION *********
     successResult = outputData.getReturnStatus().isSRM_SUCCESS();
    duration = System.currentTimeMillis() - startTime;
    logExecution(opType, dn, startTime, duration, successResult);
//******************


     return outputParam;
  }

  public Map releaseFiles(Map inputParam) {
    //****** LOGs SYNCH OPERATION SETTING VAR*********
     OperationType opType = OperationType.RF;
    String dn = "synch";
    long startTime = System.currentTimeMillis();
    long duration = startTime;
    boolean successResult = true;
//******************


     log.debug("releaseFiles: Call received : Structure size = " + inputParam.size());
    log.debug("releaseFiles: Input Structure toString: " + inputParam.toString());

    ReleaseFilesConverter converter = new ReleaseFilesConverter();
    DataTransferManager dataTransferManager = new DataTransferManagerImpl(DataTransferManager.RELEASEFILES);
    ReleaseFilesInputData inputData;
    ReleaseFilesOutputData outputData;
    Map outputParam = new HashMap();

    inputData = converter.getReleaseFilesInputData(inputParam);
    outputData = dataTransferManager.releaseFiles(inputData);
    outputParam = converter.getOutputParameter(outputData);

    //****** LOGs SYNCH OPERATION *********
     successResult = outputData.getReturnStatus().isSRM_SUCCESS();
    duration = System.currentTimeMillis() - startTime;
    logExecution(opType, dn, startTime, duration, successResult);
//******************


     return outputParam;
  }

  public Map extendFileLifeTime(Map inputParam) {

    //****** LOGs SYNCH OPERATION SETTING VAR*********
     OperationType opType = OperationType.EFL;
    String dn = "synch";
    long startTime = System.currentTimeMillis();
    long duration = startTime;
    boolean successResult = true;
    //******************

     log.debug("extendFileLifeTime: Call received : Structure size = " + inputParam.size());
    log.debug("extendFileLifeTime: Input Structure toString: " + inputParam.toString());

    ExtendFileLifeTimeConverter converter = new ExtendFileLifeTimeConverter();
    DataTransferManager dataTransferManager = new DataTransferManagerImpl(DataTransferManager.EXTENDFILELIFETIME);
    ExtendFileLifeTimeInputData inputData;
    ExtendFileLifeTimeOutputData outputData;
    Map outputParam = new HashMap();

    inputData = converter.getExtendFileLifeTimeInputData(inputParam);
    outputData = dataTransferManager.extendFileLifeTime(inputData);
    outputParam = converter.getOutputParameter(outputData);

    //****** LOGs SYNCH OPERATION *********
     successResult = outputData.getReturnStatus().isSRM_SUCCESS();
    duration = System.currentTimeMillis() - startTime;
    logExecution(opType, dn, startTime, duration, successResult);
    //******************

     return outputParam;
  }

  public Map abortRequest(Map inputParam) {

    //****** LOGs SYNCH OPERATION SETTING VAR*********
     OperationType opType = OperationType.AR;
    String dn = "synch";
    long startTime = System.currentTimeMillis();
    long duration = startTime;
    boolean successResult = true;
    //******************

     log.debug("abortRequest: Call received : Structure size = " + inputParam.size());
    log.debug("abortRequest: Input Structure toString: " + inputParam.toString());
    //Converter
    AbortRequestConverter converter = new AbortRequestConverter();
    //Manager
    DataTransferManager dataTransferManager = new DataTransferManagerImpl(DataTransferManager.ABORT_REQUEST);
    //Data structure
    AbortRequestInputData inputData;
    AbortRequestOutputData outputData;
    Map outputParam = new HashMap();
    //Convert data from xmlrpc struct to StoRM types
    inputData = converter.getAbortRequestInputData(inputParam);
    //Performs request
    outputData = dataTransferManager.abortRequest(inputData);
    //Convert data from StoRM type to xmlrpc struct.
    outputParam = converter.getOutputParameter(outputData);

    //****** LOGs SYNCH OPERATION *********
     successResult = outputData.getReturnStatus().isSRM_SUCCESS();
    duration = System.currentTimeMillis() - startTime;
    logExecution(opType, dn, startTime, duration, successResult);
    //******************

     return outputParam;
  }

  public Map abortFiles(HashMap inputParam) {
    //****** LOGs SYNCH OPERATION SETTING VAR*********
     OperationType opType = OperationType.AF;
    String dn = "synch";
    long startTime = System.currentTimeMillis();
    long duration = startTime;
    boolean successResult = true;
    //******************

     log.debug("abortFiles: Call received : Structure size = " + inputParam.size());
    log.debug("abortFiles: Input Structure toString: " + inputParam.toString());
    //Converter
    AbortFilesConverter converter = new AbortFilesConverter();
    //Manager
    DataTransferManager dataTransferManager = new DataTransferManagerImpl(DataTransferManager.ABORT_FILES);
    //Data struct
    AbortFilesInputData inputData;
    AbortFilesOutputData outputData;
    Map outputParam = new HashMap();
    //Convert data from xmlrpc to StoRM readble type
    inputData = converter.getAbortFilesInputData(inputParam);
    //Perform request
    outputData = dataTransferManager.abortFiles(inputData);
    //Convert data from StoRM type to xmlrpc
    outputParam = converter.getOutputParameter(outputData);

    //****** LOGs SYNCH OPERATION *********
     successResult = outputData.getReturnStatus().isSRM_SUCCESS();
    duration = System.currentTimeMillis() - startTime;
    logExecution(opType, dn, startTime, duration, successResult);
    //******************

     return outputParam;
  }

  public Map reserveSpace(Map inputParam) {
    //****** LOGs SYNCH OPERATION SETTING VAR*********
     OperationType opType = OperationType.RS;
    String dn = "synch";
    long startTime = System.currentTimeMillis();
    long duration = startTime;
    boolean successResult = true;
//******************


     log.debug("reserveSpace:Call received : Structure size = " + inputParam.size());
    log.debug("reserveSpace: Input Structure toString: " + inputParam.toString());

    //Converter used to obtain StoRM data from generic  xmlrpc structure
    ReserveSpaceConverter converter = new ReserveSpaceConverter();
    //Space Reservation Manager
    SpaceManager spaceManager = new SpaceManagerImpl(SpaceManager.RESERVESPACE); //SpaceReservationManager;
    //Creation of Space Reservation Manager Input Data
    ReserveSpaceInputData inputData;
    //OutputData returned from reserveSpace method
    ReserveSpaceOutputData outputData;
    //Output Hashtable structure used to return value to xmlrpc client
    Map outputParam = new HashMap();

    //
    //Parse Hashtable structure passed by xmlrpc call to obtain SpaceResData
    inputData = converter.getSpaceResInputData(inputParam);

    /* ************************************************* */
    /* Invokation of SrmReserveSpaceManager !!!!!!!!!!!! */
    /* ************************************************* */

    //Invocation of Space Reservation Manager
    outputData = spaceManager.reserveSpace(inputData);

    /* Creation of return structure outputParam */
    /* This structure contains elements by SRM V2.2 interface */
    outputParam = converter.getSpaceResOutputParameter(outputData);

    //****** LOGs SYNCH OPERATION *********
     successResult = outputData.getStatus().isSRM_SUCCESS();
    duration = System.currentTimeMillis() - startTime;
    logExecution(opType, dn, startTime, duration, successResult);
    //******************

     //Return Hashtable
     return outputParam;
  }

  /**
   * GetSpaceMetaData
   */
  public Map getSpaceMetaData(HashMap inputParam) {

    //****** LOGs SYNCH OPERATION SETTING VAR*********
     OperationType opType = OperationType.GSM;
    String dn = "synch";
    long startTime = System.currentTimeMillis();
    long duration = startTime;
    boolean successResult = true;
    //******************


     log.debug("SynchCallServer: getSpaceMetaData. Size Of received data: " + inputParam.size());
    log.debug("SynchCallServer: getSpaceMetaData. Received: " + inputParam.toString());

    // GetSpaceMetaDataManager Input Data
    GetSpaceMetaDataInputData inputData = null;
    //OutputData returned from reserveSpace method
    GetSpaceMetaDataOutputData outputData = null;
    //Output xmlrpcstructure to return to client
    Map outputParam = new HashMap();

    //Converter used to parse generic xmlrpc structure to StoRM type
    GetSpaceMetaDataConverter converter = new GetSpaceMetaDataConverter();

    //Obtain GetSpaceMetaDataInputData from xmlrp input hashtable
    inputData = converter.getGetSpaceMetaDataInputData(inputParam);

    //Manager
    SpaceManager manager = new SpaceManagerImpl(SpaceManager.GETSPACEMETA);

    //Create outputdata from persistence
    outputData = manager.getSpaceMetaData(inputData);

    /* Creation of return structure outputParam */
    /* This structure contains elements by SRM V2.2 interface for GetSpaceMetaData OutputStructure */
    outputParam = converter.getOutputParameter(outputData);

    //****** LOGs SYNCH OPERATION *********
     successResult = outputData.getStatus().isSRM_SUCCESS();
    duration = System.currentTimeMillis() - startTime;
    logExecution(opType, dn, startTime, duration, successResult);
    //******************

     //Return output Parameter structure
     return outputParam;
  }

  /**
   * GetSpaceTokens
   * @param inputParam
   * @return
   */
  public Map getSpaceTokens(Map inputParam) {
    //****** LOGs SYNCH OPERATION SETTING VAR*********
     OperationType opType = OperationType.GST;
    String dn = "synch";
    long startTime = System.currentTimeMillis();
    long duration = startTime;
    boolean successResult = true;
//******************


     log.debug("SynchCallServer: GetSpaceTokens. Size Of received data: " + inputParam.size());
    log.debug("SynchCallServer: GetSpaceTokens. Received: " + inputParam.toString());

    GetSpaceTokensInputData inputData = null;
    GetSpaceTokensOutputData outputData = null;
    // Output xmlrpcstructure to return to client
    Map outputParam = new HashMap();

    // Converter used to parse generic xmlrpc structure to StoRM type
    GetSpaceTokensConverter converter = new GetSpaceTokensConverter();
    inputData = converter.getGetSpaceTokensInputData(inputParam);

    // Manager
    SpaceManager manager = new SpaceManagerImpl(SpaceManager.GETSPACETOKENS);

    // Execute the request and create outputdata
    outputData = manager.getSpaceTokens(inputData);

    // Creation of return structure outputParam */
   outputParam = converter.getOutputParameter(outputData);

    //****** LOGs SYNCH OPERATION *********
     successResult = outputData.getStatus().isSRM_SUCCESS();
    duration = System.currentTimeMillis() - startTime;
    logExecution(opType, dn, startTime, duration, successResult);
    //******************

     return outputParam;
  }

  /**
   * ReleaseSpace
   */

  public Map ReleaseSpace(Map inputParam) {
    //****** LOGs SYNCH OPERATION SETTING VAR*********
     OperationType opType = OperationType.RSP;
    String dn = "synch";
    long startTime = System.currentTimeMillis();
    long duration = startTime;
    boolean successResult = true;
    //******************

     log.debug("releaseSpace:Call received : Structure size = " + inputParam.size());

    log.debug("releaseSpace: Input Structure toString: " + inputParam.toString());

    /* Creationd of SpaceResData, INPUT STRUCTURE for */
    /* SpaceReservationManager!			  */
    ReleaseSpaceInputData inputData = null;
    //OutputData FIXME WHEN ReleaseSPace OutputData done
    ReleaseSpaceOutputData outputData;
    //Output Structure to return to xmlrpc client
    Map outputParam;

    //Converter Used to obtain Storm Structure from general xmlrpc structure
    ReleaseSpaceConverter converter = new ReleaseSpaceConverter();

    //Obtain ReleaseSpaceData from generic xmlrpc input structure
    inputData = converter.getReleaseSpaceInputData(inputParam);

    //Manager
    SpaceManager manager = new SpaceManagerImpl(SpaceManager.RELEASESPACE);

    //Invokation of manger function
    outputData = manager.releaseSpace(inputData);

    //TODO Correct Creation of output Structure
    outputParam = converter.getOutputParameter(outputData);

    //****** LOGs SYNCH OPERATION *********
     successResult = outputData.getStatus().isSRM_SUCCESS();
    duration = System.currentTimeMillis() - startTime;
    logExecution(opType, dn, startTime, duration, successResult);
    //******************

     //Return output Parameter structure
     return outputParam;
  }

  /** SrmLs request.
   * This method catch an SrmLs request passed by StoRM  FrontEnd trough xmlrpc
   * communication. The Hastable is the default Java type used to represent structure
   * passed by xmlrpc.
   * @param Hastable output parameter structure returned.
   * @param inputParameter input parameter structure received from xmlrpc call.
   */

  public Map ls(Map inputParam) {
    //****** LOGs SYNCH OPERATION SETTING VAR*********
     OperationType opType = OperationType.LS;
    String dn = "synch";
    long startTime = System.currentTimeMillis();
    long duration = startTime;
    boolean successResult = true;
    //******************

     log.debug("SrmLs Call received : Structure size = " + inputParam.size());
    log.debug("SrmLs Sructure received : toString: " + inputParam.toString());

    //Input LSInputData structure
    LSInputData inputData;
    //OutputData LSOutputData Structure
    LSOutputData outputData;
    //Output Hashtable Structure to return to xmlrpc client
    Map outputParam;
    //Converter used to obtain StoRM type from generic xmlrpc Structure
    LSConverter converter = new LSConverter();

    // Directory Funtions Manager user for srmLs method
    DirectoryManager directoryManager = new DirectoryManagerImpl(DirectoryManager.LS);

    //Obtain LSInputData from generic xmlrpc structure
    inputData = converter.getLSInputData(inputParam);
    log.debug("LS:InputData Created!");

    //
    // Invocation of SrmLS function of DirectoryManager
    //

    log.debug("Invokation of DirFunctionManager");
    //OutputData returned from directory manager
    outputData = directoryManager.ls(inputData);
    log.debug("DirFunctManager returned");

    /* Creation of return structure outputParam */
    /* This structure contains elements by SRM V2.2 interface */
    outputParam = converter.getOutputParameter(outputData);
    log.debug("LS:OutputData Returned");

    log.debug("OUT: " + outputParam);

    //****** LOGs SYNCH OPERATION *********
     successResult = outputData.getStatus().isSRM_SUCCESS();
    duration = System.currentTimeMillis() - startTime;
    logExecution(opType, dn, startTime, duration, successResult);
//******************


     //Return global structure.
     return outputParam;

  }

  /**
   * SrmMkdir functionality.
   */

  public Map mkdir(Map inputParam) {
    //****** LOGs SYNCH OPERATION SETTING VAR*********
     OperationType opType = OperationType.MKD;
    String dn = "synch";
    long startTime = System.currentTimeMillis();
    long duration = startTime;
    boolean successResult = true;
//******************


     log.debug("Call received : Structure size = " + inputParam.size());
    log.debug("toString: " + inputParam.toString());

    //Mkdir InputData used to call DirectoryFunctionsManager
    MkdirInputData inputData;
    //OutputData returned from Mkdir method of DirectoryFunctionsManager (simple TReturnStatus)
    TReturnStatus outputData;
    //Output hashtable structure to return to xmlrpc client
    Map outputParam;
    //Converter used to obtain StoRM Internal type from generic xmlrpc structure
    MkdirConverter converter = new MkdirConverter();

    //Directory Functions Manger
    DirectoryManager directoryManager = new DirectoryManagerImpl(DirectoryManager.MKDIR);

    //
    //Creation of Mkdir Input Data from generic xmlrpc strucuture
    //
    inputData = converter.getMkdirInputData(inputParam);

    //Invocation
    outputData = directoryManager.mkdir(inputData);

    /* Creation of return structure outputParam */
    /* This structure contains elements by SRM V2.2 interface */
    outputParam = converter.getOutputParameter(outputData);

    //****** LOGs SYNCH OPERATION *********
     successResult = outputData.isSRM_SUCCESS();
    duration = System.currentTimeMillis() - startTime;
    logExecution(opType, dn, startTime, duration, successResult);
    //******************

     //Return Output Structure
     return outputParam;

  }

  /**
   * SrmRmdir functionality.
   */

  public Map rmdir(Map inputParam) {

    //****** LOGs SYNCH OPERATION SETTING VAR*********
     OperationType opType = OperationType.RMD;
    String dn = "synch";
    long startTime = System.currentTimeMillis();
    long duration = startTime;
    boolean successResult = true;
//******************

     log.debug("Call received : Structure size = " + inputParam.size());
    log.debug("toString: " + inputParam.toString());

    //InputData RmdirInputData uset for Rmdir method of DirectoryFunctionsManager
    RmdirInputData inputData;
    //Output data returned from Rmdir method (Simple TReturnStatus)
    TReturnStatus outputData;
    //Ouput Hashtable structure to return to xmlrpc client
    Map outputParam;
    //RmdirData Converter, used to obtain and create StoRM type from/to generic xmlrpc types
    RmdirConverter converter = new RmdirConverter();

    //Directory Manger
    DirectoryManager directoryManager = new DirectoryManagerImpl(DirectoryManager.RMDIR);

    //Creation of RmdirInputData from generic xmlrpc structure
    inputData = converter.getRmdirInputData(inputParam);

    //Invocation of Rmdir method of Directory Function Manager
    //Invocation
    outputData = directoryManager.rmdir(inputData);

    /* Creation of return structure outputParam */
    /* This structure contains elements by SRM V2.2 interface */
    outputParam = converter.getOutputParameter(outputData);

    //Logging operation for INFO level
    //StringBuffer buf = new StringBuffer("srmRm: ");
    //if(inputData!=null) {
    //	buf.append("<"+inputData.getUser()+"> ");
    //} else {
    //
    //}



    //****** LOGs SYNCH OPERATION *********
     successResult = outputData.isSRM_SUCCESS();
    duration = System.currentTimeMillis() - startTime;
    logExecution(opType, dn, startTime, duration, successResult);
//******************

     //Return Output Structure
     return outputParam;

  }

  /**
   * SrmRm functionality.
   */

  public Map rm(Map inputParam) {
    //****** LOGs SYNCH OPERATION SETTING VAR*********
     OperationType opType = OperationType.RM;
    String dn = "synch";
    long startTime = System.currentTimeMillis();
    long duration = startTime;
    boolean successResult = true;
//******************


     log.debug("Call received : Structure size = " + inputParam.size());
    log.debug("toString: " + inputParam.toString());

    //InputData RmInputData uset for Rm method of DirectoryFunctionsManager
    RmInputData inputData;
    //Output data returned from Rmdir method (Simple TReturnStatus)
    RmOutputData outputData;
    //Output Hashtable structure to return to xmlrpc client
    Map outputParam;
    //RmdirData Converter, used to obtain and create StoRM type from/to generic xmlrpc types
    RmConverter converter = new RmConverter();

    //Directory Manger
    DirectoryManager directoryManager = new DirectoryManagerImpl(DirectoryManager.RM);

    //Creation of RmdirInputData from generic xmlrpc structure
    inputData = converter.getRmInputData(inputParam);

    //Invocation of Rmdir method of Directory Function Manager
    //Invocation
    outputData = directoryManager.rm(inputData);

    /* Creation of return structure outputParam */
    /* This structure contains elements by SRM V2.2 interface */
    outputParam = converter.getOutputParameter(outputData);

    //****** LOGs SYNCH OPERATION *********
     successResult = outputData.getStatus().isSRM_SUCCESS();
    duration = System.currentTimeMillis() - startTime;
    logExecution(opType, dn, startTime, duration, successResult);
    //******************

     //Return Output Structure
     return outputParam;

  }

  /**
   * SrmMv functionality.
   */

  public Map mv(Map inputParam) {
    //****** LOGs SYNCH OPERATION SETTING VAR*********
     OperationType opType = OperationType.MV;
    String dn = "synch";
    long startTime = System.currentTimeMillis();
    long duration = startTime;
    boolean successResult = true;
//******************


     log.debug("Call received : Structure size = " + inputParam.size());
    log.debug("toString: " + inputParam.toString());

    //InputData MvInputData uset for Mv method of DirectoryFunctionsManager
    MvInputData inputData;
    //Output data returned from Mv method (Simple TReturnStatus)
    MvOutputData outputData;
    //Output Hashtable structure to return to xmlrpc client
    Map outputParam;
    //MvData Converter, used to obtain and create StoRM type from/to generic xmlrpc types
    MvConverter converter = new MvConverter();

    //Directory Manger
    DirectoryManager directoryManager = new DirectoryManagerImpl(DirectoryManager.MV);

    //Creation of MvInputData from generic xmlrpc structure
    inputData = converter.getMvInputData(inputParam);

    //Invocation of Mv method of Directory Function Manager
    //Invocation
    outputData = directoryManager.mv(inputData);

    /* Creation of return structure outputParam */
    /* This structure contains elements by SRM V2.2 interface */
    outputParam = converter.getOutputParameter(outputData);

    //****** LOGs SYNCH OPERATION *********
     successResult = outputData.getStatus().isSRM_SUCCESS();
    duration = System.currentTimeMillis() - startTime;
    logExecution(opType, dn, startTime, duration, successResult);
    //******************

     //Return Output Structure
     return outputParam;

  }

  public void createServer() {
    try {

      //
      // WebServer is a sample http server
      // Used to provide xmlRpcServer on http
      // XmlRpc serve is multithreded, one thred for each
      // Connection requested.
      // Default MAX_NUMBER of thread is 100, but can be changed.
      //

      // TODO
      // Default port is 8080
      // Getting the port from Confiuration file!
      _port = config.getXmlRpcServerPort();

      //Invoke me as <http://localhost:port/RPC2>
      log.info("[xmlrpc server] Starting server on port: " + _port);
      WebServer xmlrpcWebServer = new WebServer(_port);

      //Add Client Filtering
      log.info("[xmlrpc server] IP requests filtering enabled.");
      //xmlrpcWebServer.setParanoid(true);

      //By default the localhost interface is enabled.
      //Accept Client only from loopback inerface
      log.info("[xmlrpc server] Accepting requests from: 127.0.0.1");
      //xmlrpcWebServer.acceptClient("127.0.0.1");

      //Get List of machine names that host a FE service
      //for this StoRM instance

      fe_hostname_array = config.getListOfMachineNames();
      //For each host name set the IP as authorized
      HostLookup hl = new HostLookup();
      for (Iterator it = fe_hostname_array.iterator(); it.hasNext(); ) {
        String hostname = (String) it.next();
        try {
          String IP = hl.lookup(hostname);
          log.info("[xmlrpc server] Accepting requests from " + IP + " (" + hostname + ")");
          //xmlrpcWebServer.acceptClient(IP);
        }
        catch (UnknownHostException ex) {
          log.warn("Synchserver: IP for hostname: " + hostname + " cannot be resolved.");
        }
      }

      /**
       * Dynamic DNS for the FE machine.
       * Where a dynamic DNS is used to load balancing on the FE machines,
       * the hostname IP lookup above cannot be used.
       * In that case the configuration properties files must contains
       * the set of the specific IPs for the frontend machines.
       * In that way, the xmlrpc server can be configured to
       * accept request from each particulare FE machine.
       */

      fe_IP_array = config.getListOfMachineIPs();
      //For each host name set the IP as authorized
      for (Iterator it = fe_IP_array.iterator(); it.hasNext(); ) {
        String IP = (String) it.next();
        log.info("[xmlrpc server] Accepting requests from " + IP);
        //xmlrpcWebServer.acceptClient(IP);
      }

      XmlRpcServer xmlRpcServer = xmlrpcWebServer.getXmlRpcServer();
      PropertyHandlerMapping phm = new PropertyHandlerMapping();

      //use reflection for (dynamic) mapping
      //DynamicHandlerMapping dhm = new DynamicHandlerMapping(new  TypeConverterFactoryImpl(), true);

      //xmlrpcWebServer.getXmlRpcServer().setMaxThreads(arg0);

      // Add handler
      //xmlrpcWebServer.addHandler("synchcall", new SynchCallServer());
      SynchCallServer server = new SynchCallServer();
      phm.addHandler("synchcall", SynchCallServer.class);

      //Set the number of thread to use
      //xmlrpcWebServer.
      xmlRpcServer.setHandlerMapping(phm);

      //Set Number of thread
      //Get from configuration FILE
      int num_thread = config.getMaxXMLRPCThread();
      xmlRpcServer.setMaxThreads(num_thread);

      // Starting xmlrpc server
      xmlrpcWebServer.start();
      //Insecure...
      log.info("[xmlrpc server] Server running...");

      //
      // Secure WebServer is a WebServer with SSL support.
      // As server certificate can be used the host certificate.
      // (or can be requestes a service certifcate, but is not necessary...)
      //

      //TODO SECURE SERVER
      //Default port for secure version is 80443
      //Get port number from configuration file
      /*			_secure_port = config.getSecureXmlRpcServerPort();
       log.debug("Starting Secure Server server on port: "+_secure_port);
       SecureWebServer xmlrpcServer_secure = new SecureWebServer(_secure_port);

       //Debug Print for Secure Server Attributes

       log.debug("Working Directory:"+System.getProperty("user.dir"));
       log.debug("getKeyManagerType(): "+SecurityTool.getKeyManagerType());
       log.debug("getKeyStore(): "+SecurityTool.getKeyStore());
       SecurityTool.setKeyStore("fake_cert");
       //Setting path fpor the host cert
       //SecurityTool.setKeyStore("hostcert");

       log.debug("After Setting: getKeyStore(): "+SecurityTool.getKeyStore());

       log.debug("getKeyStorePassword(): "+SecurityTool.getKeyStorePassword() );
       SecurityTool.setKeyStorePassword("fake_cert");


       log.debug("After Setting getKeyStorePassword(): "+SecurityTool.getKeyStorePassword() );


       log.debug("getKeyStoreType(): "+SecurityTool.getKeyStoreType() );
       log.debug("getProtocolHandlerPackages(): "+SecurityTool.getProtocolHandlerPackages());
       log.debug("getSecurityProtocol(): "+SecurityTool.getSecurityProtocol() );
       log.debug("getSecurityProviderClass(): "+SecurityTool.getSecurityProviderClass());

       log.debug("getTrustStore() : "+SecurityTool.getTrustStore() );
       //SecurityTool.setTrustStore("");
       log.debug("After Setting:getTrustStore() : "+SecurityTool.getTrustStore() );

       log.debug("getTrustStorePassword() : "+SecurityTool.getTrustStorePassword() );
       //SecurityTool.setTrustStorePassword("ciccio");
       log.debug("After Setting:getTrustStorePassword() : "+SecurityTool.getTrustStorePassword());

       log.debug("getTrustStoreType() : "+SecurityTool.getTrustStoreType() );



       //
       // Adding handler on SecureServer
       //

       xmlrpcServer_secure.addHandler("synchcall", new SynchCallServer());
       //Starting secure Server
       xmlrpcServer_secure.start();
       log.debug("server secure running...");

       */

    }
    catch (Exception ServerStartError) {
      log.error("SynchCallServer " + ServerStartError.toString());
    }
  }

  /**
   * Method used to book the execution of SYNCH operation
   */
  public void logExecution(OperationType opType, String dn, long startTime, long duration, boolean successResult) {
    BookKeeper bk = HealthDirector.getHealthMonitor().getBookKeeper();
    LogEvent event = new LogEvent(opType, dn, startTime, duration, successResult);
    bk.addLogEvent(event);
  }

  public static void main(String[] args) {
    SynchCallServer s = new SynchCallServer();
    s.createServer();
  }

}
