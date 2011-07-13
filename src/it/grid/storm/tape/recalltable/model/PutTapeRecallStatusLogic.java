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

package it.grid.storm.tape.recalltable.model;


import java.util.Date;
import java.util.UUID;
import it.grid.storm.filesystem.FSException;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.TapeRecallTO;
import it.grid.storm.tape.recalltable.TapeRecallCatalog;
import it.grid.storm.tape.recalltable.TapeRecallException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PutTapeRecallStatusLogic
{

    private static final Logger log = LoggerFactory.getLogger(PutTapeRecallStatusLogic.class);

    public static Response serveRequest(String requestToken, StoRI stori) throws TapeRecallException
    {

        LocalFile localFile = stori.getLocalFile();
        String outputMessage;
        boolean fileOnDisk; 
        try
        {
            fileOnDisk = localFile.isOnDisk();
        }
        catch (FSException e)
        {
            log.error("Unable to test file presence on disk. FSException " + e.getMessage());
            javax.ws.rs.core.Response.Status status = javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

            return Response.status(status).build();
        }
        if (fileOnDisk)
        {
            outputMessage = "true";
            TapeRecallCatalog rtCat = null;
            try
            {
                rtCat = new TapeRecallCatalog();
            }
            catch (DataAccessException e)
            {
                log.error("Unable to use RecallTable DB.");
                throw new TapeRecallException("Unable to use RecallTable DB");
            }

            UUID groupTaskId = null;
            try
            {

                String pfn = localFile.getAbsolutePath();

                UUID taskId = TapeRecallTO.buildTaskIdFromFileName(pfn);
                TapeRecallTO task = rtCat.getTask(taskId, requestToken);
                groupTaskId = task.getGroupTaskId();
                boolean statusUpdated = rtCat.changeGroupTaskStatus(groupTaskId, TapeRecallStatus.SUCCESS, new Date());

                if (statusUpdated)
                {
                    log.info("Task status set to SUCCESS. groupTaskId=" + groupTaskId + " requestToken=" + requestToken
                            + " pfn=" + pfn);
                }
            }
            catch (DataAccessException e)
            {
                if (groupTaskId == null)
                {
                    log.warn("Unable to update task recall status because unable to retrieve taskId for token " + requestToken
                            + " " + e.getMessage());
                }
                else
                {
                    log.warn("Unable to update task recall status for token " + requestToken
                            + " with groupTaskId=" + groupTaskId + ". DataAccessException : " + e.getMessage());
                }
            }
        }
        else
        {
            outputMessage = "false";
        }

        return Response.ok(outputMessage, MediaType.TEXT_PLAIN_TYPE).status(200).build();
    }
}
