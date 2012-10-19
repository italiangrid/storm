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

package it.grid.storm.catalogs;

import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.common.types.TimeUnit;
import it.grid.storm.config.Configuration;
import it.grid.storm.griduser.GridUserInterface;
//import it.grid.storm.namespace.SurlStatusStore;
import it.grid.storm.srm.types.InvalidTDirOptionAttributesException;
import it.grid.storm.srm.types.InvalidTLifeTimeAttributeException;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TTURL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * Class that represents StoRMs BoLChunkCatalog: it collects BoLChunkData and
 * provides methods for looking up a BoLChunkData based on TRequestToken, as
 * well as for adding a new entry and removing an existing one.
 * 
 * @author CNAF
 * @date Aug 2009
 * @version 1.0
 */
public class BoLChunkCatalog
{
	private static final Logger log = LoggerFactory.getLogger(BoLChunkCatalog.class);

	/* only instance of BoLChunkCatalog present in StoRM! */
	private static final BoLChunkCatalog cat = new BoLChunkCatalog();
	private final BoLChunkDAO dao = BoLChunkDAO.getInstance();

	/*
	 * Timer object in charge of transiting expired requests from
	 * SRM_FILE_PINNED to SRM_RELEASED!	 */
	private final Timer transiter = new Timer();
	/* Delay time before starting cleaning thread! */
	private final long delay = Configuration.getInstance().getTransitInitialDelay() * 1000;
	/* Period of execution of cleaning! */
	private final long period = Configuration.getInstance().getTransitTimeInterval() * 1000;

	/**
	 * Private constructor that starts the internal timer needed to periodically
	 * check and transit requests whose pinLifetime has expired and are in
	 * SRM_FILE_PINNED, to SRM_RELEASED.
	 */
	private BoLChunkCatalog() {

		TimerTask transitTask = new TimerTask()
		{
			@Override
			public void run() {

				transitExpiredSRM_SUCCESS();
			}
		};
		transiter.scheduleAtFixedRate(transitTask, delay, period);
	}

	/**
	 * Method that returns the only instance of BoLChunkCatalog available.
	 */
	public static BoLChunkCatalog getInstance() {

		return cat;
	}

	/**
	 * Method that returns a Collection of BoLChunkData Objects matching the
	 * supplied TRequestToken.
	 * 
	 * If any of the data associated to the TRequestToken is not well formed and
	 * so does not allow a BoLChunkData Object to be created, then that part of
	 * the request is dropped and gets logged, and the processing continues with
	 * the next part. All valid chunks get returned: the others get dropped.
	 * 
	 * If there are no chunks to process then an empty Collection is returned,
	 * and a message gets logged.
	 */
	synchronized public Collection<BoLPersistentChunkData> lookup(TRequestToken rt) {
		
		Collection<BoLChunkDataTO> chunkCollection = dao.find(rt);
		log.debug("BoL CHUNK CATALOG: retrieved data " + chunkCollection);
		List<BoLPersistentChunkData> list = new ArrayList<BoLPersistentChunkData>();

		if(chunkCollection.isEmpty())
		{
			log.warn("BoL CHUNK CATALOG! No chunks found in persistence for specified request: "
				+ rt);
		}
		else
		{
			// TODO MICHELE USER_SURL here I can update all requests that has
			// not the new fields set alltogether in a bunch, adding a method to
			// the DAO for this purpose
			BoLPersistentChunkData chunk;
			for(BoLChunkDataTO chunkTO : chunkCollection)
			{
				chunk = makeOne(chunkTO, rt);
				if(chunk != null)
				{
					list.add(chunk);
					// TODO MICHELE SURL STORE
//					SurlStatusStore.getInstance().storeSurlStatus(chunk.getSURL(), chunk.getStatus().getStatusCode());
					if(!this.isComplete(chunkTO))
					{
						try
						{
							dao.updateIncomplete(this.completeTO(chunkTO, chunk));
						} catch(InvalidReducedBoLChunkDataAttributesException e)
						{
							log
								.warn("PtG CHUNK CATALOG! unable to add missing informations on DB to the request: "
									+ e);
						}
					}
				}
			}
		}
		log.debug("BoL CHUNK CATALOG: returning " + list);
		return list;
	}

