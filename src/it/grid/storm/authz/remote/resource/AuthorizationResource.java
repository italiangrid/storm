package it.grid.storm.authz.remote.resource;

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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.common.types.InvalidStFNAttributeException;
import it.grid.storm.common.types.StFN;
import it.grid.storm.griduser.FQAN;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.MappingRule;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.jersey.server.impl.ResponseBuilderImpl;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import it.grid.storm.authz.remote.Constants;

/**
 * @author Michele Dibenedetto
 */
@Path("/" + Constants.RESOURCE + "/" + Constants.VERSION + "/{filePath}")
public class AuthorizationResource
{

    private static final Logger log = LoggerFactory.getLogger(AuthorizationResource.class);
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.READ_OPERATION + "/" + Constants.VOMS_EXTENSIONS + "/" + Constants.USER)
    @Produces("text/plain")
    public String evaluateVomsGridUserReadPermission(@PathParam("filePath") String filePath,
            @QueryParam(Constants.DN_KEY) String DN, @QueryParam(Constants.FQANS_KEY) String FQANS) throws WebApplicationException
    {
        log.info("Serving read operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        
        String DNDecoded, FQANSDecoded, filePathDecoded;
        try
        {
            filePathDecoded = URLDecoder.decode(filePath, Constants.ENCODING_SCHEME);
            DNDecoded = URLDecoder.decode(DN, Constants.ENCODING_SCHEME);
            FQANSDecoded = URLDecoder.decode(FQANS, Constants.ENCODING_SCHEME);
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("Unable to decode parameters. UnsupportedEncodingException : " + e.getMessage());
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity("Unable to decode paramethesr, unsupported encoding \'" + Constants.ENCODING_SCHEME + "\'");
            throw new WebApplicationException(responseBuilder.build());
        }
        log.debug("Decoded filePath = " + filePathDecoded);
        log.debug("Decoded DN = " + DNDecoded);
        log.debug("Decoded FQANS = " + FQANSDecoded);
        
        if (filePathDecoded == null || filePathDecoded.trim().equals("") || DNDecoded == null || DNDecoded.trim().equals("")
                || FQANSDecoded == null || FQANSDecoded.trim().equals(""))
        {
            log.error("Unable to evaluate permissions. Some parameters are missing : DN " + DNDecoded
                    + " FQANS " + FQANSDecoded + " filePath " + filePathDecoded);
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity("Unable to evaluate permissions. Some parameters are missing");
            throw new WebApplicationException(responseBuilder.build());
        }
        return evaluateVomsGridUserPermission(DNDecoded, FQANSDecoded, filePathDecoded, SRMFileRequest.PTG).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.WRITE_OPERATION + "/" + Constants.VOMS_EXTENSIONS + "/" + Constants.USER)
    @Produces("text/plain")
    public String evaluateVomsGridUserWritePermission(@PathParam("filePath") String filePath,
            @QueryParam(Constants.DN_KEY) String DN, @QueryParam(Constants.FQANS_KEY) String FQANS) throws WebApplicationException
    {
        log.info("Serving write operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        String DNDecoded, FQANSDecoded, filePathDecoded;
        try
        {
            filePathDecoded = URLDecoder.decode(filePath, Constants.ENCODING_SCHEME);
            DNDecoded = URLDecoder.decode(DN, Constants.ENCODING_SCHEME);
            FQANSDecoded = URLDecoder.decode(FQANS, Constants.ENCODING_SCHEME);
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("Unable to decode parameters. UnsupportedEncodingException : " + e.getMessage());
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity("Unable to decode paramethesr, unsupported encoding \'" + Constants.ENCODING_SCHEME + "\'");
            throw new WebApplicationException(responseBuilder.build());
        }
        log.debug("Decoded filePath = " + filePathDecoded);
        log.debug("Decoded DN = " + DNDecoded);
        log.debug("Decoded FQANS = " + FQANSDecoded);
        
        if (filePathDecoded == null || filePathDecoded.trim().equals("") || DNDecoded == null || DNDecoded.trim().equals("")
                || FQANSDecoded == null || FQANSDecoded.trim().equals(""))
        {
            log.error("Unable to evaluate permissions. Some parameters are missing : DN " + DNDecoded
                    + " FQANS " + FQANSDecoded + " filePath " + filePathDecoded);
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity("Unable to evaluate permissions. Some parameters are missing");
            throw new WebApplicationException(responseBuilder.build());
        }

        return evaluateVomsGridUserPermission(DNDecoded, FQANSDecoded, filePathDecoded, SRMFileRequest.PTP).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.READ_OPERATION + "/" + Constants.PLAIN + "/" + Constants.USER)
    @Produces("text/plain")
    public String evaluateGridUserReadPermission(@PathParam("filePath") String filePath,
            @QueryParam(Constants.DN_KEY) String DN)
            throws WebApplicationException
    {
        log.info("Serving read operation authorization on file '" + filePath + "\'");
        
        String DNDecoded, filePathDecoded;
        try
        {
            filePathDecoded = URLDecoder.decode(filePath, Constants.ENCODING_SCHEME);
            DNDecoded = URLDecoder.decode(DN, Constants.ENCODING_SCHEME);
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("Unable to decode parameters. UnsupportedEncodingException : " + e.getMessage());
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity("Unable to decode paramethesr, unsupported encoding \'" + Constants.ENCODING_SCHEME + "\'");
            throw new WebApplicationException(responseBuilder.build());
        }
        log.debug("Decoded filePath = " + filePathDecoded);
        log.debug("Decoded DN = " + DNDecoded);
        
        if (filePathDecoded == null || filePathDecoded.trim().equals("") || DNDecoded == null || DNDecoded.trim().equals(""))
        {
            log.error("Unable to evaluate permissions. Some parameters are missing : DN " + DNDecoded
                    + " filePath " + filePathDecoded);
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity("Unable to evaluate permissions. Some parameters are missing");
            throw new WebApplicationException(responseBuilder.build());
        }

        return evaluateVomsGridUserPermission(DNDecoded, null, filePathDecoded, SRMFileRequest.PTG).toString();
    }

    /**
     * @param filePath
     * @param DN
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.WRITE_OPERATION + "/" + Constants.PLAIN + "/" + Constants.USER)
    @Produces("text/plain")
    public String evaluateGridUserWritePermission(@PathParam("filePath") String filePath,
            @QueryParam(Constants.DN_KEY) String DN)
            throws WebApplicationException
    {
        log.info("Serving write operation authorization on file '" + filePath + "\'");
        
        String DNDecoded, filePathDecoded;
        try
        {
            filePathDecoded = URLDecoder.decode(filePath, Constants.ENCODING_SCHEME);
            DNDecoded = URLDecoder.decode(DN, Constants.ENCODING_SCHEME);
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("Unable to decode parameters. UnsupportedEncodingException : " + e.getMessage());
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity("Unable to decode paramethesr, unsupported encoding \'" + Constants.ENCODING_SCHEME + "\'");
            throw new WebApplicationException(responseBuilder.build());
        }
        log.debug("Decoded filePath = " + filePathDecoded);
        log.debug("Decoded DN = " + DNDecoded);
        
        if (filePathDecoded == null || filePathDecoded.trim().equals("") || DNDecoded == null || DNDecoded.trim().equals(""))
        {
            log.error("Unable to evaluate permissions. Some parameters are missing : DN " + DNDecoded
                    + " filePath " + filePathDecoded);
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.BAD_REQUEST);
            responseBuilder.entity("Unable to evaluate permissions. Some parameters are missing");
            throw new WebApplicationException(responseBuilder.build());
        }

        return evaluateVomsGridUserPermission(DNDecoded, null, filePathDecoded, SRMFileRequest.PTP).toString();
    }
    
    /**
     * @param DNDecoded
     * @param FQANSDecoded
     * @param filePathDecoded
     * @param request
     * @return never null
     * @throws WebApplicationException
     */
    private Boolean evaluateVomsGridUserPermission(String DNDecoded, String FQANSDecoded,
            String filePathDecoded, SRMFileRequest request) throws WebApplicationException
    {
        String[] FQANSArray = parseFQANS(FQANSDecoded);
        GridUserInterface gu;
        try
        {
            if (FQANSArray.length > 0)
            {
                gu = loadVomsGridUser(DNDecoded, FQANSArray);
            }
            else
            {
                gu = loadGridUser(DNDecoded);
            }
        }
        catch (IllegalArgumentException e)
        {
            //never thrown
            log.error("Unable to build the GridUserInterface object for DN \'" + DNDecoded + "\' and FQANS \'" + Arrays.toString(FQANSArray) + "\'. IllegalArgumentException: "
                    + e.getMessage());
            ResponseBuilderImpl builder = new ResponseBuilderImpl();
            builder.status(Response.Status.BAD_REQUEST);
            builder.entity("Unable to build a GridUser for DN \'" + DNDecoded + "\' and FQANS \'" + Arrays.toString(FQANSArray) + "\'. Missing argument(s)");
            throw new WebApplicationException(builder.build());
        }
        String VFSStFNRoot;
        try
        {
            VirtualFSInterface fileVFS = NamespaceDirector.getNamespace().resolveVFSbyAbsolutePath(filePathDecoded);
            if(fileVFS != null)
            {
                List<MappingRule> VFSMappingRules = fileVFS.getMappingRules();
                if(VFSMappingRules != null && VFSMappingRules.size() > 0)
                {
                    VFSStFNRoot = VFSMappingRules.get(0).getStFNRoot();
                }
                else
                {
                    log.error("Unable to determine the StFNRoot for file path's VFS. VFSMappingRules is empty!");
                    ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
                    responseBuilder.status(Response.Status.NOT_FOUND);
                    responseBuilder.entity("Unable to determine the StFNRoot for file path's VFS");
                    throw new WebApplicationException(responseBuilder.build());
                }
            }
            else
            {
                log.error("None of the VFS maps the requested file path \'" + filePathDecoded + "\'. fileVFS is null!");
                ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
                responseBuilder.status(Response.Status.NOT_FOUND);
                responseBuilder.entity("Unable to determine file path\'s associated virtual file system");
                throw new WebApplicationException(responseBuilder.build());
            }
        }
        catch (NamespaceException e)
        {
            log.error("Unable to determine a VFS that maps the requested file path \'" + filePathDecoded + "\'. NamespaceException: " + e.getMessage());
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.NOT_FOUND);
            responseBuilder.entity("Unable to determine file path\'s associated virtual file system");
            throw new WebApplicationException(responseBuilder.build());
        }
        int rootIndex = filePathDecoded.indexOf(VFSStFNRoot);
        if(rootIndex < 0)
        {
            log.error("The provided file path does not contains the StFNRoot of its VFS");
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.INTERNAL_SERVER_ERROR);
            responseBuilder.entity("The provided file path does not contains the StFNRoot of its VFS");
            throw new WebApplicationException(responseBuilder.build());
        }
        String fileStFNpath = filePathDecoded.substring(rootIndex, filePathDecoded.length());
        StFN fileStFN;
        try
        {
            fileStFN = StFN.make(fileStFNpath);
        }
        catch (InvalidStFNAttributeException e)
        {
            log.error("Unable to build StFN for path \'" + fileStFNpath + "\'. InvalidStFNAttributeException: " + e.getMessage());
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(Response.Status.INTERNAL_SERVER_ERROR);
            responseBuilder.entity("Unable to determine file path\'s associated virtual file system");
            throw new WebApplicationException(responseBuilder.build());
        }
        AuthzDecision decision = AuthzDirector.getPathAuthz().authorize(gu, SRMFileRequest.PTG, fileStFN);
        
        if (decision.equals(AuthzDecision.PERMIT))
        {
            return new Boolean(true);
        }
        else
        {
            if (decision.equals(AuthzDecision.DENY))
            {
                return new Boolean(false);
            }
            else
            {
                if (decision.equals(AuthzDecision.INDETERMINATE))
                {
                    log.warn("Authorizatino decision is INDETERMINATE! Unable to determine authorization of user " + gu + " to perform operation \'" + request + "\' on resource " + fileStFN);
                    return new Boolean(false);
                }
                else
                {
                    log.warn("Authorizatino decision has an unknown value \'" + decision + "\' ! Unable to determine authorization of user " + gu + " to perform operation \'" + request + "\' on resource " + fileStFN);
                    return new Boolean(false);
                }
            }
        }
    }
    
    /**
     * @param fQANS
     * @return
     */
    private String[] parseFQANS(String fQANS)
    {
        if(fQANS == null)
        {
            return new String[0];
        }
        return fQANS.trim().split(Constants.FQANS_SEPARATOR);
    }

    /**
     * Creates a GridUserInterface from the provided DN and FQANS
     * 
     * @param dn
     * @param fqansStringVector
     * @return the VOMS grid user corresponding to the provided parameters. never null
     * @throws IllegalArgumentException
     */
    private GridUserInterface loadVomsGridUser(String dn, String[] fqansStringVector) throws IllegalArgumentException
    {
        if(dn == null || fqansStringVector == null || fqansStringVector.length == 0)
        {
            log.error("Received invalid arguments DN parameter in loadVomsGridUser!");
            throw new IllegalArgumentException("Received null DN parameter");
        }
        
        FQAN[] fqansVector = new FQAN[fqansStringVector.length];
        for (int i = 0; i < fqansStringVector.length; i++)
        {
            fqansVector[i] = new FQAN(fqansStringVector[i]);
        }
        GridUserInterface gridUser = null;
        try
        {
            gridUser = GridUserManager.makeVOMSGridUser(dn, fqansVector);
        }
        catch (IllegalArgumentException e)
        {
            log.error("Unexpected error on voms grid user creation. Contact StoRM Support : IllegalArgumentException "
                      + e.getMessage());
        }
        return gridUser;
    }
    
    /**
     * Creates a GridUserInterface from the provided DN
     * 
     * @param dn
     * @return the grid user corresponding to the provided parameter. never null
     * @throws IllegalArgumentException
     */
    private GridUserInterface loadGridUser(String dn) throws IllegalArgumentException
    {
        if(dn == null)
        {
            log.error("Received null DN parameter in loadVomsGridUser!");
            throw new IllegalArgumentException("Received null DN parameter");
        }
        return GridUserManager.makeGridUser(dn);
    }
}