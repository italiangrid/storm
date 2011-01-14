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

import it.grid.storm.catalogs.BoLChunkCatalog;
import it.grid.storm.catalogs.BoLChunkData;
import it.grid.storm.catalogs.InvalidBoLChunkDataAttributesException;
import it.grid.storm.catalogs.RequestSummaryCatalog;
import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.InvalidDescendantsAuthRequestException;
import it.grid.storm.namespace.InvalidDescendantsEmptyRequestException;
import it.grid.storm.namespace.InvalidDescendantsFileRequestException;
import it.grid.storm.namespace.InvalidDescendantsPathRequestException;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.scheduler.Delegable;
import it.grid.storm.scheduler.SchedulerException;
import it.grid.storm.srm.types.InvalidTDirOptionAttributesException;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TSURL;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a BringOnLine Feeder: the Feeder that will handle the srmBringOnLine
 * statements. It chops a multifile request, and for each part it checks whether the dir option is
 * set and expands the directory as necessary.
 * 
 * If the request contains nothing to process, an error message gets logged, the number of queued
 * requests is decreased, and the number of finished requests is increased.
 * 
 * If the single part of the request has dirOption NOT set, then the number of queued requests is
 * decreased, the number of progressing requests is increased, the status of that chunk is changed
 * to SRM_REQUEST_INPROGRESS; the chunk is given to the scheduler for handling. In case the
 * scheduler cannot accept the chunk for any reason, a message with the requestToken and the chunk s
 * data is logged, status of the chunk passes to SRM_ABORTED, and at the end the counters are such
 * that the queued-requests is decreased while the finished-requests is increased.
 * 
 * If the single part of the request DOES have a dirOption set, then it is considered as an
 * expansion job and it gets handled now! So the number of queued requests is decreased and that for
 * progressing ones is increased, while the status is set to SRM_REQUEST_INPROGRESS. Each newly
 * expanded file gets handled as though it were part of the multifile request WITHOUT the dirOption
 * set, so it goes through the same steps as mentioned earlier on; notice that a new entry in the
 * persistence system is created, and the total number of files in this request is updated. Finally
 * the status of this expansion request is set to SRM_DONE, the number of progressing requests is
 * decreased and the number of finished requests is increased.
 * 
 * At the beginning of the expansion stage, some anomalous situations are considered and handled as
 * follows:
 * 
 * (0) In case of internal errors, they get logged and the expansion request gets failed: the status
 * changes to SRM_FAILURE, number of progressing is decreased, number of finished is increased.
 * 
 * (1) The expanded directory is empty: the request is set to SRM_SUCCESS with an explanatory String
 * saying so. The number of progressing is decreased, and the number of finished is increased.
 * 
 * (2) The directory does not exist: status set to SRM_INVALID_PATH; number of progressing is
 * decresed; number of finished is increased.
 * 
 * (3) Attempting to expand a file: status set to SRM_INVALID_PATH; number of progressing is
 * decreased; number of finished is increased.
 * 
 * (4) No rights to directory: status set to SRM_AUTHORIZATION_FAILURE; number of progressing is
 * decreased; number of finished is increased.
 * 
 * 
 * @author CNAF
 * @date Aug, 2009
 * @version 1.0
 */
public final class BoLFeeder implements Delegable {

    private static Logger log = LoggerFactory.getLogger(BoLFeeder.class);
    /** RequestSummaryData this BoLFeeder refers to. */
    private RequestSummaryData rsd = null;
    /** GridUser for this BoLFeeder. */
    private GridUserInterface gu = null;
    /** Overall request status. */
    private GlobalStatusManager gsm = null;

