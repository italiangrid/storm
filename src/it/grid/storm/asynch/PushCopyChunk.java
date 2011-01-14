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

import it.grid.storm.catalogs.CopyChunkData;
import it.grid.storm.catalogs.PtGChunkCatalog;
import it.grid.storm.catalogs.PtGChunkData;
import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.common.types.TimeUnit;
import it.grid.storm.common.types.TransferProtocol;
import it.grid.storm.config.Configuration;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.model.Protocol;
import it.grid.storm.srm.types.InvalidTLifeTimeAttributeException;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TTURL;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SubClass of CopyChunk that handles Push mode, that is, the SRM server that
 * receives the srmCopy request transfers a file from itself (local) to another
 * SRM server (remote).
 *
 * The executeGetOpeartion method is overwritten with logic to handle a local
 * srmPreparToGet; the executePutOperation method is overwritten with logic to
 * handle a remote srmPrepareToPut through the use of an internal SRMClient; the
 * executeTransfer method is overwritten to carry out a GridFTP put of the local
 * file to the remote locattion.
 *
 * @author  EGRID - ICTP Trieste
 * @date    September, 2005
 * @version 2.0
 */
public class PushCopyChunk extends CopyChunk {

    private static Logger log = LoggerFactory.getLogger(PushCopyChunk.class);

    /**
     * Constructor requiring the GridUser, the RequestSummaryData, the
     * CopyChunkData about this chunk, and the integer representing the
     * progessive number of the chunk being handled, and the GlobalStatusManager.
     * If the supplied attributes are null or the counter is negative, an
     * InvalidCopyChunkAttributesException is thrown.
     */
    public PushCopyChunk(GridUserInterface gu, RequestSummaryData rsd, CopyChunkData chunkData, int n, GlobalStatusManager gsm) throws InvalidCopyChunkAttributesException {
        boolean ok = (gu!=null) &&
        (rsd!=null) &&
        (chunkData!=null) &&
        (n>=0) &&
        (gsm!=null);
        if (!ok) {
            throw new InvalidCopyChunkAttributesException(gu,rsd,chunkData,n,gsm);
        }
        this.gu = gu;
        this.rsd = rsd;
        this.chunkData = chunkData;
        this.n = n;
        this.gsm = gsm;
    }

    /**
     * In PushMode the getOperation is a _local_ srmPtG.
     *
     * A requestSummary is then made having the new requestToken, requestType set
     * to PREPARE_TO_GET, the same GridUser that issued the srmCopy request.
     *
     * A PtGChunkData is created and the PtGChunkCatalog is asked to save it in
     * persistence. It is made with: the new requestToken, the fromSURL of the
     * srmCopy, the lifeTime from the srmCopy, empty TSpaceToken, dirOption set
     * to non directory and non recursive, TURLPrefix with TransferProtocol.FILE,
     * empty fileSize, TReturnStatus set to SRM_REQUEST_QUEUED and explanation
     * string "PushCopyChunk has queued this local srmPrepareToGet operation;
     * srmCopy request " followed by the requestToken of the srmCopy, empty TTURL.
     *
     * A PtGChunk is therefore constructed and executed right away.
     *
     * The local execution of the srmPtG has some preliminary steps to be
     * completed before actually starting it; if this preparatory phase cannot
     * be completed thereby not even allowing the initiation of the local
     * operation, a Failed GetOperationResult is returned; but if it can start,
     * then a successful GetOperationResult is returned regardless of the SRM
     * status.
     */
    @Override
    protected GetOperationResult executeGetOperation() {
        try {
            //create new RequestSummaryData for PtGChunk
            RequestSummaryData ptgrsd = new RequestSummaryData(TRequestType.PREPARE_TO_GET,localrt,gu);
            //create new PtGChunkData and ask the catalog to add it to persistence
            TURLPrefix turlPrefix = new TURLPrefix();
            turlPrefix.addProtocol(Protocol.FILE);
            PtGChunkData ptgChunkData = new PtGChunkData(localrt,chunkData.fromSURL(),
                    chunkData.lifetime(), new TDirOption(false,false,0),turlPrefix,
                    TSizeInBytes.makeEmpty(),
                    new TReturnStatus(TStatusCode.SRM_REQUEST_QUEUED,"PushCopyChunk has queued this local srmPrepareToGet operation; srmCopy request "+chunkData.requestToken()),
                    TTURL.makeEmpty()
            );
            log.debug("executeGetOperation: adding new chunkData to PtGCatalog!");
            PtGChunkCatalog.getInstance().add(ptgChunkData,gu); //add a new non-child ptg entry to the catalogue, for grid user gu!
            log.debug("executeGetOperation: finished adding to PtGCatalog!");
            //create a new PtGChunk
            GlobalStatusManager gsm = new GlobalStatusManager(localrt);
            gsm.addChunk(ptgChunkData);
            gsm.finishedAdding();
            PtGChunk ptgChunk = new PtGChunk(gu,ptgrsd,ptgChunkData,gsm);
            //run it!
            log.debug("executeGetOperation: starting ptgChunk.doIt()!");
            ptgChunk.doIt();
            log.debug("executeGetOperation: finished ptgChunk.doIt()!");
            //preparatory phase of local GET operation completed normally, so return Success and the status of the PtG
            return new GetOperationResult(ptgChunkData.status(),ptgChunkData.transferURL(),ptgChunkData.fileSize(),localrt);
        } catch (Exception e) {
            //Catches all exceptions that are thrown when parameters do not allow correct creation of
            //StoRM objects! These are: InvalidRequestSummaryDataAttributesException,
            //Invalid PtGChunkDataAttributesException,  InvalidPtGChunkAttributesException,
            //InvalidTLifeTimeAttributesException, InvalidTReturnStatusAttributeException,
            //InvalidTDirOptionAttributesException
            log.error("ERROR IN PushCopyChunk! Cannot initiate local PtG! CopyRequestID: "+chunkData.requestToken());
            log.error(e.getMessage(), e);
            return new GetOperationResult("Cannot initiate local PtG! "+e);
        }
    }