	/**
	 * Generates a BoLChunkData from the received BoLChunkDataTO
	 * 
	 * @param auxTO
	 * @param rt
	 * @return
	 */
	private BoLPersistentChunkData makeOne(BoLChunkDataTO auxTO, TRequestToken rt) {

		StringBuffer errorSb = new StringBuffer();
		TSURL fromSURL = null;
		try
		{
			fromSURL = TSURL.makeFromStringValidate(auxTO.getFromSURL());
		} catch(InvalidTSURLAttributesException e)
		{
			errorSb.append(e);
		}
		if(auxTO.normalizedStFN() != null)
		{
			fromSURL.setNormalizedStFN(auxTO.normalizedStFN());
		}
		if(auxTO.sulrUniqueID() != null)
		{
			fromSURL.setUniqueID(auxTO.sulrUniqueID().intValue());
		}
		// lifeTime
		TLifeTimeInSeconds lifeTime = null;
		try
		{
			long pinLifeTime = PinLifetimeConverter.getInstance().toStoRM(auxTO.getLifeTime());
			// Check for max value allowed
			long max = Configuration.getInstance().getPinLifetimeMaximum();
			if(pinLifeTime > max)
			{
				log.warn("PinLifeTime is greater than the max value allowed. "
					+ "Drop the value to the max = " + max + " seconds");
				pinLifeTime = max;
			}
			lifeTime = TLifeTimeInSeconds.make(pinLifeTime, TimeUnit.SECONDS);
		} catch(InvalidTLifeTimeAttributeException e)
		{
			errorSb.append("\n");
			errorSb.append(e);
		}
		// dirOption
		TDirOption dirOption = null;
		try
		{
			dirOption =
						new TDirOption(auxTO.getDirOption(), auxTO.getAllLevelRecursive(), auxTO
							.getNumLevel());
		} catch(InvalidTDirOptionAttributesException e)
		{
			errorSb.append("\n");
			errorSb.append(e);
		}
		// transferProtocols
		TURLPrefix transferProtocols =
									   TransferProtocolListConverter.toSTORM(auxTO
										   .getProtocolList());
		if(transferProtocols.size() == 0)
		{
			errorSb.append("\nEmpty list of TransferProtocols or"
				+ " could not translate TransferProtocols!");
			/* fail construction of BoLChunkData! */
			transferProtocols = null;
		}
		// fileSize
		TSizeInBytes fileSize = null;
		try
		{
			fileSize = TSizeInBytes.make(auxTO.getFileSize(), SizeUnit.BYTES);
		} catch(InvalidTSizeAttributesException e)
		{
			errorSb.append("\n");
			errorSb.append(e);
		}
		// status
		TReturnStatus status = null;
		TStatusCode code = StatusCodeConverter.getInstance().toSTORM(auxTO.getStatus());
		if(code == TStatusCode.EMPTY)
		{
			errorSb.append("\nRetrieved StatusCode was not recognised: " + auxTO.getStatus());
		}
		else
		{
			try
			{
				status = new TReturnStatus(code, auxTO.getErrString());
			} catch(InvalidTReturnStatusAttributeException e)
			{
				errorSb.append("\n");
				errorSb.append(e);
			}
		}
		// transferURL
		/*
		 * whatever is read is just meaningless because BoL will fill it in!!!
		 * So create an Empty TTURL by default! Vital to avoid problems with
		 * unknown DPM NULL/EMPTY logic policy!
		 */
		TTURL transferURL = TTURL.makeEmpty();
		// make BoLChunkData
		BoLPersistentChunkData aux = null;
		try
		{
			aux =
				  new BoLPersistentChunkData(rt, fromSURL, lifeTime, dirOption, transferProtocols, fileSize,
					  status, transferURL, auxTO.getDeferredStartTime());
			aux.setPrimaryKey(auxTO.getPrimaryKey());
		} catch(InvalidSurlRequestDataAttributesException e)
		{
			dao.signalMalformedBoLChunk(auxTO);
			log.warn("BoL CHUNK CATALOG! Retrieved malformed BoL "
				+ "chunk data from persistence. Dropping chunk from request " + rt);
			log.warn(e.getMessage(), e);
			log.warn(errorSb.toString());
		}
		// end...
		return aux;
	}

	/**
	 * 
	 * Adds to the received BoLChunkDataTO the normalized StFN and the SURL
	 * unique ID taken from the BoLChunkData
	 * 
	 * @param chunkTO
	 * @param chunk
	 */
	private void completeTO(ReducedBoLChunkDataTO chunkTO, final ReducedBoLChunkData chunk) {

		chunkTO.setNormalizedStFN(chunk.fromSURL().normalizedStFN());
		chunkTO.setSurlUniqueID(new Integer(chunk.fromSURL().uniqueId()));
	}

