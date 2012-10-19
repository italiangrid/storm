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

import it.grid.storm.common.types.TimeUnit;
//import it.grid.storm.namespace.SurlStatusStore;
import it.grid.storm.srm.types.InvalidTLifeTimeAttributeException;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.InvalidTSpaceTokenAttributesException;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that represents StoRMs CopyChunkCatalog: it collects CopyChunkData and
 * provides methods for looking up a CopyChunkData based on TRequestToken, as
 * well as for updating an existing one.
 * 
 * @author EGRID - ICTP Trieste
 * @date september, 2005
 * @version 2.0
 */
public class CopyChunkCatalog
{
	private static final Logger log = LoggerFactory.getLogger(CopyChunkCatalog.class);
	
	/* only instance of CopyChunkCatalog present in StoRM! */
	private static final CopyChunkCatalog cat = new CopyChunkCatalog();
	/* WARNING!!! TO BE MODIFIED WITH FACTORY!!! */
	private CopyChunkDAO dao = CopyChunkDAO.getInstance();

	private CopyChunkCatalog() {
	}

    /**
     * Method that returns the only instance of PtPChunkCatalog available.
     */
    public static CopyChunkCatalog getInstance() {
        return cat;
    }
    
    /**
     * Method used to update into Persistence a retrieved CopyChunkData. In case
     * any error occurs, the operation does not proceed and no Exception is
     * thrown.
     *
     * Beware that the only fields updated into persistence are the StatusCode and
     * the errorString.
     */
	synchronized public void update(CopyPersistentChunkData cd) {

		CopyChunkDataTO to = new CopyChunkDataTO();
		/* primary key needed by DAO Object */
		to.setPrimaryKey(cd.getPrimaryKey());
		to.setLifeTime(FileLifetimeConverter.getInstance().toDB(cd.getLifetime().value()));
		to.setStatus(StatusCodeConverter.getInstance().toDB(cd.getStatus().getStatusCode()));
		to.setErrString(cd.getStatus().getExplanation());
		to.setFileStorageType(FileStorageTypeConverter.getInstance().toDB(cd.getFileStorageType()));
		to.setOverwriteOption(OverwriteModeConverter.getInstance().toDB(cd.getOverwriteOption()));
		to.setNormalizedSourceStFN(cd.getSURL().normalizedStFN());
		to.setSourceSurlUniqueID(new Integer(cd.getSURL().uniqueId()));
		to.setNormalizedTargetStFN(cd.getDestinationSURL().normalizedStFN());
		to.setTargetSurlUniqueID(new Integer(cd.getDestinationSURL().uniqueId()));
		
		dao.update(to);
		// TODO MICHELE SURL STORE
//		SurlStatusStore.getInstance().storeSurlStatus(cd.getSURL(), cd.getStatus().getStatusCode());
	}

    /**
     * Method that returns a Collection of CopyChunkData Objects matching the
     * supplied TRequestToken.
     *
     * If any of the data associated to the TRequestToken is not well formed and
     * so does not allow a CopyChunkData Object to be created, then that part of
     * the request is dropped and gets logged, and the processing continues with
     * the next part. All valid chunks get returned: the others get dropped.
     *
     * If there are no chunks to process then an empty Collection is returned,
     * and a messagge gets logged.
     */
	synchronized public Collection<CopyPersistentChunkData> lookup(TRequestToken rt) {
		
		Collection<CopyChunkDataTO> chunkDataTOs = dao.find(rt);
		log.debug("COPY CHUNK CATALOG: retrieved data " + chunkDataTOs);
		return buildChunkDataList(chunkDataTOs, rt);
//		ArrayList<CopyPersistentChunkData> list = new ArrayList<CopyPersistentChunkData>();
//		if(chunkDataTOs.isEmpty())
//		{
//			log.warn("COPY CHUNK CATALOG! No chunks found in persistence for specified request: "
//				+ rt);
//		}
//		else
//		{
//			CopyPersistentChunkData chunk;
//			for(CopyChunkDataTO chunkTO : chunkDataTOs)
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
//						} catch(InvalidReducedCopyChunkDataAttributesException e)
//						{
//							log.warn("PtG CHUNK CATALOG! unable to add missing informations on DB to the request: " + e);
//						}
//					}
//				}
//			}
//		}
//		log.debug("COPY CHUNK CATALOG: returning " + list + "\n\n");
//		return list;
	}

