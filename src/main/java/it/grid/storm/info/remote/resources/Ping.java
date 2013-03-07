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
 
package it.grid.storm.info.remote.resources;

import it.grid.storm.info.remote.Constants;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;

@Path("/info/ping")
public class Ping {

    // The Java method will process HTTP GET requests
    @GET
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"
    @Produces("text/plain")
    public String getClichedMessage() {
        // Return some cliched textual content
        return "Hello World";
    }
	
    @GET
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"
    @Produces("text/plain")
    @Path("/queryMeGet")
    public String getParameterizedMessage(@QueryParam("uno") String uno, @QueryParam("due") String due) {
        String unoDecoded, dueDecoded;
        try
        {
            unoDecoded = URLDecoder.decode(uno.trim(), Constants.ENCODING_SCHEME);
            dueDecoded = URLDecoder.decode(due.trim(), Constants.ENCODING_SCHEME);
        }
        catch (UnsupportedEncodingException e)
        {
            System.err.println("Unable to decode parameters. UnsupportedEncodingException : " + e.getMessage());
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity("Unable to decode paramethesr, unsupported encoding \'" + Constants.ENCODING_SCHEME + "\'");
            throw new WebApplicationException(responseBuilder.build());
        }
        return "Hello by GET my friend " + unoDecoded + " from " + dueDecoded;
    }
    
    @PUT
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"
    @Produces("text/plain")
    @Path("/queryMePut")
    public String putParameterizedMessage(@QueryParam("uno") String uno, @QueryParam("due") String due) {
        String unoDecoded, dueDecoded;
        try
        {
            unoDecoded = URLDecoder.decode(uno.trim(), Constants.ENCODING_SCHEME);
            dueDecoded = URLDecoder.decode(due.trim(), Constants.ENCODING_SCHEME);
        }
        catch (UnsupportedEncodingException e)
        {
            System.err.println("Unable to decode parameters. UnsupportedEncodingException : " + e.getMessage());
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity("Unable to decode paramethesr, unsupported encoding \'" + Constants.ENCODING_SCHEME + "\'");
            throw new WebApplicationException(responseBuilder.build());
        }
        return "Hello by PUT my friend " + unoDecoded + " from " + dueDecoded;
    }
    
}