	/**
	 * 
	 * Creates a ReducedBoLChunkDataTO from the received BoLChunkDataTO and
	 * completes it with the normalized StFN and the SURL unique ID taken from
	 * the PtGChunkData
	 * 
	 * @param chunkTO
	 * @param chunk
	 * @return
	 * @throws InvalidReducedBoLChunkDataAttributesException
	 */
	private ReducedBoLChunkDataTO completeTO(BoLChunkDataTO chunkTO, final BoLPersistentChunkData chunk)
			throws InvalidReducedBoLChunkDataAttributesException {

		ReducedBoLChunkDataTO reducedChunkTO = this.reduce(chunkTO);
		this.completeTO(reducedChunkTO, this.reduce(chunk));
		return reducedChunkTO;
	}

	/**
	 * Creates a ReducedBoLChunkData from the data contained in the received
	 * BoLChunkData
	 * 
	 * @param chunk
	 * @return
	 * @throws InvalidReducedBoLChunkDataAttributesException
	 */
	private ReducedBoLChunkData reduce(BoLPersistentChunkData chunk)
			throws InvalidReducedBoLChunkDataAttributesException {

		ReducedBoLChunkData reducedChunk =
										   new ReducedBoLChunkData(chunk.getSURL(), chunk
											   .getStatus());
		reducedChunk.setPrimaryKey(chunk.getPrimaryKey());
		return reducedChunk;
	}

	/**
	 * Creates a ReducedBoLChunkDataTO from the data contained in the received
	 * BoLChunkDataTO
	 * 
	 * @param chunkTO
	 * @return
	 */
	private ReducedBoLChunkDataTO reduce(BoLChunkDataTO chunkTO) {

		ReducedBoLChunkDataTO reducedChunkTO = new ReducedBoLChunkDataTO();
		reducedChunkTO.setPrimaryKey(chunkTO.getPrimaryKey());
		reducedChunkTO.setFromSURL(chunkTO.getFromSURL());
		reducedChunkTO.setNormalizedStFN(chunkTO.normalizedStFN());
		reducedChunkTO.setSurlUniqueID(chunkTO.sulrUniqueID());
		reducedChunkTO.setStatus(chunkTO.getStatus());
		reducedChunkTO.setErrString(chunkTO.getErrString());
		return reducedChunkTO;
	}

	/**
	 * Checks if the received BoLChunkDataTO contains the fields not set by the
	 * front end but required
	 * 
	 * @param chunkTO
	 * @return
	 */
	private boolean isComplete(BoLChunkDataTO chunkTO) {

		return (chunkTO.normalizedStFN() != null) && (chunkTO.sulrUniqueID() != null);
	}

	/**
	 * Checks if the received ReducedBoLChunkDataTO contains the fields not set
	 * by the front end but required
	 * 
	 * @param reducedChunkTO
	 * @return
	 */
	// TODO MICHELE USER_SURL new method
	private boolean isComplete(ReducedBoLChunkDataTO reducedChunkTO) {

		return (reducedChunkTO.normalizedStFN() != null) && (reducedChunkTO.surlUniqueID() != null);
	}

	/**
	 * Method used to update into Persistence a retrieved BoLChunkData. In case
	 * any error occurs, the operation does not proceed but no Exception is
	 * thrown. Error messages get logged.
	 * 
	 * Only fileSize, StatusCode, errString and transferURL are updated.
	 * Likewise for the request pinLifetime.
	 */
	synchronized public void update(BoLPersistentChunkData cd) {
			
		BoLChunkDataTO to = new BoLChunkDataTO();
		/* Primary key needed by DAO Object */
		to.setPrimaryKey(cd.getPrimaryKey());
		to.setFileSize(cd.getFileSize().value());
		to.setStatus(StatusCodeConverter.getInstance().toDB(cd.getStatus().getStatusCode()));
		to.setErrString(cd.getStatus().getExplanation());
		to.setLifeTime(PinLifetimeConverter.getInstance().toDB(cd.getLifeTime().value()));
		// TODO MICHELE USER_SURL fill new fields
		to.setNormalizedStFN(cd.getSURL().normalizedStFN());
		to.setSurlUniqueID(new Integer(cd.getSURL().uniqueId()));

		dao.update(to);
		// TODO MICHELE SURL STORE
//        SurlStatusStore.getInstance().storeSurlStatus(cd.getSURL(), cd.getStatus().getStatusCode());
	}

