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

import it.grid.storm.catalogs.CopyChunkCatalog;
import it.grid.storm.catalogs.CopyPersistentChunkData;
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
 * This class represents a Copy Feeder: the Feeder that will handle the
 * srmCopy statements. It chops a multifile request into its constituent
 * parts. Recursive chunks will also get expanded.
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
 * FOR NOW RECURSIVE REQUESTS ARE *NOT* HANDLED! The chunk fails with SRM_ABORT
 * and an appropriate error string.
 *
 * @author  EGRID - ICTP Trieste
 * @date    September, 2005
 * @version 2.0
 */
public final class CopyFeeder implements Delegable {

    private static Logger log = LoggerFactory.getLogger(CopyFeeder.class);
    /* RequestSummaryData this CopyFeeder refers to. */
    private RequestSummaryData rsd = null; 
    /* GridUser for this PtPFeeder. */
    private GridUserInterface gu = null; 
    /* Overall request status. */
    private GlobalStatusManager gsm = null; 

    /**
     * Public constructor requiring the RequestSummaryData to which this CopyFeeder
     * refers to, as well as the GridUser. In case of null objects, an
     * InvalidCopyFeederAttributesException is thrown.
     */
	public CopyFeeder(RequestSummaryData rsd) throws InvalidCopyFeederAttributesException {

		if(rsd == null)
		{
			throw new InvalidCopyFeederAttributesException(null, null, null);
		}
		if(rsd.gridUser() == null)
		{
			throw new InvalidCopyFeederAttributesException(rsd, null, null);
		}
		try
		{
			gu = rsd.gridUser();
			this.rsd = rsd;
			gsm = new GlobalStatusManager(rsd.requestToken());
		} catch(InvalidOverallRequestAttributeException e)
		{
			log.error("ATTENTION in CopyFeeder! "
				+ "Programming bug when creating GlobalStatusManager! " + e);
			throw new InvalidCopyFeederAttributesException(rsd, gu, gsm);
		}
	}

    /**
     * This method splits a multifile request; it then creates the necessary tasks and
     * loads them into the Copy chunk scheduler.
     */
	public void doIt() {

		log.debug("CopyFeeder: pre-processing " + rsd.requestToken());
		/* Get all parts in request */
		Collection<CopyPersistentChunkData> chunks =
										   CopyChunkCatalog.getInstance()
											   .lookup(rsd.requestToken());
		if(chunks.isEmpty())
		{
			log.warn("ATTENTION in CopyFeeder! "
				+ "This SRM Copy request contained nothing to process! " + rsd.requestToken());
			RequestSummaryCatalog.getInstance().failRequest(rsd,
				"This SRM Copy request contained nothing to process!");
		}
		else
		{
			manageChunks(chunks);
			log.debug("CopyFeeder: finished pre-processing " + rsd.requestToken());
		}
	}

    /**
     * Private method that handles the Collection of chunks associated with
     * the srm command!
     */
    private void manageChunks(Collection<CopyPersistentChunkData> chunksData) {

		log.debug("CopyFeeder: number of chunks in request " + chunksData.size());
		int counter = 0; // counter of the number of chunk retrieved
		for(CopyPersistentChunkData chunkData : chunksData)
		{
			/* Add chunk for global status consideration */
			gsm.addChunk(chunkData);
			manage(chunkData, counter++);
		}
		/*
		 * no more chunks need to be cosidered for the overall status
		 * computation
		 */
		gsm.finishedAdding();
	}

