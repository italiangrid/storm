/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package it.grid.storm.asynch;

import java.util.Arrays;
import it.grid.storm.catalogs.PtPChunkCatalog;
import it.grid.storm.catalogs.PtPData;
import it.grid.storm.catalogs.PtPPersistentChunkData;
import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.scheduler.PersistentRequestChunk;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.CommandHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michele Dibenedetto
 * 
 */
public class PtPPersistentChunk extends PtP implements PersistentRequestChunk {

	private static Logger log = LoggerFactory.getLogger(PtPPersistentChunk.class);

	/**
	 * RequestSummaryData containing all the statistics for the originating
	 * srmPrepareToPutRequest
	 */
	private final RequestSummaryData rsd;

	/**
	 * GlobalStatusManager object in charge of computing the global status of the
	 * request This chunk belongs to
	 */
	private final GlobalStatusManager gsm;

	/**
	 * Constructor requiring the VomsGridUser, the RequestSummaryData, the
	 * PtPChunkData about this chunk, and the GlobalStatusManager. If the supplied
	 * attributes are null, an InvalidPtPChunkAttributesException is thrown.
	 * 
	 * @throws InvalidPtPAttributesException
	 * @throws InvalidPtPChunkAttributesException
	 */
	public PtPPersistentChunk(RequestSummaryData summaryData,
		PtPPersistentChunkData chunkData, GlobalStatusManager gsm)
		throws InvalidRequestAttributesException, IllegalArgumentException {

		super(chunkData);
		if (summaryData == null || gsm == null) {
			throw new IllegalArgumentException(
				"Unable to instantiate the object, illegal arguments: summaryData="
					+ summaryData + " chunkData=" + chunkData);
		}
		this.rsd = summaryData;
		this.gsm = gsm;
	}

	/**
	 * Method that supplies a String describing this PtGChunk - for scheduler Log
	 * purposes! It returns the request token and the SURL that was asked for.
	 */
	@Override
	public String getName() {

		return "PtPChunk of request " + rsd.requestToken() + " for SURL "
			+ requestData.getSURL();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.asynch.RequestChunk#getRequestToken()
	 */
	@Override
	public String getRequestToken() {

		return rsd.requestToken().toString();
	}

	@Override
	public void persistStatus() {

		PtPPersistentChunk.log.debug("Persisting status of request : "
			+ rsd.requestToken() + " on SURL " + this.requestData.getSURL());
		PtPChunkCatalog.getInstance().update((PtPPersistentChunkData) requestData);
	}

	@Override
	public void updateGlobalStatus() {

		PtPPersistentChunk.log.debug("Updating global status for request : "
			+ rsd.requestToken() + " on SURL " + this.requestData.getSURL());
		if (failure) {
			gsm.failedChunk((PtPPersistentChunkData) requestData);
		} else {
			if (spacefailure) {
				gsm.expiredSpaceLifetimeChunk((PtPPersistentChunkData) requestData);
			} else {
				gsm.successfulChunk((PtPPersistentChunkData) requestData);
			}
		}
	}

	@Override
	protected void printRequestOutcome(PtPData inputData) {

		if (inputData != null) {
			if (inputData.getSURL() != null) {
				if (rsd.requestToken() != null) {
					CommandHelper.printRequestOutcome(SRM_COMMAND, log,
						inputData.getStatus(), inputData, rsd.requestToken(),
						Arrays.asList(inputData.getSURL().toString()));
				} else {
					CommandHelper.printRequestOutcome(SRM_COMMAND, log,
						inputData.getStatus(), inputData,
						Arrays.asList(inputData.getSURL().toString()));
				}

			} else {
				if (rsd.requestToken() != null) {
					CommandHelper.printRequestOutcome(SRM_COMMAND, log,
						inputData.getStatus(), inputData, rsd.requestToken());
				} else {
					CommandHelper.printRequestOutcome(SRM_COMMAND, log,
						inputData.getStatus(), inputData);
				}
			}

		} else {
			CommandHelper.printRequestOutcome(SRM_COMMAND, log, CommandHelper
				.buildStatus(TStatusCode.SRM_INTERNAL_ERROR, "No input available"));
		}
	}
}
