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
import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.scheduler.PersistentRequestChunk;

/**
 * Public super class from which both the PushCopyChunk and the PullCopyChunk are derived.
 *
 * @author  EGRID - ICTP Trieste
 * @version 2.0
 * @date    september, 2005
 */
public abstract class CopyPersistentChunk extends Copy implements PersistentRequestChunk {

    protected RequestSummaryData rsd=null;  
    protected GlobalStatusManager gsm = null;


    /**
     * Constructor requiring the GridUser, the RequestSummaryData, the
     * CopyChunkData about this chunk, and the integer representing the
     * progessive number of the chunk being handled, and the GlobalStatusManager.
     * If the supplied attributes are null or the counter is negative, an
     * InvalidCopyChunkAttributesException is thrown.
     */
    public CopyPersistentChunk(GridUserInterface gu, RequestSummaryData rsd, CopyPersistentChunkData requestData, int n, GlobalStatusManager gsm) throws InvalidCopyAttributesException, InvalidCopyPersistentChunkAttributesException {
        super(gu, requestData, n);
        if (rsd==null || gsm==null) {
            throw new InvalidCopyPersistentChunkAttributesException(gu,rsd,requestData,n,gsm);
        }
        this.rsd = rsd;
        this.gsm = gsm;
    }
    
    /**
     * Method that supplies a String describing this PushCopyChunk - for scheduler Log
     * purposes! It returns the request token of This request.
     */
    public String getName() {
        return "RequestToken of srmCopy: " + ((CopyPersistentChunkData)requestData).getRequestToken();
    }
    
    @Override
    public void persistStatus()
    {
        CopyChunkCatalog.getInstance().update((CopyPersistentChunkData)requestData);
    }

    @Override
    public String getRequestToken()
    {
       return ((CopyPersistentChunkData)requestData).getRequestToken().getValue();
    }

    @Override
    public void updateGlobalStatus()
    {
        if (this.failure) {
          gsm.failedChunk((CopyPersistentChunkData)requestData);
      } else {
          gsm.successfulChunk((CopyPersistentChunkData)requestData);
      }
    }
}
