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

import it.grid.storm.catalogs.InvalidPtGChunkDataAttributesException;
import it.grid.storm.catalogs.PtGChunkCatalog;
import it.grid.storm.catalogs.PtGChunkData;
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
 * This class represents a PrepareToGet Feeder: the Feeder that will handle the
 * srmPrepareToGet statements. It chops a multifile request, and for each part it
 * checks whether the dir option is set and expands the directory as necessary.
 *
 * If the request contains nothing to process, an error message gets logged, the
 * number of queued requests is decreased, and the number of finished requests is
 * increased.
 *
 * If the single part of the request has dirOption NOT set, then the number of
 * queued requests is decreased, the number of progressing requests is increased,
 * the status of that chunk is changed to SRM_REQUEST_INPROGRESS; the chunk is
 * given to the scheduler for handling. In case the scheduler cannot accept the
 * chunk for any reason, a messagge with the requestToken and the chunk s data is
 * logged, status of the chunk passes to SRM_ABORTED, and at the end the counters
 * are such that the queued-requests is decreased while the finished-requests is
 * increased.
 *
 * If the single part of the request DOES have a dirOption set, then it is considered
 * as an expansion job and it gets handled now! So the number of queued requests is
 * decreased and that for progressing ones is increased, while the status is set to
 * SRM_REQUEST_INPROGRESS. Each newly expanded file gets handled as though it were part
 * of the multifile request WITHOUT the dirOption set, so it goes through the same steps
 * as mentioned earlier on; notice that a new entry in the persistence system is created,
 * and the total number of files in this request is updated. Finally the status of this
 * expansion request is set to SRM_DONE, the number of progressing requests is decreased
 * and the number of finished requests is increased.
 *
 * At the beginning of the expansion stage, some anomalous situations are considered
 * and handled as follows:
 *
 * (0) In case of internal errors, they get logged and the expansion request gets failed:
 * the status changes to SRM_FAILURE, number of progressing is decreased, number of
 * finished is increased.
 *
 * (1) The expanded directory is empty: the request is set to SRM_SUCCESS with an explanatory
 * String saying so. The number of progressing is decreased, and the number of finished is
 * increased.
 *
 * (2) The directory does not exist: status set to SRM_INVALID_PATH; number of
 * progressing is decresed; number of finished is increased.
 *
 * (3) Attempting to expand a file: status set to SRM_INVALID_PATH; number of
 * progressing is decreased; number of finished is increased.
 *
 * (4) No rights to directory: status set to SRM_AUTHORIZATION_FAILURE; number of
 * progressing is decreased; number of finished is increased.
 *
 *
 * @author  EGRID - ICTP Trieste
 * @date    March 21st, 2005
 * @version 4.0
 */
public final class PtGFeeder implements Delegable {

    private static Logger log = LoggerFactory.getLogger(PtGFeeder.class);
    /* RequestSummaryData this PtGFeeder refers to. */
    private RequestSummaryData rsd = null;
    /* GridUser for this PtGFeeder. */
    private GridUserInterface gu = null;
    /* Overall request status. */
    private GlobalStatusManager gsm = null; 

    /**
     * Public constructor requiring the RequestSummaryData to which this PtGFeeder
     * refers, as well as the GridUser. If null objects are supplied, an
     * InvalidPtGFeederAttributesException is thrown.
	 * @param rsd
	 * @throws InvalidPtGFeederAttributesException
	 */
	public PtGFeeder(RequestSummaryData rsd) throws InvalidPtGFeederAttributesException {

		if(rsd == null)
		{
			throw new InvalidPtGFeederAttributesException(null, null, null);
		}
		if(rsd.gridUser() == null)
		{
			throw new InvalidPtGFeederAttributesException(rsd, null, null);
		}
		try
		{
			gu = rsd.gridUser();
			this.rsd = rsd;
			gsm = new GlobalStatusManager(rsd.requestToken());
		} catch(InvalidOverallRequestAttributeException e)
		{
			log.error("ATTENTION in PtGFeeder! Programming bug when creating GlobalStatusManager! "
				+ e);
			throw new InvalidPtGFeederAttributesException(rsd, gu, null);
		}
	}

