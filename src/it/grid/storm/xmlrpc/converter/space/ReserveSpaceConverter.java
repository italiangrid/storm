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
 * This class represents the Type Converter for space Reservation function .
 * This class have get an input data from xmlrpc call anc convert it into a
 * StoRM Type that can be used to invoke the space Reservation Manager
 *
 * @author  Magnoni Luca
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.xmlrpc.converter.space;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.srm.types.InvalidArrayOfTExtraInfoAttributeException;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRetentionPolicyInfo;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.space.AnonymousReserveSpaceInputData;
import it.grid.storm.synchcall.data.space.IdentityReserveSpaceInputData;
import it.grid.storm.synchcall.data.space.ReserveSpaceInputData;
import it.grid.storm.synchcall.data.space.ReserveSpaceOutputData;
import it.grid.storm.xmlrpc.converter.Converter;
import it.grid.storm.xmlrpc.converter.ParameterDisplayHelper;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReserveSpaceConverter implements Converter {

    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(ReserveSpaceConverter.class);

    public ReserveSpaceConverter()
    {};

    /** This method return a SpaceResData created from input Hashtable structure of an xmlrpc spaceReservation v2.1 call.
     *  SpaceResData can be used to invoke SpaceResevation Manager
     */
    public InputData convertToInputData(Map inputParam)    {

        log.debug("reserveSpaceConverter :Call received :Creation of SpaceResData = "+inputParam.size());
        log.debug("reserveSpaceConverter: Input Structure toString: "+ParameterDisplayHelper.display(inputParam));

        String memberName = null;

        GridUserInterface guser = GridUserManager.decode(inputParam);

        memberName = new String("authorizationID");
        String authID = (String) inputParam.get(memberName);

        memberName = new String("userSpaceTokenDescription");
        String spaceAlias = (String) inputParam.get(memberName);
        if (spaceAlias == null) {
            spaceAlias = new String("");
        }

        TRetentionPolicyInfo retentionPolicyInfo = TRetentionPolicyInfo.decode(inputParam, TRetentionPolicyInfo.PNAME_retentionPolicyInfo);

        TSizeInBytes desiredSizeOfTotalSpace = TSizeInBytes.decode(inputParam, TSizeInBytes.PNAME_DESIREDSIZEOFTOTALSPACE);

        TSizeInBytes desiredSizeOfGuaranteedSpace = TSizeInBytes.decode(inputParam, TSizeInBytes.PNAME_DESIREDSIZEOFGUARANTEEDSPACE);

        ArrayOfTExtraInfo storageSystemInfo;
        try {
            storageSystemInfo = ArrayOfTExtraInfo.decode(inputParam, ArrayOfTExtraInfo.PNAME_STORAGESYSTEMINFO);
        }
        catch (InvalidArrayOfTExtraInfoAttributeException e) {
            storageSystemInfo = null;
        }
        
        ReserveSpaceInputData inputData;
        if(guser != null)
        {
            inputData = new IdentityReserveSpaceInputData(guser, spaceAlias, retentionPolicyInfo, desiredSizeOfTotalSpace,
                                                          desiredSizeOfGuaranteedSpace, storageSystemInfo);            
        }
        else
        {
            inputData = new AnonymousReserveSpaceInputData(spaceAlias, retentionPolicyInfo, desiredSizeOfTotalSpace,
                                                          desiredSizeOfGuaranteedSpace, storageSystemInfo);
        }
        TLifeTimeInSeconds desiredLifetimeOfReservedSpace = TLifeTimeInSeconds.decode(inputParam, TLifeTimeInSeconds.PNAME_DESIREDLIFETIMEOFRESERVEDSPACE);
        if(desiredLifetimeOfReservedSpace != null && !desiredLifetimeOfReservedSpace.isEmpty())
        {
            inputData.setSpaceLifetime(desiredLifetimeOfReservedSpace); 
        }
        return inputData;

    }


    public Map convertFromOutputData(OutputData data)
    {
        log.debug("reserveSpaceConverter :Call received :Creation of XMLRPC Output Structure! ");

        //Creation of new Hashtable to return
        Map outputParam = new HashMap();

        ReserveSpaceOutputData outputData = (ReserveSpaceOutputData) data;

        /* (1) returnStatus */
        TReturnStatus returnStatus = outputData.getStatus();
        returnStatus.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);

        /* (2) requestToken */
        /* Actually we are not planning an asynchronous version of ReserveSpace (in theory not needed for StoRM).
         * Therefor this parameter is not set.
         */

        /* (3) estimatedProcessingTime */
        // TODO: in the future (actually the FE is predisposed to decode this value as an int).

        /* (4) retentionPolocyInfo */
        TRetentionPolicyInfo retentionPolicyInfo = outputData.getRetentionPolicyInfo();
        if (retentionPolicyInfo != null) {
            retentionPolicyInfo.encode(outputParam, TRetentionPolicyInfo.PNAME_retentionPolicyInfo);
        }

        /* (5) sizeOfTotalReservedSpace */
        TSizeInBytes sizeOfTotalReservedSpace = outputData.getTotalSize();
        if (sizeOfTotalReservedSpace != null) {
            if (!(sizeOfTotalReservedSpace.isEmpty())) {
                sizeOfTotalReservedSpace.encode(outputParam, TSizeInBytes.PNAME_SIZEOFTOTALRESERVEDSPACE);
            }
        }

        /* (6) sizeOfGuaranteedReservedSpace */
        TSizeInBytes sizeOfGuaranteedReservedSpace = outputData.getGuaranteedSize();
        if (sizeOfGuaranteedReservedSpace != null) {
            if (!(sizeOfGuaranteedReservedSpace.isEmpty())) {
                sizeOfGuaranteedReservedSpace.encode(outputParam, TSizeInBytes.PNAME_SIZEOFGUARANTEEDRESERVEDSPACE);
            }
        }

        /* (7) lifetimeOfReservedSpace */
        TLifeTimeInSeconds lifetimeOfReservedSpace = outputData.getLifeTimeInSeconds();
        if (lifetimeOfReservedSpace != null) {
            if (!(lifetimeOfReservedSpace.isEmpty())) {
                lifetimeOfReservedSpace.encode(outputParam, TLifeTimeInSeconds.PNAME_LIFETIMEOFRESERVEDSPACE);
            }
        }

        /* (8) spaceToken */
        TSpaceToken spaceToken = outputData.getSpaceToken();
        if (spaceToken != null) {
            spaceToken.encode(outputParam, TSpaceToken.PNAME_SPACETOKEN);
        }

        log.debug(outputParam.toString());

        return outputParam;
    }
}
