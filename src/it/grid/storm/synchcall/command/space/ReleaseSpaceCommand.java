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
import it.grid.storm.common.types.PFN;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.SpaceCommand;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.space.ReleaseSpaceInputData;
import it.grid.storm.synchcall.data.space.ReleaseSpaceOutputData;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents the ReleaseSpaceManager Class. This class hava a
 * reseveSpace method that perform all operation nedded to satisfy a SRM space
 * release request.
 * 
 * @author Magnoni Luca
 * @author Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */

public class ReleaseSpaceCommand extends SpaceCommand implements Command {

    private final ReservedSpaceCatalog catalog;

    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(ReleaseSpaceCommand.class);

    public ReleaseSpaceCommand() {
        catalog = new ReservedSpaceCatalog();
    };

    public OutputData execute(InputData indata) {
        ReleaseSpaceOutputData outputData = new ReleaseSpaceOutputData();
        ReleaseSpaceInputData inputData = (ReleaseSpaceInputData) indata;
        TReturnStatus returnStatus = null;

        /**
         * Check input parameter.
         * 
         * Validate ReleaseSpaceInputData. The check is done at this level to
         * separate internal StoRM logic from xmlrpc specific operation.
         */

        if ((inputData == null)
                || ((inputData != null) && (inputData.getSpaceToken() == null))) {
            ReleaseSpaceCommand.log.error("ReleaseSpace : Invalid input parameter specified");
            returnStatus = manageStatus(TStatusCode.SRM_INVALID_REQUEST,
            "SpaceToken is empty.");
            outputData.setStatus(returnStatus);
            return outputData;
        }

        /**
         * Check if GridUser in InputData is not null, otherwise return with an
         * error message.
         */
        GridUserInterface user = inputData.getUser();
        if (user == null) {
            ReleaseSpaceCommand.log.debug("Release Space: Unable to get user credential. ");
            returnStatus = manageStatus(TStatusCode.SRM_AUTHENTICATION_FAILURE,
            "Unable to get user credential");
            outputData.setStatus(returnStatus);
            ReleaseSpaceCommand.log.error("srmReleaseSpace: <" + user
                    + "> Request for [spacetoken:" + inputData.getSpaceToken()
                    + "] failed with: [status:" + returnStatus + "]");
            return outputData;
        }

        ReleaseSpaceCommand.log.debug("INPUT data not null");

        boolean forceFileRelease = inputData.getForceFileRelease();
        boolean nopinned = true;
        boolean failure = false;
        String explanation = "";
        TStatusCode statusCode = null;

        /**
         * Get StorageSpaceData linked with the specified space Token.
         */
        StorageSpaceData data = catalog.getStorageSpace(inputData
                .getSpaceToken());

        if (data == null) {
            failure = true;
            explanation = "SpaceToken does not refers to an existing space.";
            statusCode = TStatusCode.SRM_INVALID_REQUEST;
            returnStatus = manageStatus(statusCode, explanation);
            outputData.setStatus(returnStatus);
            ReleaseSpaceCommand.log.error("srmReleaseSpace: <" + user
                    + "> Request for [spacetoken:" + inputData.getSpaceToken()
                    + "] for failed with: [status:" + returnStatus + "]");
            return outputData;

        }

        ReleaseSpaceCommand.log.debug("ReleaseExecutor: space data not null retrieved.");

        /**
         * With forceFileRelease = false, the space is not released if there are
         * still file pinned on it.
         */
        if (!forceFileRelease) {
            /**
             * @todo Verify if there are still pinned files...
             */
            nopinned = true;
        }

        if ((forceFileRelease) || (nopinned)) {
            // Verify if the requester is the owner of the token
            if (data.getUser().getDn().equals(user.getDn())) {
                // This is an authorized request of ReleaseSpace.
                // The user that perform the ReleaseSpace is the owner of the
                // SpaceToken.
                ReleaseSpaceCommand.log
                .debug("ReleaseCommand: Authorized Release Space for user: "
                        + data.getUser());
                returnStatus = manageAuthorizedReleaseSpace(data, user);
            } else {
                ReleaseSpaceCommand.log.debug("ReleaseCommand: Unauthorized ReleaseSpaceRequest!");
                failure = true;
                explanation = "User is not authorized to release this token";
                statusCode = TStatusCode.SRM_AUTHORIZATION_FAILURE;
                returnStatus = manageStatus(statusCode, explanation);
            }

        } else {
            ReleaseSpaceCommand.log.debug("ReleaseCommand: Space still contains pinned files!");
            failure = true;
            explanation = "Space still contains pinned files. ";
            statusCode = TStatusCode.SRM_FAILURE;
            returnStatus = manageStatus(statusCode, explanation);
        }

        // if (failure) {
        // returnStatus = manageStatus(statusCode, explanation);
        // }
        ReleaseSpaceCommand.log.debug("ReleaseCommand:return outputData");
        outputData.setStatus(returnStatus);
        if (returnStatus.isSRM_SUCCESS()) {
            ReleaseSpaceCommand.log
            .info("srmReleaseSpace: <" + user
                    + "> Request for [spacetoken:"
                    + inputData.getSpaceToken()
                    + "] successfully done with: [status:"
                    + returnStatus + "]");
        } else {
            ReleaseSpaceCommand.log.error("srmReleaseSpace: <" + user
                    + "> Request for [spacetoken:" + inputData.getSpaceToken()
                    + "] for failed with: [status:" + returnStatus + "]");
        }

        return outputData;
    }

    /**
     * 
     * @param user
     *            GridUserInterface
     * @param data
     *            StorageSpaceData
     * @return TReturnStatus
     */
    private TReturnStatus manageAuthorizedReleaseSpace(StorageSpaceData data,
            GridUserInterface user) {
        // Get Space File name
        String spaceFileName;
        PFN pfn = data.getSpaceFileName();
        ReleaseSpaceCommand.log.debug("ReleaseCommand: manageAuthorizedReleaseSpace");

        if (pfn != null) {
            spaceFileName = pfn.getValue();
            ReleaseSpaceCommand.log.debug("ReleaseCommand: spaceFileName: " + spaceFileName);
            File spaceFile = new File(spaceFileName);
            if (spaceFile.delete()) {
                // Remove spaceData from presistence
                if (catalog.release(user, data.getSpaceToken())) {
                    return manageStatus(TStatusCode.SRM_SUCCESS,
                    "Space Released.");
                } else {
                    return manageStatus(TStatusCode.SRM_INTERNAL_ERROR,
                    "Space removed, but spaceToken was not found in the DB");
                }
            } else {
                return manageStatus(TStatusCode.SRM_FAILURE,
                "Space can not be removed by StoRM!");
            }
        } else {
            return manageStatus(TStatusCode.SRM_FAILURE,
            "SRM Internal failure.");
        }
    }

    /**
     * 
     * @param statusCode
     *            statusCode
     * @param explanation
     *            explanation string
     * @return returnStatus returnStatus
     */
    private TReturnStatus manageStatus(TStatusCode statusCode,
            String explanation) {
        TReturnStatus returnStatus = null;
        try {
            returnStatus = new TReturnStatus(statusCode, explanation);
        } catch (InvalidTReturnStatusAttributeException ex1) {
            ReleaseSpaceCommand.log.debug("ReleaseFile : Error creating returnStatus " + ex1);
        }
        return returnStatus;
    }

}
