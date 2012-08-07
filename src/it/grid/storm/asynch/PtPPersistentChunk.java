/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.grid.storm.asynch;


import it.grid.storm.catalogs.PtPChunkCatalog;
import it.grid.storm.catalogs.PtPPersistentChunkData;
import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.scheduler.PersistentRequestChunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michele Dibenedetto
 *
 */
public class PtPPersistentChunk extends PtP implements PersistentRequestChunk
{

    private static Logger log = LoggerFactory.getLogger(PtPPersistentChunk.class);

    /**
     * RequestSummaryData containing all the statistics for the originating srmPrepareToPutRequest
     */
    private final RequestSummaryData rsd;

    /**
     * GlobalStatusManager object in charge of computing the global status of the request This chunk belongs
     * to
     */
    private final GlobalStatusManager gsm;

    /**
     * Constructor requiring the VomsGridUser, the RequestSummaryData, the PtPChunkData about this chunk, and
     * the
     * GlobalStatusManager. If the supplied attributes are null, an InvalidPtPChunkAttributesException is
     * thrown.
     * 
     * @throws InvalidPtPAttributesException
     * @throws InvalidPtPChunkAttributesException
     */
    public PtPPersistentChunk(GridUserInterface gu, RequestSummaryData rsd, PtPPersistentChunkData chunkData,
            GlobalStatusManager gsm) throws InvalidRequestAttributesException, InvalidPersistentRequestAttributesException
    {
        super(gu, chunkData);
        if (rsd == null || gsm == null)
        {
            throw new InvalidPersistentRequestAttributesException(gu, rsd, chunkData, gsm);
        }
        this.rsd = rsd;
        this.gsm = gsm;
    }

    /**
     * Method that handles a chunk. It is invoked by the scheduler to carry out the task.
     */
    public void doIt()
    {
        PtPPersistentChunk.log.info("Handling PtP chunk for user DN: " + gu.getDn() + "; for SURL: "
                + requestData.getSURL() + "; for requestToken: " + rsd.requestToken());
        super.doIt();
        log.info("Finished handling PtP chunk for user DN: " + gu.getDn() + "; for SURL: "
                 + requestData.getSURL() + "; for requestToken: " + rsd.requestToken() + "; result is: "
                 + requestData.getStatus());
    }
    
    /**
     * Method that supplies a String describing this PtGChunk - for scheduler Log purposes! It returns the request token
     * and the SURL that was asked for.
     */
    @Override
    public String getName() {
        return "PtPChunk of request " + rsd.requestToken() + " for SURL " + requestData.getSURL();
    }
    
    /* (non-Javadoc)
     * @see it.grid.storm.asynch.RequestChunk#getRequestToken()
     */
    @Override
    public String getRequestToken() {
        return rsd.requestToken().toString();
    }

    @Override
    public void persistStatus()
    {
        PtPPersistentChunk.log.debug("Persisting status of request : " + rsd.requestToken() + " on SURL " + this.requestData.getSURL());
        PtPChunkCatalog.getInstance().update((PtPPersistentChunkData) requestData);
    }

    @Override
    public void updateGlobalStatus()
    {
        PtPPersistentChunk.log.debug("Updating global status for request : " + rsd.requestToken() + " on SURL " + this.requestData.getSURL());
        if (failure)
        {
            gsm.failedChunk((PtPPersistentChunkData) requestData);
        }
        else
        {
            if (spacefailure)
            {
                gsm.expiredSpaceLifetimeChunk((PtPPersistentChunkData) requestData);
            }
            else
            {
                gsm.successfulChunk((PtPPersistentChunkData) requestData);
            }
        }
    }
}
