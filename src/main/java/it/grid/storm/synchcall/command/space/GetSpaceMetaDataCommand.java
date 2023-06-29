/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.command.space;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.TransferObjectDecodingException;
import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.space.gpfsquota.GPFSQuotaManager;
import it.grid.storm.srm.types.ArrayOfTMetaDataSpace;
import it.grid.storm.srm.types.InvalidTMetaDataSpaceAttributeException;
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
import it.grid.storm.synchcall.data.space.GetSpaceMetaDataOutputData;
import it.grid.storm.synchcall.data.space.IdentityGetSpaceMetaDataInputData;

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

	public static final Logger log = LoggerFactory
	  .getLogger(GetSpaceMetaDataCommand.class);
	
	private ReservedSpaceCatalog catalog = null;

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
	 *          GetSpaceMetaDataInputData
	 * @return GetSpaceMetaDataOutputData
	 */
	public OutputData execute(InputData indata) {

		log.debug("<GetSpaceMetaData Start!>");
		log.debug(" Updating SA with GPFS quotas results");
		GPFSQuotaManager.INSTANCE.triggerComputeQuotas();

		IdentityGetSpaceMetaDataInputData data;
		if (indata instanceof IdentityInputData) {
			data = (IdentityGetSpaceMetaDataInputData) indata;
		} else {
			GetSpaceMetaDataOutputData outputData = new GetSpaceMetaDataOutputData();
			outputData.setStatus(CommandHelper.buildStatus(
				TStatusCode.SRM_NOT_SUPPORTED, "Anonymous user can not perform"
					+ SRM_COMMAND));
			printRequestOutcome(outputData.getStatus(),
				(GetSpaceMetaDataInputData) indata);
			return outputData;
		}
		int errorCount = 0;
		ArrayOfTMetaDataSpace arrayData = new ArrayOfTMetaDataSpace();
		TReturnStatus globalStatus = null;

		TMetaDataSpace metadata = null;

		for (TSpaceToken token : data.getSpaceTokenArray().getTSpaceTokenArray()) {
			StorageSpaceData spaceData = null;
			try {
				spaceData = catalog.getStorageSpace(token);
			} catch (TransferObjectDecodingException e) {
				log.error("Error getting storage space data for token {}. {}",
				  token, e.getMessage(),e);
				metadata = createFailureMetadata(token, TStatusCode.SRM_INTERNAL_ERROR,
					"Error building space data from row DB data", data.getUser());
				errorCount++;
				arrayData.addTMetaDataSpace(metadata);
				continue;

			} catch (DataAccessException e) {
				log.error("Error getting storage space data for token {}. {}",
				  token, e.getMessage(),e);
				metadata = createFailureMetadata(token, TStatusCode.SRM_INTERNAL_ERROR,
					"Error retrieving row space token data from DB", data.getUser());
				errorCount++;
				arrayData.addTMetaDataSpace(metadata);
				continue;
			}
			if (spaceData != null) {
				if (!spaceData.isInitialized()) {
					log.warn("Uninitialized storage data found for token {}", token);
					metadata = createFailureMetadata(token, TStatusCode.SRM_FAILURE,
						"Storage Space not initialized yet", data.getUser());
					errorCount++;
				} else {
					try {
						metadata = new TMetaDataSpace(spaceData);
					} catch (InvalidTMetaDataSpaceAttributeException e) {
						log.error("Metadata error. {}", e.getMessage(), e);
						metadata = createFailureMetadata(token,
							TStatusCode.SRM_INTERNAL_ERROR,
							"Error building Storage Space Metadata from row data",
							data.getUser());
						errorCount++;
					} catch (InvalidTSizeAttributesException e) {
						log.error("Metadata error. {}", e.getMessage(), e);
						metadata = createFailureMetadata(token,
							TStatusCode.SRM_INTERNAL_ERROR,
							"Error building Storage Space Metadata from row data",
							data.getUser());
						errorCount++;
					}
				}
			} else {
				log.warn("Unable to retrieve space data for token {}.",token);
				metadata = createFailureMetadata(token,
					TStatusCode.SRM_INVALID_REQUEST, "Space Token not found",
					data.getUser());
				errorCount++;
			}
			arrayData.addTMetaDataSpace(metadata);
		}

		boolean requestSuccess = (errorCount == 0);
		boolean requestFailure = (errorCount == data.getSpaceTokenArray().size());

		if (requestSuccess) {
			globalStatus = new TReturnStatus(TStatusCode.SRM_SUCCESS, "");

			log.info("srmGetSpaceMetadata: user <{}> Request for [spaceTokens: {}] "
				+ "done succesfully with: [status: {}]", data.getUser(),
				data.getSpaceTokenArray(), globalStatus);

		} else {
			if (requestFailure) {
				globalStatus = new TReturnStatus(TStatusCode.SRM_FAILURE,
					"No valid space tokens");

				log.info(
					"srmGetSpaceMetadata: user <{}> Request for [spaceTokens: {}] "
						+ "failed with: [status: {}]", data.getUser(),
					data.getSpaceTokenArray(), globalStatus);

			} else {

				globalStatus = new TReturnStatus(TStatusCode.SRM_PARTIAL_SUCCESS,
					"Check space tokens statuses for details");

				log.info(
					"srmGetSpaceMetadata: user <{}> Request for [spaceTokens: {}] "
						+ "partially done with: [status: {}]", data.getUser(),
					data.getSpaceTokenArray(), globalStatus);

			}
		}

		GetSpaceMetaDataOutputData response = null;
		try {
			response = new GetSpaceMetaDataOutputData(globalStatus, arrayData);
		} catch (InvalidGetSpaceMetaDataOutputAttributeException e) {
		  log.error(e.getMessage(),e);
		}
		return response;
	}

	private TMetaDataSpace createFailureMetadata(TSpaceToken token,
		TStatusCode statusCode, String message, GridUserInterface user) {

		TMetaDataSpace metadata = TMetaDataSpace.makeEmpty();
		metadata.setSpaceToken(token);

		try {
			metadata.setStatus(new TReturnStatus(statusCode, message));
		} catch (IllegalArgumentException e) {
		  log.error(e.getMessage(),e);
		}
		
		return metadata;
	}

	private void printRequestOutcome(TReturnStatus status,
		GetSpaceMetaDataInputData inputData) {

		if (inputData != null) {
			CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData);
		} else {
			CommandHelper.printRequestOutcome(SRM_COMMAND, log, status);
		}
	}

}