	/**
	 * Refresh method. TODO THIS IS A WORK IN PROGRESS!!!! This method have to
	 * synch the ChunkData information with the database status.
	 * 
	 * @param auxTO
	 * @param BoLPersistentChunkData
	 *            inputChunk
	 * @return BoLChunkData outputChunk
	 */
	synchronized public BoLPersistentChunkData refreshStatus(BoLPersistentChunkData inputChunk) {
		/* Currently not used*/
		// Call the dao refresh method to synch with the db status
		BoLChunkDataTO auxTO = dao.refresh(inputChunk.getPrimaryKey());

		log.debug("BoL CHUNK CATALOG: retrieved data " + auxTO);
		if(auxTO == null)
		{
			log.warn("BoL CHUNK CATALOG! Empty TO found in persistence for specified request: "
				+ inputChunk.getPrimaryKey());
		}
		else
		{
			/*
			 * In this first version the only field updated is the Status. Once
			 * updated, the new status is rewritten into the input ChunkData
			 */

			// status
			TReturnStatus status = null;
			TStatusCode code = StatusCodeConverter.getInstance().toSTORM(auxTO.getStatus());
			if(code != TStatusCode.EMPTY)
			{
				try
				{
					status = new TReturnStatus(code, auxTO.getErrString());
				} catch(InvalidTReturnStatusAttributeException e)
				{
					log.debug("BoL Chunk: Unable to build the Return Status from the String '"
						+ auxTO.getErrString() + " and code '" + auxTO.getStatus() + "''." + e);
				}
			}
			inputChunk.setStatus(status);
			// TODO MICHELE SURL STORE
//			SurlStatusStore.getInstance().storeSurlStatus(inputChunk.getSURL(), status.getStatusCode());
		}
		return inputChunk;
	}

	/**
	 * Method that returns a Collection of ReducedBoLChunkData Objects
	 * associated to the supplied TRequestToken.
	 * 
	 * If any of the data retrieved for a given chunk is not well formed and so
	 * does not allow a ReducedBoLChunkData Object to be created, then that
	 * chunk is dropped and gets logged, while processing continues with the
	 * next one. All valid chunks get returned: the others get dropped.
	 * 
	 * If there are no chunks associated to the given TRequestToken, then an
	 * empty Collection is returned and a messagge gets logged.
	 */
	synchronized public Collection<ReducedBoLChunkData> lookupReducedBoLChunkData(TRequestToken rt) {
		
		Collection<ReducedBoLChunkDataTO> reducedChunkDataTOs = dao.findReduced(rt.getValue());
		log.debug("BoL CHUNK CATALOG: retrieved data " + reducedChunkDataTOs);
		ArrayList<ReducedBoLChunkData> list = new ArrayList<ReducedBoLChunkData>();
		if(reducedChunkDataTOs.isEmpty())
		{
			log.debug("BoL CHUNK CATALOG! No chunks found in persistence for " + rt);
		}
		else
		{
			ReducedBoLChunkData reducedChunkData = null;
			for(ReducedBoLChunkDataTO reducedChunkDataTO : reducedChunkDataTOs)
			{
				reducedChunkData = makeOneReduced(reducedChunkDataTO);
				if(reducedChunkData != null)
				{
					list.add(reducedChunkData);
					// TODO MICHELE SURL STORE
//					SurlStatusStore.getInstance().storeSurlStatus(reducedChunkData.fromSURL(), reducedChunkData.status().getStatusCode());
					if(!this.isComplete(reducedChunkDataTO))
					{
						this.completeTO(reducedChunkDataTO, reducedChunkData);
						dao.updateIncomplete(reducedChunkDataTO);
					}
				}
			}
			log.debug("BoL CHUNK CATALOG: returning " + list);
		}
		return list;
	}
	
