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

package it.grid.storm.synchcall.command.space;

import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.SpaceCommand;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.space.GetSpaceTokensInputData;
import it.grid.storm.synchcall.data.space.GetSpaceTokensOutputData;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008
 * Company: INFN-CNAF and ICTP/EGRID project
 *  * Execute the GetSpaceTokens request.
 * 
 * @author lucamag
 * @author Alberto Forti
 * 
 * @date May 29, 2008
 * 
 */

public class GetSpaceTokensCommand extends SpaceCommand implements Command {
    private ReservedSpaceCatalog catalog = null;

    public GetSpaceTokensCommand() {
        catalog = new ReservedSpaceCatalog();
    };

    public OutputData execute(InputData data) {
        GetSpaceTokensOutputData outputData;
        GetSpaceTokensInputData inputData = (GetSpaceTokensInputData) data;

        TReturnStatus status = null;

        log.debug("Started GetSpaceTokens function");

        /**
         * ******************** Check user authentication and authorization
         * *****************************
         */
        GridUserInterface user = inputData.getUser();
        if (user == null) {
            log.debug("GetSpaceTokens: the user field is NULL");
            try {
                status = new TReturnStatus(
                        TStatusCode.SRM_AUTHENTICATION_FAILURE,
                        "Unable to get user credential!");
            } catch (InvalidTReturnStatusAttributeException ex1) {

                // Nothing to do, it will never be thrown
                log.debug("GetSpaceTokens: Error creating returnStatus ");
            }
            log.error("srmGetSpaceTokens: <" + user
                    + "> Request for [spaceTokenDescription:"
                    + inputData.getSpaceTokenAlias()
                    + "] failed with: [status:" + status + "]");
            outputData = new GetSpaceTokensOutputData(status, null);
            return outputData;
        }

        /**
         * ******************************** Start to manage the request
         * **********************************
         */
        String spaceAlias = inputData.getSpaceTokenAlias();
        if (spaceAlias == null) {
            log.debug("userSpaceTokenDescription=NULL");
        } else {
            log.debug("userSpaceTokenDescription=" + spaceAlias);
        }

        ArrayOfTSpaceToken arrayOfSpaceTokens = catalog.getSpaceTokens(user,
                spaceAlias);

        // If no valid tokenis found try withVOSPACEToken
        if (arrayOfSpaceTokens.size() == 0) {
            arrayOfSpaceTokens = catalog.getSpaceTokensByAlias(spaceAlias);
            // //TODO ADD HERE A CHECK IF THE RESULTS ID A DEFAULT TOKEN OF NOT

        }

        try {
            if (arrayOfSpaceTokens.size() == 0) {
                if (spaceAlias != null) {
                    status = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
                            "'userSpaceTokenDescription' does not refer to an existing space");
                } else {
                    status = new TReturnStatus(TStatusCode.SRM_FAILURE,
                            "No space tokens owned by this user");
                }
                arrayOfSpaceTokens = null;
            } else {
                status = new TReturnStatus(TStatusCode.SRM_SUCCESS, "");
            }
        } catch (InvalidTReturnStatusAttributeException e) {
            // Nothing to do, it will never be thrown
            log.error("GetSpaceTokens: Error creating returnStatus ");
        }

        if (status.isSRM_SUCCESS()) {
            log.info("srmGetSpaceTokens: <" + user
                    + "> Request for [spaceTokenDescription:"
                    + inputData.getSpaceTokenAlias()
                    + "] successfully done with: [status:" + status + "]");
        } else {
            log.error("srmGetSpaceTokens: <" + user
                    + "> Request for [spaceTokenDescription:"
                    + inputData.getSpaceTokenAlias()
                    + "] failed with: [status:" + status + "]");
        }

        outputData = new GetSpaceTokensOutputData(status, arrayOfSpaceTokens);

        return outputData;

    }
}
