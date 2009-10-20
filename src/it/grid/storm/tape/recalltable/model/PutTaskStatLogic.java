package it.grid.storm.tape.recalltable.model;

import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TSURL;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class PutTaskStatLogic {
    
    public static Response serveRequest(String requestToken, String surlString) {

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
        } else {
            outputMessage = "false";
        }
        
        return Response.ok(outputMessage, MediaType.TEXT_PLAIN_TYPE).status(200).build();
    }
}
