/**
 * This class represents the Type Converter for GetSpaceMetaData function .
 * This class have get an input data from xmlrpc call anc convert it into a
 * StoRM Type that can be used to invoke the GetSpaceMetaDataManager
 *
 * @author  Magnoni Luca
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.synchcall.space;

import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;

import it.grid.storm.srm.types.*;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.VomsGridUser;

public class GetSpaceMetaDataConverter
{
    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger("synch_xmlrpc_server");

    public GetSpaceMetaDataConverter()
    {
    };

    /**
     * This method return a SpaceResData created from input Hashtable structure
     * of an xmlrpc spaceReservation v2.2 call. SpaceResData can be used to
     * invoke SpaceResevation Manager
     */
    public GetSpaceMetaDataInputData getGetSpaceMetaDataInputData(Map inputParam)
    {
        GetSpaceMetaDataInputData inputData = null;

        String memberName = null;

        /* Creation of VomsGridUser */
        GridUserInterface guser = null;
        guser = VomsGridUser.decode(inputParam);

        /* (1) authorizationID (never used) */
        memberName = new String("authorizationID");
        String authID = (String) inputParam.get(memberName);

        /* (2) arrayOfSpaceTokens */
        ArrayOfTSpaceToken arrayOfSpaceTokens;
        try {
            arrayOfSpaceTokens = ArrayOfTSpaceToken.decode(inputParam,
                            ArrayOfTSpaceToken.PNAME_ARRAYOFSPACETOKENS);
        } catch (InvalidArrayOfTSpaceTokenAttributeException e) {
            arrayOfSpaceTokens = null;
        }

        try {
            inputData = new GetSpaceMetaDataInputData((VomsGridUser) guser, arrayOfSpaceTokens);
        } catch (InvalidGetSpaceMetaDataInputAttributeException e) {
            log.error("Error Creating inputData for GetSpaceMetaDataManager" + e);
        }

        // Return GetSpaceMetaDataInputData Created
        return inputData;
    }

    public Map getOutputParameter(GetSpaceMetaDataOutputData outputData)
    {
        log.debug("GetSpaceMetaDataConverter: Creation of XMLRPC Output Structure! ");

        // Creation of new Hashtable to return
        Hashtable outputParam = new Hashtable();

        /* (1) returnStatus */
        TReturnStatus returnStatus = outputData.getStatus();
        if (returnStatus != null) {
            returnStatus.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);
        }

        /* (2) arrayOfSpaceDetails */
        ArrayOfTMetaDataSpace arrayOfSpaceDetails = outputData.getMetaDataSpaceArray();
        if (arrayOfSpaceDetails != null) {
            arrayOfSpaceDetails.encode(outputParam, ArrayOfTMetaDataSpace.PNAME_ARRAYOFSPACEDETAILS);
        }

        log.debug(outputParam.toString());

        //Return output Parameter structure
        return outputParam;
    }
}