    /**
     * In PushMode the PutOperation consists of a _remote_ srmPrepareToPut.
     *
     * The SRMClient is invoked specifying the same GridUser that made the srmCopy
     * request, the toSURL, the same fileLifetime specified in the srmCopy, the
     * same fileStorageType specified in the srmCopy, the same spaceToken
     * specified in the srmCopy, the file size from the getOperation, GSIFTP as
     * transfer protocol, "StoRM Remote PtP for (push) srmCopy" as description
     * string, the same overwriteOption specified in the srmCopy, a retry time
     * found in the Configuration class from getSRMClientPutTimeOut().
     *
     * Polling then starts by invoking the SRMClient statusOfPutRequest()
     * specifying: the request token from the reply to the remote srmPtP
     * operation, the same GridUser that made the srmCopy request, and the
     * toSURL from the original srmCopy request. The time-out is calculated
     * from the Configuration class from getSRMClientPutTimeOut(); the polling
     * frequency is derived from the Configuration class from
     * getSRMClientPutSleepTime().
     *
     * Once the polled status reaches a state different from SRM_QUEUED and
     * SRM_REQUEST_IN_PROGRESS, a successfull PutOperationResult is returned
     * containing the TURL and the TReturnStatus; if the operation times out,
     * then a failed PutOperationResult is returned; in case the SRMClient has
     * any problem carrying out its functionality, a failed PutOperationResult
     * is returned containing the error message returned by the exception thrown
     * by SRMClient.
     *
     * For each of the possible resuts, appropriate messagges get logged!
     */
    @Override
    protected PutOperationResult executePutOperation(TSizeInBytes getFileSize) {
        try {
            TLifeTimeInSeconds retryTime = TLifeTimeInSeconds.make(Configuration.getInstance().getSRMClientPutTotalRetryTime(),TimeUnit.SECONDS);
            try {
                //Make an SRMClient
                log.debug("PUSH COPY CHUNK: getting SRM client...");
                SRMClient srmClient = SRMClientFactory.getInstance().client();
                log.debug("... got it!");
                //Invoke prepareToPut functionality of SRMClient
                log.debug("PUSH COPY CHUNK: Invoking prepareToPut functionality...");
                SRMPrepareToPutReply reply = srmClient.prepareToPut(gu,
                        chunkData.toSURL(),chunkData.lifetime(),chunkData.fileStorageType(),chunkData.spaceToken(),
                        getFileSize,
                        TransferProtocol.GSIFTP,
                        "StoRM Remote PtP for (push) srmCopy",
                        chunkData.overwriteOption(),
                        retryTime);
                log.debug("... got it! Reply was: "+reply);
                //Polling...
                long timeOut = new Date().getTime() + Configuration.getInstance().getSRMClientPutTimeOut()*1000; //starting time from which to count the time-out!
                long sleepTime = Configuration.getInstance().getSRMClientPutSleepTime()*1000; //time interval in milliseconds for periodic polling
                boolean timedOut = false; //boolean true if the operation times out
                SRMStatusOfPutRequestReply statusOfPutRequestReply = null; //reply obtained from a statusOfPutRequest SRMClient invocation.
                TStatusCode replyCode = null; //TStatusCode of the request!
                try {
                    do {
                        log.debug("PUSH COPY CHUNK: Going to sleep...");
                        try { Thread.sleep(sleepTime); } catch (InterruptedException e) {}; //go to sleep before polling!
                        log.debug("PUSH COPY CHUNK: Waking up and verifying status...");
                        statusOfPutRequestReply = srmClient.statusOfPutRequest(reply.requestToken(),gu,chunkData.toSURL());
                        replyCode = statusOfPutRequestReply.returnStatus().getStatusCode();
                        timedOut = (new Date().getTime() > timeOut);
                        log.debug("PUSH COPY CHUNK: reply was "+statusOfPutRequestReply+"; the reply code was: "+replyCode+"; timedOut is:"+timedOut);
                    } while ( ((replyCode==TStatusCode.SRM_REQUEST_QUEUED) || (replyCode==TStatusCode.SRM_REQUEST_INPROGRESS) || (replyCode==TStatusCode.SRM_INTERNAL_ERROR)) && !timedOut);
                } catch (SRMClientException e2) {
                    //The SRMClient statusOfPutRequest functionality failed!
                    log.error("ERROR IN PushCopyChunk! PutOperation failed: SRMClient could not do an srmStatusOfPutRequest! "+e2);
                    StringBuffer sb = new StringBuffer();
                    sb.append("Parameters passed to client: ");
                    sb.append("requestToken: "); sb.append(reply.requestToken().toString()); sb.append(", ");
                    sb.append("GridUser: "); sb.append(gu.toString()); sb.append(", ");
                    sb.append("toSURL: "); sb.append(chunkData.toSURL().toString()); sb.append(".");
                    log.debug(sb.toString());
                    return new PutOperationResult("SRMClient failure! Could not do an srmStatusOfPutRequest! "+e2);
                }
                //Handle all possible states...
                log.debug("PUSH COPY CHUNK: out of loop ...");
                if (timedOut) {
                    //Reached time out!
                    log.warn("ATTENTION IN PushCopyChunk! PutOperation timed out!");
                    return new PutOperationResult("PutOperation timed out!");
                }
                //The remote operation completed!!!
                log.debug("PushCopyChunk! The PutOperation completed! " + statusOfPutRequestReply.returnStatus());
                return new PutOperationResult(statusOfPutRequestReply.returnStatus(),statusOfPutRequestReply.toTURL(),reply.requestToken());
            } catch (SRMClientException e1) {
                //The SRMClient prepareToPut functionality failed!
                log.error("ERROR IN PushCopyChunk! PutOperation failed: SRMClient could not do an srmPrepareToPut! "+e1);
                StringBuffer sb = new StringBuffer();
                sb.append("Parameters passed to client: ");
                sb.append("GridUser:"); sb.append(gu.toString()); sb.append(", ");
                sb.append("toSURL:"); sb.append(chunkData.toSURL().toString()); sb.append(", ");
                sb.append("lifetime:"); sb.append(chunkData.lifetime().toString()); sb.append(", ");
                sb.append("fileStorageType:"); sb.append(chunkData.fileStorageType().toString()); sb.append(", ");
                sb.append("spaceToken:"); sb.append(chunkData.spaceToken().toString()); sb.append(", ");
                sb.append("fileSize:"); sb.append(getFileSize.toString()); sb.append(", ");
                sb.append("transferProtocol:"); sb.append(TransferProtocol.GSIFTP.toString()); sb.append(", ");
                sb.append("description:"); sb.append("StoRM Remote PtP for (push) srmCopy"); sb.append(", ");
                sb.append("overwriteOption:"); sb.append(chunkData.overwriteOption().toString()); sb.append(", ");
                sb.append("retryTime:"); sb.append(retryTime.toString()); sb.append(".");
                log.debug(sb.toString());
                return new PutOperationResult("SRMClient failure! Could not do an srmPrepareToPut! "+e1);
            } catch (NoSRMClientFoundException e1) {
                log.error("ERROR IN PushCopyChunk! Cannot call remote SRM server because no SRM client could be loaded! "+e1);
                return new PutOperationResult("Cannot talk to other SRM server because no SRM client could be loaded!");
            }
        } catch (InvalidTLifeTimeAttributeException e3) {
            //Cannot create TLifeTimeInSeconds! This is a programming bug and should not occur!!!
            log.error("ERROR IN PushCopyChunk! Cannot create TLifeTimeInSeconds! "+e3);
            return new PutOperationResult(e3.toString());
        }
    }


