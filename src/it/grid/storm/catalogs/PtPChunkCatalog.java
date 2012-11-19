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
import it.grid.storm.griduser.AbstractGridUser;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.InvalidTLifeTimeAttributeException;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.InvalidTSpaceTokenAttributesException;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TTURL;
import it.grid.storm.synchcall.command.datatransfer.PutDoneCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that represents StoRMs PtPChunkCatalog: it collects PtPChunkData and provides methods for looking up a
 * PtPChunkData based on TRequestToken, as well as for updating data into persistence. Methods are also supplied to
 * evaluate if a SURL is in SRM_SPACE_AVAILABLE state, and to transit expired SURLs in SRM_SPACE_AVAILABLE state to
 * SRM_FILE_LIFETIME_EXPIRED.
 * 
 * @author EGRID - ICTP Trieste
 * @date June, 2005
 * @version 3.0
 */
public class PtPChunkCatalog {
    private static final Logger log = LoggerFactory.getLogger(PtPChunkCatalog.class);
    
    /* only instance of PtPChunkCatalog present in StoRM! */
    private static final PtPChunkCatalog cat = new PtPChunkCatalog();
    private final PtPChunkDAO dao = PtPChunkDAO.getInstance();
    
    /* Timer object in charge of transiting expired requests from SRM_SPACE_AVAILABLE to SRM_FILE_LIFETIME_EXPIRED! */
    private final Timer transiter = new Timer();
    /* Delay time before starting cleaning thread! Set to 1 minute */
    private final long delay = Configuration.getInstance().getTransitInitialDelay() * 1000;
    /* Period of execution of cleaning! Set to 1 hour */
    private final long period = Configuration.getInstance().getTransitTimeInterval() * 1000;

    /**
     * Private constructor that starts the internal timer needed to periodically check and transit requests whose
     * pinLifetime has expired and are in SRM_SPACE_AVAILABLE, to SRM_FILE_LIFETIME_EXPIRED. Moreover, the physical file
     * corresponding to the SURL gets removed; then any JiT entry gets removed, except those on traverse for the parent
     * directory; finally any volatile entry gets removed too.
     */
    private PtPChunkCatalog() {
        TimerTask transitTask = new TimerTask() {
            @Override
            public void run() {

                List<Long> ids = getExpiredSRM_SPACE_AVAILABLE();

                if (ids.isEmpty()) {
                    return;
                }
                
                Collection<ReducedPtPChunkData> reduced = lookupReducedPtPChunkData(ids.toArray(new Long[ids.size()]));
                
                if (reduced.isEmpty()) {
                    log.error("ATTENTION in PtP CHUNK CATALOG! Attempt to handle physical files for transited expired entries failed! "
                            + "No data could be translated from persitence for PtP Chunks with ID " + ids);
                }
                ArrayList<TSURL> surls = new ArrayList<TSURL>(reduced.size());
                for(ReducedPtPChunkData data : reduced)
                {
                    surls.add(data.toSURL());
                }
                PutDoneCommand.executePutDone(surls, null);
            }
        };
        transiter.scheduleAtFixedRate(transitTask, delay, period);
    }

    /**
     * Method that returns the only instance of PtPChunkCatalog available.
     */
    public static PtPChunkCatalog getInstance() {
        return cat;
    }

    /**
     * Method used to update into Persistence a retrieved PtPChunkData.
     */
	synchronized public void update(PtPPersistentChunkData chunkData) {
		
		PtPChunkDataTO to = new PtPChunkDataTO();
		/* rimary key needed by DAO Object */
		to.setPrimaryKey(chunkData.getPrimaryKey());
		to.setStatus(StatusCodeConverter.getInstance().toDB(chunkData.getStatus().getStatusCode()));
		to.setErrString(chunkData.getStatus().getExplanation());
		to.setTransferURL(TURLConverter.getInstance().toDB(chunkData.getTransferURL().toString()));
		to.setPinLifetime(PinLifetimeConverter.getInstance().toDB(chunkData.pinLifetime().value()));
		to.setFileLifetime(FileLifetimeConverter.getInstance().toDB(chunkData.fileLifetime().value()));
		to.setFileStorageType(FileStorageTypeConverter.getInstance().toDB(chunkData.fileStorageType()));
		to.setOverwriteOption(OverwriteModeConverter.getInstance().toDB(chunkData.overwriteOption()));
		to.setNormalizedStFN(chunkData.getSURL().normalizedStFN());
		to.setSurlUniqueID(new Integer(chunkData.getSURL().uniqueId()));
		to.setClientDN(chunkData.getUser().getDn());
		if(chunkData.getUser() instanceof AbstractGridUser)
        {
            if(((AbstractGridUser)chunkData.getUser()).hasVoms())
            {
                to.setVomsAttributes(((AbstractGridUser)chunkData.getUser()).getFQANsAsString());
            }
                
        }
		dao.update(to);
		// TODO MICHELE SURL STORE
//		SurlStatusStore.getInstance().storeSurlStatus(cd.getSURL(), cd.getStatus().getStatusCode());
	}

