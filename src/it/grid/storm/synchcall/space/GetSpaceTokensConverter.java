/**
 * This class represents the Type Converter for GetSpaceTokens function.
 * This class gets input data from xmlrpc call and converts it into a
 * StoRM Type that can be used to invoke the GetSpaceTokensExecutor
 *
 * @author  Alberto Forti
 * @author  CNAF -INFN Bologna
 * @date    November 2006
 * @version 1.0
 */

package it.grid.storm.synchcall.space;

import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;

import it.grid.storm.srm.types.*;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.VomsGridUser;

public class GetSpaceTokensConverter
{
    // Logger
    private static final Logger log = Logger.getLogger("synch_xmlrpc_server");

    public GetSpaceTokensConverter() {};

    /**
     * Returns an instance of GetSpaceTokenInputData from a Hashtable structure
     * created by a xmlrpc GetSpaceTokens v2.2 call.
     */
    public GetSpaceTokensInputData getGetSpaceTokensInputData(Map inputParam)
    {
        GetSpaceTokensInputData inputData = null;

        String memberName = null;

        /* Creation of VomsGridUser */
        GridUserInterface guser = null;
        guser = VomsGridUser.decode(inputParam);

        /* (1) authorizationID (never used) */
        memberName = new String("authorizationID");
        String authID = (String) inputParam.get(memberName);

        /* (2) userSpaceTokenDescription */
        memberName = new String("userSpaceTokenDescription");
        String userSpaceTokenDescription = (String) inputParam.get(memberName);

        inputData = new GetSpaceTokensInputData((VomsGridUser) guser, userSpaceTokenDescription);

        return inputData;
    }

    public Map getOutputParameter(GetSpaceTokensOutputData outputData)
    {
        log.debug("GetSpaceTokensConverter. Creation of XMLRPC Output Structure! ");

        // Creation of new Hashtable to return
        Hashtable outputParam = new Hashtable();

        /* (1) returnStatus */
        TReturnStatus returnStatus = outputData.getStatus();
        if (returnStatus != null) {
            returnStatus.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);
        }

        /* (2) arrayOfSpaceTokens */
        ArrayOfTSpaceToken arrayOfSpaceTokens = outputData.getArrayOfSpaceTokens();
        if (arrayOfSpaceTokens != null) {
            arrayOfSpaceTokens.encode(outputParam, ArrayOfTSpaceToken.PNAME_ARRAYOFSPACETOKENS);
        }

        log.debug("Sending: " + outputParam.toString());

        //Return output Parameter structure
        return outputParam;
    }
}
