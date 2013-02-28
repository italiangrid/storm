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

import java.util.List;
import it.grid.storm.catalogs.CopyData;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TTURL;

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
 * file to the remote location.
 *
 * @author  EGRID - ICTP Trieste
 * @date    September, 2005
 * @version 2.0
 */
public class PushCopy extends Copy implements VisitableCopy{

    private static Logger log = LoggerFactory.getLogger(PushCopy.class);

    public PushCopy(GridUserInterface gu, CopyData chunkData, int n) throws InvalidCopyAttributesException {
        super(gu, chunkData, n);
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
    protected GetOperationResult executeGetOperation()
    {
        PushCopyGetVisitor visitor = new PushCopyGetVisitor();
        return (GetOperationResult) visitor.visit(this);
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
    protected PutOperationResult executePutOperation(TSizeInBytes getFileSize)
    {
        PushCopyPutVisitor visitor = new PushCopyPutVisitor(getFileSize);
        return (PutOperationResult) visitor.visit(this);
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
    protected TransferResult executeTransfer(GetOperationResult get, PutOperationResult put)
    {
        PushCopyTransferVisitor visitor = new PushCopyTransferVisitor(get,put);
        return (TransferResult) visitor.visit(this);
    }

    @Override
    public TRequestToken getLocalrt()
    {
        return this.localrt;
    }

    @Override
    public GridUserInterface getGu()
    {
        return this.gu;
    }

    @Override
    public CopyData getRequestData()
    {
        return this.requestData;
    }

    @Override
    public Logger getLog()
    {
        return PushCopy.log;
    }

    @Override
    public Result buildOperationResult(String string, Copy.ResultType type) throws IllegalArgumentException
    {
        return OperationResultBuilder.build(this, string, type);
    }

    @Override
    public Result buildOperationResult(List<Object> arguments, ResultType type)
            throws IllegalArgumentException
    {
        
        return OperationResultBuilder.build(this, arguments, type);
    }
}