	/**
     * Method that synchronizes the supplied PtPChunkData with the information present in Persistence. BE WARNED: a new
     * object is returned, and the original PtPChunkData is left untouched! null is returned in case of any error.
     */
	synchronized public PtPPersistentChunkData refreshStatus(PtPPersistentChunkData inputChunk) {
		
		PtPChunkDataTO auxTO = dao.refresh(inputChunk.getPrimaryKey());
		log.debug("PtP CHUNK CATALOG refreshStatus: retrieved data " + auxTO);
		if(auxTO == null)
		{
			log.warn("PtP CHUNK CATALOG! Empty TO found in persistence for specified request: "
				+ inputChunk.getPrimaryKey());
			return null;
		}
		else
		{
		    PtPPersistentChunkData data = makeOne(auxTO, inputChunk.getRequestToken());
		 // TODO MICHELE SURL STORE
//		    SurlStatusStore.getInstance().storeSurlStatus(data.getSURL(), data.getStatus().getStatusCode());
			return data;
		}
	}
	
    /**
     * Method that returns a Collection of PtPChunkData Objects matching the supplied TRequestToken. If any of the data
     * associated to the TRequestToken is not well formed and so does not allow a PtPChunkData Object to be created,
     * then that part of the request is dropped, gets logged and an attempt is made to write in the DB that the chunk
     * was malformed; the processing continues with the next part. Only the valid chunks get returned. If there are no
     * chunks to process then an empty Collection is returned, and a messagge gets logged. NOTE! Chunks in SRM_ABORTED
     * status are NOT returned! This is imporant because this method is intended to be used by the Feeders to fetch all
     * chunks in the request, and aborted chunks should not be picked up for processing!
     */
	synchronized public Collection<PtPPersistentChunkData> lookup(final TRequestToken rt) {
		
		Collection<PtPChunkDataTO> chunkTOs = dao.find(rt);
		log.debug("PtPChunkCatalog: retrieved data " + chunkTOs);
		return buildChunkDataList(chunkTOs);
//		ArrayList<PtPPersistentChunkData> list = new ArrayList<PtPPersistentChunkData>();
//		if(chunkTOs.isEmpty())
//		{
//			log.warn("PtP CHUNK CATALOG! No chunks found in persistence for specified request: "
//				+ rt);
//		}
//		else
//		{
//		    PtPPersistentChunkData chunk;
//			for(PtPChunkDataTO chunkTO : chunkTOs)
//			{
//				chunk = makeOne(chunkTO, rt);
//				if(chunk != null)
//				{
//					list.add(chunk);
//					// TODO MICHELE SURL STORE
////					SurlStatusStore.getInstance().storeSurlStatus(chunk.getSURL(), chunk.getStatus().getStatusCode());
//					if(!this.isComplete(chunkTO))
//					{
//						try
//						{
//							dao.updateIncomplete(this.completeTO(chunkTO, chunk));
//						} catch(InvalidReducedPtPChunkDataAttributesException e)
//						{
//							log.warn("PtG CHUNK CATALOG! unable to add missing informations on DB to the request: " + e);
//						}
//					}
//				}
//			}
//		}
//		log.debug("PtPChunkCatalog: returning " + list + "\n\n");
//		return list;
	}