    public Collection<ReducedBoLChunkData> lookupReducedBoLChunkData(TRequestToken requestToken,
                                                                     Collection<TSURL> surls)
    {
        int[] surlsUniqueIDs = new int[surls.size()];
        String[] surlsArray = new String[surls.size()];
        int index = 0;
        for (TSURL tsurl : surls)
        {
            surlsUniqueIDs[index] = tsurl.uniqueId();
            surlsArray[index] = tsurl.rawSurl();
            index++;
        }
        Collection<ReducedBoLChunkDataTO> chunkDataTOCollection = dao.findReduced(requestToken, surlsUniqueIDs,
                                                                                  surlsArray);
        return buildReducedChunkDataList(chunkDataTOCollection);
    }
    
    public Collection<BoLPersistentChunkData> lookupBoLChunkData(TSURL surl)
    {
        return lookupBoLChunkData(Arrays.asList(new TSURL[]{surl}));
    }
    
    public Collection<BoLPersistentChunkData> lookupBoLChunkData(List<TSURL> surls)
    {
        int[] surlsUniqueIDs = new int[surls.size()];
        String[] surlsArray = new String[surls.size()];
        int index = 0;
        for (TSURL tsurl : surls)
        {
            surlsUniqueIDs[index] = tsurl.uniqueId();
            surlsArray[index] = tsurl.rawSurl();
            index++;
        }
        Collection<BoLChunkDataTO> chunkDataTOCollection = dao.find(surlsUniqueIDs, surlsArray);
        log.debug("PtG CHUNK CATALOG: retrieved data " + chunkDataTOCollection);
        return buildChunkDataList(chunkDataTOCollection);
    }

	private Collection<BoLPersistentChunkData> buildChunkDataList(
            Collection<BoLChunkDataTO> chunkDataTOCollection)
    {
	    List<BoLPersistentChunkData> list = new ArrayList<BoLPersistentChunkData>();
        // TODO MICHELE USER_SURL here I can update all requests that has
        // not the new fields set alltogether in a bunch, adding a method to
        // the DAO for this purpose
        BoLPersistentChunkData chunk;
        for(BoLChunkDataTO chunkTO : chunkDataTOCollection)
        {
            chunk = makeOne(chunkTO);
            if(chunk != null)
            {
                list.add(chunk);
                // TODO MICHELE SURL STORE
//                  SurlStatusStore.getInstance().storeSurlStatus(chunk.getSURL(), chunk.getStatus().getStatusCode());
                if(!this.isComplete(chunkTO))
                {
                    try
                    {
                        dao.updateIncomplete(this.completeTO(chunkTO, chunk));
                    } catch(InvalidReducedBoLChunkDataAttributesException e)
                    {
                        log
                            .warn("PtG CHUNK CATALOG! unable to add missing informations on DB to the request: "
                                + e);
                    }
                }
            }
        }
        log.debug("BoL CHUNK CATALOG: returning " + list);
        return list;
    }

    private BoLPersistentChunkData makeOne(BoLChunkDataTO chunkTO)
    {
        try
        {
            return makeOne(chunkTO, new TRequestToken(chunkTO.getRequestToken(), chunkTO.getTimeStamp()));
        } catch(InvalidTRequestTokenAttributesException e)
        {
            throw new IllegalStateException("Unexpected InvalidTRequestTokenAttributesException in TRequestToken: " + e);
        }
    }