    private Collection<CopyPersistentChunkData> buildChunkDataList(Collection<CopyChunkDataTO> chunkDataTOs,
            TRequestToken rt)
    {
        ArrayList<CopyPersistentChunkData> list = new ArrayList<CopyPersistentChunkData>();
        CopyPersistentChunkData chunk;
        for (CopyChunkDataTO chunkTO : chunkDataTOs)
        {
            chunk = makeOne(chunkTO, rt);
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
                    } catch(InvalidReducedCopyChunkDataAttributesException e)
                    {
                        log.warn("PtG CHUNK CATALOG! unable to add missing informations on DB to the request: "
                                + e);
                    }
                }
            }
        }
        log.debug("COPY CHUNK CATALOG: returning " + list + "\n\n");
        return list;
    }
    
    private Collection<CopyPersistentChunkData> buildChunkDataList(Collection<CopyChunkDataTO> chunkDataTOs)
    {
        ArrayList<CopyPersistentChunkData> list = new ArrayList<CopyPersistentChunkData>();
        CopyPersistentChunkData chunk;
        for (CopyChunkDataTO chunkTO : chunkDataTOs)
        {
            chunk = makeOne(chunkTO);
            if (chunk != null)
            {
                list.add(chunk);
                // TODO MICHELE SURL STORE
                // SurlStatusStore.getInstance().storeSurlStatus(chunk.getSURL(),
// chunk.getStatus().getStatusCode());
                if (!this.isComplete(chunkTO))
                {
                    try
                    {
                        dao.updateIncomplete(this.completeTO(chunkTO, chunk));
                    } catch(InvalidReducedCopyChunkDataAttributesException e)
                    {
                        log.warn("PtG CHUNK CATALOG! unable to add missing informations on DB to the request: "
                                + e);
                    }
                }
            }
        }
        log.debug("COPY CHUNK CATALOG: returning " + list + "\n\n");
        return list;
    }

    public Collection<CopyPersistentChunkData> lookupCopyChunkData(TRequestToken requestToken,
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
        Collection<CopyChunkDataTO> chunkDataTOs = dao.find(requestToken, surlsUniqueIDs,
                                                                                  surlsArray);
        return buildChunkDataList(chunkDataTOs, requestToken);
    }
    
    public Collection<CopyPersistentChunkData> lookupCopyChunkData(TSURL surl)
    {
        return lookupCopyChunkData(Arrays.asList(new TSURL[]{surl}));
    }


    public Collection<CopyPersistentChunkData> lookupCopyChunkData(List<TSURL> surls)
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
        Collection<CopyChunkDataTO> chunkDataTOs = dao.find(surlsUniqueIDs, surlsArray);
        return buildChunkDataList(chunkDataTOs);
    }

    private CopyPersistentChunkData makeOne(CopyChunkDataTO chunkTO)
    {
        try
        {
            return makeOne(chunkTO, new TRequestToken(chunkTO.requestToken(), chunkTO.timeStamp()));
        } catch(InvalidTRequestTokenAttributesException e)
        {
            throw new IllegalStateException("Unexpected InvalidTRequestTokenAttributesException in TRequestToken: " + e);
        }
    }
    
    /**
     * Generates a CopyChunkData from the received CopyChunkDataTO
     * 
     * @param chunkDataTO
     * @param rt
     * @return
     */
	private CopyPersistentChunkData makeOne(CopyChunkDataTO chunkDataTO, TRequestToken rt) {

		StringBuffer errorSb = new StringBuffer();
		// fromSURL
		TSURL fromSURL = null;
		try
		{
			fromSURL = TSURL.makeFromStringValidate(chunkDataTO.fromSURL());
		} catch(InvalidTSURLAttributesException e)
		{
			errorSb.append(e);
		}
		if(chunkDataTO.normalizedSourceStFN() != null)
		{
			fromSURL.setNormalizedStFN(chunkDataTO.normalizedSourceStFN());
		}
		if(chunkDataTO.sourceSurlUniqueID() != null)
		{
			fromSURL.setUniqueID(chunkDataTO.sourceSurlUniqueID().intValue());
		}
		// toSURL
		TSURL toSURL = null;
		try
		{
			toSURL = TSURL.makeFromStringValidate(chunkDataTO.toSURL());
		} catch(InvalidTSURLAttributesException e)
		{
			errorSb.append(e);
		}
		if(chunkDataTO.normalizedTargetStFN() != null)
		{
			toSURL.setNormalizedStFN(chunkDataTO.normalizedTargetStFN());
		}
		if(chunkDataTO.targetSurlUniqueID() != null)
		{
			toSURL.setUniqueID(chunkDataTO.targetSurlUniqueID().intValue());
		}
		// lifeTime
		TLifeTimeInSeconds lifeTime = null;
		try
		{
			lifeTime =
					   TLifeTimeInSeconds.make(FileLifetimeConverter.getInstance().toStoRM(
						   chunkDataTO.lifeTime()), TimeUnit.SECONDS);
		} catch(InvalidTLifeTimeAttributeException e)
		{
			errorSb.append("\n");
			errorSb.append(e);
		}
		// fileStorageType
		TFileStorageType fileStorageType =
										   FileStorageTypeConverter.getInstance().toSTORM(
											   chunkDataTO.fileStorageType());
		if(fileStorageType == TFileStorageType.EMPTY)
		{
			log.error("\nTFileStorageType could not be "
				+ "translated from its String representation! String: "
				+ chunkDataTO.fileStorageType());
			// fail creation of PtPChunk!
			fileStorageType = null;
		}
		// spaceToken!
		//
		// WARNING! Although this field is in common between StoRM and DPM, a
		// converter is still used
		// because DPM logic for NULL/EMPTY is not known. StoRM model does not
		// allow for null, so it must
		// be taken care of!
		TSpaceToken spaceToken = null;
		TSpaceToken emptyToken = TSpaceToken.makeEmpty();
		// convert empty string representation of DPM into StoRM representation;
		String spaceTokenTranslation =
									   SpaceTokenStringConverter.getInstance().toStoRM(
										   chunkDataTO.spaceToken());
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
		TOverwriteMode globalOverwriteOption =
											   OverwriteModeConverter.getInstance().toSTORM(
												   chunkDataTO.overwriteOption());
		if(globalOverwriteOption == TOverwriteMode.EMPTY)
		{
			errorSb.append("\nTOverwriteMode could not be "
				+ "translated from its String representation! String: "
				+ chunkDataTO.overwriteOption());
			globalOverwriteOption = null;
		}
		// status
		TReturnStatus status = null;
		TStatusCode code = StatusCodeConverter.getInstance().toSTORM(chunkDataTO.status());
		if(code == TStatusCode.EMPTY)
		{
			errorSb.append("\nRetrieved StatusCode was not recognised: " + chunkDataTO.status());
		}
		else
		{
			try
			{
				status = new TReturnStatus(code, chunkDataTO.errString());
			} catch(InvalidTReturnStatusAttributeException e)
			{
				errorSb.append("\n");
				errorSb.append(e);
			}
		}
		// make CopyChunkData
		CopyPersistentChunkData aux = null;
		try
		{
			aux =
				  new CopyPersistentChunkData(rt, fromSURL, toSURL, lifeTime, fileStorageType, spaceToken,
					  globalOverwriteOption, status);
			aux.setPrimaryKey(chunkDataTO.primaryKey());
		} catch(InvalidSurlRequestDataAttributesException e)
		{
			dao.signalMalformedCopyChunk(chunkDataTO);
			log.warn("COPY CHUNK CATALOG! Retrieved malformed Copy"
				+ " chunk data from persistence. Dropping chunk from request: " + rt);
			log.warn(e.getMessage());
			log.warn(errorSb.toString());
		}
		// end...
		return aux;
	}
    
    /**
	 * 
	 * Adds to the received CopyChunkDataTO the normalized StFN and the SURL unique ID taken from the CopyChunkData
	 *  
	 * @param chunkTO
	 * @param chunk
	 */
	private void completeTO(ReducedCopyChunkDataTO chunkTO, final ReducedCopyChunkData chunk) {
		
		chunkTO.setNormalizedSourceStFN(chunk.fromSURL().normalizedStFN());
		chunkTO.setSourceSurlUniqueID(new Integer(chunk.fromSURL().uniqueId()));
		chunkTO.setNormalizedTargetStFN(chunk.toSURL().normalizedStFN());
		chunkTO.setTargetSurlUniqueID(new Integer(chunk.toSURL().uniqueId()));
	}

	/**
	 * 
	 * Creates a ReducedCopyChunkDataTO from the received CopyChunkDataTO and
	 * completes it with the normalized StFN and the SURL unique ID taken from
	 * the PtGChunkData
	 * 
	 * @param chunkTO
	 * @param chunk
	 * @return
	 * @throws InvalidReducedCopyChunkDataAttributesException
	 */
	private ReducedCopyChunkDataTO completeTO(CopyChunkDataTO chunkTO, final CopyPersistentChunkData chunk) throws InvalidReducedCopyChunkDataAttributesException {
		ReducedCopyChunkDataTO reducedChunkTO = this.reduce(chunkTO);
		this.completeTO(reducedChunkTO, this.reduce(chunk));
		return reducedChunkTO;
	}
	
	/**
	 * Creates a ReducedCopyChunkData from the data contained in the received CopyChunkData
	 * 
	 * @param chunk
	 * @return
	 * @throws InvalidReducedPtGChunkDataAttributesException
	 */
	private ReducedCopyChunkData reduce(CopyPersistentChunkData chunk) throws InvalidReducedCopyChunkDataAttributesException {

		ReducedCopyChunkData reducedChunk = new ReducedCopyChunkData(chunk.getSURL(), chunk.getDestinationSURL(), chunk.getStatus());
		reducedChunk.setPrimaryKey(chunk.getPrimaryKey());
		return reducedChunk;
	}

	/**
	 * Creates a ReducedCopyChunkDataTO from the data contained in the received CopyChunkDataTO
	 * 
	 * @param chunkTO
	 * @return
	 */
	private ReducedCopyChunkDataTO reduce(CopyChunkDataTO chunkTO) {

		ReducedCopyChunkDataTO reducedChunkTO = new ReducedCopyChunkDataTO();
		reducedChunkTO.setPrimaryKey(chunkTO.primaryKey());
		reducedChunkTO.setFromSURL(chunkTO.fromSURL());
		reducedChunkTO.setNormalizedSourceStFN(chunkTO.normalizedSourceStFN());
		reducedChunkTO.setSourceSurlUniqueID(chunkTO.sourceSurlUniqueID());
		reducedChunkTO.setToSURL(chunkTO.toSURL());
		reducedChunkTO.setNormalizedTargetStFN(chunkTO.normalizedTargetStFN());
		reducedChunkTO.setTargetSurlUniqueID(chunkTO.targetSurlUniqueID());
		reducedChunkTO.setStatus(chunkTO.status());
		reducedChunkTO.setErrString(chunkTO.errString());
		return reducedChunkTO;
	}

	/**
     * Checks if the received CopyChunkDataTO contains the fields not set by the front end
     * but required
     * 
     * @param chunkTO
     * @return
     */
	private boolean isComplete(CopyChunkDataTO chunkTO) {

		return (chunkTO.normalizedSourceStFN() != null)
			&& (chunkTO.sourceSurlUniqueID() != null && chunkTO.normalizedTargetStFN() != null)
			&& (chunkTO.targetSurlUniqueID() != null);
	}
    
    /**
     * Checks if the received ReducedPtGChunkDataTO contains the fields not set by the front end
     * but required
     * 
     * @param reducedChunkTO
     * @return
     */
    @SuppressWarnings("unused")
	private boolean isComplete(ReducedCopyChunkDataTO reducedChunkTO) {

    	return (reducedChunkTO.normalizedSourceStFN() != null) && (reducedChunkTO.sourceSurlUniqueID() != null
    			&& reducedChunkTO.normalizedTargetStFN() != null) && (reducedChunkTO.targetSurlUniqueID() != null);
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