	/**
     * Private method used to create a PtPChunkData object, from a PtPChunkDataTO and TRequestToken. If a chunk cannot
     * be created, an error messagge gets logged and an attempt is made to signal in the DB that the chunk is malformed.
     */
	private PtPPersistentChunkData makeOne(PtPChunkDataTO auxTO, TRequestToken rt) {

		StringBuffer errorSb = new StringBuffer();
		// toSURL
		TSURL toSURL = null;
		try
		{
			toSURL = TSURL.makeFromStringValidate(auxTO.toSURL());
		} catch(InvalidTSURLAttributesException e)
		{
			errorSb.append(e);
		}
		if(auxTO.normalizedStFN() != null)
		{
			toSURL.setNormalizedStFN(auxTO.normalizedStFN());
		}
		if(auxTO.surlUniqueID() != null)
		{
			toSURL.setUniqueID(auxTO.surlUniqueID().intValue());
		}
		// pinLifetime
		TLifeTimeInSeconds pinLifetime = null;
		try
		{
			long pinLifeTime = PinLifetimeConverter.getInstance().toStoRM(auxTO.pinLifetime());
			// Check for max value allowed
			long max = Configuration.getInstance().getPinLifetimeMaximum();
			if(pinLifeTime > max)
			{
				log.warn("PinLifeTime is greater than the max value "
					+ "allowed. Drop the value to the max = " + max + " seconds");
				pinLifeTime = max;
			}
			pinLifetime = TLifeTimeInSeconds.make(pinLifeTime, TimeUnit.SECONDS);
		} catch(InvalidTLifeTimeAttributeException e)
		{
			errorSb.append("\n");
			errorSb.append(e);
		}
		// fileLifetime
		TLifeTimeInSeconds fileLifetime = null;
		try
		{
			fileLifetime =
						   TLifeTimeInSeconds.make(FileLifetimeConverter.getInstance().toStoRM(
							   auxTO.fileLifetime()), TimeUnit.SECONDS);
		} catch(InvalidTLifeTimeAttributeException e)
		{
			errorSb.append("\n");
			errorSb.append(e);
		}
		// fileStorageType
		TFileStorageType fileStorageType =
										   FileStorageTypeConverter.getInstance().toSTORM(
											   auxTO.fileStorageType());
		if(fileStorageType == TFileStorageType.EMPTY)
		{
			errorSb.append("\nTFileStorageType could not be translated from "
				+ "its String representation! String: " + auxTO.fileStorageType());
			// Use the default value defined in Configuration.
			fileStorageType =
							  TFileStorageType.getTFileStorageType(Configuration.getInstance()
								  .getDefaultFileStorageType());
			errorSb.append("\nUsed the default TFileStorageType as defined " + "in StoRM config.: "
				+ fileStorageType);
		}
		// expectedFileSize
		//
		// WARNING! A converter is used because the DB uses 0 for empty, whereas
		// StoRM object model does allow a 0 size! Since this is an optional
		// field
		// in the SRM specs, null must be converted explicitly to Empty
		// TSizeInBytes
		// because it is indeed well formed!
		TSizeInBytes expectedFileSize = null;
		TSizeInBytes emptySize = TSizeInBytes.makeEmpty();
		long sizeTranslation =
							   SizeInBytesIntConverter.getInstance().toStoRM(
								   auxTO.expectedFileSize());
		if(emptySize.value() == sizeTranslation)
		{
			expectedFileSize = emptySize;
		}
		else
		{
			try
			{
				expectedFileSize = TSizeInBytes.make(auxTO.expectedFileSize(), SizeUnit.BYTES);
			} catch(InvalidTSizeAttributesException e)
			{
				errorSb.append("\n");
				errorSb.append(e);
			}
		}
		// spaceToken!
		//
		// WARNING! A converter is still needed because of DB logic for missing
		// SpaceToken makes use of NULL, whereas StoRM object model does not
		// allow
		// for null! It makes use of a specific Empty type.
		//
		// Indeed, the SpaceToken field is optional, so a request with a null
		// value
		// for the SpaceToken field in the DB, _is_ well formed!
		TSpaceToken spaceToken = null;
		TSpaceToken emptyToken = TSpaceToken.makeEmpty();
		/**
		 * convert empty string representation of DPM into StoRM representation;
		 */
		String spaceTokenTranslation =
									   SpaceTokenStringConverter.getInstance().toStoRM(
										   auxTO.spaceToken());
		if(emptyToken.toString().equals(spaceTokenTranslation))
		{
			spaceToken = emptyToken;
		}
		else
		{
			try
			{
				spaceToken = TSpaceToken.make(spaceTokenTranslation);
			} catch(InvalidTSpaceTokenAttributesException e)
			{
				errorSb.append("\n");
				errorSb.append(e);
			}
		}
		// overwriteOption!
		TOverwriteMode overwriteOption =
										 OverwriteModeConverter.getInstance().toSTORM(
											 auxTO.overwriteOption());
		if(overwriteOption == TOverwriteMode.EMPTY)
		{
			errorSb.append("\nTOverwriteMode could not be translated "
				+ "from its String representation! String: " + auxTO.overwriteOption());
			overwriteOption = null;
		}
		// transferProtocols
		TURLPrefix transferProtocols = TransferProtocolListConverter.toSTORM(auxTO.protocolList());
		if(transferProtocols.size() == 0)
		{
			errorSb.append("\nEmpty list of TransferProtocols "
				+ "or could not translate TransferProtocols!");
			transferProtocols = null; // fail construction of PtPChunkData!
		}
		// status
		TReturnStatus status = null;
		TStatusCode code = StatusCodeConverter.getInstance().toSTORM(auxTO.status());
		if(code == TStatusCode.EMPTY)
		{
			errorSb.append("\nRetrieved StatusCode was not recognised: " + auxTO.status());
		}
		else
		{
			try
			{
				status = new TReturnStatus(code, auxTO.errString());
			} catch(InvalidTReturnStatusAttributeException e)
			{
				errorSb.append("\n");
				errorSb.append(e);
			}
		}
		GridUserInterface gridUser = null;
        try
        {
            if(auxTO.vomsAttributes() != null && !auxTO.vomsAttributes().trim().equals(""))
            {
                gridUser = GridUserManager.makeVOMSGridUser(auxTO.clientDN(), auxTO.vomsAttributesArray());    
            }
            else
            {
                gridUser = GridUserManager.makeGridUser(auxTO.clientDN());
            }
            
        }
        catch (IllegalArgumentException e)
        {
            log.error("Unexpected error on voms grid user creation. Contact StoRM Support : IllegalArgumentException "
                      + e.getMessage());
        }
		
		// transferURL
		/**
		 * whatever is read is just meaningless because PtP will fill it in!!!
		 * So create an Empty TTURL by default! Vital to avoid problems with
		 * unknown DPM NULL/EMPTY logic policy!
		 */
		TTURL transferURL = TTURL.makeEmpty();
		// make PtPChunkData
		PtPPersistentChunkData aux = null;
		try
		{
			aux = new PtPPersistentChunkData(gridUser, rt, toSURL, pinLifetime, fileLifetime, fileStorageType,
					  spaceToken, expectedFileSize, transferProtocols, overwriteOption, status,
					  transferURL);
			aux.setPrimaryKey(auxTO.primaryKey());
		} catch(InvalidPtPPersistentChunkDataAttributesException e)
		{
			dao.signalMalformedPtPChunk(auxTO);
			log.warn("PtP CHUNK CATALOG! Retrieved malformed PtP chunk data"
				+ " from persistence. Dropping chunk from request: " + rt);
			log.warn(e.getMessage(), e);
			log.warn(errorSb.toString());
		} catch(InvalidPtPDataAttributesException e)
        {
		    dao.signalMalformedPtPChunk(auxTO);
            log.warn("PtP CHUNK CATALOG! Retrieved malformed PtP chunk data"
                + " from persistence. Dropping chunk from request: " + rt);
            log.warn(e.getMessage(), e);
            log.warn(errorSb.toString());
        } catch(InvalidFileTransferDataAttributesException e)
        {
            dao.signalMalformedPtPChunk(auxTO);
            log.warn("PtP CHUNK CATALOG! Retrieved malformed PtP chunk data"
                + " from persistence. Dropping chunk from request: " + rt);
            log.warn(e.getMessage(), e);
            log.warn(errorSb.toString());
        } catch(InvalidSurlRequestDataAttributesException e)
        {
            dao.signalMalformedPtPChunk(auxTO);
            log.warn("PtP CHUNK CATALOG! Retrieved malformed PtP chunk data"
                + " from persistence. Dropping chunk from request: " + rt);
            log.warn(e.getMessage(), e);
            log.warn(errorSb.toString());
        }
		// end...
		return aux;
	}
	
