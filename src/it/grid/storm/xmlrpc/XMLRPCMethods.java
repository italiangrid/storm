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

import it.grid.storm.common.OperationType;
import it.grid.storm.config.Configuration;
import it.grid.storm.xmlrpc.converter.ConveterFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLRPCMethods {
    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(XMLRPCMethods.class);
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

        return executor.execute(OperationType.PD, inputParam);

    }

    public Map releaseFiles(Map inputParam) {

        return executor.execute(OperationType.RF, inputParam);

    }

    public Map extendFileLifeTime(Map inputParam) {

        return executor.execute(OperationType.EFL, inputParam);

    }

    public Map abortRequest(Map inputParam) {

        return executor.execute(OperationType.AR, inputParam);

    }

    public Map abortFiles(HashMap inputParam) {

        return executor.execute(OperationType.AF, inputParam);

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
    /**
    public void logExecution(OperationType opType, String dn, long startTime,
            long duration, boolean successResult) {
        BookKeeper bk = HealthDirector.getHealthMonitor().getBookKeeper();
       // LogEvent event = new LogEvent(opType, dn, startTime, duration,
       //         successResult);
       // bk.addLogEvent(event);
    }
     **/

}
