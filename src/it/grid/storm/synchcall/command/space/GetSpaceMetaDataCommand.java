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
import it.grid.storm.space.StorageSpaceNotInitializedException;
import it.grid.storm.srm.types.ArrayOfTMetaDataSpace;
import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TMetaDataSpace;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TSpaceType;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.SpaceCommand;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.exception.InvalidGetSpaceMetaDataOutputAttributeException;
import it.grid.storm.synchcall.data.space.GetSpaceMetaDataInputData;
import it.grid.storm.synchcall.data.space.GetSpaceMetaDataOutputData;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008
 * Company: INFN-CNAF and ICTP/EGRID project
 * 
 * This class represents the GetSpaceMetaDataManager Class. This class hava a
 * reseveSpace method that perform all operation nedded to satisfy a SRM space
 * release request.
 * 
 * @author lucamag
 * @date May 29, 2008
 * 
 */

public class GetSpaceMetaDataCommand extends SpaceCommand implements Command {

    private ReservedSpaceCatalog catalog = null;

    private static final boolean SUCCESS = true;
    private static final boolean FAILURE = false;
    private static final boolean GLOBALSTATUS = true;
    private static final boolean LOCALSTATUS = false;

    /**
     * Constructor. Bind the Executor with ReservedSpaceCatalog
     */

    public GetSpaceMetaDataCommand() {
        catalog = new ReservedSpaceCatalog();
    }

    /**
     * 
     * @param data
     *            GetSpaceMetaDataInputData
     * @return GetSpaceMetaDataOutputData
     */
    public OutputData execute(InputData indata) {
        log.debug("<GetSpaceMetaData Start!>");

        GetSpaceMetaDataInputData data = (GetSpaceMetaDataInputData) indata;

        int errorCount = 0;
        int sizeBulkRequest = data.getSpaceTokenArray().size();

        GetSpaceMetaDataOutputData response = null;
        ArrayOfTMetaDataSpace arrayData = new ArrayOfTMetaDataSpace();
        TReturnStatus globalStatus = null;

        TSpaceToken token;
        TMetaDataSpace metadata = null;

        // For each Space Token retrieve MetaData info
        for (int i = 0; i < sizeBulkRequest; i++) {

            // Retrieve entry from Space Catalog corresponding to the TOKEN
            token = data.getSpaceToken(i);
            boolean isInitialized = true;
            try {
                metadata = catalog.getMetaDataSRMSpace(token);
            }
            catch (StorageSpaceNotInitializedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                isInitialized = false;
            }

            if(!isInitialized)
            {
                errorCount++;
                metadata = TMetaDataSpace.makeEmpty();
                metadata.setSpaceToken(token);
                TReturnStatus status = null;
                try {
                    status = new TReturnStatus(TStatusCode.SRM_INTERNAL_ERROR, "Storage Space not initialized yet");
                } catch (InvalidTReturnStatusAttributeException e) {
                    //never thrown
                    log.error("Unexpected InvalidTReturnStatusAttributeException in execute : " + e);
                }
                metadata.setStatus(status);
                log.error(formatLogMessage(SUCCESS, LOCALSTATUS, data.getUser(), token, null, status));
            }
            else
            {
                if (metadata == null) { // There are some problems...
                    errorCount++;
                    metadata = TMetaDataSpace.makeEmpty();
                    metadata.setSpaceToken(token);
                    TReturnStatus status = null;
                    try {
                        status = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST, "Space token not valid");
                    } catch (InvalidTReturnStatusAttributeException e) {
                        //never thrown
                        log.error("Unexpected InvalidTReturnStatusAttributeException in execute : " + e);
                    }
                    metadata.setStatus(status);
                    log.error(formatLogMessage(SUCCESS, LOCALSTATUS, data.getUser(), token, null, status));
                } else { // Retrieved with success MetaData from the catalog
    
                    // Check if it is a VOSpaceToken, that is STATIC SPACE RESERVATION
                    if (metadata.getSpaceType().equals(TSpaceType.VOSPACE)) {
    
                    } else {
                        log.debug("srmGetSpaceMetaData: Space Token '" + metadata.getSpaceToken()
                                + "' is pointing to a dynamic space reservation.");
    
                        // Into the TMetaDataSpace constructor (from the SpaceData
                        // object retrieved from catalog)
                        // is setted the correct status for the request.
                        // In case of lifetime expired , SRM_SPACE_LIFETIME_EXPIRED
                        // is setted,
                        // otherwise SRM_SUCCESS.
                        /**
                         * @todo : Above description of todo.
                         */
                    }
                }
            }
            arrayData.addTMetaDataSpace(metadata);

        }

        boolean requestSuccess = (errorCount == 0);
        boolean requestFailure = (errorCount == sizeBulkRequest);

        // Create Global Status Response
        try {
            if (requestSuccess) {
                globalStatus = new TReturnStatus(TStatusCode.SRM_SUCCESS, "");
                log.info(formatLogMessage(SUCCESS,
                                          GLOBALSTATUS,
                                          data.getUser(),
                                          null,
                                          data.getSpaceTokenArray(),
                                          globalStatus));
            } else if (requestFailure) {
                globalStatus = new TReturnStatus(TStatusCode.SRM_FAILURE, "No valid space tokens");
                log.error(formatLogMessage(FAILURE,
                                           GLOBALSTATUS,
                                           data.getUser(),
                                           null,
                                           data.getSpaceTokenArray(),
                                           globalStatus));
            } else {
                globalStatus =
                        new TReturnStatus(TStatusCode.SRM_PARTIAL_SUCCESS, "Check space tokens statuses for details");
                log.error(formatLogMessage(SUCCESS,
                                           GLOBALSTATUS,
                                           data.getUser(),
                                           null,
                                           data.getSpaceTokenArray(),
                                           globalStatus));
            }
        } catch (InvalidTReturnStatusAttributeException ex) {
            log.error("srmGetSpaceMetaData: Impossible is happen!!", ex);
            return new GetSpaceMetaDataOutputData();
        }

        log.debug("<GetSpaceMetaData > all value retrived...");
        try {
            response = new GetSpaceMetaDataOutputData(globalStatus, arrayData);
        } catch (InvalidGetSpaceMetaDataOutputAttributeException ex1) {
            log.error("srmGetSpaceMetaData: Unable to build Output", ex1);
        }
        return response;
    }




    /**
     * 
     * @param success
     *            boolean
     * @param globalStatus
     *            boolean
     * @param user
     *            GridUserInterface
     * @param token
     *            TSpaceToken
     * @param arrayOfToken
     *            ArrayOfTSpaceToken
     * @param status
     *            TReturnStatus
     * @return String
     */
    private String formatLogMessage(boolean success, boolean globalStatus, GridUserInterface user, TSpaceToken token,
            ArrayOfTSpaceToken arrayOfToken, TReturnStatus status) {
        StringBuffer buf = new StringBuffer("srmGetSpaceMetaData: ");
        buf.append("<" + user + "> ");
        buf.append("Request for [spacetoken:");
        if (!globalStatus) {
            buf.append(token);
        } else {
            buf.append(arrayOfToken);
        }
        buf.append("] ");
        if (success) {
            buf.append("successfully done with:[status:");
        } else {
            buf.append("failed with:[status:");
        }
        buf.append(status);
        buf.append("]");
        return buf.toString();
    }

}