    /**
     * Public constructor requiring the RequestSummaryData to which this BoLFeeder refers, as well
     * as the GridUser. If null objects are supplied, an InvalidBoLFeederAttributesException is
     * thrown.
     */
	public BoLFeeder(RequestSummaryData rsd) throws InvalidBoLFeederAttributesException {

		if(rsd == null)
		{
			throw new InvalidBoLFeederAttributesException(null, null, null);
		}
		if(rsd.gridUser() == null)
		{
			throw new InvalidBoLFeederAttributesException(rsd, null, null);
		}
		try
		{
			gu = rsd.gridUser();
			this.rsd = rsd;
			gsm = new GlobalStatusManager(rsd.requestToken());
		} catch(InvalidOverallRequestAttributeException e)
		{
			log.error("ATTENTION in BoLFeeder! Programming bug when creating GlobalStatusManager! "
				+ e);
			throw new InvalidBoLFeederAttributesException(rsd, gu, null);
		}
	}

    /**
     * This method splits a multifile request as well as exapanding recursive ones; it then creates
     * the necessary tasks and loads them into the BoL chunk scheduler.
     */
	public void doIt() {

		log.debug("BoLFeeder: pre-processing " + rsd.requestToken());
		// Get all parts in request
		Collection<BoLChunkData> chunks = BoLChunkCatalog.getInstance().lookup(rsd.requestToken());
		if(chunks.isEmpty())
		{
			log.warn("ATTENTION in BoLFeeder! This SRM BoL request contained nothing to process! "
				+ rsd.requestToken());
			RequestSummaryCatalog.getInstance().failRequest(rsd,
				"This SRM Get request contained nothing to process!");
		}
		else
		{
			manageChunks(chunks);
			log.debug("BoLFeeder: finished pre-processing " + rsd.requestToken());
		}
	}