    /**
	 * 
	 * Adds to the received PtPChunkDataTO the normalized StFN and the SURL unique ID taken from the PtPChunkData
	 *  
	 * @param chunkTO
	 * @param chunk
	 */
	private void completeTO(ReducedPtPChunkDataTO chunkTO, final ReducedPtPChunkData chunk) {
		
		chunkTO.setNormalizedStFN(chunk.toSURL().normalizedStFN());
		chunkTO.setSurlUniqueID(new Integer(chunk.toSURL().uniqueId()));
	}

	/**
	 * 
	 * Creates a ReducedPtGChunkDataTO from the received PtGChunkDataTO and
	 * completes it with the normalized StFN and the SURL unique ID taken from
	 * the PtGChunkData
	 * 
	 * @param chunkTO
	 * @param chunk
	 * @return
	 * @throws InvalidReducedPtPChunkDataAttributesException
	 */
	private ReducedPtPChunkDataTO completeTO(PtPChunkDataTO chunkTO, final PtPPersistentChunkData chunk) throws InvalidReducedPtPChunkDataAttributesException {
		ReducedPtPChunkDataTO reducedChunkTO = this.reduce(chunkTO);
		this.completeTO(reducedChunkTO, this.reduce(chunk));
		return reducedChunkTO;
	}
	
	/**
	 * Creates a ReducedPtPChunkData from the data contained in the received PtPChunkData
	 * 
	 * @param chunk
	 * @return
	 * @throws InvalidReducedPtPChunkDataAttributesException
	 */
	private ReducedPtPChunkData reduce(PtPPersistentChunkData chunk) throws InvalidReducedPtPChunkDataAttributesException {

		ReducedPtPChunkData reducedChunk = new ReducedPtPChunkData(chunk.getSURL(), chunk.getStatus(), chunk.fileStorageType(), chunk.fileLifetime());
		reducedChunk.setPrimaryKey(chunk.getPrimaryKey());
		return reducedChunk;
	}

