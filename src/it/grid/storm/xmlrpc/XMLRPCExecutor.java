package it.grid.storm.xmlrpc;

import it.grid.storm.health.BookKeeper;
import it.grid.storm.health.HealthDirector;
import it.grid.storm.health.LogEvent;
import it.grid.storm.common.OperationType;
import it.grid.storm.synchcall.SynchcallDispatcher;
import it.grid.storm.synchcall.SynchcallDispatcherFactory;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.xmlrpc.converter.Converter;
import it.grid.storm.xmlrpc.converter.ConveterFactory;

import java.util.Map;

import org.apache.log4j.Logger;

/**
 * This class is part of the StoRM project.
 * 
 * @author lucamag
 * @date May 27, 2008
 * 
 */

public class XMLRPCExecutor {

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger("synch_xmlrpc_server");

    /**
     * @param type
     * @param inputParam
     * @return
     */

    public Map execute(OperationType type, Map inputParam) {

        // ****** LOGs SYNCH OPERATION SETTING VAR*********
        OperationType opType = type;
        String dn = "synch";
        long startTime = System.currentTimeMillis();
        long duration = startTime;
        boolean successResult = true;
        // ******************

        log.debug("Call received : Structure size = " + inputParam.size());
        log.debug("Call Type: "+type.toString());
        log.debug("toString: " + inputParam.toString());

        // InputData RmInputData uset for Rm method of DirectoryFunctionsManager
        InputData inputData;
        // Output data returned from Rmdir method (Simple TReturnStatus)
        OutputData outputData;
        // Output Hashtable structure to return to xmlrpc client
        Map outputParam;
        // RmdirData Converter, used to obtain and create StoRM type from/to
        // generic xmlrpc types
        Converter converter = ConveterFactory.getConverter(type);
        
        assert(converter!=null): "CONVERTER_IS_NULL";
        
        // Synchcall dispatcher da factory
        
        SynchcallDispatcher dispatcher = SynchcallDispatcherFactory.getDispatcher();
        
        log.debug("Converter");
        // Creation of RmdirInputData from generic xmlrpc structure
        inputData = converter.convertToInputData(inputParam);
        log.debug("Dispatcher");
        // Invocation of Rmdir method of Directory Function Manager
        // Invocation
        outputData = dispatcher.processRequest(type, inputData);

        /* Creation of return structure outputParam */
        /* This structure contains elements by SRM V2.2 interface */
        outputParam = converter.convertFromOutputData(outputData);

        // ****** LOGs SYNCH OPERATION *********
        successResult = outputData.isSuccess();

        duration = System.currentTimeMillis() - startTime;
        //logExecution(opType, dn, startTime, duration, successResult);
        // ******************

        // Return Output Structure
        return outputParam;
    }

    /**
     * Method used to book the execution of SYNCH operation
     */
    public void logExecution(it.grid.storm.health.OperationType opType, String dn, long startTime,
            long duration, boolean successResult) {
        BookKeeper bk = HealthDirector.getHealthMonitor().getBookKeeper();
        LogEvent event = new LogEvent(opType, dn, startTime, duration,
                successResult);
        bk.addLogEvent(event);
    }

}