    /**
     * This method splits a multifile request as well as exapanding recursive ones; it
     * then creates the necessary tasks and loads them into the PtG chunk scheduler.
     */
	public void doIt() {

		log.debug("PtGFeeder: pre-processing " + rsd.requestToken());
		// Get all parts in request
		Collection<PtGChunkData> chunks = PtGChunkCatalog.getInstance().lookup(rsd.requestToken());
		if(chunks.isEmpty())
		{
			log.warn("ATTENTION in PtGFeeder! This SRM PtG request contained nothing to process! "
				+ rsd.requestToken());
			RequestSummaryCatalog.getInstance().failRequest(rsd,
				"This SRM Get request contained nothing to process!");
		}
		else
		{
			manageChunks(chunks);
			log.debug("PtGFeeder: finished pre-processing " + rsd.requestToken());
		}
	}

    /**
     * Private method that handles the Collection of chunks associated with
     * the srm command!
	 */
	private void manageChunks(Collection<PtGChunkData> chunks) {

		log.debug("PtGFeeder - number of chunks in request: " + chunks.size());
		for(PtGChunkData chunkData : chunks)
		{
			gsm.addChunk(chunkData); // add chunk for global status
			// consideration
			if(TSURL.isValid(chunkData.fromSURL()))
			{
				/*
				 * fromSURL corresponds to This installation of StoRM: go on
				 * with processing!
				 */
				if(chunkData.dirOption().isDirectory())
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
				log.warn("PtGFeeder: srmPtG contract violation!"
					+ " fromSURL does not correspond to this machine!");
				log.warn("Request: " + rsd.requestToken());
				log.warn("Chunk: " + chunkData);
				
				chunkData.changeStatusSRM_FAILURE("SRM protocol violation!"
					+ " Cannot do an srmPtG of a SURL that is not local!");
				
				/* update persistence!!! */
				PtGChunkCatalog.getInstance().update(chunkData);
				/* inform global status computation of the chunk s failure */
				gsm.failedChunk(chunkData);
			}
		}
		/*
		 * no more chunks need to be cosidered for the overall status
		 * computation
		 */
		gsm.finishedAdding();
	}

    /**
     * Private method that handles the case of dirOption NOT set!
     * @param auxChunkData
     */
	private void manageNotDirectory(PtGChunkData auxChunkData) {

		log.debug("PtGFeeder - scheduling... ");
		/* change status of this chunk to being processed! */
		auxChunkData.changeStatusSRM_REQUEST_INPROGRESS("srmPrepareToGet "
			+ "chunk is being processed!");
		PtGChunkCatalog.getInstance().update(auxChunkData);
		try
		{
			/* hand it to scheduler! */
			SchedulerFacade.getInstance().chunkScheduler().schedule(
				new PtGChunk(gu, rsd, auxChunkData, gsm));
			log.debug("PtGFeeder - chunk scheduled.");
		} catch(InvalidPtGChunkAttributesException e)
		{
			/*
			 * for some reason gu, rsd or auxChunkData may be null! This should
			 * not be so!
			 */
			log.error("UNEXPECTED ERROR in PtGFeeder! Chunk could not be created!\n" + e);
			log.error("Request: " + rsd.requestToken());
			log.error("Chunk: " + auxChunkData);
			
			auxChunkData.changeStatusSRM_FAILURE("StoRM internal error does"
				+ " not allow this chunk to be processed!");
			
			PtGChunkCatalog.getInstance().update(auxChunkData);
			gsm.failedChunk(auxChunkData);
		} catch(SchedulerException e)
		{
			/* Internal error of scheduler! */
			log.error("UNEXPECTED ERROR in ChunkScheduler! " + "Chunk could not be scheduled!\n"
				+ e);
			log.error("Request: " + rsd.requestToken());
			log.error("Chunk: " + auxChunkData);
			
			auxChunkData.changeStatusSRM_FAILURE("StoRM internal scheduler "
				+ "error prevented this chunk from being processed!");
			
			PtGChunkCatalog.getInstance().update(auxChunkData);
			gsm.failedChunk(auxChunkData);
		}
	}

