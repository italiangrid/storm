package it.grid.storm.ea.remote.resource;

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

/**
* 
*/

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;
import it.grid.storm.ea.ExtendedAttributesException;
import it.grid.storm.ea.FileNotFoundException;
import it.grid.storm.ea.NotSupportedException;
import it.grid.storm.ea.StormEA;
import it.grid.storm.ea.remote.Constants;

/**
 * @author Michele Dibenedetto
 */
@Path("/" + Constants.RESOURCE + "/" + Constants.VERSION + "/{filePath}")
public class StormEAResource
{
    
    private static final Logger log = LoggerFactory.getLogger(StormEAResource.class);

    @GET
    @Path("/" + Constants.ADLER_32)
    @Produces("text/plain")
    public String getAdler32Checksum(@PathParam("filePath") String filePath) throws WebApplicationException
    {
        RequestParameters parameters = new RequestParameters.Builder(filePath).build();
        log.info("Getting " + Constants.ADLER_32 +" checksum for file " + parameters.getFilePathDecoded());
        String checksum;
        try
        {
            checksum = StormEA.getChecksum(parameters.getFilePathDecoded(), Constants.ADLER_32);
        } catch(FileNotFoundException e)
        {
            log.error("Unable to get file checksum. FileNotFoundException: " + e.getMessage());
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity("File " + parameters.getFilePathDecoded() + " does not exists");
            throw new WebApplicationException(responseBuilder.build());
        } catch(NotSupportedException e)
        {
            log.error("Unable to get file checksum. NotSupportedException: " + e.getMessage());
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.INTERNAL_SERVER_ERROR);
            responseBuilder.entity("Unable to get the checksum, operation not supported by the filesystem");
            throw new WebApplicationException(responseBuilder.build());
        } catch(ExtendedAttributesException e)
        {
            log.error("Unable to get file checksum. ExtendedAttributesException: " + e.getMessage());
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.INTERNAL_SERVER_ERROR);
            responseBuilder.entity("Unable to get the checksum, Extended attributes management failure");
            throw new WebApplicationException(responseBuilder.build());
        }
        log.info("Retrieved checksum is " + checksum);
        return checksum;
    }
    
    @PUT
    @Path("/" + Constants.ADLER_32)
    @Produces("text/plain")
    public void setAdler32Checksum(@PathParam("filePath") String filePath, @QueryParam(Constants.CHECKSUM_VALUE_KEY) String checksum) throws WebApplicationException
    {
        RequestParameters parameters = new RequestParameters.Builder(filePath).checksum(checksum).build();
        log.info("Setting " + Constants.ADLER_32 +" \'" + parameters.getChecksumDecoded() + "\' checksum for file " + parameters.getFilePathDecoded());
        try
        {
            StormEA.setChecksum(parameters.getFilePathDecoded(), parameters.getChecksumDecoded(), Constants.ADLER_32);
        } catch(FileNotFoundException e)
        {
            log.error("Unable to set file checksum. FileNotFoundException: " + e.getMessage());
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity("File " + parameters.getFilePathDecoded() + " does not exists");
            throw new WebApplicationException(responseBuilder.build());
        } catch(NotSupportedException e)
        {
            log.error("Unable to set file checksum. NotSupportedException: " + e.getMessage());
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.INTERNAL_SERVER_ERROR);
            responseBuilder.entity("Unable to set the checksum, operation not supported by the filesystem");
            throw new WebApplicationException(responseBuilder.build());
        } catch(ExtendedAttributesException e)
        {
            log.error("Unable to set file checksum. ExtendedAttributesException: " + e.getMessage());
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.INTERNAL_SERVER_ERROR);
            responseBuilder.entity("Unable to set the checksum, Extended attributes management failure");
            throw new WebApplicationException(responseBuilder.build());
        }
    }
    
}