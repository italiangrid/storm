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

import java.util.ArrayList;
import java.util.Date;
import it.grid.storm.asynch.Copy.GetOperationResult;
import it.grid.storm.asynch.Copy.PutOperationResult;
import it.grid.storm.asynch.Copy.Result;
import it.grid.storm.asynch.Copy.ResultType;
import it.grid.storm.config.Configuration;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TTURL;


/**
 * @author Michele Dibenedetto
 *
 */
public class PushCopyTransferVisitor implements CopyVisitor
{
    private final GetOperationResult get;
    private final PutOperationResult put;

    @SuppressWarnings("unused")
    private PushCopyTransferVisitor()
    {
        //forbidden
        this.get = null;
        this.put = null;
    }
    
    public PushCopyTransferVisitor(GetOperationResult get, PutOperationResult put)
    {
        this.get = get;
        this.put = put;
    }

    @Override
    public Result visit(VisitableCopy copy)
    {
        try
        {
            TTURL getTURL = get.getTURL();
            TTURL putTURL = put.putTURL();
            // do transfer
            GridFTPTransferClient gridFTPTransferClient = GridFTPTransferClientFactory.getInstance().client();
            gridFTPTransferClient.putFile(copy.getGu(), getTURL, putTURL);
            // Make an SRMClient
            copy.getLog().debug("PUSH COPY CHUNK - executeTransfer: getting SRM client...");
            SRMClient srmClient = SRMClientFactory.getInstance().client();
            copy.getLog().debug("... got it!");
            // Invoke putDone functionality of SRMClient
            long timeOut = new Date().getTime() + Configuration.getInstance().getSRMClientPutDoneTimeOut()
                    * 1000; // starting time from which to count the time-out!
            long sleepTime = Configuration.getInstance().getSRMClientPutDoneSleepTime() * 1000; // time
// interval in milliseconds for periodic polling
            boolean timedOut = false; // boolean true if the operation times out
            SRMPutDoneReply reply = null;
            TStatusCode replyCode = null;
            try
            {
                do
                {
                    copy.getLog().debug("PUSH COPY CHUNK - executeTransfer: Going to sleep...");
                    try
                    {
                        Thread.sleep(sleepTime);
                    } catch(InterruptedException e)
                    {
                    }; // go to sleep before executing!
                    copy.getLog().debug("PUSH COPY CHUNK - executeTransfer: Waking up and invoking srmPutDone...");
                    reply = srmClient.srmPutDone(put.requestToken(), copy.getGu(), copy.getRequestData().getDestinationSURL());
                    replyCode = reply.overallRetStat().getStatusCode();
                    timedOut = (new Date().getTime() > timeOut);
                    copy.getLog().debug("PUSH COPY CHUNK: reply was " + reply + "; the reply code was: " + replyCode
                            + "; timedOut is:" + timedOut);
                } while ((replyCode == TStatusCode.SRM_INTERNAL_ERROR) && !timedOut);
            } catch(SRMClientException e2)
            {
                // The SRMClient putDone functionality failed!
                copy.getLog().error("File transfer completed successfully, but problems were encountered performing final srmPutDone! "
                        + e2);
                StringBuffer sb = new StringBuffer();
                sb.append("Parameters passed to client: ");
                sb.append(", ");
                sb.append("GridUser: ");
                sb.append(copy.getGu().toString());
                sb.append(", ");
                sb.append("toSURL: ");
                sb.append(copy.getRequestData().getDestinationSURL().toString());
                sb.append(".");
                copy.getLog().debug(sb.toString());
                return copy.buildOperationResult(
                                          "File transfer completed successfully, but problems were encountered performing final srmPutDone! "
                                                  + e2, ResultType.TRANSFER);
            }
            if (timedOut)
            {
                // Reached time out!
                copy.getLog().warn("File transfer completed successfully, but there was a timeout waiting for srmPutDone to return a status different from SRM_INTERNAL_ERROR!");
                return copy.buildOperationResult(
                                          "File transfer completed successfully, but there was a timeout waiting for srmPutDone to return a status different from SRM_INTERNAL_ERROR!", ResultType.TRANSFER);
            }
            // successful!
            return copy.buildOperationResult(new ArrayList<Object>(0), ResultType.TRANSFER);
        } catch(NoSRMClientFoundException e)
        {
            copy.getLog().error("ERROR IN PushCopyChunk! TransferOperation could not invoke srmPutDone on remote SRM server because no SRM client could be loaded! "
                    + e);
            return copy.buildOperationResult(
                                      "TransferOperation could not invoke srmPutDone on remote SRM server because no SRM client could be loaded!", ResultType.TRANSFER);
        } catch(GridFTPTransferClientException e)
        {
            copy.getLog().error("ERROR IN PushCopyChunk! File transfer failed! " + e);
            return copy.buildOperationResult(e.toString(), ResultType.TRANSFER);
        } catch(NoGridFTPTransferClientFoundException e)
        {
            copy.getLog().error("ERROR IN PushCopyChunk! Cannot transfer file because no transfer client could be loaded! "
                    + e);
            return copy.buildOperationResult("Cannot transfer file because no transfer client could be loaded!", ResultType.TRANSFER);
        }
    }

}