	/**
	 * Creates a ReducedPtPChunkDataTO from the data contained in the received PtPChunkDataTO
	 * 
	 * @param chunkTO
	 * @return
	 */
	private ReducedPtPChunkDataTO reduce(PtPChunkDataTO chunkTO) {

		ReducedPtPChunkDataTO reducedChunkTO = new ReducedPtPChunkDataTO();
		reducedChunkTO.setPrimaryKey(chunkTO.primaryKey());
		reducedChunkTO.setToSURL(chunkTO.toSURL());
		reducedChunkTO.setNormalizedStFN(chunkTO.normalizedStFN());
		reducedChunkTO.setSurlUniqueID(chunkTO.surlUniqueID());
		reducedChunkTO.setStatus(chunkTO.status());
		reducedChunkTO.setErrString(chunkTO.errString());
		return reducedChunkTO;
	}

	/**
     * Checks if the received PtPChunkDataTO contains the fields not set by the front end
     * but required
     * 
     * @param chunkTO
     * @return
     */
    private boolean isComplete(PtPChunkDataTO chunkTO) {
    	return (chunkTO.normalizedStFN() != null) && (chunkTO.surlUniqueID() != null); 
	}
    
    /**
     * Checks if the received ReducedPtGChunkDataTO contains the fields not set by the front end
     * but required
     * 
     * @param reducedChunkTO
     * @return
     */
    private boolean isComplete(ReducedPtPChunkDataTO reducedChunkTO) {

    	return (reducedChunkTO.normalizedStFN() != null) && (reducedChunkTO.surlUniqueID() != null);
	}
    
    /**
     * Method that returns a Collection of ReducedPtPChunkData Objects associated to the supplied TRequestToken. If any
     * of the data retrieved for a given chunk is not well formed and so does not allow a ReducedPtPChunkData Object to
     * be created, then that chunk is dropped and gets logged, while processing continues with the next one. All valid
     * chunks get returned: the others get dropped. If there are no chunks associated to the given TRequestToken, then
     * an empty Collection is returned and a messagge gets logged. All ReducedChunks, regardless of their status, are
     * returned.
     */
	synchronized public Collection<ReducedPtPChunkData> lookupReducedPtPChunkData(TRequestToken rt) {
		
	    return lookupReducedPtPChunkData(rt, new ArrayList<TSURL>(0));
//		Collection<ReducedPtPChunkDataTO> reducedChunkDataTOs = dao.findReduced(rt.getValue(), null);
//		log.debug("PtP CHUNK CATALOG: retrieved data " + reducedChunkDataTOs);
//		ArrayList<ReducedPtPChunkData> list = new ArrayList<ReducedPtPChunkData>();
//		if(reducedChunkDataTOs.isEmpty())
//		{
//			log.debug("PtP CHUNK CATALOG! No chunks found in persistence for " + rt);
//		}
//		else
//		{
//			ReducedPtPChunkData reducedChunkData;
//			for(ReducedPtPChunkDataTO reducedChunkDataTO : reducedChunkDataTOs)
//			{
//				reducedChunkData = makeOneReduced(reducedChunkDataTO);
//				if(reducedChunkData != null)
//				{
//					list.add(reducedChunkData);
//					SurlStatusStore.getInstance().storeSurlStatus(reducedChunkData.toSURL(), reducedChunkData.status().getStatusCode());
//					if(!this.isComplete(reducedChunkDataTO))
//					{
//						this.completeTO(reducedChunkDataTO, reducedChunkData);
//						dao.updateIncomplete(reducedChunkDataTO);
//					}
//				}
//			}
//			log.debug("PtP CHUNK CATALOG: returning " + list);
//		}
//		return list;
	}
	
    public Collection<ReducedPtPChunkData> lookupReducedPtPChunkData(TRequestToken requestToken,
                                                                     Collection<TSURL> surls)
    {
        Collection<ReducedPtPChunkDataTO> reducedChunkDataTOs = dao.findReduced(requestToken.getValue(), surls);
        log.debug("PtP CHUNK CATALOG: retrieved data " + reducedChunkDataTOs);
        return buildReducedChunkDataList(reducedChunkDataTOs);
//        ArrayList<ReducedPtPChunkData> list = new ArrayList<ReducedPtPChunkData>();
//        if (reducedChunkDataTOs.isEmpty())
//        {
//            log.debug("PtP CHUNK CATALOG! No chunks found in persistence for " + requestToken);
//        }
//        else
//        {
//            ReducedPtPChunkData reducedChunkData;
//            for (ReducedPtPChunkDataTO reducedChunkDataTO : reducedChunkDataTOs)
//            {
//                reducedChunkData = makeOneReduced(reducedChunkDataTO);
//                if (reducedChunkData != null)
//                {
//                    list.add(reducedChunkData);
//                 // TODO MICHELE SURL STORE
////                    SurlStatusStore.getInstance().storeSurlStatus(reducedChunkData.toSURL(),
////                                                                  reducedChunkData.status().getStatusCode());
//                    if (!this.isComplete(reducedChunkDataTO))
//                    {
//                        this.completeTO(reducedChunkDataTO, reducedChunkData);
//                        dao.updateIncomplete(reducedChunkDataTO);
//                    }
//                }
//            }
//            log.debug("PtP CHUNK CATALOG: returning " + list);
//        }
//        return list;
    }
    