    /**
	 * Method that returns a Collection of ReducedBoLChunkData Objects matching
	 * the supplied GridUser and Collection of TSURLs.
	 * 
	 * If any of the data retrieved for a given chunk is not well formed and so
	 * does not allow a ReducedBoLChunkData Object to be created, then that
	 * chunk is dropped and gets logged, while processing continues with the
	 * next one. All valid chunks get returned: the others get dropped.
	 * 
	 * If there are no chunks associated to the given GridUser and Collection of
	 * TSURLs, then an empty Collection is returned and a message gets logged.
	 */
	synchronized public Collection<ReducedBoLChunkData> lookupReducedBoLChunkData(
			GridUserInterface gu, Collection<TSURL> tsurlCollection) {
		
		int[] surlsUniqueIDs = new int[tsurlCollection.size()];
		String[] surls = new String[tsurlCollection.size()];
		int index = 0;
		for(TSURL tsurl : tsurlCollection)
		{
			surlsUniqueIDs[index] = tsurl.uniqueId();
			surls[index] = tsurl.rawSurl();
			index++;
		}
		Collection<ReducedBoLChunkDataTO> chunkDataTOCollection =
																  dao.findReduced(gu.getDn(),
																	  surlsUniqueIDs, surls);
		log.debug("BoL CHUNK CATALOG: retrieved data " + chunkDataTOCollection);
		return buildReducedChunkDataList(chunkDataTOCollection);
//		ArrayList<ReducedBoLChunkData> list = new ArrayList<ReducedBoLChunkData>();
//		if(chunkDataTOCollection.isEmpty())
//		{
//			log.debug("BoL CHUNK CATALOG! No chunks found in persistence for " + gu + " " + chunkDataTOCollection);
//		}
//		else
//		{
//			ReducedBoLChunkData reducedChunkData;
//			for(ReducedBoLChunkDataTO reducedChunkDataTO : chunkDataTOCollection)
//			{
//				reducedChunkData = makeOneReduced(reducedChunkDataTO);
//				if(reducedChunkData != null)
//				{
//					list.add(reducedChunkData);
//					// TODO MICHELE SURL STORE
////					SurlStatusStore.getInstance().storeSurlStatus(reducedChunkData.fromSURL(), reducedChunkData.status().getStatusCode());
//					if(!this.isComplete(reducedChunkDataTO))
//					{
//						this.completeTO(reducedChunkDataTO, reducedChunkData);
//						dao.updateIncomplete(reducedChunkDataTO);
//					}
//				}
//			}
//			log.debug("BoL CHUNK CATALOG: returning " + list);
//		}
//		return list;
	}

	
	private Collection<ReducedBoLChunkData> buildReducedChunkDataList(
            Collection<ReducedBoLChunkDataTO> chunkDataTOCollection)
    {
	    ArrayList<ReducedBoLChunkData> list = new ArrayList<ReducedBoLChunkData>();
        ReducedBoLChunkData reducedChunkData;
        for(ReducedBoLChunkDataTO reducedChunkDataTO : chunkDataTOCollection)
        {
            reducedChunkData = makeOneReduced(reducedChunkDataTO);
            if(reducedChunkData != null)
            {
                list.add(reducedChunkData);
                // TODO MICHELE SURL STORE
//                  SurlStatusStore.getInstance().storeSurlStatus(reducedChunkData.fromSURL(), reducedChunkData.status().getStatusCode());
                if(!this.isComplete(reducedChunkDataTO))
                {
                    this.completeTO(reducedChunkDataTO, reducedChunkData);
                    dao.updateIncomplete(reducedChunkDataTO);
                }
            }
        }
        log.debug("BoL CHUNK CATALOG: returning " + list);
        return list;
    }

    /**
	 * @param auxTO
	 * @return
	 */
	private ReducedBoLChunkData makeOneReduced(ReducedBoLChunkDataTO reducedChunkDataTO) {

		StringBuffer errorSb = new StringBuffer();
		// fromSURL
		TSURL fromSURL = null;
		try
		{
			fromSURL = TSURL.makeFromStringValidate(reducedChunkDataTO.fromSURL());
		} catch(InvalidTSURLAttributesException e)
		{
			errorSb.append(e);
		}
		if(reducedChunkDataTO.normalizedStFN() != null)
		{
			fromSURL.setNormalizedStFN(reducedChunkDataTO.normalizedStFN());
		}
		if(reducedChunkDataTO.surlUniqueID() != null)
		{
			fromSURL.setUniqueID(reducedChunkDataTO.surlUniqueID().intValue());
		}
		// status
		TReturnStatus status = null;
		TStatusCode code = StatusCodeConverter.getInstance().toSTORM(reducedChunkDataTO.status());
		if(code == TStatusCode.EMPTY)
		{
			errorSb.append("\nRetrieved StatusCode was not recognised: " 
				+ reducedChunkDataTO.status());
		}
		else
		{
			try
			{
				status = new TReturnStatus(code, reducedChunkDataTO.errString());
			} catch(InvalidTReturnStatusAttributeException e)
			{
				errorSb.append("\n");
				errorSb.append(e);
			}
		}
		// make ReducedBoLChunkData
		ReducedBoLChunkData aux = null;
		try
		{
			aux = new ReducedBoLChunkData(fromSURL, status);
			aux.setPrimaryKey(reducedChunkDataTO.primaryKey());
		} catch(InvalidReducedBoLChunkDataAttributesException e)
		{
			log.warn("BoL CHUNK CATALOG! Retrieved malformed "
				+ "Reduced BoL chunk data from persistence: dropping reduced chunk...");
			log.warn(e.getMessage(), e);
			log.warn(errorSb.toString());
		}
		// end...
		return aux;
	}

