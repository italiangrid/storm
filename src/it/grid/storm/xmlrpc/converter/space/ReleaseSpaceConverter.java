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

package it.grid.storm.xmlrpc.converter.space;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.srm.types.InvalidArrayOfTExtraInfoAttributeException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.exception.InvalidReleaseSpaceAttributesException;
import it.grid.storm.synchcall.data.space.ReleaseSpaceInputData;
import it.grid.storm.synchcall.data.space.ReleaseSpaceOutputData;
import it.grid.storm.xmlrpc.converter.Converter;
import it.grid.storm.xmlrpc.converter.ParameterDisplayHelper;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents the Type Converter for ReleaseSpace function .
 * This class have get an input data from xmlrpc call anc convert it into a
 * StoRM Type that can be used to invoke the ReleaseSpaceManager
 *
 * @author  Magnoni Luca
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */

public class ReleaseSpaceConverter implements Converter {
    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(ReleaseSpaceConverter.class);

    public ReleaseSpaceConverter()
    {};

    /** This method return a ReleaseSpaceInputData
     * created from input Hashtable structure of an xmlrpc releaseSpace v2.1 call.
     *  ReleaseSpaceInputData can be used to invoke ReleaseSpace Manager
     */


    public InputData convertToInputData(Map inputParam)
    {
        log.debug("ReleaseSpaceConverter :Call received :Creation of SpaceResData = "+inputParam.size());
        log.debug("ReleaseSpaceConverter: Input Structure toString: "+ParameterDisplayHelper.display(inputParam));

        /* Creationd of SpaceResData, INPUT STRUCTURE for */
        /* SpaceReservationManager!			  */
        ReleaseSpaceInputData inputData = null;

        // Member name definition for inputParam struct , from SRM V2.1
        String member_authID = new String("authorizationID");
        String member_token = new String("spaceToken");
        String member_sysInfo = new String("storageSystemInfo");
        String member_force = new String("forceFileRelease");

        // String member_DN = new String("authorizationID");

        /* Get parameter value from struct, if defined! */
        /* Creation of GridUser into inputStructure */
        GridUserInterface guser = null;
        guser = GridUserManager.decode(inputParam);
        //guser = VomsGridUser.decode(inputParam);

        /* (1) authorizationID (never used) */
        String authID = (String) inputParam.get(member_authID);

        /* (2) spaceToken */
        TSpaceToken spaceToken = TSpaceToken.decode(inputParam,TSpaceToken.PNAME_SPACETOKEN);

        /*
        String tokenString = (String)inputParam.get(member_token);
    	if (tokenString!=null) {
    	    //LOG4J is better...
    	    log.debug("ReleaseSpace:SpaceToken: "+tokenString);
    	    //Creation of srm TSpaceToken
    	    try {
    		spaceToken = TSpaceToken.make(tokenString);
    	    }
    	    catch (InvalidTSpaceTokenAttributesException e) {
    		log.warn("Error creating TSpaceToken:"+e);
    	    }
    	}
         */

        /* (3) StorageSystemInfo */
        ArrayOfTExtraInfo storageSystemInfo;
        try {
            storageSystemInfo = ArrayOfTExtraInfo.decode(inputParam, ArrayOfTExtraInfo.PNAME_STORAGESYSTEMINFO);
        }
        catch (InvalidArrayOfTExtraInfoAttributeException e) {
            storageSystemInfo = null;
        }

        /* (4) ForceFileRelease */
        Boolean force = (Boolean)inputParam.get(member_force);
        if (force == null) {
            force = new Boolean(false);
        }

        /* Creation of ReleaseSpaceInputStructure */
        try {
            inputData = new ReleaseSpaceInputData(guser, spaceToken, storageSystemInfo, force.booleanValue());
        }
        catch (InvalidReleaseSpaceAttributesException e) {
            log.error("Error Creating inputData for ReleaseSpace"+e);
        }

        //Return ReleaseSpaceData Data Created
        return inputData;

    }


    public Map convertFromOutputData(OutputData data) {
        log.debug("releaseSpaceConverter :Call received :Creation of XMLRPC Output Structure! ");
        ReleaseSpaceOutputData outputData = (ReleaseSpaceOutputData) data;

        //Creation of new Hashtable to return
        Map outputParam = new HashMap();

        TReturnStatus returnStatus = outputData.getStatus();
        returnStatus.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);

        //Return output Parameter structure
        return outputParam;
    }
}