    /**
     * Private method that handles the Collection of chunks associated with the srm command!
     */
	private void manageChunks(Collection<BoLChunkData> chunks) {

		log.debug("BoLFeeder - number of chunks in request: " + chunks.size());
		for(BoLChunkData chunkData : chunks)
		{
			/* add chunk for global status consideration */
			gsm.addChunk(chunkData);
			if(TSURL.isValid(chunkData.getFromSURL()))
			{
				/*
				 * fromSURL corresponds to This installation of StoRM: go on
				 * with processing!
				 */
				if(chunkData.getDirOption().isDirectory())
				{
					/* expand the directory and manage the children! */
					manageIsDirectory(chunkData);
				}
				else
				{
					/* manage the request directly without any expansion */
					manageNotDirectory(chunkData);
				}
			}
			else
			{
				/*
				 * fromSURL does _not_ correspond to this installation of StoRM:
				 * fail chunk!
				 */
				log.warn("BoLFeeder: srmBoL contract violation! fromSURL"
					+ " does not correspond to this machine!\n Request: " + rsd.requestToken() + "\n Chunk: " + chunkData);
					
				chunkData.changeStatusSRM_FAILURE("SRM protocol violation! "
					+ "Cannot do an srmBoL of a SURL that is not local!");
					
				BoLChunkCatalog.getInstance().update(chunkData);
				/* inform global status computation of the chunk failure */
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
     * Private method that handles the case of dirOption NOT set!
     */
	private void manageNotDirectory(BoLChunkData auxChunkData) {

		log.debug("BoLFeeder - scheduling... ");
		auxChunkData.changeStatusSRM_REQUEST_INPROGRESS("srmBringOnLine chunk is being processed!");
		BoLChunkCatalog.getInstance().update(auxChunkData);
		try
		{
			/* hand it to scheduler! */
			SchedulerFacade.getInstance().chunkScheduler().schedule(
				new BoLChunk(gu, rsd, auxChunkData, gsm));
			log.debug("BoLFeeder - chunk scheduled.");
		} catch(InvalidBoLChunkAttributesException e)
		{
			/*
			 * for some reason gu, rsd or auxChunkData may be null! This should
			 * not be so!
			 */
			log.error("UNEXPECTED ERROR in BoLFeeder! Chunk could not be created!\n" + e);
			log.error("Request: " + rsd.requestToken());
			log.error("Chunk: " + auxChunkData);
			
			auxChunkData.changeStatusSRM_FAILURE("StoRM internal error does"
				+ " not allow this chunk to be processed!");
			
			BoLChunkCatalog.getInstance().update(auxChunkData);
			gsm.failedChunk(auxChunkData);
		} catch(SchedulerException e)
		{
			/* Internal error of scheduler! */
			log.error("UNEXPECTED ERROR in ChunkScheduler! Chunk could not be scheduled!\n" + e);
			log.error("Request: " + rsd.requestToken());
			log.error("Chunk: " + auxChunkData);
			
			auxChunkData.changeStatusSRM_FAILURE("StoRM internal scheduler " +
						"error prevented this chunk from being processed!");
			
			BoLChunkCatalog.getInstance().update(auxChunkData);
			gsm.failedChunk(auxChunkData);
		}
	}

    /**
     * Private method that handles the case of a BoLChunkData having dirOption set!
     */
	private void manageIsDirectory(BoLChunkData chunkData) {

		log.debug("BoLFeeder - pre-processing Directory chunk...");
		chunkData.changeStatusSRM_REQUEST_INPROGRESS("srmBringOnLine chunk is being processed!");
		BoLChunkCatalog.getInstance().update(chunkData);
		
		try
		{
			StoRI stori = NamespaceDirector.getNamespace().resolveStoRIbySURL(
								 chunkData.getFromSURL(), gu);
			/* Collection of children! */
			Collection<StoRI> storiChildren = stori.getChildren(chunkData
										   .getDirOption());
			log.debug("BoLFeeder - Number of children in parent: " + storiChildren.size());
			
			TDirOption notDir = new TDirOption(false, false, 0);
			BoLChunkData childData;
			for(StoRI storiChild : storiChildren)
			{
				try
				{
					childData = new BoLChunkData(chunkData.getRequestToken(), storiChild
									.getSURL(), chunkData.getLifeTime(), notDir, chunkData
									.getDesiredProtocols(), chunkData.getFileSize(),
									chunkData.getStatus(), chunkData.getTransferURL(),
									chunkData.getDeferredStartTime());
					
					/* fill in new db row and set the PrimaryKey of ChildData! */
					BoLChunkCatalog.getInstance().addChild(childData);
					
					log.debug("BoLFeeder - added child data: " + childData);
					/* add chunk for global status consideration */
					gsm.addChunk(childData);
					
					manageNotDirectory(childData);
				} catch(InvalidBoLChunkDataAttributesException e)
				{
					/*
					 * For some reason it was not possible to create a
					 * BoLChunkData: it is a programme bug!!! It should not
					 * occur!!! Log it and skip to the next one!
					 */
					log.error("ERROR in BoLFeeder! While expanding recursive request"
						+ ", it was not possible to create a new BoLChunkData! " + e);
				}
			}
			log.debug("BoLFeeder - expansion completed."); // info
			/*
			 * A request on a Directory is considered done whether there is
			 * somethig to expand or not!
			 */
			/*
			 * auxChunkData.changeStatusSRM_FILE_PINNED("srmBringOnLine with dirOption set: request successfully expanded!"
			 * );
			 */
			chunkData.changeStatusSRM_SUCCESS("srmBringOnLine with dirOption"
				+ " set: request successfully expanded!");

			BoLChunkCatalog.getInstance().update(chunkData);
			gsm.successfulChunk(chunkData);
		} catch(NamespaceException e)
		{
			/*
			 * The Supplied SURL does not contain a root that could be
			 * identified by the StoRI factory as referring to a VO being
			 * managed by StoRM... that is SURLs begining with such root are not
			 * handled by this SToRM!
			 */
			chunkData.changeStatusSRM_INVALID_PATH("The path specified in the "
				+ "SURL does not have a local equivalent!");
			
			BoLChunkCatalog.getInstance().update(chunkData);
			
			log.debug("ATTENTION in BoLFeeder! BoLFeeder received request"
				+ " for a SURL whose root is not recognised by StoRI!");
			gsm.failedChunk(chunkData);
		} catch(InvalidTDirOptionAttributesException e)
		{
			/* Could not create TDirOption that specifies no-expansion! */
			chunkData.changeStatusSRM_FAILURE("srmBringOnLine with dirOption set:"
				+ " expansion failure due to internal error!")
				;
			BoLChunkCatalog.getInstance().update(chunkData);
			
			log.error("UNEXPECTED ERROR in BoLFeeder! Could"
				+ " not create TDirOption specifying non-expansion!\n" + e);
			log.error("Request: " + rsd.requestToken());
			log.error("Chunk: " + chunkData);
			gsm.failedChunk(chunkData);
		} catch(InvalidDescendantsEmptyRequestException e)
		{
			/*
			 * The expanded directory was empty A request on a Directory is
			 * considered done whether there is somethig to expand or not!
			 */
			/*
			 * auxChunkData.changeStatusSRM_FILE_PINNED("BEWARE! srmBringOnLine with dirOption set: it referred to a directory that was empty!"
			 * );
			 */
			chunkData.changeStatusSRM_SUCCESS("BEWARE! srmBringOnLine with dirOption set:"
				+ " it referred to a directory that was empty!");
			
			BoLChunkCatalog.getInstance().update(chunkData);
			
			log.debug("ATTENTION in BoLFeeder! BoLFeeder received request "
				+ "to expand empty directory.");
			gsm.successfulChunk(chunkData);
//		} catch(InvalidDescendantsTDirOptionRequestException e)
//		{
//			/* The expanded directory was empty */
//			chunkData
//				.changeStatusSRM_FAILURE("BEWARE! srmBringOnLine with dirOption set:"
//					+ " it was impossible to expand the directory with the provided directory options!");
//			
//			BoLChunkCatalog.getInstance().update(chunkData);
//			
//			log.debug("ATTENTION in BoLFeeder! BoLFeeder received request to"
//				+ " expand a directory with wrong TDirOptions.");
//			gsm.failedChunk(chunkData);
		} catch(InvalidDescendantsPathRequestException e)
		{
			/* Attempting to expand non existent directory! */
			chunkData.changeStatusSRM_INVALID_PATH("srmBringOnLine with dirOption set:"
				+ " it referred to a non-existent directory!");
			
			BoLChunkCatalog.getInstance().update(chunkData);
			
			log.debug("ATTENTION in BoLFeeder! BoLFeeder received request"
				+ " to expand non-existing directory.");
			gsm.failedChunk(chunkData);
		} catch(InvalidDescendantsFileRequestException e)
		{
			/* Attempting to expand a file! */
			chunkData.changeStatusSRM_INVALID_PATH("srmBringOnLine with dirOption set: "
				+ "a file was asked to be expanded!");

			BoLChunkCatalog.getInstance().update(chunkData);

			log.debug("ATTENTION in BoLFeeder! BoLFeeder received request to expand a file.");
			gsm.failedChunk(chunkData);
		} catch(InvalidDescendantsAuthRequestException e)
		{
			/* No rights to directory! */
			chunkData.changeStatusSRM_AUTHORIZATION_FAILURE("srmBringOnLine with dirOption set:"
				+ " user has no right to access directory!");
			
			BoLChunkCatalog.getInstance().update(chunkData);

			log.debug("ATTENTION in BoLFeeder! BoLFeeder received request to"
				+ " expand a directory for which the user has no rights.");
			gsm.failedChunk(chunkData);
		}
	}

    /**
     * Method used by chunk scheduler for internal logging; it returns the request id of This
     * BoLFeeder!
     */
	public String getName() {

		return "BoLFeeder of request: " + rsd.requestToken();
	}
}
