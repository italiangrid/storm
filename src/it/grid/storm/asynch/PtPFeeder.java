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

package it.grid.storm.asynch;

import it.grid.storm.catalogs.PtPChunkCatalog;
import it.grid.storm.catalogs.PtPPersistentChunkData;
import it.grid.storm.catalogs.RequestSummaryCatalog;
import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.scheduler.Delegable;
import it.grid.storm.scheduler.SchedulerException;
import it.grid.storm.srm.types.TSURL;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a PrepareToPut Feeder: the Feeder that will handle the
 * srmPrepareToPut statements. It chops a multifile request into its constituent
 * parts.
 *
 * If the request contains nothing to process, an error message gets logged, the
 * number of queued requests is decreased, and the number of finished requests is
 * increased.
 *
 * Each single part of the request is handled as follows: the number of
 * queued requests is decreased, the number of progressing requests is increased,
 * the status of that chunk is changed to SRM_REQUEST_INPROGRESS; the chunk is
 * given to the scheduler for handling. In case the scheduler cannot accept the
 * chunk for any reason, a messagge with the requestToken and the chunk s data is
 * logged, status of the chunk passes to SRM_ABORTED, and at the end the counters
 * are such that the queued-requests is decreased while the finished-requests is
 * increased.
 *
 * @author  EGRID - ICTP Trieste
 * @date    June, 2005
 * @version 2.0
 */
public final class PtPFeeder implements Delegable {

    private static Logger log = LoggerFactory.getLogger(PtPFeeder.class);
    /* RequestSummaryData this PtPFeeder refers to. */
    private RequestSummaryData rsd = null;
    /* GridUser for this PtPFeeder. */
    private GridUserInterface gu = null;
    /* Overall request status. */
    private GlobalStatusManager gsm = null; 

    /**
     * Public constructor requiring the RequestSummaryData to which this PtPFeeder
     * refers, as well as the GridUser. In case of null objects, an InvalidPtPFeederAttributesException
     * is thrown; likewise if the OverallRequest object cannot be instantiated for this
     * request.
     */
	public PtPFeeder(RequestSummaryData rsd) throws InvalidPtPFeederAttributesException {

		if(rsd == null)
		{
			throw new InvalidPtPFeederAttributesException(null, null, null);
		}
		if(rsd.gridUser() == null)
		{
			throw new InvalidPtPFeederAttributesException(rsd, null, null);
		}
		try
		{
			gu = rsd.gridUser();
			this.rsd = rsd;
			gsm = new GlobalStatusManager(rsd.requestToken());
		} catch(InvalidOverallRequestAttributeException e)
		{
			log.error("ATTENTION in PtPFeeder! Programming bug when creating GlobalStatusManager! "
				+ e);
			throw new InvalidPtPFeederAttributesException(rsd, gu, null);
		}
	}

    /**
     * This method splits a multifile request; it then creates the necessary tasks and
     * loads them into the PtP chunk scheduler.
     */
	public void doIt() {

		log.debug("PtPFeeder: pre-processing " + rsd.requestToken());
		/* Get all parts in request */
		Collection<PtPPersistentChunkData> chunks = PtPChunkCatalog.getInstance().lookup(rsd.requestToken());
		if(chunks.isEmpty())
		{
			log.warn("ATTENTION in PtPFeeder! This SRM put request contained nothing to process! "
				+ rsd.requestToken());
			RequestSummaryCatalog.getInstance().failRequest(rsd,
				"This SRM put request contained nothing to process!");
		}
		else
		{
			manageChunks(chunks);
			log.debug("PtPFeeder: finished pre-processing " + rsd.requestToken());
		}
	}

    /**
     * Private method that handles the Collection of chunks associated with
     * the srm command!
	 * @param chunksData
	 */
	private void manageChunks(Collection<PtPPersistentChunkData> chunksData) {

		log.debug("PtPFeeder: number of chunks in request " + chunksData.size());
		/* chunk currently being processed */
		for(PtPPersistentChunkData chunkData : chunksData)
		{
			/* add chunk for global status consideration */
			gsm.addChunk(chunkData);
			if(TSURL.isValid(chunkData.getSURL()))
			{
				manage(chunkData);
			}
			else
			{
				/*
				 * toSURL does _not_ correspond to this installation of StoRM:
				 * fail chunk!
				 */
				log.warn("PtPFeeder: srmPtP contract violation! toSURL"
					+ " does not refer to this machine!");
				log.warn("Request: " + rsd.requestToken());
				log.warn("Chunk: " + chunkData);
				
				chunkData.changeStatusSRM_FAILURE("SRM protocol violation!" +
							" Cannot do an srmPtP of a SURL that is not local!");
				
				PtPChunkCatalog.getInstance().update(chunkData);
				/* inform global status computation of the chunk s failure */
				gsm.failedChunk(chunkData);
			}
		}
		/*
		 * no more chunks need to be considered for the overall status
		 * computation
		 */
		gsm.finishedAdding();
	}

	/**
	 * Private method that handles the chunk!
	 * 
	 * @param auxChunkData
	 */
	private void manage(PtPPersistentChunkData auxChunkData) {

		log.debug("PtPFeeder - scheduling... ");
		try
		{
			/* change status of this chunk to being processed! */
			auxChunkData.changeStatusSRM_REQUEST_INPROGRESS("srmPrepareToPut "
				+ "chunk is being processed!");
			
			PtPChunkCatalog.getInstance().update(auxChunkData);
			
			/* hand it to scheduler! */
			SchedulerFacade.getInstance().chunkScheduler().schedule(
				new PtPPersistentChunk(gu, rsd, auxChunkData, gsm));
			log.debug("PtPFeeder - chunk scheduled.");
		} catch(InvalidPersistentRequestAttributesException e)
		{
			log.error("UNEXPECTED ERROR in PtPFeeder! Chunk could not be created!\n" + e);
			
			auxChunkData.changeStatusSRM_FAILURE("StoRM internal error"
				+ " does not allow this chunk to be processed!");
			
			PtPChunkCatalog.getInstance().update(auxChunkData);
			gsm.failedChunk(auxChunkData);
		} catch(InvalidRequestAttributesException e)
        {
            log.error("UNEXPECTED ERROR in PtPFeeder! Chunk could not be created!\n" + e);
            
            auxChunkData.changeStatusSRM_FAILURE("StoRM internal error"
                + " does not allow this chunk to be processed!");
            
            PtPChunkCatalog.getInstance().update(auxChunkData);
            gsm.failedChunk(auxChunkData);
        } catch(SchedulerException e)
        {
            /* Internal error of scheduler! */
            log.error("UNEXPECTED ERROR in ChunkScheduler! Chunk could not be scheduled!\n" + e);

            auxChunkData.changeStatusSRM_FAILURE("StoRM internal scheduler "
                    + "error prevented this chunk from being processed!");

            PtPChunkCatalog.getInstance().update(auxChunkData);
            gsm.failedChunk(auxChunkData);
        }
    }

    /**
     * Method used by chunk scheduler for internal logging; it returns the request
     * token!
     */
	public String getName() {

		return "PtPFeeder of request: " + rsd.requestToken();
	}
}
