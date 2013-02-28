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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.QueryParam;
import it.grid.storm.authz.remote.Constants;

/**
 * @author Michele Dibenedetto
 */
@Path("/" + Constants.RESOURCE + "/" + Constants.VERSION_1_0 + "/{filePath}")
public class AuthorizationResourceCompat_1_0
{

    private static final AuthorizationResource resourceInstance = new AuthorizationResource();
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
        return resourceInstance.evaluateVomsGridUserReadPermission(filePath, DN, FQANS);
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
        return resourceInstance.evaluateVomsGridUserWritePermission(filePath, DN, FQANS);
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
        return resourceInstance.evaluateGridUserReadPermission(filePath, DN);
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
        return resourceInstance.evaluateGridUserWritePermission(filePath, DN);
    }
}