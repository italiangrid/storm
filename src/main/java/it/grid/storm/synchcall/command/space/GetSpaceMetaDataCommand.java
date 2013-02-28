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
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.TransferObjectDecodingException;
import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.space.quota.BackgroundGPFSQuota;
import it.grid.storm.srm.types.ArrayOfTMetaDataSpace;
import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.srm.types.InvalidTMetaDataSpaceAttributeException;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.TMetaDataSpace;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.CommandHelper;
import it.grid.storm.synchcall.command.SpaceCommand;
import it.grid.storm.synchcall.data.IdentityInputData;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.exception.InvalidGetSpaceMetaDataOutputAttributeException;
import it.grid.storm.synchcall.data.space.GetSpaceMetaDataInputData;
import it.grid.storm.synchcall.data.space.IdentityGetSpaceMetaDataInputData;
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

    private static final String SRM_COMMAND = "srmGetSpaceMetaData";

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
        log.debug(" Updating SA with GPFS quotas results");
        BackgroundGPFSQuota.getInstance().submitGPFSQuota();
        
        IdentityGetSpaceMetaDataInputData data;
        if (indata instanceof IdentityInputData)
        {
            data = (IdentityGetSpaceMetaDataInputData) indata;
        }
        else
        {
            GetSpaceMetaDataOutputData outputData = new GetSpaceMetaDataOutputData();
            outputData.setStatus(CommandHelper.buildStatus(TStatusCode.SRM_NOT_SUPPORTED, "Anonymous user can not perform" + SRM_COMMAND));
            printRequestOutcome(outputData.getStatus(), (GetSpaceMetaDataInputData) indata);
            return outputData;
        }
        int errorCount = 0;
        ArrayOfTMetaDataSpace arrayData = new ArrayOfTMetaDataSpace();
        TReturnStatus globalStatus = null;

        TMetaDataSpace metadata = null;

        for(TSpaceToken token : data.getSpaceTokenArray().getTSpaceTokenArray())
        {
            StorageSpaceData spaceData = null;
            try
            {
                spaceData = catalog.getStorageSpace(token);
            } catch(TransferObjectDecodingException e)
            {
                log.error("Unable to build StorageSpaceData from StorageSpaceTO. TransferObjectDecodingException: "
                        + e.getMessage());
                metadata = createFailureMetadata(token, TStatusCode.SRM_INTERNAL_ERROR,
                                                 "Error building space data from row DB data", data.getUser());
                errorCount++;
                arrayData.addTMetaDataSpace(metadata);
                continue;

            } catch(DataAccessException e)
            {
                log.error("Unable to build get StorageSpaceTO. DataAccessException: " + e.getMessage());
                metadata = createFailureMetadata(token, TStatusCode.SRM_INTERNAL_ERROR,
                                                 "Error retrieving row space token data from DB",
                                                 data.getUser());
                errorCount++;
                arrayData.addTMetaDataSpace(metadata);
                continue;
            }
            if (spaceData != null)
            {
                if (!spaceData.isInitialized())
                {
                    log.warn("Unable to create a valid TMetaDataSpace for storage space token \'" + token
                            + "\', the space is not initialized");
                    metadata = createFailureMetadata(token, TStatusCode.SRM_FAILURE,
                                                     "Storage Space not initialized yet",
                                                     data.getUser());
                    errorCount++;
                }
                else
                {
                    try
                    {
                        metadata = new TMetaDataSpace(spaceData);
                    } catch(InvalidTMetaDataSpaceAttributeException e)
                    {
                        log.error("Unable to build TMetaDataSpace from  StorageSpaceData. InvalidTMetaDataSpaceAttributeException: "
                                + e.getMessage());
                        metadata = createFailureMetadata(token, TStatusCode.SRM_INTERNAL_ERROR, "Error building Storage Space Metadata from row data", data.getUser());
                        errorCount++;
                    } catch(InvalidTSizeAttributesException e)
                    {
                        log.error("Unable to build TMetaDataSpace from  StorageSpaceData. InvalidTSizeAttributesException: " + e.getMessage());
                        metadata = createFailureMetadata(token, TStatusCode.SRM_INTERNAL_ERROR, "Error building Storage Space Metadata from row data", data.getUser());
                        errorCount++;
                    }
                }
            }
            else
            {
                log.warn("getMetaDataSpace: unable to retrieve SpaceData for token: " + token);
                metadata = createFailureMetadata(token, TStatusCode.SRM_INVALID_REQUEST, "Space Token not found", data.getUser());
                errorCount++;
            }
            arrayData.addTMetaDataSpace(metadata);
        }

        boolean requestSuccess = (errorCount == 0);
        boolean requestFailure = (errorCount == data.getSpaceTokenArray().size());

        // Create Global Status Response
        try
        {
            if (requestSuccess)
            {
                globalStatus = new TReturnStatus(TStatusCode.SRM_SUCCESS, "");
                log.info(formatLogMessage(SUCCESS, GLOBALSTATUS, data.getUser(), null,
                                          data.getSpaceTokenArray(), globalStatus));
            }
            else
            {
                if (requestFailure)
                {
                    globalStatus = new TReturnStatus(TStatusCode.SRM_FAILURE, "No valid space tokens");
                    log.info(formatLogMessage(FAILURE, GLOBALSTATUS, data.getUser(), null,
                                              data.getSpaceTokenArray(), globalStatus));
                }
                else
                {
                    globalStatus = new TReturnStatus(TStatusCode.SRM_PARTIAL_SUCCESS,
                                                     "Check space tokens statuses for details");
                    log.info(formatLogMessage(SUCCESS, GLOBALSTATUS, data.getUser(), null,
                                              data.getSpaceTokenArray(), globalStatus));
                }
            }
        } catch(InvalidTReturnStatusAttributeException ex)
        {
            log.error("srmGetSpaceMetaData: Impossible is happen!!", ex);
            return new GetSpaceMetaDataOutputData();
        }

        log.debug("<GetSpaceMetaData > all value retrived...");
        GetSpaceMetaDataOutputData response = null;
        try
        {
            response = new GetSpaceMetaDataOutputData(globalStatus, arrayData);
        } catch(InvalidGetSpaceMetaDataOutputAttributeException e)
        {
            // never thrown
            log.error("Unexpected InvalidGetSpaceMetaDataOutputAttributeException in execute : " + e.getMessage());
        }
        return response;
    }

    private TMetaDataSpace createFailureMetadata(TSpaceToken token, TStatusCode statusCode, String message,
            GridUserInterface user)
    {
        TMetaDataSpace metadata = TMetaDataSpace.makeEmpty();
        metadata.setSpaceToken(token);
        try
        {
            metadata.setStatus(new TReturnStatus(statusCode,
                                                 message));
        } catch(InvalidTReturnStatusAttributeException e)
        {
            // never thrown
            log.error("Unexpected InvalidTReturnStatusAttributeException in execute : " + e);
        }
        log.error(formatLogMessage(SUCCESS, LOCALSTATUS, user, token, null, metadata.getStatus()));
        return metadata;
    }
    
    private void printRequestOutcome(TReturnStatus status, GetSpaceMetaDataInputData inputData)
    {
        if(inputData != null)
        {
            CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData);
        }
        else
        {
            CommandHelper.printRequestOutcome(SRM_COMMAND, log, status);
        }
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
    private String formatLogMessage(boolean success, boolean globalStatus, GridUserInterface user,
            TSpaceToken token, ArrayOfTSpaceToken arrayOfToken, TReturnStatus status)
    {
        StringBuffer buf = new StringBuffer("srmGetSpaceMetaData: ");
        buf.append("<" + user + "> ");
        buf.append("Request for [spacetoken:");
        if (!globalStatus)
        {
            buf.append(token);
        }
        else
        {
            buf.append(arrayOfToken);
        }
        buf.append("] ");
        if (success)
        {
            buf.append("successfully done with:[status:");
        }
        else
        {
            buf.append("failed with:[status:");
        }
        buf.append(status);
        buf.append("]");
        return buf.toString();
    }

}