    /**
     * In PushMode the transfer operation consists of a PUT of the local file into
     * the remote storage area.
     *
     * If the transfer fails, then a failed TransferResult is returned with a
     * failureExplanation String; otherwise a successful TransferResult is
     * returned.
     *
     * This operation also invokes an srmPutDone on the remote server.
     */
    @Override
    protected TransferResult executeTransfer(GetOperationResult get, PutOperationResult put) {
        try {
            TTURL getTURL = get.getTURL();
            TTURL putTURL = put.putTURL();
            //do transfer
            GridFTPTransferClient gridFTPTransferClient = GridFTPTransferClientFactory.getInstance().client();
            gridFTPTransferClient.putFile(gu,getTURL,putTURL);
            //Make an SRMClient
            log.debug("PUSH COPY CHUNK - executeTransfer: getting SRM client...");
            SRMClient srmClient = SRMClientFactory.getInstance().client();
            log.debug("... got it!");
            //Invoke putDone functionality of SRMClient
            long timeOut = new Date().getTime() + Configuration.getInstance().getSRMClientPutDoneTimeOut()*1000; //starting time from which to count the time-out!
            long sleepTime = Configuration.getInstance().getSRMClientPutDoneSleepTime()*1000; //time interval in milliseconds for periodic polling
            boolean timedOut = false; //boolean true if the operation times out
            SRMPutDoneReply reply = null;
            TStatusCode replyCode = null;
            try {
                do {
                    log.debug("PUSH COPY CHUNK - executeTransfer: Going to sleep...");
                    try { Thread.sleep(sleepTime); } catch (InterruptedException e) {}; //go to sleep before executing!
                    log.debug("PUSH COPY CHUNK - executeTransfer: Waking up and invoking srmPutDone...");
                    reply = srmClient.srmPutDone(put.requestToken(),gu,chunkData.toSURL());
                    replyCode = reply.overallRetStat().getStatusCode();
                    timedOut = (new Date().getTime() > timeOut);
                    log.debug("PUSH COPY CHUNK: reply was "+reply+"; the reply code was: "+replyCode+"; timedOut is:"+timedOut);
                } while ((replyCode==TStatusCode.SRM_INTERNAL_ERROR) && !timedOut);
            } catch (SRMClientException e2) {
                //The SRMClient putDone functionality failed!
                log.error("File transfer completed successfully, but problems were encountered performing final srmPutDone! "+e2);
                StringBuffer sb = new StringBuffer();
                sb.append("Parameters passed to client: ");
                sb.append("requestToken: "); sb.append(chunkData.requestToken().toString()); sb.append(", ");
                sb.append("GridUser: "); sb.append(gu.toString()); sb.append(", ");
                sb.append("toSURL: "); sb.append(chunkData.toSURL().toString()); sb.append(".");
                log.debug(sb.toString());
                return new TransferResult("File transfer completed successfully, but problems were encountered performing final srmPutDone! "+e2);
            }
            if (timedOut) {
                //Reached time out!
                log.warn("File transfer completed successfully, but there was a timeout waiting for srmPutDone to return a status different from SRM_INTERNAL_ERROR!");
                return new TransferResult("File transfer completed successfully, but there was a timeout waiting for srmPutDone to return a status different from SRM_INTERNAL_ERROR!");
            }
            //successful!
            return new TransferResult();
        } catch (NoSRMClientFoundException e) {
            log.error("ERROR IN PushCopyChunk! TransferOperation could not invoke srmPutDone on remote SRM server because no SRM client could be loaded! "+e);
            return new TransferResult("TransferOperation could not invoke srmPutDone on remote SRM server because no SRM client could be loaded!");
        } catch (GridFTPTransferClientException e) {
            log.error("ERROR IN PushCopyChunk! File transfer failed! "+e);
            return new TransferResult(e.toString());
        } catch (NoGridFTPTransferClientFoundException e) {
            log.error("ERROR IN PushCopyChunk! Cannot transfer file because no transfer client could be loaded! "+e);
            return new TransferResult("Cannot transfer file because no transfer client could be loaded!");
        }
    }
}
