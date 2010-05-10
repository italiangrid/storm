package it.grid.storm.tape.recalltable.model;

import java.util.UUID;

import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.tape.recalltable.RecallTableCatalog;
import it.grid.storm.tape.recalltable.RecallTableException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PutTaskStatusLogic {

    private static final Logger log = LoggerFactory.getLogger(PutTaskStatusLogic.class);

    public static Response serveRequest(String requestToken, StoRI stori) throws RecallTableException {

        LocalFile localFile = stori.getLocalFile();

        String outputMessage;

        if (localFile.isOnDisk()) {
            outputMessage = "true";

            RecallTableCatalog rtCat = null;

            try {
                rtCat = new RecallTableCatalog(false);
            } catch (DataAccessException e) {
                log.error("Unable to use RecallTable DB.");
                throw new RecallTableException("Unable to use RecallTable DB");
            }

            UUID taskId = null;

            try {

                String pfn = localFile.getAbsolutePath();
                
                taskId = rtCat.getTaskId(requestToken, pfn);
                boolean statusUpdated = rtCat.changeStatus(taskId, RecallTaskStatus.SUCCESS);
                
                if (statusUpdated) {
                    log.info("Task status set to SUCCESS. taskId=" + taskId + " requestToken=" + requestToken + " pfn=" + pfn);
                }

            } catch (DataAccessException e) {
                if (taskId == null) {
                    throw new RecallTableException("Unable to retrieve taskId");
                } else {
                    throw new RecallTableException("Unable to change status for taskId=" + taskId);
                }
            }

        } else {
            outputMessage = "false";
        }

        return Response.ok(outputMessage, MediaType.TEXT_PLAIN_TYPE).status(200).build();
    }
}
