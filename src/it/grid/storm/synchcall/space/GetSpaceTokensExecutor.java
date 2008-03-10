/**
 * Execute the GetSpaceTokens request.
 * @author  Alberto Forti
 * @author  CNAF-INFN Bologna
 * @date    November 2006
 * @version 1.0
 */
package it.grid.storm.synchcall.space;

import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TStatusCode;


import org.apache.log4j.Logger;

public class GetSpaceTokensExecutor
{
    private ReservedSpaceCatalog catalog = null;
    private static final Logger log = Logger.getLogger("synch");

    public GetSpaceTokensExecutor() {
        catalog = new ReservedSpaceCatalog();
    };
    
    public GetSpaceTokensOutputData doIt(GetSpaceTokensInputData inputData) {
        GetSpaceTokensOutputData outputData;
        TReturnStatus status = null;
        
        log.debug("Started GetSpaceTokens function");
        
        /********************** Check user authentication and authorization ******************************/
        VomsGridUser user = (VomsGridUser) inputData.getUser();
        if (user == null) {
            log.debug("GetSpaceTokens: the user field is NULL");
            try {
                status = new TReturnStatus(TStatusCode.SRM_AUTHENTICATION_FAILURE, "Unable to get user credential!");
            }
            catch (InvalidTReturnStatusAttributeException ex1) {
            
                // Nothing to do, it will never be thrown
                log.debug("GetSpaceTokens: Error creating returnStatus ");
            }
            log.error("srmGetSpaceTokens: <"+user+"> Request for [spaceTokenDescription:"+inputData.getSpaceTokenAlias()+"] failed with: [status:"+status+"]");
            outputData = new GetSpaceTokensOutputData(status, null);
            return outputData;
        }
        
        /********************************** Start to manage the request ***********************************/
        String spaceAlias = inputData.getSpaceTokenAlias();
        if (spaceAlias == null)
            log.debug("userSpaceTokenDescription=NULL");
        else
            log.debug("userSpaceTokenDescription=" + spaceAlias);
        
        ArrayOfTSpaceToken arrayOfSpaceTokens = catalog.getSpaceTokens(user, spaceAlias);

        //If no valid tokenis found try withVOSPACEToken
        if (arrayOfSpaceTokens.size() == 0) {
        	arrayOfSpaceTokens = catalog.getSpaceTokensByAlias(spaceAlias);
             ////TODO ADD HERE A CHECK IF THE RESULTS ID A DEFAULT TOKEN OF NOT
        
        }
        
        
        try {
            if (arrayOfSpaceTokens.size() == 0) {
                if (spaceAlias != null)
                    status = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST, "'userSpaceTokenDescription' does not refer to an existing space");
                else
                    status = new TReturnStatus(TStatusCode.SRM_FAILURE, "No space tokens owned by this user");
                arrayOfSpaceTokens = null;
            } else
                status = new TReturnStatus(TStatusCode.SRM_SUCCESS, "");
        } catch (InvalidTReturnStatusAttributeException e) {
            // Nothing to do, it will never be thrown
            log.error("GetSpaceTokens: Error creating returnStatus ");
        }
        
        if(status.isSRM_SUCCESS())
        	log.info("srmGetSpaceTokens: <"+user+"> Request for [spaceTokenDescription:"+inputData.getSpaceTokenAlias()+"] successfully done with: [status:"+status+"]");
        else
        	log.error("srmGetSpaceTokens: <"+user+"> Request for [spaceTokenDescription:"+inputData.getSpaceTokenAlias()+"] failed with: [status:"+status+"]");
        
        
        outputData = new GetSpaceTokensOutputData(status, arrayOfSpaceTokens);

        return outputData;

    }
}