    public Collection<PtPPersistentChunkData> lookupPtPChunkData(TSURL surl)
    {
        return lookupPtPChunkData((List<TSURL>)Arrays.asList(new TSURL[]{surl}));
    }
    
    public Collection<PtPPersistentChunkData> lookupPtPChunkData(List<TSURL> surls)
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
        Collection<PtPChunkDataTO> chunkDataTOs = dao.find(surlsUniqueIDs, surlsArray);
        log.debug("PtP CHUNK CATALOG: retrieved data " + chunkDataTOs);
        return buildChunkDataList(chunkDataTOs);
    }
    
    
    private Collection<PtPPersistentChunkData> buildChunkDataList(Collection<PtPChunkDataTO> chunkDataTOs)
    {
        ArrayList<PtPPersistentChunkData> list = new ArrayList<PtPPersistentChunkData>();
        PtPPersistentChunkData chunk;
        for (PtPChunkDataTO chunkTO : chunkDataTOs)
        {
            chunk = makeOne(chunkTO);
            if (chunk != null)
            {
                list.add(chunk);
                // TODO MICHELE SURL STORE
// SurlStatusStore.getInstance().storeSurlStatus(chunk.getSURL(), chunk.getStatus().getStatusCode());
                if (!this.isComplete(chunkTO))
                {
                    try
                    {
                        dao.updateIncomplete(this.completeTO(chunkTO, chunk));
                    } catch(InvalidReducedPtPChunkDataAttributesException e)
                    {
                        log.warn("PtG CHUNK CATALOG! unable to add missing informations on DB to the request: "
                                + e);
                    }
                }
            }
        }
        log.debug("PtPChunkCatalog: returning " + list + "\n\n");
        return list;
    }

    private PtPPersistentChunkData makeOne(PtPChunkDataTO chunkTO)
    {
        try
        {
            return makeOne(chunkTO, new TRequestToken(chunkTO.requestToken(), chunkTO.timeStamp()));
        } catch(InvalidTRequestTokenAttributesException e)
        {
            throw new IllegalStateException("Unexpected InvalidTRequestTokenAttributesException in TRequestToken: " + e);
        }
}

    private Collection<ReducedPtPChunkData> buildReducedChunkDataList(
            Collection<ReducedPtPChunkDataTO> chunkDataTOCollection)
    {
        ArrayList<ReducedPtPChunkData> list = new ArrayList<ReducedPtPChunkData>();
        ReducedPtPChunkData reducedChunkData;
        for (ReducedPtPChunkDataTO reducedChunkDataTO : chunkDataTOCollection)
        {
            reducedChunkData = makeOneReduced(reducedChunkDataTO);
            if (reducedChunkData != null)
            {
                list.add(reducedChunkData);
                // TODO MICHELE SURL STORE
// SurlStatusStore.getInstance().storeSurlStatus(reducedChunkData.toSURL(),
// reducedChunkData.status().getStatusCode());
                if (!this.isComplete(reducedChunkDataTO))
                {
                    this.completeTO(reducedChunkDataTO, reducedChunkData);
                    dao.updateIncomplete(reducedChunkDataTO);
                }
            }
        }
        log.debug("PtP CHUNK CATALOG: returning " + list);
        return list;
    }

    
    
    /**
     * Method that returns a Collection of ReducedPtPChunkData Objects corresponding to each of the IDs else {
     * log.debug(
     * "PtPChunkDAO! No chunk of PtP request was transited from SRM_SPACE_AVAILABLE to SRM_FILE_LIFETIME_EXPIRED.");
     * }contained inthe supplied List of Long objects. If any of the data retrieved for a given chunk is not well formed
     * and so does not allow a ReducedPtPChunkData Object to be created, then that chunk is dropped and gets logged,
     * while processing continues with the next one. All valid chunks get returned: the others get dropped. WARNING! If
     * there are no chunks associated to any of the given IDs, no messagge gets written in the logs!
     */
	synchronized public Collection<ReducedPtPChunkData> lookupReducedPtPChunkData(Long[] volids) {
		
		Collection<ReducedPtPChunkDataTO> reducedChunkDataTOs = dao.findReduced(Arrays.asList(volids));
		log.debug("PtP CHUNK CATALOG: fetched data " + reducedChunkDataTOs);
		return buildReducedChunkDataList(reducedChunkDataTOs);
//		List<ReducedPtPChunkData> list = new ArrayList<ReducedPtPChunkData>();
//		if(reducedChunkDataTOs.isEmpty())
//		{
//			log.debug("PtP CHUNK CATALOG! No chunks found in persistence for " + volids);
//		}
//		else
//		{
//			ReducedPtPChunkData reducedChunkData;
//			for(ReducedPtPChunkDataTO reducedChunkDataTO : reducedChunkDataTOs)
//			{
//				reducedChunkData = makeOneReduced(reducedChunkDataTO);
//				if(reducedChunkData != null)
//				{
//					list.add(reducedChunkData);
//					// TODO MICHELE SURL STORE
////					SurlStatusStore.getInstance().storeSurlStatus(reducedChunkData.toSURL(), reducedChunkData.status().getStatusCode());
//					if(!this.isComplete(reducedChunkDataTO))
//					{
//						this.completeTO(reducedChunkDataTO, reducedChunkData);
//						dao.updateIncomplete(reducedChunkDataTO);
//					}
//				}
//			}
//			log.debug("PtP CHUNK CATALOG: returning " + list);
//		}
//		return list;
	}

	private ReducedPtPChunkData makeOneReduced(ReducedPtPChunkDataTO reducedChunkDataTO) {

		StringBuffer errorSb = new StringBuffer();
		// fromSURL
		TSURL toSURL = null;
		try
		{
			toSURL = TSURL.makeFromStringValidate(reducedChunkDataTO.toSURL());
		} catch(InvalidTSURLAttributesException e)
		{
			errorSb.append(e);
		}
		if(reducedChunkDataTO.normalizedStFN() != null)
		{
			toSURL.setNormalizedStFN(reducedChunkDataTO.normalizedStFN());
		}
		if(reducedChunkDataTO.surlUniqueID() != null)
		{
			toSURL.setUniqueID(reducedChunkDataTO.surlUniqueID().intValue());
		}
		// status
		TReturnStatus status = null;
		TStatusCode code = StatusCodeConverter.getInstance().toSTORM(reducedChunkDataTO.status());
		if(code == TStatusCode.EMPTY)
		{
			errorSb.append("\nRetrieved StatusCode was not recognised: " + reducedChunkDataTO.status());
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
		// fileStorageType
		TFileStorageType fileStorageType =
										   FileStorageTypeConverter.getInstance().toSTORM(
											   reducedChunkDataTO.fileStorageType());
		if(fileStorageType == TFileStorageType.EMPTY)
		{
			errorSb.append("\nTFileStorageType could not be "
				+ "translated from its String representation! String: "
				+ reducedChunkDataTO.fileStorageType());
			// Use the default value defined in Configuration.
			fileStorageType =
							  TFileStorageType.getTFileStorageType(Configuration.getInstance()
								  .getDefaultFileStorageType());
			errorSb.append("\nUsed the default TFileStorageType as defined in StoRM config.: "
				+ fileStorageType);
		}
		// fileLifetime
		TLifeTimeInSeconds fileLifetime = null;
		try
		{
			fileLifetime =
						   TLifeTimeInSeconds.make(FileLifetimeConverter.getInstance().toStoRM(
							   reducedChunkDataTO.fileLifetime()), TimeUnit.SECONDS);
		} catch(InvalidTLifeTimeAttributeException e)
		{
			errorSb.append("\n");
			errorSb.append(e);
		}
		// make ReducedPtPChunkData
		ReducedPtPChunkData aux = null;
		try
		{
			aux = new ReducedPtPChunkData(toSURL, status, fileStorageType, fileLifetime);
			aux.setPrimaryKey(reducedChunkDataTO.primaryKey());
		} catch(InvalidReducedPtPChunkDataAttributesException e)
		{
			log.warn("PtP CHUNK CATALOG! Retrieved malformed Reduced PtP"
				+ " chunk data from persistence: dropping reduced chunk...");
			log.warn(e.getMessage(), e);
			log.warn(errorSb.toString());
		}
		// end...
		return aux;
	}
    
    /**
     * Method used to establish if in Persistence there is a PtPChunkData working on the supplied SURL, and whose state
     * is SRM_SPACE_AVAILABLE, in which case true is returned. In case none are found or there is any problem, false is
     * returned.
     */
	synchronized public boolean isSRM_SPACE_AVAILABLE(TSURL surl) {
		
		return (dao.numberInSRM_SPACE_AVAILABLE(surl.uniqueId()) > 0);
		// TODO MICHELE SURL STORE
//	    return TStatusCode.SRM_SPACE_AVAILABLE.equals(SurlStatusStore.getInstance().getSurlStatus(surl));
	}

	/**
	 * Method used to force transition to SRM_SUCCESS from SRM_SPACE_AVAILABLE,
	 * of all PtP Requests whose pinLifetime has expired and the state still has
	 * not been changed (a user forgot to run srmPutDone)! The method returns a
	 * List containing all ids of transited chunks that are also Volatile.
	 */
	synchronized public List<Long> getExpiredSRM_SPACE_AVAILABLE() {
		
		return dao.getExpiredSRM_SPACE_AVAILABLE();
	}

    /**
     * Method used to transit the specified Collection of ReducedPtPChunkData of the request identified by the supplied
     * TRequestToken, from SRM_SPACE_AVAILABLE to SRM_SUCCESS. Chunks in any other starting state are not transited.
     * <code>null</code> entries in the collection are permitted and skipped. In case of any error nothing is done, but
     * proper error messages get logged.
     */
	synchronized public void transitSRM_SPACE_AVAILABLEtoSRM_SUCCESS(
//			Collection<ReducedPtPChunkData> chunks) {
	    TRequestToken token, List<TSURL> surls){
		
		if(token == null || surls == null || surls.size() == 0)
		{
			return;
		}
		Collection<ReducedPtPChunkDataTO> tos = dao.findReduced(token.getValue(), null);
		LinkedList<Long> primaryKeys = new LinkedList<Long>();
		for(ReducedPtPChunkDataTO to : tos)
		{
		    for(TSURL surl : surls)
		    {
		        if(to.toSURL().equals(surl))
		        {
		            primaryKeys.add(to.primaryKey());
		            break;
		        }
		    }
		}
		dao.transitSRM_SPACE_AVAILABLEtoSRM_SUCCESS(primaryKeys);
		// TODO MICHELE SURL STORE
//		for(TSURL surl : surls)
//        {
//            if(surl != null)
//            {
//                SurlStatusStore.getInstance().storeSurlStatus(surl, TStatusCode.SRM_SUCCESS);
//            }
//        }
	}

    /**
     * This method is intended to be used by srmRm to transit all PtP chunks on the given SURL which are in the
     * SRM_SPACE_AVAILABLE state, to SRM_ABORTED. The supplied String will be used as explanation in those chunks return
     * status. The global status of the request is not changed. The TURL of those requests will automatically be set to
     * empty. Notice that both removeAllJit(SURL) and removeVolatile(SURL) are automatically invoked on
     * PinnedFilesCatalog, to remove any entry and corresponding physical ACLs. Beware, that the chunks may be part of
     * requests that have finished, or that still have not finished because other chunks are being processed.
     */
	synchronized public void transitSRM_SPACE_AVAILABLEtoSRM_ABORTED(TSURL surl, String explanation) {
		
		if(surl == null)
		{
			log.error("ATTENTION in PtP CHUNK CATALOG! Attempt to invoke transitSRM_SPACE_AVAILABLEtoSRM_ABORTED with a null SURL!");
			return;
		}
		if(explanation == null)
		{
			explanation = "";
		}
		dao.transitSRM_SPACE_AVAILABLEtoSRM_ABORTED(surl.uniqueId(), surl.toString(), explanation);
		// TODO MICHELE SURL STORE
//		SurlStatusStore.getInstance().storeSurlStatus(surl, TStatusCode.SRM_ABORTED);
	}

	public void updateStatus(TRequestToken requestToken, TSURL surl, TStatusCode statusCode,
            String explanation)
    {
	    dao.updateStatus(requestToken, new int[]{surl.uniqueId()}, new String[]{surl.rawSurl()},
	                     statusCode, explanation);
    }
	
	public void updateStatus(TSURL surl, TStatusCode statusCode, String explanation)
    {
	    dao.updateStatus(new int[]{surl.uniqueId()}, new String[]{surl.rawSurl()},
                         statusCode, explanation);
    }
	
    public void updateFromPreviousStatus(TRequestToken requestToken, TStatusCode expectedStatusCode,
            TStatusCode newStatusCode, String explanation)
    {
        dao.updateStatusOnMatchingStatus(requestToken, expectedStatusCode, newStatusCode, explanation);
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

    public void updateFromPreviousStatus(TSURL surl, TStatusCode expectedStatusCode,
            TStatusCode newStatusCode, String explanation)
    {
        dao.updateStatusOnMatchingStatus(new int[]{surl.uniqueId()}, new String[]{surl.rawSurl()},
                                         expectedStatusCode, newStatusCode, explanation);
    }

}