	/**
	 * Method used to add into Persistence a new entry. The supplied
	 * BoLChunkData gets the primary key changed to the value assigned in
	 * Persistence.
	 * 
	 * This method is intended to be used by a recursive BoL request: the parent
	 * request supplies a directory which must be expanded, so all new children
	 * requests resulting from the files in the directory are added into
	 * persistence.
	 * 
	 * So this method does _not_ add a new SRM prepare_to_get request into the
	 * DB!
	 * 
	 * The only children data written into the DB are: sourceSURL, TDirOption,
	 * statusCode and explanation.
	 * 
	 * In case of any error the operation does not proceed, but no Exception is
	 * thrown! Proper messages get logged by underlaying DAO.
	 */
	synchronized public void addChild(BoLPersistentChunkData chunkData) {
		
		BoLChunkDataTO to = new BoLChunkDataTO();
		// needed for now to find ID of request! Must be changed soon!
		to.setRequestToken(chunkData.getRequestToken().toString()); 
		to.setFromSURL(chunkData.getSURL().toString());
        to.setNormalizedStFN(chunkData.getSURL().normalizedStFN());
        to.setSurlUniqueID(new Integer(chunkData.getSURL().uniqueId()));
        
		to.setAllLevelRecursive(chunkData.getDirOption().isAllLevelRecursive());
		to.setDirOption(chunkData.getDirOption().isDirectory());
		to.setNumLevel(chunkData.getDirOption().getNumLevel());
		to.setStatus(StatusCodeConverter.getInstance().toDB(chunkData.getStatus().getStatusCode()));
		to.setErrString(chunkData.getStatus().getExplanation());
		to.setDeferredStartTime(chunkData.getDeferredStartTime());
		
		/*  add the entry and update the Primary Key field */
		dao.addChild(to);
		// TODO MICHELE SURL STORE
//		SurlStatusStore.getInstance().storeSurlStatus(chunkData.getSURL(), chunkData.getStatus().getStatusCode());
        /* set the assigned PrimaryKey! */
		chunkData.setPrimaryKey(to.getPrimaryKey()); 
	}

	/**
	 * Method used to add into Persistence a new entry. The supplied
	 * BoLChunkData gets the primary key changed to the value assigned in the
	 * Persistence. The method requires the GridUser to whom associate the added
	 * request.
	 * 
	 * This method is intended to be used by an srmCopy request in push mode
	 * which implies a local srmBoL. The only fields from BoLChunkData that are
	 * considered are: the requestToken, the sourceSURL, the pinLifetime, the
	 * dirOption, the protocolList, the status and error string.
	 * 
	 * So this method _adds_ a new SRM prepare_to_get request into the DB!
	 * 
	 * In case of any error the operation does not proceed, but no Exception is
	 * thrown! The underlaying DAO logs proper error messages.
	 */
	synchronized public void add(BoLPersistentChunkData chunkData, GridUserInterface gu) {
		/* Currently NOT used*/
		BoLChunkDataTO to = new BoLChunkDataTO();
		to.setRequestToken(chunkData.getRequestToken().toString());
		to.setFromSURL(chunkData.getSURL().toString());
		//TODO MICHELE USER_SURL fill new fields
        to.setNormalizedStFN(chunkData.getSURL().normalizedStFN());
        to.setSurlUniqueID(new Integer(chunkData.getSURL().uniqueId()));
        
		to.setLifeTime(new Long(chunkData.getLifeTime().value()).intValue());
		to.setAllLevelRecursive(chunkData.getDirOption().isAllLevelRecursive());
		to.setDirOption(chunkData.getDirOption().isDirectory());
		to.setNumLevel(chunkData.getDirOption().getNumLevel());
		to.setProtocolList(TransferProtocolListConverter.toDB(chunkData.getTransferProtocols()));
		to.setStatus(StatusCodeConverter.getInstance().toDB(chunkData.getStatus().getStatusCode()));
		to.setErrString(chunkData.getStatus().getExplanation());
		to.setDeferredStartTime(chunkData.getDeferredStartTime());
		
		/* add the entry and update the Primary Key field! */
		dao.addNew(to, gu.getDn());
		// TODO MICHELE SURL STORE
//		SurlStatusStore.getInstance().storeSurlStatus(chunkData.getSURL(), chunkData.getStatus().getStatusCode());
        /* set the assigned PrimaryKey! */
		chunkData.setPrimaryKey(to.getPrimaryKey()); 
	}

	
	/**
	 * Method used to establish if in Persistence there is a BoLChunkData
	 * working on the supplied SURL, and whose state is SRM_FILE_PINNED, in
	 * which case true is returned. In case none are found or there is any
	 * problem, false is returned. This method is intended to be used by srmMv.
	 */
	synchronized public boolean isSRM_FILE_PINNED(TSURL surl) {
		
		return (dao.numberInSRM_SUCCESS(surl.uniqueId()) > 0);
		// TODO MICHELE SURL STORE
//		return TStatusCode.SRM_SUCCESS.equals(SurlStatusStore.getInstance().getSurlStatus(surl));
	}

