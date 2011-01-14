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


import java.util.UUID;
import it.grid.storm.filesystem.FSException;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.tape.recalltable.RecallTableCatalog;
import it.grid.storm.tape.recalltable.RecallTableException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PutTaskStatusLogic
{

    private static final Logger log = LoggerFactory.getLogger(PutTaskStatusLogic.class);

    public static Response serveRequest(String requestToken, StoRI stori) throws RecallTableException
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
            RecallTableCatalog rtCat = null;
            try
            {
                rtCat = new RecallTableCatalog(false);
            }
            catch (DataAccessException e)
            {
                log.error("Unable to use RecallTable DB.");
                throw new RecallTableException("Unable to use RecallTable DB");
            }

            UUID taskId = null;
            try
            {

                String pfn = localFile.getAbsolutePath();

                taskId = rtCat.getTaskId(requestToken, pfn);
                boolean statusUpdated = rtCat.changeStatus(taskId, RecallTaskStatus.SUCCESS);

                if (statusUpdated)
                {
                    log.info("Task status set to SUCCESS. taskId=" + taskId + " requestToken=" + requestToken
                            + " pfn=" + pfn);
                }
            }
            catch (DataAccessException e)
            {
                if (taskId == null)
                {
                    log.warn("Unable to update task recall status because unable to retrieve taskId for token " + requestToken
                            + " " + e.getMessage());
                }
                else
                {
                    log.warn("Unable to update task recall status for token " + requestToken
                            + " with taskId=" + taskId + e.getMessage());
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