    /**
     * Private method that handles the case of a PtGChunkData having
     * dirOption set!
     * @param chunkData
     */
	private void manageIsDirectory(PtGChunkData chunkData) {

		log.debug("PtGFeeder - pre-processing Directory chunk...");
		/* Change status of this chunk to being processed! */
		chunkData.changeStatusSRM_REQUEST_INPROGRESS("srmPrepareToGet "
			+ "chunk is being processed!");
		/* update persistence!!! */
		PtGChunkCatalog.getInstance().update(chunkData);
		try
		{
			/* Build StoRI for current chunk */
			StoRI stori = NamespaceDirector.getNamespace().resolveStoRIbySURL(
								 chunkData.fromSURL(), gu);
			/* Collection of children! */
			//TODO MICHELE here if the recursion on directory is supported substiture the following withe the commented one
			Collection<StoRI> storiChildren = stori.getChildren(chunkData
				  .dirOption());
//			Collection<StoRI> storiChildren = stori.generateChildrenNoFolders(chunkData
//												  .dirOption());
			
			log.debug("PtGFeeder - Number of children in parent: " + storiChildren.size());
			
			// FIXME MICHELE why here we set the diroption in this way? maybe it
			// is correct in this other way:
			// new TDirOption(childStoRI.isDirectory() (this method doesn't
			// exists), (childStoRI.isDirectory() ? auxChunkData.dirOption() :
			// false).isAllLevelRecursive(), (childStoRI.isDirectory() ?
			// auxChunkData.dirOption().getNumLevel() > 0 ?
			// (auxChunkData.dirOption().getNumLevel()-1)) : 0 ) :0));
			TDirOption notDir = new TDirOption(false, false, 0);
			
			PtGChunkData childData;
			for(StoRI storiChild : storiChildren)
			{
				try
				{
					childData = new PtGChunkData(chunkData.requestToken(), storiChild.getSURL(),
									chunkData.getPinLifeTime(), notDir, chunkData
										.desiredProtocols(), chunkData.fileSize(), chunkData
										.status(), chunkData.transferURL());
					/* fill in new db row and set the PrimaryKey of ChildData! */
					PtGChunkCatalog.getInstance().addChild(childData);
					log.debug("PtGFeeder - added child data: " + childData);
					
					/* add chunk for global status consideration */
					gsm.addChunk(childData);
					/* manage chunk */
					manageNotDirectory(childData);
				} catch(InvalidPtGChunkDataAttributesException e)
				{
					/*
					 * For some reason it was not possible to create a
					 * PtGChunkData: it is a program bug!!! It should not
					 * occur!!! Log it and skip to the next one!
					 */
					log.error("ERROR in PtGFeeder! While expanding recursive request,"
						+ " it was not possible to create a new PtGChunkData! " + e);
				}
			}
			log.debug("PtGFeeder - expansion completed.");
			/*
			 * A request on a Directory is considered done whether there is
			 * somethig to expand or not!
			 */
			//FIXME MICHELE maybe here as in BOL we have to set the success status and not pinned
			chunkData.changeStatusSRM_FILE_PINNED("srmPrepareToGet with dirOption set: "
				+ "request successfully expanded!");
			PtGChunkCatalog.getInstance().update(chunkData);
			gsm.successfulChunk(chunkData);
		} catch(NamespaceException e)
		{
			/*
			 * The Supplied SURL does not contain a root that could be
			 * identified by the StoRI factory as referring to a VO being
			 * managed by StoRM... that is SURLs beginning with such root are not
			 * handled by this SToRM!
			 */
			chunkData.changeStatusSRM_INVALID_PATH("The path specified "
				+ "in the SURL does not have a local equivalent!");
			PtGChunkCatalog.getInstance().update(chunkData);
			
			log.debug("ATTENTION in PtGFeeder! PtGFeeder received "
				+ "request for a SURL whose root is not recognised by StoRI!"); // info
			gsm.failedChunk(chunkData);
		} catch(InvalidTDirOptionAttributesException e)
		{
			// Could not create TDirOption that specifies no-expansion!
			chunkData.changeStatusSRM_FAILURE("srmPrepareToGet with dirOption set:"
				+ " expansion failure due to internal error!");
			PtGChunkCatalog.getInstance().update(chunkData);
			
			log.error("UNEXPECTED ERROR in PtGFeeder! Could not"
				+ " create TDirOption specifying non-expansion!\n" + e);
			log.error("Request: " + rsd.requestToken());
			log.error("Chunk: " + chunkData);
			gsm.failedChunk(chunkData);
		} catch(InvalidDescendantsEmptyRequestException e)
		{
			/*
			 * The expanded directory was empty, anyway a request on a Directory is considered done whether there is
			 * somethig to expand or not!
			 */
			chunkData.changeStatusSRM_FILE_PINNED("BEWARE! srmPrepareToGet with dirOption"
				+ " set: it referred to a directory that was empty!");
			PtGChunkCatalog.getInstance().update(chunkData);
			
			log.debug("ATTENTION in PtGFeeder! PtGFeeder received "
				+ "request to expand empty directory.");
			gsm.successfulChunk(chunkData);
		}
		//TODO MICHELE here if the recursion on directory is supported uncomment the following
//		catch(InvalidDescendantsTDirOptionRequestException e)
//		{
//			// The expanded directory was empty
//			chunkData
//				.changeStatusSRM_FAILURE("BEWARE! srmPrepareToGet with dirOption set:"
//					+ " it was impossible to expand the directory with the provided directory options!");
//			PtGChunkCatalog.getInstance().update(chunkData);
//			
//			log.debug("ATTENTION in PtGFeeder! PtGFeeder received request"
//				+ " to expand a directory with wrong TDirOptions.");
//			gsm.failedChunk(chunkData);
//		}
		catch(InvalidDescendantsPathRequestException e)
		{
			// Attempting to expand non existent directory!
			chunkData.changeStatusSRM_INVALID_PATH("srmPrepareToGet with dirOption set:"
				+ " it referred to a non-existent directory!");
			PtGChunkCatalog.getInstance().update(chunkData);
			
			log.debug("ATTENTION in PtGFeeder! PtGFeeder received request"
				+ " to expand non-existing directory.");
			gsm.failedChunk(chunkData);
		} catch(InvalidDescendantsFileRequestException e)
		{
			// Attempting to expand a file!
			chunkData.changeStatusSRM_INVALID_PATH("srmPrepareToGet with dirOption set:"
				+ " a file was asked to be expanded!");
			PtGChunkCatalog.getInstance().update(chunkData); 
			
			log.debug("ATTENTION in PtGFeeder! PtGFeeder received request to expand a file.");
			gsm.failedChunk(chunkData);
		} catch(InvalidDescendantsAuthRequestException e)
		{
			// No rights to directory!
			chunkData
				.changeStatusSRM_AUTHORIZATION_FAILURE("srmPrepareToGet with dirOption set:"
					+ " user has no right to access directory!");
			PtGChunkCatalog.getInstance().update(chunkData);
			
			log.debug("ATTENTION in PtGFeeder! PtGFeeder received request to"
				+ " expand a directory for which the user has no rights."); // info
			gsm.failedChunk(chunkData);
		}
	}

	/**
	 * Method used by chunk scheduler for internal logging; it returns the
	 * request id of This PtGFeeder!
	 */
	public String getName() {

		return "PtGFeeder of request: " + rsd.requestToken();
	}
}
