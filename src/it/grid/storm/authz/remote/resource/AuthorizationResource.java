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

import java.io.File;
import it.grid.storm.authz.path.model.PathOperation;
import it.grid.storm.authz.path.model.SRMFileRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.QueryParam;
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
        RequestParameters parameters = new RequestParameters(filePath, DN, FQANS);
        return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), PathOperation.READ_FILE).toString();
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
        RequestParameters parameters = new RequestParameters(filePath, DN, FQANS);
        return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), PathOperation.WRITE_FILE).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.PREPARE_TO_PUT_OPERATION + "/" + Constants.VOMS_EXTENSIONS + "/" + Constants.USER)
    @Produces("text/plain")
    public String evaluateVomsGridUserPTPPermission(@PathParam("filePath") String filePath,
            @QueryParam(Constants.DN_KEY) String DN, @QueryParam(Constants.FQANS_KEY) String FQANS) throws WebApplicationException
    {
        log.info("Serving prepareToPut operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath, DN, FQANS);
        return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), SRMFileRequest.PTP).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.PREPARE_TO_PUT_OVERWRITE_OPERATION + "/" + Constants.VOMS_EXTENSIONS + "/" + Constants.USER)
    @Produces("text/plain")
    public String evaluateVomsGridUserPTPOverwritePermission(@PathParam("filePath") String filePath,
            @QueryParam(Constants.DN_KEY) String DN, @QueryParam(Constants.FQANS_KEY) String FQANS) throws WebApplicationException
    {
        log.info("Serving prepareToPut Overwrite operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath, DN, FQANS);
        return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), SRMFileRequest.PTP_Overwrite).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.PREPARE_TO_GET_OPERATION + "/" + Constants.VOMS_EXTENSIONS + "/" + Constants.USER)
    @Produces("text/plain")
    public String evaluateVomsGridUserPTGPermission(@PathParam("filePath") String filePath,
            @QueryParam(Constants.DN_KEY) String DN, @QueryParam(Constants.FQANS_KEY) String FQANS) throws WebApplicationException
    {
        log.info("Serving prepareToGet operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath, DN, FQANS);
        return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), SRMFileRequest.PTG).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.RM_OPERATION + "/" + Constants.VOMS_EXTENSIONS + "/" + Constants.USER)
    @Produces("text/plain")
    public String evaluateVomsGridUserRmPermission(@PathParam("filePath") String filePath,
            @QueryParam(Constants.DN_KEY) String DN, @QueryParam(Constants.FQANS_KEY) String FQANS) throws WebApplicationException
    {
        log.info("Serving rm operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath, DN, FQANS);
        File file = new File(parameters.getFilePathDecoded());
        if(file.isDirectory())
        {
            return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), SRMFileRequest.RMD).toString();    
        }
        else
        {
            return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), SRMFileRequest.RM).toString();
        }
        
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.LS_OPERATION + "/" + Constants.VOMS_EXTENSIONS + "/" + Constants.USER)
    @Produces("text/plain")
    public String evaluateVomsGridUserLsPermission(@PathParam("filePath") String filePath,
            @QueryParam(Constants.DN_KEY) String DN, @QueryParam(Constants.FQANS_KEY) String FQANS) throws WebApplicationException
    {
        log.info("Serving ls operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath, DN, FQANS);
        return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), SRMFileRequest.LS).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.MKDIR_OPERATION + "/" + Constants.VOMS_EXTENSIONS + "/" + Constants.USER)
    @Produces("text/plain")
    public String evaluateVomsGridUserMkdirPermission(@PathParam("filePath") String filePath,
            @QueryParam(Constants.DN_KEY) String DN, @QueryParam(Constants.FQANS_KEY) String FQANS) throws WebApplicationException
    {
        log.info("Serving mkdir operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath, DN, FQANS);
        return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), SRMFileRequest.MD).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.CP_FROM_OPERATION + "/" + Constants.VOMS_EXTENSIONS + "/" + Constants.USER)
    @Produces("text/plain")
    public String evaluateVomsGridUserCpFromPermission(@PathParam("filePath") String filePath,
            @QueryParam(Constants.DN_KEY) String DN, @QueryParam(Constants.FQANS_KEY) String FQANS) throws WebApplicationException
    {
        log.info("Serving cpFrom operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath, DN, FQANS);
        return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), SRMFileRequest.CPfrom).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.CP_TO_OPERATION + "/" + Constants.VOMS_EXTENSIONS + "/" + Constants.USER)
    @Produces("text/plain")
    public String evaluateVomsGridUserCpToPermission(@PathParam("filePath") String filePath,
            @QueryParam(Constants.DN_KEY) String DN, @QueryParam(Constants.FQANS_KEY) String FQANS) throws WebApplicationException
    {
        log.info("Serving cpTo operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath, DN, FQANS);
        return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), SRMFileRequest.CPto).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.MOVE_FROM_OPERATION + "/" + Constants.VOMS_EXTENSIONS + "/" + Constants.USER)
    @Produces("text/plain")
    public String evaluateVomsGridUserMvFromPermission(@PathParam("filePath") String filePath,
            @QueryParam(Constants.DN_KEY) String DN, @QueryParam(Constants.FQANS_KEY) String FQANS) throws WebApplicationException
    {
        log.info("Serving mvFrom operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath, DN, FQANS);
        return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), SRMFileRequest.MV_source).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.MOVE_TO_OPERATION + "/" + Constants.VOMS_EXTENSIONS + "/" + Constants.USER)
    @Produces("text/plain")
    public String evaluateVomsGridUserMvToPermission(@PathParam("filePath") String filePath,
            @QueryParam(Constants.DN_KEY) String DN, @QueryParam(Constants.FQANS_KEY) String FQANS) throws WebApplicationException
    {
        log.info("Serving mvTo operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath, DN, FQANS);
        return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), SRMFileRequest.MV_dest).toString();
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
        RequestParameters parameters = new RequestParameters(filePath, DN);
        return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), PathOperation.READ_FILE).toString();
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
        
        RequestParameters parameters = new RequestParameters(filePath, DN);
        return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), PathOperation.WRITE_FILE).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.PREPARE_TO_PUT_OPERATION + "/" + Constants.PLAIN + "/" + Constants.USER)
    @Produces("text/plain")
    public String evaluateGridUserPTPPermission(@PathParam("filePath") String filePath,
            @QueryParam(Constants.DN_KEY) String DN)
            throws WebApplicationException
    {
        log.info("Serving prepareToPut operation authorization on file '" + filePath + "\'");
        
        RequestParameters parameters = new RequestParameters(filePath, DN);
        return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), SRMFileRequest.PTP).toString();

    }

    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.PREPARE_TO_GET_OPERATION + "/" + Constants.PLAIN + "/" + Constants.USER)
    @Produces("text/plain")
    public String evaluateGridUserPTGPermission(@PathParam("filePath") String filePath,
            @QueryParam(Constants.DN_KEY) String DN) throws WebApplicationException
    {
        log.info("Serving prepareToGet operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath, DN);
        return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), SRMFileRequest.PTG).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.RM_OPERATION + "/" + Constants.PLAIN + "/" + Constants.USER)
    @Produces("text/plain")
    public String evaluateGridUserRmPermission(@PathParam("filePath") String filePath,
            @QueryParam(Constants.DN_KEY) String DN) throws WebApplicationException
    {
        log.info("Serving rm operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath, DN);
        File file = new File(parameters.getFilePathDecoded());
        if(file.isDirectory())
        {
            return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), SRMFileRequest.RMD).toString();    
        }
        else
        {
            return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), SRMFileRequest.RM).toString();
        }
        
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.LS_OPERATION + "/" + Constants.PLAIN + "/" + Constants.USER)
    @Produces("text/plain")
    public String evaluateGridUserLsPermission(@PathParam("filePath") String filePath,
            @QueryParam(Constants.DN_KEY) String DN) throws WebApplicationException
    {
        log.info("Serving ls operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath, DN);
        return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), SRMFileRequest.LS).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.MKDIR_OPERATION + "/" + Constants.PLAIN + "/" + Constants.USER)
    @Produces("text/plain")
    public String evaluateGridUserMkdirPermission(@PathParam("filePath") String filePath,
            @QueryParam(Constants.DN_KEY) String DN) throws WebApplicationException
    {
        log.info("Serving mkdir operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath, DN);
        return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), SRMFileRequest.MD).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.CP_FROM_OPERATION + "/" + Constants.PLAIN + "/" + Constants.USER)
    @Produces("text/plain")
    public String evaluateGridUserCpFromPermission(@PathParam("filePath") String filePath,
            @QueryParam(Constants.DN_KEY) String DN) throws WebApplicationException
    {
        log.info("Serving cpFrom operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath, DN);
        return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), SRMFileRequest.CPfrom).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.CP_TO_OPERATION + "/" + Constants.PLAIN + "/" + Constants.USER)
    @Produces("text/plain")
    public String evaluateGridUserCpToPermission(@PathParam("filePath") String filePath,
            @QueryParam(Constants.DN_KEY) String DN) throws WebApplicationException
    {
        log.info("Serving cpTo operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath, DN);
        return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), SRMFileRequest.CPto).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.MOVE_FROM_OPERATION + "/" + Constants.PLAIN + "/" + Constants.USER)
    @Produces("text/plain")
    public String evaluateGridUserMvFromPermission(@PathParam("filePath") String filePath,
            @QueryParam(Constants.DN_KEY) String DN) throws WebApplicationException
    {
        log.info("Serving mvFrom operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath, DN);
        return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), SRMFileRequest.MV_source).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.MOVE_TO_OPERATION + "/" + Constants.PLAIN + "/" + Constants.USER)
    @Produces("text/plain")
    public String evaluateGridUserMvToPermission(@PathParam("filePath") String filePath,
            @QueryParam(Constants.DN_KEY) String DN) throws WebApplicationException
    {
        log.info("Serving mvTo operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath, DN);
        return PermissionEvaluator.evaluateVomsGridUserPermission(parameters.getDNDecoded(), parameters.getFQANSDecoded(), parameters.getFilePathDecoded(), SRMFileRequest.MV_dest).toString();
    }
    
    
    
    
    
    
    
    
    /**
     * @param filePath
     * @param DN
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.READ_OPERATION)
    @Produces("text/plain")
    public String evaluateAnonymousReadPermission(@PathParam("filePath") String filePath)
            throws WebApplicationException
    {
        log.info("Serving read operation authorization on file '" + filePath + "\'");
        RequestParameters parameters = new RequestParameters(filePath);
        return PermissionEvaluator.evaluateAnonymousPermission(parameters.getFilePathDecoded(), PathOperation.READ_FILE).toString();
    }

    /**
     * @param filePath
     * @param DN
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.WRITE_OPERATION)
    @Produces("text/plain")
    public String evaluateAnonymousWritePermission(@PathParam("filePath") String filePath)
            throws WebApplicationException
    {
        log.info("Serving write operation authorization on file '" + filePath + "\'");
        
        RequestParameters parameters = new RequestParameters(filePath);
        return PermissionEvaluator.evaluateAnonymousPermission(parameters.getFilePathDecoded(), PathOperation.WRITE_FILE).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.PREPARE_TO_PUT_OPERATION)
    @Produces("text/plain")
    public String evaluateAnonymousPTPPermission(@PathParam("filePath") String filePath)
            throws WebApplicationException
    {
        log.info("Serving prepareToPut operation authorization on file '" + filePath + "\'");
        
        RequestParameters parameters = new RequestParameters(filePath);
        return PermissionEvaluator.evaluateAnonymousPermission(parameters.getFilePathDecoded(), SRMFileRequest.PTP).toString();

    }

    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.PREPARE_TO_GET_OPERATION)
    @Produces("text/plain")
    public String evaluateAnonymousPTGPermission(@PathParam("filePath") String filePath) throws WebApplicationException
    {
        log.info("Serving prepareToGet operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath);
        return PermissionEvaluator.evaluateAnonymousPermission(parameters.getFilePathDecoded(), SRMFileRequest.PTG).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.RM_OPERATION)
    @Produces("text/plain")
    public String evaluateAnonymousRmPermission(@PathParam("filePath") String filePath) throws WebApplicationException
    {
        log.info("Serving rm operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath);
        File file = new File(parameters.getFilePathDecoded());
        if(file.isDirectory())
        {
            return PermissionEvaluator.evaluateAnonymousPermission(parameters.getFilePathDecoded(), SRMFileRequest.RMD).toString();    
        }
        else
        {
            return PermissionEvaluator.evaluateAnonymousPermission(parameters.getFilePathDecoded(), SRMFileRequest.RM).toString();
        }
        
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.LS_OPERATION)
    @Produces("text/plain")
    public String evaluateAnonymousLsPermission(@PathParam("filePath") String filePath) throws WebApplicationException
    {
        log.info("Serving ls operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath);
        return PermissionEvaluator.evaluateAnonymousPermission(parameters.getFilePathDecoded(), SRMFileRequest.LS).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.MKDIR_OPERATION)
    @Produces("text/plain")
    public String evaluateAnonymousPermission(@PathParam("filePath") String filePath) throws WebApplicationException
    {
        log.info("Serving mkdir operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath);
        return PermissionEvaluator.evaluateAnonymousPermission(parameters.getFilePathDecoded(), SRMFileRequest.MD).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.CP_FROM_OPERATION)
    @Produces("text/plain")
    public String evaluateAnonymousCpFromPermission(@PathParam("filePath") String filePath) throws WebApplicationException
    {
        log.info("Serving cpFrom operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath);
        return PermissionEvaluator.evaluateAnonymousPermission(parameters.getFilePathDecoded(), SRMFileRequest.CPfrom).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.CP_TO_OPERATION)
    @Produces("text/plain")
    public String evaluateAnonymousCpToPermission(@PathParam("filePath") String filePath) throws WebApplicationException
    {
        log.info("Serving cpTo operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath);
        return PermissionEvaluator.evaluateAnonymousPermission(parameters.getFilePathDecoded(), SRMFileRequest.CPto).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.MOVE_FROM_OPERATION)
    @Produces("text/plain")
    public String evaluateAnonymousMvFromPermission(@PathParam("filePath") String filePath) throws WebApplicationException
    {
        log.info("Serving mvFrom operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath);
        return PermissionEvaluator.evaluateAnonymousPermission(parameters.getFilePathDecoded(), SRMFileRequest.MV_source).toString();
    }
    
    /**
     * @param filePath
     * @param DN
     * @param FQANS
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/" + Constants.MOVE_TO_OPERATION)
    @Produces("text/plain")
    public String evaluateAnonymousMvToPermission(@PathParam("filePath") String filePath) throws WebApplicationException
    {
        log.info("Serving mvTo operation authorization on file '" + filePath + "\' User provides a VOMS proxy");
        RequestParameters parameters = new RequestParameters(filePath);
        return PermissionEvaluator.evaluateAnonymousPermission(parameters.getFilePathDecoded(), SRMFileRequest.MV_dest).toString();
    }
}