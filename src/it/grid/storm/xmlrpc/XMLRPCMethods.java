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

package it.grid.storm.xmlrpc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.common.TypeConverterFactoryImpl;
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
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.dataTransfer.*;
import it.grid.storm.xmlrpc.converter.Converter;
import it.grid.storm.xmlrpc.converter.ConveterFactory;
import it.grid.storm.health.BookKeeper;
import it.grid.storm.health.HealthDirector;
import it.grid.storm.common.OperationType;
import it.grid.storm.health.LogEvent;

public class XMLRPCMethods {
    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger("synch_xmlrpc_server");
    private final Configuration config;
    

    private final ConveterFactory converterFactory = new ConveterFactory();

    private final XMLRPCExecutor executor = new XMLRPCExecutor();

    public XMLRPCMethods() {
        config = Configuration.getInstance();

    };

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
    
        return executor.execute(OperationType.PNG, inputParam);

    }

    public Map putDone(Map inputParam) {

        // ****** LOGs SYNCH OPERATION SETTING VAR*********
        OperationType opType = OperationType.PD;
        String dn = "synch";
        long startTime = System.currentTimeMillis();
        long duration = startTime;
        boolean successResult = true;
        // ******************

        log.debug("putDone: Call received : Structure size = "
                + inputParam.size());
        log
                .debug("putDone: Input Structure toString: "
                        + inputParam.toString());

        PutDoneConverter converter = new PutDoneConverter();
        DataTransferManager dataTransferManager = new DataTransferManagerImpl(
                DataTransferManager.PUTDONE);
        PutDoneInputData inputData;
        PutDoneOutputData outputData;
        Map outputParam = new HashMap();

        inputData = converter.getPutDoneInputData(inputParam);
        outputData = dataTransferManager.putDone(inputData);
        outputParam = converter.getOutputParameter(outputData);

        // ****** LOGs SYNCH OPERATION *********
        successResult = outputData.getReturnStatus().isSRM_SUCCESS();
        duration = System.currentTimeMillis() - startTime;
        logExecution(opType, dn, startTime, duration, successResult);
        // ******************

        return outputParam;
    }

    public Map releaseFiles(Map inputParam) {
        // ****** LOGs SYNCH OPERATION SETTING VAR*********
        OperationType opType = OperationType.RF;
        String dn = "synch";
        long startTime = System.currentTimeMillis();
        long duration = startTime;
        boolean successResult = true;
        // ******************

        log.debug("releaseFiles: Call received : Structure size = "
                + inputParam.size());
        log.debug("releaseFiles: Input Structure toString: "
                + inputParam.toString());

        ReleaseFilesConverter converter = new ReleaseFilesConverter();
        DataTransferManager dataTransferManager = new DataTransferManagerImpl(
                DataTransferManager.RELEASEFILES);
        ReleaseFilesInputData inputData;
        ReleaseFilesOutputData outputData;
        Map outputParam = new HashMap();

        inputData = converter.getReleaseFilesInputData(inputParam);
        outputData = dataTransferManager.releaseFiles(inputData);
        outputParam = converter.getOutputParameter(outputData);

        // ****** LOGs SYNCH OPERATION *********
        successResult = outputData.getReturnStatus().isSRM_SUCCESS();
        duration = System.currentTimeMillis() - startTime;
        logExecution(opType, dn, startTime, duration, successResult);
        // ******************

        return outputParam;
    }

    public Map extendFileLifeTime(Map inputParam) {

        // ****** LOGs SYNCH OPERATION SETTING VAR*********
        OperationType opType = OperationType.EFL;
        String dn = "synch";
        long startTime = System.currentTimeMillis();
        long duration = startTime;
        boolean successResult = true;
        // ******************

        log.debug("extendFileLifeTime: Call received : Structure size = "
                + inputParam.size());
        log.debug("extendFileLifeTime: Input Structure toString: "
                + inputParam.toString());

        ExtendFileLifeTimeConverter converter = new ExtendFileLifeTimeConverter();
        DataTransferManager dataTransferManager = new DataTransferManagerImpl(
                DataTransferManager.EXTENDFILELIFETIME);
        ExtendFileLifeTimeInputData inputData;
        ExtendFileLifeTimeOutputData outputData;
        Map outputParam = new HashMap();

        inputData = converter.getExtendFileLifeTimeInputData(inputParam);
        outputData = dataTransferManager.extendFileLifeTime(inputData);
        outputParam = converter.getOutputParameter(outputData);

        // ****** LOGs SYNCH OPERATION *********
        successResult = outputData.getReturnStatus().isSRM_SUCCESS();
        duration = System.currentTimeMillis() - startTime;
        logExecution(opType, dn, startTime, duration, successResult);
        // ******************

        return outputParam;
    }

    public Map abortRequest(Map inputParam) {

        // ****** LOGs SYNCH OPERATION SETTING VAR*********
        OperationType opType = OperationType.AR;
        String dn = "synch";
        long startTime = System.currentTimeMillis();
        long duration = startTime;
        boolean successResult = true;
        // ******************

        log.debug("abortRequest: Call received : Structure size = "
                + inputParam.size());
        log.debug("abortRequest: Input Structure toString: "
                + inputParam.toString());
        // Converter
        AbortRequestConverter converter = new AbortRequestConverter();
        // Manager
        DataTransferManager dataTransferManager = new DataTransferManagerImpl(
                DataTransferManager.ABORT_REQUEST);
        // Data structure
        AbortRequestInputData inputData;
        AbortRequestOutputData outputData;
        Map outputParam = new HashMap();
        // Convert data from xmlrpc struct to StoRM types
        inputData = converter.getAbortRequestInputData(inputParam);
        // Performs request
        outputData = dataTransferManager.abortRequest(inputData);
        // Convert data from StoRM type to xmlrpc struct.
        outputParam = converter.getOutputParameter(outputData);

        // ****** LOGs SYNCH OPERATION *********
        successResult = outputData.getReturnStatus().isSRM_SUCCESS();
        duration = System.currentTimeMillis() - startTime;
        logExecution(opType, dn, startTime, duration, successResult);
        // ******************

        return outputParam;
    }

    public Map abortFiles(HashMap inputParam) {
        // ****** LOGs SYNCH OPERATION SETTING VAR*********
        OperationType opType = OperationType.AF;
        String dn = "synch";
        long startTime = System.currentTimeMillis();
        long duration = startTime;
        boolean successResult = true;
        // ******************

        log.debug("abortFiles: Call received : Structure size = "
                + inputParam.size());
        log.debug("abortFiles: Input Structure toString: "
                + inputParam.toString());
        // Converter
        AbortFilesConverter converter = new AbortFilesConverter();
        // Manager
        DataTransferManager dataTransferManager = new DataTransferManagerImpl(
                DataTransferManager.ABORT_FILES);
        // Data struct
        AbortFilesInputData inputData;
        AbortFilesOutputData outputData;
        Map outputParam = new HashMap();
        // Convert data from xmlrpc to StoRM readble type
        inputData = converter.getAbortFilesInputData(inputParam);
        // Perform request
        outputData = dataTransferManager.abortFiles(inputData);
        // Convert data from StoRM type to xmlrpc
        outputParam = converter.getOutputParameter(outputData);

        // ****** LOGs SYNCH OPERATION *********
        successResult = outputData.getReturnStatus().isSRM_SUCCESS();
        duration = System.currentTimeMillis() - startTime;
        logExecution(opType, dn, startTime, duration, successResult);
        // ******************

        return outputParam;
    }

    public Map reserveSpace(Map inputParam) {
        
        return executor.execute(OperationType.RESSP, inputParam);
    }

    /**
     * GetSpaceMetaData
     */
    public Map getSpaceMetaData(HashMap inputParam) {
        
        return executor.execute(OperationType.GSM, inputParam);
        
    }

    /**
     * GetSpaceTokens
     * 
     * @param inputParam
     * @return
     */
    public Map getSpaceTokens(Map inputParam) {
        
        return executor.execute(OperationType.GST, inputParam);
     
    }

    /**
     * ReleaseSpace
     */

    public Map ReleaseSpace(Map inputParam) {
        
        return executor.execute(OperationType.RELSP, inputParam);
        
        
    }

    /**
     * SrmLs request. This method catch an SrmLs request passed by StoRM
     * FrontEnd trough xmlrpc communication. The Hastable is the default Java
     * type used to represent structure passed by xmlrpc.
     * 
     * @param Hastable
     *            output parameter structure returned.
     * @param inputParameter
     *            input parameter structure received from xmlrpc call.
     */

    public Map ls(Map inputParam) {
        
        log.debug("ls chiamata");

        return executor.execute(OperationType.LS, inputParam);


    }

    /**
     * SrmMkdir functionality.
     */

    public Map mkdir(Map inputParam) {
    
        return executor.execute(OperationType.MKD, inputParam);
    
    }

    /**
     * SrmRmdir functionality.
     * @param inputParam
     * @return
     */
    public Map rmdir(Map inputParam) {
        
        return executor.execute(OperationType.RMD, inputParam);

    }

    /**
     * SrmRm functionality.
     * @param inputParam
     * @return
     */
    public Map rm(Map inputParam) {

        return executor.execute(OperationType.RM, inputParam);

    }

    /**
     * SrmMv functionality.
     */

    public Map mv(Map inputParam) {
        
        return executor.execute(OperationType.MV, inputParam);
        

    }

    /**
     * Method used to book the execution of SYNCH operation
     */
    public void logExecution(OperationType opType, String dn, long startTime,
            long duration, boolean successResult) {
        BookKeeper bk = HealthDirector.getHealthMonitor().getBookKeeper();
       // LogEvent event = new LogEvent(opType, dn, startTime, duration,
       //         successResult);
       // bk.addLogEvent(event);
    }

}