    /**
     * Private method that handles the chunk!
     */
	private void manage(CopyPersistentChunkData chunkData, int counter) {

		log.debug("CopyFeeder: scheduling chunk... ");
		try
		{
			/* change status of this chunk to being processed! */
			chunkData.changeStatusSRM_REQUEST_INPROGRESS("srmCopy chunk is being processed!");
			CopyChunkCatalog.getInstance().update(chunkData);
			boolean validFromSurl = TSURL.isValid(chunkData.getSURL());
			boolean validToSurl = TSURL.isValid(chunkData.getDestinationSURL());
			if(validFromSurl)
			{
				if(validToSurl)
				{
					/*
					 * source and destination are the same physical machine!
					 * make a local copy!
					 */
					/*
					 * For now it is being handled as a special case of ush
					 * copy! MUST BE CHANGED SOON!!! ONLY FOR DEBUG PURPOSES!!!
					 */
					log.info("CopyFeeder: chunk is localCopy.");
					log.debug("Request: " + rsd.requestToken());
					log.debug("Chunk: " + chunkData);

					SchedulerFacade.getInstance().chunkScheduler().schedule(
						new PushCopyPersistentChunk(gu, rsd, chunkData, counter, gsm));

					log.info("CopyFeeder: chunk scheduled.");
				}
				else
				{

					/*
					 * source is this machine, but destination is elsewhere!
					 * make a push copy to destination!
					 */
					log.info("CopyFeeder: chunk is pushCopy.");
					log.debug("Request: " + rsd.requestToken());
					log.debug("Chunk: " + chunkData);

					SchedulerFacade.getInstance().chunkScheduler().schedule(
						new PushCopyPersistentChunk(gu, rsd, chunkData, counter, gsm));

					log.info("CopyFeeder: chunk scheduled.");
				}
			}
			else
			{
				if(validToSurl)
				{
					/*
					 * destination is this machine, but _source_ is elsewhere!
					 * make a pull copy from the source!
					 */
					/*
					 * WARNING!!! OPERATION NOT SUPPORTED!!! MUST BE CHANGED
					 * SOON!!!
					 */
					log.warn("CopyFeeder: srmCopy in pull mode NOT supported yet!");
					log.debug("Request: " + rsd.requestToken());
					log.debug("Chunk: " + chunkData);

					chunkData.changeStatusSRM_NOT_SUPPORTED("srmCopy in pull "
						+ "mode NOT supported yet!");

					CopyChunkCatalog.getInstance().update(chunkData);
					/*
					 * inform global status computation of the chunk s failure
					 */
					gsm.failedChunk(chunkData);
				}
				else
				{
					/*
					 * boolean condition is (!names.contains(from) &&
					 * !names.contains(to)) operation between two foreign
					 * machines! it is forbidden!
					 */
					log.warn("CopyFeeder: srmCopy contract violation! Neither fromSURL"
						+ " nor toSURL are this machine! Cannot do a third party SRM"
						+ " transfer as per protocol!");
					log.warn("Request: " + rsd.requestToken());
					log.warn("Chunk: " + chunkData);
					
					chunkData.changeStatusSRM_FAILURE("SRM protocol violation"
						+ "! Cannot do an srmCopy between third parties!");
					
					CopyChunkCatalog.getInstance().update(chunkData);
					/*
					 * inform global status computation of the chunk s failure
					 */
					gsm.failedChunk(chunkData);
				}
			}
		} catch(InvalidCopyAttributesException e)
		{
			/*
			 * for some reason gu, rsd or auxChunkData may be null! This should
			 * not be so!
			 */
			log.error("UNEXPECTED ERROR in CopyFeeder! Chunk could not be created!\n" + e);
			log.error("Request: " + rsd.requestToken());
			log.error("Chunk: " + chunkData);
			
			chunkData.changeStatusSRM_FAILURE("StoRM internal error does not"
				+ " allow this chunk to be processed!");
			
			CopyChunkCatalog.getInstance().update(chunkData);
			/* inform global status computation of the chunk s failure */
			gsm.failedChunk(chunkData);
		} catch(SchedulerException e)
		{
			/* Internal error of scheduler! */
			log.error("UNEXPECTED ERROR in ChunkScheduler! Chunk could not be scheduled!\n" + e);
			log.error("Request: " + rsd.requestToken());
			log.error("Chunk: " + chunkData);
			
			chunkData.changeStatusSRM_FAILURE("StoRM internal scheduler "
				+ "error prevented this chunk from being processed!");
			
			CopyChunkCatalog.getInstance().update(chunkData);
			/* inform global status computation of the chunk s failure */
			gsm.failedChunk(chunkData);
		}
	}

    /**
     * Method used by chunk scheduler for internal logging; it returns the request
     * token!
     */
	public String getName() {

		return "CopyFeeder of request: " + rsd.requestToken();
	}
}
