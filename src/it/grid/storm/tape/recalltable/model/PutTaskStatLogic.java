package it.grid.storm.tape.recalltable.model;

import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.tape.recalltable.RecallTableCatalog;
import it.grid.storm.tape.recalltable.RecallTableException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PutTaskStatLogic {

    private static final Logger log = LoggerFactory.getLogger(PutTaskStatLogic.class);

    public static Response serveRequest(String requestToken, String surlString) throws RecallTableException {

        TSURL surl;
        try {
            surl = TSURL.makeFromString(surlString);
        } catch (InvalidTSURLAttributesException e) {
            return Response.status(400).build();
        }

        StoRI stori = null;
        try {
            stori = NamespaceDirector.getNamespace().resolveStoRIbySURL(surl);
        } catch (NamespaceException e) {
            return Response.status(400).build();
        }

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

            int taskId = -1;

            try {

                taskId = rtCat.getTaskId(requestToken, localFile.getAbsolutePath());
                rtCat.changeStatus(taskId, RecallTaskStatus.SUCCESS);

            } catch (DataAccessException e) {
                if (taskId == -1) {
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