	/**
	 * Method used to transit the specified Collection of ReducedBoLChunkData
	 * from SRM_FILE_PINNED to SRM_RELEASED. Chunks in any other starting state
	 * are not transited. In case of any error nothing is done, but proper error
	 * messages get logged by the underlaying DAO.
	 */
	synchronized public void transitSRM_SUCCESStoSRM_RELEASED(
			Collection<ReducedBoLChunkData> chunks, TRequestToken token) {
				
		if(chunks == null || chunks.isEmpty())
		{
			return;
		}

		long[] primaryKeys = new long[chunks.size()];
		int index = 0;
		for(ReducedBoLChunkData chunkData : chunks)
		{
			if(chunkData != null)
			{
			 // TODO MICHELE SURL STORE
//			    SurlStatusStore.getInstance().storeSurlStatus(chunkData.fromSURL(), chunkData.status().getStatusCode());
				primaryKeys[index] = chunkData.primaryKey();
				index++;
			}
		}
		dao.transitSRM_SUCCESStoSRM_RELEASED(primaryKeys, token);
	}

	/**
	 * This method is intended to be used by srmRm to transit all BoL chunks on
	 * the given SURL which are in the SRM_FILE_PINNED state, to SRM_ABORTED.
	 * The supplied String will be used as explanation in those chunks return
	 * status. The global status of the request is _not_ changed.
	 * 
	 * The TURL of those requests will automatically be set to empty. Notice
	 * that both removeAllJit(SURL) and removeVolatile(SURL) are automatically
	 * invoked on PinnedFilesCatalog, to remove any entry and corresponding
	 * physical ACLs.
	 * 
	 * Beware, that the chunks may be part of requests that have finished, or
	 * that still have not finished because other chunks are being processed.
	 */
	synchronized public void transitSRM_SUCCESStoSRM_ABORTED(TSURL surl, String explanation) {
		/* Currently NOT used*/		
		if(explanation == null)
		{
			explanation = "";
		}
		dao.transitSRM_SUCCESStoSRM_ABORTED(surl.uniqueId(), surl.toString(), explanation);
		// TODO MICHELE SURL STORE
//		SurlStatusStore.getInstance().storeSurlStatus(surl,TStatusCode.SRM_ABORTED);
		// PinnedFilesCatalog.getInstance().removeAllJit(surl);
		// PinnedfilesCatalog.getInstance().removeVolatile(surl);
	}

	/**
	 * Method used to force transition to SRM_RELEASED from SRM_FILE_PINNED, of
	 * all BoL Requests whose pinLifetime has expired and the state still has
	 * not been changed (a user forgot to run srmReleaseFiles)!
	 */
	synchronized public void transitExpiredSRM_SUCCESS() {
	    dao.transitExpiredSRM_SUCCESS();
	 // TODO MICHELE SURL STORE
//	    for (TSURL surl : expiredSurls)
//        {
//            SurlStatusStore.getInstance().storeSurlStatus(surl, TStatusCode.SRM_RELEASED);
//        }
	}

    public void updateFromPreviousStatus(TRequestToken requestToken, List<TSURL> surlList,
            TStatusCode expectedStatusCode, TStatusCode newStatusCode)
    {
        int[] surlsUniqueIDs = new int[surlList.size()];
        String[] surls = new String[surlList.size()];
        int index = 0;
        for(TSURL tsurl : surlList)
        {
            surlsUniqueIDs[index] = tsurl.uniqueId();
            surls[index] = tsurl.rawSurl();
            index++;
        }
        dao.updateStatusOnMatchingStatus(requestToken, surlsUniqueIDs, surls,
                                         expectedStatusCode, newStatusCode);
    }
}
